/*
 * MetricRegistry.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.metrics;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Logger;

import org.lukep.javavis.generated.jaxb.Metrics;
import org.lukep.javavis.generated.jaxb.QualityModels;
import org.lukep.javavis.metrics.qualityModels.QualityModel;
import org.lukep.javavis.program.generic.models.ProjectModel;
import org.lukep.javavis.util.JavaVisConstants;
import org.lukep.javavis.util.config.ConfigurationManager;

public class MetricRegistry { // singleton

	protected final static Logger log = 
		Logger.getLogger(ProjectModel.class.getSimpleName());
	
	private static MetricRegistry instance = null;
	
	private Map<String, MetricType> typeMap;
	private Map<String, MetricAttribute> metricMap;
	private Map<String, Vector<MetricAttribute>> metricSupportMap;
	private Map<String, QualityModel> qualityModelMap;
	private Map<IMeasurableNode, Map<MetricAttribute, MetricMeasurement>> measurementMap;
	
	private MetricRegistry() {
		typeMap = Collections.synchronizedMap(
					new HashMap<String, MetricType>());
		metricMap = Collections.synchronizedMap(
					new LinkedHashMap<String, MetricAttribute>());
		metricSupportMap = Collections.synchronizedMap(
					new HashMap<String, Vector<MetricAttribute>>());
		qualityModelMap = Collections.synchronizedMap(
					new HashMap<String, QualityModel>());
		measurementMap = Collections.synchronizedMap(
					new HashMap<IMeasurableNode, Map<MetricAttribute, MetricMeasurement>>());
		
		// load the MetricAttributes from configuration data source
		MetricAttribute newMetric;
		Metrics metrics = ConfigurationManager.getInstance().getMetrics();
		if (metrics != null) {
			try {
				for (org.lukep.javavis.generated.jaxb.Metrics.Metric metric 
						: metrics.getMetric()) {
					newMetric = new MetricAttribute(metric, this);
					registerMetric(newMetric);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else
			log.warning("No metrics loaded. Check " + JavaVisConstants.METRICS_FILE_NAME);
		
		// load the QualityModels from configuration data source
		QualityModels qualityModels = ConfigurationManager.getInstance().getQualityModels();
		for (org.lukep.javavis.generated.jaxb.QualityModels.QualityModel qm 
				: qualityModels.getQualityModel()) {
			QualityModel newQualityModel = new QualityModel(qm, this);
			qualityModelMap.put(qm.getInternalName(), newQualityModel);
			
			// register all of the design quality attributes of the quality model as standard metrics
			for (MetricAttribute metric : newQualityModel)
				registerMetric(metric);
		}
	}
	
	public static MetricRegistry getInstance() {
		if (instance == null)
			instance = new MetricRegistry();
		return instance;
	}
	
	private void registerMetric(MetricAttribute newMetric) {
		metricMap.put(newMetric.getInternalName(), newMetric);
		
		// add the support info
		Vector<MetricAttribute> curSupportMap;
		for (String applicableMeasurable : newMetric.getAppliesTo()) {
			
			// get or create the list of supported metric attributes
			if (metricSupportMap.containsKey(applicableMeasurable)) {
				curSupportMap = metricSupportMap.get(applicableMeasurable);
			} else {
				curSupportMap = new Vector<MetricAttribute>();
				metricSupportMap.put(applicableMeasurable, curSupportMap);
			}
			
			// add our entry to the list!
			curSupportMap.add(newMetric);
		}
		
		log.info("New metric: " + newMetric.toString());
	}
	
	// Metric Types
	
	public MetricType getMetricType(String typeName) {
		if (typeMap.containsKey(typeName))
			return typeMap.get(typeName);
		return null;
	}
	
	public MetricType getOrSetMetricType(String typeName) {
		MetricType mt = getMetricType(typeName);
		if (mt == null) {
			mt = new MetricType(typeName);
			typeMap.put(typeName, mt);
		}
		return mt;
	}
	
	public Set<String> getMetricTypes() {
		return typeMap.keySet();
	}
	
	// Metric Attributes
	
	public MetricAttribute getMetricAttribute(String metricInternalName) {
		if (metricMap.containsKey(metricInternalName))
			return metricMap.get(metricInternalName);
		return null;
	}
	
	public Collection<MetricAttribute> getMetricAttributes() {
		return metricMap.values();
	}
	
	public Set<String> getMetricAttributeNames() {
		return metricMap.keySet();
	}
	
	public int getMetricAttributeCount() {
		return metricMap.size();
	}
	
	// Supported Metrics
	
	public Set<String> getSupportedMetricTargets() {
		return metricSupportMap.keySet();
	}
	
	public Vector<MetricAttribute> getSupportedMetrics(String measurableName) {
		Vector<MetricAttribute> supportMap;
		if (metricSupportMap.containsKey(measurableName))
			supportMap = metricSupportMap.get(measurableName);
		else
			supportMap = new Vector<MetricAttribute>();
		return supportMap;
	}
	
	public int getSupportedMetricCount(String measurableName) {
		if (metricSupportMap.containsKey(measurableName))
			return metricSupportMap.get(measurableName).size();
		return 0;
	}
	
	// Quality Models
	
	public Map<String, QualityModel> getQualityModelMap() {
		return qualityModelMap;
	}
	
	// Metric Measurements
	
	public MetricMeasurement getMetricMeasurement(IMeasurableNode target, MetricAttribute attribute) {
		
		// check for if the target IMeasurable exists in our measurement map
		if (measurementMap.containsKey(target)) {
			
			// get the list of measurements associated with the target IMeasurable
			Map<MetricAttribute, MetricMeasurement> targetMeasurements = measurementMap.get(target);
			
			// check for if the required MetricAttribute exists in our measurement mapping
			if (targetMeasurements.containsKey(attribute))
				return targetMeasurements.get(attribute);
		}
		return null;
	}

	public void setMetricMeasurement(IMeasurableNode target, MetricAttribute attribute, 
			MetricMeasurement measurement) {
		
		// check for if the target IMeasurable exists in our measurement map
		Map<MetricAttribute,MetricMeasurement> foundMeasurementMap;
		if (measurementMap.containsKey(target)) {
			foundMeasurementMap = measurementMap.get(target);
		} else {
			foundMeasurementMap = Collections.synchronizedMap(
					new HashMap<MetricAttribute, MetricMeasurement>());
			measurementMap.put(target, foundMeasurementMap);
		}
		
		// add the metric measurement to the map object
		foundMeasurementMap.put(attribute, measurement);
	}
	
	public MetricMeasurement getCachedMeasurement(IMeasurableNode target, MetricAttribute attribute) {

		// search for an existing measurement in the MetricRegistry
		MetricMeasurement measurement = getInstance().getMetricMeasurement(target, attribute);
		if (measurement != null)
			return measurement;
		
		// nope - create a new one and set the result
		measurement = attribute.measureTarget(target);
		
		// add measurement to the MetricRegistry
		if (measurement != null)
			getInstance().setMetricMeasurement(target, attribute, measurement);
		
		return measurement;
	}
	
}
