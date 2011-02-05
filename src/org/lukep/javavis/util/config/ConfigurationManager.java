/*
 * ConfigurationManager.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.util.config;

import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.lukep.javavis.generated.jaxb.Metrics;
import org.lukep.javavis.util.JavaVisConstants;

public class ConfigurationManager {

	private static ConfigurationManager instance = null;
	
	private static Metrics metrics = null;
	
	static {
		try {
			// create the JAXB context
			JAXBContext jc = JAXBContext.newInstance(
					Metrics.class.getPackage().getName(),
					ConfigurationManager.class.getClassLoader());
			
			// create the unmarshaller (to marshal our XML to usable objects)
			Unmarshaller unmarshaller = jc.createUnmarshaller();
			
			// load configuration.xml
			InputStream is = ConfigurationManager.class.getClassLoader().getResourceAsStream(
					JavaVisConstants.METRICS_FILE_NAME);
			
			// get the root element
			metrics = (Metrics) unmarshaller.unmarshal(is);
			
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
	
}
