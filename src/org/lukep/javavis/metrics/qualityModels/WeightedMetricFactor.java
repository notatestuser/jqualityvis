/*
 * WeightedMetricFactor.java (JQualityVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.metrics.qualityModels;

import org.lukep.javavis.generated.jaxb.QualityModels.QualityModel.DesignQualityAttributes.DesignQualityAttribute.WeightedMetric;
import org.lukep.javavis.metrics.MetricAttribute;
import org.lukep.javavis.metrics.MetricRegistry;

/**
 * A weighted metric factor used to compute a design quality attribute.
 */
public class WeightedMetricFactor {

	public static enum Value { 
		
		POSITIVE, NEGATIVE;
		
		public static Value stringToValue(String val) {
			if (val.equals("-"))
				return NEGATIVE;
			else
				return POSITIVE;
		}
	}
	
	private MetricAttribute metric;
	private Value value;
	private float weight;
	private String target;
	
	/**
	 * Instantiates a new weighted metric factor.
	 *
	 * @param sourceWeightedMetricEntry the source weighted metric entry
	 * @param registry the registry
	 */
	public WeightedMetricFactor(WeightedMetric sourceWeightedMetricEntry, MetricRegistry registry) {
		
		// load our values!
		metric = registry.getMetricAttribute(sourceWeightedMetricEntry.getInternalName());
		assert(metric != null);
		value = Value.stringToValue(sourceWeightedMetricEntry.getValue());
		weight = sourceWeightedMetricEntry.getWeight();
		target = sourceWeightedMetricEntry.getTarget();
	}

	/**
	 * Gets the metric.
	 *
	 * @return the metric
	 */
	public MetricAttribute getMetric() {
		return metric;
	}

	/**
	 * Gets the value.
	 *
	 * @return the value
	 */
	public Value getValue() {
		return value;
	}

	/**
	 * Gets the weight.
	 *
	 * @return the weight
	 */
	public float getWeight() {
		return weight;
	}

	/**
	 * Gets the target.
	 *
	 * @return the target
	 */
	public String getTarget() {
		return target;
	}
	
}
