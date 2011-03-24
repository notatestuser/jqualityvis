/*
 * MetricAttribute.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.metrics;

import java.util.Arrays;
import java.util.List;

import org.lukep.javavis.generated.jaxb.Metrics.Metric;
import org.lukep.javavis.generated.jaxb.Metrics.Metric.AppliesTo;
import org.lukep.javavis.generated.jaxb.ObjectFactory;
import org.lukep.javavis.util.FormValidationException;

public class MetricAttribute {
	
	private org.lukep.javavis.generated.jaxb.Metrics.Metric source;
	
	private String name;
	private String nameInternal;
	private MetricType type;
	private List<String> appliesTo;
	private Class<IMeasurableVisitor> visitor;
	private String argument;
	private double cold;
	private double hot;
	
	public MetricAttribute(String name, String nameInternal, MetricType type, List<String> appliesTo) {
		this.name = name;
		this.nameInternal = nameInternal;
		this.type = type;
		this.appliesTo = appliesTo;
	}
	
	@SuppressWarnings("unchecked")
	public MetricAttribute(org.lukep.javavis.generated.jaxb.Metrics.Metric source, 
			MetricRegistry registry) throws ClassNotFoundException {
		
		this.source = source;
		
		setFieldsFromSourceObject(source, registry);
	}
	
	private void setFieldsFromSourceObject(Metric source, MetricRegistry registry) throws ClassNotFoundException {
		// set the fields in our new MetricAttribute object from the data source object
		name = source.getName();
		nameInternal = source.getInternalName();
		type = registry.getOrSetMetricType(source.getType());
		appliesTo = source.getAppliesTo().getMeasurable();
		visitor = (Class<IMeasurableVisitor>) Class.forName(source.getVisitor());
		argument = source.getArgument();
		cold = source.getCold();
		hot = source.getHot();
	}
	
	public org.lukep.javavis.generated.jaxb.Metrics.Metric getSource() {
		return source;
	}

	public MetricMeasurement measureTarget(IMeasurableNode target) {
		// if this metric applies to the target's type - run it!
		if (testAppliesTo(target.getModelTypeName()))
			return target.accept( this, 
					MeasurableVisitorPool.getInstance().getPooledVisitor(visitor) );
		return null;
	}
	
	public MetricMeasurement measureTargetCached(IMeasurableNode target) {
		return MetricRegistry.getInstance().getCachedMeasurement(target, this);
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
	
	public boolean isArgumentSet(String argument) {
		if (argument != null)
			return this.argument.contains(argument);
		return false;
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
	
	@SuppressWarnings("rawtypes")
	public static MetricAttribute validateAndCreateOrUpdate(MetricAttribute toUpdate, String name, String internalName, 
			String type, String[] appliesTo, String visitor, String arguments, double cold, double hot) 
				throws FormValidationException {
		
		// field length checks (name, internalName, type, visitor)
		if (name.length() <= 0)
			throw new FormValidationException("name", "cannot be blank");
		if (internalName.length() <= 0)
			throw new FormValidationException("internalName", "cannot be blank");
		if (type.length() <= 0)
			throw new FormValidationException("type", "cannot be blank");
		if (visitor.length() <= 0)
			throw new FormValidationException("IMeasurableVisitor", "cannot be blank");
		
		// class existence checks (IMeasurableVisitor)
		Class classLookup;
		try {
			classLookup = Class.forName(visitor);
			if (!IMeasurableVisitor.class.isAssignableFrom(classLookup))
				throw new FormValidationException("IMeasurableVisitor", "class must implement IMeasurableVisitor");
		} catch (ClassNotFoundException e) {
			throw new FormValidationException("IMeasurableVisitor", "class must exist in classpath");
		}
		
		// create a new MetricAttribute object or update toUpdate if it was passed
		ObjectFactory of = new ObjectFactory();
		Metric newMetricSource = 
			(toUpdate == null ? of.createMetricsMetric() : toUpdate.getSource());
		newMetricSource.setName(name);
		newMetricSource.setInternalName(internalName);
		newMetricSource.setType(type);
		newMetricSource.setVisitor(visitor);
		newMetricSource.setArgument(arguments);
		newMetricSource.setCold(cold);
		newMetricSource.setHot(hot);
		
		// set appliesTo values
		AppliesTo at = of.createMetricsMetricAppliesTo();
		at.getMeasurable().addAll(Arrays.asList(appliesTo));
		newMetricSource.setAppliesTo(at);
		
		try {
			MetricRegistry reg = MetricRegistry.getInstance();
			if (toUpdate == null) {
				return new MetricAttribute(newMetricSource, reg);
			} else {
				toUpdate.setFieldsFromSourceObject(newMetricSource, reg);
				return toUpdate;
			}
		} catch (ClassNotFoundException e) {
			// unreachable (we've already validated the specified classes)
			e.printStackTrace();
		}
		return null;
	}
	
}
