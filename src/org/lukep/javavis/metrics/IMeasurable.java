/*
 * IMeasurableNode.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.metrics;

public interface IMeasurable {
	
	public MetricMeasurement accept(MetricAttribute metric, IMeasurableVisitor visitor);
	
	public float getMetricMeasurementVal(MetricAttribute attribute);
	public MetricMeasurement getMetricMeasurement(MetricAttribute attribute);
	
}
