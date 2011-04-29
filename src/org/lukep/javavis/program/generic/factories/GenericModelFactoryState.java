/*
 * GenericModelFactoryState.java (JQualityVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.program.generic.factories;

import org.lukep.javavis.program.generic.models.ClassModel;
import org.lukep.javavis.program.generic.models.ProjectModel;

/**
 * Maintains the GenericModelFactory's state (project model and the last class), as all of its methods 
 * are static.
 */
public class GenericModelFactoryState {

	/** The project model store. */
	protected ProjectModel programStore;
	
	/** The last class encountered in the parse tree. */
	protected ClassModel lastClass = null;
	
	/**
	 * Instantiates a new state object.
	 *
	 * @param programStore the program store
	 */
	public GenericModelFactoryState(ProjectModel programStore) {
		this.programStore = programStore;
	}
	
}
