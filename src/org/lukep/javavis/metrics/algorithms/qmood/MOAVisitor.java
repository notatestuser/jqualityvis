/*
 * AggregationMeasureVisitor.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.metrics.algorithms.qmood;

import org.lukep.javavis.metrics.MetricAttribute;
import org.lukep.javavis.metrics.MetricMeasurement;
import org.lukep.javavis.metrics.algorithms.AbstractMeasurableVisitor;
import org.lukep.javavis.program.generic.models.ClassModel;
import org.lukep.javavis.program.generic.models.VariableModel;

public class MOAVisitor extends AbstractMeasurableVisitor {

	@Override
	public MetricMeasurement visit(MetricAttribute metric, ClassModel clazz) {

		int userClassAttributeCount = 0;
		for (VariableModel var : clazz.getVariables()) {
			if (var.isClassAttribute()) {
				// is this class known by us?
				if (var.getTypeInternalClass() instanceof ClassModel)
					userClassAttributeCount++;
			}
		}
		return new MetricMeasurement(clazz, metric, userClassAttributeCount);
	}

}
