/*
 * JavaCodeTreeVisitor.java (JavaVis)
 * Copyright 2010 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.program.java;

import java.util.Vector;

import org.lukep.javavis.program.generic.models.ClassInfo;
import org.lukep.javavis.program.generic.models.MethodInfo;
import org.lukep.javavis.ui.IProgramSourceObserver;

import com.sun.source.tree.ClassTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.util.TreePathScanner;
import com.sun.source.util.Trees;
import com.sun.tools.javac.code.Symbol.ClassSymbol;
import com.sun.tools.javac.tree.JCTree.JCClassDecl;

public class JavaCodeTreeVisitor extends TreePathScanner<Object, Trees> {

	protected Vector<IProgramSourceObserver> observers;
	
	public JavaCodeTreeVisitor(Vector<IProgramSourceObserver> observers) {
		super();

		this.observers = observers;
	}

	@Override
	public Object visitClass(ClassTree classTree, Trees trees) {
		JCClassDecl jccl = (JCClassDecl) classTree;
		ClassSymbol cs = jccl.sym;
		
		if (cs != null) {
			ClassInfo newClassModel = new ClassInfo(
												cs.getSimpleName().toString(),
												cs.getQualifiedName().toString());
		
			for (IProgramSourceObserver observer : observers)
				observer.notifyFindClass(newClassModel);
		}
		return super.visitClass(classTree, trees);
	}

	@Override
	public Object visitMethod(MethodTree methodTree, Trees trees) {
		MethodInfo newMethodInfo = new MethodInfo(methodTree.getName().toString());
		for (IProgramSourceObserver observer : observers)
			observer.notifyFindMethod(newMethodInfo);
		return super.visitMethod(methodTree, trees);
	}

}
