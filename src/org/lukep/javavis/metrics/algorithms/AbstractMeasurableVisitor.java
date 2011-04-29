/*
 * AbstractMeasurableVisitor.java (JQualityVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.metrics.algorithms;

import org.lukep.javavis.metrics.IMeasurableVisitor;
import org.lukep.javavis.metrics.MetricAttribute;
import org.lukep.javavis.metrics.MetricMeasurement;
import org.lukep.javavis.program.generic.models.ClassModel;
import org.lukep.javavis.program.generic.models.MethodModel;
import org.lukep.javavis.program.generic.models.ProjectModel;
import org.lukep.javavis.program.generic.models.VariableModel;

/**
 * The Class AbstractMeasurableVisitor.
 */
public abstract class AbstractMeasurableVisitor implements IMeasurableVisitor {

	/* (non-Javadoc)
	 * @see org.lukep.javavis.metrics.IMeasurableVisitor#visit(org.lukep.javavis.metrics.MetricAttribute, org.lukep.javavis.program.generic.models.ProjectModel)
	 */
	@Override
	public MetricMeasurement visit(MetricAttribute metric, ProjectModel project) {
		// implemented in child visitor class
		return null;
	}

	/* (non-Javadoc)
	 * @see org.lukep.javavis.metrics.IMeasurableVisitor#visit(org.lukep.javavis.metrics.MetricAttribute, org.lukep.javavis.program.generic.models.ClassModel)
	 */
	@Override
	public MetricMeasurement visit(MetricAttribute metric, ClassModel clazz) {
		// implemented in child visitor class
		return null;
	}

	/* (non-Javadoc)
	 * @see org.lukep.javavis.metrics.IMeasurableVisitor#visit(org.lukep.javavis.metrics.MetricAttribute, org.lukep.javavis.program.generic.models.MethodModel)
	 */
	@Override
	public MetricMeasurement visit(MetricAttribute metric, MethodModel method) {
		// implemented in child visitor class
		return null;
	}

	/* (non-Javadoc)
	 * @see org.lukep.javavis.metrics.IMeasurableVisitor#visit(org.lukep.javavis.metrics.MetricAttribute, org.lukep.javavis.program.generic.models.VariableModel)
	 */
	@Override
	public MetricMeasurement visit(MetricAttribute metric, VariableModel variable) {
		// implemented in child visitor class
		return null;
	}
	
	/**
	 * Reset instance attributes.
	 */
	public abstract void resetInstanceAttributes();

}
