/*
 * CodeUnitInfoFactory.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.program.generic.helpers;

import java.util.Map;
import java.util.logging.Logger;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

import org.lukep.javavis.program.generic.models.AbstractModelSourceLang;
import org.lukep.javavis.program.generic.models.ClassModel;
import org.lukep.javavis.program.generic.models.IGenericModelNode;
import org.lukep.javavis.program.generic.models.MethodModel;
import org.lukep.javavis.program.generic.models.PackageModel;
import org.lukep.javavis.program.generic.models.ProjectModel;
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

public class CodeUnitInfoFactory {

	protected final static Logger log = 
		Logger.getLogger(CodeUnitInfoFactory.class.getSimpleName());
	
	public static ClassModel createClassInfoFromJava(CodeUnitInfoFactoryState s, 
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
		
		return newClassModel;
	}
	
	public static MethodModel createMethodInfoFromJava(CodeUnitInfoFactoryState s, 
			MethodTree methodTree, TreePath path, Trees trees) {
		
		MethodSymbol e = (MethodSymbol) trees.getElement(path);
		
		if (e == null)
			return null;
		
		// create the new generic method object
		MethodModel newMethodModel = new MethodModel(AbstractModelSourceLang.JAVA,
											methodTree.getName().toString());
		
		// set the method's BlockTree entry point
		newMethodModel.setRootStatementBlock(methodTree.getBody());
		
		// set modifiers
		for (Modifier modifier : e.getModifiers())
			setJavaModifiers(modifier.toString(), newMethodModel);
		
		// set constructor method or just add to the parent class's list of methods
		if (newMethodModel.getName().equals(JavaVisConstants.DEFAULT_CONSTRUCTOR_NAME)) {
			s.lastClass.setConstructorMethod(newMethodModel);
			
			log.info("Added constructor method to " + s.lastClass.getSimpleName());
		} else if (s.lastClass != null) {
			// add method to previous class as a method
			s.lastClass.addMethod(newMethodModel);
			//s.lastClass.addChild(newMethodInfo);
			newMethodModel.setParentClass(s.lastClass);
			
			log.info("Added method to " + s.lastClass.getSimpleName()
										+ ": " + newMethodModel.getName());
		}
		
		return newMethodModel;
	}
	
	public static VariableModel createVariableInfoFromJava(CodeUnitInfoFactoryState s,
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
			ClassModel typeInternal = ProjectModel.lookupClassGlobal(qualifiedTypeName);
			if (typeInternal != null)
				newVariableModel.setTypeInternalClass(typeInternal);
			
			log.info("New " + newVariableModel.toString());
		}
		
		return newVariableModel;
	}
	
	private static PackageModel getOrCreatePackage(String packageName, 
			Map<String, PackageModel> pkgMap, CodeUnitInfoFactoryState s) {
		
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
		
		pkgMap.put(usingDefaultPkgName ? "" : packageName, curPackage);
		
		log.info("New package: " + curPackage.toString());
		
		return curPackage;
	}
	
	private static PackageModel getOrCreatePackage(String packageName, CodeUnitInfoFactoryState s) {
		return getOrCreatePackage(packageName, s.programStore.getPackageMap(), s);
	}
	
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
