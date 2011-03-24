/*
 * MetricThreshold.java (JQualityVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.metrics;

import java.io.Serializable;

import org.lukep.javavis.util.FormValidationException;

public class MetricThreshold implements Serializable {

	private String name;
	private String metricInternalName;
	private double bound1;
	private double bound2;
	
	private MetricAttribute cachedMetric;
	
	public MetricThreshold(String name, String metricInternalName, double cold, double hot) {
		super();
		this.name = name;
		this.metricInternalName = metricInternalName;
		this.bound1 = cold;
		this.bound2 = hot;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public MetricAttribute getMetric() {
		if (cachedMetric == null)
			cachedMetric = MetricRegistry.getInstance().getMetricAttribute(metricInternalName);
		return cachedMetric;
	}

	public String getMetricInternalName() {
		return metricInternalName;
	}

	public void setMetricInternalName(String metricInternalName) {
		this.metricInternalName = metricInternalName;
	}

	public double getBound1() {
		return bound1;
	}

	public void setBound1(double cold) {
		this.bound1 = cold;
	}

	public double getBound2() {
		return bound2;
	}

	public void setBound2(double hot) {
		this.bound2 = hot;
	}

	@Override
	public String toString() {
		return name;
	}
	
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
