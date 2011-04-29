/*
 * StatementCountVisitor.java (JQualityVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.metrics.algorithms;

import org.lukep.javavis.metrics.MetricAttribute;
import org.lukep.javavis.metrics.MetricMeasurement;
import org.lukep.javavis.program.generic.models.ClassModel;
import org.lukep.javavis.program.generic.models.MethodModel;

/**
 * This visitor class is simply a proxy that'll return the total number of statements if visited by a ClassModel 
 * or the total statement count if visited by a MethodModel.
 */
public class StatementCountVisitor extends AbstractMeasurableVisitor {
	
	/* (non-Javadoc)
	 * @see org.lukep.javavis.metrics.algorithms.AbstractMeasurableVisitor#visit(org.lukep.javavis.metrics.MetricAttribute, org.lukep.javavis.program.generic.models.ClassModel)
	 */
	@Override
	public MetricMeasurement visit(MetricAttribute metric, ClassModel clazz) {
		return new MetricMeasurement(clazz, metric, clazz.getTotalNumberOfStatements());
	}

	/* (non-Javadoc)
	 * @see org.lukep.javavis.metrics.algorithms.AbstractMeasurableVisitor#visit(org.lukep.javavis.metrics.MetricAttribute, org.lukep.javavis.program.generic.models.MethodModel)
	 */
	@Override
	public MetricMeasurement visit(MetricAttribute metric, MethodModel method) {
		return new MetricMeasurement(method, metric, method.getStatementCount());
	}

	/* (non-Javadoc)
	 * @see org.lukep.javavis.metrics.algorithms.AbstractMeasurableVisitor#resetInstanceAttributes()
	 */
	@Override
	public void resetInstanceAttributes() {
		
	}
	
}
