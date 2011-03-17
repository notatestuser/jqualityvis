/*
 * IGenericModelNodeVisitor.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.program.generic.models;

public interface IGenericModelNodeVisitor {

	public void visit(ProjectModel model);
	public void visit(PackageModel model);
	public void visit(ClassModel model);
	public void visit(MethodModel model);
	public void visit(VariableModel model);
	
}
