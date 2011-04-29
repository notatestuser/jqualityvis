/*
 * Relationship.java (JQualityVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.program.generic.models;

import java.io.Serializable;

/**
 * Represents a relationship between two nodes in the model node graph.
 * Represents: Inheritance, abstract data type, and message passing (Li, 1992)
 */
public class Relationship implements Serializable {
	
	private static final long serialVersionUID = -4209289935115473583L;
	
	/**
	 * The Enum RelationshipType.
	 */
	public static enum RelationshipType {
		ENCLOSED_IN, 
		 MEMBER_OF, 
		 FAN_IN, 
		 FAN_OUT
	}
	
	protected IGenericModelNode source;
	protected IGenericModelNode target;
	protected RelationshipType relationshipType;
	
	/**
	 * Instantiates a new relationship.
	 *
	 * @param source the source
	 * @param target the target
	 * @param relationshipType the relationship type
	 */
	public Relationship(IGenericModelNode source, IGenericModelNode target,
			RelationshipType relationshipType) {
		super();
		this.source = source;
		this.target = target;
		this.relationshipType = relationshipType;
	}

	/**
	 * Gets the source.
	 *
	 * @return the source
	 */
	public IGenericModelNode getSource() {
		return source;
	}

	/**
	 * Gets the target.
	 *
	 * @return the target
	 */
	public IGenericModelNode getTarget() {
		return target;
	}

	/**
	 * Gets the relationship type.
	 *
	 * @return the relationship type
	 */
	public RelationshipType getRelationshipType() {
		return relationshipType;
	}
	
}
