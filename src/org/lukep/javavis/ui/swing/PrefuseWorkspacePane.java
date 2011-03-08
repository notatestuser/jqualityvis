/*
 * PrefuseWorkspacePane.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.ui.swing;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.event.MouseEvent;
import java.util.Iterator;

import javax.swing.BorderFactory;

import org.lukep.javavis.program.generic.models.IGenericModelNode;
import org.lukep.javavis.ui.IProgramStatusReporter;
import org.lukep.javavis.visualisation.IVisualisationVisitor;

import prefuse.Display;
import prefuse.Visualization;
import prefuse.controls.ControlAdapter;
import prefuse.controls.FocusControl;
import prefuse.controls.PanControl;
import prefuse.controls.WheelZoomControl;
import prefuse.controls.ZoomControl;
import prefuse.controls.ZoomToFitControl;
import prefuse.visual.EdgeItem;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualItem;

public class PrefuseWorkspacePane extends AbstractWorkspacePane {

	private static final int BACKGROUND_COLOR_RGB = 0xF9FFFB;
	private static final int HIGHLIGHT_COLOR_RGB  = 0xFFBB87FF;
	private static final int SELECTION_COLOR_RGB  = 0xFF5EC3C7;
	
	private Display display;
	private Visualization visualisation;
	
	private NodeItem currentSelectedNode;
	
	public PrefuseWorkspacePane(IProgramStatusReporter statusTarget)
			throws Exception {
		super(statusTarget);

		visualisation = new Visualization();
		
		display = new Display(visualisation);
		display.setOpaque(true);
		display.setBackground( new Color(0xF9FFFB) );
		display.setBorder( BorderFactory.createEtchedBorder() );
		
		bindPerfuseEvents();
		
		super.setGraphComponent(display);
	}
	
	private void bindPerfuseEvents() {
		display.addControlListener(new ZoomToFitControl());
		display.addControlListener(new ZoomControl());
		display.addControlListener(new PanControl(true));
		display.addControlListener(new WheelZoomControl());
		
		// handle roll-overs
        display.addControlListener(new ControlAdapter() {
        	
        	@Override
			public void mouseClicked(MouseEvent e) {
				wspContext.setSelectedItem(wspContext.getModelStore());
			}

			@Override
        	public void itemEntered(VisualItem item, MouseEvent e) {
                // only un-highlight this node if it's not selected
        		if (item != currentSelectedNode) {
        			item.setStrokeColor(HIGHLIGHT_COLOR_RGB);
        			item.setStroke(new BasicStroke(3));
        		}
                
                // highlight connected edges
                if (item instanceof NodeItem) {
                	EdgeItem edge;
                	Iterator<EdgeItem> iter = ((NodeItem)(item)).edges();
        			while (iter.hasNext()) {
        				edge = iter.next();
        				edge.setStrokeColor(HIGHLIGHT_COLOR_RGB);
        				edge.setStroke(new BasicStroke(2));
        			}
                }
                
                item.getVisualization().repaint();
            }
            
        	@Override
            public void itemExited(VisualItem item, MouseEvent e) {
                // only un-highlight this node if it's not selected
        		if (item != currentSelectedNode) {
	        		item.setStrokeColor(item.getEndStrokeColor());
	                item.setStroke(new BasicStroke(0));
        		}
                
                // un-highlight connected edges
                if (item instanceof NodeItem) {
                	EdgeItem edge;
                	Iterator<EdgeItem> iter = ((NodeItem)(item)).edges();
        			while (iter.hasNext()) {
        				edge = iter.next();
        				edge.setStrokeColor(edge.getEndStrokeColor());
        				edge.setStroke(new BasicStroke(0));
        			}
                }
                
                item.getVisualization().repaint();
            }
        });
        
        // handle single clicks / selections
		display.addControlListener(new FocusControl(1) {

			@Override
			public void itemClicked(VisualItem item, MouseEvent e) {
				
				if (item instanceof NodeItem) {
					
					if (item != currentSelectedNode) {
						
						// deselect last item
						if (currentSelectedNode != null) {
							currentSelectedNode.setStrokeColor(item.getEndStrokeColor());
							currentSelectedNode.setStroke(new BasicStroke(0));
						}
						
						// update the currently selected item pointer
						currentSelectedNode = (NodeItem) item;
					}
					
					// update the selected node's visual attributes
    				item.setStrokeColor(SELECTION_COLOR_RGB);
    				item.setStroke(new BasicStroke(3));
					
					// set the WorkspaceContext's currently selected item
					wspContext.setSelectedItem(
							(IGenericModelNode) item.get("model"));
				}
				
				item.getVisualization().repaint();
				super.itemClicked(item, e);
			}
			
		});
	}

	@Override
	public void setGraphScale(double scale) {
		display.zoom(display.getLocation(), scale);
	}

	@Override
	public void acceptVisualisation(IVisualisationVisitor visitor) {
		visitor.visit(this, wspContext, display);
	}

}
