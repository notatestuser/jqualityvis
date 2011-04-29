/*
 * VisualisationConfigurationPanel.java (JQualityVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.ui.swing.configPanes;

import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.lukep.javavis.ui.UIMain;
import org.lukep.javavis.util.FormValidationException;
import org.lukep.javavis.visualisation.Visualisation;
import org.lukep.javavis.visualisation.VisualisationRegistry;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

/**
 * A configuration panel implementation for Visualisation objects.
 */
public class VisualisationConfigurationPanel extends AbstractConfigurationPanel {
	
	private static final String UNSAVED_ENTITY_TITLE = "New Visualisation [Unsaved]";
	
	private Visualisation currentVis;
	
	private JTextField txtName;
	private JTextField txtType;
	private JTextField txtDescription;
	private JTextField txtIVisualiserClass;
	private JTextField txtIVisualiserVisitorClass;
	private JTextField txtArguments;
	
	/**
	 * Instantiates a new visualisation configuration panel.
	 *
	 * @param uiMain the ui main
	 */
	public VisualisationConfigurationPanel(UIMain uiMain) {
		super(uiMain, UNSAVED_ENTITY_TITLE, false);
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
				ColumnSpec.decode("default:grow"),},
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
				FormFactory.DEFAULT_ROWSPEC,}));
		
		// name
		JLabel lblName = new JLabel("Name:");
		lblName.setHorizontalAlignment(SwingConstants.RIGHT);
		addFormControl(lblName, "2, 2, right, default");
		
		txtName = new JTextField();
		addFormControl(txtName, "4, 2, fill, default");
		txtName.setColumns(10);
		
		// type
		JLabel lblType = new JLabel("Type:");
		lblType.setHorizontalAlignment(SwingConstants.RIGHT);
		addFormControl(lblType, "2, 4, right, default");
		
		txtType = new JTextField();
		addFormControl(txtType, "4, 4, fill, default");
		txtType.setColumns(10);
		
		// description
		JLabel lblDescription = new JLabel("Description:");
		lblDescription.setHorizontalAlignment(SwingConstants.RIGHT);
		addFormControl(lblDescription, "2, 6, right, default");
		
		txtDescription = new JTextField();
		addFormControl(txtDescription, "4, 6, fill, default");
		txtDescription.setColumns(10);
		
		// IVisualiser
		JLabel lblIVisualiserClass = new JLabel("IVisualiser Class:");
		lblIVisualiserClass.setHorizontalAlignment(SwingConstants.RIGHT);
		addFormControl(lblIVisualiserClass, "2, 8, right, default");
		
		txtIVisualiserClass = new JTextField();
		addFormControl(txtIVisualiserClass, "4, 8, fill, default");
		txtIVisualiserClass.setColumns(10);
		
		// IVisualiserVisitor
		JLabel lblIVisualiserVisitorClass = new JLabel("IVisualiserVisitor Class:");
		lblIVisualiserVisitorClass.setHorizontalAlignment(SwingConstants.RIGHT);
		addFormControl(lblIVisualiserVisitorClass, "2, 10, right, default");
		
		txtIVisualiserVisitorClass = new JTextField();
		addFormControl(txtIVisualiserVisitorClass, "4, 10, fill, default");
		txtIVisualiserVisitorClass.setColumns(10);
		
		// arguments
		JLabel lblArguments = new JLabel("Arguments:");
		lblArguments.setHorizontalAlignment(SwingConstants.RIGHT);
		addFormControl(lblArguments, "2, 12, right, default");
		
		txtArguments = new JTextField();
		addFormControl(txtArguments, "4, 12, fill, default");
		txtArguments.setColumns(10);
	}
	
	/* (non-Javadoc)
	 * @see org.lukep.javavis.ui.swing.configPanes.AbstractConfigurationPanel#reloadListModel()
	 */
	@Override
	protected void reloadListModel() {
		DefaultListModel listModel = getListModel();
		listModel.clear();
		for (Vector<Visualisation> visList 
				: VisualisationRegistry.getInstance().getVisualisationMap().values())
			for (Visualisation vis : visList)
				listModel.addElement(vis);
	}
	
	/* (non-Javadoc)
	 * @see org.lukep.javavis.ui.swing.configPanes.AbstractConfigurationPanel#getSelectedEntityName()
	 */
	@Override
	protected String getSelectedEntityName() {
		if (currentVis != null)
			return currentVis.getName();
		return "";
	}

	/* (non-Javadoc)
	 * @see org.lukep.javavis.ui.swing.configPanes.AbstractConfigurationPanel#updateFormFields()
	 */
	@Override
	protected void updateFormFields() {
		setEntityTitle("Visualisation: " + currentVis.getName());
		txtName.setText(currentVis.getName());
		txtType.setText(currentVis.getSource().getType());
		txtDescription.setText(currentVis.getSource().getDescription());
		txtIVisualiserClass.setText(
				currentVis.getSource().getIVisualiser());
		txtIVisualiserVisitorClass.setText(
				currentVis.getSource().getIVisualiserVisitor());
		txtArguments.setText(currentVis.getArguments());
	}

	/* (non-Javadoc)
	 * @see org.lukep.javavis.ui.swing.configPanes.AbstractConfigurationPanel#resetFormFields()
	 */
	@Override
	protected void resetFormFields() {
		setEntityTitle(UNSAVED_ENTITY_TITLE);
		txtName.setText(null);
		txtType.setText(null);
		txtDescription.setText(null);
		txtIVisualiserClass.setText(null);
		txtIVisualiserVisitorClass.setText(null);
		txtArguments.setText(null);
	}

	/* (non-Javadoc)
	 * @see org.lukep.javavis.ui.swing.configPanes.AbstractConfigurationPanel#setSelectedEntity(java.lang.Object)
	 */
	@Override
	protected void setSelectedEntity(Object entity) {
		if (entity instanceof Visualisation) {
			currentVis = (Visualisation) entity;
			updateFormFields();
			btnDeleteEntity.setEnabled(true);
		} else {
			currentVis = null;
			resetFormFields();
			btnDeleteEntity.setEnabled(false);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.lukep.javavis.ui.swing.configPanes.AbstractConfigurationPanel#saveCurrentEntity()
	 */
	@Override
	protected boolean saveCurrentEntity() throws FormValidationException {
		// validate the fields and create our new Visualisation
		Visualisation newVis = Visualisation.validateAndCreateOrUpdate(currentVis, 
				txtName.getText(), 
				txtType.getText(), 
				txtDescription.getText(), 
				txtIVisualiserClass.getText(), 
				txtIVisualiserVisitorClass.getText(), 
				txtArguments.getText());
		
		// if the Visualisation is new, register it with the registry
		if (currentVis != newVis) {
			VisualisationRegistry.getInstance().addVisualisation(newVis);
			reloadListModel();
		}
		
		// select this new entity in the configuration pane
		setSelectedEntity(newVis);
		return true;
	}
	
	/* (non-Javadoc)
	 * @see org.lukep.javavis.ui.swing.configPanes.AbstractConfigurationPanel#deleteCurrentEntity()
	 */
	@Override
	protected void deleteCurrentEntity() {
		if (currentVis != null) {
			VisualisationRegistry.getInstance().deleteVisualisation(currentVis);
			setSelectedEntity(null);
			reloadListModel();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.lukep.javavis.ui.swing.configPanes.AbstractConfigurationPanel#saveAllToSource()
	 */
	@Override
	protected void saveAllToSource() throws Exception {
		VisualisationRegistry.getInstance().saveAllVisualisations();
	}
	
}
