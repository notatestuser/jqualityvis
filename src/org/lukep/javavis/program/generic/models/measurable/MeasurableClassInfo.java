/*
 * MeasurableClassInfo.java (JavaVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.program.generic.models.measurable;

import org.lukep.javavis.metrics.IMeasurable;
import org.lukep.javavis.metrics.MetricAttribute;
import org.lukep.javavis.metrics.MetricMeasurement;
import org.lukep.javavis.metrics.MetricRegistry;
import org.lukep.javavis.program.generic.models.ClassInfo;
import org.lukep.javavis.program.generic.models.GenericModelSourceLang;

public class MeasurableClassInfo extends ClassInfo implements IMeasurable {

	public MeasurableClassInfo(GenericModelSourceLang lang, String simpleName, String qualifiedName) {
		super(lang, simpleName, qualifiedName);
	}
	
	@Override
	public int getMetricMeasurementVal(MetricAttribute attribute) {
		
		switch (attribute) {
		case NUMBER_OF_METHODS:
			return this.getMethodCount();
		case COHESION:
		case COUPLING:
			return 0; // TODO: associate a real value here
		}
		return -1;
	}
	
	@Override
	public MetricMeasurement getMetricMeasurement(MetricAttribute attribute) {

		// search for an existing measurement in the MetricRegistry
		MetricMeasurement measurement = MetricRegistry.getInstance().getMetric(this, attribute);
		if (measurement != null)
			return measurement;
		
		// nope - create a new one and set the result
		measurement = new MetricMeasurement(this, attribute);
		int measurementVal = getMetricMeasurementVal(attribute);
		if (measurementVal != -1)
			measurement.setResult( measurementVal );
		
		// add measurement to the MetricRegistry
		MetricRegistry.getInstance().setMetric(this, attribute, measurement);
		
		return measurement;
	}
	
}
