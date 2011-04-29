/*
 * ClassHierarchyView.java (JQualityVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.visualisation.views;

import java.awt.Color;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import org.lukep.javavis.metrics.IMeasurableNode;
import org.lukep.javavis.program.generic.models.ClassModel;
import org.lukep.javavis.program.generic.models.IGenericModelNode;
import org.lukep.javavis.program.generic.models.PackageModel;
import org.lukep.javavis.program.generic.models.ProjectModel;
import org.lukep.javavis.program.generic.models.Relationship;
import org.lukep.javavis.program.generic.models.Relationship.RelationshipType;
import org.lukep.javavis.ui.swing.WorkspaceContext;
import org.lukep.javavis.visualisation.visualisers.PrefuseVisualiser;
import org.lukep.javavis.visualisation.visualisers.mxGraphVisualiser;

import prefuse.Constants;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.RepaintAction;
import prefuse.action.assignment.ColorAction;
import prefuse.action.assignment.DataColorAction;
import prefuse.action.layout.graph.ForceDirectedLayout;
import prefuse.activity.Activity;
import prefuse.data.Graph;
import prefuse.data.Node;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.LabelRenderer;
import prefuse.util.ColorLib;
import prefuse.visual.VisualItem;

import com.mxgraph.layout.mxCompactTreeLayout;
import com.mxgraph.layout.mxGraphLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxIGraphModel;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.util.mxMorphing;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource.mxIEventListener;
import com.mxgraph.view.mxGraph;

/**
 * The Class ClassHierarchyView.
 */
public class ClassHierarchyView extends AbstractVisualisationView {

	/* (non-Javadoc)
	 * @see org.lukep.javavis.visualisation.views.AbstractVisualisationView#visit(org.lukep.javavis.visualisation.visualisers.mxGraphVisualiser, org.lukep.javavis.ui.swing.WorkspaceContext, com.mxgraph.swing.mxGraphComponent)
	 */
	@Override
	public void visit(mxGraphVisualiser visualiser, final WorkspaceContext wspContext,
			final mxGraphComponent graphComponent) {
		
		// start the mxGraph transaction and clear model
		final mxGraph graph = graphComponent.getGraph();
		final mxIGraphModel graphModel = graphComponent.getGraph().getModel();
		graphModel.beginUpdate();
		graph.selectAll();
		graph.removeCells();
		
		// bind graph layout manager
		mxGraphLayout graphLayout = new mxCompactTreeLayout(graph);
		
		// set a smaller scale beforehand
		graphComponent.zoomTo(0.6, false);
		
		// create package vertices
		ProjectModel modelStore = wspContext.getModelStore();
		HashMap<IGenericModelNode, mxCell> parentCellMap = 
			new HashMap<IGenericModelNode, mxCell>(
				modelStore.getPackageMap().size() + 10);
		mxCell curCell;
		
		for (PackageModel pkg : modelStore.getPackageMap().values()) {
			
			curCell = (mxCell) graph.insertVertex(graph.getDefaultParent(), 
					pkg.getQualifiedName(), pkg, 250, 100, 150, 30);
			
			IGenericModelNode parentPackage = pkg.getParent();
			if (parentPackage != null
					&& parentPackage instanceof PackageModel
					&& parentCellMap.containsKey(parentPackage)) {
				
				// link to the parent package with an edge
				graph.insertEdge(graph.getDefaultParent(), null, null, 
						parentCellMap.get(parentPackage), curCell, 
						mxConstants.STYLE_ENDARROW + "=" + mxConstants.ARROW_OPEN);
			}
			
			parentCellMap.put(pkg, curCell);
		}
		
		// create class vertices
		for (ClassModel clazz : modelStore.getClassMap().values()) {
			
			curCell = (mxCell) graph.insertVertex(graph.getDefaultParent(), null, clazz, 20, 20, 150, 50);
			
			if (parentCellMap.containsKey(clazz.getParent())) {
				
				// link to the parent package
				graph.insertEdge(graph.getDefaultParent(), null, null, 
						parentCellMap.get(clazz.getParent()), curCell);
			}
			
			if (clazz.getChildCount() > 0)
				parentCellMap.put(clazz, curCell);
		}
		
		// execute the layout and animation
		graphModel.endUpdate();
		graphModel.beginUpdate();
		
		try {
			graphLayout.execute(graph.getDefaultParent());
		} finally {
			mxMorphing morph = new mxMorphing(graphComponent, 20, 1.2, 20);
			
			morph.addListener(mxEvent.DONE, new mxIEventListener() {
				
				@Override
				public void invoke(Object sender, mxEventObject evt) {
					graphModel.endUpdate();
				}
				
			});
			
			morph.startAnimation();
		}
	}

	/* (non-Javadoc)
	 * @see org.lukep.javavis.visualisation.views.AbstractVisualisationView#visit(org.lukep.javavis.visualisation.visualisers.PrefuseVisualiser, org.lukep.javavis.ui.swing.WorkspaceContext, prefuse.Display)
	 */
	@Override
	public void visit(PrefuseVisualiser visualiser, WorkspaceContext wspContext,
			Display display) {
		
		display.reset();
		
		// -- 1. load the data ------------------------------------------------

		Graph graph = new Graph();
		graph.addColumn("type", String.class);
		graph.addColumn("name", String.class);
		graph.addColumn("model", IGenericModelNode.class);
		graph.addColumn("metricMeasurement", double.class);
		
		// create package vertices
		ProjectModel modelStore = wspContext.getModelStore();
		HashMap<IGenericModelNode, Node> parentNodeMap = 
			new HashMap<IGenericModelNode, Node>(
				modelStore.getPackageMap().size() + 10);
		Node curNode;
		
		for (PackageModel pkg : modelStore.getPackageMap().values()) {
			
			// add the package node
			curNode = graph.addNode();
			curNode.setString("type", pkg.getModelTypeName());
			curNode.setString("name", pkg.getSimpleName());
			curNode.set("model", pkg);
			
			// link to the parent with an edge
			IGenericModelNode parentPackage = pkg.getParent();
			if (parentPackage != null
					&& parentPackage instanceof PackageModel
					&& parentNodeMap.containsKey(parentPackage)) {
				
				// link to the parent package with an edge
				graph.addEdge(parentNodeMap.get(parentPackage), curNode);
			}
			
			parentNodeMap.put(pkg, curNode);
		}
		
		// create class and method nodes
		Queue<IGenericModelNode> modelQueue = 
			new LinkedList<IGenericModelNode>(modelStore.getClassMap().values());
		IGenericModelNode model;
		
		while (modelQueue.peek() != null) {
			model = modelQueue.remove();
			
			curNode = graph.addNode();
			curNode.setString("type", model.getModelTypeName());
			curNode.setString("name", model.getSimpleName());
			curNode.set("model", model);
			
			// add children to iterModels
			if (model.getChildCount() > 0) {
				for (Relationship r : model.getChildren())
					if (r.getRelationshipType() == RelationshipType.ENCLOSED_IN)
						modelQueue.add(r.getTarget());
				parentNodeMap.put(model, curNode);
			}
			
			// link to the parent package
			if (parentNodeMap.containsKey(model.getParent())) {
				graph.addEdge(parentNodeMap.get(model.getParent()), curNode);
			}
			
			// grab metric measurement if applicable
			if (model instanceof IMeasurableNode
					&& wspContext.getMetric().testAppliesTo(model.getModelTypeName())) {
				curNode.setDouble("metricMeasurement", 
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
        int[] mPalette = ColorLib.getInterpolatedPalette(0xFF00AA6D, 0xFFFFAA6D);
        
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
        layout.add(new ForceDirectedLayout("graph"));
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
