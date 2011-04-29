/*
 * QualityModel.java (JQualityVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.metrics.qualityModels;

import java.util.Vector;

import org.lukep.javavis.metrics.MetricAttribute;
import org.lukep.javavis.metrics.MetricRegistry;

/**
 * The quality model loads in DesignQualityAttribute declarations from source file, using my 
 * DesignQualityAttribute class to load in the design quality attributes, which then subsequently load in the
 * weighted metric factors.
 */
public class QualityModel extends Vector<MetricAttribute> {

	private static final long serialVersionUID = -4064602565979392577L;
	
	private String name;
	private String internalName;
	
	/**
	 * Instantiates a new QualityModel.
	 */
	public QualityModel(org.lukep.javavis.generated.jaxb.QualityModels.QualityModel sourceQualityModel, 
			MetricRegistry registry) {
		name = sourceQualityModel.getName();
		internalName = sourceQualityModel.getInternalName();
		
		for (org.lukep.javavis.generated.jaxb.QualityModels.QualityModel.DesignQualityAttributes.DesignQualityAttribute dqa
				: sourceQualityModel.getDesignQualityAttributes().getDesignQualityAttribute()) {
			add( new DesignQualityAttribute(this, dqa, registry) );
		}
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the internal name.
	 *
	 * @return the internal name
	 */
	public String getInternalName() {
		return internalName;
	}

	/* (non-Javadoc)
	 * @see java.util.Vector#toString()
	 */
	@Override
	public String toString() {
		return name + " (" + internalName + ")";
	}
	
}
