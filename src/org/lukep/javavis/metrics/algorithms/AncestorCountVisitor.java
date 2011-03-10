/*
 * AverageAncestorsCountVisitor.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.metrics.algorithms;

import java.util.Collection;

import org.lukep.javavis.metrics.MetricAttribute;
import org.lukep.javavis.metrics.MetricMeasurement;
import org.lukep.javavis.program.generic.models.ClassModel;
import org.lukep.javavis.program.generic.models.ProjectModel;

public class AncestorCountVisitor extends AbstractMeasurableVisitor {

	private static final String ARG_AVERAGE	= "average";
	
	@Override
	public MetricMeasurement visit(MetricAttribute metric, ProjectModel project) {

		// calculate the mean of ancestor counts by firstly accumulating the total number of 
		// ancestors of all classes in the project
		Collection<ClassModel> classes = project.getClassMap().values();
		long ancestorCount = 0;
		for (ClassModel clazz : classes)
			ancestorCount += clazz.getAncestorCount();
		
		float mean = metric.getArgument() != null && metric.getArgument().equals(ARG_AVERAGE) ? 
				ancestorCount / (float)(classes.size()) : ancestorCount;
		return new MetricMeasurement(project, metric, mean);
	}

	@Override
	public MetricMeasurement visit(MetricAttribute metric, ClassModel clazz) {
		return new MetricMeasurement(clazz, metric, clazz.getAncestorCount());
	}

}
