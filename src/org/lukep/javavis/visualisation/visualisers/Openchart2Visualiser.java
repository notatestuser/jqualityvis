/*
 * Openchart2Visualiser.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.visualisation.visualisers;

import java.awt.Component;
import java.awt.event.MouseEvent;

import org.lukep.javavis.ui.swing.WorkspaceContext;
import org.lukep.javavis.visualisation.views.IVisualiserVisitor;

import com.approximatrix.charting.swing.ExtendedChartPanel;

public class Openchart2Visualiser extends AbstractVisualiser {

	private static final int MOUSE_BUTTON_DYNAMIC_ZOOM = MouseEvent.BUTTON2;
	
	private ExtendedChartPanel currentChartPanel;
	
	public Openchart2Visualiser(WorkspaceContext wspContext) {
		super(wspContext);
	}

	@Override
	public Component acceptVisualisation(IVisualiserVisitor visitor) {
		currentChartPanel = visitor.visit(this, getWorkspaceContext());
		currentChartPanel.setZoomMouseButton(MOUSE_BUTTON_DYNAMIC_ZOOM);
		return currentChartPanel;
	}

	@Override
	public void setScale(double scale) {
		// TODO implement
	}

}
