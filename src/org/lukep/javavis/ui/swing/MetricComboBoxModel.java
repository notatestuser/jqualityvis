/*
 * MetricComboBoxModel.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.ui.swing;

import javax.swing.DefaultComboBoxModel;

import org.lukep.javavis.metrics.MetricRegistry;

@SuppressWarnings("serial")
public class MetricComboBoxModel extends DefaultComboBoxModel {

	@Override
	public int getSize() {
		return MetricRegistry.getInstance().getMetricAttributeCount() + 1;
	}

	@Override
	public Object getElementAt(int index) {
		if (index == 0)
			return "( " + (getSize() - 1) + " metrics loaded )";
		return MetricRegistry.getInstance().getMetricAttributes().toArray()[index - 1];
	}

	@Override
	public int getIndexOf(Object anObject) {
		// TODO Auto-generated method stub
		return super.getIndexOf(anObject);
	}


}
