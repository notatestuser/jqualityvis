/*
 * IMeasurableNode.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.metrics;

import java.util.Vector;

import org.lukep.javavis.program.generic.models.Relationship;
import org.lukep.javavis.program.generic.models.Relationship.RelationshipType;

public interface IMeasurable {
	
	public MetricMeasurement accept(MetricAttribute metric, IMeasurableVisitor visitor);
	
	public float getMetricMeasurementVal(MetricAttribute attribute);
	public MetricMeasurement getMetricMeasurement(MetricAttribute attribute);
	
	public void addChild(IMeasurable child, RelationshipType type);
	public Vector<Relationship> getChildren();
	
}
