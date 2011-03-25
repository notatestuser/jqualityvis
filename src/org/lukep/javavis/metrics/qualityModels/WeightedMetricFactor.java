/*
 * WeightedMetricFactor.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.metrics.qualityModels;

import org.lukep.javavis.generated.jaxb.QualityModels.QualityModel.DesignQualityAttributes.DesignQualityAttribute.WeightedMetric;
import org.lukep.javavis.metrics.MetricAttribute;
import org.lukep.javavis.metrics.MetricRegistry;

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
	
	public WeightedMetricFactor(WeightedMetric sourceWeightedMetricEntry, MetricRegistry registry) {
		
		// load our values!
		metric = registry.getMetricAttribute(sourceWeightedMetricEntry.getInternalName());
		assert(metric != null);
		value = Value.stringToValue(sourceWeightedMetricEntry.getValue());
		weight = sourceWeightedMetricEntry.getWeight();
		target = sourceWeightedMetricEntry.getTarget();
	}

	public MetricAttribute getMetric() {
		return metric;
	}

	public Value getValue() {
		return value;
	}

	public float getWeight() {
		return weight;
	}

	public String getTarget() {
		return target;
	}
	
}
