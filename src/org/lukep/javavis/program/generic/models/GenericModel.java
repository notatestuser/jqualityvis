/*
 * GenericModel.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.program.generic.models;

import java.io.Serializable;

import org.lukep.javavis.metrics.IMeasurable;
import org.lukep.javavis.metrics.MetricAttribute;
import org.lukep.javavis.metrics.MetricMeasurement;
import org.lukep.javavis.metrics.MetricRegistry;

abstract class GenericModel implements Serializable, IMeasurable {

	public String APPLIES_TO_STR = "generic";
	
	protected GenericModelSourceLang sourceLang;

	public GenericModel(GenericModelSourceLang sourceLang, String appliesToString) {
		super();
		this.sourceLang = sourceLang;
		APPLIES_TO_STR = appliesToString;
	}
	
	///////////////////////////////////////////////////////
	
	@Override
	public float getMetricMeasurementVal(MetricAttribute attribute) {
		
		try {
		
			// if the metric applies to a class - run it!
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
	
	///////////////////////////////////////////////////////

	public GenericModelSourceLang getSourceLang() {
		return sourceLang;
	}
	
}
