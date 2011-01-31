/*
 * GenericClassModel.java (JavaVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.program.generic.models;

import java.util.Vector;

public class ClassInfo {

	protected String simpleName;
	protected String qualifiedName;
	protected Vector<MethodInfo> methods = new Vector<MethodInfo>();

	public ClassInfo(String simpleName, String qualifiedName) {
		super();
		this.simpleName = simpleName;
		this.qualifiedName = qualifiedName;
	}

	public String getSimpleName() {
		return simpleName;
	}

	public void setSimpleName(String simpleName) {
		this.simpleName = simpleName;
	}

	public String getQualifiedName() {
		return qualifiedName;
	}

	public void setQualifiedName(String qualifiedName) {
		this.qualifiedName = qualifiedName;
	}
	
	public void addMethod(MethodInfo method) {
		methods.add(method);
	}
	
}
