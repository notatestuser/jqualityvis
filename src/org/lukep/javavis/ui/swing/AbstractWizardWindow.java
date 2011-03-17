/*
 * AbstractWizardWindow.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.ui.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.border.EmptyBorder;

import org.lukep.javavis.program.generic.helpers.MetricPreloader;
import org.lukep.javavis.program.generic.models.ProjectModel;
import org.lukep.javavis.ui.UIMain;

@SuppressWarnings("serial")
abstract class AbstractWizardWindow extends JDialog implements ActionListener {

	AbstractWizardWindow thisInstance;
	UIMain uiInstance;
	
	private JPanel pnlForm;
	
	protected JProgressBar progressBar;
	
	protected JButton btnPerformAction;
	protected boolean btnPerformActionEnabled = false;
	
	private JButton btnCancel;
	
	private JLabel lblStatus;
	
	protected KeyListener validationListener = new KeyListener() {
		@Override
		public void keyTyped(KeyEvent e) {
		}
		@Override
		public void keyReleased(KeyEvent e) {
			refreshActionable();
		}
		@Override
		public void keyPressed(KeyEvent e) {
		}
	};
	
	protected final Thread.UncaughtExceptionHandler exHandler = 
		new Thread.UncaughtExceptionHandler() {
		@Override
		public void uncaughtException(Thread t, Throwable e) {
			showError(e.getLocalizedMessage());
		}
	};
	
	public AbstractWizardWindow(Frame parent, UIMain uiInstance, String title, ImageIcon icon) {
		super(parent, title);
		setModal(true);
		setBounds(200, 200, 500, 300);
		setLocationRelativeTo(parent);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		this.thisInstance = this;
		this.uiInstance = uiInstance;
		
		initialise(title, icon);
		initialiseFormControls();
		pack();
	}
	
	private void initialise(String title, ImageIcon icon) {
		JPanel pnlHeader = new JPanel();
		pnlHeader.setBackground(Color.WHITE);
		getContentPane().add(pnlHeader, BorderLayout.NORTH);
		pnlHeader.setLayout(new BorderLayout(0, 0));
		
		JLabel lblHeader = new JLabel(title);
		lblHeader.setFont(new Font("Tahoma", Font.BOLD, 17));
		pnlHeader.add(lblHeader, BorderLayout.CENTER);
		
		JPanel pnlIcon = new JPanel();
		pnlIcon.setBorder(new EmptyBorder(0, 5, 0, 6));
		pnlIcon.setBackground(Color.WHITE);
		pnlHeader.add(pnlIcon, BorderLayout.WEST);
		
		JLabel lblIcon = new JLabel("");
		pnlIcon.add(lblIcon);
		lblIcon.setIcon(icon);
		
		JSeparator separator = new JSeparator();
		pnlHeader.add(separator, BorderLayout.SOUTH);
		
		// --- end header, begin form
		
		pnlForm = new JPanel();
		pnlForm.setBorder(new EmptyBorder(15, 15, 15, 15));
		JScrollPane spForm = new JScrollPane(pnlForm);
		spForm.setBorder(BorderFactory.createEmptyBorder());
		getContentPane().add(spForm, BorderLayout.CENTER);
		
		// --- end form, begin footer
		
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
		
		btnPerformAction = new JButton("Confirm Action") {
			@Override
			public void setEnabled(boolean arg0) {
				super.setEnabled(arg0);
				btnPerformActionEnabled = arg0;
			}
		};
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
	
	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			if (btnCancel == e.getSource()) {
				setVisible(false);
			} else if (btnPerformAction == e.getSource()) {
				if (validateFormControls()) {
					lockFormControls();
					if (!performAction())
						unlockFormControls();
				}
			}
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, "Exception: " + ex.getLocalizedMessage(), 
					"An error occurred!", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	protected void setFormLayout(LayoutManager layout) {
		pnlForm.setLayout(layout);
	}
	
	protected void addFormControl(Component comp, Object constraints) {
		pnlForm.add(comp, constraints);
	}
	
	protected void setActionButtonText(String text) {
		btnPerformAction.setText(text);
	}
	
	protected void setProgramStatus(String status, boolean indeterminate, int progress) {
		progressBar.setIndeterminate(indeterminate);
		progressBar.setValue(progress);
		
		lblStatus.setText(status);
		lblStatus.repaint();
		
		uiInstance.setProgramStatus(status);
	}
	
	protected void refreshActionable() {
		if (validateFormControls()) {
			if (!btnPerformActionEnabled) {
				btnPerformAction.setEnabled(true);
			}
		} else if (btnPerformActionEnabled) {
			btnPerformAction.setEnabled(false);
		}
	}
	
	protected void showError(String message) {
		JOptionPane.showMessageDialog(this, message, "Something went awry!", 
				JOptionPane.ERROR_MESSAGE);
		
		// reset progress bar and unlock form controls
		setProgramStatus("", false, 0);
		unlockFormControls();
	}
	
	protected void createWorkspaceAndClose(ProjectModel project) {
		WorkspacePane workspace = null;
		try {
			workspace = new WorkspacePane(project, uiInstance);
			uiInstance.addChildWorkspaceFrame(workspace);
			workspace.setVisible(true);
			setVisible(false);
			dispose();
		} catch (Exception e) {
			showError("Error creating workspace: " + e.getLocalizedMessage());
		}
	}
	
	protected void preloadMetrics(ProjectModel project) {
		setProgramStatus("Preloading metric measurements...", false, 0);
		MetricPreloader.preloadMetricMeasurements(project, progressBar.getModel());
	}
	
	protected abstract void initialiseFormControls();
	protected abstract boolean validateFormControls();
	protected abstract void lockFormControls();
	protected abstract void unlockFormControls();
	protected abstract boolean performAction() throws Exception;

}
