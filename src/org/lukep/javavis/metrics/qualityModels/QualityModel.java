/*
 * QualityModel.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.metrics.qualityModels;

import java.util.Vector;

import org.lukep.javavis.metrics.MetricAttribute;
import org.lukep.javavis.metrics.MetricRegistry;

public class QualityModel extends Vector<MetricAttribute> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4064602565979392577L;
	
	private String name;
	private String internalName;
	
	public QualityModel(org.lukep.javavis.generated.jaxb.QualityModels.QualityModel sourceQualityModel, 
			MetricRegistry registry) {
		name = sourceQualityModel.getName();
		internalName = sourceQualityModel.getInternalName();
		
		for (org.lukep.javavis.generated.jaxb.QualityModels.QualityModel.DesignQualityAttributes.DesignQualityAttribute dqa
				: sourceQualityModel.getDesignQualityAttributes().getDesignQualityAttribute()) {
			add( new DesignQualityAttribute(dqa, registry) );
		}
	}

	public String getName() {
		return name;
	}

	public String getInternalName() {
		return internalName;
	}

	@Override
	public String toString() {
		return name + " (" + internalName + ")";
	}
	
}
