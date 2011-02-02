/*
 * IMeasurable.java (JavaVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.metrics;

public interface IMeasurable {

	public MetricMeasurement getMetricMeasurement(MetricAttribute metric);
	
}
