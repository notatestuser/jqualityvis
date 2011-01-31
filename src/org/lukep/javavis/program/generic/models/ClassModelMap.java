/*
 * GenericClassModelMap.java (JavaVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.program.generic.models;

import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.logging.Logger;

import org.lukep.javavis.ui.IProgramSourceObserver;
import org.lukep.javavis.util.JavaVisConstants;

public class ClassModelMap extends Observable implements IProgramSourceObserver {
	
	protected final static Logger log = Logger.getLogger("GenericClassModelMap");
	
    protected Map<String, ClassInfo> classMap = new HashMap<String, ClassInfo>();
    protected ClassInfo lastClass = null;

    public ClassModelMap() {
    	
    }

    public Map<String, ClassInfo> getClassMap() {
        return classMap;
    }

    public ClassInfo getClassInfo(String className) {
        return classMap.get(className);
    }

    public void addClass(String className, ClassInfo classInfo) {
        classMap.put(className, classInfo);
        log.info("Added class: " + className);
    }

	@Override
	public void notifyFindClass(ClassInfo clazz) {
		addClass(clazz.getQualifiedName(), clazz);
		lastClass = clazz;
	}

	@Override
	public void notifyFindMethod(MethodInfo method) {
		if (lastClass != null) {
			if (method.getName().equals(JavaVisConstants.DEFAULT_CONSTRUCTOR_NAME)) {
				lastClass.setConstructorMethod(method);
				log.info("Added constructor method to " + lastClass.getSimpleName());
			} else {
				lastClass.addMethod(method);
				log.info("Added method to " + lastClass.getSimpleName()
														+ ": " + method.getName());
			}
				
			method.setParentClass(lastClass);
		}
	}
    
}
