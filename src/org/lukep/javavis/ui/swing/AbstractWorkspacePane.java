/*
 * AbstractWorkspacePane.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.ui.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JDesktopPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.ScrollPaneLayout;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.lukep.javavis.metrics.MetricAttribute;
import org.lukep.javavis.program.generic.models.ClassModel;
import org.lukep.javavis.program.generic.models.MethodModel;
import org.lukep.javavis.program.generic.models.ProgramModelStore;
import org.lukep.javavis.ui.IProgramSourceObserver;
import org.lukep.javavis.ui.IProgramStatusReporter;
import org.lukep.javavis.visualisation.IVisualiser;
import org.lukep.javavis.visualisation.Visualisation;
import org.lukep.javavis.visualisation.VisualisationRegistry;

abstract class AbstractWorkspacePane extends JDesktopPane implements 
		ActionListener, IProgramSourceObserver, IVisualiser {
	
	protected JPanel leftPane;
	protected JPanel mainPane;
	
	protected JSplitPane rightSplitPane;
	protected JSplitPane outerSplitPane;
	
	protected JToolBar toolbar;
	protected JComboBox metricComboBox;
	protected JComboBox visComboBox;
	
	protected JTree programTree;
	protected JScrollPane codeOverviewPanel;
	protected JTabbedPane tabbedPane;
	protected ClassPropertiesPanel propertiesPane;
	
	protected WorkspaceContext wspContext = new WorkspaceContext();
	
	protected IProgramStatusReporter statusTarget;
	
	public AbstractWorkspacePane(IProgramStatusReporter statusTarget) throws Exception {
		super();
		this.statusTarget = statusTarget;
		
		setBackground( Color.WHITE );
		setLayout( new BorderLayout() );
		
		// create a new ProgramModelStore in our WorkspaceContext
		wspContext.modelStore = new ProgramModelStore();
		
		/*
		 * UI Initialisation
		 */
		// create a panel to contain the left side of the JSplitPane's components
		leftPane = new JPanel( new BorderLayout() );
		leftPane.setVisible(true);
		
		// create a panel to contain the graph component and toolbar
		mainPane = new JPanel( new BorderLayout() );
		mainPane.setVisible(true);
		
		// create the toolbar panel
		toolbar = new JToolBar();
		mainPane.add(toolbar, BorderLayout.NORTH);
		
		// add the controls to the toolbar
		toolbar.add(new JLabel("Metric: "));
		metricComboBox = new JComboBox( new MetricComboBoxModel() );
		metricComboBox.setEnabled(false);
		metricComboBox.setSelectedIndex(0);
		metricComboBox.addActionListener(this);
		toolbar.add(metricComboBox);
		
		toolbar.add(new JLabel(" Visualisation: "));
		visComboBox = new JComboBox( new VisualisationComboBoxModel() );
		visComboBox.setEnabled(false);
		visComboBox.setSelectedIndex(0);
		visComboBox.addActionListener(this);
		toolbar.add(visComboBox);
		
		// create the properties panel to show the attributes of the currently selected class
		propertiesPane = new ClassPropertiesPanel();
		propertiesPane.setVisible(true);
		
		// create the right split pane that contains the graph component on the top and the class
		// properties pane on the bottom
		rightSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, mainPane, propertiesPane);
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
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (metricComboBox == e.getSource()
				&& metricComboBox.isEnabled()) {
			
			if (metricComboBox.getSelectedItem() instanceof MetricAttribute) {
				
				MetricAttribute metric = (MetricAttribute) metricComboBox.getSelectedItem();
				
				wspContext.metric = metric;
				
				List<Visualisation> visualisations = 
					VisualisationRegistry.getInstance().getVisualisationsByType(metric.getType());
				((VisualisationComboBoxModel)(visComboBox.getModel())).setVisualisations(visualisations);
				visComboBox.setEnabled(true);
				
			} else {
				wspContext.metric = null;
				wspContext.visualisation = null;
				visComboBox.setEnabled(false);
			}
		} else if (visComboBox == e.getSource()
				&& visComboBox.isEnabled()) {
			
			if (visComboBox.getSelectedItem() instanceof Visualisation) {
				Visualisation vis = (Visualisation) visComboBox.getSelectedItem();
				wspContext.visualisation = vis;
				
				try {
					setProgramStatus("Applying Visualisation \"" + vis.getName() + "\"...");
					acceptVisualisation(vis.getVisitorClass().newInstance());
				} catch (InstantiationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IllegalAccessException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} finally {
					setProgramStatus("Applied Visualisation \"" + vis.getName() + "\".");
				}
			} else {
				wspContext.visualisation = null;
			}
		}
	}
	
	private IProgramStatusReporter getNearestStatusReporter() throws NullPointerException {
		Component c = getParent();
		
		while ( !(c instanceof IProgramStatusReporter) ) {
			c = c.getParent();
		}
		
		return (IProgramStatusReporter) c;
	}
	
	public WorkspaceContext getContext() {
		return wspContext;
	}

	protected void setGraphComponent(Component graph) {
		//rightSplitPane.setLeftComponent(graph);
		mainPane.add(graph, BorderLayout.CENTER);
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
					//addClass( clazz, packageName ); // create cell graph
					addedToExistingNode = true;
				}
			}
			if (!addedToExistingNode) {
				rootNode.add( newPackageNode ); // add new package node
				newPackageNode.add( new DefaultMutableTreeNode( clazz.getSimpleName() ) ); // add class sub-node
				//addClass( clazz, packageName );
			}
		}
		// TODO add classes in the default package to the tree?
	}
	
	@Override
	public void notifyFindMethod(MethodModel method) {
		// TODO Auto-generated method stub
		
	}
	
}
