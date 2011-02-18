/*
 * Relationship.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.program.generic.models;

import java.io.Serializable;

public class Relationship implements Serializable {
	
	public static enum RelationshipType {
		ENCLOSED_IN, MEMBER_OF, FAN_IN, FAN_OUT
	}
	
	protected IGenericModelNode source;
	protected IGenericModelNode target;
	protected RelationshipType relationshipType;
	
	public Relationship(IGenericModelNode source, IGenericModelNode target,
			RelationshipType relationshipType) {
		super();
		this.source = source;
		this.target = target;
		this.relationshipType = relationshipType;
	}

	public IGenericModelNode getSource() {
		return source;
	}

	public IGenericModelNode getTarget() {
		return target;
	}

	public RelationshipType getRelationshipType() {
		return relationshipType;
	}
	
}
