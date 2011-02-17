/*
 * IGenericModelNode.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.program.generic.models;

import java.io.Serializable;
import java.util.Vector;

import org.lukep.javavis.metrics.IMeasurable;
import org.lukep.javavis.program.generic.models.Relationship.RelationshipType;

public interface IGenericModelNode extends Serializable {

	public IGenericModelNode getParent();
	public void setParent(IGenericModelNode parent);
	
	public void addChild(IMeasurable child, RelationshipType type);
	public Vector<Relationship> getChildren();
	public int getChildCount();
	
	public String getSimpleName();
	public void setSimpleName(String simpleName);
	public String getQualifiedName();
	public void setQualifiedName(String qualifiedName);
	public String getContainerName();
	
}
