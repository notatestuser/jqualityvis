/*
 * MethodCountVisitor.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.metrics.algorithms;

import org.lukep.javavis.metrics.MetricAttribute;
import org.lukep.javavis.metrics.MetricMeasurement;
import org.lukep.javavis.program.generic.models.ClassModel;
import org.lukep.javavis.program.generic.models.MethodModel;

public class MethodCountVisitor extends AbstractMeasurableVisitor {

	private static final String ARG_POLYMORPHIC	= "polymorphic";
	private static final String ARG_PUBLIC		= "public"; 
	
	private int methodCount;
	
	@Override
	public MetricMeasurement visit(MetricAttribute metric, ClassModel clazz) {
		
		resetInstanceAttributes();
		
		if (metric.isArgumentSet(ARG_POLYMORPHIC)) { // NOP
			for (MethodModel method : clazz.getMethods())
				if (method.isAbstract())
					methodCount++;
		} else if (metric.isArgumentSet(ARG_PUBLIC)) { // CIS
			for (MethodModel method : clazz.getMethods())
				if (method.isPublic())
					methodCount++;
		} else {
			methodCount = clazz.getMethodCount();
		}
		
		return new MetricMeasurement(clazz, metric, methodCount);
	}

	@Override
	public void resetInstanceAttributes() {
		methodCount = 0;
	}

}
