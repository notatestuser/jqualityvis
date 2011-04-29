/*
 * IGenericModelNode.java (JQualityVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.program.generic.models;

import java.io.Serializable;
import java.util.Set;

import org.lukep.javavis.program.generic.models.Relationship.RelationshipType;

/**
 * Represents a language agnostic abstract program entity obtained from the originally parsed object graph.
 * Each model node has a list of Relationships with other models, and one parent in the tree structure.
 */
public interface IGenericModelNode extends Serializable {

	/**
	 * Accept an IGenericModelNodeVisitor.
	 *
	 * @param visitor the visitor to accept
	 */
	public void accept(IGenericModelNodeVisitor visitor);
	
	/**
	 * Gets the parent node of this entity.
	 *
	 * @return the parent node
	 */
	public IGenericModelNode getParent();
	
	/**
	 * Gets the root node of the object graph (should be the ProjectModel).
	 *
	 * @return the root node of the object graph
	 */
	public IGenericModelNode getRootNode();
	
	/**
	 * Checks if this model is in fact the root node.
	 *
	 * @return true, if it is the root node
	 */
	public boolean isRootNode();
	
	/**
	 * Adds the child node in a relationship whose type is specified.
	 *
	 * @param child the child node to add
	 * @param type the type
	 */
	public void addChild(IGenericModelNode child, RelationshipType type);
	
	/**
	 * Gets the children of this node.
	 *
	 * @return the children
	 */
	public Set<Relationship> getChildren();
	
	/**
	 * Gets the child count.
	 *
	 * @return the child count
	 */
	public int getChildCount();
	
	/**
	 * Gets the model type name.
	 *
	 * @return the model type name
	 */
	public String getModelTypeName();
	
	/**
	 * Gets the container name.
	 *
	 * @return the container name
	 */
	public String getContainerName();
	
	/**
	 * Gets the simple name.
	 *
	 * @return the simple name
	 */
	public String getSimpleName();
	
	/**
	 * Gets the qualified name.
	 *
	 * @return the qualified name
	 */
	public String getQualifiedName();
	
	/**
	 * Gets the modifier names.
	 *
	 * @param delimiter the delimiter
	 * @return the modifier names
	 */
	public String getModifierNames(String delimiter);
	
    /**
     * Checks if the 'public' modifier flag is set.
     */
    boolean isPublic();
    
    /**
     * Checks if the 'protected' modifier flag is set.
     */
    boolean isProtected();
    
    /**
     * Checks if the 'private' modifier flag is set.
     */
    boolean isPrivate();
    
    /**
     * Checks if the 'final' modifier flag is set.
     */
    boolean isFinal();
    
    /**
     * Checks if the 'abstract' modifier flag is set.
     */
    boolean isAbstract();
    
    /**
     * Checks if the 'native' modifier flag is set.
     */
    boolean isNative();
    
    /**
     * Checks if the 'static' modifier flag is set.
     */
    boolean isStatic();
    
    /**
     * Sets the 'public' modifier flag.
     */
    void setPublic(boolean publicFlag);
    
    /**
     * Sets the 'protected' modifier flag.
     */
    void setProtected(boolean protectedFlag);
    
    /**
     * Sets the 'private' modifier flag.
     */
    void setPrivate(boolean privateFlag);
    
    /**
     * Sets the 'final' modifier flag.
     */
    void setFinal(boolean finalFlag);
    
    /**
     * Sets the 'abstract' modifier flag.
     */
    void setAbstract(boolean abstractFlag);
    
    /**
     * Sets the 'native' modifier flag.
     */
    void setNative(boolean nativeFlag);
    
    /**
     * Sets the 'static' modifier flag.
     */
    void setStatic(boolean staticFlag);
	
}
