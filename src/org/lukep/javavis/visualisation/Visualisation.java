/*
 * Visualisation.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.visualisation;

import org.lukep.javavis.generated.jaxb.ObjectFactory;
import org.lukep.javavis.metrics.MetricRegistry;
import org.lukep.javavis.metrics.MetricType;
import org.lukep.javavis.util.FormValidationException;
import org.lukep.javavis.visualisation.views.IVisualiserVisitor;
import org.lukep.javavis.visualisation.visualisers.IVisualiser;

public class Visualisation {
	
	private org.lukep.javavis.generated.jaxb.Visualisations.Visualisation source;
	
	private String name;
	private MetricType type;
	private String description;
	private Class<IVisualiser> visualiser;
	private Class<IVisualiserVisitor> visualiserVisitor;
	private String arguments;
	
	public Visualisation(org.lukep.javavis.generated.jaxb.Visualisations.Visualisation source) throws 
			ClassNotFoundException {
		
		this.source = source;
		
		setFieldsFromSourceObject(source);
	}
	
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

	public org.lukep.javavis.generated.jaxb.Visualisations.Visualisation getSource() {
		return source;
	}

	public String getName() {
		return name;
	}

	public MetricType getType() {
		return type;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Class<IVisualiser> getVisualiserClass() {
		return visualiser;
	}
	
	public Class<IVisualiserVisitor> getVisitorClass() {
		return visualiserVisitor;
	}

	public String getArguments() {
		return arguments;
	}

	@Override
	public String toString() {
		return name;
	}
	
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
