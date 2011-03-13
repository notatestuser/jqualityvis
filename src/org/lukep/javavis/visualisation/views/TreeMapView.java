/*
 * TreeMapView.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.visualisation.views;

import infovis.Tree;
import infovis.Visualization;
import infovis.column.FloatColumn;
import infovis.column.IntColumn;
import infovis.column.ObjectColumn;
import infovis.column.StringColumn;
import infovis.tree.DefaultTree;
import infovis.tree.visualization.TreemapVisualization;

import java.awt.Font;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Iterator;

import org.lukep.javavis.metrics.IMeasurableNode;
import org.lukep.javavis.program.generic.models.ClassModel;
import org.lukep.javavis.program.generic.models.IGenericModelNode;
import org.lukep.javavis.program.generic.models.PackageModel;
import org.lukep.javavis.program.generic.models.ProjectModel;
import org.lukep.javavis.ui.swing.InfoVisWorkspacePane;
import org.lukep.javavis.ui.swing.PrefuseWorkspacePane;
import org.lukep.javavis.ui.swing.WorkspaceContext;

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
import prefuse.util.ColorMap;
import prefuse.util.FontLib;
import prefuse.util.PrefuseLib;
import prefuse.visual.DecoratorItem;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualItem;
import prefuse.visual.expression.InGroupPredicate;

public class TreeMapView extends AbstractVisualisationView {

	@Override
	public void visit(PrefuseWorkspacePane workspace,
			WorkspaceContext wspContext, Display display) {

		display.reset();
		
		// -- 1. load the data ------------------------------------------------

		prefuse.data.Tree t = new prefuse.data.Tree();
		t.addColumn("type", String.class);
		t.addColumn("name", String.class);
		t.addColumn("model", IGenericModelNode.class);
		t.addColumn("metricMeasurement", float.class);
		
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
			if (model instanceof IMeasurableNode
					&& wspContext.getMetric().testAppliesTo(model.getModelTypeName())) {
				curNode.setFloat("metricMeasurement", 
						((IMeasurableNode)(model)).getMetricMeasurement(
								wspContext.getMetric()).getResult());
			}
			
			curNode.setString("type", model.getModelTypeName());
			curNode.setString("name", model.getSimpleName()
					+ "\r\n" + curNode.getFloat("metricMeasurement"));
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
		
		// -- 2. the visualisation --------------------------------------------
		
        // add the tree to the visualisation
		prefuse.Visualization m_vis = new prefuse.Visualization();
        m_vis.addTree("tree", t);
        m_vis.setVisible("tree.edges", null, false);
        
        // ensure that only leaf nodes are interactive
        Predicate noLeaf = (Predicate)ExpressionParser.parse("childcount() > 0");
        m_vis.setInteractive("tree.nodes", noLeaf, false);

        // add labels to the visualisation
        // create a filter to show labels only at top-level nodes
        Predicate labelP = (Predicate)ExpressionParser.parse("[type] == 'package'");
        // now create the labels as decorators of the nodes
        Schema labelSchema = PrefuseLib.getVisualItemSchema();
        labelSchema.setDefault(VisualItem.INTERACTIVE, false);
        labelSchema.setDefault(VisualItem.TEXTCOLOR, ColorLib.gray(30));
        labelSchema.setDefault(VisualItem.FONT, FontLib.getFont("Tahoma", Font.BOLD, 12));
        m_vis.addDecorators("labels", "tree.nodes", labelP, labelSchema);

        // create a filter to show class-level node labels
        labelP = (Predicate)ExpressionParser.parse("[type] != 'package'");
        // now create the labels as decorators of the nodes
        labelSchema = PrefuseLib.getVisualItemSchema();
        labelSchema.setDefault(VisualItem.INTERACTIVE, false);
        labelSchema.setDefault(VisualItem.TEXTCOLOR, ColorLib.gray(60));
        labelSchema.setDefault(VisualItem.FONT, FontLib.getFont("Tahoma", 7));
        m_vis.addDecorators("classLabels", "tree.nodes", labelP, labelSchema);
        
        // set up the renderers - one for nodes and one for labels
        DefaultRendererFactory rf = new DefaultRendererFactory();
        rf.add(new InGroupPredicate("tree.nodes"), new NodeRenderer());
        rf.add(new InGroupPredicate("labels"), new LabelRenderer("name"));
        rf.add(new InGroupPredicate("classLabels"), new LabelRenderer("name"));
        m_vis.setRendererFactory(rf);
        
        // border colours
        final ColorAction borderColor = new BorderColorAction("tree.nodes");
        //borderColor.setDefaultColor(0xFF000000);
        //final ColorAction fillColor = new FillColorAction("tree.nodes");
        
        // colour settings
        //ActionList colors = new ActionList();
        //colors.add(fillColor);
        //colors.add(borderColor);
        //m_vis.putAction("colors", colors);
        
        // create our nominal color palette
        // pink for classes, baby blue for packages
        //int[] palette = ColorLib.getCoolPalette();
        // create a metric palette
        int[] mPalette = ColorLib.getInterpolatedPalette(0xFFDBFFCC, 0xFF473D42);
        int[] mPaletteR = ColorLib.getInterpolatedPalette(0xFF473D42, 0xFFDBFFCC);
        
        // TODO replace
        //int[] mPalette2 = new int[mPalette.length + 1];
        //mPalette2[0] = Color.lightGray.getRGB();
        //for (int i = 1; i < mPalette.length; i++)
        //	mPalette2[i] = mPalette[i];
        
        // map nominal data values to colours using our provided palette
        //DataColorAction fill = new DataColorAction("tree.nodes", "type",
        // Constants.NOMINAL, VisualItem.FILLCOLOR, palette);
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
        m_vis.putAction("animatePaint", animatePaint);
        
        // create the single filtering and layout action list
        ActionList layout = new ActionList();
        layout.add(new SquarifiedTreeMapLayout("tree"));
        layout.add(new NodeLabelLayout("labels"));
        layout.add(new NodeLabelLayout("classLabels"));
        //layout.add(colors);
        layout.add(new RepaintAction());
        m_vis.putAction("colour", colour);
        m_vis.putAction("layout", layout);
        
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
        display.setVisualization(m_vis);
        m_vis.run("colour");
        m_vis.run("layout");
	}
	
    // ------------------------------------------------------------------------
    
	public static class Suze extends ColorAction {
		
		public Suze(String group) {
			super(group, VisualItem.TEXTCOLOR);
		}

		@Override
		public int getColor(VisualItem item) {

			if (item instanceof DecoratorItem)
				item = ((DecoratorItem)(item)).getDecoratedItem();
			int rgb = item.getEndFillColor();
			//float hsb[] = Color.RGBtoHSB(ColorLib.red(rgb), ColorLib.green(rgb), ColorLib.blue(rgb), null);
			//float hue = hsb[0] + 0.5f;
			//if (hue > 1f)
			//	hue -= 1;
			//return ColorLib.hsba(hue, hsb[1], hsb[2], 1.0f);
			return ColorLib.rgb(0xFF - ColorLib.red(rgb), 0xFF - ColorLib.green(rgb), 0xFF - ColorLib.blue(rgb));
		}
		
	}
	
    /**
     * Set the stroke color for drawing treemap node outlines. A graded
     * grayscale ramp is used, with higer nodes in the tree drawn in
     * lighter shades of gray.
     */
    public static class BorderColorAction extends ColorAction {
        
        public BorderColorAction(String group) {
            super(group, VisualItem.STROKECOLOR);
        }
        
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
     * Set fill colors for treemap nodes. Search items are colored
     * in pink, while normal nodes are shaded according to their
     * depth in the tree.
     */
    public static class FillColorAction extends ColorAction {
        private ColorMap cmap = new ColorMap(
            ColorLib.getInterpolatedPalette(10,
                ColorLib.rgb(85,85,85), ColorLib.rgb(0,0,0)), 0, 9);

        public FillColorAction(String group) {
            super(group, VisualItem.FILLCOLOR);
        }
        
        @Override
        public int getColor(VisualItem item) {
            if ( item instanceof NodeItem ) {
                NodeItem nitem = (NodeItem)item;
                if ( nitem.getChildCount() > 0 ) {
                    return 0; // no fill for parent nodes
                } else {
                    if ( m_vis.isInGroup(item, prefuse.Visualization.SEARCH_ITEMS) )
                        return ColorLib.rgb(191,99,130);
                    else
                        return cmap.getColor(nitem.getDepth());
                }
            } else {
                return cmap.getColor(0);
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
        public NodeLabelLayout(String group) {
            super(group);
        }
        
        @Override
        public void run(double frac) {
            Iterator iter = m_vis.items(m_group);
            while (iter.hasNext()) {
                DecoratorItem item = (DecoratorItem)iter.next();
                VisualItem node = item.getDecoratedItem();
                Rectangle2D bounds = node.getBounds();
                //if (item.getString("type").equals("package")) {
	                setX(item, null, bounds.getCenterX());
	                setY(item, null, bounds.getCenterY());
                //} else {
	            //    setX(item, null, bounds.getX());
	            //    setY(item, null, bounds.getY());
                //}
            }
        }
    }
    
    /**
     * A renderer for treemap nodes. Draws simple rectangles, but defers
     * the bounds management to the layout.
     */
    public static class NodeRenderer extends AbstractShapeRenderer {
        private Rectangle2D m_bounds = new Rectangle2D.Double();
        
        public NodeRenderer() {
            m_manageBounds = false;
        }
        protected Shape getRawShape(VisualItem item) {
            m_bounds.setRect(item.getBounds());
            return m_bounds;
        }
    }
    
    // ------------------------------------------------------------------------

	@Override
	public void visit(InfoVisWorkspacePane workspace,
			WorkspaceContext wspContext) {

		DefaultTree t = new DefaultTree();
		StringColumn name = new StringColumn("name");
		t.addColumn(name);
		IntColumn size = new IntColumn("size");
		t.addColumn(size);
		ObjectColumn model = new ObjectColumn("model");
		t.addColumn(model);
		FloatColumn metric = new FloatColumn("metric");
		
		// create package vertices
		ProjectModel modelStore = wspContext.getModelStore();
		HashMap<IGenericModelNode, Integer> parentNodeMap = 
			new HashMap<IGenericModelNode, Integer>(
				modelStore.getPackageMap().size() + 10);
		
		// create package leaves
		Integer curNode, parentNode;
		for (PackageModel pkg : modelStore.getPackageMap().values()) {
			
			parentNode = Tree.ROOT;
			
			// get parent node ID
			IGenericModelNode parentPackage = pkg.getParent();
			if (parentPackage != null
					&& parentPackage instanceof PackageModel
					&& parentNodeMap.containsKey(parentPackage)) {
				
				parentNode = parentNodeMap.get(parentPackage);
			}
			
			// add the package node
			curNode = t.addNode(parentNode);
			try {
				name.setValueAt(curNode, pkg.getSimpleName());
			} catch (ParseException e) {
			}
			size.setExtend(curNode, 50);
			model.setExtend(curNode, pkg);
			
			parentNodeMap.put(pkg, curNode);
		}
		
		// create class nodes
		for (ClassModel c : modelStore.getClassMap().values()) {
			
			parentNode = Tree.ROOT;
			
			// link to the parent package
			if (parentNodeMap.containsKey(c.getParent())) {
				parentNode = parentNodeMap.get(c.getParent());
			}
			
			// add the class node
			curNode = t.addNode(parentNode);
			try {
				name.setValueAt(curNode, c.getSimpleName());
			} catch (ParseException e) {
			}
			size.setExtend(curNode, 50);
			model.setExtend(curNode, c);
			
			// grab metric measurement if applicable
			if (c instanceof IMeasurableNode
					&& wspContext.getMetric().testAppliesTo(c.getModelTypeName())) {
				metric.setFloatAt(curNode,
						((IMeasurableNode)(c)).getMetricMeasurement(
								wspContext.getMetric()).getResult());
			}
			
			// add this class to the parent node map if it has kids
			if (c.getChildCount() > 0) {
				parentNodeMap.put(c, curNode);
			}
		}
		
		// create the visualisation
		TreemapVisualization treemap = new TreemapVisualization(t);
		treemap.setVisualColumn(Visualization.VISUAL_COLOR, metric);
		treemap.setVisualColumn(Visualization.VISUAL_COLOR, metric);
		workspace.setVis(treemap);
		
	}

}
