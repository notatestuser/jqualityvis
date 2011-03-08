/*
 * IGenericModelNode.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.program.generic.models;

import java.io.Serializable;
import java.util.Vector;

import org.lukep.javavis.program.generic.models.Relationship.RelationshipType;

public interface IGenericModelNode extends Serializable {

	public IGenericModelNode getParent();
	
	public void addChild(IGenericModelNode child, RelationshipType type);
	public Vector<Relationship> getChildren();
	public int getChildCount();
	
	public String getModelTypeName();
	
	public String getContainerName();
	
	public String getSimpleName();
	
	public String getQualifiedName();
	
    boolean isPublic();
    boolean isProtected();
    boolean isFinal();
    boolean isNative();
    boolean isStatic();
    boolean isPrivate();
    boolean isAbstract();
    
    void setPublic(boolean publicFlag);
    void setProtected(boolean protectedFlag);
    void setFinal(boolean finalFlag);
    void setNative(boolean nativeFlag);
    void setStatic(boolean staticFlag);
    void setPrivate(boolean privateFlag);
    void setAbstract(boolean abstractFlag);
	
}
