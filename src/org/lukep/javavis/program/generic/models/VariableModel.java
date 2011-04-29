/*
 * VariableModel.java (JQualityVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.program.generic.models;

import org.lukep.javavis.metrics.IMeasurableVisitor;
import org.lukep.javavis.metrics.MetricAttribute;
import org.lukep.javavis.metrics.MetricMeasurement;
import org.lukep.javavis.util.JavaVisConstants;

/**
 * This class represents a single variable in the project. It could be of any data type, and class attributes are explicitly 
 * marked with the "isClassAttribute" flag.
 */
public class VariableModel extends AbstractModel {

	private static final long serialVersionUID = 5524831645892374369L;
	
	protected ClassModel typeInternalClass;
	protected boolean isClassAttribute;
	
	/**
	 * Instantiates a new VariableModel.
	 */
	public VariableModel(AbstractModelSourceLang lang, String name, String typeName) {
		super(lang, JavaVisConstants.METRIC_APPLIES_TO_VAR);
		setSimpleName(name);
		setQualifiedName(typeName);
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
	}

	/**
	 * Gets the type name.
	 *
	 * @return the type name
	 */
	public String getTypeName() {
		return getQualifiedName();
	}

	/**
	 * Sets the type name.
	 *
	 * @param typeName the new type name
	 */
	public void setTypeName(String typeName) {
		setQualifiedName(typeName);
	}

	/**
	 * Gets the type of the internal class.
	 *
	 * @return the type of the internal class
	 */
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

	/**
	 * Sets the type of the internal class.
	 *
	 * @param typeInternalClass the new type of the internal class
	 */
	public void setTypeInternalClass(ClassModel typeInternalClass) {
		this.typeInternalClass = typeInternalClass;
	}

	/**
	 * Gets the parent class.
	 *
	 * @return the parent class
	 */
	public ClassModel getParentClass() {
		if (getParent() instanceof ClassModel)
			return (ClassModel) getParent();
		return null;
	}

	/**
	 * Sets the parent class.
	 *
	 * @param parentClass the new parent class
	 */
	public void setParentClass(ClassModel parentClass) {
		setParent(parentClass);
	}

	/**
	 * Checks if this variable is a class attribute.
	 *
	 * @return true, if this variable is a class attribute
	 */
	public boolean isClassAttribute() {
		return isClassAttribute;
	}

	/**
	 * Sets the class attribute flag.
	 *
	 * @param isClassAttribute the new class attribute flag value
	 */
	public void setClassAttribute(boolean isClassAttribute) {
		this.isClassAttribute = isClassAttribute;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "VariableModel [typeInternalClass=" + typeInternalClass
				+ ", isClassAttribute=" + isClassAttribute + ", simpleName="
				+ simpleName + ", qualifiedName=" + qualifiedName + ", parent=" + parent + "]";
	}
	
}
