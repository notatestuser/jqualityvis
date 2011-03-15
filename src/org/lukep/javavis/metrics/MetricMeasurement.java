/*
 * MetricMeasurement.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.metrics;

import java.text.DecimalFormat;

public class MetricMeasurement {

	public static final float DEFAULT_RESULT = -1;
	
	protected IMeasurableNode target;
	protected MetricAttribute metric;
	protected double result = -1;
	protected boolean resultSet = false;
	
	public MetricMeasurement(IMeasurableNode target, MetricAttribute metric) {
		super();
		this.target = target;
		this.metric = metric;
	}
	
	public MetricMeasurement(IMeasurableNode target, MetricAttribute metric, double result) {
		this(target, metric);
		setResult(result);
	}

	public MetricAttribute getMetric() {
		return metric;
	}

	public void setMetric(MetricAttribute metric) {
		this.metric = metric;
	}
	
	public double getResult() {
		return result;
	}
	
	public double getRoundedResult(int fractionalDigits) {
		if (Double.isNaN(getResult()))
			return Double.NaN;
		
		StringBuilder sb = new StringBuilder("##########.");
		for (int i = 0; i < fractionalDigits; i++)
			sb.append('#');
		DecimalFormat df = new DecimalFormat(sb.toString());
		return Double.valueOf(df.format(getResult()));
	}

	public void setResult(double result) {
		this.result = result;
		resultSet = true;
	}
	
	public boolean isResultSet() {
		return resultSet;
	}
	
	public void refreshResult() {
		setResult(metric.measureTarget(target).getResult());
	}
	
}
