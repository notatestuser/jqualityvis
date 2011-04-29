/*
 * VisualisationRegistry.java (JQualityVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.visualisation;

import java.io.FileNotFoundException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Logger;

import javax.xml.bind.JAXBException;

import org.lukep.javavis.generated.jaxb.Visualisations;
import org.lukep.javavis.metrics.MetricType;
import org.lukep.javavis.util.JavaVisConstants;
import org.lukep.javavis.util.config.ConfigurationManager;

/**
 * The VisualisationRegistry is a singleton that stores all of the Visualisations in the program's memory.
 */
public class VisualisationRegistry {

	/** The Constant log. */
	protected final static Logger log = 
		Logger.getLogger(VisualisationRegistry.class.getSimpleName());
	
	/** The instance. */
	private static VisualisationRegistry instance = null;
	
	/** The visualisation map. */
	private Map<MetricType, Vector<Visualisation>> visualisationMap;
	
	/**
	 * Instantiates a new visualisation registry.
	 */
	private VisualisationRegistry() {
		visualisationMap = new LinkedHashMap<MetricType, Vector<Visualisation>>();
		
		// load the Visualisations from configuration data source
		Visualisations visualisations = ConfigurationManager.getInstance().getVisualisations();
		if (visualisations != null) {
			try {
				Visualisation newVis;
				
				for (org.lukep.javavis.generated.jaxb.Visualisations.Visualisation vis : 
						visualisations.getVisualisation()) {
					
					newVis = new Visualisation(vis);
					addVisualisation(newVis);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else
			log.warning("No visualisations loaded. Check " + JavaVisConstants.VISUALISATIONS_FILE_NAME);
	}
	
	/**
	 * Gets the single instance of VisualisationRegistry.
	 *
	 * @return single instance of VisualisationRegistry
	 */
	public static VisualisationRegistry getInstance() {
		if (instance == null)
			instance = new VisualisationRegistry();
		return instance;
	}
	
	/**
	 * Adds the visualisation.
	 *
	 * @param newVis the new vis
	 */
	public void addVisualisation(Visualisation newVis) {
		MetricType metricType = newVis.getType();
		Vector<Visualisation> visualisationList;
		if (visualisationMap.containsKey(metricType)) {
			visualisationList = visualisationMap.get(metricType);
		} else {
			visualisationList = new Vector<Visualisation>();
			visualisationMap.put(metricType, visualisationList);
		}
		visualisationList.add(newVis);
		
		log.info("New visualisation: " + newVis.toString());
	}
	
	/**
	 * Delete visualisation.
	 *
	 * @param doomedVis the doomed vis
	 */
	public void deleteVisualisation(Visualisation doomedVis) {
		for (Vector<Visualisation> visList : visualisationMap.values())
			if (visList.contains(doomedVis))
				visList.remove(doomedVis);
	}
	
	/**
	 * Gets the visualisations by type.
	 *
	 * @param metricType the metric type
	 * @return the visualisations by type
	 */
	public Vector<Visualisation> getVisualisationsByType(MetricType metricType) {
		if (visualisationMap.containsKey(metricType))
			return visualisationMap.get(metricType);
		return new Vector<Visualisation>();
	}
	
	/**
	 * Gets the visualisation map.
	 *
	 * @return the visualisation map
	 */
	public Map<MetricType, Vector<Visualisation>> getVisualisationMap() {
		return visualisationMap;
	}

	/**
	 * Gets the all visualisations.
	 *
	 * @return the all visualisations
	 */
	public Vector<Visualisation> getAllVisualisations() {
		Vector<Visualisation> visualisations = new Vector<Visualisation>();
		for (Vector<Visualisation> visList : visualisationMap.values())
			visualisations.addAll(visList);
		return visualisations;
	}
	
	/**
	 * Save all visualisations.
	 *
	 * @throws FileNotFoundException the file not found exception
	 * @throws JAXBException the jAXB exception
	 */
	public void saveAllVisualisations() throws FileNotFoundException, JAXBException {
		// get the Visualisations from configuration data source
		ConfigurationManager configMgr = ConfigurationManager.getInstance();
		Visualisations visualisations = configMgr.getVisualisations();
		List<org.lukep.javavis.generated.jaxb.Visualisations.Visualisation> visList = 
			visualisations.getVisualisation();
		
		// clear the list and re-add the visualisations we have synced as local Visualisation objects
		visList.clear();
		for (Visualisation vis : getAllVisualisations())
			visList.add(vis.getSource());
		
		// marshal the list back to XML
		configMgr.writeVisualisations();
	}
	
}
