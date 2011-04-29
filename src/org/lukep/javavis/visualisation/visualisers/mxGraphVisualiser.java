/*
 * mxGraphVisualiser.java (JQualityVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.visualisation.visualisers;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JComponent;

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

/**
 * An mxGraphVisualiser.
 */
public class mxGraphVisualiser extends AbstractVisualiser {
	
	private static final int BACKGROUND_COLOR_RGB = 0xF9FFFB;
	
	private mxGraph graph;
	private mxGraphComponent graphComponent;
	
	/**
	 * Instantiates a new mx graph visualiser.
	 *
	 * @param wspContext the wsp context
	 * @throws Exception the exception
	 */
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
		
		// initialise the containing mxGraphComponent
		graphComponent = new mxGraphComponent(graph)
		{
			private static final long serialVersionUID = 4683716829748931448L;

			public mxInteractiveCanvas createCanvas()
			{
				return new MxSwingCanvas(graphComponent);
			}
		};
		graphComponent.getViewport().setOpaque(false);
		graphComponent.setBackground( new Color(BACKGROUND_COLOR_RGB) );
		
		// bind mxGraph events
		bindMxGraphEvents();
		
	}
	
	private void bindMxGraphEvents() {
		// graph entity selection handling
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
		
		// mouse wheel zoom handling
		graphComponent.getGraphControl().addMouseWheelListener(new MouseWheelListener() {
			
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				if (e.getWheelRotation() < 0)
					graphComponent.zoomIn();
				else if (e.getWheelRotation() > 0)
					graphComponent.zoomOut();
			}
		});
	}
	
	/* (non-Javadoc)
	 * @see org.lukep.javavis.visualisation.visualisers.AbstractVisualiser#acceptVisualisation(org.lukep.javavis.visualisation.views.IVisualiserVisitor)
	 */
	@Override
	public JComponent acceptVisualisation(IVisualiserVisitor visitor) {
		visitor.visit(this, getWorkspaceContext(), graphComponent);
		return graphComponent;
	}

	/* (non-Javadoc)
	 * @see org.lukep.javavis.visualisation.visualisers.AbstractVisualiser#setScale(double)
	 */
	@Override
	public void setScale(double scale) {
		graphComponent.zoomTo(scale, true);
	}

}
