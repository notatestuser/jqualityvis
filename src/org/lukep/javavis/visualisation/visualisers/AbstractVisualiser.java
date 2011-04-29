/*
 * AbstractVisualiser.java (JQualityVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.visualisation.visualisers;

import javax.swing.JComponent;

import org.lukep.javavis.ui.swing.WorkspaceContext;
import org.lukep.javavis.visualisation.views.IVisualiserVisitor;

/**
 * Represents a Visualiser that realises the functionality of a visualisation library to produce visualisations.
 */
public class AbstractVisualiser implements IVisualiser {

	/** The workspace's context object. */
	private WorkspaceContext wspContext;
	
	/**
	 * Instantiates a new abstract visualiser.
	 *
	 * @param wspContext the wsp context
	 */
	public AbstractVisualiser(WorkspaceContext wspContext) {
		this.wspContext = wspContext;
	}
	
	/**
	 * Gets the workspace context.
	 *
	 * @return the workspace context
	 */
	public WorkspaceContext getWorkspaceContext() {
		return wspContext;
	}

	/* (non-Javadoc)
	 * @see org.lukep.javavis.visualisation.visualisers.IVisualiser#acceptVisualisation(org.lukep.javavis.visualisation.views.IVisualiserVisitor)
	 */
	@Override
	public JComponent acceptVisualisation(IVisualiserVisitor visitor) {
		// Overridden in sub-class
		return null;
	}

	/* (non-Javadoc)
	 * @see org.lukep.javavis.visualisation.visualisers.IVisualiser#setScale(double)
	 */
	@Override
	public void setScale(double scale) {
		// Overridden in sub-class
	}

}
