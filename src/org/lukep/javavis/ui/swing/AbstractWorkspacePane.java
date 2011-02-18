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
import java.io.File;
import java.util.Enumeration;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JDesktopPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.ScrollPaneLayout;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.lukep.javavis.metrics.MetricAttribute;
import org.lukep.javavis.program.generic.models.ClassModel;
import org.lukep.javavis.program.generic.models.MethodModel;
import org.lukep.javavis.program.generic.models.ProgramModelStore;
import org.lukep.javavis.program.java.JavaSourceLoaderThread;
import org.lukep.javavis.ui.IProgramSourceObserver;
import org.lukep.javavis.ui.IProgramStatusReporter;
import org.lukep.javavis.util.JavaVisConstants;
import org.lukep.javavis.visualisation.IVisualiser;
import org.lukep.javavis.visualisation.Visualisation;
import org.lukep.javavis.visualisation.VisualisationRegistry;

abstract class AbstractWorkspacePane extends JDesktopPane implements 
		ActionListener, IProgramSourceObserver, IVisualiser {
	
	protected JPanel mainPane;
	protected JPanel leftPaneTop;
	protected JPanel leftPaneBottom;
	
	protected JScrollPane projectExplorerPanel;
	protected JScrollPane metricAnalysisPanel;
	
	protected JSplitPane rightSplitPane;
	protected JSplitPane leftSplitPane;
	protected JSplitPane outerSplitPane;
	
	protected JToolBar toolbar;
	protected JComboBox metricComboBox;
	protected JComboBox visComboBox;
	
	protected JTree programTree;
	
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
		// create a panel to contain the left TOP side of the JSplitPane's components
		leftPaneTop = new JPanel( new BorderLayout() );
		leftPaneTop.setVisible(true);
		
		// create a panel to contain the left BOTTOM side of the JSplitPane's components
		leftPaneBottom = new JPanel( new BorderLayout() );
		leftPaneBottom.setVisible(true);
		
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
		propertiesPane = new ClassPropertiesPanel(wspContext);
		propertiesPane.setVisible(true);
		
		// create the right split pane that contains the graph component on the top and the class
		// properties pane on the bottom
		rightSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, mainPane, propertiesPane);
		rightSplitPane.setOneTouchExpandable(false);
		rightSplitPane.setDividerLocation(500);
		rightSplitPane.setResizeWeight(1);
		rightSplitPane.setDividerSize(2);
		rightSplitPane.setBorder(null);
		
		// create the left split pane that contains the "Project Explorer" and the "Quality Analysis"
		// panels on the left side of the window
		leftSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, leftPaneTop, leftPaneBottom);
		leftSplitPane.setOneTouchExpandable(false);
		leftSplitPane.setDividerLocation(400);
		leftSplitPane.setResizeWeight(1);
		leftSplitPane.setDividerSize(2);
		leftSplitPane.setBorder(null);
		
		// create the outer split pane that contains the left (inner) split pane and the graph
		// component on the right side of the window
		outerSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftSplitPane, rightSplitPane);
		outerSplitPane.setOneTouchExpandable(false);
		outerSplitPane.setDividerLocation(250);
		outerSplitPane.setDividerSize(2);
		outerSplitPane.setBorder(null);
		this.add(outerSplitPane, BorderLayout.CENTER);
		
		// create the "Project Explorer" panel
		projectExplorerPanel = new JScrollPane();
		projectExplorerPanel.setLayout(new ScrollPaneLayout());
		leftPaneTop.add(projectExplorerPanel, BorderLayout.CENTER);
		leftPaneTop.add(new HeaderLabel(JavaVisConstants.HEADING_PROJECT_EXPLORER, 
						new ImageIcon(JavaVisConstants.ICON_PROJECT_EXPLORER)), BorderLayout.NORTH);
		
		// create the "Quality Analysis" panel
		metricAnalysisPanel = new JScrollPane();
		metricAnalysisPanel.setLayout(new ScrollPaneLayout());
		leftPaneBottom.add(metricAnalysisPanel, BorderLayout.CENTER);
		leftPaneBottom.add(new HeaderLabel(JavaVisConstants.HEADING_QUALITY_ANALYSIS, 
						new ImageIcon(JavaVisConstants.ICON_QUALITY_ANALYSIS)), BorderLayout.NORTH);
		
		// create the program tree
		programTree = new JTree();
		programTree.setModel( new DefaultTreeModel( new DefaultMutableTreeNode("Program") ) );
		projectExplorerPanel.setViewportView(programTree);

	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (metricComboBox == e.getSource()
				&& metricComboBox.isEnabled()) {
			
			if (metricComboBox.getSelectedItem() instanceof MetricAttribute) {
				
				MetricAttribute metric = (MetricAttribute) metricComboBox.getSelectedItem();
				
				wspContext.setMetric(metric);
				
				List<Visualisation> visualisations = 
					VisualisationRegistry.getInstance().getVisualisationsByType(metric.getType());
				((VisualisationComboBoxModel)(visComboBox.getModel())).setVisualisations(visualisations);
				visComboBox.setEnabled(true);
				
			} else {
				wspContext.setMetric(null);
				wspContext.setVisualisation(null);
				visComboBox.setEnabled(false);
			}
		} else if (visComboBox == e.getSource()
				&& visComboBox.isEnabled()) {
			
			if (visComboBox.getSelectedItem() instanceof Visualisation) {
				Visualisation vis = (Visualisation) visComboBox.getSelectedItem();
				wspContext.setVisualisation(vis);
				
				try {
					setProgramStatus("Applying Visualisation \"" + vis.getName() + "\"...");
					acceptVisualisation( vis.getVisitorClass().newInstance() );
					setProgramStatus("Applied Visualisation \"" + vis.getName() + "\".");
					
				} catch (Exception e1) {
					setProgramStatus("Error: " + e1.getLocalizedMessage());
				}
			} else {
				wspContext.setVisualisation(null);
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

	protected void setGraphComponent(Component graph) {
		mainPane.add(graph, BorderLayout.CENTER);
	}
	
	@Override
	public void loadCodeBase(File selectedDirectory) {
		JavaSourceLoaderThread jslt = new JavaSourceLoaderThread(selectedDirectory, 
				wspContext.modelStore) {
			
			@Override
			public void notifyStatusChange(String message) {
				setProgramStatus(message);
			}

			@Override
			public void statusFinished() {
				((DefaultTreeModel)programTree.getModel()).reload();
				metricComboBox.setEnabled(true);
			}
			
		};
		jslt.addObserver(this);
		jslt.addObserver(wspContext.modelStore);
		new Thread(jslt).start();
	}
	
	@Override
	public void notifyFindClass(ClassModel clazz) {
		DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) programTree.getModel().getRoot();
		String packageName = clazz.getContainerName();
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
	public WorkspaceContext getContext() {
		return wspContext;
	}
	
	@Override
	public void setProgramStatus(String status) {
		statusTarget.setProgramStatus(status);
	}
	
	@Override
	public void notifyFindMethod(MethodModel method) {
		// TODO Auto-generated method stub
		
	}
	
}
