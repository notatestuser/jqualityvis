/*
 * IVisualiser.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.visualisation.visualisers;

import javax.swing.JComponent;

import org.lukep.javavis.visualisation.views.IVisualiserVisitor;

public interface IVisualiser {
	
	JComponent acceptVisualisation(IVisualiserVisitor visitor);
	
	public void setScale(double scale);
	
}
