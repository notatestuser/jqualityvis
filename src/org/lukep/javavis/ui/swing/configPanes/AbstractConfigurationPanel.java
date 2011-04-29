/*
 * AbstractConfigurationPanel.java (JQualityVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.ui.swing.configPanes;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.lukep.javavis.ui.UIMain;
import org.lukep.javavis.util.FormValidationException;
import org.lukep.javavis.util.JavaVisConstants;

/**
 * The Class AbstractConfigurationPanel.
 */
abstract class AbstractConfigurationPanel extends JPanel implements 
		ActionListener, ListDataListener, ListSelectionListener {
	
	private UIMain uiMain;
	private DefaultListModel listModel;
	private JList listEntities;
	private JLabel lblEntitiesCount;
	private JButton btnSaveToSource;
	private JButton btnNewEntity;
	private JLabel lblEntityTitle;
	private JPanel pnlForm;
	protected JButton btnDeleteEntity;
	protected JButton btnSaveEntity;
	
	/**
	 * Instantiates a new abstract configuration panel.
	 *
	 * @param uiMain the ui main
	 * @param unsavedEntityName the unsaved entity name
	 * @param deferReload the defer reload
	 */
	public AbstractConfigurationPanel(UIMain uiMain, String unsavedEntityName, boolean deferReload) {
		setLayout(new BorderLayout(0, 0));
		
		this.uiMain = uiMain;
		
		initialise(unsavedEntityName);
		initialiseFormControls();
		if (!deferReload)
			reloadListModel();
	}
	
	/**
	 * Initialise.
	 *
	 * @param unsavedEntityName the unsaved entity name
	 */
	private void initialise(String unsavedEntityName) {
		JPanel leftPane = new JPanel();
		add(leftPane, BorderLayout.WEST);
		leftPane.setLayout(new BorderLayout(0, 0));
		
		Component paddingStrut = Box.createHorizontalStrut(190);
		leftPane.add(paddingStrut, BorderLayout.NORTH);
		
		lblEntitiesCount = new JLabel();
		lblEntitiesCount.setFont(new Font("Tahoma", Font.PLAIN, 10));
		lblEntitiesCount.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 0));
		leftPane.add(lblEntitiesCount, BorderLayout.NORTH);
		
		listModel = new DefaultListModel();
		listEntities = new JList(listModel);
		listEntities.setFont(new Font("Tahoma", Font.PLAIN, 12));
		listEntities.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		listEntities.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listEntities.getModel().addListDataListener(this);
		listEntities.addListSelectionListener(this);
		JScrollPane listScroller = new JScrollPane(listEntities);
		listScroller.setPreferredSize(new Dimension(250, 400));
		leftPane.add(listScroller, BorderLayout.CENTER);
		
		JPanel listButtonPane = new JPanel();
		leftPane.add(listButtonPane, BorderLayout.SOUTH);
		
		btnSaveToSource = new JButton("Commit to Disk");
		btnSaveToSource.setIcon(new ImageIcon(JavaVisConstants.ICON_MENU_PROJECT_SAVE));
		btnSaveToSource.addActionListener(this);
		listButtonPane.add(btnSaveToSource);
		
		btnNewEntity = new JButton("New");
		btnNewEntity.setIcon(new ImageIcon(JavaVisConstants.ICON_MENU_PROJECT_CREATE));
		btnNewEntity.addActionListener(this);
		listButtonPane.add(btnNewEntity);
		
		JPanel mainPane = new JPanel();
		add(mainPane, BorderLayout.CENTER);
		mainPane.setLayout(new BorderLayout(0, 0));
		
		// --- end left list panel and main panel, begin form
		
		pnlForm = new JPanel();
		mainPane.add(pnlForm, BorderLayout.CENTER);
		pnlForm.setBorder(new EmptyBorder(0, 15, 15, 15));
		pnlForm.setPreferredSize(new Dimension(500, 200));
		
		// --- end form, begin main button pane and title
		
		JPanel mainButtonPane = new JPanel();
		FlowLayout fl_mainButtonPane = (FlowLayout) mainButtonPane.getLayout();
		fl_mainButtonPane.setAlignment(FlowLayout.RIGHT);
		mainPane.add(mainButtonPane, BorderLayout.SOUTH);
		
		btnDeleteEntity = new JButton("Delete Entity");
		btnDeleteEntity.setIcon(new ImageIcon(JavaVisConstants.ICON_CONFIG_DELETE_ENT));
		btnDeleteEntity.addActionListener(this);
		btnDeleteEntity.setEnabled(false);
		mainButtonPane.add(btnDeleteEntity);
		
		btnSaveEntity = new JButton("Save Entity");
		btnSaveEntity.setIcon(new ImageIcon(JavaVisConstants.ICON_CONFIG_SAVE_ENT));
		btnSaveEntity.addActionListener(this);
		mainButtonPane.add(btnSaveEntity);
		
		JPanel mainTitlePane = new JPanel();
		mainTitlePane.setBorder(new EmptyBorder(10, 10, 10, 10));
		mainPane.add(mainTitlePane, BorderLayout.NORTH);
		mainTitlePane.setLayout(new BorderLayout(0, 0));
		
		lblEntityTitle = new JLabel(unsavedEntityName);
		lblEntityTitle.setFont(new Font("Tahoma", Font.BOLD, 11));
		mainTitlePane.add(lblEntityTitle);
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.JComponent#setVisible(boolean)
	 */
	@Override
	public void setVisible(boolean arg0) {
		System.out.println("setVisible");
		super.setVisible(arg0);
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (btnNewEntity == e.getSource()) {
			setSelectedEntity(null);
			listEntities.setSelectedIndices(new int[] {});
		} else if (btnSaveEntity == e.getSource()) {
			try {
				if (saveCurrentEntity()) {
					uiMain.refreshWorkspaceMetricTrees();
					JOptionPane.showMessageDialog(this, "'"+getSelectedEntityName()+"' saved/updated in memory.", 
							"Saved", JOptionPane.INFORMATION_MESSAGE);
				}
			} catch (FormValidationException ex) {
				JOptionPane.showMessageDialog(this, ex.getMessage(), 
						"Form Validation Error", JOptionPane.ERROR_MESSAGE);
			}
		} else if (btnDeleteEntity == e.getSource()) {
			int r = JOptionPane.showConfirmDialog(this, "Delete '"+getSelectedEntityName()+"' from memory?", 
					"Deletion Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
			if (r == JOptionPane.YES_OPTION) {
				deleteCurrentEntity();
				uiMain.refreshWorkspaceMetricTrees();
			}
		} else if (btnSaveToSource == e.getSource()) {
			int r = JOptionPane.showConfirmDialog(this, 
					"This will overwrite the current configuration file containing these entities.\r\n"
					+ "Make a backup if you would like to keep the original file.\r\n\r\nProceed?", 
					"Overwrite Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
			if (r == JOptionPane.YES_OPTION)
				try {
					saveAllToSource();
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(this, ex.getMessage(), 
							"Export Error", JOptionPane.ERROR_MESSAGE);
				}
		}
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.event.ListDataListener#contentsChanged(javax.swing.event.ListDataEvent)
	 */
	@Override
	public void contentsChanged(ListDataEvent e) {
	}

	/* (non-Javadoc)
	 * @see javax.swing.event.ListDataListener#intervalAdded(javax.swing.event.ListDataEvent)
	 */
	@Override
	public void intervalAdded(ListDataEvent e) {
		lblEntitiesCount.setText(listEntities.getModel().getSize() + " item(s) in program memory");
	}

	/* (non-Javadoc)
	 * @see javax.swing.event.ListDataListener#intervalRemoved(javax.swing.event.ListDataEvent)
	 */
	@Override
	public void intervalRemoved(ListDataEvent e) {
	}

	/* (non-Javadoc)
	 * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
	 */
	@Override
	public void valueChanged(ListSelectionEvent e) {

		if (listEntities == e.getSource()
				&& listEntities.getSelectedValue() != null)
			setSelectedEntity(listEntities.getSelectedValue());
	}
	
	/**
	 * Gets the list model.
	 *
	 * @return the list model
	 */
	protected DefaultListModel getListModel() {
		return listModel;
	}
	
	/**
	 * Sets the entity title.
	 *
	 * @param title the new entity title
	 */
	protected void setEntityTitle(String title) {
		lblEntityTitle.setText(title);
	}
	
	/**
	 * Sets the form layout.
	 *
	 * @param layout the new form layout
	 */
	protected void setFormLayout(LayoutManager layout) {
		pnlForm.setLayout(layout);
	}
	
	/**
	 * Adds the form control.
	 *
	 * @param comp the comp
	 * @param constraints the constraints
	 */
	protected void addFormControl(Component comp, Object constraints) {
		pnlForm.add(comp, constraints);
	}
	
	/**
	 * Hide save to source button.
	 */
	protected void hideSaveToSourceButton() {
		btnSaveToSource.setVisible(false);
	}
	
	/**
	 * Reload list model.
	 */
	protected abstract void reloadListModel();
	
	/**
	 * Initialise form controls.
	 */
	protected abstract void initialiseFormControls();
	
	/**
	 * Gets the selected entity name.
	 *
	 * @return the selected entity name
	 */
	protected abstract String getSelectedEntityName();
	
	/**
	 * Update form fields.
	 */
	protected abstract void updateFormFields();
	
	/**
	 * Reset form fields.
	 */
	protected abstract void resetFormFields();
	
	/**
	 * Sets the selected entity.
	 *
	 * @param entity the new selected entity
	 */
	protected abstract void setSelectedEntity(Object entity);
	
	/**
	 * Save current entity.
	 *
	 * @return true, if successful
	 * @throws FormValidationException the form validation exception
	 */
	protected abstract boolean saveCurrentEntity() throws FormValidationException;
	
	/**
	 * Delete current entity.
	 */
	protected abstract void deleteCurrentEntity();
	
	/**
	 * Save all to source.
	 *
	 * @throws Exception the exception
	 */
	protected abstract void saveAllToSource() throws Exception;
	
}
