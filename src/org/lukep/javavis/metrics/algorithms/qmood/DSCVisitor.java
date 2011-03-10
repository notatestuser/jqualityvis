/*
 * DesignSizeVisitor.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.metrics.algorithms.qmood;

import org.lukep.javavis.metrics.MetricAttribute;
import org.lukep.javavis.metrics.MetricMeasurement;
import org.lukep.javavis.metrics.algorithms.AbstractMeasurableVisitor;
import org.lukep.javavis.program.generic.models.ProjectModel;

public class DSCVisitor extends AbstractMeasurableVisitor {

	@Override
	public MetricMeasurement visit(MetricAttribute metric,
			ProjectModel project) {

		return new MetricMeasurement(project, metric, project.getClassMap().size());
	}

}
