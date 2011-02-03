/*
 * IMeasurableVisitor.java (JavaVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.metrics;

import org.lukep.javavis.program.generic.models.measurable.MeasurableClassInfo;
import org.lukep.javavis.program.generic.models.measurable.MeasurableMethodInfo;

public interface IMeasurableVisitor {

	MetricMeasurement visit(MeasurableClassInfo clazz);
	MetricMeasurement visit(MeasurableMethodInfo method);
	
}
