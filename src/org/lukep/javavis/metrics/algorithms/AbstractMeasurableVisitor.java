/*
 * AbstractMeasurableVisitor.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.metrics.algorithms;

import org.lukep.javavis.metrics.IMeasurableVisitor;
import org.lukep.javavis.metrics.MetricAttribute;
import org.lukep.javavis.metrics.MetricMeasurement;
import org.lukep.javavis.program.generic.models.ClassModel;
import org.lukep.javavis.program.generic.models.MethodModel;
import org.lukep.javavis.program.generic.models.VariableModel;

import com.sun.source.util.TreeScanner;

abstract class AbstractMeasurableVisitor extends TreeScanner<Object, Object>
		implements IMeasurableVisitor {

	@Override
	public MetricMeasurement visit(MetricAttribute metric, ClassModel clazz) {
		// implemented in child visitor class
		return null;
	}

	@Override
	public MetricMeasurement visit(MetricAttribute metric, MethodModel method) {
		// implemented in child visitor class
		return null;
	}

	@Override
	public MetricMeasurement visit(MetricAttribute metric, VariableModel variable) {
		// implemented in child visitor class
		return null;
	}

}
