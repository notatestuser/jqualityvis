/*
 * ClassAncestor.java (JQualityVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.program.generic.models;

import java.io.Serializable;

/**
 * An ancestor class of a ClassModel. It doesn't matter where this class is - it could be in the 
 * Java classloader or in the actual project, but we only store information about the declared fields 
 * that it has for metric calculation.
 */
public class ClassAncestor implements Serializable {

	private static final long serialVersionUID = 8368848167487954848L;
	
	/** The name. */
	private String name;
	
	/** The declared fields. */
	private int declaredFields = 0;
	
	/** The declared methods. */
	private int declaredMethods = 0;
	
	/**
	 * Instantiates a new class ancestor.
	 */
	public ClassAncestor() {
		super();
	}
	
	/**
	 * Instantiates a new class ancestor.
	 *
	 * @param name the name
	 */
	public ClassAncestor(String name) {
		super();
		this.name = name;
	}
	
	/**
	 * Instantiates a new class ancestor.
	 *
	 * @param name the name
	 * @param declaredFields the declared fields
	 * @param declaredMethods the declared methods
	 */
	public ClassAncestor(String name, int declaredFields, int declaredMethods) {
		this(name);
		this.declaredFields = declaredFields;
		this.declaredMethods = declaredMethods;
	}
	
	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Sets the name.
	 *
	 * @param name the new name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Gets the declared fields.
	 *
	 * @return the declared fields
	 */
	public int getDeclaredFields() {
		return declaredFields;
	}
	
	/**
	 * Sets the declared fields of the ancestor class.
	 *
	 * @param declaredFields the new declared fields
	 */
	public void setDeclaredFields(int declaredFields) {
		this.declaredFields = declaredFields;
	}
	
	/**
	 * Gets the declared methods of the ancestor class.
	 *
	 * @return the declared methods
	 */
	public int getDeclaredMethods() {
		return declaredMethods;
	}
	
	/**
	 * Sets the declared methods of the ancestor class.
	 *
	 * @param declaredMethods the new declared methods
	 */
	public void setDeclaredMethods(int declaredMethods) {
		this.declaredMethods = declaredMethods;
	}
	
}
