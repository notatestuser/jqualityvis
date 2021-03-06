/*
 * RadarChartView.java (JQualityVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.visualisation.views;

import java.util.Map;

import org.lukep.javavis.metrics.MetricAttribute;
import org.lukep.javavis.program.generic.models.ClassModel;
import org.lukep.javavis.program.generic.models.IGenericModelNode;
import org.lukep.javavis.program.generic.models.ProjectModel;
import org.lukep.javavis.ui.swing.WorkspaceContext;
import org.lukep.javavis.visualisation.visualisers.Openchart2Visualiser;

import com.approximatrix.charting.coordsystem.ClassicCoordSystem;
import com.approximatrix.charting.model.ObjectChartDataModel;
import com.approximatrix.charting.render.RadarChartRenderer;
import com.approximatrix.charting.swing.ExtendedChartPanel;

/**
 * It's a radar chart implementation that uses Openchart2!
 */
public class RadarChartView extends AbstractVisualisationView {

	/* (non-Javadoc)
	 * @see org.lukep.javavis.visualisation.views.AbstractVisualisationView#visit(org.lukep.javavis.visualisation.visualisers.Openchart2Visualiser, org.lukep.javavis.ui.swing.WorkspaceContext)
	 */
	@Override
	public ExtendedChartPanel visit(Openchart2Visualiser visualiser,
			WorkspaceContext wspContext) {

		ProjectModel project = wspContext.getModelStore();
		MetricAttribute metric = wspContext.getMetric();
		
		// get filtered collection of classes
		IGenericModelNode[] subjects = wspContext.getSubjects();
		Map<IGenericModelNode, ClassModel[]> filteredClasses = getFilteredClasses(project, subjects);
		
		// create the chart's data model - single series
		ObjectChartDataModel model = getContainerSeriesObjectChartDataModel(filteredClasses, metric);
		
		// initialise the chart itself
		ExtendedChartPanel chart = new ExtendedChartPanel(model, metric.getName());
		
		// create a coordinate system object and renderer
		ClassicCoordSystem coord = new ClassicCoordSystem(model);
		coord.setPaintAxes(false);
		
		chart.setCoordSystem(coord);
		RadarChartRenderer radarRenderer = new RadarChartRenderer(coord, model);
		chart.addChartRenderer(radarRenderer, 0);
		
		return chart;
	}

}
