/*
 * PrefuseWorkspacePane.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.ui.swing;

import java.awt.Color;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;

import org.lukep.javavis.program.generic.models.IGenericModelNode;
import org.lukep.javavis.ui.IProgramStatusReporter;
import org.lukep.javavis.visualisation.IVisualisationVisitor;

import prefuse.Display;
import prefuse.Visualization;
import prefuse.controls.FocusControl;
import prefuse.controls.PanControl;
import prefuse.controls.WheelZoomControl;
import prefuse.controls.ZoomControl;
import prefuse.controls.ZoomToFitControl;
import prefuse.visual.VisualItem;

public class PrefuseWorkspacePane extends AbstractWorkspacePane {

	private Display display;
	private Visualization visualisation;
	
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
		display.addControlListener(new FocusControl(1) {

			@Override
			public void itemClicked(VisualItem item, MouseEvent e) {
				Object sItem = item.get("model");
				if (sItem instanceof IGenericModelNode)
					wspContext.setSelectedItem((IGenericModelNode) item.get("model"));
				super.itemClicked(item, e);
			}
			
		});
		display.addControlListener(new ZoomToFitControl());
		display.addControlListener(new ZoomControl());
		display.addControlListener(new PanControl(true));
		display.addControlListener(new WheelZoomControl());
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
