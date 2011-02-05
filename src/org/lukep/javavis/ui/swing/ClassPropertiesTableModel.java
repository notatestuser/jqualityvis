/*
 * ClassPropertiesTableModel.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.ui.swing;

import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import org.lukep.javavis.metrics.MetricAttribute;
import org.lukep.javavis.metrics.MetricRegistry;
import org.lukep.javavis.program.generic.models.MethodModel;
import org.lukep.javavis.program.generic.models.measurable.MeasurableClassInfo;
import org.lukep.javavis.program.generic.models.measurable.MeasurableMethodInfo;

public class ClassPropertiesTableModel extends AbstractTableModel {

	protected MeasurableClassInfo subject;

	public ClassPropertiesTableModel(MeasurableClassInfo subject) {
		super();
		this.subject = subject;
	}

	public void setSubject(MeasurableClassInfo subject) {
		this.subject = subject;
		fireTableDataChanged();
	}

	@Override
	public int getRowCount() {
		if (subject == null)
			return 0;
		return subject.getMethodCount();
	}

	@Override
	public int getColumnCount() {
		return MetricRegistry.getInstance().getSupportedMetricCount(
				MeasurableMethodInfo.APPLIES_TO_STR) + 1;
	}
	
	@Override
	public String getColumnName(int column) {
		if (column == 0) {
			return "Method Name";
		} else {
			Vector<MetricAttribute> supportMap = 
				MetricRegistry.getInstance().getSupportedMetrics(
						MeasurableMethodInfo.APPLIES_TO_STR);
			return supportMap.get(column - 1).getName();
		}
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (subject == null)
			return null;
		
		MethodModel method = subject.getMethods().get(rowIndex);
		if (method instanceof MeasurableMethodInfo) {
			if (columnIndex == 0) {
				return method.getName();
			} else {
				Vector<MetricAttribute> supportMap = 
					MetricRegistry.getInstance().getSupportedMetrics(
							MeasurableMethodInfo.APPLIES_TO_STR);
				return ((MeasurableMethodInfo)(method)).getMetricMeasurement(
						supportMap.get(columnIndex - 1)).getResult();
			}
		}
		return "unknown";
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}

}
