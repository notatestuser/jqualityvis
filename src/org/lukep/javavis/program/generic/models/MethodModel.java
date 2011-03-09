/*
 * MethodModel.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.program.generic.models;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import org.lukep.javavis.metrics.IMeasurableVisitor;
import org.lukep.javavis.metrics.MetricAttribute;
import org.lukep.javavis.metrics.MetricMeasurement;
import org.lukep.javavis.util.JavaVisConstants;

public class MethodModel extends AbstractModel {

	class SourceLangDependentAttributes implements Serializable {
		public Object rootStatementBlock;
	}
	
	private String returnType;
	private Map<String, String> parameters;
	private SourceLangDependentAttributes extraAttributes;

	public MethodModel(AbstractModelSourceLang lang, String name) {
		super(lang, JavaVisConstants.METRIC_APPLIES_TO_METHOD);
		extraAttributes = new SourceLangDependentAttributes();
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

	public Object getRootStatementBlock() {
		return extraAttributes.rootStatementBlock;
	}
	
	public void setRootStatementBlock(Object rootStatementBlock) {
		extraAttributes.rootStatementBlock = rootStatementBlock;
	}

	@Override
	public String toString() {
		return "MethodInfo [name=" + simpleName + ", parent=" + parent + "]";
	}
	
}
