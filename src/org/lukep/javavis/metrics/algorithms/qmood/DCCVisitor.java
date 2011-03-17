/*
 * DCCVisitor.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.metrics.algorithms.qmood;

import java.util.HashSet;
import java.util.Set;

import org.lukep.javavis.metrics.MetricAttribute;
import org.lukep.javavis.metrics.MetricMeasurement;
import org.lukep.javavis.metrics.MetricMeasurementRelation;
import org.lukep.javavis.metrics.algorithms.AbstractMeasurableVisitor;
import org.lukep.javavis.program.generic.models.ClassModel;
import org.lukep.javavis.program.generic.models.MethodModel;
import org.lukep.javavis.program.generic.models.ProjectModel;

public class DCCVisitor extends AbstractMeasurableVisitor {

	private static final String ARG_INCLUDE_RELATIONS	= "includeClassRelations";
	
	private static final String[] PRIMITIVE_TYPE_EXCLUSIONS = 
			{ "byte", "short", "int", "long", "float", "double", "char", "boolean" };
	
	private Set<String> coupledClasses = new HashSet<String>();
	
	@Override
	public MetricMeasurement visit(MetricAttribute metric, ClassModel clazz) {
		
		resetInstanceAttributes();
		
		MetricMeasurement measurement = new MetricMeasurement(clazz, metric);
		
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
		
		// process applicable arguments
		if (metric.isArgumentSet(ARG_INCLUDE_RELATIONS)) {
			// loop through the set of coupled classes and add relations for the classes we know of
			ProjectModel project = null;
			if (clazz.getRootNode() instanceof ProjectModel) {
				project = (ProjectModel) clazz.getRootNode();
				ClassModel coupledClass;
				for (String coupledClassName : coupledClasses) {
					coupledClass = project.lookupClass(coupledClassName);
					if (coupledClass != null)
						measurement.addRelation( new MetricMeasurementRelation(clazz, coupledClass, 1) );
				}
			}
		}
		
		measurement.setResult(coupledClasses.size());
		return measurement;
	}
	
	private static boolean isTypeNotPrimitive(String type) {
		for (String prim : PRIMITIVE_TYPE_EXCLUSIONS)
			if (prim.equals(type))
				return false;
		return true;
	}

	@Override
	public void resetInstanceAttributes() {
		if (coupledClasses.size() > 0)
			coupledClasses.clear();
	}

}
