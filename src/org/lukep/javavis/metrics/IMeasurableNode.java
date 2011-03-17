/*
 * IMeasurableNode.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.metrics;

import org.lukep.javavis.program.generic.models.IGenericModelNode;

public interface IMeasurableNode extends IGenericModelNode {
	
	public MetricMeasurement accept(MetricAttribute metric, IMeasurableVisitor visitor);
	
	public MetricMeasurement getMetricMeasurement(MetricAttribute attribute);
	
	public boolean isMetricsPreloaded();
	public void setMetricsPreloaded(boolean metricsPreloaded);
	
}
