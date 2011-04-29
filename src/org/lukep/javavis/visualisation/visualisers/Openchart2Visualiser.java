/*
 * Openchart2Visualiser.java (JQualityVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.visualisation.visualisers;

import java.awt.Color;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;

import org.lukep.javavis.ui.swing.WorkspaceContext;
import org.lukep.javavis.visualisation.views.IVisualiserVisitor;

import com.approximatrix.charting.swing.ExtendedChartPanel;

/**
 * The Class Openchart2Visualiser.
 */
public class Openchart2Visualiser extends AbstractVisualiser {

	private static final int BACKGROUND_COLOR_RGB = 0xF9FFFB;
	private static final int MOUSE_BUTTON_DYNAMIC_ZOOM = MouseEvent.BUTTON2;
	private ExtendedChartPanel currentChartPanel;
	
	/**
	 * Instantiates a new openchart2 visualiser.
	 *
	 * @param wspContext the wsp context
	 */
	public Openchart2Visualiser(WorkspaceContext wspContext) {
		super(wspContext);
	}

	/* (non-Javadoc)
	 * @see org.lukep.javavis.visualisation.visualisers.AbstractVisualiser#acceptVisualisation(org.lukep.javavis.visualisation.views.IVisualiserVisitor)
	 */
	@Override
	public JComponent acceptVisualisation(IVisualiserVisitor visitor) {
		currentChartPanel = visitor.visit(this, getWorkspaceContext());
		currentChartPanel.setZoomMouseButton(MOUSE_BUTTON_DYNAMIC_ZOOM);
		currentChartPanel.setBackground(new Color(BACKGROUND_COLOR_RGB));
		return currentChartPanel;
	}

	/* (non-Javadoc)
	 * @see org.lukep.javavis.visualisation.visualisers.AbstractVisualiser#setScale(double)
	 */
	@Override
	public void setScale(double scale) {
		// TODO implement
	}

}
