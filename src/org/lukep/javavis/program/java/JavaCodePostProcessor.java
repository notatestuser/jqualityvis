/*
 * JavaCodePostProcessor.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.program.java;

import org.lukep.javavis.program.generic.models.ClassAncestor;
import org.lukep.javavis.program.generic.models.ClassModel;
import org.lukep.javavis.program.generic.models.ProjectModel;
import org.lukep.javavis.program.generic.models.VariableModel;

public class JavaCodePostProcessor {

	private static final char     GENERIC_BRACE_CHAR           = '<';
	private static final String   INHERITANCE_CLASS_DEFAULT    = "java.lang.Object";
	private static final String[] INHERITANCE_CLASS_EXCLUSIONS = { "java.lang.Object" };
	
	private ProjectModel projectModel;

	public JavaCodePostProcessor(ProjectModel projectModel) {
		super();
		this.projectModel = projectModel;
	}
	
	public void process() {
		findClassHierarchies();
	}
	
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
