/*
 * VisualisationComboBoxModel.java (JQualityVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.ui.swing;

import java.util.List;

import javax.swing.DefaultComboBoxModel;

import org.lukep.javavis.visualisation.Visualisation;

/**
 * The visualisation combo-box model shows the list of available visualisations to the user.
 */
@SuppressWarnings("serial")
public class VisualisationComboBoxModel extends DefaultComboBoxModel {

	/** The visualisations. */
	protected List<Visualisation> visualisations;
	
	/* (non-Javadoc)
	 * @see javax.swing.DefaultComboBoxModel#getSize()
	 */
	@Override
	public int getSize() {
		if (visualisations == null)
			return 1;
		return super.getSize();
	}

	/* (non-Javadoc)
	 * @see javax.swing.DefaultComboBoxModel#getElementAt(int)
	 */
	@Override
	public Object getElementAt(int index) {
		if (visualisations == null)
			return "( No metric selected )";
		else
			return super.getElementAt(index);
	}

	/**
	 * Sets the visualisations stored in this model.
	 *
	 * @param visualisations the new visualisations
	 */
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
