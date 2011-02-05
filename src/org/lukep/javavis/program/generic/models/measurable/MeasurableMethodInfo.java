/*
 * MeasurableMethodInfo.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.program.generic.models.measurable;

import org.lukep.javavis.metrics.IMeasurable;
import org.lukep.javavis.metrics.IMeasurableVisitor;
import org.lukep.javavis.metrics.MetricAttribute;
import org.lukep.javavis.metrics.MetricMeasurement;
import org.lukep.javavis.metrics.MetricRegistry;
import org.lukep.javavis.program.generic.models.GenericModelSourceLang;
import org.lukep.javavis.program.generic.models.MethodModel;

public class MeasurableMethodInfo extends MethodModel implements IMeasurable {

	public static final String APPLIES_TO_STR = "method";
	
	public MeasurableMethodInfo(GenericModelSourceLang lang, String name) {
		super(lang, name);
	}

	@Override
	public float getMetricMeasurementVal(MetricAttribute attribute) {
		
		try {
		
			// if the metric applies to a method - run it!
			if (attribute.testAppliesTo(APPLIES_TO_STR))
				return accept( attribute, attribute.getVisitor() ).getResult();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return -1;
	}

	@Override
	public MetricMeasurement getMetricMeasurement(MetricAttribute attribute) {
		return MetricRegistry.getInstance().getCachedMeasurement(this, attribute);
	}

	@Override
	public MetricMeasurement accept(MetricAttribute metric, IMeasurableVisitor visitor) {
		return visitor.visit(metric, this);
	}

}
