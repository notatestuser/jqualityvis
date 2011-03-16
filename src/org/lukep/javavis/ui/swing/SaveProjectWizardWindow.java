/*
 * SaveProjectWizardWindow.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.ui.swing;

import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.lukep.javavis.program.generic.helpers.ModelSerializer;
import org.lukep.javavis.program.generic.models.ProjectModel;
import org.lukep.javavis.ui.UIMain;
import org.lukep.javavis.util.JavaVisConstants;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class SaveProjectWizardWindow extends AbstractWizardWindow {
	
	private ProjectModel project;
	
	private JTextField txtSaveFilename;
	private JButton btnSaveBrowse;
	private File selectedSaveFile;
	private JComboBox cbxSelectedLanguage;
	private JCheckBox chckbxSerialiseXML;
	private JCheckBox chckbxGZIPCompress;
	
	private final Thread.UncaughtExceptionHandler exHandler = 
		new Thread.UncaughtExceptionHandler() {
		@Override
		public void uncaughtException(Thread t, Throwable e) {
			showError(e.getLocalizedMessage());
		}
	};
	
	public SaveProjectWizardWindow(Frame parent, UIMain uiInstance, ProjectModel project) {
		super(parent, uiInstance, 
				"Save Project '" + project.getSimpleName() + "'",
				new ImageIcon(JavaVisConstants.ICON_PROJECT_WIZARD_SAVE));
		setActionButtonText("Save Project File");
		
		this.project = project;
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
				FormFactory.DEFAULT_ROWSPEC,}));
		
		JLabel lblSaveFilename = new JLabel("File Path:");
		addFormControl(lblSaveFilename, "2, 2, right, default");
		
		txtSaveFilename = new JTextField();
		addFormControl(txtSaveFilename, "4, 2, fill, default");
		txtSaveFilename.setColumns(30);
		txtSaveFilename.addKeyListener(validationListener);
		
		btnSaveBrowse = new JButton("Browse...");
		btnSaveBrowse.addActionListener(this);
		addFormControl(btnSaveBrowse, "6, 2");
		
		JLabel lblSourceLanguage = new JLabel("Options:");
		addFormControl(lblSourceLanguage, "2, 4, right, default");
		
		JPanel pnlOptions = new JPanel(new FlowLayout());
		{
			chckbxSerialiseXML = new JCheckBox("Export as XML");
			chckbxSerialiseXML.setSelected(true);
			pnlOptions.add(chckbxSerialiseXML);
			
			chckbxGZIPCompress = new JCheckBox("GZIP Compress Output");
			chckbxGZIPCompress.setSelected(true);
			pnlOptions.add(chckbxGZIPCompress);
		}
		addFormControl(pnlOptions, "4, 4, fill, default");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (btnSaveBrowse == e.getSource()) {
			selectedSaveFile = browseFileSaveLocation();
			if (selectedSaveFile != null) {
				txtSaveFilename.setText(selectedSaveFile.getAbsolutePath());
				refreshActionable();
			}
		}
		super.actionPerformed(e);
	}

	@Override
	protected boolean validateFormControls() {
		String saveFilename = txtSaveFilename.getText();
		if (saveFilename.length() > 0) {
			selectedSaveFile = new File(txtSaveFilename.getText());
			if (selectedSaveFile.isAbsolute()
					&& !selectedSaveFile.isDirectory())
				return true;
		}
		return false;
	}

	@Override
	protected void lockFormControls() {
		txtSaveFilename.setEnabled(false);
		btnSaveBrowse.setEnabled(false);
		btnPerformAction.setEnabled(false);
	}

	@Override
	protected void unlockFormControls() {
		txtSaveFilename.setEnabled(true);
		btnSaveBrowse.setEnabled(true);
		refreshActionable();
	}

	@Override
	protected boolean performAction() throws Exception {
		
		progressBar.setMaximum(1);
		setProgramStatus("Saving " + selectedSaveFile.getName() + "...", true, 0);
		
		// start the serialisation task in a new thread
		final boolean fxml = chckbxSerialiseXML.isSelected();
		final boolean fgzip = chckbxGZIPCompress.isSelected();
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					ModelSerializer.serializeProjectToFile(selectedSaveFile, project, fxml, fgzip);
				} catch (IOException e) {
					showError(e.getLocalizedMessage());
				}
				notifySerializationComplete();
			}
		});
		t.setUncaughtExceptionHandler(exHandler);
		t.start();
		return true;
	}

	private File browseFileSaveLocation() {
		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle("Save Project");
		int returnVal = fc.showSaveDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION)
			return fc.getSelectedFile();
		return null;
	}
	
	public void notifySerializationComplete() {
		setProgramStatus("Save successful.", false, 1);
		JOptionPane.showMessageDialog(this, "Project saved to " + selectedSaveFile.getName() + ".", 
				"Saved", JOptionPane.INFORMATION_MESSAGE);
		setVisible(false);
		dispose();
	}

}
