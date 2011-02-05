/*
 * IMeasurable.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.metrics;

public interface IMeasurable {

	public static final MetricAttribute[] SUPPORTED_METRICS_CLASS = 
	{ 
		MetricAttribute.NUMBER_OF_METHODS,
		MetricAttribute.NUMBER_OF_STATEMENTS,
		MetricAttribute.MCCABE_CYCLOMATIC_COMPLEXITY_AVG,
		MetricAttribute.MCCABE_CYCLOMATIC_COMPLEXITY_MAX,
		MetricAttribute.COHESION,
		MetricAttribute.COUPLING
	};
	
	public static final MetricAttribute[] SUPPORTED_METRICS_METHOD = 
	{
		MetricAttribute.NUMBER_OF_STATEMENTS,
		MetricAttribute.MCCABE_CYCLOMATIC_COMPLEXITY
	};
	
	public MetricMeasurement accept(IMeasurableVisitor visitor);
	
	public float getMetricMeasurementVal(MetricAttribute attribute);
	public MetricMeasurement getMetricMeasurement(MetricAttribute attribute);
	
}
