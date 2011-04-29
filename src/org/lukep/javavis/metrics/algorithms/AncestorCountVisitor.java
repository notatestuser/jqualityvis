/*
 * AncestorCountVisitor.java (JQualityVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.metrics.algorithms;

import java.util.Collection;

import org.lukep.javavis.metrics.MetricAttribute;
import org.lukep.javavis.metrics.MetricMeasurement;
import org.lukep.javavis.program.generic.models.ClassModel;
import org.lukep.javavis.program.generic.models.ProjectModel;

/**
 * The Class AncestorCountVisitor.
 */
public class AncestorCountVisitor extends AbstractMeasurableVisitor {

	private static final String ARG_AVERAGE	= "average";
	
	/* (non-Javadoc)
	 * @see org.lukep.javavis.metrics.algorithms.AbstractMeasurableVisitor#visit(org.lukep.javavis.metrics.MetricAttribute, org.lukep.javavis.program.generic.models.ProjectModel)
	 */
	@Override
	public MetricMeasurement visit(MetricAttribute metric, ProjectModel project) {

		// calculate the mean of ancestor counts by firstly accumulating the total number of 
		// ancestors of all classes in the project
		Collection<ClassModel> classes = project.getClassMap().values();
		long ancestorCount = 0;
		for (ClassModel clazz : classes)
			ancestorCount += clazz.getAncestorCount();
		
		float mean = metric.isArgumentSet(ARG_AVERAGE) ? 
				ancestorCount / (float)(classes.size()) : ancestorCount;
		return new MetricMeasurement(project, metric, mean);
	}

	/* (non-Javadoc)
	 * @see org.lukep.javavis.metrics.algorithms.AbstractMeasurableVisitor#visit(org.lukep.javavis.metrics.MetricAttribute, org.lukep.javavis.program.generic.models.ClassModel)
	 */
	@Override
	public MetricMeasurement visit(MetricAttribute metric, ClassModel clazz) {
		return new MetricMeasurement(clazz, metric, clazz.getAncestorCount());
	}

	/* (non-Javadoc)
	 * @see org.lukep.javavis.metrics.algorithms.AbstractMeasurableVisitor#resetInstanceAttributes()
	 */
	@Override
	public void resetInstanceAttributes() {
		
	}

}
