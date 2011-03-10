/*
 * ClassAncestor.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.program.generic.models;

import java.io.Serializable;

public class ClassAncestor implements Serializable {

	private String name;
	private int declaredFields = 0;
	private int declaredMethods = 0;
	
	public ClassAncestor() {
		super();
	}
	
	public ClassAncestor(String name) {
		super();
		this.name = name;
	}
	
	public ClassAncestor(String name, int declaredFields, int declaredMethods) {
		this(name);
		this.declaredFields = declaredFields;
		this.declaredMethods = declaredMethods;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getDeclaredFields() {
		return declaredFields;
	}
	public void setDeclaredFields(int declaredFields) {
		this.declaredFields = declaredFields;
	}
	public int getDeclaredMethods() {
		return declaredMethods;
	}
	public void setDeclaredMethods(int declaredMethods) {
		this.declaredMethods = declaredMethods;
	}
	
}
