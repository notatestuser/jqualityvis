/*
 * MethodInfo.java (JavaVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.program.generic.models;

import java.io.Serializable;

public class MethodInfo implements Serializable {

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

	@Override
	public String toString() {
		return "MethodInfo [name=" + name + ", parentClass=" + parentClass + "]";
	}
	
}
