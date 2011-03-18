/*
 * BarChartView.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.visualisation.views;

import java.awt.Font;
import java.text.DecimalFormat;
import java.util.Map;

import org.lukep.javavis.metrics.MetricAttribute;
import org.lukep.javavis.program.generic.models.ClassModel;
import org.lukep.javavis.program.generic.models.IGenericModelNode;
import org.lukep.javavis.program.generic.models.ProjectModel;
import org.lukep.javavis.ui.swing.WorkspaceContext;
import org.lukep.javavis.visualisation.visualisers.Openchart2Visualiser;

import com.approximatrix.charting.coordsystem.ClassicCoordSystem;
import com.approximatrix.charting.model.ObjectChartDataModel;
import com.approximatrix.charting.render.BarChartRenderer;
import com.approximatrix.charting.swing.ExtendedChartPanel;

public class BarChartView extends AbstractVisualisationView {

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
		
		/*
		AffineTransform fontAT = new AffineTransform();
		Font theFont = coord.getTickFont();
		fontAT.rotate(-0.6f);
		coord.setFont(theFont.deriveFont(fontAT));
		*/
		
		chart.setCoordSystem(coord);
		BarChartRenderer barRenderer = new BarChartRenderer(
				coord, model, new DecimalFormat("####.##"), new Font("Tahoma", Font.PLAIN, 8), 1.0f);
		chart.addChartRenderer(barRenderer, 0);
		
		return chart;
		
	}
	
}
