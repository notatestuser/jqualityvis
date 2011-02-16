/*
 * CodeUnitInfoFactoryState.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.program.generic.helpers;

import org.lukep.javavis.program.generic.models.ClassModel;
import org.lukep.javavis.program.generic.models.ProgramModelStore;

public class CodeUnitInfoFactoryState {

	protected ProgramModelStore programStore;
	protected ClassModel lastClass = null;
	
	public CodeUnitInfoFactoryState(ProgramModelStore programStore) {
		this.programStore = programStore;
	}
	
}
