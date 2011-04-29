/*
 * IMeasurableVisitor.java (JQualityVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.metrics;

import org.lukep.javavis.program.generic.models.ClassModel;
import org.lukep.javavis.program.generic.models.MethodModel;
import org.lukep.javavis.program.generic.models.ProjectModel;
import org.lukep.javavis.program.generic.models.VariableModel;

/**
 * A visitor to a measurable node in the object graph. This is employed when metric measurement is carried out 
 * on a node. Employing a visitor pattern in this case serves to aid extensibility in the future.
 */
public interface IMeasurableVisitor {

	/**
	 * Visit a ProjectModel.
	 *
	 * @param metric the metric to measure
	 * @param project the project to measure
	 * @return the metric measurement obtained through measurement of the metric
	 */
	MetricMeasurement visit(MetricAttribute metric, ProjectModel project);
	
	/**
	 * Visit a ClassModel.
	 *
	 * @param metric the metric to measure
	 * @param clazz the class to measure
	 * @return the metric measurement obtained through measurement of the metric
	 */
	MetricMeasurement visit(MetricAttribute metric, ClassModel clazz);
	
	/**
	 * Visit a MethodModel.
	 *
	 * @param metric the metric to measure
	 * @param method the method to measure
	 * @return the metric measurement obtained through measurement of the metric
	 */
	MetricMeasurement visit(MetricAttribute metric, MethodModel method);
	
	/**
	 * Visit a VariableModel.
	 *
	 * @param metric the metric to measure
	 * @param variable the variable to measure
	 * @return the metric measurement obtained through measurement of the metric
	 */
	MetricMeasurement visit(MetricAttribute metric, VariableModel variable);
	
}
