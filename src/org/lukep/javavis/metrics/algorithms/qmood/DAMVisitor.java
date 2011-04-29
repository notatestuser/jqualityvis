/*
 * DAMVisitor.java (JQualityVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.metrics.algorithms.qmood;

import org.lukep.javavis.metrics.MetricAttribute;
import org.lukep.javavis.metrics.MetricMeasurement;
import org.lukep.javavis.metrics.algorithms.AbstractMeasurableVisitor;
import org.lukep.javavis.program.generic.models.ClassModel;
import org.lukep.javavis.program.generic.models.VariableModel;

/**
 * The Class DAMVisitor.
 */
public class DAMVisitor extends AbstractMeasurableVisitor {

	/** The attribute count. */
	private int attributeCount;
	
	/** The hidden attribute count. */
	private int hiddenAttributeCount;
	
	/* (non-Javadoc)
	 * @see org.lukep.javavis.metrics.algorithms.AbstractMeasurableVisitor#visit(org.lukep.javavis.metrics.MetricAttribute, org.lukep.javavis.program.generic.models.ClassModel)
	 */
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
		return new MetricMeasurement(clazz, metric, Double.isNaN(dam) ? 0.0 : dam);
	}

	/* (non-Javadoc)
	 * @see org.lukep.javavis.metrics.algorithms.AbstractMeasurableVisitor#resetInstanceAttributes()
	 */
	@Override
	public void resetInstanceAttributes() {
		attributeCount = 0;
		hiddenAttributeCount = 0;
	}

}
