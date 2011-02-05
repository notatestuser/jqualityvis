/*
 * ClassInfo.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.program.generic.models;

import java.util.Vector;

import org.lukep.javavis.util.JavaVisConstants;

public class ClassInfo extends GenericModel {

	protected String simpleName;
	protected String qualifiedName;
	protected MethodInfo constructorMethod;
	protected Vector<MethodInfo> methods = new Vector<MethodInfo>();
	protected Vector<VariableInfo> variables = new Vector<VariableInfo>();

	public ClassInfo(GenericModelSourceLang lang, String simpleName, String qualifiedName) {
		super(lang);
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
	
	public String getPackageName() {
		int lastDotIndex = qualifiedName.lastIndexOf('.');
		if (qualifiedName.length() > 0 && lastDotIndex != -1) // class is member of a named package (non-default)
			return qualifiedName.substring(0, lastDotIndex);
		else
			return JavaVisConstants.DEFAULT_PACKAGE_NAME;
	}

	public MethodInfo getConstructorMethod() {
		return constructorMethod;
	}

	public void setConstructorMethod(MethodInfo constructorMethod) {
		this.constructorMethod = constructorMethod;
	}
	
	public void addMethod(MethodInfo method) {
		methods.add(method);
	}
	
	public Vector<MethodInfo> getMethods() {
		return methods;
	}
	
	public int getMethodCount() {
		return methods.size();
	}
	
	public void addVariable(VariableInfo variable) {
		variables.add(variable);
	}
	
	public Vector<VariableInfo> getVariables() {
		return variables;
	}
	
	public int getVariableCount() {
		return variables.size();
	}

	@Override
	public String toString() {
		return simpleName;
	}
	
}
