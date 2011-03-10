/*
 * DirectCouplingVisitor.java (JMetricVis)
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

public class DCCVisitor extends AbstractMeasurableVisitor {

	private static final String[] PRIMITIVE_TYPE_EXCLUSIONS = 
			{ "byte", "short", "int", "long", "float", "double", "char", "boolean" };
	
	@Override
	public MetricMeasurement visit(MetricAttribute metric, ClassModel clazz) {
		
		Set<String> coupledClasses = new HashSet<String>();
		
		String returnType;
		for (MethodModel method : clazz.getMethods()) {
			// add return type to set of coupled classes
			returnType = method.getReturnType();
			if (isTypeNotPrimitive(returnType))
				coupledClasses.add(returnType);
			
			// add parameter types to set of coupled classes
			if (method.getParameters() != null)
				for (String parameterType : method.getParameters().values()) {
					if (isTypeNotPrimitive(parameterType))
						coupledClasses.add(parameterType);
				}
		}
		
		return new MetricMeasurement(clazz, metric, coupledClasses.size());
	}
	
	private static boolean isTypeNotPrimitive(String type) {
		for (String prim : PRIMITIVE_TYPE_EXCLUSIONS)
			if (prim.equals(type))
				return false;
		return true;
	}

}
