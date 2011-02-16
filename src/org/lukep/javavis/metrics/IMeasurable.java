/*
 * IMeasurable.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.metrics;

import java.util.Vector;

public interface IMeasurable {
	
	public MetricMeasurement accept(MetricAttribute metric, IMeasurableVisitor visitor);
	
	public void addChild(IMeasurable child);
	public Vector<IMeasurable> getChildren();
	
	public float getMetricMeasurementVal(MetricAttribute attribute);
	public MetricMeasurement getMetricMeasurement(MetricAttribute attribute);
	
}
