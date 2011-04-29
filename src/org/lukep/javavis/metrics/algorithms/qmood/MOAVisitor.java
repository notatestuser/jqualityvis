/*
 * MOAVisitor.java (JQualityVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.metrics.algorithms.qmood;

import org.lukep.javavis.metrics.MetricAttribute;
import org.lukep.javavis.metrics.MetricMeasurement;
import org.lukep.javavis.metrics.algorithms.AbstractMeasurableVisitor;
import org.lukep.javavis.program.generic.models.ClassModel;
import org.lukep.javavis.program.generic.models.VariableModel;

/**
 * The Class MOAVisitor.
 */
public class MOAVisitor extends AbstractMeasurableVisitor {

	/** The user class attribute count. */
	private int userClassAttributeCount;
	
	/* (non-Javadoc)
	 * @see org.lukep.javavis.metrics.algorithms.AbstractMeasurableVisitor#visit(org.lukep.javavis.metrics.MetricAttribute, org.lukep.javavis.program.generic.models.ClassModel)
	 */
	@Override
	public MetricMeasurement visit(MetricAttribute metric, ClassModel clazz) {
		
		resetInstanceAttributes();
		
		for (VariableModel var : clazz.getVariables()) {
			if (var.isClassAttribute()) {
				// is this class known by us?
				if (var.getTypeInternalClass() instanceof ClassModel)
					userClassAttributeCount++;
			}
		}
		return new MetricMeasurement(clazz, metric, userClassAttributeCount);
	}

	/* (non-Javadoc)
	 * @see org.lukep.javavis.metrics.algorithms.AbstractMeasurableVisitor#resetInstanceAttributes()
	 */
	@Override
	public void resetInstanceAttributes() {
		userClassAttributeCount = 0;
	}

}
