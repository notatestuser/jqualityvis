/*
 * IGenericModelNodeVisitor.java (JQualityVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.program.generic.models;

/**
 * The Interface IGenericModelNodeVisitor.
 */
public interface IGenericModelNodeVisitor {

	/**
	 * Visit ProjectModel.
	 *
	 * @param model the ProjectModel
	 */
	public void visit(ProjectModel model);
	
	/**
	 * Visit PackageModel.
	 *
	 * @param model the PackageModel
	 */
	public void visit(PackageModel model);
	
	/**
	 * Visit ClassModel.
	 *
	 * @param model the ClassModel
	 */
	public void visit(ClassModel model);
	
	/**
	 * Visit MethodModel.
	 *
	 * @param model the MethodModel
	 */
	public void visit(MethodModel model);
	
	/**
	 * Visit VariableModel.
	 *
	 * @param model the VariableModel
	 */
	public void visit(VariableModel model);
	
}
