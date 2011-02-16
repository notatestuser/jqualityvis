/*
 * Visualisation.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.visualisation;

import java.util.List;

import org.lukep.javavis.metrics.MetricRegistry;
import org.lukep.javavis.metrics.MetricType;

public class Visualisation {
	
	protected String name;
	protected MetricType type;
	protected Class<IVisualisationVisitor> visitor;
	protected List<String> arguments;
	
	public Visualisation(org.lukep.javavis.generated.jaxb.Visualisations.Visualisation sourceVis) throws 
			ClassNotFoundException {
		
		// set the fields in our new VisualisationView object from the data source object
		name = sourceVis.getName();
		type = MetricRegistry.getInstance().getOrSetMetricType(sourceVis.getType());
		visitor = (Class<IVisualisationVisitor>) Class.forName(sourceVis.getVisitor());
		arguments = sourceVis.getArguments().getArgument();
	}

	public String getName() {
		return name;
	}

	public MetricType getType() {
		return type;
	}

	public Class<IVisualisationVisitor> getVisitorClass() {
		return visitor;
	}

	public List<String> getArguments() {
		return arguments;
	}

	@Override
	public String toString() {
		return name;
	}
	
}

