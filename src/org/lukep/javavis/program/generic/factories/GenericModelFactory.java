/*
 * GenericModelFactory.java (JQualityVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.program.generic.factories;

import java.util.Map;
import java.util.logging.Logger;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.SimpleElementVisitor6;

import org.lukep.javavis.program.generic.models.AbstractModelSourceLang;
import org.lukep.javavis.program.generic.models.ClassModel;
import org.lukep.javavis.program.generic.models.IGenericModelNode;
import org.lukep.javavis.program.generic.models.MethodModel;
import org.lukep.javavis.program.generic.models.PackageModel;
import org.lukep.javavis.program.generic.models.Relationship.RelationshipType;
import org.lukep.javavis.program.generic.models.VariableModel;
import org.lukep.javavis.util.JavaVisConstants;

import com.sun.source.tree.ClassTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreePath;
import com.sun.source.util.Trees;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Symbol.MethodSymbol;
import com.sun.tools.javac.code.Symbol.VarSymbol;

/**
 * A factory for creating GenericModel objects.
 */
public class GenericModelFactory {

	/** The Constant log. */
	protected final static Logger log = 
		Logger.getLogger(GenericModelFactory.class.getSimpleName());
	
	/** The method utils. */
	private static ModelFactoryJavaMethodUtils methodUtils = new ModelFactoryJavaMethodUtils();
	
	/**
	 * Creates a new GenericModel object.
	 *
	 * @param s the s
	 * @param classTree the class tree
	 * @param path the path
	 * @param trees the trees
	 * @return the class model
	 */
	public static ClassModel createClassModelFromJava(GenericModelFactoryState s, 
			ClassTree classTree, TreePath path, Trees trees) {
		
		TypeElement e = (TypeElement) trees.getElement(path);
		
		if (e == null)
			return null;
		
		// create the new generic class object
		ClassModel newClassModel = new ClassModel(
				AbstractModelSourceLang.JAVA,
				e.getSimpleName().toString(),
				e.getQualifiedName().toString());
		s.lastClass = newClassModel;
		
		// set modifiers
		for (Modifier modifier : e.getModifiers())
			setJavaModifiers(modifier.toString(), newClassModel);
		
		// set the class's superclass (parent in the inheritance hierarchy)
		if (classTree.getExtendsClause() != null)
			newClassModel.setSuperClassName(e.getSuperclass().toString());
		
		// set the class's parent element
		IGenericModelNode parent = null;
		Symbol enclosingElement = (Symbol) e.getEnclosingElement();
		
		switch (enclosingElement.getKind()) {
		
		case PACKAGE:
			parent = getOrCreatePackage(
					enclosingElement.getQualifiedName().toString(), s);
			break;
			
		case CLASS:
			parent = s.programStore.getClassInfo(
					enclosingElement.getQualifiedName().toString());
			break;
		}
		if (parent != null) {
			// parent is a package or another class
			parent.addChild(newClassModel, RelationshipType.ENCLOSED_IN);
			newClassModel.setParent(parent);
		} else {
			// parent is the project
			newClassModel.setParent(s.programStore);
		}
		
		s.programStore.incModelCount(1);
		
		return newClassModel;
	}
	
	/**
	 * Creates a new GenericModel object.
	 *
	 * @param s the s
	 * @param methodTree the method tree
	 * @param path the path
	 * @param trees the trees
	 * @return the method model
	 */
	public static MethodModel createMethodModelFromJava(GenericModelFactoryState s, 
			MethodTree methodTree, TreePath path, Trees trees) {
		
		MethodSymbol e = (MethodSymbol) trees.getElement(path);
		
		if (e == null)
			return null;
		
		// create the new generic method object
		final MethodModel newMethodModel = new MethodModel(AbstractModelSourceLang.JAVA,
											methodTree.getName().toString());
		
		// set modifiers
		for (Modifier modifier : e.getModifiers())
			setJavaModifiers(modifier.toString(), newMethodModel);
		
		// add parameters
		e.accept(new SimpleElementVisitor6<Object, MethodModel>() {

			@Override
			public Object visitExecutable(ExecutableElement e, MethodModel p) {
				for (VariableElement var : e.getParameters()) {
					newMethodModel.addParameter(var.getSimpleName().toString(), 
							var.asType().toString());
				}
				newMethodModel.setReturnType(e.getReturnType().toString());
				return super.visitExecutable(e, p);
			}
		}, null);
		
		// set constructor method or just add to the parent class's list of methods
		if (newMethodModel.getName().equals(JavaVisConstants.DEFAULT_CONSTRUCTOR_NAME)) {
			s.lastClass.setConstructorMethod(newMethodModel);
			s.lastClass.addMethod(newMethodModel);
			
			log.info("Added constructor method to " + s.lastClass.getSimpleName());
		} else if (s.lastClass != null) {
			// add method to previous class as a method
			s.lastClass.addMethod(newMethodModel);
			//s.lastClass.addChild(newMethodInfo);
			newMethodModel.setParentClass(s.lastClass);
			
			log.info("Added method to " + s.lastClass.getSimpleName()
										+ ": " + newMethodModel.getName());
		}
		
		// calculate the method's statement and complexity heuristics (no. of statements, independent exec. paths)
		methodUtils.populateMethodStatementHeuristics(methodTree, newMethodModel);
		
		s.programStore.incModelCount(1);
		
		return newMethodModel;
	}
	
	/**
	 * Creates a new GenericModel object.
	 *
	 * @param s the s
	 * @param variableTree the variable tree
	 * @param path the path
	 * @param trees the trees
	 * @return the variable model
	 */
	public static VariableModel createVariableInfoFromJava(GenericModelFactoryState s,
			VariableTree variableTree, TreePath path, Trees trees) {
		
		VarSymbol e = (VarSymbol) trees.getElement(path);
		
		if (e == null)
			return null;
		
		VariableModel newVariableModel = null;
		
		String qualifiedTypeName = e.type.toString();
		newVariableModel = new VariableModel(AbstractModelSourceLang.JAVA,
				e.getQualifiedName().toString(), qualifiedTypeName);
		
		// set modifiers
		for (Modifier modifier : e.getModifiers())
			setJavaModifiers(modifier.toString(), newVariableModel);
		
		// add this information to the last parsed class (if it exists)
		if (s.lastClass != null) {
			s.lastClass.addVariable(newVariableModel);
			newVariableModel.setParentClass(s.lastClass);
			
			// if the enclosing element is our last class, then it's a class attribute!
			if (e.getEnclosingElement().getQualifiedName().toString().equals(
					s.lastClass.getQualifiedName()))
				newVariableModel.setClassAttribute(true);
			
			// set the variable's internal type target if we've got a generic of that type stored
			ClassModel typeInternal = s.programStore.lookupClass(qualifiedTypeName);
			if (typeInternal != null)
				newVariableModel.setTypeInternalClass(typeInternal);
			
			log.info("New " + newVariableModel.toString());
			
			s.programStore.incModelCount(1);
		}
		
		return newVariableModel;
	}
	
	/**
	 * Gets the or create package.
	 *
	 * @param packageName the package name
	 * @param pkgMap the pkg map
	 * @param s the s
	 * @return the or create package
	 */
	private static PackageModel getOrCreatePackage(String packageName, 
			Map<String, PackageModel> pkgMap, GenericModelFactoryState s) {
		
		// return the package if we already have it
		if (pkgMap.containsKey(packageName))
			return pkgMap.get(packageName);
		
		// parent package exists?
		boolean usingDefaultPkgName = false;
		int lastDotIndex = packageName.lastIndexOf('.');
		String parentPackageName;
		PackageModel parentPackage = null;
		
		if (packageName.length() > 0) {
			if (lastDotIndex != -1) { // class is member of a named package (non-default)
				parentPackageName = packageName.substring(0, lastDotIndex);
				parentPackage = getOrCreatePackage(parentPackageName, pkgMap, s);
			}
		} else {
			// we're at the root
			packageName = JavaVisConstants.DEFAULT_PACKAGE_NAME;
			usingDefaultPkgName = true;
		}
		
		// create the current package cell and link it up to the parent
		PackageModel curPackage = new PackageModel(AbstractModelSourceLang.JAVA, packageName);
		if (parentPackage != null) {
			curPackage.setParent(parentPackage);
			curPackage.setSimpleName(packageName.substring(lastDotIndex + 1));
		} else {
			// no parent package - our root is the project
			curPackage.setParent(s.programStore);
		}
		
		//pkgMap.put(usingDefaultPkgName ? "" : packageName, curPackage);
		s.programStore.addPackage((usingDefaultPkgName ? "" : packageName), curPackage);
		
		log.info("New package: " + curPackage.toString());
		
		s.programStore.incModelCount(1);
		
		return curPackage;
	}
	
	/**
	 * Gets the or create package.
	 *
	 * @param packageName the package name
	 * @param s the s
	 * @return the or create package
	 */
	private static PackageModel getOrCreatePackage(String packageName, GenericModelFactoryState s) {
		return getOrCreatePackage(packageName, s.programStore.getPackageMap(), s);
	}
	
	/**
	 * Sets the java modifiers.
	 *
	 * @param modifiers the modifiers
	 * @param model the model
	 */
	private static void setJavaModifiers(String modifiers, IGenericModelNode model) {
		
		if (modifiers.contains(Modifier.PUBLIC.toString()))
			model.setPublic(true);
		if (modifiers.contains(Modifier.PROTECTED.toString()))
			model.setProtected(true);
		if (modifiers.contains(Modifier.FINAL.toString()))
			model.setFinal(true);
		if (modifiers.contains(Modifier.NATIVE.toString()))
			model.setNative(true);
		if (modifiers.contains(Modifier.STATIC.toString()))
			model.setStatic(true);
		if (modifiers.contains(Modifier.PRIVATE.toString()))
			model.setPrivate(true);
		if (modifiers.contains(Modifier.ABSTRACT.toString()))
			model.setAbstract(true);
	}
	
}
