/*
 * IVisualiser.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.visualisation;

public interface IVisualiser {

	void acceptVisualisation(IVisualisationVisitor visitor);
	
}
