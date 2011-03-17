/*
 * AbstractVisualisationView.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.visualisation.views;

import java.util.Collection;
import java.util.Vector;

import org.lukep.javavis.metrics.MetricAttribute;
import org.lukep.javavis.program.generic.models.ClassModel;
import org.lukep.javavis.program.generic.models.IGenericModelNode;
import org.lukep.javavis.program.generic.models.ProjectModel;
import org.lukep.javavis.ui.swing.WorkspaceContext;
import org.lukep.javavis.visualisation.visualisers.Openchart2Visualiser;
import org.lukep.javavis.visualisation.visualisers.PrefuseVisualiser;
import org.lukep.javavis.visualisation.visualisers.mxGraphVisualiser;

import prefuse.Display;

import com.approximatrix.charting.model.ObjectChartDataModel;
import com.approximatrix.charting.swing.ExtendedChartPanel;
import com.mxgraph.swing.mxGraphComponent;

public class AbstractVisualisationView implements IVisualiserVisitor {

	private static final String UNSUPPORTED_VISUALISATION_EXCEPTION = 
		"This Visualisation is unsupported by the current Workspace.";
	
	@Override
	public void visit(mxGraphVisualiser visualiser, WorkspaceContext wspContext, 
			mxGraphComponent graphComponent) {
		
		// overridden in sub-class
		throw new UnsupportedOperationException(UNSUPPORTED_VISUALISATION_EXCEPTION);
	}

	@Override
	public void visit(PrefuseVisualiser visualiser, WorkspaceContext wspContext, 
			Display display) {
		
		// overridden in sub-class
		throw new UnsupportedOperationException(UNSUPPORTED_VISUALISATION_EXCEPTION);
	}

	@Override
	public ExtendedChartPanel visit(Openchart2Visualiser visualiser, WorkspaceContext wspContext) {
		
		// overridden in sub-class
		throw new UnsupportedOperationException(UNSUPPORTED_VISUALISATION_EXCEPTION);
	}
	
	protected static Collection<ClassModel> getFilteredClasses(ProjectModel project, 
			IGenericModelNode subject) {
		
		// create filtered collection of classes
		Collection<ClassModel> filteredClasses = null;
		if (subject instanceof ProjectModel) {
			filteredClasses = project.getClassMap().values();
		} else {
			filteredClasses = new Vector<ClassModel>();
			String containerFilter = subject.getQualifiedName();
			for (ClassModel model : project.getClassMap().values()) {
				if (model.getContainerName().equals(containerFilter))
					filteredClasses.add(model);
			}
		}
		return filteredClasses;
	}
	
	protected static ObjectChartDataModel getSingleSeriesObjectChartDataModel(
			Collection<ClassModel> classes, MetricAttribute metric) {
		
		// create the chart's data model - single series
		int classCount = classes.size();
		Object[] columns = new Object[classCount];
		double[][] data = new double[1][classCount];
		
		int i = 0;
		for (ClassModel clazz : classes) {
			// add the ClassModel object as a column
			columns[i] = clazz;
			
			// add the metric measurement as a data value in series 0
			data[0][i] = metric.measureTargetCached(clazz).getResult();
			
			i++;
		}
		
		return new ObjectChartDataModel(data, columns, new String[] { metric.getInternalName() });
	}
	
	protected static ObjectChartDataModel getClassSeriesObjectChartDataModel(
			Collection<ClassModel> classes, MetricAttribute metric) {
		
		// create the chart's data model - multiple series
		int classCount = classes.size();
		Object[] columns = new Object[classCount];
		double[][] data = new double[classCount][1];
		String[] series = new String[classCount];
		
		int i = 0;
		for (ClassModel clazz : classes) {
			// add the ClassModel object as a column
			columns[i] = clazz;
			series[i] = clazz.getSimpleName();
			
			// add the metric measurement as a data value in series 0
			data[i][0] = metric.measureTargetCached(clazz).getResult();
			
			i++;
		}
		
		return new ObjectChartDataModel(data, columns, series);
	}

}
