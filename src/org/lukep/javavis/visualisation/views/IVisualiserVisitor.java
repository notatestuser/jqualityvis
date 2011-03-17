/*
 * IVisualiserVisitor.java (JMetricVis)
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

public interface IVisualiserVisitor {
	
	void visit(mxGraphVisualiser visualiser, WorkspaceContext wspContext, 
			mxGraphComponent graphComponent);
	
	void visit(PrefuseVisualiser visualiser, WorkspaceContext wspContext, 
			Display display);
	
	ExtendedChartPanel visit(Openchart2Visualiser visualiser, WorkspaceContext wspContext);
	
}
