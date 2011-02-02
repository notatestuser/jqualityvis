/*
 * MethodInfo.java (JavaVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.program.generic.models;


public class MethodInfo extends GenericModel {

	class SourceLangDependentAttributes {
		public Object rootStatementBlock;
	}
	
	protected String name;
	protected ClassInfo parentClass;
	protected SourceLangDependentAttributes extraAttributes;

	public MethodInfo(GenericModelSourceLang lang, String name) {
		super(lang);
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
