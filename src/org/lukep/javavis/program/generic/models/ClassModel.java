/*
 * ClassInfo.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.program.generic.models;

import java.util.Vector;

import org.lukep.javavis.util.JavaVisConstants;

public class ClassModel extends GenericModel {

	protected String simpleName;
	protected String qualifiedName;
	protected MethodModel constructorMethod;
	protected Vector<MethodModel> methods = new Vector<MethodModel>();
	protected Vector<VariableModel> variables = new Vector<VariableModel>();

	public ClassModel(GenericModelSourceLang lang, String simpleName, String qualifiedName) {
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

	public MethodModel getConstructorMethod() {
		return constructorMethod;
	}

	public void setConstructorMethod(MethodModel constructorMethod) {
		this.constructorMethod = constructorMethod;
	}
	
	public void addMethod(MethodModel method) {
		methods.add(method);
	}
	
	public Vector<MethodModel> getMethods() {
		return methods;
	}
	
	public int getMethodCount() {
		return methods.size();
	}
	
	public void addVariable(VariableModel variable) {
		variables.add(variable);
	}
	
	public Vector<VariableModel> getVariables() {
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
