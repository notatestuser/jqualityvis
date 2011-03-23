/*
 * DAMVisitor.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.metrics.algorithms.qmood;

import org.lukep.javavis.metrics.MetricAttribute;
import org.lukep.javavis.metrics.MetricMeasurement;
import org.lukep.javavis.metrics.algorithms.AbstractMeasurableVisitor;
import org.lukep.javavis.program.generic.models.ClassModel;
import org.lukep.javavis.program.generic.models.VariableModel;

public class DAMVisitor extends AbstractMeasurableVisitor {

	private int attributeCount;
	private int hiddenAttributeCount;
	
	@Override
	public MetricMeasurement visit(MetricAttribute metric, ClassModel clazz) {

		resetInstanceAttributes();
		
		for (VariableModel var : clazz.getVariables()) {
			if (var.isClassAttribute()) {
				attributeCount++;
				
				if (var.isPrivate() || var.isProtected())
					hiddenAttributeCount++;
			}
		}
		
		double dam = (double)(hiddenAttributeCount) / (double)(attributeCount);
		return new MetricMeasurement(clazz, metric, Double.isNaN(dam) ? 1.0 : dam);
	}

	@Override
	public void resetInstanceAttributes() {
		attributeCount = 0;
		hiddenAttributeCount = 0;
	}

}
