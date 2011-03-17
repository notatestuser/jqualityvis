/*
 * PieChartView.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.visualisation.views;

import java.awt.Font;
import java.awt.Rectangle;
import java.util.Collection;

import org.lukep.javavis.metrics.MetricAttribute;
import org.lukep.javavis.program.generic.models.ClassModel;
import org.lukep.javavis.program.generic.models.IGenericModelNode;
import org.lukep.javavis.program.generic.models.ProjectModel;
import org.lukep.javavis.ui.swing.WorkspaceContext;
import org.lukep.javavis.visualisation.visualisers.Openchart2Visualiser;

import com.approximatrix.charting.coordsystem.ClassicCoordSystem;
import com.approximatrix.charting.model.ObjectChartDataModel;
import com.approximatrix.charting.render.PieChartRenderer;
import com.approximatrix.charting.swing.ExtendedChartPanel;

public class PieChartView extends AbstractVisualisationView {

	@Override
	public ExtendedChartPanel visit(Openchart2Visualiser visualiser,
			WorkspaceContext wspContext) {
		
		ProjectModel project = wspContext.getModelStore();
		MetricAttribute metric = wspContext.getMetric();
		
		// get filtered collection of classes
		IGenericModelNode subject = wspContext.getSubject();
		Collection<ClassModel> filteredClasses = getFilteredClasses(project, subject);
		
		// create the chart's data model - multiple series (classes as series)
		ObjectChartDataModel model = getClassSeriesObjectChartDataModel(filteredClasses, metric);
		
		// initialise the chart itself
		ExtendedChartPanel chart = 
			new ExtendedChartPanel(model, metric.getName() + " in " + subject.getSimpleName());
		
		// set up legend
		chart.getLegend().setFont( new Font("Tahoma", Font.PLAIN, 9) );
		chart.getLegend().setColorBox( new Rectangle(5, 5) );
		
		// create a coordinate system object and renderer
		ClassicCoordSystem coord = new ClassicCoordSystem(model);
		coord.setPaintAxes(false);
		
		chart.setCoordSystem(coord);
		PieChartRenderer pieRenderer = new PieChartRenderer(coord, model);
		chart.addChartRenderer(pieRenderer, 0);
		
		return chart;

	}

}
