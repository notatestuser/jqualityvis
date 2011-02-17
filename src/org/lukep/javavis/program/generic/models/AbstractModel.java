/*
 * AbstractModel.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.program.generic.models;

import java.io.Serializable;
import java.util.Vector;

import org.lukep.javavis.metrics.IMeasurable;
import org.lukep.javavis.metrics.MetricAttribute;
import org.lukep.javavis.metrics.MetricMeasurement;
import org.lukep.javavis.metrics.MetricRegistry;
import org.lukep.javavis.program.generic.models.Relationship.RelationshipType;

abstract class AbstractModel implements Serializable, IMeasurable {

	public String APPLIES_TO_STR = "generic";
	
	protected AbstractModelSourceLang sourceLang;
	protected IMeasurable parent;
	protected Vector<Relationship> children;

	public AbstractModel(AbstractModelSourceLang sourceLang, String appliesToString) {
		super();
		this.sourceLang = sourceLang;
		APPLIES_TO_STR = appliesToString;
	}
	
	public AbstractModel(AbstractModelSourceLang sourceLang, String appliesToString, IMeasurable parent) {
		this(sourceLang, appliesToString);
		this.parent = parent;
	}
	
	///////////////////////////////////////////////////////
	
	@Override
	public float getMetricMeasurementVal(MetricAttribute attribute) {
		
		if (attribute != null) {
			try {
			
				// if the metric applies to a class - run it!
				if (attribute.testAppliesTo(APPLIES_TO_STR))
					return accept( attribute, 
							attribute.getVisitorClass().newInstance() ).getResult();
			
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return -1;
	}
	
	@Override
	public MetricMeasurement getMetricMeasurement(MetricAttribute attribute) {
		if (attribute != null)
			return MetricRegistry.getInstance().getCachedMeasurement(this, attribute);
		return null;
	}
	
	///////////////////////////////////////////////////////

	@Override
	public void addChild(IMeasurable child, RelationshipType type) {
		// lazy instantiated list of child models
		if (children == null)
			children = new Vector<Relationship>();
		children.add( new Relationship(this, child, type) );
	}
	
	@Override
	public Vector<Relationship> getChildren() {
		return children;
	}
	
	///////////////////////////////////////////////////////
	
	public AbstractModelSourceLang getSourceLang() {
		return sourceLang;
	}

	public IMeasurable getParent() {
		return parent;
	}

	public void setParent(IMeasurable parent) {
		this.parent = parent;
	}
	
}
