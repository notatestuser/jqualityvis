/*
 * CyclomaticComplexityVisitor.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.metrics.algorithms;

import org.lukep.javavis.metrics.MetricAttribute;
import org.lukep.javavis.metrics.MetricMeasurement;
import org.lukep.javavis.program.generic.models.ClassModel;
import org.lukep.javavis.program.generic.models.MethodModel;
import org.lukep.javavis.util.JavaVisConstants;

public class CyclomaticComplexityVisitor extends AbstractMeasurableVisitor {
	
	@Override
	public MetricMeasurement visit(MetricAttribute metric, ClassModel clazz) {

		// if we're dealing with a class - we just return its total or avg complexity
		double complexity;
		if (metric.getInternalName().equals(JavaVisConstants.METRIC_CYCLO_COMPLEX_AVG))
			complexity = clazz.getAvgCyclomaticComplexity();
		else if (metric.getInternalName().equals(JavaVisConstants.METRIC_CYCLO_COMPLEX_MAX))
			complexity = clazz.getMaxCyclomaticComplexity();
		else
			return null;
		
		return new MetricMeasurement(clazz, metric, complexity);
	}

	@Override
	public MetricMeasurement visit(MetricAttribute metric, MethodModel method) {
		return new MetricMeasurement(method, metric, method.getIndependentExecutionPaths());
	}
	
}
