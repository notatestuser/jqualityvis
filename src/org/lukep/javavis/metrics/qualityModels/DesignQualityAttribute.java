/*
 * DesignQualityAttribute.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.metrics.qualityModels;

import java.util.Arrays;
import java.util.Collection;
import java.util.Vector;

import org.lukep.javavis.generated.jaxb.QualityModels.QualityModel.DesignQualityAttributes;
import org.lukep.javavis.generated.jaxb.QualityModels.QualityModel.DesignQualityAttributes.DesignQualityAttribute.WeightedMetric;
import org.lukep.javavis.metrics.IMeasurableNode;
import org.lukep.javavis.metrics.MetricAttribute;
import org.lukep.javavis.metrics.MetricMeasurement;
import org.lukep.javavis.metrics.MetricRegistry;
import org.lukep.javavis.util.JavaVisConstants;

public class DesignQualityAttribute extends MetricAttribute {
	
	private static final String ATTRIBUTE_METRIC_TYPE = "WeightedClass";
	private static final String[] ATTRIBUTE_METRIC_APPLIES_TO = { 
		//JavaVisConstants.METRIC_APPLIES_TO_PROJCT, 
		JavaVisConstants.METRIC_APPLIES_TO_CLASS };
	
	private Collection<WeightedMetricFactor> weightedMetrics;
	
	public DesignQualityAttribute(DesignQualityAttributes.DesignQualityAttribute sourceAttribute, 
			MetricRegistry registry) {
		
		// set the fields in our new DesignQualityAttribute object from the data source object
		super(sourceAttribute.getName(), 
				sourceAttribute.getName(),
				registry.getOrSetMetricType(ATTRIBUTE_METRIC_TYPE),
				Arrays.asList(ATTRIBUTE_METRIC_APPLIES_TO));
		
		// load in the weighted metrics from the data source object
		weightedMetrics = new Vector<WeightedMetricFactor>(sourceAttribute.getWeightedMetric().size());
		for (WeightedMetric sourceWeightedMetric : sourceAttribute.getWeightedMetric()) {
			weightedMetrics.add( new WeightedMetricFactor(sourceWeightedMetric, registry) );
		}
	}

	@Override
	public MetricMeasurement measureTarget(IMeasurableNode target) {

		String modelTypeName = target.getModelTypeName();
		if (testAppliesTo(modelTypeName)) {
			
			float measurement = 0.0f, current = 0.0f;
			MetricAttribute metric;
			IMeasurableNode curTarget;
			
			for (WeightedMetricFactor wmf : weightedMetrics) {
				// calculate the weighted metric value
				metric = wmf.getMetric();
				curTarget = target;
				
				while (!metric.testAppliesTo(curTarget.getModelTypeName())) {
					curTarget = (IMeasurableNode) curTarget.getParent();
					assert(curTarget != null);
				}
				
				current = metric.measureTarget(curTarget).getResult() * wmf.getWeight();
				
				// tack it on to the accumulated measurement based on its value (positive or negative)
				switch (wmf.getValue()) {
				case POSITIVE:
					measurement += current;
					break;
				case NEGATIVE:
					measurement -= current;
					break;
				}
			}
			
			return new MetricMeasurement(target, this, measurement);
		}
		return null;
	}
	
}
