/*
 * ConfigurationManager.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.util.config;

import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.lukep.javavis.generated.jaxb.Metrics;
import org.lukep.javavis.generated.jaxb.Visualisations;
import org.lukep.javavis.util.JavaVisConstants;

public class ConfigurationManager {

	private static ConfigurationManager instance = null;
	
	private static Metrics metrics = null;
	private static Visualisations visualisations = null;
	
	static {
		try {
			// create the JAXB context
			JAXBContext jc = JAXBContext.newInstance(
					Metrics.class.getPackage().getName(),
					ConfigurationManager.class.getClassLoader());
			
			// create the unmarshaller (to marshal our XML to usable objects)
			Unmarshaller unmarshaller = jc.createUnmarshaller();
			
			// load metrics xml
			InputStream is = ConfigurationManager.class.getClassLoader().getResourceAsStream(
					JavaVisConstants.METRICS_FILE_NAME);
			if (is != null)
				metrics = (Metrics) unmarshaller.unmarshal(is);
			
			// load visualisations xml
			is = ConfigurationManager.class.getClassLoader().getResourceAsStream(
					JavaVisConstants.VISUALISATIONS_FILE_NAME);
			if (is != null)
				visualisations = (Visualisations) unmarshaller.unmarshal(is);
			
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
	
	public Visualisations getVisualisations() {
		return visualisations;
	}
	
}
