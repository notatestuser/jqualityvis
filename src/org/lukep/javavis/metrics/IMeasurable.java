/*
 * IMeasurableNode.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.metrics;

public interface IMeasurable {
	
	public String getModelTypeName();
	
	public MetricMeasurement accept(MetricAttribute metric, IMeasurableVisitor visitor);
	
	public MetricMeasurement getMetricMeasurement(MetricAttribute attribute);
	
}
