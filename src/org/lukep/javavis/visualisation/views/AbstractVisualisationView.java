/*
 * AbstractVisualisationView.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.visualisation.views;

import org.lukep.javavis.ui.swing.mxGraphWorkspacePane;
import org.lukep.javavis.visualisation.IVisualisationVisitor;

import com.mxgraph.swing.mxGraphComponent;

public class AbstractVisualisationView implements IVisualisationVisitor {

	@Override
	public void visit(mxGraphWorkspacePane workspace,
			mxGraphComponent graphComponent) {
		// overridden in implementing class
	}

}
