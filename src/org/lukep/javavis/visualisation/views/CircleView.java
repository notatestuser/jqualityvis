/*
 * CircleView.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.visualisation.views;

import java.awt.Color;
import java.util.HashMap;

import org.lukep.javavis.metrics.IMeasurableNode;
import org.lukep.javavis.program.generic.models.IGenericModelNode;
import org.lukep.javavis.program.generic.models.ProjectModel;
import org.lukep.javavis.ui.swing.PrefuseWorkspacePane;
import org.lukep.javavis.ui.swing.WorkspaceContext;

import prefuse.Constants;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.RepaintAction;
import prefuse.action.assignment.ColorAction;
import prefuse.action.assignment.DataColorAction;
import prefuse.action.layout.CircleLayout;
import prefuse.activity.Activity;
import prefuse.data.Graph;
import prefuse.data.Node;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.LabelRenderer;
import prefuse.util.ColorLib;
import prefuse.visual.VisualItem;

public class CircleView extends AbstractVisualisationView {

	@Override
	public void visit(PrefuseWorkspacePane workspace, WorkspaceContext wspContext,
			Display display) {
		
		display.reset();
		
		// -- 1. load the data ------------------------------------------------

		Graph graph = new Graph();
		graph.addColumn("type", String.class);
		graph.addColumn("name", String.class);
		graph.addColumn("model", IGenericModelNode.class);
		graph.addColumn("metricMeasurement", float.class);
		
		// create package vertices
		ProjectModel modelStore = wspContext.getModelStore();
		HashMap<IGenericModelNode, Node> parentNodeMap = 
			new HashMap<IGenericModelNode, Node>(
				modelStore.getPackageMap().size() + 10);
		Node curNode;
		
		// create class nodes
		for (IGenericModelNode model : modelStore.getClassMap().values()) {
			curNode = graph.addNode();
			curNode.setString("type", model.getModelTypeName());
			curNode.setString("name", model.getSimpleName());
			curNode.set("model", model);
			
			// link to the parent package
			if (parentNodeMap.containsKey(model.getParent())) {
				graph.addEdge(parentNodeMap.get(model.getParent()), curNode);
			}
			
			// grab metric measurement if applicable
			if (model instanceof IMeasurableNode
					&& wspContext.getMetric().testAppliesTo(model.getModelTypeName())) {
				curNode.setFloat("metricMeasurement", 
						((IMeasurableNode)(model)).getMetricMeasurement(
								wspContext.getMetric()).getResult());
			}
		}
		
		// -- 2. the visualisation --------------------------------------------
		
		// TODO implement a Visualization cache
		Visualization vis = new Visualization();
		vis.add("graph", graph);
		vis.setInteractive("graph.edges", null, false);
		
		// -- 3. the renderers and renderer factory ---------------------------
		
		LabelRenderer r = new LabelRenderer("name");
		r.setRoundedCorner(8, 8);
		
		vis.setRendererFactory(new DefaultRendererFactory(r));
		
		// -- 4. the processing actions ---------------------------------------
		
        // create our nominal color palette
        // pink for classes, baby blue for packages
        int[] palette = ColorLib.getCoolPalette();
        // create a metric palette
        int[] mPalette = ColorLib.getInterpolatedPalette(0xFF91FF80, 0xFFFF8080);
        
        // TODO replace
        int[] mPalette2 = new int[mPalette.length + 1];
        mPalette2[0] = Color.lightGray.getRGB();
        for (int i = 1; i < mPalette.length; i++)
        	mPalette2[i] = mPalette[i];
        
        // map nominal data values to colors using our provided palette
        DataColorAction fill = new DataColorAction("graph.nodes", "type",
                Constants.NOMINAL, VisualItem.FILLCOLOR, palette);
        DataColorAction fillMetrics = new DataColorAction("graph.nodes", 
        		"metricMeasurement", Constants.NUMERICAL, VisualItem.FILLCOLOR, mPalette2);
        // use black for node text
        ColorAction text = new ColorAction("graph.nodes",
                VisualItem.TEXTCOLOR, ColorLib.gray(0));
        // use light grey for edges
        ColorAction edges = new ColorAction("graph.edges",
                VisualItem.STROKECOLOR, ColorLib.gray(200));
        
        // create an action list containing all color assignments
        ActionList colour = new ActionList();
        colour.add(fillMetrics);
        colour.add(text);
        colour.add(edges);
		
        // create an action list with an animated layout
        ActionList layout = new ActionList(Activity.INFINITY);
        //layout.add(new ForceDirectedLayout("graph"));
        layout.add(new CircleLayout("graph"));
        layout.add(new RepaintAction());
        
        // add the actions to the visualisation
        vis.putAction("colour", colour);
        vis.putAction("layout", layout);
		
		// -- 5. the display and interactive controls -------------------------
		
		display.setVisualization(vis);
		
		// -- 6. launch the visualization -------------------------------------
		
		vis.run("colour");
		vis.run("layout");
	}
	
}
