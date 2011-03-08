/*
 * CodeUnitInfoFactoryState.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.program.generic.helpers;

import org.lukep.javavis.program.generic.models.ClassModel;
import org.lukep.javavis.program.generic.models.ProjectModel;

public class CodeUnitInfoFactoryState {

	protected ProjectModel programStore;
	protected ClassModel lastClass = null;
	
	public CodeUnitInfoFactoryState(ProjectModel programStore) {
		this.programStore = programStore;
	}
	
}
