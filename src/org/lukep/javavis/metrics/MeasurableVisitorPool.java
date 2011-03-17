/*
 * MeasurableVisitorPool.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.metrics;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.lukep.javavis.program.generic.factories.GenericModelFactory;

public class MeasurableVisitorPool {

	protected final static Logger log = 
		Logger.getLogger(GenericModelFactory.class.getSimpleName());
	
	private static MeasurableVisitorPool instance = null;
	
	private Map<Class<IMeasurableVisitor>, IMeasurableVisitor> visitorInstances;
	
	private MeasurableVisitorPool() {
		visitorInstances = new HashMap<Class<IMeasurableVisitor>, IMeasurableVisitor>();
	}
	
	public static MeasurableVisitorPool getInstance() {
		if (instance == null)
			instance = new MeasurableVisitorPool();
		return instance;
	}
	
	public IMeasurableVisitor getPooledVisitor(Class<IMeasurableVisitor> visitorClass) {
		if (visitorInstances.containsKey(visitorClass))
			return visitorInstances.get(visitorClass);
		try {
			IMeasurableVisitor visitor = visitorClass.newInstance();
			visitorInstances.put(visitorClass, visitor);
			log.info("Created pooled instance of " + visitorClass + ": " + visitor);
			return visitor;
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
}
