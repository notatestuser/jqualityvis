/*
 * ClassModel.java (JQualityVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.program.generic.models;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;

import org.lukep.javavis.metrics.IMeasurableVisitor;
import org.lukep.javavis.metrics.MetricAttribute;
import org.lukep.javavis.metrics.MetricMeasurement;
import org.lukep.javavis.metrics.MetricRegistry;
import org.lukep.javavis.program.generic.models.Relationship.RelationshipType;
import org.lukep.javavis.util.JavaVisConstants;

/**
 * The Class ClassModel.
 */
public class ClassModel extends AbstractModel {
	
	private static final long serialVersionUID = -7311995018796844405L;
	
	protected String superClassName;
	protected int methodCount = 0;
	protected int variableCount = 0;
	protected MethodModel constructorMethod;
	protected LinkedList<ClassAncestor> ancestors = new LinkedList<ClassAncestor>();

	/**
	 * Instantiates a new class model.
	 *
	 * @param lang the lang
	 * @param simpleName the simple name
	 * @param qualifiedName the qualified name
	 */
	public ClassModel(AbstractModelSourceLang lang, String simpleName, String qualifiedName) {
		super(lang, JavaVisConstants.METRIC_APPLIES_TO_CLASS);
		this.simpleName = simpleName;
		this.qualifiedName = qualifiedName;
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
	 * Gets the total number of statements.
	 *
	 * @return the total number of statements
	 */
	public int getTotalNumberOfStatements() {
		int totalStatements = 0;
		MetricMeasurement result;
		
		Vector<MethodModel> methods = getMethods();
		if (methods != null) {
			for (MethodModel method : methods) {
				result = method.getMetricMeasurement(
							MetricRegistry.getInstance().getMetricAttribute(
								JavaVisConstants.METRIC_NUM_OF_STATEMENTS));
				assert(result != null);
				totalStatements += result.getResult();
			}
		}
		
		return totalStatements;
	}
	
	/**
	 * Gets the avg cyclomatic complexity.
	 *
	 * @return the avg cyclomatic complexity
	 */
	public double getAvgCyclomaticComplexity() {
		int count = 0;
		double avgComplexity = 0;
		MetricMeasurement result;
		
		Vector<MethodModel> methods = getMethods();
		if (methods != null) {
			for (MethodModel method : methods) {
					result = method.getMetricMeasurement(
								MetricRegistry.getInstance().getMetricAttribute(
									JavaVisConstants.METRIC_CYCLO_COMPLEX));
					assert(result != null);
					avgComplexity += result.getResult();
					count++;
			}
			if (count > 0)
				avgComplexity /= count;
		}
		
		return avgComplexity;
	}
	
	/**
	 * Gets the max cyclomatic complexity.
	 *
	 * @return the max cyclomatic complexity
	 */
	public double getMaxCyclomaticComplexity() {
		double maxComplexity = 0;
		MetricMeasurement result;
		
		Vector<MethodModel> methods = getMethods();
		if (methods != null) {
			for (MethodModel method : methods) {
					result = method.getMetricMeasurement(
								MetricRegistry.getInstance().getMetricAttribute(
									JavaVisConstants.METRIC_CYCLO_COMPLEX));
					assert(result != null);
					if (result.getResult() > maxComplexity)
						maxComplexity = result.getResult();
			}
		}
		
		return maxComplexity;
	}
	
	///////////////////////////////////////////////////////

	/* (non-Javadoc)
	 * @see org.lukep.javavis.program.generic.models.AbstractModel#getContainerName()
	 */
	@Override
	public String getContainerName() {
		int lastDotIndex = qualifiedName.lastIndexOf('.');
		if (qualifiedName.length() > 0 && lastDotIndex != -1) // class is member of a named package (non-default)
			return qualifiedName.substring(0, lastDotIndex);
		else
			return JavaVisConstants.DEFAULT_PACKAGE_NAME;
	}
	
	///////////////////////////////////////////////////////

	/**
	 * Gets the super class name.
	 *
	 * @return the super class name
	 */
	public String getSuperClassName() {
		return superClassName;
	}

	/**
	 * Sets the super class name.
	 *
	 * @param parentName the new super class name
	 */
	public void setSuperClassName(String parentName) {
		this.superClassName = parentName;
	}
	
	/**
	 * Gets the constructor method.
	 *
	 * @return the constructor method
	 */
	public MethodModel getConstructorMethod() {
		return constructorMethod;
	}

	/**
	 * Sets the constructor method.
	 *
	 * @param constructorMethod the new constructor method
	 */
	public void setConstructorMethod(MethodModel constructorMethod) {
		this.constructorMethod = constructorMethod;
	}

	/**
	 * Adds the ancestor.
	 *
	 * @param ancestor the ancestor
	 */
	public void addAncestor(ClassAncestor ancestor) {
		this.ancestors.addFirst(ancestor);
	}
	
	/**
	 * Gets the ancestors.
	 *
	 * @return the ancestors
	 */
	public Queue<ClassAncestor> getAncestors() {
		return ancestors;
	}
	
	/**
	 * Gets the ancestor count.
	 *
	 * @return the ancestor count
	 */
	public int getAncestorCount() {
		return ancestors.size();
	}
	
	/**
	 * Gets the inherited field count.
	 *
	 * @return the inherited field count
	 */
	public int getInheritedFieldCount() {
		int inheritedFields = 0;
		for (ClassAncestor ancestor : ancestors)
			inheritedFields += ancestor.getDeclaredFields();
		return inheritedFields;
	}
	
	/**
	 * Gets the inherited method count.
	 *
	 * @return the inherited method count
	 */
	public int getInheritedMethodCount() {
		int inheritedMethods = 0;
		for (ClassAncestor ancestor : ancestors)
			inheritedMethods += ancestor.getDeclaredMethods();
		return inheritedMethods;
	}

	/**
	 * Adds the method.
	 *
	 * @param method the method
	 */
	public void addMethod(MethodModel method) {
		addChild(method, RelationshipType.ENCLOSED_IN);
		methodCount++;
	}
	
	/**
	 * Gets the methods.
	 *
	 * @return the methods
	 */
	@SuppressWarnings("serial")
	public Vector<MethodModel> getMethods() {
		if (children != null) {
			Vector<MethodModel> methods = new Vector<MethodModel>(children.size());
			for (Relationship m : children)
				if (m.target instanceof MethodModel)
					methods.add((MethodModel) m.target);
			return methods;
		}
		return new Vector<MethodModel>() { };
	}
	
	/**
	 * Gets the method count.
	 *
	 * @return the method count
	 */
	public int getMethodCount() {
		return methodCount;
	}
	
	/**
	 * Gets the constructorless method count.
	 *
	 * @return the constructorless method count
	 */
	public int getConstructorlessMethodCount() {
		if (constructorMethod != null)
			return methodCount - 1;
		return methodCount;
	}
	
	/**
	 * Adds the variable.
	 *
	 * @param variable the variable
	 */
	public void addVariable(VariableModel variable) {
		addChild(variable, RelationshipType.MEMBER_OF);
		variableCount++;
	}
	
	/**
	 * Gets the variables.
	 *
	 * @return the variables
	 */
	@SuppressWarnings("serial")
	public Vector<VariableModel> getVariables() {
		if (children != null) {
			Vector<VariableModel> variables = new Vector<VariableModel>(children.size());
			for (Relationship m : children)
				if (m.target instanceof VariableModel)
					variables.add((VariableModel) m.target);
			return variables;
		}
		return new Vector<VariableModel>() {};
	}
	
	/**
	 * Gets the variable count.
	 *
	 * @return the variable count
	 */
	public int getVariableCount() {
		return variableCount;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return simpleName;
	}
	
}
