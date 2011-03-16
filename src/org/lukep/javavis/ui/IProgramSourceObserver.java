/*
 * IProgramSourceObserver.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.ui;

import org.lukep.javavis.program.generic.models.ClassModel;
import org.lukep.javavis.program.generic.models.MethodModel;

public interface IProgramSourceObserver {

	public void notifyRootNodeCount(int rootNodes);
	public void notifyRootNodeProcessing(int rootNode, String name);
	public void notifyRootNodesProcessed();
	public void notifyFindClass(ClassModel clazz);
	public void notifyFindMethod(MethodModel method);
	
}
