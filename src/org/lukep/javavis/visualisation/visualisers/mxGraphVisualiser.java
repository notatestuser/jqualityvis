/*
 * mxGraphVisualiser.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.visualisation.visualisers;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import org.lukep.javavis.program.generic.models.ClassModel;
import org.lukep.javavis.program.generic.models.IGenericModelNode;
import org.lukep.javavis.ui.mxgraph.MxSwingCanvas;
import org.lukep.javavis.ui.swing.ClassCompositionComponent;
import org.lukep.javavis.ui.swing.WorkspaceContext;
import org.lukep.javavis.visualisation.views.IVisualiserVisitor;

import com.mxgraph.canvas.mxICanvas;
import com.mxgraph.canvas.mxImageCanvas;
import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.view.mxInteractiveCanvas;
import com.mxgraph.view.mxCellState;
import com.mxgraph.view.mxGraph;

public class mxGraphVisualiser extends AbstractVisualiser {
	
	private mxGraph graph;
	private mxGraphComponent graphComponent;
	
	public mxGraphVisualiser(WorkspaceContext wspContext) throws Exception {

		super(wspContext);
		
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
		bindMxGraphEvents();
		
	}
	
	private void bindMxGraphEvents() {
		graphComponent.getGraphControl().addMouseListener(new MouseAdapter() {

			@Override
			public void mouseReleased(MouseEvent e) {
				mxCell currentSelectionCell = (mxCell)graphComponent.getCellAt(e.getX(), e.getY());
				if (currentSelectionCell != null //&& graph.getModel().isVertex(currentSelectionCell)
						&& currentSelectionCell.getValue() instanceof IGenericModelNode) {
					getWorkspaceContext().setSelectedItem((IGenericModelNode) currentSelectionCell.getValue());
				}
				super.mouseClicked(e);
			}
			
		});
	}
	
	@Override
	public Component acceptVisualisation(IVisualiserVisitor visitor) {
		visitor.visit(this, getWorkspaceContext(), graphComponent);
		return graphComponent;
	}

	@Override
	public void setScale(double scale) {
		graphComponent.zoomTo(scale, true);
	}

}
