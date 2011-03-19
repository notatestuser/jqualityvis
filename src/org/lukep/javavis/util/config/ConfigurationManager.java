/*
 * ConfigurationManager.java (JMetricVis)
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
	
	private ConfigurationManager() {
		// singleton
	}

	public static ConfigurationManager getInstance() {
		if (instance == null)
			instance = new ConfigurationManager();
		return instance;
	}
	
	public Metrics getMetrics() {
		return metrics;
	}
	
	public QualityModels getQualityModels() {
		return qualityModels;
	}
	
	public Visualisations getVisualisations() {
		return visualisations;
	}
	
	public void writeVisualisations() throws JAXBException, FileNotFoundException {
		// export visualisations xml
		Marshaller marshaller = jc.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, new Boolean(true));
		marshaller.marshal(visualisations, new FileOutputStream(JavaVisConstants.VISUALISATIONS_FILE_NAME));
	}
	
}
