/*
 * IVisualiser.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.visualisation;

import java.io.File;

import org.lukep.javavis.ui.swing.WorkspaceContext;

public interface IVisualiser {

	void acceptVisualisation(IVisualisationVisitor visitor);
	
	public WorkspaceContext getContext();
	public void setProgramStatus(String status);
	public void loadCodeBase(File selectedDirectory);
	public void setGraphScale(double scale);
	
}
