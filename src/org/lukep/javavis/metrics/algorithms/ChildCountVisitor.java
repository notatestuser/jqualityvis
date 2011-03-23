/*
 * ChildCountVisitor.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.metrics.algorithms;

import java.util.HashSet;
import java.util.Set;

import org.lukep.javavis.metrics.MetricAttribute;
import org.lukep.javavis.metrics.MetricMeasurement;
import org.lukep.javavis.program.generic.models.ClassModel;
import org.lukep.javavis.program.generic.models.ProjectModel;

public class ChildCountVisitor extends AbstractMeasurableVisitor {
	
	private Set<ClassModel> children = new HashSet<ClassModel>();
	
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

	@Override
	public void resetInstanceAttributes() {
		if (children.size() > 0)
			children.clear();
	}

}
