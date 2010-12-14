/*
 * VisualisationDesktopPane.java (JavaVis)
 * Copyright 2010 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.io.File;
import java.util.Enumeration;
import java.util.HashMap;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.lukep.javavis.visualisation.java.JavaSourceLoaderThread;

import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;
import com.sun.tools.javac.code.Symbol.ClassSymbol;

public class VisualisationDesktopPane extends StatefulWorkspacePane implements IProgramSourceObserver {
	
	private JTree programTree;
	private JScrollPane codeOverviewPanel;
	private JTabbedPane tabbedPane;
	private mxGraph graph;
	
	private int cellX = 250;
	private int cellY = 100;

	public VisualisationDesktopPane(IProgramStatusReporter statusTarget) throws Exception {
		super(statusTarget);
		
		this.setBackground(Color.WHITE);
		this.setLayout(null);
		
		graph = new mxGraph();
		JPanel graphCanvas = new JPanel( new BorderLayout() );
		graphCanvas.add(new mxGraphComponent(graph), BorderLayout.CENTER);
		graphCanvas.setSize(1000, 666);
		this.add(graphCanvas, DEFAULT_LAYER);
		
		JInternalFrame layeredPane = new JInternalFrame();
		layeredPane.setTitle("Toolbox");
		layeredPane.setIconifiable(true);
		layeredPane.setResizable(true);
		layeredPane.setBounds(11, 11, 200, 300);
		layeredPane.setVisible(true);
		this.add(layeredPane, PALETTE_LAYER);
		
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
					addClass( clazz.getSimpleName().toString(), packageName ); // create cell graph
					addedToExistingNode = true;
				}
			}
			if (!addedToExistingNode) {
				rootNode.add( newPackageNode ); // add new package node
				newPackageNode.add( new DefaultMutableTreeNode( clazz.getSimpleName() ) ); // add class sub-node
				addClass( clazz.getSimpleName().toString(), packageName ); // create cell graph
			}
		}
		((DefaultTreeModel)programTree.getModel()).reload();
	}
	
	private HashMap<String, mxCell> packageMap = new HashMap<String, mxCell>();
	private void addClass(String className, String packageName) {
		Object parent = null;
		if (packageMap.containsKey(packageName))
			parent = packageMap.get(packageName);
		else {
			parent = graph.insertVertex(null, null, packageName, 250, 100, 150, 80);
			packageMap.put(packageName, (mxCell)parent);
		}
		graph.insertVertex(parent, null, className, 250, 100, 150, 80);
		cellX += 50;
		cellY += 50;
	}

}
