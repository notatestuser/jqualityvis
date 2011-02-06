/*
 * AbstractWorkspacePane.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.ui.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.util.Enumeration;

import javax.swing.JDesktopPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.ScrollPaneLayout;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.lukep.javavis.program.generic.models.ClassModel;
import org.lukep.javavis.program.generic.models.ClassModelStore;
import org.lukep.javavis.program.generic.models.MethodModel;
import org.lukep.javavis.ui.IProgramSourceObserver;
import org.lukep.javavis.ui.IProgramStatusReporter;

abstract class AbstractWorkspacePane extends JDesktopPane implements IProgramSourceObserver {
	
	protected JSplitPane rightSplitPane;
	protected JSplitPane outerSplitPane;
	protected JTree programTree;
	protected JScrollPane codeOverviewPanel;
	protected JTabbedPane tabbedPane;
	protected ClassPropertiesPanel propertiesPane;
	
	protected ClassModelStore classModel;
	
	IProgramStatusReporter statusTarget;
	
	public AbstractWorkspacePane(IProgramStatusReporter statusTarget) throws Exception {
		super();
		this.statusTarget = statusTarget;
		
		this.setBackground(Color.WHITE);
		this.setLayout(  new BorderLayout() );
		
		// initialize the class model repository
		classModel = new ClassModelStore();
		
		/*
		 * UI Initialisation
		 */
		// create a panel to contain the left side of the JSplitPane's components
		JPanel leftPane = new JPanel( new BorderLayout() );
		leftPane.setVisible(true);
		
		// create a panel to show the properties of the currently selected class
		propertiesPane = new ClassPropertiesPanel();
		propertiesPane.setVisible(true);
		
		// create the right split pane that contains the graph component on the top and the class
		// properties pane on the bottom
		rightSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, null, propertiesPane);
		rightSplitPane.setOneTouchExpandable(true);
		rightSplitPane.setDividerLocation(500);
		rightSplitPane.setResizeWeight(1);
		rightSplitPane.setDividerSize(6);
		rightSplitPane.setBorder(null);
		
		// create the outer split pane that contains the left (inner) split pane and the graph
		// component on the right side of the window
		outerSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPane, rightSplitPane);
		outerSplitPane.setOneTouchExpandable(true);
		outerSplitPane.setDividerLocation(250);
		outerSplitPane.setDividerSize(6);
		outerSplitPane.setBorder(null);
		this.add(outerSplitPane, BorderLayout.CENTER);
		
		// create the tabbed pane on the left split
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		leftPane.add(tabbedPane, BorderLayout.CENTER);
		
		// create the "Code Overview" tab
		codeOverviewPanel = new JScrollPane();
		tabbedPane.addTab("Project Explorer", null, codeOverviewPanel, null);
		codeOverviewPanel.setLayout(new ScrollPaneLayout());
		
		// create the program tree
		programTree = new JTree();
		programTree.setModel( new DefaultTreeModel( new DefaultMutableTreeNode("Program") ) );
		codeOverviewPanel.setViewportView(programTree);

	}
	
	protected void setGraphComponent(Component graph) {
		rightSplitPane.setLeftComponent(graph);
	}
	
	private IProgramStatusReporter getNearestStatusReporter() throws NullPointerException {
		Component c = getParent();
		
		while ( !(c instanceof IProgramStatusReporter) ) {
			c = c.getParent();
		}
		
		return (IProgramStatusReporter) c;
	}
	
	public void setProgramStatus(String status) {
		statusTarget.setProgramStatus(status);
	}
	
	@Override
	public void notifyFindClass(ClassModel clazz) {
		DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) programTree.getModel().getRoot();
		String packageName = clazz.getPackageName();
		if (packageName.length() > 0) { // class is member of a named package (non-default)
			DefaultMutableTreeNode newPackageNode = new DefaultMutableTreeNode( packageName );
			Enumeration rootNodeChildren = rootNode.children();
			boolean addedToExistingNode = false;
			while (rootNodeChildren.hasMoreElements()) {
				DefaultMutableTreeNode element = (DefaultMutableTreeNode) rootNodeChildren.nextElement();
				if (element.toString().equals(packageName)) { // check if existing package node exists
					element.add( new DefaultMutableTreeNode( clazz.getSimpleName() ) ); // yes, add it to that one
					addClass( clazz, packageName ); // create cell graph
					addedToExistingNode = true;
				}
			}
			if (!addedToExistingNode) {
				rootNode.add( newPackageNode ); // add new package node
				newPackageNode.add( new DefaultMutableTreeNode( clazz.getSimpleName() ) ); // add class sub-node
				addClass( clazz, packageName );
			}
		}
		// TODO add classes in the default package to the tree?
	}
	
	@Override
	public void notifyFindMethod(MethodModel method) {
		// TODO Auto-generated method stub
		
	}
	
	protected abstract void addClass(ClassModel clazz, String packageName);
	
}
