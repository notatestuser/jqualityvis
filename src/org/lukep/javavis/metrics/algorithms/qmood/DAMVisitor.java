/*
 * DataAccessMetricVisitor.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.metrics.algorithms.qmood;

import org.lukep.javavis.metrics.MetricAttribute;
import org.lukep.javavis.metrics.MetricMeasurement;
import org.lukep.javavis.metrics.algorithms.AbstractMeasurableVisitor;
import org.lukep.javavis.program.generic.models.ClassModel;
import org.lukep.javavis.program.generic.models.VariableModel;

public class DAMVisitor extends AbstractMeasurableVisitor {

	@Override
	public MetricMeasurement visit(MetricAttribute metric, ClassModel clazz) {

		int attributeCount = 0, hiddenAttributeCount = 0;
		for (VariableModel var : clazz.getVariables()) {
			if (var.isClassAttribute()) {
				attributeCount++;
				
				if (var.isPrivate() || var.isProtected())
					hiddenAttributeCount++;
			}
		}
		
		float dam = (float)(hiddenAttributeCount) / (float)(attributeCount);
		return new MetricMeasurement(clazz, metric, Float.isNaN(dam) ? 1.0f : dam);
	}

}
