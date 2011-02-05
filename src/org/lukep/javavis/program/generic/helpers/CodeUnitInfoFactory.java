/*
 * CodeUnitInfoFactory.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.program.generic.helpers;

import java.util.logging.Logger;

import javax.lang.model.element.TypeElement;

import org.lukep.javavis.program.generic.models.ClassModel;
import org.lukep.javavis.program.generic.models.ClassModelStore;
import org.lukep.javavis.program.generic.models.GenericModelSourceLang;
import org.lukep.javavis.program.generic.models.MethodModel;
import org.lukep.javavis.program.generic.models.VariableModel;
import org.lukep.javavis.program.generic.models.measurable.MeasurableClassInfo;
import org.lukep.javavis.program.generic.models.measurable.MeasurableMethodInfo;
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
		MeasurableClassInfo newClassModel = new MeasurableClassInfo(
											GenericModelSourceLang.JAVA,
											e.getSimpleName().toString(),
											e.getQualifiedName().toString());
		s.lastClass = newClassModel;
		return newClassModel;
	}
	
	public static MethodModel createMethodInfoFromJava(CodeUnitInfoFactoryState s, 
			MethodTree methodTree, TreePath path, Trees trees) {
		
		// create the new generic method object
		MethodModel newMethodInfo = new MeasurableMethodInfo(GenericModelSourceLang.JAVA,
											methodTree.getName().toString());
		
		// set the method's BlockTree entry point
		newMethodInfo.setRootStatementBlock(methodTree.getBody());
		
		// set constructor method or just add to the parent class's list of methods
		if (newMethodInfo.getName().equals(JavaVisConstants.DEFAULT_CONSTRUCTOR_NAME)) {
			s.lastClass.setConstructorMethod(newMethodInfo);
			
			log.info("Added constructor method to " + s.lastClass.getSimpleName());
		} else if (s.lastClass != null) {
			s.lastClass.addMethod(newMethodInfo);
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
			newVariableInfo = new VariableModel(GenericModelSourceLang.JAVA,
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
				ClassModel typeInternal = ClassModelStore.lookupClassGlobal(qualifiedTypeName);
				if (typeInternal != null)
					newVariableInfo.setTypeInternalClass(typeInternal);
			}
			
			log.info("New " + newVariableInfo.toString());
		}
		
		return newVariableInfo;
	}
	
}
