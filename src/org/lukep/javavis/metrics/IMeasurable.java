/*
 * IMeasurable.java (JavaVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.metrics;

public interface IMeasurable {

	public int getMetricMeasurementVal(MetricAttribute attribute);
	public MetricMeasurement getMetricMeasurement(MetricAttribute attribute);
	
}
