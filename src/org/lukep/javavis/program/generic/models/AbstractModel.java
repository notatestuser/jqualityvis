/*
 * AbstractModel.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.program.generic.models;

import java.util.Vector;

import org.lukep.javavis.metrics.IMeasurable;
import org.lukep.javavis.metrics.MetricAttribute;
import org.lukep.javavis.metrics.MetricMeasurement;
import org.lukep.javavis.metrics.MetricRegistry;
import org.lukep.javavis.program.generic.models.Relationship.RelationshipType;

abstract class AbstractModel implements IGenericModelNode, IMeasurable {

	public String APPLIES_TO_STR = "generic";
	
	protected String simpleName;
	protected String qualifiedName;
	protected AbstractModelSourceLang sourceLang;
	protected IGenericModelNode parent;
	protected Vector<Relationship> children;

	public AbstractModel(AbstractModelSourceLang sourceLang, String appliesToString) {
		super();
		this.sourceLang = sourceLang;
		APPLIES_TO_STR = appliesToString;
	}
	
	public AbstractModel(AbstractModelSourceLang sourceLang, String appliesToString, IGenericModelNode parent) {
		this(sourceLang, appliesToString);
		this.parent = parent;
	}
	
	///////////////////////////////////////////////////////

	@Override
	public IGenericModelNode getParent() {
		return parent;
	}

	@Override
	public void setParent(IGenericModelNode parent) {
		this.parent = parent;
	}
	
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
	
	@Override
	public int getChildCount() {
		if (children == null)
			return 0;
		return children.size();
	}
	
	@Override
	public String getModelTypeName() {
		return APPLIES_TO_STR;
	}
	
	@Override
	public String getContainerName() {
		if (parent != null)
			return parent.getQualifiedName();
		return "";
	}

	@Override
	public String getSimpleName() {
		return this.simpleName;
	}

	@Override
	public void setSimpleName(String simpleName) {
		this.simpleName = simpleName;
	}
	
	@Override
	public String getQualifiedName() {
		return this.qualifiedName;
	}

	@Override
	public void setQualifiedName(String qualifiedName) {
		this.qualifiedName = qualifiedName;
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
	
	public AbstractModelSourceLang getSourceLang() {
		return sourceLang;
	}
	
}
