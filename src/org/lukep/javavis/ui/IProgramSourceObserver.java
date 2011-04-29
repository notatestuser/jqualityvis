/*
 * IProgramSourceObserver.java (JQualityVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.ui;

import org.lukep.javavis.program.generic.models.ClassModel;
import org.lukep.javavis.program.generic.models.MethodModel;

/**
 * An observer interface to gather notifications about the compilation and parsing status 
 * of source code units being carried out by an ISourceLoaderThread. 
 * This is used to update progress bars in the user interface.
 */
public interface IProgramSourceObserver {

	/**
	 * Called when the number of root nodes is known. This is only done once.
	 *
	 * @param rootNodes the root nodes
	 */
	public void notifyRootNodeCount(int rootNodes);
	
	/**
	 * Notifies an observer that the root number identified is now being processed.
	 *
	 * @param rootNode the index of the root node being processed.
	 * @param name the name of the root node being processed (to display in a label)
	 */
	public void notifyRootNodeProcessing(int rootNode, String name);
	
	/**
	 * Called when all of the root nodes in the set have been processed.
	 */
	public void notifyRootNodesProcessed();
	
	/**
	 * Called when a class is found in in the parsed source tree and has been constructed 
	 * ready for addition to a data structure in the ProjectModel.
	 *
	 * @param clazz the newly constructed class model
	 */
	public void notifyFindClass(ClassModel clazz);
	
	/**
	 * Called when a method is found in the parsed source tree.
	 *
	 * @param method the newly constructed method model
	 */
	public void notifyFindMethod(MethodModel method);
	
}
