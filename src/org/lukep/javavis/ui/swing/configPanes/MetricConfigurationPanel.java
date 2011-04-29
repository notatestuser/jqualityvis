/*
 * MetricConfigurationPanel.java (JQualityVis)
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

/**
 * The Class MetricConfigurationPanel.
 */
public class MetricConfigurationPanel extends AbstractConfigurationPanel {

	private static final String UNSAVED_ENTITY_TITLE = "New Metric [Unsaved]";
	
	private MetricAttribute currentMetric;
	
	private JTextField txtName;
	private JTextField txtInternalName;
	private JTextField txtType;
	private JTextField txtCharacteristic;
	private JTextField txtDescription;
	private JTextField txtAppliesTo;
	private JTextField txtIMeasurableVisitorClass;
	private JTextField txtArguments;
	
	private JSlider sldCold;
	private JSlider sldHot;
	
	private JTextField txtColdBox;
	private JTextField txtHotBox;
	
	/**
	 * Instantiates a new metric configuration panel.
	 *
	 * @param uiMain the ui main
	 */
	public MetricConfigurationPanel(UIMain uiMain) {
		super(uiMain, UNSAVED_ENTITY_TITLE, false);
	}

	/* (non-Javadoc)
	 * @see org.lukep.javavis.ui.swing.configPanes.AbstractConfigurationPanel#reloadListModel()
	 */
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
		txtName.setColumns(50);
		
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
		
		// characteristic
		JLabel lblCharacteristic = new JLabel("Characteristic:");
		lblCharacteristic.setHorizontalAlignment(SwingConstants.RIGHT);
		addFormControl(lblCharacteristic, "2, 8, right, default");
		
		txtCharacteristic = new JTextField();
		addFormControl(txtCharacteristic, "4, 8, fill, default");
		txtCharacteristic.setColumns(10);
		
		// description
		JLabel lblDescription = new JLabel("Description:");
		lblDescription.setHorizontalAlignment(SwingConstants.RIGHT);
		addFormControl(lblDescription, "2, 10, right, default");
		
		txtDescription = new JTextField();
		addFormControl(txtDescription, "4, 10, fill, default");
		txtDescription.setColumns(10);
		
		// applies to
		JLabel lblAppliesTo = new JLabel("Applies to models:");
		lblAppliesTo.setHorizontalAlignment(SwingConstants.RIGHT);
		addFormControl(lblAppliesTo, "2, 12, right, default");
		
		txtAppliesTo = new JTextField();
		addFormControl(txtAppliesTo, "4, 12, fill, default");
		txtAppliesTo.setColumns(10);
		
		// IMeasurableVisitor
		JLabel lblIMeasurableVisitor = new JLabel("IMeasurableVisitor Class:");
		lblIMeasurableVisitor.setHorizontalAlignment(SwingConstants.RIGHT);
		addFormControl(lblIMeasurableVisitor, "2, 14, right, default");
		
		txtIMeasurableVisitorClass = new JTextField();
		addFormControl(txtIMeasurableVisitorClass, "4, 14, fill, default");
		txtIMeasurableVisitorClass.setColumns(10);
		
		// argument
		JLabel lblArguments = new JLabel("Arguments:");
		lblArguments.setHorizontalAlignment(SwingConstants.RIGHT);
		addFormControl(lblArguments, "2, 16, right, default");
		
		txtArguments = new JTextField();
		addFormControl(txtArguments, "4, 16, fill, default");
		txtArguments.setColumns(10);
		
		// cold
		JLabel lblColdValue = new JLabel("Default bound 1 threshold: ");
		lblColdValue.setHorizontalAlignment(SwingConstants.RIGHT);
		addFormControl(lblColdValue, "2, 18, fill, default");
		
		sldCold = new JSlider(SwingConstants.HORIZONTAL, -100, 100, 0);
		addFormControl(sldCold, "4, 18, fill, default");
		sldCold.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				txtColdBox.setText(Double.toString(sldCold.getValue()));
			}
		});
		
		// ... cold value textbox
		txtColdBox = new JTextField("0.0");
		addFormControl(txtColdBox, "6, 18, fill, default");
		txtColdBox.setColumns(5);
		
		// hot
		JLabel lblHotValue = new JLabel("Default bound 1 threshold: ");
		lblHotValue.setHorizontalAlignment(SwingConstants.RIGHT);
		addFormControl(lblHotValue, "2, 20, fill, default");
		
		sldHot = new JSlider(SwingConstants.HORIZONTAL, -100, 100, 0);
		addFormControl(sldHot, "4, 20, fill, default");
		sldHot.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				txtHotBox.setText(Double.toString(sldHot.getValue()));
			}
		});
		
		// ... hot value textbox
		txtHotBox = new JTextField("0.0");
		addFormControl(txtHotBox, "6, 20, fill, default");
		txtHotBox.setColumns(5);
	}
	
	/* (non-Javadoc)
	 * @see org.lukep.javavis.ui.swing.configPanes.AbstractConfigurationPanel#getSelectedEntityName()
	 */
	@Override
	protected String getSelectedEntityName() {
		if (currentMetric != null)
			return currentMetric.getName();
		return "";
	}
	
	/* (non-Javadoc)
	 * @see org.lukep.javavis.ui.swing.configPanes.AbstractConfigurationPanel#updateFormFields()
	 */
	@Override
	protected void updateFormFields() {
		setEntityTitle("Metric: " + currentMetric.getName());
		txtName.setText(currentMetric.getName());
		txtInternalName.setText(currentMetric.getInternalName());
		txtType.setText(currentMetric.getType().getName());
		txtCharacteristic.setText(currentMetric.getCharacteristic());
		txtDescription.setText(currentMetric.getDescription());
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

	/* (non-Javadoc)
	 * @see org.lukep.javavis.ui.swing.configPanes.AbstractConfigurationPanel#resetFormFields()
	 */
	@Override
	protected void resetFormFields() {
		setEntityTitle(UNSAVED_ENTITY_TITLE);
		txtName.setText(null);
		txtInternalName.setText(null);
		txtType.setText(null);
		txtCharacteristic.setText(null);
		txtDescription.setText(null);
		txtAppliesTo.setText(null);
		txtIMeasurableVisitorClass.setText(null);
		txtArguments.setText(null);
		sldCold.setValue(0);
		sldHot.setValue(0);
		txtColdBox.setText("0");
		txtHotBox.setText("0");
	}

	/* (non-Javadoc)
	 * @see org.lukep.javavis.ui.swing.configPanes.AbstractConfigurationPanel#setSelectedEntity(java.lang.Object)
	 */
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
	
	/* (non-Javadoc)
	 * @see org.lukep.javavis.ui.swing.configPanes.AbstractConfigurationPanel#saveCurrentEntity()
	 */
	@Override
	protected boolean saveCurrentEntity() throws FormValidationException {
		try {
			// validate the fields and create our new MetricAttribute
			MetricAttribute newMetric = MetricAttribute.validateAndCreateOrUpdate(
					currentMetric, txtName.getText(), 
					txtInternalName.getText(),
					txtType.getText(),
					txtCharacteristic.getText(),
					txtDescription.getText(),
					txtAppliesTo.getText().split(","),
					txtIMeasurableVisitorClass.getText(),
					txtArguments.getText(), 
					Double.parseDouble(txtColdBox.getText()),
					Double.parseDouble(txtHotBox.getText()));
			
			// if the metric is new, register it with the registry
			if (currentMetric != newMetric) {
				MetricRegistry.getInstance().registerMetric(newMetric);
				reloadListModel();
			}
			
			// select this new entity in the configuration pane
			setSelectedEntity(newMetric);
			
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
		if (currentMetric != null) {
			MetricRegistry.getInstance().deleteMetric(currentMetric);
			setSelectedEntity(null);
			reloadListModel();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.lukep.javavis.ui.swing.configPanes.AbstractConfigurationPanel#saveAllToSource()
	 */
	@Override
	protected void saveAllToSource() throws Exception {
		MetricRegistry.getInstance().saveAllMetrics();
	}

}
