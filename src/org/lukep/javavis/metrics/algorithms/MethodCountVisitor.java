/*
 * MethodCountVisitor.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.metrics.algorithms;

import org.lukep.javavis.metrics.IMeasurableVisitor;
import org.lukep.javavis.metrics.MetricAttribute;
import org.lukep.javavis.metrics.MetricMeasurement;
import org.lukep.javavis.program.generic.models.measurable.MeasurableClassInfo;
import org.lukep.javavis.program.generic.models.measurable.MeasurableMethodInfo;

import com.sun.source.util.TreeScanner;

public class MethodCountVisitor extends TreeScanner<Object, Object> implements
		IMeasurableVisitor {

	@Override
	public MetricMeasurement visit(MetricAttribute metric,
			MeasurableClassInfo clazz) {
		
		return new MetricMeasurement(clazz, metric, clazz.getMethodCount());
	}

	@Override
	public MetricMeasurement visit(MetricAttribute metric,
			MeasurableMethodInfo method) {
		// we'll never end up here
		return null;
	}

}
