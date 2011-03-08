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
	
	@Override
	public MetricMeasurement visit(MetricAttribute metric, ClassModel clazz) {
		
		int methodCount = 0;
		
		String metricArgument = metric.getArgument();
		if (metricArgument == null || metricArgument.length() == 0) 
			methodCount = clazz.getMethodCount();
		else if (metric.getArgument().equals(ARG_POLYMORPHIC)) {
			for (MethodModel method : clazz.getMethods())
				if (method.isAbstract())
					methodCount++;
		} else if (metric.getArgument().equals(ARG_PUBLIC)) {
			for (MethodModel method : clazz.getMethods())
				if (method.isPublic())
					methodCount++;
		}
		
		return new MetricMeasurement(clazz, metric, methodCount);
	}

}
