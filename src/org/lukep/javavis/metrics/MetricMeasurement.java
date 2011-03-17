/*
 * MetricMeasurement.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.metrics;

import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Set;

public class MetricMeasurement {

	public static final float DEFAULT_RESULT = -1;
	
	private IMeasurableNode target;
	private MetricAttribute metric;
	private double result = -1;
	private boolean resultSet = false;
	private Set<MetricMeasurementRelation> relations;
	
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
	
	public void addRelation(MetricMeasurementRelation relation) {
		if (relations == null)
			relations = new HashSet<MetricMeasurementRelation>();
		relations.add(relation);
	}

	public Set<MetricMeasurementRelation> getRelations() {
		if (relations != null)
			return relations;
		return null;
	}
	
}
