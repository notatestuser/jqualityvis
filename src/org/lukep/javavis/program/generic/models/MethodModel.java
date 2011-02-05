/*
 * MethodInfo.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.program.generic.models;

import java.io.Serializable;

import org.lukep.javavis.metrics.IMeasurableVisitor;
import org.lukep.javavis.metrics.MetricAttribute;
import org.lukep.javavis.metrics.MetricMeasurement;
import org.lukep.javavis.util.JavaVisConstants;

public class MethodModel extends GenericModel {

	class SourceLangDependentAttributes implements Serializable {
		public Object rootStatementBlock;
	}
	
	protected String name;
	protected ClassModel parentClass;
	protected SourceLangDependentAttributes extraAttributes;

	public MethodModel(GenericModelSourceLang lang, String name) {
		super(lang, JavaVisConstants.METRIC_APPLIES_TO_METHOD);
		this.name = name;
		extraAttributes = new SourceLangDependentAttributes();
	}

	///////////////////////////////////////////////////////
	
	@Override
	public MetricMeasurement accept(MetricAttribute metric, IMeasurableVisitor visitor) {
		return visitor.visit(metric, this);
	}
	
	///////////////////////////////////////////////////////
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ClassModel getParentClass() {
		return parentClass;
	}

	public void setParentClass(ClassModel parentClass) {
		this.parentClass = parentClass;
	}
	
	public Object getRootStatementBlock() {
		return extraAttributes.rootStatementBlock;
	}
	
	public void setRootStatementBlock(Object rootStatementBlock) {
		extraAttributes.rootStatementBlock = rootStatementBlock;
	}

	@Override
	public String toString() {
		return "MethodInfo [name=" + name + ", parentClass=" + parentClass + "]";
	}
	
}
