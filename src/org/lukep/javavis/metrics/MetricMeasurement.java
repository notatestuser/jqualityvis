/*
 * MetricMeasurement.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.metrics;

public class MetricMeasurement {

	public static final float DEFAULT_RESULT = -1;
	
	protected IMeasurable target;
	protected MetricAttribute metric;
	protected float result = -1;
	protected boolean resultSet = false;
	
	public MetricMeasurement(IMeasurable target, MetricAttribute metric) {
		super();
		this.target = target;
		this.metric = metric;
	}
	
	public MetricMeasurement(IMeasurable target, MetricAttribute metric, float result) {
		this(target, metric);
		setResult(result);
	}

	public MetricAttribute getMetric() {
		return metric;
	}

	public void setMetric(MetricAttribute metric) {
		this.metric = metric;
	}
	
	public float getResult() {
		return result;
	}

	public void setResult(float result) {
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
