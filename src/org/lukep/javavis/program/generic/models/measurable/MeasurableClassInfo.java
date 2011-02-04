/*
 * MeasurableClassInfo.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.program.generic.models.measurable;

import org.lukep.javavis.metrics.IMeasurable;
import org.lukep.javavis.metrics.IMeasurableVisitor;
import org.lukep.javavis.metrics.MetricAttribute;
import org.lukep.javavis.metrics.MetricMeasurement;
import org.lukep.javavis.metrics.MetricRegistry;
import org.lukep.javavis.program.generic.models.ClassInfo;
import org.lukep.javavis.program.generic.models.GenericModelSourceLang;
import org.lukep.javavis.program.generic.models.MethodInfo;

public class MeasurableClassInfo extends ClassInfo implements IMeasurable {
	
	public MeasurableClassInfo(GenericModelSourceLang lang, String simpleName, 
			String qualifiedName) {
		super(lang, simpleName, qualifiedName);
	}
	
	@Override
	public float getMetricMeasurementVal(MetricAttribute attribute) {
		
		switch (attribute) {
		case NUMBER_OF_METHODS:
			return this.getMethodCount();
		case NUMBER_OF_STATEMENTS:
			return getTotalNumberOfStatements();
		case MCCABE_CYCLOMATIC_COMPLEXITY_AVG:
			return getAvgCyclomaticComplexity();
		case MCCABE_CYCLOMATIC_COMPLEXITY_MAX:
			return getMaxCyclomaticComplexity();
		case COHESION:
		case COUPLING:
			return 0; // TODO: associate a real value here
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
	
	private int getTotalNumberOfStatements() {
		int totalStatements = 0;
		MetricMeasurement result;
		
		for (MethodInfo method : methods) {
			if (method instanceof MeasurableMethodInfo) {
				result = ((MeasurableMethodInfo)(method)).getMetricMeasurement(
						MetricAttribute.NUMBER_OF_STATEMENTS);
				totalStatements += result.getResult();
			}
		}
		
		return totalStatements;
	}
	
	private float getAvgCyclomaticComplexity() {
		int count = 0;
		float avgComplexity = 0;
		MetricMeasurement result;
		
		for (MethodInfo method : methods) {
			if (method instanceof MeasurableMethodInfo) {
				result = ((MeasurableMethodInfo)(method)).getMetricMeasurement(
						MetricAttribute.MCCABE_CYCLOMATIC_COMPLEXITY);
				avgComplexity += result.getResult();
				count++;
			}
		}
		if (count > 0)
			avgComplexity /= count;
		
		return avgComplexity;
	}
	
	private float getMaxCyclomaticComplexity() {
		float maxComplexity = 0;
		MetricMeasurement result;
		
		for (MethodInfo method : methods) {
			if (method instanceof MeasurableMethodInfo) {
				result = ((MeasurableMethodInfo)(method)).getMetricMeasurement(
						MetricAttribute.MCCABE_CYCLOMATIC_COMPLEXITY);
				if (result.getResult() > maxComplexity)
					maxComplexity = result.getResult();
			}
		}
		
		return maxComplexity;
	}
	
}
