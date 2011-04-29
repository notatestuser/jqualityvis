/*
 * MetricAttribute.java (JQualityVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.metrics;

import java.util.Arrays;
import java.util.List;

import org.lukep.javavis.generated.jaxb.Metrics.Metric;
import org.lukep.javavis.generated.jaxb.Metrics.Metric.AppliesTo;
import org.lukep.javavis.generated.jaxb.ObjectFactory;
import org.lukep.javavis.util.FormValidationException;

/**
 * Represents a static metric that is associated with an IMeasurableVisitor for measuring program models.
 */
public class MetricAttribute {
	
	private org.lukep.javavis.generated.jaxb.Metrics.Metric source;
	
	private String name;
	private String nameInternal;
	private MetricType type;
	private String characteristic;
	private String description;
	private List<String> appliesTo;
	private Class<IMeasurableVisitor> visitor;
	private String argument;
	private double cold;
	private double hot;
	
	/**
	 * Instantiates a new metric attribute.
	 *
	 * @param name the name
	 * @param nameInternal the name internal
	 * @param type the type
	 * @param appliesTo the applies to
	 */
	public MetricAttribute(String name, String nameInternal, MetricType type, List<String> appliesTo) {
		this.name = name;
		this.nameInternal = nameInternal;
		this.type = type;
		this.appliesTo = appliesTo;
	}
	
	/**
	 * Instantiates a new metric attribute.
	 *
	 * @param source the source
	 * @param registry the registry
	 * @throws ClassNotFoundException the class not found exception
	 */
	@SuppressWarnings("unchecked")
	public MetricAttribute(org.lukep.javavis.generated.jaxb.Metrics.Metric source, 
			MetricRegistry registry) throws ClassNotFoundException {
		
		this.source = source;
		
		setFieldsFromSourceObject(source, registry);
	}
	
	/**
	 * Sets the fields from source object.
	 *
	 * @param source the source
	 * @param registry the registry
	 * @throws ClassNotFoundException the class not found exception
	 */
	private void setFieldsFromSourceObject(Metric source, MetricRegistry registry) throws ClassNotFoundException {
		// set the fields in our new MetricAttribute object from the data source object
		name = source.getName();
		nameInternal = source.getInternalName();
		type = registry.getOrSetMetricType(source.getType());
		characteristic = source.getCharacteristic();
		description = source.getDescription();
		appliesTo = source.getAppliesTo().getMeasurable();
		visitor = (Class<IMeasurableVisitor>) Class.forName(source.getVisitor());
		argument = source.getArgument();
		cold = source.getCold();
		hot = source.getHot();
	}
	
	/**
	 * Gets the source.
	 *
	 * @return the source
	 */
	public org.lukep.javavis.generated.jaxb.Metrics.Metric getSource() {
		return source;
	}

	/**
	 * Measure target.
	 *
	 * @param target the target
	 * @return the metric measurement
	 */
	public MetricMeasurement measureTarget(IMeasurableNode target) {
		// if this metric applies to the target's type - run it!
		if (testAppliesTo(target.getModelTypeName()))
			return target.accept( this, 
					MeasurableVisitorPool.getInstance().getPooledVisitor(visitor) );
		return null;
	}
	
	/**
	 * Measure target (using the metric measurement cache).
	 *
	 * @param target the target
	 * @return the metric measurement
	 */
	public MetricMeasurement measureTargetCached(IMeasurableNode target) {
		return MetricRegistry.getInstance().getCachedMeasurement(target, this);
	}
	
	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the internal name.
	 *
	 * @return the internal name
	 */
	public String getInternalName() {
		return nameInternal;
	}

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public MetricType getType() {
		return type;
	}

	/**
	 * Gets the characteristic.
	 *
	 * @return the characteristic
	 */
	public String getCharacteristic() {
		return characteristic;
	}

	/**
	 * Sets the characteristic.
	 *
	 * @param characteristic the new characteristic
	 */
	public void setCharacteristic(String characteristic) {
		this.characteristic = characteristic;
	}

	/**
	 * Gets the description.
	 *
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the description.
	 *
	 * @param description the new description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Gets the applies to.
	 *
	 * @return the applies to
	 */
	public List<String> getAppliesTo() {
		return appliesTo;
	}

	/**
	 * Test applies to.
	 *
	 * @param measurableName the measurable name
	 * @return true, if successful
	 */
	public boolean testAppliesTo(String measurableName) {
		return appliesTo.contains(measurableName);
	}

	/**
	 * Gets the argument.
	 *
	 * @return the argument
	 */
	public String getArgument() {
		return argument;
	}
	
	/**
	 * Checks if argument is set.
	 *
	 * @param argument the argument
	 * @return true, if is argument set
	 */
	public boolean isArgumentSet(String argument) {
		if (argument != null)
			return this.argument.contains(argument);
		return false;
	}

	/**
	 * Gets the cold value.
	 *
	 * @return the cold
	 */
	public double getCold() {
		return cold;
	}

	/**
	 * Gets the hot value.
	 *
	 * @return the hot
	 */
	public double getHot() {
		return hot;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return nameInternal + " - " + name;
	}
	
	/**
	 * Validate and create or update a new MetricAttribute. This is a form validation function used by ConfigurationPanes.
	 *
	 * @param toUpdate the to update
	 * @param name the name
	 * @param internalName the internal name
	 * @param type the type
	 * @param characteristic the characteristic
	 * @param description the description
	 * @param appliesTo the applies to
	 * @param visitor the visitor
	 * @param arguments the arguments
	 * @param cold the cold
	 * @param hot the hot
	 * @return the metric attribute
	 * @throws FormValidationException the form validation exception
	 */
	@SuppressWarnings("rawtypes")
	public static MetricAttribute validateAndCreateOrUpdate(MetricAttribute toUpdate, String name, String internalName, 
			String type, String characteristic, String description, String[] appliesTo, String visitor, String arguments, 
			double cold, double hot) 
				throws FormValidationException {
		
		// field length checks (name, internalName, type, visitor)
		if (name.length() <= 0)
			throw new FormValidationException("name", "cannot be blank");
		if (internalName.length() <= 0)
			throw new FormValidationException("internalName", "cannot be blank");
		if (type.length() <= 0)
			throw new FormValidationException("type", "cannot be blank");
		if (characteristic.length() <= 0)
			throw new FormValidationException("characteristic", "cannot be blank");
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
		newMetricSource.setCharacteristic(characteristic);
		newMetricSource.setDescription(description);
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
