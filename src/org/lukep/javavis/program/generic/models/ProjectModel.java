/*
 * ProgramModelStore.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.program.generic.models;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.lukep.javavis.metrics.IMeasurableVisitor;
import org.lukep.javavis.metrics.MetricAttribute;
import org.lukep.javavis.metrics.MetricMeasurement;
import org.lukep.javavis.program.generic.models.Relationship.RelationshipType;
import org.lukep.javavis.ui.IProgramSourceObserver;
import org.lukep.javavis.util.JavaVisConstants;

public class ProjectModel extends AbstractModel implements IProgramSourceObserver {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4060931547733163850L;
	
	private final static Logger log = 
		Logger.getLogger(ProjectModel.class.getSimpleName());
	
	private Date creationDate = new Date();
	private String creationUser = System.getProperty("user.name");
	
	private int modelCount = 0;
	
    private Map<String, ClassModel> classMap;
    private Map<String, PackageModel> packageMap;
    
    ///////////////////////////////////////////////////////

    public ProjectModel() {
    	this("Untitled Project");
    }
    
    public ProjectModel(String name) {
    	super(AbstractModelSourceLang.UNKNOWN, 
    			JavaVisConstants.METRIC_APPLIES_TO_PROJCT);
    	setSimpleName(name);
    	setQualifiedName(name);
    	
    	classMap = Collections.synchronizedMap(new LinkedHashMap<String, ClassModel>());
    	packageMap = Collections.synchronizedMap(new LinkedHashMap<String, PackageModel>());
    }
    
	@Override
	public boolean isRootNode() {
		return true;
	}
    
	///////////////////////////////////////////////////////
	
	@Override
	public void accept(IGenericModelNodeVisitor visitor) {
		visitor.visit(this);
	}
	
	@Override
	public MetricMeasurement accept(MetricAttribute metric, IMeasurableVisitor visitor) {
		return visitor.visit(metric, this);
	}
    
	///////////////////////////////////////////////////////
	
	public Date getCreationDate() {
		return creationDate;
	}
	
    public String getCreationUser() {
		return creationUser;
	}

	public int getModelCount() {
		return modelCount;
    }

	public void setModelCount(int modelCount) {
		this.modelCount = modelCount;
	}
	
	public void incModelCount(int n) {
		this.modelCount += n;
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
    
    ///////////////////////////////////////////////////////
    
    public Map<String, PackageModel> getPackageMap() {
    	return packageMap;
    }
    
    public void addPackage(String packageName, PackageModel pkg) {
    	packageMap.put(packageName, pkg);
    	addChild(pkg, RelationshipType.ENCLOSED_IN);
    }
    
    ///////////////////////////////////////////////////////

	@Override
	public void notifyFindClass(ClassModel clazz) {
		addClass(clazz.getQualifiedName(), clazz);
	}

	@Override
	public void notifyFindMethod(MethodModel method) {
	}

	@Override
	public void notifyRootNodeCount(int rootNodes) {
	}

	@Override
	public void notifyRootNodeProcessing(int rootNode, String name) {
	}
	
	@Override
	public void notifyRootNodesProcessed() {
	}
	
    ///////////////////////////////////////////////////////
	
	@Override
	public String toString() {
		return simpleName;
	}
    
}
