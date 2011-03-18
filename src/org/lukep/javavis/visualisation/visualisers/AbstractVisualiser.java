/*
 * AbstractVisualiser.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.visualisation.visualisers;

import javax.swing.JComponent;

import org.lukep.javavis.ui.swing.WorkspaceContext;
import org.lukep.javavis.visualisation.views.IVisualiserVisitor;

public class AbstractVisualiser implements IVisualiser {

	private WorkspaceContext wspContext;
	
	public AbstractVisualiser(WorkspaceContext wspContext) {
		this.wspContext = wspContext;
	}
	
	public WorkspaceContext getWorkspaceContext() {
		return wspContext;
	}

	@Override
	public JComponent acceptVisualisation(IVisualiserVisitor visitor) {
		// Overridden in sub-class
		return null;
	}

	@Override
	public void setScale(double scale) {
		// Overridden in sub-class
	}

}
