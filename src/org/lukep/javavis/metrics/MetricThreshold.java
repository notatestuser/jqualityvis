/*
 * MetricThreshold.java (JQualityVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.metrics;

import java.io.Serializable;

import org.lukep.javavis.util.FormValidationException;

/**
 * The Class MetricThreshold.
 */
public class MetricThreshold implements Serializable {

	private String name;
	private String metricInternalName;
	private double bound1;
	private double bound2;
	private MetricAttribute cachedMetric;
	
	/**
	 * Instantiates a new metric threshold.
	 *
	 * @param name the name
	 * @param metricInternalName the metric internal name
	 * @param cold the cold
	 * @param hot the hot
	 */
	public MetricThreshold(String name, String metricInternalName, double cold, double hot) {
		super();
		this.name = name;
		this.metricInternalName = metricInternalName;
		this.bound1 = cold;
		this.bound2 = hot;
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
	 * Sets the name.
	 *
	 * @param name the new name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Gets the metric.
	 *
	 * @return the metric
	 */
	public MetricAttribute getMetric() {
		if (cachedMetric == null)
			cachedMetric = MetricRegistry.getInstance().getMetricAttribute(metricInternalName);
		return cachedMetric;
	}

	/**
	 * Gets the metric internal name.
	 *
	 * @return the metric internal name
	 */
	public String getMetricInternalName() {
		return metricInternalName;
	}

	/**
	 * Sets the metric internal name.
	 *
	 * @param metricInternalName the new metric internal name
	 */
	public void setMetricInternalName(String metricInternalName) {
		this.metricInternalName = metricInternalName;
	}

	/**
	 * Gets the bound1.
	 *
	 * @return the bound1
	 */
	public double getBound1() {
		return bound1;
	}

	/**
	 * Sets the bound1.
	 *
	 * @param cold the new bound1
	 */
	public void setBound1(double cold) {
		this.bound1 = cold;
	}

	/**
	 * Gets the bound2.
	 *
	 * @return the bound2
	 */
	public double getBound2() {
		return bound2;
	}

	/**
	 * Sets the bound2.
	 *
	 * @param hot the new bound2
	 */
	public void setBound2(double hot) {
		this.bound2 = hot;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return name;
	}
	
	/**
	 * Validate and create or update a new MetricThreshold object. This is a form validation method used by ConfigPanes.
	 *
	 * @param toUpdate the to update
	 * @param name the name
	 * @param metricInternalName the metric internal name
	 * @param cold the cold
	 * @param hot the hot
	 * @return the metric threshold
	 * @throws FormValidationException the form validation exception
	 */
	public static MetricThreshold validateAndCreateOrUpdate(MetricThreshold toUpdate, String name, 
			String metricInternalName, double cold, double hot) 
				throws FormValidationException {
		
		// field length checks (name, internalName, type, visitor)
		if (name.length() <= 0)
			throw new FormValidationException("name", "cannot be blank");
		
		// create a new MetricThreshold object or update toUpdate if it was passed
		MetricThreshold newMetricThres = toUpdate;
		
		if (toUpdate == null) {
			newMetricThres = new MetricThreshold(name, metricInternalName, cold, hot);
		} else {
			// update the fields in existing object
			newMetricThres.setName(name);
			newMetricThres.setMetricInternalName(metricInternalName);
			newMetricThres.setBound1(cold);
			newMetricThres.setBound2(hot);
		}
		
		// set the fields
		newMetricThres.setName(name);
		
		return newMetricThres;
	}
	
}
