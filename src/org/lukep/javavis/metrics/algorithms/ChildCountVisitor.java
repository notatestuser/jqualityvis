/*
 * ChildCountVisitor.java (JQualityVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.metrics.algorithms;

import java.util.HashSet;
import java.util.Set;

import org.lukep.javavis.metrics.MetricAttribute;
import org.lukep.javavis.metrics.MetricMeasurement;
import org.lukep.javavis.program.generic.models.ClassModel;
import org.lukep.javavis.program.generic.models.ProjectModel;

/**
 * The Class ChildCountVisitor.
 */
public class ChildCountVisitor extends AbstractMeasurableVisitor {
	
	/** The children. */
	private Set<ClassModel> children = new HashSet<ClassModel>();
	
	/* (non-Javadoc)
	 * @see org.lukep.javavis.metrics.algorithms.AbstractMeasurableVisitor#visit(org.lukep.javavis.metrics.MetricAttribute, org.lukep.javavis.program.generic.models.ClassModel)
	 */
	@Override
	public MetricMeasurement visit(MetricAttribute metric, ClassModel clazz) {

		resetInstanceAttributes();
		
		// count the number of descendants that this class has in the project
		String classQName = clazz.getQualifiedName();
		if (clazz.getRootNode() instanceof ProjectModel) { // this should always be true
			ProjectModel project = (ProjectModel) clazz.getRootNode();
			// iterate through all classes in the project, find descendants
			for (ClassModel possibleChildClass : project.getClassMap().values()) {
				if (possibleChildClass.getSuperClassName() != null
						&& classQName.equals(possibleChildClass.getSuperClassName()))
					// the class is one of us!
					children.add(possibleChildClass);
			}
		}
		
		return new MetricMeasurement(clazz, metric, children.size());
	}

	/* (non-Javadoc)
	 * @see org.lukep.javavis.metrics.algorithms.AbstractMeasurableVisitor#resetInstanceAttributes()
	 */
	@Override
	public void resetInstanceAttributes() {
		if (children.size() > 0)
			children.clear();
	}

}
