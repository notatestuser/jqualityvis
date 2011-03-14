/*
 * ScatterGraphView.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.visualisation.views;

import org.lukep.javavis.metrics.IMeasurableNode;
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

public class ScatterGraphView extends AbstractVisualisationView {

	@Override
	public void visit(PrefuseVisualiser visualiser,
			WorkspaceContext wspContext, Display display) {

		display.reset();
		
		// -- 1. load the data ------------------------------------------------
		
		ProjectModel modelStore = wspContext.getModelStore();
		
		Table t = new Table(wspContext.getModelStore().getClassMap().size(), 4);
		t.addColumn("type", String.class);
		t.addColumn("name", String.class);
		t.addColumn("model", IGenericModelNode.class);
		t.addColumn("metricMeasurement", float.class);
		
		// create class nodes
		int curNode;
		for (IGenericModelNode model : modelStore.getClassMap().values()) {
			curNode = t.addRow();
			
			// grab metric measurement if applicable
			if (model instanceof IMeasurableNode
					&& wspContext.getMetric().testAppliesTo(model.getModelTypeName())) {
				t.setFloat(curNode, "metricMeasurement", 
						((IMeasurableNode)(model)).getMetricMeasurement(
								wspContext.getMetric()).getResult());
			}
			
			// add other fields
			t.setString(curNode, "type", model.getModelTypeName());
			t.setString(curNode, "name", model.getSimpleName()
					+ "\r\n" + t.getFloat(curNode, "metricMeasurement"));
			t.set(curNode, "model", model);			

		}
		
		// -- 2. the visualisation --------------------------------------------
		
		Visualization m_vis = new Visualization();
		m_vis.add("data", t);
		
        //DefaultRendererFactory rf = new DefaultRendererFactory( new ShapeRenderer(2) );
        //m_vis.setRendererFactory(rf);
		
		LabelRenderer r = new LabelRenderer("name");
		r.setRoundedCorner(8, 8);
		
		m_vis.setRendererFactory(new DefaultRendererFactory(r));
        
        // --------------------------------------------------------------------
        // STEP 2: create actions to process the visual data
        
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
        
        // --------------------------------------------------------------------        
        // STEP 4: launching the visualization
        
        display.setVisualization(m_vis);
        m_vis.run("draw");
		
	}

}
