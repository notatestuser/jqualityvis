/*
 * IVisualiserVisitor.java (JQualityVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.visualisation.views;

import org.lukep.javavis.ui.swing.WorkspaceContext;
import org.lukep.javavis.visualisation.visualisers.Openchart2Visualiser;
import org.lukep.javavis.visualisation.visualisers.PrefuseVisualiser;
import org.lukep.javavis.visualisation.visualisers.mxGraphVisualiser;

import prefuse.Display;

import com.approximatrix.charting.swing.ExtendedChartPanel;
import com.mxgraph.swing.mxGraphComponent;

/**
 * This interface is implemented by AbstractVisualisationView to facilitate the application of visualisation layout 
 * and rendering algorithms on an IVisualiser derivative that wraps up a visualisation library of some kind.
 */
public interface IVisualiserVisitor {
	
	/**
	 * Visit an mxGraph visualiser.
	 *
	 * @param visualiser the visualiser to run our goodness on
	 * @param wspContext the currently active workspace's context object
	 * @param graphComponent the mxGraph mxGraphComponent to apply changes to
	 */
	void visit(mxGraphVisualiser visualiser, WorkspaceContext wspContext, 
			mxGraphComponent graphComponent);
	
	/**
	 * Visit a Prefuse visualiser.
	 *
	 * @param visualiser the visualiser to run our goodness on
	 * @param wspContext the currently active workspace's context object
	 * @param display the Prefuse Display object to apply changes to
	 */
	void visit(PrefuseVisualiser visualiser, WorkspaceContext wspContext, 
			Display display);
	
	/**
	 * Visit an Openchart2 visualiser.
	 *
	 * @param visualiser the visualiser to run our goodness on
	 * @param wspContext the currently active workspace's context object
	 * @return the Openchart2 ExtendedChartPanel to apply changes to
	 */
	ExtendedChartPanel visit(Openchart2Visualiser visualiser, WorkspaceContext wspContext);
	
}
