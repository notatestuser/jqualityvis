/*
 * IVisualiser.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.visualisation.visualisers;

import java.awt.Component;

import org.lukep.javavis.visualisation.views.IVisualiserVisitor;

public interface IVisualiser {
	
	Component acceptVisualisation(IVisualiserVisitor visitor);
	
	public void setScale(double scale);
	
}
