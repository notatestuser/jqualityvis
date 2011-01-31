/*
 * MethodInfo.java (JavaVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.program.generic.models;

public class MethodInfo {

	protected String name;
	protected ClassInfo parentClass;

	public MethodInfo(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ClassInfo getParentClass() {
		return parentClass;
	}

	public void setParentClass(ClassInfo parentClass) {
		this.parentClass = parentClass;
	}
	
}
