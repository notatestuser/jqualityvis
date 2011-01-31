/*
 * JavaCodeProcessor.java (JavaVis)
 * Copyright 2010 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.program.java;

import java.util.Set;
import java.util.Vector;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import org.lukep.javavis.ui.IProgramSourceObserver;

import com.sun.source.util.TreePath;
import com.sun.source.util.Trees;

@SupportedSourceVersion(SourceVersion.RELEASE_6)
@SupportedAnnotationTypes("*")
public class JavaCodeProcessor extends AbstractProcessor {
	
	protected Trees trees;
	protected Vector<IProgramSourceObserver> observers;
	
	public JavaCodeProcessor(Vector<IProgramSourceObserver> observers) {
		super();

		this.observers = observers;
	}

	@Override
	public synchronized void init(ProcessingEnvironment processingEnv) {
		super.init(processingEnv);
		trees = Trees.instance(processingEnv);
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations,
			RoundEnvironment roundEnv) {
		Set<? extends Element> roots = roundEnv.getRootElements();
		//System.out.println(roots.size() + " root elements in this RoundEnvironment");
		
		// initialise tree scanner to traverse constituent elements
		JavaCodeTreeVisitor treeVisitor = new JavaCodeTreeVisitor(observers);
		
		for (Element root : roots) {
			TreePath tp = trees.getPath(root);
			treeVisitor.scan(tp, trees);
			
			/*if (root instanceof ClassSymbol) {
				ClassSymbol rootClass = (ClassSymbol) root;
				System.out.println(rootClass.toString());
			}*/
		}
		
		return false;
	}

}
