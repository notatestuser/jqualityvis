/*
 * Visualisation.java (JQualityVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.visualisation;

import org.lukep.javavis.generated.jaxb.ObjectFactory;
import org.lukep.javavis.metrics.MetricRegistry;
import org.lukep.javavis.metrics.MetricType;
import org.lukep.javavis.util.FormValidationException;
import org.lukep.javavis.visualisation.views.IVisualiserVisitor;
import org.lukep.javavis.visualisation.visualisers.IVisualiser;

/**
 * Represents a visualisation in the system and is asssociated both an IVisualiser class and an IVisualiserVisitor.
 * 
 * @see Visualisation
 */
public class Visualisation {
	
	private org.lukep.javavis.generated.jaxb.Visualisations.Visualisation source;
	
	private String name;
	private MetricType type;
	private String description;
	private Class<IVisualiser> visualiser;
	private Class<IVisualiserVisitor> visualiserVisitor;
	private String arguments;
	
	/**
	 * Instantiates a new visualisation.
	 *
	 * @param source the source
	 * @throws ClassNotFoundException the class not found exception
	 */
	public Visualisation(org.lukep.javavis.generated.jaxb.Visualisations.Visualisation source) throws 
			ClassNotFoundException {
		
		this.source = source;
		
		setFieldsFromSourceObject(source);
	}
	
	/**
	 * Sets the fields from source object.
	 *
	 * @param source the new fields from source object
	 * @throws ClassNotFoundException the class not found exception
	 */
	@SuppressWarnings("unchecked")
	private void setFieldsFromSourceObject(org.lukep.javavis.generated.jaxb.Visualisations.Visualisation source) 
			throws ClassNotFoundException {
		// set the fields in our new VisualisationView object from the data source object
		name = source.getName();
		type = MetricRegistry.getInstance().getOrSetMetricType(source.getType());
		description = source.getDescription();
		visualiser = (Class<IVisualiser>) Class.forName(source.getIVisualiser());
		visualiserVisitor = (Class<IVisualiserVisitor>) Class.forName(source.getIVisualiserVisitor());
		arguments = source.getArguments();
	}

	/**
	 * Gets the source.
	 *
	 * @return the source
	 */
	public org.lukep.javavis.generated.jaxb.Visualisations.Visualisation getSource() {
		return source;
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
	 * Gets the type.
	 *
	 * @return the type
	 */
	public MetricType getType() {
		return type;
	}

	/**
	 * Gets the description.
	 *
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the description.
	 *
	 * @param description the new description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Gets the visualiser class.
	 *
	 * @return the visualiser class
	 */
	public Class<IVisualiser> getVisualiserClass() {
		return visualiser;
	}
	
	/**
	 * Gets the visitor class.
	 *
	 * @return the visitor class
	 */
	public Class<IVisualiserVisitor> getVisitorClass() {
		return visualiserVisitor;
	}

	/**
	 * Gets the arguments.
	 *
	 * @return the arguments
	 */
	public String getArguments() {
		return arguments;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return name;
	}
	
	/**
	 * Validate and create or update a new Visualisation object. This is a form validation function used in ConfigPanes.
	 *
	 * @param toUpdate the to update
	 * @param name the name
	 * @param type the type
	 * @param description the description
	 * @param visualiser the visualiser
	 * @param visualiserVisitor the visualiser visitor
	 * @param arguments the arguments
	 * @return the visualisation
	 * @throws FormValidationException the form validation exception
	 */
	@SuppressWarnings("rawtypes")
	public static Visualisation validateAndCreateOrUpdate(Visualisation toUpdate, String name, String type, 
			String description, String visualiser, String visualiserVisitor, String arguments) 
				throws FormValidationException {
		
		// field length checks (name, type, IVisualier, IVisualiserVisitor)
		if (name.length() <= 0)
			throw new FormValidationException("name", "cannot be blank");
		if (type.length() <= 0)
			throw new FormValidationException("type", "cannot be blank");
		if (visualiser.length() <= 0)
			throw new FormValidationException("IVisualiser", "cannot be blank");
		if (visualiserVisitor.length() <= 0)
			throw new FormValidationException("IVisualiserVisitor", "cannot be blank");
		
		// class existence checks (IVisualiser, IVisualiserVisitor)
		Class classLookup;
		try {
			classLookup = Class.forName(visualiser);
			if (!IVisualiser.class.isAssignableFrom(classLookup))
				throw new FormValidationException("IVisualiser", "class must implement IVisualiser");
		} catch (ClassNotFoundException e) {
			throw new FormValidationException("IVisualiser", "class must exist in classpath");
		}
		try {
			classLookup = Class.forName(visualiserVisitor);
			if (!IVisualiserVisitor.class.isAssignableFrom(classLookup))
				throw new FormValidationException("IVisualiserVisitor", "class must implement IVisualiserVisitor");
		} catch (ClassNotFoundException e) {
			throw new FormValidationException("IVisualiserVisitor", "class must exist in classpath");
		}
		
		// create a new Visualisation object or update toUpdate if it was passed
		org.lukep.javavis.generated.jaxb.Visualisations.Visualisation newVisSource = 
			(toUpdate == null ? new ObjectFactory().createVisualisationsVisualisation() : toUpdate.getSource());
		newVisSource.setName(name);
		newVisSource.setType(type);
		newVisSource.setDescription(description);
		newVisSource.setIVisualiser(visualiser);
		newVisSource.setIVisualiserVisitor(visualiserVisitor);
		newVisSource.setArguments(arguments);
		try {
			if (toUpdate == null) {
				return new Visualisation(newVisSource);
			} else {
				toUpdate.setFieldsFromSourceObject(newVisSource);
				return toUpdate;
			}
		} catch (ClassNotFoundException e) {
			// unreachable (we've already validated the specified classes)
			e.printStackTrace();
		}
		return null;
	}
	
}
