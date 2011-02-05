/*
 * MeasurableClassInfo.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.program.generic.models.measurable;

import org.lukep.javavis.metrics.IMeasurable;
import org.lukep.javavis.metrics.IMeasurableVisitor;
import org.lukep.javavis.metrics.MetricAttribute;
import org.lukep.javavis.metrics.MetricMeasurement;
import org.lukep.javavis.metrics.MetricRegistry;
import org.lukep.javavis.program.generic.models.ClassModel;
import org.lukep.javavis.program.generic.models.GenericModelSourceLang;
import org.lukep.javavis.program.generic.models.MethodModel;
import org.lukep.javavis.util.JavaVisConstants;

public class MeasurableClassInfo extends ClassModel implements IMeasurable {
	
	public static final String APPLIES_TO_STR = "class";
	
	public MeasurableClassInfo(GenericModelSourceLang lang, String simpleName, 
			String qualifiedName) {
		super(lang, simpleName, qualifiedName);
	}
	
	@Override
	public float getMetricMeasurementVal(MetricAttribute attribute) {
		
		try {
		
			// if the metric applies to a class - run it!
			if (attribute.testAppliesTo(APPLIES_TO_STR))
				return accept( attribute, attribute.getVisitor() ).getResult();
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return -1;
	}
	
	@Override
	public MetricMeasurement getMetricMeasurement(MetricAttribute attribute) {
		return MetricRegistry.getInstance().getCachedMeasurement(this, attribute);
	}

	@Override
	public MetricMeasurement accept(MetricAttribute metric, IMeasurableVisitor visitor) {
		return visitor.visit(metric, this);
	}
	
	public int getTotalNumberOfStatements() {
		int totalStatements = 0;
		MetricMeasurement result;
		
		for (MethodModel method : methods) {
			if (method instanceof MeasurableMethodInfo) {
				result = ((MeasurableMethodInfo)(method)).getMetricMeasurement(
						MetricRegistry.getInstance().getMetricAttribute(
								JavaVisConstants.METRIC_NUM_OF_STATEMENTS));
				totalStatements += result.getResult();
			}
		}
		
		return totalStatements;
	}
	
	public float getAvgCyclomaticComplexity() {
		int count = 0;
		float avgComplexity = 0;
		MetricMeasurement result;
		
		for (MethodModel method : methods) {
			if (method instanceof MeasurableMethodInfo) {
				result = ((MeasurableMethodInfo)(method)).getMetricMeasurement(
						MetricRegistry.getInstance().getMetricAttribute(
								JavaVisConstants.METRIC_CYCLO_COMPLEX));
				avgComplexity += result.getResult();
				count++;
			}
		}
		if (count > 0)
			avgComplexity /= count;
		
		return avgComplexity;
	}
	
	public float getMaxCyclomaticComplexity() {
		float maxComplexity = 0;
		MetricMeasurement result;
		
		for (MethodModel method : methods) {
			if (method instanceof MeasurableMethodInfo) {
				result = ((MeasurableMethodInfo)(method)).getMetricMeasurement(
						MetricRegistry.getInstance().getMetricAttribute(
								JavaVisConstants.METRIC_CYCLO_COMPLEX));
				if (result.getResult() > maxComplexity)
					maxComplexity = result.getResult();
			}
		}
		
		return maxComplexity;
	}
	
}
