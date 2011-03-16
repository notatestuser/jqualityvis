/*
 * ClassHierarchyCountVisitor.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.metrics.algorithms.qmood;

import java.util.HashSet;
import java.util.Set;

import org.lukep.javavis.metrics.MetricAttribute;
import org.lukep.javavis.metrics.MetricMeasurement;
import org.lukep.javavis.metrics.algorithms.AbstractMeasurableVisitor;
import org.lukep.javavis.program.generic.models.ClassModel;
import org.lukep.javavis.program.generic.models.ProjectModel;

public class NOHVisitor extends AbstractMeasurableVisitor {

	@Override
	public MetricMeasurement visit(MetricAttribute metric, ProjectModel project) {
		
		// build up a set of all classes that don't inherit from another class (except Object)
		Set<String> loneWolves = new HashSet<String>();
		// ... and a set of all dependent classes
		Set<String> dependents = new HashSet<String>();
		
		for (ClassModel clazz : project.getClassMap().values()) {
			if (clazz.getSuperClassName() == null) {
				// we can add both the simple and fully qualified names - intersection will prune
				// the unmatched values
				loneWolves.add(clazz.getSimpleName());
				loneWolves.add(clazz.getQualifiedName());
			} else {
				// we've got a dependent class - add its superclass to the 'dependents' set
				dependents.add(clazz.getSuperClassName());
			}
		}
		
		// find the intersection of the two classes and count
		Set<String> intersection = new HashSet<String>(loneWolves);
		intersection.retainAll(dependents);
		
		return new MetricMeasurement(project, metric, intersection.size());
	}

}
