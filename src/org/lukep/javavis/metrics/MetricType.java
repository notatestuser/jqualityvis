/*
 * MetricType.java (JQualityVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.metrics;

/**
 * The Class MetricType.
 */
public class MetricType {
	
	protected String name;

	/**
	 * Instantiates a new metric type.
	 *
	 * @param name the name
	 */
	public MetricType(String name) {
		super();
		this.name = name;
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
	 * Sets the name.
	 *
	 * @param name the new name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return name;
	}
	
}
