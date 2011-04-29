/*
 * IMeasurableNode.java (JQualityVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.metrics;

import org.lukep.javavis.program.generic.models.IGenericModelNode;

/**
 * Represents a single node that is able to have IMeasurableVisitors visit and apply metric 
 * measurements upon it.
 */
public interface IMeasurableNode extends IGenericModelNode {
	
	/**
	 * Accept the specified IMeasurableVisitor to take a measurement of this node using the metric.
	 *
	 * @param metric the metric
	 * @param visitor the visitor
	 * @return the metric measurement
	 */
	public MetricMeasurement accept(MetricAttribute metric, IMeasurableVisitor visitor);
	
	/**
	 * Gets the metric measurement.
	 *
	 * @param attribute the attribute
	 * @return the metric measurement
	 */
	public MetricMeasurement getMetricMeasurement(MetricAttribute attribute);
	
	/**
	 * Checks if the metrics are pre-loaded.
	 *
	 * @return true, if the metrics are pre-loaded
	 */
	public boolean isMetricsPreloaded();
	
	/**
	 * Sets the metrics pre-loaded value.
	 *
	 * @param metricsPreloaded the new metrics preloaded
	 */
	public void setMetricsPreloaded(boolean metricsPreloaded);
	
}
