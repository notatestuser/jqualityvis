/*
 * VariableInfo.java (JavaVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.program.generic.models;

import java.io.Serializable;

public class VariableInfo implements Serializable {

	protected String name;
	protected String typeName;
	protected ClassInfo typeInternalClass;
	protected ClassInfo parentClass;
	protected boolean isClassAttribute;
	
	public VariableInfo(String name, String typeName) {
		super();
		this.name = name;
		this.typeName = typeName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public ClassInfo getTypeInternalClass() {
		return typeInternalClass;
	}

	public void setTypeInternalClass(ClassInfo typeInternalClass) {
		this.typeInternalClass = typeInternalClass;
	}

	public ClassInfo getParentClass() {
		return parentClass;
	}

	public void setParentClass(ClassInfo parentClass) {
		this.parentClass = parentClass;
	}

	public boolean isClassAttribute() {
		return isClassAttribute;
	}

	public void setClassAttribute(boolean isClassAttribute) {
		this.isClassAttribute = isClassAttribute;
	}

	@Override
	public String toString() {
		return "VariableInfo [name=" + name + ", typeName=" + typeName
				+ ", typeInternalClass=" + typeInternalClass + ", parentClass="
				+ parentClass + ", isClassAttribute=" + isClassAttribute + "]";
	}
	
}
