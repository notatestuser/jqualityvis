/*
 * JavaCodeTreeVisitor.java (JavaVis)
 * Copyright 2010 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.program.java;

import org.lukep.javavis.ui.IProgramSourceObserver;

import com.sun.source.tree.ClassTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.util.TreePathScanner;
import com.sun.source.util.Trees;
import com.sun.tools.javac.tree.JCTree.JCClassDecl;

public class JavaCodeTreeVisitor extends TreePathScanner<Object, Trees> {

	protected IProgramSourceObserver observer;
	
	public JavaCodeTreeVisitor(IProgramSourceObserver observer) {
		super();

		this.observer = observer;
	}

	@Override
	public Object visitClass(ClassTree arg0, Trees arg1) {
		JCClassDecl jccl = (JCClassDecl) arg0;
		if (jccl.sym != null)
			observer.notifyFindClass(jccl.sym);
		return super.visitClass(arg0, arg1);
	}

}
