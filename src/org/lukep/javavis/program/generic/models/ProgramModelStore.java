/*
 * ProgramModelStore.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.program.generic.models;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Logger;

import org.lukep.javavis.metrics.IMeasurableVisitor;
import org.lukep.javavis.metrics.MetricAttribute;
import org.lukep.javavis.metrics.MetricMeasurement;
import org.lukep.javavis.ui.IProgramSourceObserver;
import org.lukep.javavis.util.JavaVisConstants;

public class ProgramModelStore extends AbstractModel implements IProgramSourceObserver {
	
	protected final static Logger log = 
		Logger.getLogger(ProgramModelStore.class.getSimpleName());
	
	// keeps track of all instances of the ClassModelStore (for global class lookups)
	protected final static Vector<ProgramModelStore> instances = new Vector<ProgramModelStore>();
	
    protected Map<String, ClassModel> classMap;
    protected Map<String, PackageModel> packageMap;
    
    ///////////////////////////////////////////////////////

    public ProgramModelStore(String name) {
    	super(AbstractModelSourceLang.UNKNOWN, 
    			JavaVisConstants.METRIC_APPLIES_TO_PROJCT);
    	
    	classMap = Collections.synchronizedMap(new LinkedHashMap<String, ClassModel>());
    	packageMap = Collections.synchronizedMap(new LinkedHashMap<String, PackageModel>());
    	instances.add(this);
    }
    
	@Override
	public boolean isRootNode() {
		return true;
	}
    
	///////////////////////////////////////////////////////

	@Override
	public MetricMeasurement accept(MetricAttribute metric, IMeasurableVisitor visitor) {
		return visitor.visit(metric, this);
	}
    
    ///////////////////////////////////////////////////////

    public Map<String, ClassModel> getClassMap() {
        return classMap;
    }

    public ClassModel getClassInfo(String className) {
        return classMap.get(className);
    }

    public void addClass(String className, ClassModel classInfo) {
        classMap.put(className, classInfo);
        log.info("Added class: " + className);
    }
    
    public ClassModel lookupClass(String qualifiedName) {
    	if (classMap.size() > 0 && classMap.containsKey(qualifiedName))
    		return classMap.get(qualifiedName);
    	return null;
    }
    
    public static ClassModel lookupClassGlobal(String qualifiedName) {
    	// check all instances of ClassModelStore that we know about
    	if (instances.size() > 0) {
    		ClassModel clazz = null;
    		for (ProgramModelStore cms : instances) {
    			clazz = cms.lookupClass(qualifiedName);
    			if (clazz != null)
    				return clazz;
    		}
    	}
    	return null;
    }
    
    ///////////////////////////////////////////////////////
    
    public Map<String, PackageModel> getPackageMap() {
    	return packageMap;
    }
    
    public void addPackage(String packageName, PackageModel pkg) {
    	packageMap.put(packageName, pkg);
    }
    
    ///////////////////////////////////////////////////////

	@Override
	public void notifyFindClass(ClassModel clazz) {
		addClass(clazz.getQualifiedName(), clazz);
	}

	@Override
	public void notifyFindMethod(MethodModel method) {
		// TODO Auto-generated method stub
		
	}
    
}
