/*
 * CodeUnitInfoFactory.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.program.generic.helpers;

import java.util.Map;
import java.util.logging.Logger;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import org.lukep.javavis.metrics.IMeasurable;
import org.lukep.javavis.program.generic.models.AbstractModelSourceLang;
import org.lukep.javavis.program.generic.models.ClassModel;
import org.lukep.javavis.program.generic.models.MethodModel;
import org.lukep.javavis.program.generic.models.PackageModel;
import org.lukep.javavis.program.generic.models.ProgramModelStore;
import org.lukep.javavis.program.generic.models.VariableModel;
import org.lukep.javavis.util.JavaVisConstants;

import com.sun.source.tree.ClassTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreePath;
import com.sun.source.util.Trees;
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
		
		// set the class's parent element
		IMeasurable parent = null;
		Element enclosingElement = e.getEnclosingElement();
		
		switch (enclosingElement.getKind()) {
		
		case PACKAGE:
			parent = getOrCreatePackage(e.getQualifiedName().toString(), s);
			break;
			
		case CLASS:
			parent = s.programStore.getClassInfo(e.getQualifiedName().toString());
			break;
		}
		if (parent != null) {
			parent.addChild(newClassModel);
			newClassModel.setParent(parent);
		}
		
		return newClassModel;
	}
	
	public static MethodModel createMethodInfoFromJava(CodeUnitInfoFactoryState s, 
			MethodTree methodTree, TreePath path, Trees trees) {
		
		// create the new generic method object
		MethodModel newMethodInfo = new MethodModel(AbstractModelSourceLang.JAVA,
											methodTree.getName().toString());
		
		// set the method's BlockTree entry point
		newMethodInfo.setRootStatementBlock(methodTree.getBody());
		
		// set constructor method or just add to the parent class's list of methods
		if (newMethodInfo.getName().equals(JavaVisConstants.DEFAULT_CONSTRUCTOR_NAME)) {
			s.lastClass.setConstructorMethod(newMethodInfo);
			
			log.info("Added constructor method to " + s.lastClass.getSimpleName());
		} else if (s.lastClass != null) {
			// add method to previous class as a method
			s.lastClass.addMethod(newMethodInfo);
			//s.lastClass.addChild(newMethodInfo);
			newMethodInfo.setParentClass(s.lastClass);
			
			log.info("Added method to " + s.lastClass.getSimpleName()
														+ ": " + newMethodInfo.getName());
		}
		
		return newMethodInfo;
	}
	
	public static VariableModel createVariableInfoFromJava(CodeUnitInfoFactoryState s,
			VariableTree variableTree, TreePath path, Trees trees) {
		
		VarSymbol e = (VarSymbol) trees.getElement(path);
		VariableModel newVariableInfo = null;
		
		if (e != null) {
			String qualifiedTypeName = e.type.toString();
			newVariableInfo = new VariableModel(AbstractModelSourceLang.JAVA,
					e.getQualifiedName().toString(), qualifiedTypeName);
			
			// add this information to the last parsed class (if it exists)
			if (s.lastClass != null) {
				s.lastClass.addVariable(newVariableInfo);
				newVariableInfo.setParentClass(s.lastClass);
				
				// if the enclosing element is our last class, then it's a class attribute!
				if (e.getEnclosingElement().getQualifiedName().toString().equals(
						s.lastClass.getQualifiedName()))
					newVariableInfo.setClassAttribute(true);
				
				// set the variable's internal type target if we've got a generic of that type stored
				ClassModel typeInternal = ProgramModelStore.lookupClassGlobal(qualifiedTypeName);
				if (typeInternal != null)
					newVariableInfo.setTypeInternalClass(typeInternal);
			}
			
			log.info("New " + newVariableInfo.toString());
		}
		
		return newVariableInfo;
	}
	
	
	private static PackageModel getOrCreatePackage(String packageName, Map<String, PackageModel> pkgMap) {
		
		// return the package if we already have it
		if (pkgMap.containsKey(packageName))
			return pkgMap.get(packageName);
		
		// parent package exists?
		int lastDotIndex = packageName.lastIndexOf('.');
		String parentPackageName;
		PackageModel parentPackage = null;
		if (packageName.length() > 0 && lastDotIndex != -1) { // class is member of a named package (non-default)
			parentPackageName = packageName.substring(0, lastDotIndex);
			parentPackage = getOrCreatePackage(parentPackageName, pkgMap);
		} else {
			// we're at the root
		}
		
		// create the current package cell and link it up to the parent
		PackageModel curPackage = new PackageModel(AbstractModelSourceLang.JAVA, packageName);
		if (parentPackage != null)
			curPackage.setParent(parentPackage);
		
		pkgMap.put(packageName, curPackage);
		
		log.info("New " + curPackage.toString());
		
		return curPackage;
	}
	
	private static PackageModel getOrCreatePackage(String packageName, CodeUnitInfoFactoryState s) {
		return getOrCreatePackage(packageName, s.programStore.getPackageMap());
	}
	
}
