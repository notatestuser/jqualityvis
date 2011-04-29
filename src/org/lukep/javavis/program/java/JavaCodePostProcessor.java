/*
 * JavaCodePostProcessor.java (JQualityVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.program.java;

import org.lukep.javavis.program.generic.models.ClassAncestor;
import org.lukep.javavis.program.generic.models.ClassModel;
import org.lukep.javavis.program.generic.models.ProjectModel;
import org.lukep.javavis.program.generic.models.VariableModel;

/**
 * Runs post-processing actions on the abstract program model tree. This is necessary 
 * because we can't process hierarchies during the compilation process as we don't know about 
 * the classes we haven't processed yet. When this code is executed, we're aware of these objects and
 * are at liberty to calculate information about the inheritance tree.
 */
public class JavaCodePostProcessor {

	private static final char     GENERIC_BRACE_CHAR           = '<';
	private static final String   INHERITANCE_CLASS_DEFAULT    = "java.lang.Object";
	private static final String[] INHERITANCE_CLASS_EXCLUSIONS = { "java.lang.Object" };
	
	private ProjectModel projectModel;

	/**
	 * Instantiates a new JavaCodePostProcessor.
	 *
	 * @param projectModel the project model
	 */
	public JavaCodePostProcessor(ProjectModel projectModel) {
		super();
		this.projectModel = projectModel;
	}
	
	/**
	 * Processes the class hierarchies in the ProjectModel we're working on. Statistics are calculated and 
	 * accumulated in ClassAncestor objects.
	 */
	public void process() {
		findClassHierarchies();
	}
	
	/**
	 * Finds the class hierarchies in the current ProjectModel. We can through each class in the model and 
	 * call the findAncestorsRecursive() function to scan up the tree.
	 */
	private void findClassHierarchies() {
		
		for (ClassModel clazz : projectModel.getClassMap().values()) {
			
			String superClassName = clazz.getSuperClassName();
			
			// default to INHERITANCE_CLASS_DEFAULT name if we don't have a superclass specified
			if (superClassName == null)
				superClassName = INHERITANCE_CLASS_DEFAULT;
			
			// find this class's ancestor information and build it up in model meta-data
			findAncestorsRecursive(superClassName, null, clazz);
			System.out.println(
					"Class " + clazz.getSimpleName() + " has " 
					+ clazz.getAncestorCount() + " parents declaring " 
					+ clazz.getInheritedFieldCount() + " fields and " 
					+ clazz.getInheritedMethodCount() + " methods.");
		}
	}
	
	/**
	 * Scans up a tree of ancestor classes recursively. One of two distinct possibilities can occur here - 
	 * we either find a class that sits in the current project or we have to query Java's class-loader for it.
	 * This method is able to build up statistical data for either types of class.
	 *
	 * @param superClassName the class's superclass that we're looking for
	 * @param internalSuperClass the internal superclass name to use if we're already aware of the type object
	 * @param clazz the ClassModel to dump statistics into
	 */
	@SuppressWarnings("rawtypes")
	private void findAncestorsRecursive(String superClassName, 
			Class internalSuperClass, ClassModel clazz) {
		
		// check exclusion list
		for (String exc : INHERITANCE_CLASS_EXCLUSIONS)
			if (exc.equals(superClassName))
				return;
		
		// create a new ClassAncestor object
		ClassAncestor newAncestor = new ClassAncestor(superClassName != null ? superClassName : "");
		
		// first we'll look for this class's parent in the project
		ClassModel lookupClass = projectModel.lookupClass(superClassName);
		
		if (lookupClass != null) {
			
			// the parent class exists in the project. easy!
			// accumulate our field and method counts
			for (VariableModel var : lookupClass.getVariables())
				if (var.isClassAttribute())
					newAncestor.setDeclaredFields(newAncestor.getDeclaredFields() + 1);
			newAncestor.setDeclaredMethods(
					newAncestor.getDeclaredMethods() + lookupClass.getMethodCount());
			
			String lookupSuperClassName = lookupClass.getSuperClassName();
			if (!superClassName.equals(INHERITANCE_CLASS_DEFAULT))
				findAncestorsRecursive(
						lookupSuperClassName != null ? lookupSuperClassName : 
							INHERITANCE_CLASS_DEFAULT, null, clazz);
			
		} else {
			
			// we're going to have to query the java class-loader to find this one.
			try {
				Class javaClass;
				if (internalSuperClass == null) {
					// if we've got a class string with a generic type specifier
					// (e.g. TreePathScanner<Object,Trees>), strip off the ending part
					int indexOfBrace = superClassName.indexOf(GENERIC_BRACE_CHAR);
					if (indexOfBrace != -1)
						superClassName = superClassName.substring(0, indexOfBrace);
					
					javaClass = Class.forName(superClassName);
				} else {
					javaClass = internalSuperClass;
				}
				//Class javaClass = internalSuperClass == null ? 
				//		Class.forName(superClassName) : internalSuperClass;
				
				// accumulate our field and method counts
				newAncestor.setDeclaredMethods(
						newAncestor.getDeclaredMethods() + javaClass.getDeclaredMethods().length);
				newAncestor.setDeclaredFields(
						newAncestor.getDeclaredFields() + javaClass.getDeclaredFields().length);
				
				// find the superclass and recursively visit it
				Class javaSuperClass = javaClass.getSuperclass();
				if (javaSuperClass != null)
					findAncestorsRecursive(javaSuperClass.getName(), javaSuperClass, clazz);
				
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		clazz.addAncestor(newAncestor);
	}
	
}
