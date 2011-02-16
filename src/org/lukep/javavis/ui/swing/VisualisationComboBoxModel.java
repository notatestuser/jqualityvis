/*
 * VisualisationComboBoxModel.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.ui.swing;

import java.util.List;

import javax.swing.DefaultComboBoxModel;

import org.lukep.javavis.visualisation.Visualisation;

public class VisualisationComboBoxModel extends DefaultComboBoxModel {

	protected List<Visualisation> visualisations;
	
	@Override
	public int getSize() {
		if (visualisations == null)
			return 1;
		return super.getSize();
	}

	@Override
	public Object getElementAt(int index) {
		if (visualisations == null)
			return "( Select a metric to view available visualisations )";
		else
			return super.getElementAt(index);
	}

	public void setVisualisations(List<Visualisation> visualisations) {
		removeAllElements();
		this.visualisations = visualisations;
		int visualisationsSize = visualisations.size();
		addElement("( " + visualisationsSize + " " + 
				(visualisationsSize == 1 ? "visualisation" : "visualisations") + " available )");
		for (Visualisation vis : visualisations)
			addElement(vis);
	}
	
}
