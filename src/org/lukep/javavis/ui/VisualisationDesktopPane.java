/*
 * VisualisationDesktopPane.java (JavaVis)
 * Copyright 2010 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.ui;

import java.awt.Color;
import java.io.File;
import java.util.Enumeration;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.lukep.javavis.visualisation.java.JavaSourceLoaderThread;

import com.sun.tools.javac.code.Symbol.ClassSymbol;

public class VisualisationDesktopPane extends StatefulWorkspacePane implements IProgramSourceObserver {
	
	private JTree programTree;
	private JScrollPane codeOverviewPanel;
	private JTabbedPane tabbedPane;

	public VisualisationDesktopPane(IProgramStatusReporter statusTarget) throws Exception {
		super(statusTarget);
		
		setBackground(Color.WHITE);
		setLayout(null);
		
		JInternalFrame layeredPane = new JInternalFrame();
		layeredPane.setTitle("Toolbox");
		layeredPane.setIconifiable(true);
		layeredPane.setResizable(true);
		layeredPane.setBounds(11, 11, 200, 300);
		layeredPane.setVisible(true);
		this.add(layeredPane);
		
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(0, 0, 81, 516);
		layeredPane.getContentPane().add(tabbedPane);
		
		codeOverviewPanel = new JScrollPane();
		tabbedPane.addTab("Code Overview", null, codeOverviewPanel, null);
		codeOverviewPanel.setLayout(new ScrollPaneLayout());
		
		programTree = new JTree();
		programTree.setModel( new DefaultTreeModel( new DefaultMutableTreeNode("Program") ) );
		codeOverviewPanel.setViewportView(programTree);
		
		JPanel panel_1 = new JPanel();
		tabbedPane.addTab("Applied Metrics", null, panel_1, null);
	}

	public void loadCodeBase(File selectedDirectory) {
		new Thread(new JavaSourceLoaderThread(selectedDirectory, this) {

			@Override
			public void notifyStatusChange(String message) {
				setProgramStatus(message);
			}

			@Override
			public void statusFinished() {
				// TODO Auto-generated method stub
			}
			
		}).start();
	}

	@Override
	public void notifyFindClass(ClassSymbol clazz) {
		DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) programTree.getModel().getRoot();
		String classQualName = clazz.getQualifiedName().toString();
		int lastDotIndex = classQualName.lastIndexOf('.');
		if (lastDotIndex != -1) { // class is member of a named package (non-default)
			String packageName = classQualName.substring(0, lastDotIndex);
			DefaultMutableTreeNode newPackageNode = new DefaultMutableTreeNode( packageName );
			Enumeration rootNodeChildren = rootNode.children();
			boolean addedToExistingNode = false;
			while (rootNodeChildren.hasMoreElements()) {
				DefaultMutableTreeNode element = (DefaultMutableTreeNode) rootNodeChildren.nextElement();
				if (element.toString().equals(packageName)) { // check if existing package node exists
					element.add( new DefaultMutableTreeNode( clazz.getSimpleName() ) ); // yes, add it to that one
					addedToExistingNode = true;
				}
			}
			if (!addedToExistingNode) {
				rootNode.add( newPackageNode ); // add new package node
				newPackageNode.add( new DefaultMutableTreeNode( clazz.getSimpleName() ) ); // add class sub-node
			}
		}
		((DefaultTreeModel)programTree.getModel()).reload();
	}

}
