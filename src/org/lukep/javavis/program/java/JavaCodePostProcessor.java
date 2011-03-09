/*
 * JavaCodePostProcessor.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.program.java;

import org.lukep.javavis.program.generic.models.ClassModel;
import org.lukep.javavis.program.generic.models.ProjectModel;
import org.lukep.javavis.program.generic.models.VariableModel;

public class JavaCodePostProcessor {

	private static final char GENERIC_BRACE_CHAR = '<';
	private static final String[] INHERITANCE_CLASS_EXCLUSIONS = { /*"java.lang.Object"*/ };
	
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
			if (superClassName == null)
				continue;
			
			// find this class's inheritance stats
			int[] parentFieldMethodCounts = { 0, 0, 0 };
			findInheritanceCountsRecursive(superClassName, null, parentFieldMethodCounts);
			clazz.setAncestorClassCount(parentFieldMethodCounts[0]);
			clazz.setInheritedFieldCount(parentFieldMethodCounts[1]);
			clazz.setInheritedMethodCount(parentFieldMethodCounts[2]);
			System.out.println(
					"Class " + clazz.getSimpleName() + " has " 
					+ clazz.getAncestorClassCount() + " parents declaring " 
					+ clazz.getInheritedFieldCount() + " fields and " 
					+ clazz.getInheritedMethodCount() + " methods.");
		}
	}
	
	private void findInheritanceCountsRecursive(String superClassName, Class internalSuperClass, 
			int[] parentFieldMethodCounts) {
		
		// check exclusion list
		for (String exc : INHERITANCE_CLASS_EXCLUSIONS)
			if (exc.equals(superClassName))
				return;
		
		parentFieldMethodCounts[0] += 1;
		
		// first we'll look for this class's parent in the project
		ClassModel lookupClass = projectModel.lookupClass(superClassName);
		
		if (lookupClass != null) {
			
			// the parent class exists in the project. easy!
			// accumulate our field and method counts
			for (VariableModel var : lookupClass.getVariables())
				if (var.isClassAttribute())
					parentFieldMethodCounts[1]++;
			parentFieldMethodCounts[2] += lookupClass.getMethodCount();
			
			if (lookupClass.getSuperClassName() != null)
				findInheritanceCountsRecursive(lookupClass.getSuperClassName(), null,
						parentFieldMethodCounts);
			
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
				parentFieldMethodCounts[2] += javaClass.getDeclaredMethods().length;
				parentFieldMethodCounts[1] += javaClass.getDeclaredFields().length;
				
				// find the superclass and recursively visit it
				Class javaSuperClass = javaClass.getSuperclass();
				if (javaSuperClass != null)
					findInheritanceCountsRecursive(javaSuperClass.getName(), 
							javaSuperClass, parentFieldMethodCounts);
				
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
}
