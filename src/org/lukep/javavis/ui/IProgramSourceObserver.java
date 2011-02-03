/*
 * IProgramSourceObserver.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.ui;

import org.lukep.javavis.program.generic.models.ClassInfo;
import org.lukep.javavis.program.generic.models.MethodInfo;

public interface IProgramSourceObserver {

	public void notifyFindClass(ClassInfo clazz);
	public void notifyFindMethod(MethodInfo method);
	
}
