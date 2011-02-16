/*
 * ClassInfo.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.program.generic.models;

import java.util.Vector;

import org.lukep.javavis.metrics.IMeasurable;
import org.lukep.javavis.metrics.IMeasurableVisitor;
import org.lukep.javavis.metrics.MetricAttribute;
import org.lukep.javavis.metrics.MetricMeasurement;
import org.lukep.javavis.metrics.MetricRegistry;
import org.lukep.javavis.util.JavaVisConstants;

public class ClassModel extends AbstractModel {

	protected String simpleName;
	protected String qualifiedName;
	protected MethodModel constructorMethod;
	protected int methodCount = 0;
	protected int variableCount = 0;

	public ClassModel(AbstractModelSourceLang lang, String simpleName, String qualifiedName) {
		super(lang, JavaVisConstants.METRIC_APPLIES_TO_CLASS);
		this.simpleName = simpleName;
		this.qualifiedName = qualifiedName;
	}
	
	///////////////////////////////////////////////////////
	
	@Override
	public MetricMeasurement accept(MetricAttribute metric, IMeasurableVisitor visitor) {
		return visitor.visit(metric, this);
	}
	
	///////////////////////////////////////////////////////
	
	public int getTotalNumberOfStatements() {
		int totalStatements = 0;
		MetricMeasurement result;
		
		for (MethodModel method : getMethods()) {
			result = method.getMetricMeasurement(
						MetricRegistry.getInstance().getMetricAttribute(
							JavaVisConstants.METRIC_NUM_OF_STATEMENTS));
			totalStatements += result.getResult();
		}
		
		return totalStatements;
	}
	
	public float getAvgCyclomaticComplexity() {
		int count = 0;
		float avgComplexity = 0;
		MetricMeasurement result;
		
		for (MethodModel method : getMethods()) {
				result = method.getMetricMeasurement(
							MetricRegistry.getInstance().getMetricAttribute(
								JavaVisConstants.METRIC_CYCLO_COMPLEX));
				avgComplexity += result.getResult();
				count++;
		}
		if (count > 0)
			avgComplexity /= count;
		
		return avgComplexity;
	}
	
	public float getMaxCyclomaticComplexity() {
		float maxComplexity = 0;
		MetricMeasurement result;
		
		for (MethodModel method : getMethods()) {
				result = method.getMetricMeasurement(
							MetricRegistry.getInstance().getMetricAttribute(
								JavaVisConstants.METRIC_CYCLO_COMPLEX));
				if (result.getResult() > maxComplexity)
					maxComplexity = result.getResult();
		}
		
		return maxComplexity;
	}
	
	///////////////////////////////////////////////////////

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
		addChild(method);
		methodCount++;
	}
	
	public Vector<MethodModel> getMethods() {
		if (children != null) {
			Vector<MethodModel> methods = new Vector<MethodModel>(children.size());
			for (IMeasurable m : children)
				if (m instanceof MethodModel)
					methods.add((MethodModel) m);
			return methods;
		}
		return null;
	}
	
	public int getMethodCount() {
		return methodCount;
	}
	
	public void addVariable(VariableModel variable) {
		addChild(variable);
		variableCount++;
	}
	
	public Vector<VariableModel> getVariables() {
		if (children != null) {
			Vector<VariableModel> variables = new Vector<VariableModel>(children.size());
			for (IMeasurable m : children)
				if (m instanceof VariableModel)
					variables.add((VariableModel) m);
			return variables;
		}
		return null;
	}
	
	public int getVariableCount() {
		return variableCount;
	}

	@Override
	public String toString() {
		return simpleName;
	}
	
}
