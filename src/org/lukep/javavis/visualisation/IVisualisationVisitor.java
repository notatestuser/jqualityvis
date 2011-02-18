/*
 * IVisualisationVisitor.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.visualisation;

import org.lukep.javavis.ui.swing.PrefuseWorkspacePane;
import org.lukep.javavis.ui.swing.WorkspaceContext;
import org.lukep.javavis.ui.swing.mxGraphWorkspacePane;

import prefuse.Display;

import com.mxgraph.swing.mxGraphComponent;

public interface IVisualisationVisitor {
	
	void visit(mxGraphWorkspacePane workspace, WorkspaceContext wspContext, 
			mxGraphComponent graphComponent);
	
	void visit(PrefuseWorkspacePane workspace, WorkspaceContext wspContext, 
			Display display);
	
}
