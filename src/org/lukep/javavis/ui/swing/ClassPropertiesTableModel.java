/*
 * ClassPropertiesTableModel.java (JQualityVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.ui.swing;

import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import org.lukep.javavis.metrics.IMeasurableNode;
import org.lukep.javavis.metrics.MetricAttribute;
import org.lukep.javavis.metrics.MetricRegistry;
import org.lukep.javavis.program.generic.models.ClassModel;
import org.lukep.javavis.program.generic.models.IGenericModelNode;
import org.lukep.javavis.program.generic.models.PackageModel;
import org.lukep.javavis.program.generic.models.Relationship;
import org.lukep.javavis.util.JavaVisConstants;

// TODO clean this up
/**
 * The Class ClassPropertiesTableModel.
 */
@SuppressWarnings("serial")
public class ClassPropertiesTableModel extends AbstractTableModel {

	protected IGenericModelNode subject;

	/**
	 * Instantiates a new class properties table model.
	 *
	 * @param subject the subject
	 */
	public ClassPropertiesTableModel(IGenericModelNode subject) {
		super();
		this.subject = subject;
	}

	/**
	 * Sets the subject.
	 *
	 * @param subject the new subject
	 */
	public void setSubject(IGenericModelNode subject) {
		this.subject = subject;
		fireTableStructureChanged();
		fireTableDataChanged();
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	@Override
	public int getRowCount() {
		if (subject instanceof ClassModel)
			return ((ClassModel)(subject)).getMethodCount();
		else if (subject instanceof PackageModel)
			return ((PackageModel)(subject)).getChildCount();
		return 0;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
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
	
	/* (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
	 */
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
			return supportMap.get(column - 1).getInternalName();
		}
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (subject == null)
			return null;
		
		IMeasurableNode measurable = null;
		
		if (subject instanceof ClassModel)
			measurable = ((ClassModel)(subject)).getMethods().get(rowIndex);
		else if (subject instanceof PackageModel)
			measurable = (IMeasurableNode) ((Relationship)(
					subject.getChildren().toArray()[rowIndex])).getTarget();
		else
			return null;
		
		if (columnIndex == 0) {
			if (measurable instanceof IGenericModelNode)
				return measurable.getModifierNames(" ") + " "
					 + measurable.getSimpleName();
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
						supportMap.get(columnIndex - 1)).getRoundedResult(5);
		}
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#isCellEditable(int, int)
	 */
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}

}
