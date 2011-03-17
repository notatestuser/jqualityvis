/*
 * MetricMeasurementRelation.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.metrics;

import org.lukep.javavis.program.generic.models.IGenericModelNode;

public class MetricMeasurementRelation {

	protected IGenericModelNode source;
	protected IGenericModelNode target;
	protected int weight;
	
	public MetricMeasurementRelation(IGenericModelNode source,
			IGenericModelNode target, int weight) {
		super();
		this.source = source;
		this.target = target;
		this.weight = weight;
	}

	public IGenericModelNode getSource() {
		return source;
	}

	public IGenericModelNode getTarget() {
		return target;
	}
	public int getWeight() {
		return weight;
	}
	
}