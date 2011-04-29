/*
 * ProjectThresholdConfigurationPanel.java (JQualityVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.ui.swing.configPanes;

import java.awt.event.ActionEvent;
import java.util.Collection;

import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.lukep.javavis.metrics.MetricAttribute;
import org.lukep.javavis.metrics.MetricRegistry;
import org.lukep.javavis.metrics.MetricThreshold;
import org.lukep.javavis.program.generic.models.ProjectModel;
import org.lukep.javavis.ui.UIMain;
import org.lukep.javavis.util.FormValidationException;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

/**
 * The project threshold configuration panel.
 */
public class ProjectThresholdConfigurationPanel extends
		AbstractConfigurationPanel {
	
	private static final String UNSAVED_ENTITY_TITLE = "New Threshold [Unsaved]";

	private ProjectModel project;
	private Collection<MetricAttribute> metrics;
	
	private MetricThreshold currentThres;

	private JTextField txtName;
	private JTextField txtColdValue;
	private JTextField txtHotValue;

	private JComboBox cmbMetric;

	private JSlider sldCold;
	private JSlider sldHot;
	
	/**
	 * Instantiates a new project threshold configuration panel.
	 *
	 * @param uiMain the ui main
	 * @param project the project
	 */
	public ProjectThresholdConfigurationPanel(UIMain uiMain, ProjectModel project) {
		super(uiMain, UNSAVED_ENTITY_TITLE, true); // defer reloading until we've got a project object set
		hideSaveToSourceButton();

		this.project = project;
		this.metrics = MetricRegistry.getInstance().getMetricAttributes();
		
		for (MetricAttribute m : metrics)
			cmbMetric.addItem(m);
		
		reloadListModel();
		
		setVisible(true);
	}

	/* (non-Javadoc)
	 * @see org.lukep.javavis.ui.swing.configPanes.AbstractConfigurationPanel#reloadListModel()
	 */
	@Override
	protected void reloadListModel() {
		DefaultListModel listModel = getListModel();
		listModel.clear();
		
		for (MetricThreshold threshold : this.project.getMetricThresholds())
			listModel.addElement(threshold);
	}

	/* (non-Javadoc)
	 * @see org.lukep.javavis.ui.swing.configPanes.AbstractConfigurationPanel#initialiseFormControls()
	 */
	@Override
	protected void initialiseFormControls() {
		setFormLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,}));
		
		// name field
		JLabel lblName = new JLabel("Name:");
		addFormControl(lblName, "2, 2, right, default");
		
		txtName = new JTextField();
		addFormControl(txtName, "4, 2, fill, default");
		txtName.setColumns(10);
		
		// metric field
		JLabel lblMetric = new JLabel("Metric:");
		addFormControl(lblMetric, "2, 4, right, default");
		
		cmbMetric = new JComboBox();
		addFormControl(cmbMetric, "4, 4, fill, default");
		cmbMetric.addActionListener(this);
		
		// cold threshold
		JLabel lblColdThreshold = new JLabel("Bound 1:");
		addFormControl(lblColdThreshold, "2, 6");
		
		sldCold = new JSlider(SwingConstants.HORIZONTAL, -100, 100, 0);
		addFormControl(sldCold, "4, 6");
		sldCold.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				txtColdValue.setText(Double.toString(sldCold.getValue()));
			}
		});
		
		// ... cold threshold text box
		txtColdValue = new JTextField();
		addFormControl(txtColdValue, "6, 6, fill, default");
		txtColdValue.setColumns(5);
		
		// hot threshold
		JLabel lblHotThreshold = new JLabel("Bound 2:");
		addFormControl(lblHotThreshold, "2, 8");
		
		sldHot = new JSlider(SwingConstants.HORIZONTAL, -100, 100, 0);
		addFormControl(sldHot, "4, 8");
		sldHot.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				txtHotValue.setText(Double.toString(sldHot.getValue()));
			}
		});
		
		// ... hot threshold text box
		txtHotValue = new JTextField();
		addFormControl(txtHotValue, "6, 8, fill, default");
		txtHotValue.setColumns(5);
	}

	/* (non-Javadoc)
	 * @see org.lukep.javavis.ui.swing.configPanes.AbstractConfigurationPanel#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (cmbMetric == e.getSource()) {
			// set the hot/cold values from the metric's configuration
			MetricAttribute metric = (MetricAttribute) cmbMetric.getSelectedItem();
			if (metric != null)
				setCurrentThresholdValues(metric.getCold(), metric.getHot());
		}
		super.actionPerformed(e);
	}

	/* (non-Javadoc)
	 * @see org.lukep.javavis.ui.swing.configPanes.AbstractConfigurationPanel#getSelectedEntityName()
	 */
	@Override
	protected String getSelectedEntityName() {
		if (currentThres != null)
			return currentThres.getName();
		return "";
	}

	/* (non-Javadoc)
	 * @see org.lukep.javavis.ui.swing.configPanes.AbstractConfigurationPanel#updateFormFields()
	 */
	@Override
	protected void updateFormFields() {
		txtName.setText(currentThres.getName());
		
		// set metric combo box value
		if (metrics.contains(currentThres.getMetric()))
			cmbMetric.setSelectedItem(currentThres.getMetric());
		
		setCurrentThresholdValues(currentThres.getBound1(), currentThres.getBound2());
	}

	/* (non-Javadoc)
	 * @see org.lukep.javavis.ui.swing.configPanes.AbstractConfigurationPanel#resetFormFields()
	 */
	@Override
	protected void resetFormFields() {
		txtName.setText(null);
		cmbMetric.setSelectedItem(null);
		setCurrentThresholdValues(0.0, 0.0);
	}

	/* (non-Javadoc)
	 * @see org.lukep.javavis.ui.swing.configPanes.AbstractConfigurationPanel#setSelectedEntity(java.lang.Object)
	 */
	@Override
	protected void setSelectedEntity(Object entity) {
		if (entity instanceof MetricThreshold) {
			currentThres = (MetricThreshold) entity;
			updateFormFields();
			btnDeleteEntity.setEnabled(true);
		} else {
			currentThres = null;
			resetFormFields();
			btnDeleteEntity.setEnabled(false);
		}
	}

	/* (non-Javadoc)
	 * @see org.lukep.javavis.ui.swing.configPanes.AbstractConfigurationPanel#saveCurrentEntity()
	 */
	@Override
	protected boolean saveCurrentEntity() throws FormValidationException {
		try {
			MetricThreshold newThres = MetricThreshold.validateAndCreateOrUpdate(currentThres, 
					txtName.getText(), ((MetricAttribute)(cmbMetric.getSelectedItem())).getInternalName(),
					Double.parseDouble(txtColdValue.getText()), Double.parseDouble(txtHotValue.getText()));
			if (currentThres != newThres) {
				project.getMetricThresholds().add(newThres);
				reloadListModel();
			}
			setSelectedEntity(newThres);
		} catch (NumberFormatException nfe) {
			JOptionPane.showMessageDialog(this, "Enter a valid Double.", 
					"Form Validation Error", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see org.lukep.javavis.ui.swing.configPanes.AbstractConfigurationPanel#deleteCurrentEntity()
	 */
	@Override
	protected void deleteCurrentEntity() {
		if (currentThres != null) {
			project.getMetricThresholds().remove(currentThres);
			reloadListModel();
		}
	}

	/* (non-Javadoc)
	 * @see org.lukep.javavis.ui.swing.configPanes.AbstractConfigurationPanel#saveAllToSource()
	 */
	@Override
	protected void saveAllToSource() throws Exception {
		// TODO Auto-generated method stub

	}
	
	/**
	 * Sets the current threshold values.
	 *
	 * @param cold the cold
	 * @param hot the hot
	 */
	private void setCurrentThresholdValues(double cold, double hot) {
		// set slider, text boxes
		sldCold.setValue((int) cold);
		sldHot.setValue((int) hot);
		txtColdValue.setText(Double.toString(cold));
		txtHotValue.setText(Double.toString(hot));
	}

}
