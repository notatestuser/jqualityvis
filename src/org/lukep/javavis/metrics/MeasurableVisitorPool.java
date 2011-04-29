/*
 * MeasurableVisitorPool.java (JQualityVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.metrics;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.lukep.javavis.program.generic.factories.GenericModelFactory;

/**
 * Pools instances of IMeasurableVisitor for purposes of optimisation.
 */
public class MeasurableVisitorPool {

	protected final static Logger log = 
		Logger.getLogger(GenericModelFactory.class.getSimpleName());
	
	private static MeasurableVisitorPool instance = null;
	
	private Map<Class<IMeasurableVisitor>, IMeasurableVisitor> visitorInstances;
	
	/**
	 * Instantiates a new MeasurableVisitorPool.
	 */
	private MeasurableVisitorPool() {
		visitorInstances = new HashMap<Class<IMeasurableVisitor>, IMeasurableVisitor>();
	}
	
	/**
	 * Gets the singleton instance.
	 *
	 * @return singleton instance of MeasurableVisitorPool
	 */
	public static MeasurableVisitorPool getInstance() {
		if (instance == null)
			instance = new MeasurableVisitorPool();
		return instance;
	}
	
	/**
	 * Gets an instance pooled visitor - if it doesn't exist it's created and added to the cache.
	 *
	 * @param visitorClass the visitor class
	 * @return the visitor instance
	 */
	public IMeasurableVisitor getPooledVisitor(Class<IMeasurableVisitor> visitorClass) {
		if (visitorInstances.containsKey(visitorClass))
			return visitorInstances.get(visitorClass);
		try {
			IMeasurableVisitor visitor = visitorClass.newInstance();
			visitorInstances.put(visitorClass, visitor);
			log.info("Created pooled instance of " + visitorClass + ": " + visitor);
			return visitor;
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
