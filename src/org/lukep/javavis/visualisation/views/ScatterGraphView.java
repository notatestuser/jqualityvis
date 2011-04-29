/*
 * ScatterGraphView.java (JQualityVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.visualisation.views;

import org.lukep.javavis.metrics.IMeasurableNode;
import org.lukep.javavis.metrics.MetricMeasurement;
import org.lukep.javavis.program.generic.models.IGenericModelNode;
import org.lukep.javavis.program.generic.models.ProjectModel;
import org.lukep.javavis.ui.swing.WorkspaceContext;
import org.lukep.javavis.visualisation.visualisers.PrefuseVisualiser;

import prefuse.Constants;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.RepaintAction;
import prefuse.action.assignment.DataColorAction;
import prefuse.action.layout.AxisLayout;
import prefuse.data.Table;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.LabelRenderer;
import prefuse.util.ColorLib;
import prefuse.visual.VisualItem;
import prefuse.visual.expression.VisiblePredicate;

/**
 * This is a scatter graph implemented in the Prefuse library.
 */
public class ScatterGraphView extends AbstractVisualisationView {

	/* (non-Javadoc)
	 * @see org.lukep.javavis.visualisation.views.AbstractVisualisationView#visit(org.lukep.javavis.visualisation.visualisers.PrefuseVisualiser, org.lukep.javavis.ui.swing.WorkspaceContext, prefuse.Display)
	 */
	@Override
	public void visit(PrefuseVisualiser visualiser,
			WorkspaceContext wspContext, Display display) {

		display.reset();
		
		// load the data
		
		ProjectModel modelStore = wspContext.getModelStore();
		
		Table t = new Table(wspContext.getModelStore().getClassMap().size(), 4);
		t.addColumn("type", String.class);
		t.addColumn("name", String.class);
		t.addColumn("model", IGenericModelNode.class);
		t.addColumn("metricMeasurement", double.class);
		
		// create class nodes
		int curNode;
		for (IGenericModelNode model : modelStore.getClassMap().values()) {
			curNode = t.addRow();
			
			// grab metric measurement if applicable
			MetricMeasurement measurement = null;
			if (model instanceof IMeasurableNode
					&& wspContext.getMetric().testAppliesTo(model.getModelTypeName())) {
				measurement = ((IMeasurableNode)(model)).getMetricMeasurement(wspContext.getMetric());
				t.setDouble(curNode, "metricMeasurement", measurement.getResult());
			}
			
			// add other fields
			t.setString(curNode, "type", model.getModelTypeName());
			t.setString(curNode, "name", model.getSimpleName()
					+ "\r\n" + (measurement != null ? measurement.getRoundedResult(5) : "0.0"));
			t.set(curNode, "model", model);			

		}
		
		// the visualisation
		
		Visualization m_vis = new Visualization();
		m_vis.add("data", t);
		
        //DefaultRendererFactory rf = new DefaultRendererFactory( new ShapeRenderer(2) );
        //m_vis.setRendererFactory(rf);
		
		LabelRenderer r = new LabelRenderer("name");
		r.setRoundedCorner(8, 8);
		
		m_vis.setRendererFactory(new DefaultRendererFactory(r));
        
        // create actions to process the visual data
        
        // set up the actions
        AxisLayout x_axis = new AxisLayout("data", "metricMeasurement", 
                Constants.X_AXIS, VisiblePredicate.TRUE);
        m_vis.putAction("x", x_axis);
        
        AxisLayout y_axis = new AxisLayout("data", "metricMeasurement", 
                Constants.Y_AXIS, VisiblePredicate.TRUE);
        m_vis.putAction("y", y_axis);
        
        //DataShapeAction shape = new DataShapeAction("data", "name");
        //m_vis.putAction("shape", shape);
        
        int[] mPalette = ColorLib.getInterpolatedPalette(0xFFDBFFCC, 0xFF473D42);
        int[] mPaletteR = ColorLib.getInterpolatedPalette(0xFF473D42, 0xFFDBFFCC);
        
        DataColorAction fillMetrics = new DataColorAction("data", "metricMeasurement", 
        		Constants.NUMERICAL, VisualItem.FILLCOLOR, mPalette);
        DataColorAction strokeMetrics = new DataColorAction("data", "metricMeasurement", 
        		Constants.NUMERICAL, VisualItem.STROKECOLOR, mPaletteR);
        DataColorAction fillLabels = new DataColorAction("data", "metricMeasurement", 
        		Constants.NUMERICAL, VisualItem.TEXTCOLOR, mPaletteR);
        
        ActionList draw = new ActionList();
        draw.add(x_axis);
        draw.add(y_axis);
        //draw.add(shape);
        draw.add(strokeMetrics);
        draw.add(fillMetrics);
        draw.add(fillLabels);
        //draw.add(sizeAction);
        draw.add(new RepaintAction());
        m_vis.putAction("draw", draw);
        
        // launching the visualisation
        
        display.setVisualization(m_vis);
        m_vis.run("draw");
		
	}

}
