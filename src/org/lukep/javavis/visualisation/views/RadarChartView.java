/*
 * RadarChartView.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.visualisation.views;

import java.util.Collection;

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

public class RadarChartView extends AbstractVisualisationView {

	@Override
	public ExtendedChartPanel visit(Openchart2Visualiser visualiser,
			WorkspaceContext wspContext) {

		ProjectModel project = wspContext.getModelStore();
		MetricAttribute metric = wspContext.getMetric();
		
		// get filtered collection of classes
		IGenericModelNode subject = wspContext.getSubject();
		Collection<ClassModel> filteredClasses = getFilteredClasses(project, subject);
		
		// create the chart's data model - single series
		ObjectChartDataModel model = getSingleSeriesObjectChartDataModel(filteredClasses, metric);
		
		// initialise the chart itself
		ExtendedChartPanel chart = new ExtendedChartPanel(model, metric.getName() + " in " + subject.getQualifiedName());
		
		// create a coordinate system object and renderer
		ClassicCoordSystem coord = new ClassicCoordSystem(model);
		coord.setPaintAxes(false);
		
		chart.setCoordSystem(coord);
		RadarChartRenderer radarRenderer = new RadarChartRenderer(coord, model);
		chart.addChartRenderer(radarRenderer, 0);
		
		return chart;
	}

}
