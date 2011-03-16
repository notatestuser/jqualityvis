/*
 * MethodModel.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.program.generic.models;

import java.util.LinkedHashMap;
import java.util.Map;

import org.lukep.javavis.metrics.IMeasurableVisitor;
import org.lukep.javavis.metrics.MetricAttribute;
import org.lukep.javavis.metrics.MetricMeasurement;
import org.lukep.javavis.util.JavaVisConstants;

public class MethodModel extends AbstractModel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1079184886702387866L;
	
	private String returnType;
	private Map<String, String> parameters;
	private int statementCount = 0;
	private int independentExecutionPaths = 1; // we always start out with 1 path through a method

	public MethodModel(AbstractModelSourceLang lang, String name) {
		super(lang, JavaVisConstants.METRIC_APPLIES_TO_METHOD);
		setName(name);
	}

	///////////////////////////////////////////////////////
	
	@Override
	public MetricMeasurement accept(MetricAttribute metric, IMeasurableVisitor visitor) {
		return visitor.visit(metric, this);
	}
	
	///////////////////////////////////////////////////////
	
	public String getName() {
		return getSimpleName();
	}

	public void setName(String name) {
		setSimpleName(name);
		setQualifiedName(name);
	}

	public ClassModel getParentClass() {
		return (ClassModel) parent;
	}

	public void setParentClass(ClassModel parentClass) {
		parent = parentClass;
	}
	
	public String getReturnType() {
		return returnType;
	}

	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}

	public void addParameter(String paramName, String paramType) {
		if (parameters == null)
			parameters = new LinkedHashMap<String, String>();
		parameters.put(paramName, paramType);
	}
	
	public Map<String, String> getParameters() {
		return parameters;
	}

	public int getStatementCount() {
		return statementCount;
	}

	public void setStatementCount(int statementCount) {
		this.statementCount = statementCount;
	}
	
	public void incStatementCount(int n) {
		this.statementCount += n;
	}

	public int getIndependentExecutionPaths() {
		return independentExecutionPaths;
	}

	public void setIndependentExecutionPaths(int independentCodePaths) {
		this.independentExecutionPaths = independentCodePaths;
	}
	
	public void incIndependentExecutionPaths(int n) {
		this.independentExecutionPaths += n;
	}

	@Override
	public String toString() {
		return "MethodInfo [name=" + simpleName + ", parent=" + parent + "]";
	}
	
}
