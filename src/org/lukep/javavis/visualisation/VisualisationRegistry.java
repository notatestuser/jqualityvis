/*
 * VisualisationRegistry.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.visualisation;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Logger;

import org.lukep.javavis.generated.jaxb.Visualisations;
import org.lukep.javavis.metrics.MetricType;
import org.lukep.javavis.util.JavaVisConstants;
import org.lukep.javavis.util.config.ConfigurationManager;

public class VisualisationRegistry {

	protected final static Logger log = 
		Logger.getLogger(VisualisationRegistry.class.getSimpleName());
	
	private static VisualisationRegistry instance = null;
	
	private Map<MetricType, Vector<Visualisation>> visualisationMap;
	
	private VisualisationRegistry() {
		visualisationMap = new HashMap<MetricType, Vector<Visualisation>>();
		
		// load the Visualisations from configuration data source
		Visualisations visualisations = ConfigurationManager.getInstance().getVisualisations();
		if (visualisations != null) {
			try {
				Visualisation newVis;
				MetricType metricType;
				Vector<Visualisation> visualisationList;
				
				for (org.lukep.javavis.generated.jaxb.Visualisations.Visualisation vis : 
						visualisations.getVisualisation()) {
					
					newVis = new Visualisation(vis);
					metricType = newVis.getType();
					
					if (visualisationMap.containsKey(metricType)) {
						visualisationList = visualisationMap.get(metricType);
					} else {
						visualisationList = new Vector<Visualisation>();
						visualisationMap.put(metricType, visualisationList);
					}
					visualisationList.add(newVis);
					
					log.info("New visualisation: " + newVis.toString());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else
			log.warning("No visualisations loaded. Check " + JavaVisConstants.VISUALISATIONS_FILE_NAME);
	}
	
	public static VisualisationRegistry getInstance() {
		if (instance == null)
			instance = new VisualisationRegistry();
		return instance;
	}
	
	public Vector<Visualisation> getVisualisationsByType(MetricType metricType) {
		if (visualisationMap.containsKey(metricType))
			return visualisationMap.get(metricType);
		return new Vector<Visualisation>();
	}
	
}
