/*
 * DCCVisitor.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.metrics.algorithms.qmood;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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
	private Map<String, Short> coupleOccurrences = new HashMap<String, Short>();
	
	@Override
	public MetricMeasurement visit(MetricAttribute metric, ClassModel clazz) {
		
		resetInstanceAttributes();
		
		MetricMeasurement measurement = new MetricMeasurement(clazz, metric);
		
		String returnType;
		for (MethodModel method : clazz.getMethods()) {
			// add return type to set of coupled classes
			returnType = method.getReturnType();
			if (isTypeNotPrimitive(returnType))
				addCoupledClass(returnType);
			
			// add parameter types to set of coupled classes
			if (method.getParameters() != null)
				for (String parameterType : method.getParameters().values()) {
					if (isTypeNotPrimitive(parameterType))
						addCoupledClass(parameterType);
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
						measurement.addRelation( 
								new MetricMeasurementRelation(clazz, coupledClass, 
										coupleOccurrences.get(coupledClassName)) );
				}
			}
		}
		
		measurement.setResult(coupledClasses.size());
		return measurement;
	}
	
	private void addCoupledClass(String className) {
		coupledClasses.add(className);
		short occurrences = 0;
		if (coupleOccurrences.containsKey(className))
			occurrences = coupleOccurrences.get(className);
		coupleOccurrences.put(className, occurrences += 1);
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
		if (coupleOccurrences.size() > 0)
			coupleOccurrences.clear();
	}

}
