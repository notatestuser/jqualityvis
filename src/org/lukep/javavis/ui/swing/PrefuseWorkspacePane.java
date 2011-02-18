/*
 * PrefuseWorkspacePane.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.ui.swing;

import java.awt.Color;

import javax.swing.BorderFactory;

import org.lukep.javavis.ui.IProgramStatusReporter;
import org.lukep.javavis.visualisation.IVisualisationVisitor;

import prefuse.Display;
import prefuse.Visualization;

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
		
		super.setGraphComponent(display);
	}

	@Override
	public void acceptVisualisation(IVisualisationVisitor visitor) {
		visitor.visit(this, display, visualisation);
	}

}
