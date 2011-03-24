/*
 * MetricConfigurationPanel.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.ui.swing.configPanes;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.lukep.javavis.metrics.MetricAttribute;
import org.lukep.javavis.metrics.MetricRegistry;
import org.lukep.javavis.metrics.qualityModels.DesignQualityAttribute;
import org.lukep.javavis.ui.UIMain;
import org.lukep.javavis.util.FormValidationException;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class MetricConfigurationPanel extends AbstractConfigurationPanel {

	private static final String UNSAVED_ENTITY_TITLE = "New Metric [Unsaved]";
	
	private MetricAttribute currentMetric;
	
	private JTextField txtName;
	private JTextField txtInternalName;
	private JTextField txtType;
	private JTextField txtAppliesTo;
	private JTextField txtIMeasurableVisitorClass;
	private JTextField txtArguments;
	private JSlider sldCold;
	private JSlider sldHot;

	private JTextField txtColdBox;

	private JTextField txtHotBox;
	
	public MetricConfigurationPanel(UIMain uiMain) {
		super(uiMain, UNSAVED_ENTITY_TITLE);
	}

	@Override
	protected void reloadListModel() {
		DefaultListModel listModel = getListModel();
		listModel.clear();
		for (MetricAttribute ma : 
			MetricRegistry.getInstance().getMetricAttributes()) {
			// exclude design attributes (this interface doesn't deal with 'em)
			if ( !(ma instanceof DesignQualityAttribute) )
				listModel.addElement(ma);
		}
	}

	@Override
	protected void initialiseFormControls() {
		setFormLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,}));
		
		// name
		JLabel lblName = new JLabel("Name:");
		lblName.setHorizontalAlignment(SwingConstants.RIGHT);
		addFormControl(lblName, "2, 2, right, default");
		
		txtName = new JTextField();
		addFormControl(txtName, "4, 2, fill, default");
		txtName.setColumns(10);
		
		// internal name
		JLabel lblIntName = new JLabel("Internal Name:");
		lblIntName.setHorizontalAlignment(SwingConstants.RIGHT);
		addFormControl(lblIntName, "2, 4, right, default");
		
		txtInternalName = new JTextField();
		addFormControl(txtInternalName, "4, 4, fill, default");
		txtInternalName.setColumns(10);
		
		// type
		JLabel lblType = new JLabel("Type:");
		lblType.setHorizontalAlignment(SwingConstants.RIGHT);
		addFormControl(lblType, "2, 6, right, default");
		
		txtType = new JTextField();
		addFormControl(txtType, "4, 6, fill, default");
		txtType.setColumns(10);
		
		// applies to
		JLabel lblAppliesTo = new JLabel("Applies to models:");
		lblAppliesTo.setHorizontalAlignment(SwingConstants.RIGHT);
		addFormControl(lblAppliesTo, "2, 8, right, default");
		
		txtAppliesTo = new JTextField();
		addFormControl(txtAppliesTo, "4, 8, fill, default");
		txtAppliesTo.setColumns(10);
		
		// IMeasurableVisitor
		JLabel lblIMeasurableVisitor = new JLabel("IMeasurableVisitor Class:");
		lblIMeasurableVisitor.setHorizontalAlignment(SwingConstants.RIGHT);
		addFormControl(lblIMeasurableVisitor, "2, 10, right, default");
		
		txtIMeasurableVisitorClass = new JTextField();
		addFormControl(txtIMeasurableVisitorClass, "4, 10, fill, default");
		txtIMeasurableVisitorClass.setColumns(10);
		
		// argument
		JLabel lblArguments = new JLabel("Arguments:");
		lblArguments.setHorizontalAlignment(SwingConstants.RIGHT);
		addFormControl(lblArguments, "2, 12, right, default");
		
		txtArguments = new JTextField();
		addFormControl(txtArguments, "4, 12, fill, default");
		txtArguments.setColumns(10);
		
		// cold
		JLabel lblColdValue = new JLabel("Cold threshold: ");
		lblColdValue.setHorizontalAlignment(SwingConstants.RIGHT);
		addFormControl(lblColdValue, "2, 14, fill, default");
		
		sldCold = new JSlider(SwingConstants.HORIZONTAL, -100, 100, 0);
		addFormControl(sldCold, "4, 14, fill, default");
		sldCold.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				txtColdBox.setText(Double.toString(sldCold.getValue()));
			}
		});
		
		// ... cold value textbox
		txtColdBox = new JTextField("0.0");
		addFormControl(txtColdBox, "6, 14, fill, default");
		txtColdBox.setColumns(5);
		
		// hot
		JLabel lblHotValue = new JLabel("Hot threshold: ");
		lblHotValue.setHorizontalAlignment(SwingConstants.RIGHT);
		addFormControl(lblHotValue, "2, 16, fill, default");
		
		sldHot = new JSlider(SwingConstants.HORIZONTAL, -100, 100, 0);
		addFormControl(sldHot, "4, 16, fill, default");
		sldHot.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				txtHotBox.setText(Double.toString(sldHot.getValue()));
			}
		});
		
		// ... hot value textbox
		txtHotBox = new JTextField("0.0");
		addFormControl(txtHotBox, "6, 16, fill, default");
		txtHotBox.setColumns(5);
	}
	
	@Override
	protected String getSelectedEntityName() {
		if (currentMetric != null)
			return currentMetric.getName();
		return "";
	}
	
	@Override
	protected void updateFormFields() {
		setEntityTitle("Metric: " + currentMetric.getName());
		txtName.setText(currentMetric.getName());
		txtInternalName.setText(currentMetric.getInternalName());
		txtType.setText(currentMetric.getType().getName());
		txtArguments.setText(currentMetric.getArgument());
		
		sldCold.setValue((int) currentMetric.getCold());
		sldHot.setValue((int) currentMetric.getHot());
		txtColdBox.setText(Double.toString(currentMetric.getCold()));
		txtHotBox.setText(Double.toString(currentMetric.getHot()));
		
		if (currentMetric.getSource() != null) {
			txtIMeasurableVisitorClass.setText(
					currentMetric.getSource().getVisitor());
		} else {
			txtIMeasurableVisitorClass.setText(null);
		}
		
		// set the "applies to" field by turning the collected the array into a string
		StringBuilder sb = new StringBuilder();
		for (String at : currentMetric.getAppliesTo())
			sb.append(at + ",");
		String str = sb.toString();
		txtAppliesTo.setText(str.substring(0, str.length() - 1));
	}

	@Override
	protected void resetFormFields() {
		setEntityTitle(UNSAVED_ENTITY_TITLE);
		txtName.setText(null);
		txtInternalName.setText(null);
		txtType.setText(null);
		txtAppliesTo.setText(null);
		txtIMeasurableVisitorClass.setText(null);
		txtArguments.setText(null);
		sldCold.setValue(0);
		sldHot.setValue(0);
		txtColdBox.setText("0");
		txtHotBox.setText("0");
	}

	@Override
	protected void setSelectedEntity(Object entity) {
		if (entity instanceof MetricAttribute) {
			currentMetric = (MetricAttribute) entity;
			updateFormFields();
			btnDeleteEntity.setEnabled(true);
		} else {
			currentMetric = null;
			resetFormFields();
			btnDeleteEntity.setEnabled(false);
		}
	}
	
	@Override
	protected boolean saveCurrentEntity() throws FormValidationException {
		try {
			MetricAttribute newMetric = MetricAttribute.validateAndCreateOrUpdate(currentMetric, txtName.getText(), 
					txtInternalName.getText(), txtType.getText(), txtAppliesTo.getText().split(","), 
					txtIMeasurableVisitorClass.getText(), 
					txtArguments.getText(), Double.parseDouble(
							txtColdBox.getText()), Double.parseDouble(txtHotBox.getText()));
			if (currentMetric != newMetric) {
				MetricRegistry.getInstance().registerMetric(newMetric);
				reloadListModel();
			}
			setSelectedEntity(newMetric);
		} catch (NumberFormatException nfe) {
			JOptionPane.showMessageDialog(this, "Enter a valid Double.", 
					"Form Validation Error", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		return true;
	}
	
	@Override
	protected void deleteCurrentEntity() {
		if (currentMetric != null) {
			MetricRegistry.getInstance().deleteMetric(currentMetric);
			setSelectedEntity(null);
			reloadListModel();
		}
	}
	
	@Override
	protected void saveAllToSource() throws Exception {
		MetricRegistry.getInstance().saveAllMetrics();
	}

}
