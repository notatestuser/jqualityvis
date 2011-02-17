/*
 * PackageModel.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.program.generic.models;

import org.lukep.javavis.metrics.IMeasurableVisitor;
import org.lukep.javavis.metrics.MetricAttribute;
import org.lukep.javavis.metrics.MetricMeasurement;
import org.lukep.javavis.program.generic.models.Relationship.RelationshipType;
import org.lukep.javavis.util.JavaVisConstants;

public class PackageModel extends AbstractModel {
	
	public PackageModel(AbstractModelSourceLang sourceLang, String qualifiedName) {
		super(sourceLang, JavaVisConstants.METRIC_APPLIES_TO_PKG);
		this.qualifiedName = qualifiedName;
	}
	
	///////////////////////////////////////////////////////
	
	@Override
	public MetricMeasurement accept(MetricAttribute metric,
			IMeasurableVisitor visitor) {
		// TODO Auto-generated method stub
		return null;
	}
	
	///////////////////////////////////////////////////////
	
	public void addClass(ClassModel clazz) {
		addChild(clazz, RelationshipType.ENCLOSED_IN);
	}

	@Override
	public String toString() {
		return qualifiedName;
	}

}
