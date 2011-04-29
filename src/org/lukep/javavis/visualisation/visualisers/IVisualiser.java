/*
 * IVisualiser.java (JQualityVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.visualisation.visualisers;

import javax.swing.JComponent;

import org.lukep.javavis.visualisation.views.IVisualiserVisitor;

/**
 * Represents a class that is able to accept an IVisualiserVisitor and run it upon a "visualiser" - which is generally 
 * considered to be a wrapper class for a visualisation library.
 */
public interface IVisualiser {
	
	/**
	 * Accept a visualisation visitor class and apply the visualisation. The resulting visualisation rendered in a JComponent 
	 * is expected to be returned. It's that simple.
	 *
	 * @param visitor the visitor to accept and allow to do all the good stuff to us
	 * @return the j component
	 */
	JComponent acceptVisualisation(IVisualiserVisitor visitor);
	
	/**
	 * Sets the scale of the visualisation - this allows the slider bar at the foot of the interface to zoom visualisations.
	 *
	 * @param scale the new scale to set
	 */
	public void setScale(double scale);
	
}
