/*
 * AbstractVisualisationView.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.visualisation.views;

import org.lukep.javavis.ui.swing.PrefuseWorkspacePane;
import org.lukep.javavis.ui.swing.WorkspaceContext;
import org.lukep.javavis.ui.swing.mxGraphWorkspacePane;
import org.lukep.javavis.visualisation.IVisualisationVisitor;

import prefuse.Display;

import com.mxgraph.swing.mxGraphComponent;

public class AbstractVisualisationView implements IVisualisationVisitor {

	@Override
	public void visit(mxGraphWorkspacePane workspace, WorkspaceContext wspContext, 
			mxGraphComponent graphComponent) {
		
		// overridden in sub-class
		throw new UnsupportedOperationException("This Visualisation is unsupported by the current Workspace.");
	}

	@Override
	public void visit(PrefuseWorkspacePane workspace, WorkspaceContext wspContext, 
			Display display) {
		
		// overridden in sub-class
		throw new UnsupportedOperationException("This Visualisation is unsupported by the current Workspace.");
	}

}
