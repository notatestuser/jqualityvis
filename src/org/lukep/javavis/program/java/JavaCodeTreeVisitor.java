/*
 * JavaCodeTreeVisitor.java (JavaVis)
 * Copyright 2010 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.program.java;

import java.util.Vector;

import org.lukep.javavis.program.generic.helpers.CodeUnitInfoFactory;
import org.lukep.javavis.program.generic.helpers.CodeUnitInfoFactoryState;
import org.lukep.javavis.program.generic.models.ClassInfo;
import org.lukep.javavis.program.generic.models.MethodInfo;
import org.lukep.javavis.program.generic.models.VariableInfo;
import org.lukep.javavis.ui.IProgramSourceObserver;

import com.sun.source.tree.ClassTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreePathScanner;
import com.sun.source.util.Trees;

public class JavaCodeTreeVisitor extends TreePathScanner<Object, Trees> {

	protected Vector<IProgramSourceObserver> observers;
	protected CodeUnitInfoFactoryState codeUnitState = new CodeUnitInfoFactoryState();
	
	public JavaCodeTreeVisitor(Vector<IProgramSourceObserver> observers) {
		super();

		this.observers = observers;
	}

	@Override
	public Object visitClass(ClassTree classTree, Trees trees) {
		TreePath path = getCurrentPath();
		ClassInfo newClassModel = 
			CodeUnitInfoFactory.createClassInfo(codeUnitState, classTree, path, trees);
		
		// notify observers
		if (newClassModel != null) {
			for (IProgramSourceObserver observer : observers)
				observer.notifyFindClass(newClassModel);
		}
		
		return super.visitClass(classTree, trees);
	}

	@Override
	public Object visitMethod(MethodTree methodTree, Trees trees) {
		TreePath path = getCurrentPath();
		MethodInfo newMethodInfo = 
			CodeUnitInfoFactory.createMethodInfo(codeUnitState, methodTree, path, trees);
		
		// notify observers
		for (IProgramSourceObserver observer : observers)
			observer.notifyFindMethod(newMethodInfo);
		
		return super.visitMethod(methodTree, trees);
	}

	@Override
	public Object visitVariable(VariableTree variableTree, Trees trees) {
		TreePath path = getCurrentPath();
		VariableInfo newVariableInfo = CodeUnitInfoFactory.createVariableInfo(
				codeUnitState, variableTree, path, trees);
		// TODO: do something here
		return super.visitVariable(variableTree, trees);
	}

	/*@Override
	public Object visitNewClass(NewClassTree newClassTree, Trees trees) {
		TreePath path = getCurrentPath();
		TypeElement e = (TypeElement) trees.getElement(path);
		if (e != null)
			System.out.println("visitNewClass: " + newClassTree.getClassBody().toString());
		return super.visitNewClass(newClassTree, trees);
	}*/

}