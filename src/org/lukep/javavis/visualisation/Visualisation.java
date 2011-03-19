/*
 * Visualisation.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.visualisation;

import org.lukep.javavis.metrics.MetricRegistry;
import org.lukep.javavis.metrics.MetricType;
import org.lukep.javavis.visualisation.views.IVisualiserVisitor;
import org.lukep.javavis.visualisation.visualisers.IVisualiser;

public class Visualisation {
	
	protected String name;
	protected MetricType type;
	protected Class<IVisualiser> visualiser;
	protected Class<IVisualiserVisitor> visualiserVisitor;
	protected String arguments;
	
	@SuppressWarnings("unchecked")
	public Visualisation(org.lukep.javavis.generated.jaxb.Visualisations.Visualisation sourceVis) throws 
			ClassNotFoundException {
		
		// set the fields in our new VisualisationView object from the data source object
		name = sourceVis.getName();
		type = MetricRegistry.getInstance().getOrSetMetricType(sourceVis.getType());
		visualiser = (Class<IVisualiser>) Class.forName(sourceVis.getIVisualiser());
		visualiserVisitor = (Class<IVisualiserVisitor>) Class.forName(sourceVis.getIVisualiserVisitor());
		arguments = sourceVis.getArguments();
	}

	public String getName() {
		return name;
	}

	public MetricType getType() {
		return type;
	}

	public Class<IVisualiser> getVisualiserClass() {
		return visualiser;
	}
	
	public Class<IVisualiserVisitor> getVisitorClass() {
		return visualiserVisitor;
	}

	public String getArguments() {
		return arguments;
	}

	@Override
	public String toString() {
		return name;
	}
	
}

