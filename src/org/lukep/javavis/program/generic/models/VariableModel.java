/*
 * VariableInfo.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.program.generic.models;

public class VariableModel extends GenericModel {

	protected String name;
	protected String typeName;
	protected ClassModel typeInternalClass;
	protected ClassModel parentClass;
	protected boolean isClassAttribute;
	
	public VariableModel(GenericModelSourceLang lang, String name, String typeName) {
		super(lang);
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

	public ClassModel getTypeInternalClass() {
		return typeInternalClass;
	}

	public void setTypeInternalClass(ClassModel typeInternalClass) {
		this.typeInternalClass = typeInternalClass;
	}

	public ClassModel getParentClass() {
		return parentClass;
	}

	public void setParentClass(ClassModel parentClass) {
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
