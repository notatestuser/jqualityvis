/*
 * UIMain.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.lukep.javavis.ui.swing.MetricCalculatorDialog;
import org.lukep.javavis.ui.swing.WorkspacePane;
import org.lukep.javavis.ui.swing.configPanes.MetricConfigurationPanel;
import org.lukep.javavis.ui.swing.configPanes.VisualisationConfigurationPanel;
import org.lukep.javavis.ui.swing.wizards.NewProjectWizardWindow;
import org.lukep.javavis.ui.swing.wizards.OpenProjectWizardWindow;
import org.lukep.javavis.ui.swing.wizards.SaveProjectWizardWindow;
import org.lukep.javavis.util.JavaVisConstants;
import org.lukep.javavis.visualisation.visualisers.IVisualiser;

public class UIMain implements IProgramStatusReporter, ChangeListener {
	
	private JFrame frmJavavis;
	private JTabbedPane mainTabbedPane;
	private JPanel bottomPanel;
	private JLabel mainStatusBar;
	private JSlider zoomSlider;
	private int workspaces = 0;
	
	private JMenuItem mntmSaveProject;
	private JMenuItem mntmCloseTab;
	private JMenuItem mntmBatchCalc;
	
	private ActionListener actionCreateProject = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			NewProjectWizardWindow pww = new NewProjectWizardWindow(frmJavavis, UIMain.this);
			pww.setVisible(true);
		}
	};
	
	private ActionListener actionOpenProject = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			OpenProjectWizardWindow opw = new OpenProjectWizardWindow(frmJavavis, UIMain.this);
			opw.setVisible(true);
		}
	};
	
	private ActionListener actionSaveProject = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			WorkspacePane selectedWorkspace = (WorkspacePane) mainTabbedPane.getSelectedComponent();
			SaveProjectWizardWindow spw = new SaveProjectWizardWindow(frmJavavis, UIMain.this, 
					selectedWorkspace.getContext().getModelStore());
			spw.setVisible(true);
		}
	};
	
	private ActionListener actionCloseTab = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			int response = -1;
			if (mainTabbedPane.getSelectedComponent() instanceof IVisualiser) {
				response = JOptionPane.showConfirmDialog(null, "Are you sure you'd like to close this Workspace?");
				if (response != JOptionPane.YES_OPTION)
					return;
			}
			if (mainTabbedPane.getTabCount() > 0)
				mainTabbedPane.removeTabAt(mainTabbedPane.getSelectedIndex());
		}
	};
	
	private ActionListener actionMetricConfig = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			JDialog dialog = new JDialog(frmJavavis, "Metric Manager");
			dialog.setBounds(200, 200, 500, 300);
			dialog.setLocationRelativeTo(frmJavavis);
			dialog.getContentPane().add(new MetricConfigurationPanel(UIMain.this));
			dialog.setModal(true);
			dialog.pack();
			dialog.setVisible(true);
		}
	};
	
	private ActionListener actionVisualisationConfig = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			JDialog dialog = new JDialog(frmJavavis, "Visualisation Manager");
			dialog.setBounds(200, 200, 500, 300);
			dialog.setLocationRelativeTo(frmJavavis);
			dialog.getContentPane().add(new VisualisationConfigurationPanel(UIMain.this));
			dialog.setModal(true);
			dialog.pack();
			dialog.setVisible(true);
		}
	};
	
	private ActionListener actionBatchCalc = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			MetricCalculatorDialog mcd = new MetricCalculatorDialog(
					(WorkspacePane) mainTabbedPane.getSelectedComponent());
			mcd.setVisible(true);
		}
	};
	
	private ActionListener actionHelp = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				addChildHTMLFrame("Help Page", 
						(new java.io.File(JavaVisConstants.HELP_HTML_URL)).toURI().toURL());
			} catch (MalformedURLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	};
	
	private ActionListener actionAbout = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			JOptionPane.showMessageDialog(null, 
					"<html><p>"+JavaVisConstants.APP_NAME+" " + 
					JavaVisConstants.APP_VERSION + "</p>" +
					"<p>Copyright © 2011 Luke T. Plaster</p><br />" +
					"<p>" + JavaVisConstants.APP_WEBPAGE + "</p></html>",
					"About " + JavaVisConstants.APP_NAME, JOptionPane.INFORMATION_MESSAGE);
		}
	};
	
	private ActionListener actionExit = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			System.exit(0);
		}
	};

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {

				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					//PlafOptions.setCurrentTheme(new VistaTheme());
					//PlafOptions.setAsLookAndFeel();
					
					
					UIMain window = new UIMain();
					window.frmJavavis.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 * @throws Exception 
	 */
	public UIMain() throws Exception {
		initialize();
		addChildHTMLFrame("Welcome Page", 
				(new java.io.File(JavaVisConstants.WELCOME_HTML_URL)).toURI().toURL());
	}

	/**
	 * Initialise the contents of the frame.
	 * @throws Exception 
	 */
	private void initialize() throws Exception {
		frmJavavis = new JFrame();
		frmJavavis.setTitle(JavaVisConstants.APP_NAME + " Software Quality Visualiser");
		frmJavavis.setBounds(100, 100, 1280, 768);
		frmJavavis.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frmJavavis.setVisible(true);
		frmJavavis.getContentPane().setLayout(new BorderLayout(0, 0));
		
		// initialize main tabbed pane
		mainTabbedPane = new JTabbedPane();
		mainTabbedPane.addChangeListener(this);
		frmJavavis.getContentPane().add(mainTabbedPane);
		mainTabbedPane.setBackground(Color.LIGHT_GRAY);
		
		// initialize bottom panel
		bottomPanel = new JPanel( new BorderLayout() );
		mainStatusBar = new JLabel("Ready!");
		mainStatusBar.setBorder( BorderFactory.createEmptyBorder(0, 10, 0, 0) );
		bottomPanel.add(mainStatusBar, BorderLayout.CENTER);
		zoomSlider = new JSlider(0, 1000, 1000);
		zoomSlider.addChangeListener(this);
		bottomPanel.add(zoomSlider, BorderLayout.EAST);
		frmJavavis.getContentPane().add(bottomPanel, BorderLayout.SOUTH);
		
		// initialize menu bar and actions
		JMenuBar menuBar = new JMenuBar();
		frmJavavis.setJMenuBar(menuBar);
		
		// ... File menu
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		{
			// ... File > Create a New Project...
			JMenuItem mntmCreateProject = new JMenuItem("Create a New Project...", 
					new ImageIcon(JavaVisConstants.ICON_MENU_PROJECT_CREATE));
			mntmCreateProject.addActionListener(actionCreateProject);
			mnFile.add(mntmCreateProject);
			
			mnFile.addSeparator();
			
			// ... File > Open a Project...
			JMenuItem mntmOpenProject = new JMenuItem("Open Project...", 
					new ImageIcon(JavaVisConstants.ICON_MENU_PROJECT_OPEN));
			mntmOpenProject.addActionListener(actionOpenProject);
			mnFile.add(mntmOpenProject);
			
			// ... File > Save Project...
			mntmSaveProject = new JMenuItem("Save Project...", 
					new ImageIcon(JavaVisConstants.ICON_MENU_PROJECT_SAVE));
			mntmSaveProject.addActionListener(actionSaveProject);
			mnFile.add(mntmSaveProject);
			mntmSaveProject.setEnabled(false);
			
			// ... File > Close Tab
			mntmCloseTab = new JMenuItem("Close Tab", 
					new ImageIcon(JavaVisConstants.ICON_MENU_PROJECT_CLOSE));
			mntmCloseTab.addActionListener(actionCloseTab);
			mnFile.add(mntmCloseTab);
			
			mnFile.addSeparator();
			
			// ... File > Exit
			JMenuItem mntmExit = new JMenuItem("Exit");
			mntmExit.addActionListener(actionExit);
			mnFile.add(mntmExit);
		}
		
		// ... Settings menu
		JMenu mnSettings = new JMenu("Settings");
		menuBar.add(mnSettings);
		{
			// ... Settings > Metrics...
			JMenuItem mntmMetricMgr = new JMenuItem("Edit Metrics...");
			mntmMetricMgr.addActionListener(actionMetricConfig);
			mnSettings.add(mntmMetricMgr);
			
			// ... Settings > Visualisations...
			JMenuItem mntmVisMgr = new JMenuItem("Edit Visualisations...");
			mntmVisMgr.addActionListener(actionVisualisationConfig);
			mnSettings.add(mntmVisMgr);
		}
		
		// ... Tools menu
		JMenu mnTools = new JMenu("Tools");
		menuBar.add(mnTools);
		{
			// ... Tools > Batch Metric Calculator...
			mntmBatchCalc = new JMenuItem("Batch Metric Calculator...");
			mntmBatchCalc.addActionListener(actionBatchCalc);
			mnTools.add(mntmBatchCalc);
			mntmBatchCalc.setEnabled(false);
		}
		
		// ... Help menu
		JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);
		{
			// ... Help > Help
			JMenuItem mntmHelp = new JMenuItem("Help...", 
					new ImageIcon(JavaVisConstants.ICON_MENU_HELP));
			mntmHelp.addActionListener(actionHelp);
			mnHelp.add(mntmHelp);
			
			// ... Help > About
			JMenuItem mntmAbout = new JMenuItem("About " + JavaVisConstants.APP_NAME + "...");
			mntmAbout.addActionListener(actionAbout);
			mnHelp.add(mntmAbout);
		}
	}
	
	private void addChildHTMLFrame(String title, URL url) {
		JEditorPane newPane = null;
		try {
			newPane = new JEditorPane(url);
			newPane.setEditable(false);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (newPane != null) {
			mainTabbedPane.addTab(title, newPane);
			mainTabbedPane.setSelectedComponent(newPane);
		}
	}
	
	public void addChildWorkspaceFrame(WorkspacePane workspace) {
		mainTabbedPane.addTab( "Workspace " + (++workspaces) 
				+ " (" + workspace.getContext().getModelStore() + ")", (Component) workspace );
		mainTabbedPane.setSelectedIndex(mainTabbedPane.getTabCount()-1);
	}

	@Override
	public synchronized void setProgramStatus(String status) {
		mainStatusBar.setText(status);
		mainStatusBar.repaint();
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		if (mainTabbedPane == e.getSource()) {
			Component selectedTab = mainTabbedPane.getSelectedComponent();
			
			// "Close Current Tab" action availability handling
			if (selectedTab == null)
				mntmCloseTab.setEnabled(false);
			else
				mntmCloseTab.setEnabled(true);
			
			// "Save Project" and "Batch Metric Calculator" action availability handling
			if (selectedTab instanceof WorkspacePane) {
				mntmSaveProject.setEnabled(true);
				mntmBatchCalc.setEnabled(true);
			} else {
				mntmSaveProject.setEnabled(false);
				mntmBatchCalc.setEnabled(false);
			}
			
		} else if (zoomSlider == e.getSource()
				&& mainTabbedPane.getSelectedComponent() instanceof WorkspacePane) {
			WorkspacePane selectedWorkspace = 
				(WorkspacePane) mainTabbedPane.getSelectedComponent();
			
			selectedWorkspace.setVisualisationScale((double)zoomSlider.getValue() / 1000);
		}
	}
	
	public void refreshWorkspaceMetricTrees() {
		for (Component c : mainTabbedPane.getComponents()) {
			if (c instanceof WorkspacePane) {
				((WorkspacePane)(c)).fillMetricTree();
			}
		}
	}
}
