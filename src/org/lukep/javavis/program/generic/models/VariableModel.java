/*
 * VariableModel.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.program.generic.models;

import org.lukep.javavis.metrics.IMeasurableVisitor;
import org.lukep.javavis.metrics.MetricAttribute;
import org.lukep.javavis.metrics.MetricMeasurement;
import org.lukep.javavis.util.JavaVisConstants;

public class VariableModel extends AbstractModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5524831645892374369L;
	
	protected ClassModel typeInternalClass;
	protected boolean isClassAttribute;
	
	public VariableModel(AbstractModelSourceLang lang, String name, String typeName) {
		super(lang, JavaVisConstants.METRIC_APPLIES_TO_VAR);
		setSimpleName(name);
		setQualifiedName(typeName);
	}
	
	///////////////////////////////////////////////////////
	
	@Override
	public void accept(IGenericModelNodeVisitor visitor) {
		visitor.visit(this);
	}
	
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
	}

	public String getTypeName() {
		return getQualifiedName();
	}

	public void setTypeName(String typeName) {
		setQualifiedName(typeName);
	}

	public ClassModel getTypeInternalClass() {
		if (typeInternalClass != null)
			return typeInternalClass;
		
		// scan the project for this class
		IGenericModelNode root = getRootNode();
		if (root instanceof ProjectModel)
			return ((ProjectModel)(root)).lookupClass(getTypeName());
		
		// nope - it's not here!
		return null;
	}

	public void setTypeInternalClass(ClassModel typeInternalClass) {
		this.typeInternalClass = typeInternalClass;
	}

	public ClassModel getParentClass() {
		if (getParent() instanceof ClassModel)
			return (ClassModel) getParent();
		return null;
	}

	public void setParentClass(ClassModel parentClass) {
		setParent(parentClass);
	}

	public boolean isClassAttribute() {
		return isClassAttribute;
	}

	public void setClassAttribute(boolean isClassAttribute) {
		this.isClassAttribute = isClassAttribute;
	}

	@Override
	public String toString() {
		return "VariableModel [typeInternalClass=" + typeInternalClass
				+ ", isClassAttribute=" + isClassAttribute + ", simpleName="
				+ simpleName + ", qualifiedName=" + qualifiedName + ", parent=" + parent + "]";
	}
	
}
