/*
 * ClassHierarchyView.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.visualisation.views;

import java.util.HashMap;

import org.lukep.javavis.metrics.IMeasurable;
import org.lukep.javavis.program.generic.models.ClassModel;
import org.lukep.javavis.program.generic.models.PackageModel;
import org.lukep.javavis.program.generic.models.ProgramModelStore;
import org.lukep.javavis.ui.swing.WorkspaceContext;
import org.lukep.javavis.ui.swing.mxGraphWorkspacePane;

import com.mxgraph.layout.mxCompactTreeLayout;
import com.mxgraph.layout.mxGraphLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxIGraphModel;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.util.mxMorphing;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource.mxIEventListener;
import com.mxgraph.view.mxGraph;

public class ClassHierarchyView extends AbstractVisualisationView {

	@Override
	public void visit(mxGraphWorkspacePane workspace,
			mxGraphComponent graphComponent) {

		// obtain the workspace's context object (which gives us access to model store, vis, metric, relations)
		final WorkspaceContext wspContext = workspace.getContext();
		
		// start the mxGraph transaction and clear model
		final mxGraph graph = graphComponent.getGraph();
		final mxIGraphModel graphModel = graphComponent.getGraph().getModel();
		graphModel.beginUpdate();
		graph.selectAll();
		graph.removeCells();
		
		// bind graph layout manager
		mxGraphLayout graphLayout = new mxCompactTreeLayout(graph);
		
		// create package vertices
		ProgramModelStore modelStore = wspContext.getModelStore();
		HashMap<IMeasurable, mxCell> parentCellMap = new HashMap<IMeasurable, mxCell>();
		mxCell curCell;
		
		for (PackageModel pkg : modelStore.getPackageMap().values()) {
			
			curCell = (mxCell) graph.insertVertex(graph.getDefaultParent(), 
					pkg.getQualifiedName(), pkg, 250, 100, 150, 80);
			
			IMeasurable parentPackage = pkg.getParent();
			if (parentPackage != null
					&& parentPackage instanceof PackageModel
					&& parentCellMap.containsKey(parentPackage)) {
				
				// link to the parent package with an edge
				graph.insertEdge(graph.getDefaultParent(), null, null, 
						parentCellMap.get(parentPackage), curCell, 
						mxConstants.STYLE_ENDARROW + "=" + mxConstants.ARROW_OPEN);
			}
			
			parentCellMap.put(pkg, curCell);
		}
		
		// create class vertices
		for (ClassModel clazz : modelStore.getClassMap().values()) {
			
			curCell = (mxCell) graph.insertVertex(graph.getDefaultParent(), null, clazz, 20, 20, 150, 50);
			
			if (clazz.getParent() instanceof PackageModel
					&& parentCellMap.containsKey(clazz.getParent())) {
				
				// link to the parent package
				graph.insertEdge(graph.getDefaultParent(), null, null, 
						parentCellMap.get(clazz.getParent()), curCell);
			}
		}
		
		// execute the layout and animation
		graphModel.endUpdate();
		graphModel.beginUpdate();
		
		try {
			graphLayout.execute(graph.getDefaultParent());
		} finally {
			mxMorphing morph = new mxMorphing(graphComponent, 20, 1.2, 20);
			
			morph.addListener(mxEvent.DONE, new mxIEventListener() {
				
				@Override
				public void invoke(Object sender, mxEventObject evt) {
					graphModel.endUpdate();
				}
			});
			
			morph.startAnimation();
		}
	}

}
