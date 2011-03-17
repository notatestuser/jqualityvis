/*
 * StatementCountVisitor.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.metrics.algorithms;

import org.lukep.javavis.metrics.MetricAttribute;
import org.lukep.javavis.metrics.MetricMeasurement;
import org.lukep.javavis.program.generic.models.ClassModel;
import org.lukep.javavis.program.generic.models.MethodModel;

public class StatementCountVisitor extends AbstractMeasurableVisitor {
	
	@Override
	public MetricMeasurement visit(MetricAttribute metric, ClassModel clazz) {
		return new MetricMeasurement(clazz, metric, clazz.getTotalNumberOfStatements());
	}

	@Override
	public MetricMeasurement visit(MetricAttribute metric, MethodModel method) {
		return new MetricMeasurement(method, metric, method.getStatementCount());
	}

	@Override
	public void resetInstanceAttributes() {
		
	}
	
}
