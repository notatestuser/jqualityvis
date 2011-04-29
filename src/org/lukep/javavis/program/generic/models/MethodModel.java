/*
 * MethodModel.java (JQualityVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.program.generic.models;

import java.util.LinkedHashMap;
import java.util.Map;

import org.lukep.javavis.metrics.IMeasurableVisitor;
import org.lukep.javavis.metrics.MetricAttribute;
import org.lukep.javavis.metrics.MetricMeasurement;
import org.lukep.javavis.util.JavaVisConstants;

/**
 * Represents a method in the abstract model graph.
 */
public class MethodModel extends AbstractModel {
	
	private static final long serialVersionUID = -1079184886702387866L;
	private String returnType;
	private Map<String, String> parameters;
	private int statementCount = 0;
	private int independentExecutionPaths = 1; // we always start out with 1 path through a method

	/**
	 * Instantiates a new MethodModel
	 */
	public MethodModel(AbstractModelSourceLang lang, String name) {
		super(lang, JavaVisConstants.METRIC_APPLIES_TO_METHOD);
		setName(name);
	}

	///////////////////////////////////////////////////////
	
	/* (non-Javadoc)
	 * @see org.lukep.javavis.program.generic.models.IGenericModelNode#accept(org.lukep.javavis.program.generic.models.IGenericModelNodeVisitor)
	 */
	@Override
	public void accept(IGenericModelNodeVisitor visitor) {
		visitor.visit(this);
	}
	
	/* (non-Javadoc)
	 * @see org.lukep.javavis.metrics.IMeasurableNode#accept(org.lukep.javavis.metrics.MetricAttribute, org.lukep.javavis.metrics.IMeasurableVisitor)
	 */
	@Override
	public MetricMeasurement accept(MetricAttribute metric, IMeasurableVisitor visitor) {
		return visitor.visit(metric, this);
	}
	
	///////////////////////////////////////////////////////

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return getSimpleName();
	}

	/**
	 * Sets the name.
	 *
	 * @param name the new name
	 */
	public void setName(String name) {
		setSimpleName(name);
		setQualifiedName(name);
	}

	/**
	 * Gets the parent class.
	 *
	 * @return the parent class
	 */
	public ClassModel getParentClass() {
		return (ClassModel) parent;
	}

	/**
	 * Sets the parent class.
	 *
	 * @param parentClass the new parent class
	 */
	public void setParentClass(ClassModel parentClass) {
		parent = parentClass;
	}
	
	/**
	 * Gets the return type.
	 *
	 * @return the return type
	 */
	public String getReturnType() {
		return returnType;
	}

	/**
	 * Sets the return type.
	 *
	 * @param returnType the new return type
	 */
	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}

	/**
	 * Adds a parameter.
	 *
	 * @param paramName the param name
	 * @param paramType the param type
	 */
	public void addParameter(String paramName, String paramType) {
		if (parameters == null)
			parameters = new LinkedHashMap<String, String>();
		parameters.put(paramName, paramType);
	}
	
	/**
	 * Gets the parameters.
	 *
	 * @return the parameters
	 */
	public Map<String, String> getParameters() {
		return parameters;
	}

	/**
	 * Gets the statement count.
	 *
	 * @return the statement count
	 */
	public int getStatementCount() {
		return statementCount;
	}

	/**
	 * Sets the statement count.
	 *
	 * @param statementCount the new statement count
	 */
	public void setStatementCount(int statementCount) {
		this.statementCount = statementCount;
	}
	
	/**
	 * Increments the statement count.
	 *
	 * @param n the n
	 */
	public void incStatementCount(int n) {
		this.statementCount += n;
	}

	/**
	 * Gets the number of independent execution paths.
	 *
	 * @return the independent execution paths
	 */
	public int getIndependentExecutionPaths() {
		return independentExecutionPaths;
	}

	/**
	 * Sets the number of independent execution paths.
	 *
	 * @param independentCodePaths the new independent execution paths
	 */
	public void setIndependentExecutionPaths(int independentCodePaths) {
		this.independentExecutionPaths = independentCodePaths;
	}
	
	/**
	 * Increments the independent execution paths.
	 *
	 * @param n the n
	 */
	public void incIndependentExecutionPaths(int n) {
		this.independentExecutionPaths += n;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "MethodInfo [name=" + simpleName + ", parent=" + parent + "]";
	}
	
}
