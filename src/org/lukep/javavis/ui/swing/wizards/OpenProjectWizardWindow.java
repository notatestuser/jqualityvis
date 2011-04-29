/*
 * OpenProjectWizardWindow.java (JQualityVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.ui.swing.wizards;

import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
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

/**
 * The Class OpenProjectWizardWindow.
 */
@SuppressWarnings("serial")
public class OpenProjectWizardWindow extends AbstractWizardWindow {

	private JTextField txtOpenFilename;
	private JButton btnOpenBrowse;
	private File selectedFile;
	private JCheckBox chckbxUnserialiseXML;
	private JCheckBox chckbxGZIPDecompress;
	private JCheckBox chckbxPreloadMetricMeasurements;
	private Thread workerThread;
	
	/**
	 * Instantiates a new OpenProjectWizardWindow.
	 *
	 * @param parent the parent
	 * @param uiInstance the ui instance
	 */
	public OpenProjectWizardWindow(Frame parent, UIMain uiInstance) {
		super(parent, uiInstance, 
				"Open an Existing Project",
				new ImageIcon(JavaVisConstants.ICON_PROJECT_WIZARD_OPEN));
		setActionButtonText("Load Project File");
	}
	
	/* (non-Javadoc)
	 * @see java.awt.Window#dispose()
	 */
	@Override
	public void dispose() {
		//if (workerThread != null)
		//	workerThread.stop();
		super.dispose();
	}

	/* (non-Javadoc)
	 * @see org.lukep.javavis.ui.swing.wizards.AbstractWizardWindow#initialiseFormControls()
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
				FormFactory.DEFAULT_ROWSPEC,}));
		
		JLabel lblOpenFilename = new JLabel("File Path:");
		addFormControl(lblOpenFilename, "2, 2, right, default");
		
		txtOpenFilename = new JTextField();
		addFormControl(txtOpenFilename, "4, 2, fill, default");
		txtOpenFilename.setColumns(30);
		txtOpenFilename.addKeyListener(validationListener);
		
		btnOpenBrowse = new JButton("Browse...");
		btnOpenBrowse.addActionListener(this);
		addFormControl(btnOpenBrowse, "6, 2");
		
		JLabel lblOptions = new JLabel("Options:");
		addFormControl(lblOptions, "2, 4, right, default");
		
		JPanel pnlOptions = new JPanel(new GridLayout(1, 2));
		{
			chckbxUnserialiseXML = new JCheckBox("Import as XML");
			chckbxUnserialiseXML.setSelected(false);
			pnlOptions.add(chckbxUnserialiseXML);
			
			chckbxGZIPDecompress = new JCheckBox("GZIP Decompress Source");
			chckbxGZIPDecompress.setSelected(true);
			pnlOptions.add(chckbxGZIPDecompress);
		}
		addFormControl(pnlOptions, "4, 4, fill, default");
		
		chckbxPreloadMetricMeasurements = new JCheckBox("Preload Metric Measurements");
		chckbxPreloadMetricMeasurements.setSelected(true);
		addFormControl(chckbxPreloadMetricMeasurements, "4, 6");
	}

	/* (non-Javadoc)
	 * @see org.lukep.javavis.ui.swing.wizards.AbstractWizardWindow#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (btnOpenBrowse == e.getSource()) {
			selectedFile = browseFileOpenLocation();
			if (selectedFile != null) {
				txtOpenFilename.setText(selectedFile.getAbsolutePath());
				refreshActionable();
			}
		}
		super.actionPerformed(e);
	}

	/* (non-Javadoc)
	 * @see org.lukep.javavis.ui.swing.wizards.AbstractWizardWindow#validateFormControls()
	 */
	@Override
	protected boolean validateFormControls() {
		String saveFilename = txtOpenFilename.getText();
		if (saveFilename.length() > 0) {
			selectedFile = new File(txtOpenFilename.getText());
			if (selectedFile.isAbsolute()
					&& !selectedFile.isDirectory())
				return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see org.lukep.javavis.ui.swing.wizards.AbstractWizardWindow#lockFormControls()
	 */
	@Override
	protected void lockFormControls() {
		txtOpenFilename.setEnabled(false);
		btnOpenBrowse.setEnabled(false);
		btnPerformAction.setEnabled(false);
	}

	/* (non-Javadoc)
	 * @see org.lukep.javavis.ui.swing.wizards.AbstractWizardWindow#unlockFormControls()
	 */
	@Override
	protected void unlockFormControls() {
		txtOpenFilename.setEnabled(true);
		btnOpenBrowse.setEnabled(true);
		refreshActionable();
	}

	/* (non-Javadoc)
	 * @see org.lukep.javavis.ui.swing.wizards.AbstractWizardWindow#performAction()
	 */
	@Override
	protected boolean performAction() throws Exception {
		progressBar.setMaximum(1);
		setProgramStatus("Opening " + selectedFile.getName() + "...", true, 0);
		
		// start the serialisation task in a new thread
		final boolean fxml = chckbxUnserialiseXML.isSelected();
		final boolean fgzip = chckbxGZIPDecompress.isSelected();
		workerThread = new Thread(new Runnable() {
			@Override
			public void run() {
				ProjectModel project = null;
				try {
					project = ModelSerializer.loadProjectFromFile(selectedFile, fxml, fgzip, 
							progressBar.getModel());
					notifyLoadComplete(project);
				} catch (Exception e) {
					showError(e.getMessage());
					e.printStackTrace();
				}
			}
		});
		workerThread.setUncaughtExceptionHandler(exHandler);
		workerThread.start();
		return true;
	}
	
	/**
	 * Browse for the location to open the serialised ProjectModel from.
	 *
	 * @return the file
	 */
	private File browseFileOpenLocation() {
		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle("Open Project");
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setMultiSelectionEnabled(false);
		int returnVal = fc.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION)
			return fc.getSelectedFile();
		return null;
	}
	
	/**
	 * Notify that the process has completed successfully.
	 *
	 * @param project the Project
	 */
	public void notifyLoadComplete(ProjectModel project) {
		if (project != null) {
			if (chckbxPreloadMetricMeasurements.isSelected())
				preloadMetrics(project);
			setProgramStatus("Loaded project " + project.getSimpleName() + " successfully.", 
					false, progressBar.getMaximum());
			createWorkspaceAndClose(project);
			return;
		}
		showError("Project load routine returned null - please check input file.");
	}

}
