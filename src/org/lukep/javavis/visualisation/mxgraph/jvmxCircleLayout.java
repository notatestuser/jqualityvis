/*
 * jvmxCircleLayout.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.visualisation.mxgraph;

import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.view.mxGraph;

public class jvmxCircleLayout extends mxCircleLayout {

	public jvmxCircleLayout(mxGraph graph) {
		super(graph);
	}
	
	/**
	 * Executes the circular layout for the specified array
	 * of vertices and the given radius.
	 */
	public void circle(Object[] vertices, double r, double left, double top)
	{
		r = r / 2;
		super.circle(vertices, r, left, top);
	}

}
