/*
 * Openchart2Visualiser.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.visualisation.visualisers;

import java.awt.Color;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;

import org.lukep.javavis.ui.swing.WorkspaceContext;
import org.lukep.javavis.visualisation.views.IVisualiserVisitor;

import com.approximatrix.charting.swing.ExtendedChartPanel;

public class Openchart2Visualiser extends AbstractVisualiser {

	private static final int BACKGROUND_COLOR_RGB = 0xF9FFFB;
	private static final int MOUSE_BUTTON_DYNAMIC_ZOOM = MouseEvent.BUTTON2;
	
	private ExtendedChartPanel currentChartPanel;
	
	public Openchart2Visualiser(WorkspaceContext wspContext) {
		super(wspContext);
	}

	@Override
	public JComponent acceptVisualisation(IVisualiserVisitor visitor) {
		currentChartPanel = visitor.visit(this, getWorkspaceContext());
		currentChartPanel.setZoomMouseButton(MOUSE_BUTTON_DYNAMIC_ZOOM);
		currentChartPanel.setBackground(new Color(BACKGROUND_COLOR_RGB));
		return currentChartPanel;
	}

	@Override
	public void setScale(double scale) {
		// TODO implement
	}

}
