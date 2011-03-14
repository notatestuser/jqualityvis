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

import org.lukep.javavis.ui.swing.ProjectWizardWindow;
import org.lukep.javavis.util.JavaVisConstants;
import org.lukep.javavis.visualisation.IVisualiser;

public class UIMain implements IProgramStatusReporter, ChangeListener {

	protected JFrame frmJavavis;
	protected JTabbedPane mainTabbedPane;
	protected JPanel bottomPanel;
	protected JLabel mainStatusBar;
	protected JSlider zoomSlider;
	protected int workspaces = 0;
	
	protected UIMain thisInstance;
	
	private ActionListener actionCreateProject = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			ProjectWizardWindow pww = new ProjectWizardWindow(frmJavavis, thisInstance);
			pww.setVisible(true);
		}
	};
	
	private ActionListener actionCloseWorkspace = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			int response = -1;
			if (mainTabbedPane.getSelectedComponent() instanceof IVisualiser) {
				response = JOptionPane.showConfirmDialog(null, "Are you sure you'd like to close this Workspace?");
				if (response != JOptionPane.YES_OPTION)
					return;
			}
			mainTabbedPane.removeTabAt(mainTabbedPane.getSelectedIndex());
		}
	};
	
	private ActionListener actionHelp = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				addChildHTMLFrame("Help", 
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
		//addChildWorkspaceFrame();
		addChildHTMLFrame("Welcome", 
				(new java.io.File(JavaVisConstants.WELCOME_HTML_URL)).toURI().toURL());
		
		thisInstance = this;
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
		
		// ... File > Create a New Project...
		JMenuItem mntmCreateProject = new JMenuItem("Create a New Project...", 
				new ImageIcon(JavaVisConstants.ICON_MENU_PROJECT_CREATE));
		mntmCreateProject.addActionListener(actionCreateProject);
		mnFile.add(mntmCreateProject);
		
		// ... File > Close Workspace
		JMenuItem mntmCloseWorkspace = new JMenuItem("Close Workspace", 
				new ImageIcon(JavaVisConstants.ICON_MENU_PROJECT_CLOSE));
		mntmCloseWorkspace.addActionListener(actionCloseWorkspace);
		mnFile.add(mntmCloseWorkspace);
		
		// ... File > Exit
		JMenuItem mntmExit = new JMenuItem("Exit");
		mntmExit.addActionListener(actionExit);
		mnFile.add(mntmExit);
		
		// ... Help menu
		JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);
		
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
	
	public void addChildWorkspaceFrame(IVisualiser visDesktop) {
		mainTabbedPane.addTab( "Workspace " + (++workspaces), (Component) visDesktop );
		mainTabbedPane.setSelectedIndex(mainTabbedPane.getTabCount()-1);
	}

	@Override
	public synchronized void setProgramStatus(String status) {
		mainStatusBar.setText(status);
		mainStatusBar.repaint();
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		if (zoomSlider == e.getSource()) {
			IVisualiser selectedVdp = 
				(IVisualiser) mainTabbedPane.getSelectedComponent();
			selectedVdp.setGraphScale((double)zoomSlider.getValue() / 1000);
		}
	}
}
