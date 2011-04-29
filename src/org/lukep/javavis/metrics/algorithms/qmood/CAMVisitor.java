/*
 * CAMVisitor.java (JQualityVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.metrics.algorithms.qmood;

import java.util.HashSet;
import java.util.Set;

import org.lukep.javavis.metrics.MetricAttribute;
import org.lukep.javavis.metrics.MetricMeasurement;
import org.lukep.javavis.metrics.algorithms.AbstractMeasurableVisitor;
import org.lukep.javavis.program.generic.models.ClassModel;
import org.lukep.javavis.program.generic.models.MethodModel;

/**
 * The Class CAMVisitor.
 */
public class CAMVisitor extends AbstractMeasurableVisitor {

	/** The numerator. */
	private double numerator;
	
	/** The denominator. */
	private double denominator;
	
	/** The param types. */
	private Set<String> paramTypes = new HashSet<String>();
	
	/* (non-Javadoc)
	 * @see org.lukep.javavis.metrics.algorithms.AbstractMeasurableVisitor#visit(org.lukep.javavis.metrics.MetricAttribute, org.lukep.javavis.program.generic.models.ClassModel)
	 */
	@Override
	public MetricMeasurement visit(MetricAttribute metric, ClassModel clazz) {
		
		resetInstanceAttributes();
		
		// collect data about method arguments
		for (MethodModel m : clazz.getMethods()) {
			if (m.getParameters() != null)
				paramTypes.addAll(m.getParameters().values());
		}
		
		// count the metric
		for (MethodModel m : clazz.getMethods()) {
			if (m.getParameters() != null)
				numerator += m.getParameters().values().size();
			denominator += paramTypes.size();
		}
		
		return new MetricMeasurement(clazz, metric, 
				numerator/(denominator == 0 ? 1 : denominator));
	}

	/* (non-Javadoc)
	 * @see org.lukep.javavis.metrics.algorithms.AbstractMeasurableVisitor#resetInstanceAttributes()
	 */
	@Override
	public void resetInstanceAttributes() {
		numerator = 0;
		denominator = 0;
		if (paramTypes.size() > 0)
			paramTypes.clear();
	}

}
