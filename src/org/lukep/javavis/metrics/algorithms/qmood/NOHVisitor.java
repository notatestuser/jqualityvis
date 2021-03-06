/*
 * NOHVisitor.java (JQualityVis)
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

/**
 * The Class NOHVisitor.
 */
public class NOHVisitor extends AbstractMeasurableVisitor {

	// build up a set of all classes that don't inherit from another class (except Object)
	/** The lone wolves. */
	private Set<String> loneWolves = new HashSet<String>();
	// ... and a set of all dependent classes
	/** The dependents. */
	private Set<String> dependents = new HashSet<String>();
	// we'll need to take an intersection of the two sets
	/** The intersection of both. */
	private Set<String> intersection = new HashSet<String>();
	
	/* (non-Javadoc)
	 * @see org.lukep.javavis.metrics.algorithms.AbstractMeasurableVisitor#visit(org.lukep.javavis.metrics.MetricAttribute, org.lukep.javavis.program.generic.models.ProjectModel)
	 */
	@Override
	public MetricMeasurement visit(MetricAttribute metric, ProjectModel project) {
		
		resetInstanceAttributes();
		
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
		
		// find the intersection of the two sets and count
		intersection = new HashSet<String>(loneWolves);
		intersection.retainAll(dependents);
		
		return new MetricMeasurement(project, metric, intersection.size());
	}

	/* (non-Javadoc)
	 * @see org.lukep.javavis.metrics.algorithms.AbstractMeasurableVisitor#resetInstanceAttributes()
	 */
	@Override
	public void resetInstanceAttributes() {
		if (loneWolves.size() > 0)
			loneWolves.clear();
		if (dependents.size() > 0)
			dependents.clear();
		if (intersection.size() > 0)
			intersection.clear();
	}

}
