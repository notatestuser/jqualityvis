/*
 * CircleView.java (JQualityVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.visualisation.views;

import java.awt.Color;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.lukep.javavis.metrics.IMeasurableNode;
import org.lukep.javavis.metrics.MetricAttribute;
import org.lukep.javavis.metrics.MetricMeasurement;
import org.lukep.javavis.metrics.MetricMeasurementRelation;
import org.lukep.javavis.program.generic.models.ClassModel;
import org.lukep.javavis.program.generic.models.IGenericModelNode;
import org.lukep.javavis.program.generic.models.ProjectModel;
import org.lukep.javavis.ui.mxgraph.CustomMxCircleLayout;
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
import prefuse.action.layout.CircleLayout;
import prefuse.activity.Activity;
import prefuse.data.Graph;
import prefuse.data.Node;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.LabelRenderer;
import prefuse.util.ColorLib;
import prefuse.visual.VisualItem;

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
 * The Class CircleView.
 */
public class CircleView extends AbstractVisualisationView {

	private static final double MXGRAPH_VERTEX_WIDTH = 150;
	private static final double MXGRAPH_VERTEX_HEIGHT = 50;
	private static final float  MXGRAPH_EDGE_COLOUR_H = 0.65f;
	private static final float  MXGRAPH_EDGE_COLOUR_S = 0.173f;
	private static final float  MXGRAPH_EDGE_COLOUR_B = 1.0f;
	private static final float  MXGRAPH_EDGE_COLOUR_BFLUCT = 0.1f;
	private static final float  MXGRAPH_EDGE_COLOUR_BMIN = 0.3f;
	private static final float  MXGRAPH_EDGE_WIDTH_MAX_PX = 40;
	private static final String MXGRAPH_EDGE_FONTCOLOUR = "#000000";
	
	/* (non-Javadoc)
	 * @see org.lukep.javavis.visualisation.views.AbstractVisualisationView#visit(org.lukep.javavis.visualisation.visualisers.mxGraphVisualiser, org.lukep.javavis.ui.swing.WorkspaceContext, com.mxgraph.swing.mxGraphComponent)
	 */
	@Override
	public void visit(mxGraphVisualiser visualiser,
			WorkspaceContext wspContext, mxGraphComponent graphComponent) {
		
		ProjectModel project = wspContext.getModelStore();
		MetricAttribute metric = wspContext.getMetric();
		
		// get filtered collection of classes
		IGenericModelNode[] subjects = wspContext.getSubjects();
		List<ClassModel> filteredClasses = getAllClassesInFilter(getFilteredClasses(project, subjects));
		
		// start the mxGraph transaction and clear model
		final mxGraph graph = graphComponent.getGraph();
		final mxIGraphModel graphModel = graphComponent.getGraph().getModel();
		graphModel.beginUpdate();
		
		// bind graph layout manager
		mxGraphLayout graphLayout = new CustomMxCircleLayout(graph, 0.2f);
		
		// create node map to associate mxGraph cells with models
		HashMap<IGenericModelNode, mxCell> classCellMap = 
			new HashMap<IGenericModelNode, mxCell>(filteredClasses.size());
		
		// create a set of metric measurements for relation drawing
		Set<MetricMeasurement> metricMeasurements = 
			new HashSet<MetricMeasurement>(filteredClasses.size());
		
		// create class vertices
		mxCell curCell;
		for (ClassModel clazz : filteredClasses) {
			curCell = (mxCell) graph.insertVertex(graph.getDefaultParent(), null, clazz, 20, 20, 
					MXGRAPH_VERTEX_WIDTH, MXGRAPH_VERTEX_HEIGHT);
			classCellMap.put(clazz, curCell);
			metricMeasurements.add(metric.measureTargetCached(clazz));
		}
		
		// create edges between related classes
		for (MetricMeasurement metricMeasurement : metricMeasurements) {
			Set<MetricMeasurementRelation> relations = metricMeasurement.getRelations();
			if (relations != null) {
				
				for (MetricMeasurementRelation r : relations) {
					IGenericModelNode	source = r.getSource(),
										target = r.getTarget();
					if (classCellMap.containsKey(source)
							&& classCellMap.containsKey(target)) {
						
						int edgeColorRGB = Color.getHSBColor(MXGRAPH_EDGE_COLOUR_H, MXGRAPH_EDGE_COLOUR_S, 
								Math.max(MXGRAPH_EDGE_COLOUR_BMIN, MXGRAPH_EDGE_COLOUR_B - 
										(MXGRAPH_EDGE_COLOUR_BFLUCT * r.getWeight()))).getRGB();
						String edgeColorRGBHex = Integer.toHexString(edgeColorRGB & 0x00ffffff);
						
						graph.insertEdge(graph.getDefaultParent(), null, "weight = " + r.getWeight(), 
								classCellMap.get(source), classCellMap.get(target), 
								mxConstants.STYLE_FONTCOLOR + "=" + MXGRAPH_EDGE_FONTCOLOUR + ";" +
								mxConstants.STYLE_ROUNDED + "=" + "true" + ";" +
								mxConstants.STYLE_STROKEWIDTH + "=" +
									Math.min(MXGRAPH_EDGE_WIDTH_MAX_PX, 2 * r.getWeight()) + ";" +
								mxConstants.STYLE_STROKECOLOR + "=" + edgeColorRGBHex);
					}
				}
			}
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
		
		ProjectModel project = wspContext.getModelStore();
		
		// get filtered collection of classes
		IGenericModelNode[] subjects = wspContext.getSubjects();
		List<ClassModel> filteredClasses = getAllClassesInFilter(getFilteredClasses(project, subjects));
		
//		// create node map to associate prefuse nodes with models
//		HashMap<IGenericModelNode, Node> classNodeMap = 
//			new HashMap<IGenericModelNode, Node>(modelStore.getClassMap().size());
//		
//		// create a set of metric measurements for relation drawing
//		Set<MetricMeasurement> metricMeasurements = 
//			new HashSet<MetricMeasurement>(modelStore.getClassMap().size());
		
		// create class nodes
		Node curNode;
		for (IGenericModelNode model : filteredClasses) {
			curNode = graph.addNode();
			curNode.setString("type", model.getModelTypeName());
			curNode.setString("name", model.getSimpleName());
			curNode.set("model", model);
			
//			// add to the classNodeMap
//			classNodeMap.put(model, curNode);
			
			// grab metric measurement if applicable
			if (model instanceof IMeasurableNode
					&& wspContext.getMetric().testAppliesTo(model.getModelTypeName())) {
				MetricMeasurement measurement = 
					wspContext.getMetric().measureTargetCached((IMeasurableNode)model);
//				metricMeasurements.add(measurement);
				curNode.setDouble("metricMeasurement", measurement.getResult());
			}
		}
		
//		// create edges between related classes
//		for (MetricMeasurement metricMeasurement : metricMeasurements) {
//			Set<MetricMeasurementRelation> relations = metricMeasurement.getRelations();
//			if (relations != null) {
//				for (MetricMeasurementRelation r : relations) {
//					IGenericModelNode	source = r.getSource(),
//										target = r.getTarget();
//					if (classNodeMap.containsKey(source)
//							&& classNodeMap.containsKey(target)) {
//						graph.addEdge(classNodeMap.get(source), classNodeMap.get(target));
//					}
//				}
//			}
//		}
		
		// -- 2. the visualisation --------------------------------------------
		
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
        int[] mPalette = ColorLib.getInterpolatedPalette(0xFFDBFFCC, 0xFF473D42);
        
        // TODO replace
        int[] mPalette2 = new int[mPalette.length + 1];
        mPalette2[0] = Color.lightGray.getRGB();
        for (int i = 1; i < mPalette.length; i++)
        	mPalette2[i] = mPalette[i];
        
        // map nominal data values to colors using our provided palette
        DataColorAction fill = new DataColorAction("graph.nodes", "type",
                Constants.NOMINAL, VisualItem.FILLCOLOR, palette);
        DataColorAction fillMetrics = new DataColorAction("graph.nodes", 
        		"metricMeasurement", Constants.NUMERICAL, VisualItem.FILLCOLOR, mPalette);
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
		
		// -- 6. launch the visualisation -------------------------------------
		
		vis.run("colour");
		vis.run("layout");
	}
	
}
