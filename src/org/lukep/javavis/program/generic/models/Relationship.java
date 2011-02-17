/*
 * Relationship.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.program.generic.models;

import java.io.Serializable;

import org.lukep.javavis.metrics.IMeasurable;

public class Relationship implements Serializable {
	
	public static enum RelationshipType {
		ENCLOSED_IN, MEMBER_OF, FAN_IN, FAN_OUT
	}
	
	protected IMeasurable source;
	protected IMeasurable target;
	protected RelationshipType relationshipType;
	
	public Relationship(IMeasurable source, IMeasurable target,
			RelationshipType relationshipType) {
		super();
		this.source = source;
		this.target = target;
		this.relationshipType = relationshipType;
	}
	
}
