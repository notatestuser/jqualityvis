/*
 * IProgramSourceObserver.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.ui;

import org.lukep.javavis.program.generic.models.ClassModel;
import org.lukep.javavis.program.generic.models.MethodModel;

public interface IProgramSourceObserver {

	public void notifyFindClass(ClassModel clazz);
	public void notifyFindMethod(MethodModel method);
	
}
