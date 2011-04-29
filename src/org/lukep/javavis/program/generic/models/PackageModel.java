/*
 * PackageModel.java (JQualityVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.program.generic.models;

import org.lukep.javavis.metrics.IMeasurableVisitor;
import org.lukep.javavis.metrics.MetricAttribute;
import org.lukep.javavis.metrics.MetricMeasurement;
import org.lukep.javavis.program.generic.models.Relationship.RelationshipType;
import org.lukep.javavis.util.JavaVisConstants;

/**
 * Represents a package in the abstract node graph.
 */
public class PackageModel extends AbstractModel {
	
	private static final long serialVersionUID = -1910507117107683678L;
	
	/**
	 * Instantiates a new PackageModel.
	 */
	public PackageModel(AbstractModelSourceLang sourceLang, String qualifiedName) {
		super(sourceLang, JavaVisConstants.METRIC_APPLIES_TO_PKG);
		this.qualifiedName = qualifiedName;
	}
	
	///////////////////////////////////////////////////////
	
	/* (non-Javadoc)
	 * @see org.lukep.javavis.program.generic.models.IGenericModelNode#accept(org.lukep.javavis.program.generic.models.IGenericModelNodeVisitor)
	 */
	@Override
	public void accept(IGenericModelNodeVisitor visitor) {
		visitor.visit(this);
	}
	
	/* (non-Javadoc)
	 * @see org.lukep.javavis.metrics.IMeasurableNode#accept(org.lukep.javavis.metrics.MetricAttribute, org.lukep.javavis.metrics.IMeasurableVisitor)
	 */
	@Override
	public MetricMeasurement accept(MetricAttribute metric,
			IMeasurableVisitor visitor) {
		// TODO Auto-generated method stub
		return null;
	}
	
	///////////////////////////////////////////////////////
	
	/**
	 * Adds a ClassModel to this Package using an ENCLOSED_IN relationship.
	 *
	 * @param clazz the clazz
	 */
	public void addClass(ClassModel clazz) {
		addChild(clazz, RelationshipType.ENCLOSED_IN);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return qualifiedName;
	}

}
