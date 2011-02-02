/*
 * MetricMeasurement.java (JavaVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.metrics;

public class MetricMeasurement {

	protected IMeasurable target;
	protected MetricAttribute metric;
	protected int result = -1;
	protected boolean resultSet = false;
	
	public MetricMeasurement(IMeasurable target, MetricAttribute metric) {
		super();
		this.target = target;
		this.metric = metric;
	}

	public MetricAttribute getMetric() {
		return metric;
	}

	public void setMetric(MetricAttribute metric) {
		this.metric = metric;
	}
	
	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
		resultSet = true;
	}
	
	public boolean isResultSet() {
		return resultSet;
	}
	
	public void refreshResult() {
		setResult(target.getMetricMeasurementVal(metric));
	}
	
}
