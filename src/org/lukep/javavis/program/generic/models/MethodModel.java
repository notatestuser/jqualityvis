/*
 * MethodModel.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.program.generic.models;

import java.io.Serializable;

import org.lukep.javavis.metrics.IMeasurableVisitor;
import org.lukep.javavis.metrics.MetricAttribute;
import org.lukep.javavis.metrics.MetricMeasurement;
import org.lukep.javavis.util.JavaVisConstants;

public class MethodModel extends AbstractModel {

	class SourceLangDependentAttributes implements Serializable {
		public Object rootStatementBlock;
	}
	
	protected SourceLangDependentAttributes extraAttributes;

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
