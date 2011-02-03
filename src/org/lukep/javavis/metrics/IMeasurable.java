/*
 * IMeasurable.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.metrics;

public interface IMeasurable {

	public float getMetricMeasurementVal(MetricAttribute attribute);
	public MetricMeasurement getMetricMeasurement(MetricAttribute attribute);
	public MetricMeasurement accept(IMeasurableVisitor visitor);
	
}
