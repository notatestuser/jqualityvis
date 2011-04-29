/*
 * ProjectModel.java (JQualityVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.program.generic.models;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Logger;

import org.lukep.javavis.metrics.IMeasurableVisitor;
import org.lukep.javavis.metrics.MetricAttribute;
import org.lukep.javavis.metrics.MetricMeasurement;
import org.lukep.javavis.metrics.MetricThreshold;
import org.lukep.javavis.program.generic.models.Relationship.RelationshipType;
import org.lukep.javavis.ui.IProgramSourceObserver;
import org.lukep.javavis.util.JavaVisConstants;

/**
 * This is the root node of the entire abstract program model tree - the project. It contains references to all of 
 * the packages and all of the classes within the currently loaded project.
 */
public class ProjectModel extends AbstractModel implements IProgramSourceObserver {
	
	private static final long serialVersionUID = -4060931547733163850L;
	
	private final static Logger log = 
		Logger.getLogger(ProjectModel.class.getSimpleName());
	
	private Date creationDate = new Date();
	private String creationUser = System.getProperty("user.name");
	
	private int modelCount = 0;
	
    private Map<String, ClassModel> classMap;
    private Map<String, PackageModel> packageMap;
    
    private List<MetricThreshold> metricThresholds = new Vector<MetricThreshold>();
    
    ///////////////////////////////////////////////////////

    /**
     * Instantiates a new project model.
     */
    public ProjectModel() {
    	this("Untitled Project");
    }
    
    /**
     * Instantiates a new project model.
     *
     * @param name the name
     */
    public ProjectModel(String name) {
    	super(AbstractModelSourceLang.UNKNOWN, 
    			JavaVisConstants.METRIC_APPLIES_TO_PROJCT);
    	setSimpleName(name);
    	setQualifiedName(name);
    	
    	// initialise class and package maps
    	classMap = Collections.synchronizedMap(new LinkedHashMap<String, ClassModel>());
    	packageMap = Collections.synchronizedMap(new LinkedHashMap<String, PackageModel>());
    }
    
	/* (non-Javadoc)
	 * @see org.lukep.javavis.program.generic.models.AbstractModel#isRootNode()
	 */
	@Override
	public boolean isRootNode() {
		return true;
	}
    
	///////////////////////////////////////////////////////
	
	/* (non-Javadoc)
	 * @see org.lukep.javavis.program.generic.models.IGenericModelNode#accept(org.lukep.javavis.program.generic.models.IGenericModelNodeVisitor)
	 */
	@Override
	public void accept(IGenericModelNodeVisitor visitor) {
		visitor.visit(this);
	}
	
	/* (non-Javadoc)
	 * @see org.lukep.javavis.metrics.IMeasurableNode#accept(org.lukep.javavis.metrics.MetricAttribute, org.lukep.javavis.metrics.IMeasurableVisitor)
	 */
	@Override
	public MetricMeasurement accept(MetricAttribute metric, IMeasurableVisitor visitor) {
		return visitor.visit(metric, this);
	}
    
	///////////////////////////////////////////////////////
	
	/**
	 * Gets the creation date.
	 *
	 * @return the creation date
	 */
	public Date getCreationDate() {
		return creationDate;
	}
	
    /**
     * Sets the creation user.
     *
     * @param creationUser the new creation user
     */
    public void setCreationUser(String creationUser) {
		this.creationUser = creationUser;
	}

	/**
	 * Gets the creation user.
	 *
	 * @return the creation user
	 */
	public String getCreationUser() {
		return creationUser;
	}

	/**
	 * Gets the model count.
	 *
	 * @return the model count
	 */
	public int getModelCount() {
		return modelCount;
    }

	/**
	 * Sets the model count.
	 *
	 * @param modelCount the new model count
	 */
	public void setModelCount(int modelCount) {
		this.modelCount = modelCount;
	}
	
	/**
	 * Increment model count.
	 */
	public void incModelCount(int n) {
		this.modelCount += n;
	}
	
    ///////////////////////////////////////////////////////

    /**
     * Gets the class map.
     *
     * @return the class map
     */
    public Map<String, ClassModel> getClassMap() {
        return classMap;
    }

	/**
	 * Gets the class info.
	 *
	 * @param className the class name
	 * @return the class info
	 */
	public ClassModel getClassInfo(String className) {
        return classMap.get(className);
    }

    /**
     * Adds the class.
     *
     * @param className the class name
     * @param classInfo the class info
     */
    public void addClass(String className, ClassModel classInfo) {
        classMap.put(className, classInfo);
        log.info("Added class: " + className);
    }
    
    /**
     * Lookup class.
     *
     * @param qualifiedName the qualified name
     * @return the class model
     */
    public ClassModel lookupClass(String qualifiedName) {
    	if (classMap.size() > 0 && classMap.containsKey(qualifiedName))
    		return classMap.get(qualifiedName);
    	return null;
    }
    
    ///////////////////////////////////////////////////////
    
    /**
     * Gets the package map.
     *
     * @return the package map
     */
    public Map<String, PackageModel> getPackageMap() {
    	return packageMap;
    }
    
    /**
     * Adds the package.
     *
     * @param packageName the package name
     * @param pkg the pkg
     */
    public void addPackage(String packageName, PackageModel pkg) {
    	packageMap.put(packageName, pkg);
    	addChild(pkg, RelationshipType.ENCLOSED_IN);
    }
    
    ///////////////////////////////////////////////////////
    
	/**
     * Gets the metric thresholds.
     *
     * @return the metric thresholds
     */
    public List<MetricThreshold> getMetricThresholds() {
		return metricThresholds;
	}
    
    ///////////////////////////////////////////////////////
    
	/* (non-Javadoc)
     * @see org.lukep.javavis.ui.IProgramSourceObserver#notifyFindClass(org.lukep.javavis.program.generic.models.ClassModel)
     */
    @Override
	public void notifyFindClass(ClassModel clazz) {
		addClass(clazz.getQualifiedName(), clazz);
	}

	/* (non-Javadoc)
	 * @see org.lukep.javavis.ui.IProgramSourceObserver#notifyFindMethod(org.lukep.javavis.program.generic.models.MethodModel)
	 */
	@Override
	public void notifyFindMethod(MethodModel method) {
	}

	/* (non-Javadoc)
	 * @see org.lukep.javavis.ui.IProgramSourceObserver#notifyRootNodeCount(int)
	 */
	@Override
	public void notifyRootNodeCount(int rootNodes) {
	}

	/* (non-Javadoc)
	 * @see org.lukep.javavis.ui.IProgramSourceObserver#notifyRootNodeProcessing(int, java.lang.String)
	 */
	@Override
	public void notifyRootNodeProcessing(int rootNode, String name) {
	}
	
	/* (non-Javadoc)
	 * @see org.lukep.javavis.ui.IProgramSourceObserver#notifyRootNodesProcessed()
	 */
	@Override
	public void notifyRootNodesProcessed() {
	}
	
    ///////////////////////////////////////////////////////
	
	/* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
	public String toString() {
		return simpleName;
	}
    
}
