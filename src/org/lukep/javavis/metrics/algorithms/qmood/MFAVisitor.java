/*
 * MFAVisitor.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.metrics.algorithms.qmood;

import org.lukep.javavis.metrics.MetricAttribute;
import org.lukep.javavis.metrics.MetricMeasurement;
import org.lukep.javavis.metrics.algorithms.AbstractMeasurableVisitor;
import org.lukep.javavis.program.generic.models.ClassModel;

public class MFAVisitor extends
		AbstractMeasurableVisitor {

	private int inheritedMethods;
	private int totalMethods;
	
	@Override
	public MetricMeasurement visit(MetricAttribute metric, ClassModel clazz) {

		resetInstanceAttributes();
		
		inheritedMethods = clazz.getInheritedMethodCount();
		totalMethods = inheritedMethods + clazz.getConstructorlessMethodCount();
		
		double result = (double)(inheritedMethods) / totalMethods;
		return new MetricMeasurement(clazz, metric, (Double.isNaN(result) ? 0.0 : result));
	}

	@Override
	public void resetInstanceAttributes() {
		inheritedMethods = 0;
		totalMethods = 0;
	}

}
