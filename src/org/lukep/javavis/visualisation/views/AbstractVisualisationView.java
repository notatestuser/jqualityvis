/*
 * AbstractVisualisationView.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.visualisation.views;

import org.lukep.javavis.ui.swing.WorkspaceContext;
import org.lukep.javavis.visualisation.visualisers.PrefuseVisualiser;
import org.lukep.javavis.visualisation.visualisers.mxGraphVisualiser;

import prefuse.Display;

import com.mxgraph.swing.mxGraphComponent;

public class AbstractVisualisationView implements IVisualiserVisitor {

	@Override
	public void visit(mxGraphVisualiser visualiser, WorkspaceContext wspContext, 
			mxGraphComponent graphComponent) {
		
		// overridden in sub-class
		throw new UnsupportedOperationException("This Visualisation is unsupported by the current Workspace.");
	}

	@Override
	public void visit(PrefuseVisualiser visualiser, WorkspaceContext wspContext, 
			Display display) {
		
		// overridden in sub-class
		throw new UnsupportedOperationException("This Visualisation is unsupported by the current Workspace.");
	}

}
