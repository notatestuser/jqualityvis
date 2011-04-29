/*
 * MetricMeasurementRelation.java (JQualityVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.metrics;

import org.lukep.javavis.program.generic.models.IGenericModelNode;

/**
 * A relationship between two nodes in the abstract model graph.
 */
public class MetricMeasurementRelation {

	protected IGenericModelNode source;
	protected IGenericModelNode target;
	protected short weight;
	
	/**
	 * Instantiates a new MetricMeasurementRelation.
	 *
	 * @param source the source
	 * @param target the target
	 * @param weight the weight
	 */
	public MetricMeasurementRelation(IGenericModelNode source,
			IGenericModelNode target, short weight) {
		super();
		this.source = source;
		this.target = target;
		this.weight = weight;
	}

	/**
	 * Gets the source.
	 *
	 * @return the source
	 */
	public IGenericModelNode getSource() {
		return source;
	}

	/**
	 * Gets the target.
	 *
	 * @return the target
	 */
	public IGenericModelNode getTarget() {
		return target;
	}
	
	/**
	 * Gets the weight.
	 *
	 * @return the weight
	 */
	public short getWeight() {
		return weight;
	}
	
}
