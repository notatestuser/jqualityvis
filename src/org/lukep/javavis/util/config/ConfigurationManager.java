/*
 * ConfigurationManager.java (JQualityVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.util.config;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.lukep.javavis.generated.jaxb.Metrics;
import org.lukep.javavis.generated.jaxb.QualityModels;
import org.lukep.javavis.generated.jaxb.Visualisations;
import org.lukep.javavis.util.JavaVisConstants;

/**
 * The Class ConfigurationManager.
 */
public class ConfigurationManager {

	private static ConfigurationManager instance = null;
	
	private static JAXBContext jc;
	private static Metrics metrics = null;
	private static QualityModels qualityModels = null;
	private static Visualisations visualisations = null;
	
	static {
		try {
			// create the JAXB context
			jc = JAXBContext.newInstance(
					Metrics.class.getPackage().getName(),
					ConfigurationManager.class.getClassLoader());
			
			// create the unmarshaller (to marshal our XML to usable objects)
			Unmarshaller unmarshaller = jc.createUnmarshaller();
			
			// load metrics xml
			metrics = (Metrics) unmarshaller.unmarshal(
					new FileInputStream(JavaVisConstants.METRICS_FILE_NAME));
			
			// load qualityModels xml
			qualityModels = (QualityModels) unmarshaller.unmarshal(
					new FileInputStream(JavaVisConstants.QUALITYMODELS_FILE_NAME));
			
			// load visualisations xml
			visualisations = (Visualisations) unmarshaller.unmarshal(
					new FileInputStream(JavaVisConstants.VISUALISATIONS_FILE_NAME));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Instantiates a new configuration manager.
	 */
	private ConfigurationManager() {
		// singleton
	}

	/**
	 * Gets the single instance of ConfigurationManager.
	 *
	 * @return single instance of ConfigurationManager
	 */
	public static ConfigurationManager getInstance() {
		if (instance == null)
			instance = new ConfigurationManager();
		return instance;
	}
	
	/**
	 * Gets the metrics.
	 *
	 * @return the metrics
	 */
	public Metrics getMetrics() {
		return metrics;
	}
	
	/**
	 * Gets the quality models.
	 *
	 * @return the quality models
	 */
	public QualityModels getQualityModels() {
		return qualityModels;
	}
	
	/**
	 * Gets the visualisations.
	 *
	 * @return the visualisations
	 */
	public Visualisations getVisualisations() {
		return visualisations;
	}
	
	/**
	 * Write visualisations.
	 *
	 * @throws JAXBException the jAXB exception
	 * @throws FileNotFoundException the file not found exception
	 */
	public void writeVisualisations() throws JAXBException, FileNotFoundException {
		// export visualisations xml
		Marshaller marshaller = jc.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, new Boolean(true));
		marshaller.marshal(visualisations, new FileOutputStream(JavaVisConstants.VISUALISATIONS_FILE_NAME));
	}
	
	/**
	 * Write metrics.
	 *
	 * @throws JAXBException the jAXB exception
	 * @throws FileNotFoundException the file not found exception
	 */
	public void writeMetrics() throws JAXBException, FileNotFoundException {
		// export metrics xml
		Marshaller marshaller = jc.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, new Boolean(true));
		marshaller.marshal(metrics, new FileOutputStream(JavaVisConstants.METRICS_FILE_NAME));
	}
	
}
