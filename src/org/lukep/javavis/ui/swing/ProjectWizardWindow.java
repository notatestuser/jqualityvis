/*
 * ProjectWizardWindow.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.ui.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.lukep.javavis.program.generic.models.ProjectModel;
import org.lukep.javavis.program.java.JavaSourceLoaderThread;
import org.lukep.javavis.ui.UIMain;
import org.lukep.javavis.util.JavaVisConstants;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class ProjectWizardWindow extends JDialog implements ActionListener {

	private ProjectWizardWindow thisInstance;
	
	private JTextField txtProjectName;
	private JTextField txtSourceDirectory;

	private File selectedFile;
	private JButton btnBrowse;
	
	private JButton btnPerformAction;
	private JButton btnCancel;
	
	private boolean btnPerformActionEnabled = false;
	
	private JLabel lblStatus;
	
	private JProgressBar progressBar;
	
	protected UIMain uiInstance;
	
	private KeyListener validationListener = new KeyListener() {
		
		@Override
		public void keyTyped(KeyEvent e) {
			validateForm();
		}
		
		@Override
		public void keyReleased(KeyEvent e) {
		}
		
		@Override
		public void keyPressed(KeyEvent e) {
		}
	};
	
	/**
	 * Create the window.
	 */
	public ProjectWizardWindow(Frame parent, UIMain uiInstance) {
		super(parent, "Create/Open a Project");
		setModal(true);
		setResizable(false);
		setBounds(200, 200, 500, 260);
		setLocationRelativeTo(parent);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		this.thisInstance = this;
		this.uiInstance = uiInstance;
		
		initialize();
	}

	/**
	 * Initialise the contents of the frame.
	 */
	private void initialize() {
		JPanel pnlHeader = new JPanel();
		pnlHeader.setBackground(Color.WHITE);
		getContentPane().add(pnlHeader, BorderLayout.NORTH);
		pnlHeader.setLayout(new BorderLayout(0, 0));
		
		JLabel lblHeader = new JLabel("Create a Project File");
		lblHeader.setFont(new Font("Tahoma", Font.BOLD, 17));
		pnlHeader.add(lblHeader, BorderLayout.CENTER);
		
		JPanel pnlIcon = new JPanel();
		pnlIcon.setBorder(new EmptyBorder(0, 5, 0, 6));
		pnlIcon.setBackground(Color.WHITE);
		pnlHeader.add(pnlIcon, BorderLayout.WEST);
		
		JLabel lblIcon = new JLabel("");
		pnlIcon.add(lblIcon);
		lblIcon.setIcon(new ImageIcon(JavaVisConstants.ICON_PROJECT_WIZARD));
		
		JSeparator separator = new JSeparator();
		pnlHeader.add(separator, BorderLayout.SOUTH);
		
		JPanel pnlForm = new JPanel();
		pnlForm.setBorder(new EmptyBorder(15, 15, 15, 15));
		getContentPane().add(pnlForm, BorderLayout.CENTER);
		pnlForm.setLayout(new FormLayout(new ColumnSpec[] {
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
				FormFactory.DEFAULT_ROWSPEC,}));
		
		JLabel lblProjectName = new JLabel("Project Name:");
		pnlForm.add(lblProjectName, "2, 2, right, default");
		
		txtProjectName = new JTextField();
		pnlForm.add(txtProjectName, "4, 2, fill, default");
		txtProjectName.setColumns(10);
		txtProjectName.addKeyListener(validationListener);
		
		JLabel lblSourceDirectory = new JLabel("Source Root Directory:");
		pnlForm.add(lblSourceDirectory, "2, 4, right, default");
		
		txtSourceDirectory = new JTextField();
		pnlForm.add(txtSourceDirectory, "4, 4, fill, default");
		txtSourceDirectory.setColumns(10);
		txtSourceDirectory.setEditable(false);
		
		btnBrowse = new JButton("Browse...");
		btnBrowse.addActionListener(this);
		pnlForm.add(btnBrowse, "6, 4");
		
		JLabel lblSourceLanguage = new JLabel("Source Language:");
		pnlForm.add(lblSourceLanguage, "2, 6, right, default");
		
		JComboBox cbxSelectedLanguage = new JComboBox();
		cbxSelectedLanguage.setModel(new DefaultComboBoxModel(new String[] {JavaVisConstants.SUPPORTED_LANG_JAVA_6}));
		pnlForm.add(cbxSelectedLanguage, "4, 6, fill, default");
		
		JLabel lblOptions = new JLabel("Options:");
		pnlForm.add(lblOptions, "2, 8, right, default");
		
		JCheckBox chckbxPreloadMetricMeasurements = new JCheckBox("Preload Metric Measurements");
		chckbxPreloadMetricMeasurements.setSelected(true);
		pnlForm.add(chckbxPreloadMetricMeasurements, "4, 8");
		
		JPanel pnlFooter = new JPanel();
		getContentPane().add(pnlFooter, BorderLayout.SOUTH);
		pnlFooter.setLayout(new BorderLayout(0, 0));
		
		progressBar = new JProgressBar();
		pnlFooter.add(progressBar, BorderLayout.NORTH);
		
		JPanel pnlButtons = new JPanel();
		pnlButtons.setBorder(new EmptyBorder(5, 0, 5, 5));
		pnlFooter.add(pnlButtons, BorderLayout.EAST);
		pnlButtons.setLayout(new GridLayout(0, 2, 10, 0));
		
		btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(this);
		pnlButtons.add(btnCancel);
		
		btnPerformAction = new JButton("Create Project");
		btnPerformAction.addActionListener(this);
		btnPerformAction.setEnabled(false);
		pnlButtons.add(btnPerformAction);
		
		JPanel pnlStatus = new JPanel();
		pnlStatus.setBorder(new EmptyBorder(0, 10, 0, 0));
		pnlFooter.add(pnlStatus, BorderLayout.WEST);
		pnlStatus.setLayout(new BorderLayout(0, 0));
		
		lblStatus = new JLabel("");
		pnlStatus.add(lblStatus);
	}
	
	private File browseDirectorySelect() {
		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle("Browse for a directory");
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fc.setMultiSelectionEnabled(false);
		int returnVal = fc.showOpenDialog(null); // TODO: modify parent
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			return fc.getSelectedFile();
		}
		return null;
	}
	
	private void validateForm() {
		if (	// validate project name text length
				txtProjectName.getText().length() > 0
				// validate source directory length
				&& txtSourceDirectory.getText().length() > 0
				// ensure selected file exists
				&& selectedFile != null
				&& selectedFile.exists()) {
			if (!btnPerformActionEnabled) {
				btnPerformAction.setEnabled(true);
				btnPerformActionEnabled = true;
			}
		} else if (btnPerformActionEnabled) {
			btnPerformAction.setEnabled(false);
			btnPerformActionEnabled = false;
		}
	}
	
	private void lockControls() {
		txtProjectName.setEnabled(false);
		btnBrowse.setEnabled(false);
		btnPerformAction.setEnabled(false);
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		try {
			if (btnCancel == e.getSource()) {
				setVisible(false);
				dispose();
			} else if (btnBrowse == e.getSource()) {
				selectedFile = browseDirectorySelect();
				if (selectedFile != null) {
					txtSourceDirectory.setText(selectedFile.getAbsolutePath());
					validateForm();
				}
			} else if (btnPerformAction == e.getSource()) {
				if (selectedFile != null) {
					lockControls();
					loadJavaCodeBase(selectedFile);
				}
			}
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, "Exception: " + ex.getLocalizedMessage(), 
					"An error occurred...", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	public void setProgramStatus(String status, boolean indeterminate, int progress) {
		progressBar.setIndeterminate(indeterminate);
		progressBar.setValue(progress);
		
		lblStatus.setText(status);
		lblStatus.repaint();
		
		uiInstance.setProgramStatus(status);
	}
	
	public void loadJavaCodeBase(File selectedDirectory) throws Exception {
		
		final ProjectModel project = new ProjectModel(txtProjectName.getText());
		
		JavaSourceLoaderThread jslt = new JavaSourceLoaderThread(selectedDirectory, project) {
			
			@Override
			public void notifyStatusChange(String message) {
				setProgramStatus(message, true, 0);
			}

			@Override
			public void statusFinished() {
				WorkspacePane workspace = null;
				try {
					workspace = new WorkspacePane(project, uiInstance);
					uiInstance.addChildWorkspaceFrame(workspace);
					workspace.setVisible(true);
					thisInstance.setVisible(false);
					thisInstance.dispose();
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, "Error creating workspace: " + e.getLocalizedMessage(), 
							"Error", JOptionPane.ERROR_MESSAGE);
				}
			}
			
		};
		//jslt.addObserver(workspace);
		jslt.addObserver(project);
		new Thread(jslt).start();
	}

}
