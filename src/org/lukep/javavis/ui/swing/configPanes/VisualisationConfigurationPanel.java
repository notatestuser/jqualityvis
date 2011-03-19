/*
 * VisualisationConfigurationPanel.java (JMetricVis)
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

public class VisualisationConfigurationPanel extends AbstractConfigurationPanel {
	
	private static final String UNSAVED_ENTITY_TITLE = "New Visualisation [Unsaved]";
	
	private DefaultListModel visListModel;
	
	private Visualisation currentVis;
	
	private JTextField txtName;
	private JTextField txtType;
	private JTextField txtIVisualiserClass;
	private JTextField txtIVisualiserVisitorClass;
	private JTextField txtArguments;
	
	public VisualisationConfigurationPanel(UIMain uiMain) {
		super(uiMain, UNSAVED_ENTITY_TITLE);
	}
	
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
				FormFactory.DEFAULT_ROWSPEC,}));
		
		JLabel lblName = new JLabel("Name:");
		lblName.setHorizontalAlignment(SwingConstants.RIGHT);
		addFormControl(lblName, "2, 2, right, default");
		
		txtName = new JTextField();
		addFormControl(txtName, "4, 2, fill, default");
		txtName.setColumns(10);
		
		JLabel lblType = new JLabel("Type:");
		lblType.setHorizontalAlignment(SwingConstants.RIGHT);
		addFormControl(lblType, "2, 4, right, default");
		
		txtType = new JTextField();
		addFormControl(txtType, "4, 4, fill, default");
		txtType.setColumns(10);
		
		JLabel lblIVisualiserClass = new JLabel("IVisualiser Class:");
		lblIVisualiserClass.setHorizontalAlignment(SwingConstants.RIGHT);
		addFormControl(lblIVisualiserClass, "2, 6, right, default");
		
		txtIVisualiserClass = new JTextField();
		addFormControl(txtIVisualiserClass, "4, 6, fill, default");
		txtIVisualiserClass.setColumns(10);
		
		JLabel lblIVisualiserVisitorClass = new JLabel("IVisualiserVisitor Class:");
		lblIVisualiserVisitorClass.setHorizontalAlignment(SwingConstants.RIGHT);
		addFormControl(lblIVisualiserVisitorClass, "2, 8, right, default");
		
		txtIVisualiserVisitorClass = new JTextField();
		addFormControl(txtIVisualiserVisitorClass, "4, 8, fill, default");
		txtIVisualiserVisitorClass.setColumns(10);
		
		JLabel lblArguments = new JLabel("Arguments:");
		lblArguments.setHorizontalAlignment(SwingConstants.RIGHT);
		addFormControl(lblArguments, "2, 10, right, default");
		
		txtArguments = new JTextField();
		addFormControl(txtArguments, "4, 10, fill, default");
		txtArguments.setColumns(10);
	}
	
	@Override
	protected void reloadListModel() {
		DefaultListModel listModel = getListModel();
		listModel.clear();
		for (Vector<Visualisation> visList 
				: VisualisationRegistry.getInstance().getVisualisationMap().values())
			for (Visualisation vis : visList)
				listModel.addElement(vis);
	}
	
	private void updateFormFields() {
		setEntityTitle("Visualisation: " + currentVis.getName());
		txtName.setText(currentVis.getName());
		txtType.setText(currentVis.getSource().getType());
		txtIVisualiserClass.setText(
				currentVis.getSource().getIVisualiser());
		txtIVisualiserVisitorClass.setText(
				currentVis.getSource().getIVisualiserVisitor());
		txtArguments.setText(currentVis.getArguments());
	}
	
	private void resetFormFields() {
		setEntityTitle(UNSAVED_ENTITY_TITLE);
		txtName.setText(null);
		txtType.setText(null);
		txtIVisualiserClass.setText(null);
		txtIVisualiserVisitorClass.setText(null);
		txtArguments.setText(null);
	}
	
	@Override
	protected String getSelectedEntityName() {
		if (currentVis != null)
			return currentVis.getName();
		return "";
	}

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
	
	@Override
	protected void saveCurrentEntity() throws FormValidationException {
		Visualisation newVis = Visualisation.validateAndCreateOrUpdate(currentVis, txtName.getText(), 
				txtType.getText(), txtIVisualiserClass.getText(), txtIVisualiserVisitorClass.getText(), 
				txtArguments.getText());
		if (currentVis != newVis) {
			VisualisationRegistry.getInstance().addVisualisation(newVis);
			reloadListModel();
		}
		setSelectedEntity(newVis);
	}
	
	@Override
	protected void deleteCurrentEntity() {
		if (currentVis != null) {
			VisualisationRegistry.getInstance().deleteVisualisation(currentVis);
			setSelectedEntity(null);
			reloadListModel();
		}
	}
	
	@Override
	protected void saveAllToSource() throws Exception {
		VisualisationRegistry.getInstance().saveAllVisualisations();
	}
	
}
