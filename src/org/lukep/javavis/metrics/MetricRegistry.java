/*
 * MetricRegistry.java (JavaVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.metrics;

import java.util.HashMap;

public class MetricRegistry { // singleton

	private static MetricRegistry instance = null;
	
	private HashMap<IMeasurable, HashMap<MetricAttribute, MetricMeasurement>> measurementMap;
	
	private MetricRegistry() {
		measurementMap = new HashMap<IMeasurable, HashMap<MetricAttribute,MetricMeasurement>>();
	}
	
	public static MetricRegistry getInstance() {
		if (instance == null)
			instance = new MetricRegistry();
		return instance;
	}
	
	public MetricMeasurement getMetric(IMeasurable target, MetricAttribute attribute) {
		
		// check for if the target IMeasurable exists in our measurement map
		if (measurementMap.containsKey(target)) {
			
			// get the list of measurements associated with the target IMeasurable
			HashMap<MetricAttribute,MetricMeasurement> targetMeasurements = measurementMap.get(target);
			
			// check for if the required MetricAttribute exists in our measurement mapping
			if (targetMeasurements.containsKey(attribute))
				return targetMeasurements.get(attribute);
		}
		return null;
	}
	
	public void setMetric(IMeasurable target, MetricAttribute attribute, MetricMeasurement measurement) {
		
		// check for if the target IMeasurable exists in our measurement map
		HashMap<MetricAttribute,MetricMeasurement> foundMeasurementMap;
		if (measurementMap.containsKey(target)) {
			foundMeasurementMap = measurementMap.get(target);
		} else {
			foundMeasurementMap = new HashMap<MetricAttribute, MetricMeasurement>();
			measurementMap.put(target, foundMeasurementMap);
		}
		
		// add the metric measurement to the map object
		foundMeasurementMap.put(attribute, measurement);
	}
	
}
