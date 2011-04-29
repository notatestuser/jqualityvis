/*
 * DSCVisitor.java (JQualityVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.metrics.algorithms.qmood;

import org.lukep.javavis.metrics.MetricAttribute;
import org.lukep.javavis.metrics.MetricMeasurement;
import org.lukep.javavis.metrics.algorithms.AbstractMeasurableVisitor;
import org.lukep.javavis.program.generic.models.ProjectModel;

/**
 * The Class DSCVisitor.
 */
public class DSCVisitor extends AbstractMeasurableVisitor {

	/* (non-Javadoc)
	 * @see org.lukep.javavis.metrics.algorithms.AbstractMeasurableVisitor#visit(org.lukep.javavis.metrics.MetricAttribute, org.lukep.javavis.program.generic.models.ProjectModel)
	 */
	@Override
	public MetricMeasurement visit(MetricAttribute metric,
			ProjectModel project) {

		return new MetricMeasurement(project, metric, project.getClassMap().size());
	}

	/* (non-Javadoc)
	 * @see org.lukep.javavis.metrics.algorithms.AbstractMeasurableVisitor#resetInstanceAttributes()
	 */
	@Override
	public void resetInstanceAttributes() {
		
	}

}
