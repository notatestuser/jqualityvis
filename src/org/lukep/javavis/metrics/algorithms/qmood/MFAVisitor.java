/*
 * MFAVisitor.java (JQualityVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.metrics.algorithms.qmood;

import org.lukep.javavis.metrics.MetricAttribute;
import org.lukep.javavis.metrics.MetricMeasurement;
import org.lukep.javavis.metrics.algorithms.AbstractMeasurableVisitor;
import org.lukep.javavis.program.generic.models.ClassModel;

/**
 * The Class MFAVisitor.
 */
public class MFAVisitor extends
		AbstractMeasurableVisitor {

	/** The inherited methods. */
	private int inheritedMethods;
	
	/** The total methods. */
	private int totalMethods;
	
	/* (non-Javadoc)
	 * @see org.lukep.javavis.metrics.algorithms.AbstractMeasurableVisitor#visit(org.lukep.javavis.metrics.MetricAttribute, org.lukep.javavis.program.generic.models.ClassModel)
	 */
	@Override
	public MetricMeasurement visit(MetricAttribute metric, ClassModel clazz) {

		resetInstanceAttributes();
		
		inheritedMethods = clazz.getInheritedMethodCount();
		totalMethods = inheritedMethods + clazz.getConstructorlessMethodCount();
		
		double result = (double)(inheritedMethods) / totalMethods;
		return new MetricMeasurement(clazz, metric, (Double.isNaN(result) ? 0.0 : result));
	}

	/* (non-Javadoc)
	 * @see org.lukep.javavis.metrics.algorithms.AbstractMeasurableVisitor#resetInstanceAttributes()
	 */
	@Override
	public void resetInstanceAttributes() {
		inheritedMethods = 0;
		totalMethods = 0;
	}

}
