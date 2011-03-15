/*
 * MetricAttribute.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.metrics;

import java.util.List;

import org.lukep.javavis.generated.jaxb.Metrics.Metric;

public class MetricAttribute {
	
	protected String name;
	protected String nameInternal;
	protected MetricType type;
	protected List<String> appliesTo;
	protected Class<IMeasurableVisitor> visitor;
	protected String argument;
	protected double cold;
	protected double hot;
	
	public MetricAttribute(String name, String nameInternal, MetricType type, List<String> appliesTo) {
		this.name = name;
		this.nameInternal = nameInternal;
		this.type = type;
		this.appliesTo = appliesTo;
	}
	
	public MetricAttribute(Metric sourceMetric, MetricRegistry registry) throws ClassNotFoundException {
		
		// set the fields in our new MetricAttribute object from the data source object
		this(sourceMetric.getName(), 
				sourceMetric.getInternalName(), 
				registry.getOrSetMetricType(sourceMetric.getType()), 
				sourceMetric.getAppliesTo().getMeasurable());
		
		// set static metric specific fields
		visitor = (Class<IMeasurableVisitor>) Class.forName(sourceMetric.getVisitor());
		argument = sourceMetric.getArgument();
		cold = sourceMetric.getCold();
		hot = sourceMetric.getHot();
	}
	
	public MetricMeasurement measureTarget(IMeasurableNode target) {
		try {
		
			// if this metric applies to the target's type - run it!
			if (testAppliesTo(target.getModelTypeName()))
				return target.accept( this, visitor.newInstance() );
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String getName() {
		return name;
	}

	public String getInternalName() {
		return nameInternal;
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

	public String getArgument() {
		return argument;
	}

	public double getCold() {
		return cold;
	}

	public double getHot() {
		return hot;
	}

	@Override
	public String toString() {
		return name;// + " " + appliesTo;
	}
	
}
