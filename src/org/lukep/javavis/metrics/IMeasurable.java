/*
 * IMeasurable.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.metrics;

public interface IMeasurable {

	public static final MetricAttribute[] SUPPORTED_METRICS_CLASS = 
	{ 
		MetricAttribute.NUMBER_OF_METHODS, 
		MetricAttribute.MCCABE_CYCLOMATIC_COMPLEXITY, 
		MetricAttribute.COHESION,
		MetricAttribute.COUPLING
	};
	
	public static final MetricAttribute[] SUPPORTED_METRICS_METHOD = 
	{ 
		MetricAttribute.MCCABE_CYCLOMATIC_COMPLEXITY, 
	};
	
	public float getMetricMeasurementVal(MetricAttribute attribute);
	public MetricMeasurement getMetricMeasurement(MetricAttribute attribute);
	public MetricMeasurement accept(IMeasurableVisitor visitor);
	
}
