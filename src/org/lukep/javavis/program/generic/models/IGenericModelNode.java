/*
 * IGenericModelNode.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.program.generic.models;

import java.io.Serializable;
import java.util.Set;

import org.lukep.javavis.program.generic.models.Relationship.RelationshipType;

public interface IGenericModelNode extends Serializable {

	public IGenericModelNode getParent();
	public IGenericModelNode getRootNode();
	public boolean isRootNode();
	
	public void addChild(IGenericModelNode child, RelationshipType type);
	public Set<Relationship> getChildren();
	public int getChildCount();
	
	public String getModelTypeName();
	public String getContainerName();
	public String getSimpleName();
	public String getQualifiedName();
	public String getModifierNames(String delimiter);
	
    boolean isPublic();
    boolean isProtected();
    boolean isPrivate();
    boolean isFinal();
    boolean isAbstract();
    boolean isNative();
    boolean isStatic();
    
    void setPublic(boolean publicFlag);
    void setProtected(boolean protectedFlag);
    void setPrivate(boolean privateFlag);
    void setFinal(boolean finalFlag);
    void setAbstract(boolean abstractFlag);
    void setNative(boolean nativeFlag);
    void setStatic(boolean staticFlag);
	
}
