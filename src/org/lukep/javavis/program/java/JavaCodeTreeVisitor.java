/*
 * JavaCodeTreeVisitor.java (JQualityVis)
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

/**
 * The Class JavaCodeTreeVisitor.
 */
public class JavaCodeTreeVisitor extends TreePathScanner<Object, Trees> {

	/** The observers. */
	protected Vector<IProgramSourceObserver> observers;
	
	/** The GenericModelFactory state object. */
	protected GenericModelFactoryState codeUnitState;
	
	/**
	 * Instantiates a new java code tree visitor.
	 *
	 * @param observers the observers
	 * @param programStore the program store
	 */
	public JavaCodeTreeVisitor(Vector<IProgramSourceObserver> observers,
			ProjectModel programStore) {
		super();

		this.observers = observers;
		this.codeUnitState = new GenericModelFactoryState(programStore);
	}

	/* (non-Javadoc)
	 * @see com.sun.source.util.TreeScanner#visitClass(com.sun.source.tree.ClassTree, java.lang.Object)
	 */
	@Override
	public Object visitClass(ClassTree classTree, Trees trees) {
		TreePath path = getCurrentPath();
		ClassModel newClassModel = 
			GenericModelFactory.createClassModelFromJava(codeUnitState, classTree, path, trees);
		
		// notify observers
		if (newClassModel != null) {
			for (IProgramSourceObserver observer : observers)
				observer.notifyFindClass(newClassModel);
		}
		
		return super.visitClass(classTree, trees);
	}

	/* (non-Javadoc)
	 * @see com.sun.source.util.TreeScanner#visitMethod(com.sun.source.tree.MethodTree, java.lang.Object)
	 */
	@Override
	public Object visitMethod(MethodTree methodTree, Trees trees) {
		TreePath path = getCurrentPath();
		MethodModel newMethodInfo = 
			GenericModelFactory.createMethodModelFromJava(codeUnitState, methodTree, path, trees);
		
		// notify observers
		for (IProgramSourceObserver observer : observers)
			observer.notifyFindMethod(newMethodInfo);
		
		return super.visitMethod(methodTree, trees);
	}

	/* (non-Javadoc)
	 * @see com.sun.source.util.TreeScanner#visitVariable(com.sun.source.tree.VariableTree, java.lang.Object)
	 */
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
