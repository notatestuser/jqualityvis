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
import org.lukep.javavis.metrics.algorithms.CyclomaticComplexityVisitor;
import org.lukep.javavis.program.generic.models.GenericModelSourceLang;
import org.lukep.javavis.program.generic.models.MethodInfo;

public class MeasurableMethodInfo extends MethodInfo implements IMeasurable {

	public MeasurableMethodInfo(GenericModelSourceLang lang, String name) {
		super(lang, name);
	}

	@Override
	public float getMetricMeasurementVal(MetricAttribute attribute) {
		
		switch (attribute) {
		case MCCABE_CYCLOMATIC_COMPLEXITY:
			return accept( new CyclomaticComplexityVisitor() ).getResult();
		}
		return -1;
	}

	@Override
	public MetricMeasurement getMetricMeasurement(MetricAttribute attribute) {
		return MetricRegistry.getInstance().getMetricCached(this, attribute);
	}

	@Override
	public MetricMeasurement accept(IMeasurableVisitor visitor) {
		return visitor.visit(this);
	}

}
