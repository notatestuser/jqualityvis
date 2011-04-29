/*
 * JavaCodeProcessor.java (JQualityVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
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

import org.lukep.javavis.program.generic.models.ProjectModel;
import org.lukep.javavis.ui.IProgramSourceObserver;

import com.sun.source.util.TreePath;
import com.sun.source.util.Trees;

/**
 * The JavaCodeProcessor is an AbstractProcessor that is utilised by Javac to call up when it's time to scan
 * a bunch of parsed root compilation unit nodes. Effectively we're just proxying everything over to the 
 * JavaCodeTreeVisitor here.
 * 
 * @author Luke Plaster
 */
@SupportedSourceVersion(SourceVersion.RELEASE_6)
@SupportedAnnotationTypes("*")
public class JavaCodeProcessor extends AbstractProcessor {
	
	protected Trees trees;
	protected ProjectModel programStore;
	protected Vector<IProgramSourceObserver> observers;
	
	/**
	 * Instantiates a new JavaCodeProcessor.
	 *
	 * @param observers the observers to notify of status changes
	 * @param programStore the ProjectModel to pass over to the JavaCodeTreeVisitor.
	 */
	public JavaCodeProcessor(Vector<IProgramSourceObserver> observers, 
			ProjectModel programStore) {
		super();

		this.observers = observers;
		this.programStore = programStore;
	}

	/* (non-Javadoc)
	 * @see javax.annotation.processing.AbstractProcessor#init(javax.annotation.processing.ProcessingEnvironment)
	 */
	@Override
	public synchronized void init(ProcessingEnvironment processingEnv) {
		super.init(processingEnv);
		trees = Trees.instance(processingEnv);
	}

	/* (non-Javadoc)
	 * @see javax.annotation.processing.AbstractProcessor#process(java.util.Set, javax.annotation.processing.RoundEnvironment)
	 */
	@Override
	public boolean process(Set<? extends TypeElement> annotations,
			RoundEnvironment roundEnv) {
		
		Set<? extends Element> roots = roundEnv.getRootElements();
		
		// notify observers of the root element count
		for (IProgramSourceObserver observer : observers)
			observer.notifyRootNodeCount(roots.size());
		
		// initialise tree scanner to traverse constituent elements
		JavaCodeTreeVisitor treeVisitor = new JavaCodeTreeVisitor(observers, programStore);
		
		int i = 0;
		for (Element root : roots) {
			// notify observers of the root element's name
			for (IProgramSourceObserver observer : observers)
				observer.notifyRootNodeProcessing(i, root.getSimpleName().toString());
			
			// dispatch the JavaCodeTreeVisitor on the root element
			TreePath tp = trees.getPath(root);
			treeVisitor.scan(tp, trees);
			
			i++;
		}
		
		// notify observers about root node visitation completion
		for (IProgramSourceObserver observer : observers)
			observer.notifyRootNodesProcessed();
		
		return false;
	}

}
