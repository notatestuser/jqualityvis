/*
 * MeasurableMethodInfo.java (JavaVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.program.generic.models.measurable;

import org.lukep.javavis.metrics.IMeasurable;
import org.lukep.javavis.metrics.IMeasurableVisitor;
import org.lukep.javavis.metrics.MetricAttribute;
import org.lukep.javavis.metrics.MetricMeasurement;
import org.lukep.javavis.metrics.MetricRegistry;
import org.lukep.javavis.program.generic.models.GenericModelSourceLang;
import org.lukep.javavis.program.generic.models.MethodInfo;

public class MeasurableMethodInfo extends MethodInfo implements IMeasurable {

	public MeasurableMethodInfo(GenericModelSourceLang lang, String name) {
		super(lang, name);
	}

	@Override
	public int getMetricMeasurementVal(MetricAttribute attribute) {
		
		switch (attribute) {
		
		}
		return -1;
	}

	@Override
	public MetricMeasurement getMetricMeasurement(MetricAttribute attribute) {
		return MetricRegistry.getInstance().getMetricCached(this, attribute);
	}

	@Override
	public void accept(IMeasurableVisitor visitor) {
		visitor.visit(this);
	}

}
