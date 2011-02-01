/*
 * GenericClassModel.java (JavaVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.program.generic.models;

import java.io.Serializable;
import java.util.Vector;

public class ClassInfo implements Serializable {

	protected String simpleName;
	protected String qualifiedName;
	protected MethodInfo constructorMethod;
	protected Vector<MethodInfo> methods = new Vector<MethodInfo>();
	protected Vector<VariableInfo> variables = new Vector<VariableInfo>();

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

	public MethodInfo getConstructorMethod() {
		return constructorMethod;
	}

	public void setConstructorMethod(MethodInfo constructorMethod) {
		this.constructorMethod = constructorMethod;
	}
	
	public void addMethod(MethodInfo method) {
		methods.add(method);
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
