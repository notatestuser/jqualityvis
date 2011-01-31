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
			lastClass.addMethod(method);
			method.setParentClass(lastClass);
		}
		log.info("Added method: " + method.getName());
	}
    
}
