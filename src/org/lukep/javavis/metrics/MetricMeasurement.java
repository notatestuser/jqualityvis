/*
 * MetricMeasurement.java (JQualityVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.metrics;

import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents an individual metric measurement. This class houses the result and relations that were determined 
 * by the calculation algorithm.
 */
public class MetricMeasurement {

	public static final float DEFAULT_RESULT = -1;
	
	private IMeasurableNode target;
	private MetricAttribute metric;
	private double result = -1;
	private boolean resultSet = false;
	private Set<MetricMeasurementRelation> relations;
	
	/**
	 * Instantiates a new metric measurement.
	 *
	 * @param target the target
	 * @param metric the metric
	 */
	public MetricMeasurement(IMeasurableNode target, MetricAttribute metric) {
		super();
		this.target = target;
		this.metric = metric;
	}
	
	/**
	 * Instantiates a new metric measurement.
	 *
	 * @param target the target
	 * @param metric the metric
	 * @param result the result
	 */
	public MetricMeasurement(IMeasurableNode target, MetricAttribute metric, double result) {
		this(target, metric);
		setResult(result);
	}

	/**
	 * Gets the metric.
	 *
	 * @return the metric
	 */
	public MetricAttribute getMetric() {
		return metric;
	}

	/**
	 * Sets the metric.
	 *
	 * @param metric the new metric
	 */
	public void setMetric(MetricAttribute metric) {
		this.metric = metric;
	}
	
	/**
	 * Gets the result.
	 *
	 * @return the result
	 */
	public double getResult() {
		return result;
	}
	
	/**
	 * Gets the rounded result.
	 *
	 * @param fractionalDigits the fractional digits
	 * @return the rounded result
	 */
	public double getRoundedResult(int fractionalDigits) {
		if (Double.isNaN(getResult()))
			return Double.NaN;
		
		StringBuilder sb = new StringBuilder("##########.");
		for (int i = 0; i < fractionalDigits; i++)
			sb.append('#');
		DecimalFormat df = new DecimalFormat(sb.toString());
		return Double.valueOf(df.format(getResult()));
	}

	/**
	 * Sets the result.
	 *
	 * @param result the new result
	 */
	public void setResult(double result) {
		this.result = result;
		resultSet = true;
	}
	
	/**
	 * Checks if is result set.
	 *
	 * @return true, if is result set
	 */
	public boolean isResultSet() {
		return resultSet;
	}
	
	/**
	 * Refresh result.
	 */
	public void refreshResult() {
		setResult(metric.measureTarget(target).getResult());
	}
	
	/**
	 * Adds the relation.
	 *
	 * @param relation the relation
	 */
	public void addRelation(MetricMeasurementRelation relation) {
		if (relations == null)
			relations = new HashSet<MetricMeasurementRelation>();
		relations.add(relation);
	}

	/**
	 * Gets the relations.
	 *
	 * @return the relations
	 */
	public Set<MetricMeasurementRelation> getRelations() {
		if (relations != null)
			return relations;
		return null;
	}
	
}
