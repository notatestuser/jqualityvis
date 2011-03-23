/*
 * CAMVisitor.java (JMetricVis)
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

public class CAMVisitor extends AbstractMeasurableVisitor {

	private double numerator;
	private double denominator;
	
	private Set<String> paramTypes = new HashSet<String>();
	
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

	@Override
	public void resetInstanceAttributes() {
		numerator = 0;
		denominator = 0;
		if (paramTypes.size() > 0)
			paramTypes.clear();
	}

}
