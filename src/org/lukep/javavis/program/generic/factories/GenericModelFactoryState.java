/*
 * CodeUnitInfoFactoryState.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.program.generic.factories;

import org.lukep.javavis.program.generic.models.ClassModel;
import org.lukep.javavis.program.generic.models.ProjectModel;

public class GenericModelFactoryState {

	protected ProjectModel programStore;
	protected ClassModel lastClass = null;
	
	public GenericModelFactoryState(ProjectModel programStore) {
		this.programStore = programStore;
	}
	
}
