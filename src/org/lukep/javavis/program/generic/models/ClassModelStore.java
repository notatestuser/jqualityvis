/*
 * GenericClassModelMap.java (JavaVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.program.generic.models;

import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Vector;
import java.util.logging.Logger;

import org.lukep.javavis.ui.IProgramSourceObserver;

public class ClassModelStore extends Observable implements IProgramSourceObserver {
	
	protected final static Logger log = Logger.getLogger("GenericClassModelMap");
	
	// keeps track of all instances of the ClassModelStore (for global class lookups)
	protected final static Vector<ClassModelStore> instances = new Vector<ClassModelStore>();
	
    protected Map<String, ClassInfo> classMap = new HashMap<String, ClassInfo>();

    public ClassModelStore() {
    	instances.add(this);
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
    
    public ClassInfo lookupClass(String qualifiedName) {
    	if (classMap.size() > 0 && classMap.containsKey(qualifiedName))
    		return classMap.get(qualifiedName);
    	return null;
    }
    
    public static ClassInfo lookupClassGlobal(String qualifiedName) {
    	// check all instances of ClassModelStore that we know about
    	if (instances.size() > 0) {
    		ClassInfo clazz = null;
    		for (ClassModelStore cms : instances) {
    			clazz = cms.lookupClass(qualifiedName);
    			if (clazz != null)
    				return clazz;
    		}
    	}
    	return null;
    }

	@Override
	public void notifyFindClass(ClassInfo clazz) {
		addClass(clazz.getQualifiedName(), clazz);
	}

	@Override
	public void notifyFindMethod(MethodInfo method) {
		// TODO Auto-generated method stub
		
	}
    
}
