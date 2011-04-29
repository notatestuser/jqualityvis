/*
 * MetricPreloader.java (JQualityVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.metrics;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.BoundedRangeModel;

import org.lukep.javavis.program.generic.models.ClassModel;
import org.lukep.javavis.program.generic.models.IGenericModelNodeVisitor;
import org.lukep.javavis.program.generic.models.MethodModel;
import org.lukep.javavis.program.generic.models.PackageModel;
import org.lukep.javavis.program.generic.models.ProjectModel;
import org.lukep.javavis.program.generic.models.Relationship;
import org.lukep.javavis.program.generic.models.VariableModel;

/**
 * Helper class that pre-loads metric measurements to speed up the responsiveness of the user interface. A BoundedRangeModel
 * is taken as a parameter so the status of the process can be shown to the user in real time.
 */
public class MetricPreloader {

	/**
	 * Preload metric measurements.
	 *
	 * @param project the project
	 * @param progressModel the progress model
	 */
	public static void preloadMetricMeasurements(ProjectModel project, final BoundedRangeModel progressModel) {
		
		progressModel.setValueIsAdjusting(true);
		progressModel.setValue(0);
		progressModel.setMinimum(0);
		progressModel.setMaximum(project.getModelCount());
		progressModel.setValueIsAdjusting(false);
		
		Set<String> metricTargetTypes = 
			MetricRegistry.getInstance().getSupportedMetricTargets();
		final Map<String, Collection<MetricAttribute>> targetMetrics = 
			new HashMap<String, Collection<MetricAttribute>>();
		
		for (String metricTargetType : metricTargetTypes)
			targetMetrics.put(metricTargetType, 
					MetricRegistry.getInstance().getSupportedMetrics(metricTargetType));
		
		project.accept(new IGenericModelNodeVisitor() {
			
			private int progress = 0;
			
			private synchronized void measureTarget(IMeasurableNode model) {
				if (!model.isMetricsPreloaded()) {
					// cache metric measurements
					Collection<MetricAttribute> metrics = targetMetrics.get(model.getModelTypeName());
					if (metrics != null) {
						for (MetricAttribute metric : metrics) {
							metric.measureTargetCached(model);
							progressModel.setValue(progress);
						}
					}
					progress++;
					model.setMetricsPreloaded(true);
				}
				
				// visit child elements
				if (model.getChildren() != null)
					for (Relationship child : model.getChildren())
						child.getTarget().accept(this);
			}
			
			@Override
			public void visit(VariableModel model) {
				measureTarget(model);
			}
			
			@Override
			public void visit(MethodModel model) {
				measureTarget(model);
			}
			
			@Override
			public void visit(ClassModel model) {
				measureTarget(model);
			}
			
			@Override
			public void visit(PackageModel model) {
				measureTarget(model);
			}
			
			@Override
			public void visit(ProjectModel model) {
				measureTarget(model);
			}
		});
		
		progressModel.setValue(progressModel.getMaximum());
	}
	
}
