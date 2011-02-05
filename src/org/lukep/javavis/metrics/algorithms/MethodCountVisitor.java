/*
 * MethodCountVisitor.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.metrics.algorithms;

import org.lukep.javavis.metrics.MetricAttribute;
import org.lukep.javavis.metrics.MetricMeasurement;
import org.lukep.javavis.program.generic.models.ClassModel;

public class MethodCountVisitor extends AbstractMeasurableVisitor {

	@Override
	public MetricMeasurement visit(MetricAttribute metric, ClassModel clazz) {
		
		return new MetricMeasurement(clazz, metric, clazz.getMethodCount());
	}

}
