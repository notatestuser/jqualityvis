/*
 * IVisualisationVisitor.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.visualisation;

import org.lukep.javavis.ui.swing.mxGraphWorkspacePane;

import com.mxgraph.swing.mxGraphComponent;

public interface IVisualisationVisitor {

	
	
	void visit(mxGraphWorkspacePane workspace, mxGraphComponent graphComponent);
	
}
