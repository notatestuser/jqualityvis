/*
 * ClassPropertiesTableModel.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.ui.swing;

import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import org.lukep.javavis.metrics.IMeasurable;
import org.lukep.javavis.metrics.MetricAttribute;
import org.lukep.javavis.metrics.MetricRegistry;
import org.lukep.javavis.program.generic.models.ClassModel;
import org.lukep.javavis.program.generic.models.IGenericModelNode;
import org.lukep.javavis.program.generic.models.PackageModel;
import org.lukep.javavis.util.JavaVisConstants;

// TODO clean this up
public class ClassPropertiesTableModel extends AbstractTableModel {

	protected IGenericModelNode subject;

	public ClassPropertiesTableModel(IGenericModelNode subject) {
		super();
		this.subject = subject;
	}

	public void setSubject(IGenericModelNode subject) {
		this.subject = subject;
		fireTableStructureChanged();
		fireTableDataChanged();
	}

	@Override
	public int getRowCount() {
		if (subject instanceof ClassModel)
			return ((ClassModel)(subject)).getMethodCount();
		else if (subject instanceof PackageModel)
			return ((PackageModel)(subject)).getChildCount();
		return 0;
	}

	@Override
	public int getColumnCount() {
		if (subject instanceof ClassModel)
			return MetricRegistry.getInstance().getSupportedMetricCount(
						JavaVisConstants.METRIC_APPLIES_TO_METHOD) + 1;
		else if (subject instanceof PackageModel)
			return MetricRegistry.getInstance().getSupportedMetricCount(
						JavaVisConstants.METRIC_APPLIES_TO_CLASS) + 1;
		return 0;
	}
	
	@Override
	public String getColumnName(int column) {
		if (column == 0) {
			return "Member Name";
		} else {
			Vector<MetricAttribute> supportMap = null;
			if (subject instanceof ClassModel)
				supportMap = MetricRegistry.getInstance().getSupportedMetrics(
							JavaVisConstants.METRIC_APPLIES_TO_METHOD);
			else if (subject instanceof PackageModel)
				supportMap = MetricRegistry.getInstance().getSupportedMetrics(
							JavaVisConstants.METRIC_APPLIES_TO_CLASS);
			else
				return "( Unknown )";
			return supportMap.get(column - 1).getName();
		}
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (subject == null)
			return null;
		
		IMeasurable measurable = null;
		
		if (subject instanceof ClassModel)
			measurable = ((ClassModel)(subject)).getMethods().get(rowIndex);
		else if (subject instanceof PackageModel)
			measurable = (IMeasurable) subject.getChildren().get(rowIndex).getTarget();
		else
			return null;
		
		if (columnIndex == 0) {
			if (measurable instanceof IGenericModelNode)
				return ((IGenericModelNode)(measurable)).getModifierNames(" ") + " "
					 + ((IGenericModelNode)(measurable)).getSimpleName();
			else
				return "( Member )";
		} else {
			Vector<MetricAttribute> supportMap;
			
			if (subject instanceof ClassModel)
				supportMap = MetricRegistry.getInstance().getSupportedMetrics(
							JavaVisConstants.METRIC_APPLIES_TO_METHOD);
			else if (subject instanceof PackageModel)
				supportMap = MetricRegistry.getInstance().getSupportedMetrics(
							JavaVisConstants.METRIC_APPLIES_TO_CLASS);
			else
				return "( Unknown )";
			
			return measurable.getMetricMeasurement(
						supportMap.get(columnIndex - 1)).getResult();
		}
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}

}
