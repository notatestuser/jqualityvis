/*
 * IMeasurableVisitor.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.metrics;

import org.lukep.javavis.program.generic.models.ClassModel;
import org.lukep.javavis.program.generic.models.MethodModel;
import org.lukep.javavis.program.generic.models.VariableModel;

public interface IMeasurableVisitor {

	MetricMeasurement visit(MetricAttribute metric, ClassModel clazz);
	MetricMeasurement visit(MetricAttribute metric, MethodModel method);
	MetricMeasurement visit(MetricAttribute metric, VariableModel variable);
	
}
