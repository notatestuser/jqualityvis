/*
 * IMeasurableVisitor.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.metrics;

import org.lukep.javavis.program.generic.models.measurable.MeasurableClassInfo;
import org.lukep.javavis.program.generic.models.measurable.MeasurableMethodInfo;

public interface IMeasurableVisitor {

	MetricMeasurement visit(MetricAttribute metric, MeasurableClassInfo clazz);
	MetricMeasurement visit(MetricAttribute metric, MeasurableMethodInfo method);
	
}
