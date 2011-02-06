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

import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
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

import org.lukep.javavis.ui.swing.mxGraphWorkspacePane;

public class UIMain implements IProgramStatusReporter, ChangeListener {

	protected JFrame frmJavavis;
	protected JTabbedPane mainTabbedPane;
	protected JPanel bottomPanel;
	protected JLabel mainStatusBar;
	protected JSlider zoomSlider;
	protected int workspaces = 0;

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
		addChildFrame();
	}

	/**
	 * Initialize the contents of the frame.
	 * @throws Exception 
	 */
	private void initialize() throws Exception {
		frmJavavis = new JFrame();
		frmJavavis.setTitle("JMetricVis Software Quality Visualiser");
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
		// ... File > New Workspace
		JMenuItem mntmNewCanvas = new JMenuItem("New Workspace");
		mntmNewCanvas.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				addChildFrame();
			}
		});
		mnFile.add(mntmNewCanvas);
		// ... File > Close Workspace
		JMenuItem mntmCloseWorkspace = new JMenuItem("Close Workspace");
		mntmCloseWorkspace.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int response = JOptionPane.showConfirmDialog(null, "Are you sure you'd like to close this Workspace?");
				if (response == JOptionPane.YES_OPTION)
					mainTabbedPane.removeTabAt(mainTabbedPane.getSelectedIndex());
			}
		});
		mnFile.add(mntmCloseWorkspace);
		// ... File > Import Program Sources...
		JMenuItem mntmOpenCodeDirectory = new JMenuItem("Import Program Sources...");
		mntmOpenCodeDirectory.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser fc = new JFileChooser();
				fc.setDialogTitle("Select a root directory containing source code");
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				fc.setMultiSelectionEnabled(false);
				int returnVal = fc.showOpenDialog(null); // TODO: modify parent
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					Component selectedWorkspace = mainTabbedPane.getSelectedComponent();
					mxGraphWorkspacePane selectedVdp = (mxGraphWorkspacePane)selectedWorkspace;
					selectedVdp.loadCodeBase(fc.getSelectedFile());
				}
			}
		});
		mnFile.add(mntmOpenCodeDirectory);
		// ... File > Exit
		JMenuItem mntmExit = new JMenuItem("Exit");
		mntmExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});
		mnFile.add(mntmExit);
		// ... Help menu
		JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);
		// ... Help > About
		JMenuItem mntmAbout = new JMenuItem("About");
		mnHelp.add(mntmAbout);
	}
	
	private void addChildFrame()
	{
		mxGraphWorkspacePane visDesktop = null;
		try {
			visDesktop = new mxGraphWorkspacePane(this);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mainTabbedPane.addTab( "Workspace " + (++workspaces), visDesktop );
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
			mxGraphWorkspacePane selectedVdp = 
				(mxGraphWorkspacePane) mainTabbedPane.getSelectedComponent();
			selectedVdp.setGraphScale((double)zoomSlider.getValue() / 1000);
		}
	}
}
