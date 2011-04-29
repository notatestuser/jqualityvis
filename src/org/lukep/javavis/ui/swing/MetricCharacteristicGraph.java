/*
 * MetricCharacteristicGraph.java (JQualityVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.ui.swing;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;

import org.lukep.javavis.metrics.MetricAttribute;
import org.lukep.javavis.metrics.qualityModels.DesignQualityAttribute;
import org.lukep.javavis.metrics.qualityModels.WeightedMetricFactor;
import org.lukep.javavis.metrics.qualityModels.WeightedMetricFactor.Value;
import org.lukep.javavis.ui.swing.WorkspaceContext.ChangeEvent;

import com.mxgraph.layout.mxGraphLayout;
import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxIGraphModel;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxGraph;

/**
 * Displays a visual representation of the metrics and characteristics that contribute to a displayed visualisation.
 */
public class MetricCharacteristicGraph implements Observer {

	private WorkspaceContext wspContext;
	
	private mxGraphComponent graphComponent;
	private mxGraph graph = new mxGraph();
	
	private Map<MetricAttribute, mxCell> metricCellMap = 
		new HashMap<MetricAttribute, mxCell>();
		
	private Map<String, mxCell> characteristicCellMap = 
		new HashMap<String, mxCell>();
	
	/**
	 * Instantiates a new metric characteristic graph.
	 *
	 * @param wspContext the wsp context
	 */
	public MetricCharacteristicGraph(WorkspaceContext wspContext) {
		graphComponent = new mxGraphComponent(graph);
		graph.setCellsLocked(true);
		
		graphComponent.getViewport().setOpaque(false);
		graphComponent.setBackground(Color.white);
		graphComponent.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		// observe the wspContext so we know about metric changes
		this.wspContext = wspContext;
		wspContext.addObserver(this);
	}
	
	/**
	 * Sets the metric to display.
	 *
	 * @param metric the new metric to display
	 */
	public void setMetricToDisplay(MetricAttribute metric) {
		
		// start the mxGraph transaction and clear model
		final mxIGraphModel graphModel = graphComponent.getGraph().getModel();
		graphModel.beginUpdate();
		graph.selectAll();
		graph.removeCells();
		
		// bind graph layout manager
		mxGraphLayout graphLayout = new mxHierarchicalLayout(graph);
		
		// set a scale
		graphComponent.zoomTo(1, true);
		
		// create a set of metrics - these will be connected to the characteristics that they measure
		metricCellMap.clear();
		
		// create a set of measured characteristics - these will be in the centre of the diagram
		characteristicCellMap.clear();
		
		if ( !(metric instanceof DesignQualityAttribute) ) {
			// we have a normal metric - and thus only one "metric" node and one measured "characteristic"
			addSourceMetric(metric, null);
		} else {
			graphComponent.zoomTo(0.8, true);
			
			// design attributes are a little more tricky, we need to add all of the metrics measured and 
			// their characteristics
			DesignQualityAttribute dqa = (DesignQualityAttribute) metric;
			
			for (WeightedMetricFactor wmf : dqa.getWeightedMetrics()) {
				addSourceMetric(wmf.getMetric(), wmf.getValue());
			}
			
			// create the vertex representing the design quality attribute
			mxCell dqaCell = (mxCell) graph.insertVertex(graph.getDefaultParent(), 
					dqa.getName(), dqa, 0, 0, 220, 20, 
					mxConstants.STYLE_FONTSIZE+"=16;"+
					mxConstants.STYLE_FONTSTYLE+"="+
					mxConstants.FONT_BOLD+";"+
					mxConstants.STYLE_FILLCOLOR+"=#FFE1AD;"+
					mxConstants.STYLE_GRADIENTCOLOR+"=#FFD48A;"+
					mxConstants.STYLE_SHADOW+"=true");
			
			// all of the characteristics obtained determine this quality attribute - gotta catch 'em all
			for (mxCell characteristicCell : characteristicCellMap.values()) {
				graph.insertEdge(graph.getDefaultParent(), null, "determines", characteristicCell, dqaCell);
			}
		}
		
		// execute the layout manager
		graphLayout.execute(graph.getDefaultParent());
		
		// end the model update and display
		graphModel.endUpdate();
	}

	/**
	 * Gets the graph component.
	 *
	 * @return the graph component
	 */
	public mxGraphComponent getGraphComponent() {
		return graphComponent;
	}
	
	/**
	 * Adds the source metric.
	 *
	 * @param metric the metric
	 * @param value the value
	 */
	private void addSourceMetric(MetricAttribute metric, Value value) {
		// create metric cell
		mxCell mCell = (mxCell) graph.insertVertex(graph.getDefaultParent(), 
					metric.getInternalName(), metric.getInternalName(), 0, 0, 50, 20, 
					mxConstants.STYLE_FONTSIZE+"=16;"+
					mxConstants.STYLE_FILLCOLOR+"=#BEFFAD;"+
					mxConstants.STYLE_SHADOW+"=true");
		metricCellMap.put(metric, mCell);
		
		// create characteristic cell if we don't have one already
		String characteristic = metric.getCharacteristic();
		mxCell cCell = null;
		if (!characteristicCellMap.containsKey(characteristic)) {
			// add a value string "(-)" or "(+)"
			String characteristicValue = "", fillColour = "ADE7FF";
			if (value != null) {
				if (value == Value.POSITIVE) {
					characteristicValue = " (+)";
				} else {
					characteristicValue = " (-)";
					fillColour = "FFC4AD";
				}
			}
			
			// insert the characteristic vertex
			cCell = (mxCell) graph.insertVertex(graph.getDefaultParent(), 
					characteristic + characteristicValue, 
					characteristic + characteristicValue, 0, 0, 120, 20, 
					mxConstants.STYLE_FONTSIZE+"=16;"+
					mxConstants.STYLE_FILLCOLOR+"=#"+fillColour+";"+
					mxConstants.STYLE_SHADOW+"=true");
			characteristicCellMap.put(characteristic, cCell);
		} else {
			cCell = characteristicCellMap.get(characteristic);
		}
		
		// link metric (source) to characteristic (destination)
		graph.insertEdge(graph.getDefaultParent(), null, "measures", mCell, cCell);
	}

	/* (non-Javadoc)
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	@Override
	public void update(Observable o, Object arg) {
		ChangeEvent ce = (ChangeEvent) arg;
		switch (ce) {
		case METRIC_CHANGE:
			// update the metric we're visualising
			if (wspContext.getMetric() != null)
				setMetricToDisplay(wspContext.getMetric());
			break;
		}
	}
	
}
