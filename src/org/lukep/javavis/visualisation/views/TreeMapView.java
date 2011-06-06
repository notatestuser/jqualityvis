/*
 * TreeMapView.java (JQualityVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.visualisation.views;

import java.awt.Font;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Iterator;

import org.lukep.javavis.metrics.IMeasurableNode;
import org.lukep.javavis.metrics.MetricMeasurement;
import org.lukep.javavis.program.generic.models.IGenericModelNode;
import org.lukep.javavis.program.generic.models.PackageModel;
import org.lukep.javavis.program.generic.models.ProjectModel;
import org.lukep.javavis.ui.swing.WorkspaceContext;
import org.lukep.javavis.visualisation.visualisers.PrefuseVisualiser;

import prefuse.Constants;
import prefuse.Display;
import prefuse.action.ActionList;
import prefuse.action.RepaintAction;
import prefuse.action.animate.ColorAnimator;
import prefuse.action.assignment.ColorAction;
import prefuse.action.assignment.DataColorAction;
import prefuse.action.layout.Layout;
import prefuse.action.layout.graph.SquarifiedTreeMapLayout;
import prefuse.data.Node;
import prefuse.data.Schema;
import prefuse.data.expression.Predicate;
import prefuse.data.expression.parser.ExpressionParser;
import prefuse.render.AbstractShapeRenderer;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.LabelRenderer;
import prefuse.util.ColorLib;
import prefuse.util.FontLib;
import prefuse.util.PrefuseLib;
import prefuse.visual.DecoratorItem;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualItem;
import prefuse.visual.expression.InGroupPredicate;

/**
 * A Treemap visualisation.
 */
public class TreeMapView extends AbstractVisualisationView {

	/* (non-Javadoc)
	 * @see org.lukep.javavis.visualisation.views.AbstractVisualisationView#visit(org.lukep.javavis.visualisation.visualisers.PrefuseVisualiser, org.lukep.javavis.ui.swing.WorkspaceContext, prefuse.Display)
	 */
	@Override
	public void visit(PrefuseVisualiser visualiser,
			WorkspaceContext wspContext, Display display) {

		display.reset();
		
		// load up the models

		prefuse.data.Tree t = new prefuse.data.Tree();
		t.addColumn("type", String.class);
		t.addColumn("name", String.class);
		t.addColumn("model", IGenericModelNode.class);
		t.addColumn("metricMeasurement", double.class);
		
		// create package vertices
		ProjectModel modelStore = wspContext.getModelStore();
		HashMap<IGenericModelNode, Node> parentNodeMap = 
			new HashMap<IGenericModelNode, Node>(
				modelStore.getPackageMap().size() + 10);
		Node curNode;
		
		for (PackageModel pkg : modelStore.getPackageMap().values()) {
			
			// add the package node
			curNode = t.addNode();
			curNode.setString("type", pkg.getModelTypeName());
			curNode.setString("name", pkg.getSimpleName());
			curNode.set("model", pkg);
			
			// link to the parent with an edge
			IGenericModelNode parentPackage = pkg.getParent();
			if (parentPackage != null
					&& parentPackage instanceof PackageModel
					&& parentNodeMap.containsKey(parentPackage)) {
				
				// link to the parent package with an edge
				t.addEdge(parentNodeMap.get(parentPackage), curNode);
			}
			
			parentNodeMap.put(pkg, curNode);
		}
		
		// create class and method nodes
		for (IGenericModelNode model : modelStore.getClassMap().values()) {
			curNode = t.addNode();
			
			// grab metric measurement if applicable
			MetricMeasurement measurement = null;
			if (model instanceof IMeasurableNode
					&& wspContext.getMetric().testAppliesTo(model.getModelTypeName())) {
				measurement = ((IMeasurableNode)(model)).getMetricMeasurement(wspContext.getMetric());
				curNode.setDouble("metricMeasurement", measurement.getResult());
			}
			
			curNode.setString("type", model.getModelTypeName());
			curNode.setString("name", model.getSimpleName()
					+ "\r\n" + (measurement != null ? measurement.getRoundedResult(5) : "0.0"));
			curNode.set("model", model);
			
			// add children to iterModels
			if (model.getChildCount() > 0) {
				parentNodeMap.put(model, curNode);
			}
			
			// link to the parent package
			if (parentNodeMap.containsKey(model.getParent())) {
				t.addEdge(parentNodeMap.get(model.getParent()), curNode);
			}
		}
		
		// initialise the visualisation
		
        // add the tree to the visualisation
		prefuse.Visualization vis = new prefuse.Visualization();
        vis.addTree("tree", t);
        vis.setVisible("tree.edges", null, false);
        
        // ensure that only leaf nodes are interactive
        Predicate noLeaf = (Predicate)ExpressionParser.parse("childcount() > 0");
        vis.setInteractive("tree.nodes", noLeaf, false);

        // add labels to the visualisation
        // create a filter to show labels only at top-level nodes
        Predicate labelP = (Predicate)ExpressionParser.parse("[type] == 'package'");
        // now create the labels as decorators of the nodes
        Schema labelSchema = PrefuseLib.getVisualItemSchema();
        labelSchema.setDefault(VisualItem.INTERACTIVE, false);
        labelSchema.setDefault(VisualItem.TEXTCOLOR, ColorLib.gray(30));
        labelSchema.setDefault(VisualItem.FONT, FontLib.getFont("Tahoma", Font.BOLD, 12));
        vis.addDecorators("labels", "tree.nodes", labelP, labelSchema);

        // create a filter to show class-level node labels
        labelP = (Predicate)ExpressionParser.parse("[type] != 'package'");
        // now create the labels as decorators of the nodes
        labelSchema = PrefuseLib.getVisualItemSchema();
        labelSchema.setDefault(VisualItem.INTERACTIVE, false);
        labelSchema.setDefault(VisualItem.TEXTCOLOR, ColorLib.gray(60));
        labelSchema.setDefault(VisualItem.FONT, FontLib.getFont("Tahoma", 7));
        vis.addDecorators("classLabels", "tree.nodes", labelP, labelSchema);
        
        // set up the renderers - one for nodes and one for labels
        DefaultRendererFactory rf = new DefaultRendererFactory();
        rf.add(new InGroupPredicate("tree.nodes"), new NodeRenderer());
        rf.add(new InGroupPredicate("labels"), new LabelRenderer("name"));
        rf.add(new InGroupPredicate("classLabels"), new LabelRenderer("name"));
        vis.setRendererFactory(rf);
        
        // border colours
        final ColorAction borderColor = new BorderColorAction("tree.nodes");
        
        // create a metric palette
        int[] mPalette = ColorLib.getInterpolatedPalette(0xFFDBFFCC, 0xFF473D42);
        int[] mPaletteR = ColorLib.getInterpolatedPalette(0xFF473D42, 0xFFDBFFCC);
        
        DataColorAction fillMetrics = new DataColorAction("tree.nodes", "metricMeasurement", 
        		Constants.NUMERICAL, VisualItem.FILLCOLOR, mPalette);
        
        DataColorAction fillLabels = new DataColorAction("classLabels", "metricMeasurement", 
        		Constants.NUMERICAL, VisualItem.TEXTCOLOR, mPaletteR);
        
        // create an action list containing all color and size assignments
        ActionList colour = new ActionList();
        colour.add(borderColor);
        colour.add(fillMetrics);
        colour.add(fillLabels);
        //colour.add( new Suze("classLabels") );
        
        // animate paint change
        ActionList animatePaint = new ActionList(400);
        animatePaint.add(new ColorAnimator("tree.nodes"));
        animatePaint.add(new RepaintAction());
        vis.putAction("animatePaint", animatePaint);
        
        // create the single filtering and layout action list
        ActionList layout = new ActionList();
        layout.add(new SquarifiedTreeMapLayout("tree"));
        layout.add(new NodeLabelLayout("labels"));
        layout.add(new NodeLabelLayout("classLabels"));
        //layout.add(colors);
        layout.add(new RepaintAction());
        vis.putAction("colour", colour);
        vis.putAction("layout", layout);
        
        /*searchQ = new SearchQueryBinding(vt.getNodeTable(), label);
        m_vis.addFocusGroup(Visualization.SEARCH_ITEMS, searchQ.getSearchSet());
        searchQ.getPredicate().addExpressionListener(new UpdateListener() {
            public void update(Object src) {
                m_vis.cancel("animatePaint");
                m_vis.run("colors");
                m_vis.run("animatePaint");
            }
        });*/
        
        // perform layout
        display.setVisualization(vis);
        vis.run("colour");
        vis.run("layout");
	}
	
    /**
     * Set the stroke color for drawing treemap node outlines. A graded
     * grayscale ramp is used, with higer nodes in the tree drawn in
     * lighter shades of gray.
     */
    public static class BorderColorAction extends ColorAction {
        
        /**
         * Instantiates a new border color action.
         *
         * @param group the group
         */
        public BorderColorAction(String group) {
            super(group, VisualItem.STROKECOLOR);
        }
        
        /* (non-Javadoc)
         * @see prefuse.action.assignment.ColorAction#getColor(prefuse.visual.VisualItem)
         */
        @Override
        public int getColor(VisualItem item) {
            NodeItem nitem = (NodeItem)item;
            if ( nitem.isHover() )
                return ColorLib.rgb(99,130,191);
            
            int depth = nitem.getDepth();
            if ( depth < 2 ) {
                return ColorLib.gray(100);
            } else if ( depth < 4 ) {
                return ColorLib.gray(75);
            } else {
                return ColorLib.gray(50);
            }
        }
    }
    
    /**
     * Set label positions. Labels are assumed to be DecoratorItem instances,
     * decorating their respective nodes. The layout simply gets the bounds
     * of the decorated node and assigns the label coordinates to the center
     * of those bounds.
     */
    public static class NodeLabelLayout extends Layout {
        
        /**
         * Instantiates a new node label layout.
         *
         * @param group the group
         */
        public NodeLabelLayout(String group) {
            super(group);
        }
        
        /* (non-Javadoc)
         * @see prefuse.action.GroupAction#run(double)
         */
        @SuppressWarnings("rawtypes")
		@Override
        public void run(double frac) {
            Iterator iter = m_vis.items(m_group);
            while (iter.hasNext()) {
                DecoratorItem item = (DecoratorItem)iter.next();
                VisualItem node = item.getDecoratedItem();
                Rectangle2D bounds = node.getBounds();
	                setX(item, null, bounds.getCenterX());
	                setY(item, null, bounds.getCenterY());
            }
        }
    }
    
    /**
     * A renderer for treemap nodes. Draws simple rectangles, but defers
     * the bounds management to the layout.
     */
    public static class NodeRenderer extends AbstractShapeRenderer {
        
        /** The m_bounds. */
        private Rectangle2D m_bounds = new Rectangle2D.Double();
        
        /**
         * Instantiates a new node renderer.
         */
        public NodeRenderer() {
            m_manageBounds = false;
        }
        
        /* (non-Javadoc)
         * @see prefuse.render.AbstractShapeRenderer#getRawShape(prefuse.visual.VisualItem)
         */
        protected Shape getRawShape(VisualItem item) {
            m_bounds.setRect(item.getBounds());
            return m_bounds;
        }
    }

}
