/*
 * mxGraphWorkspacePane.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.ui.swing;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.HashMap;

import javax.swing.tree.DefaultTreeModel;

import org.lukep.javavis.program.generic.models.ClassModel;
import org.lukep.javavis.program.generic.models.IGenericModelNode;
import org.lukep.javavis.program.java.JavaSourceLoaderThread;
import org.lukep.javavis.ui.IProgramStatusReporter;
import org.lukep.javavis.ui.mxgraph.MxSwingCanvas;
import org.lukep.javavis.visualisation.IVisualisationVisitor;

import com.mxgraph.canvas.mxICanvas;
import com.mxgraph.canvas.mxImageCanvas;
import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.view.mxInteractiveCanvas;
import com.mxgraph.view.mxCellState;
import com.mxgraph.view.mxGraph;

public class mxGraphWorkspacePane extends AbstractWorkspacePane {
	
	private mxGraph graph;
	private mxGraphComponent graphComponent;
	
	private HashMap<String, mxCell> packageMap = new HashMap<String, mxCell>();
	private HashMap<ClassModel, mxCell> classVertexMap;
	
	public mxGraphWorkspacePane(IProgramStatusReporter statusTarget) throws Exception {
		super(statusTarget);
		
		classVertexMap = new HashMap<ClassModel, mxCell>();
		
		// initialise the mxGraph, its container component and the circle layout
		graph = new mxGraph()
		{
			public void drawState(mxICanvas canvas, mxCellState state, String label)
			{
				mxCell cell = (mxCell) state.getCell();
				
				// Indirection for wrapped swing canvas inside image canvas (used for creating
				// the preview image when cells are dragged)
				if (getModel().isVertex(cell)
						&& canvas instanceof mxImageCanvas
						&& ((mxImageCanvas) canvas).getGraphicsCanvas() instanceof MxSwingCanvas
						&& cell.getValue() instanceof ClassModel)
				{
					((MxSwingCanvas) ((mxImageCanvas) canvas).getGraphicsCanvas()).drawVertex(state, 
									new ClassCompositionComponent( (ClassModel) cell.getValue() ));
				}
				// Redirection of drawing vertices in SwingCanvas
				else if (getModel().isVertex(cell)
						&& canvas instanceof MxSwingCanvas
						&& cell.getValue() instanceof ClassModel) {
					((MxSwingCanvas) canvas).drawVertex(state, 
							new ClassCompositionComponent( (ClassModel) cell.getValue() ));
				} else {
					super.drawState(canvas, state, label);
				}
			}
		};
		
		// initialize the containing mxGraphComponent
		graphComponent = new mxGraphComponent(graph)
		{
			private static final long serialVersionUID = 4683716829748931448L;

			public mxInteractiveCanvas createCanvas()
			{
				return new MxSwingCanvas(graphComponent);
			}
		};
		graphComponent.getViewport().setOpaque(false);
		graphComponent.setBackground( new Color(0xF9FFFB) );
		
		// bind mxGraph events
		bindGraphEvents();
		
		// set the graph component in the AbstractWorkspacePane
		super.setGraphComponent(graphComponent);
		
	}
	
	private void bindGraphEvents() {
		graphComponent.getGraphControl().addMouseListener(new MouseAdapter() {

			@Override
			public void mouseReleased(MouseEvent e) {
				mxCell currentSelectionCell = (mxCell)graphComponent.getCellAt(e.getX(), e.getY());
				if (currentSelectionCell != null //&& graph.getModel().isVertex(currentSelectionCell)
						&& currentSelectionCell.getValue() instanceof IGenericModelNode) {
					wspContext.setSelectedItem((IGenericModelNode) currentSelectionCell.getValue());
				}
				super.mouseClicked(e);
			}
			
		});
	}

	public void loadCodeBase(File selectedDirectory) {
		JavaSourceLoaderThread jslt = new JavaSourceLoaderThread(selectedDirectory, 
				wspContext.modelStore) {
			
			@Override
			public void notifyStatusChange(String message) {
				setProgramStatus(message);
			}

			@Override
			public void statusFinished() {
				((DefaultTreeModel)programTree.getModel()).reload();
				metricComboBox.setEnabled(true);
			}
			
		};
		jslt.addObserver(this);
		jslt.addObserver(wspContext.modelStore);
		//graph.getModel().beginUpdate();
		new Thread(jslt).start();
	}

	@Override
	public void acceptVisualisation(IVisualisationVisitor visitor) {
		visitor.visit(this, graphComponent);
	}
	
	public void setGraphScale(double scale) {
		graphComponent.zoomTo(scale, true);
	}

}
