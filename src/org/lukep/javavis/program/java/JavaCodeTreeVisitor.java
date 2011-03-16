/*
 * JavaCodeTreeVisitor.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.program.java;

import java.util.Vector;

import org.lukep.javavis.program.generic.factories.GenericModelFactory;
import org.lukep.javavis.program.generic.factories.GenericModelFactoryState;
import org.lukep.javavis.program.generic.models.ClassModel;
import org.lukep.javavis.program.generic.models.MethodModel;
import org.lukep.javavis.program.generic.models.ProjectModel;
import org.lukep.javavis.program.generic.models.VariableModel;
import org.lukep.javavis.ui.IProgramSourceObserver;

import com.sun.source.tree.ClassTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreePathScanner;
import com.sun.source.util.Trees;

public class JavaCodeTreeVisitor extends TreePathScanner<Object, Trees> {

	protected Vector<IProgramSourceObserver> observers;
	protected GenericModelFactoryState codeUnitState;
	
	public JavaCodeTreeVisitor(Vector<IProgramSourceObserver> observers,
			ProjectModel programStore) {
		super();

		this.observers = observers;
		this.codeUnitState = new GenericModelFactoryState(programStore);
	}

	@Override
	public Object visitClass(ClassTree classTree, Trees trees) {
		TreePath path = getCurrentPath();
		ClassModel newClassModel = 
			GenericModelFactory.createClassInfoFromJava(codeUnitState, classTree, path, trees);
		
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
		MethodModel newMethodInfo = 
			GenericModelFactory.createMethodInfoFromJava(codeUnitState, methodTree, path, trees);
		
		// notify observers
		for (IProgramSourceObserver observer : observers)
			observer.notifyFindMethod(newMethodInfo);
		
		return super.visitMethod(methodTree, trees);
	}

	@Override
	public Object visitVariable(VariableTree variableTree, Trees trees) {
		TreePath path = getCurrentPath();
		VariableModel newVariableInfo = GenericModelFactory.createVariableInfoFromJava(
				codeUnitState, variableTree, path, trees);
		// TODO: do something here
		return super.visitVariable(variableTree, trees);
	}

	/*@Override
	public Object visitIdentifier(IdentifierTree identifierTree, Trees trees) {
		JCIdent identifier = (JCIdent) identifierTree;
		if (identifier != null)
			System.out.println(identifier.getName().toString());
		return super.visitIdentifier(identifierTree, trees);
	}*/

	/*@Override
	public Object visitNewClass(NewClassTree newClassTree, Trees trees) {
		TreePath path = getCurrentPath();
		TypeElement e = (TypeElement) trees.getElement(path);
		if (e != null)
			System.out.println("visitNewClass: " + newClassTree.getClassBody().toString());
		return super.visitNewClass(newClassTree, trees);
	}*/

}
