/*
 * MetricAttribute.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.metrics;

import java.util.List;

import org.lukep.javavis.generated.jaxb.Metrics.Metric;

public class MetricAttribute {
	
	protected String name;
	protected String name_internal;
	protected MetricType type;
	protected List<String> appliesTo;
	protected Class<IMeasurableVisitor> visitor;
	protected float cold;
	protected float hot;
	
	public MetricAttribute(Metric sourceMetric, MetricRegistry registry) throws ClassNotFoundException {
		
		// set the fields in our new MetricAttribute object from the data source object
		name = sourceMetric.getName();
		name_internal = sourceMetric.getInternalName();
		type = registry.getOrSetMetricType(sourceMetric.getType());
		appliesTo = sourceMetric.getAppliesTo().getMeasurable();
		visitor = (Class<IMeasurableVisitor>) Class.forName(sourceMetric.getVisitor());
		cold = sourceMetric.getCold();
		hot = sourceMetric.getHot();
	}
	
	public String getName() {
		return name;
	}

	public String getInternalName() {
		return name_internal;
	}

	public MetricType getType() {
		return type;
	}

	public List<String> getAppliesTo() {
		return appliesTo;
	}

	public boolean testAppliesTo(String measurableName) {
		return appliesTo.contains(measurableName);
	}

	public Class<IMeasurableVisitor> getVisitorClass() {
		return visitor;
	}

	public float getCold() {
		return cold;
	}

	public float getHot() {
		return hot;
	}

	@Override
	public String toString() {
		return "MetricAttribute [name=" + name + ", name_internal="
				+ name_internal + ", type=" + type + ", appliesTo=" + appliesTo
				+ ", visitor=" + visitor + ", cold=" + cold + ", hot=" + hot
				+ "]";
	}
	
}

