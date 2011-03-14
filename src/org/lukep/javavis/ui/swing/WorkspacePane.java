/*
 * AbstractWorkspacePane.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.ui.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
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
import javax.swing.border.Border;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;

import org.lukep.javavis.metrics.MetricAttribute;
import org.lukep.javavis.metrics.MetricRegistry;
import org.lukep.javavis.metrics.qualityModels.QualityModel;
import org.lukep.javavis.program.generic.models.ClassModel;
import org.lukep.javavis.program.generic.models.MethodModel;
import org.lukep.javavis.program.generic.models.ProjectModel;
import org.lukep.javavis.ui.IProgramSourceObserver;
import org.lukep.javavis.ui.IProgramStatusReporter;
import org.lukep.javavis.ui.swing.WorkspaceContext.ChangeEvent;
import org.lukep.javavis.util.JavaVisConstants;
import org.lukep.javavis.visualisation.Visualisation;
import org.lukep.javavis.visualisation.VisualisationRegistry;
import org.lukep.javavis.visualisation.views.IVisualiserVisitor;
import org.lukep.javavis.visualisation.visualisers.IVisualiser;

public class WorkspacePane extends JDesktopPane implements 
		Observer, ActionListener, TreeSelectionListener, IProgramSourceObserver {
	
	private static final int DECOY_BACKGROUND_COLOR_RGB = 0xFFF9FFFB;
	
	class WorkspaceSplitPaneUI extends BasicSplitPaneUI {
		@Override
		public BasicSplitPaneDivider createDefaultDivider() {
			return new BasicSplitPaneDivider(this) {
				@Override
				public void setBorder(Border border) { }
			};
		}
	}
	
	protected JPanel mainPane;
	protected JPanel leftPaneTop;
	protected JPanel leftPaneBottom;
	
	protected JScrollPane projectExplorerPanel;
	protected JScrollPane metricSelectionPanel;
	
	protected JSplitPane rightSplitPane;
	protected JSplitPane leftSplitPane;
	protected JSplitPane outerSplitPane;
	
	protected JToolBar toolbar;
	protected JComboBox metricComboBox;
	protected JComboBox visComboBox;
	
	protected JTree programTree;
	protected JTree metricTree;
	
	protected IVisualiser curVisualiser;
	protected Component curVisualiserComponent;
	
	protected ClassPropertiesPanel propertiesPane;
	
	protected IProgramStatusReporter statusTarget;
	
	protected WorkspaceContext wspContext;
	
	public WorkspacePane(ProjectModel project, IProgramStatusReporter statusTarget) throws Exception {
		super();
		this.statusTarget = statusTarget;
		
		setBackground( Color.WHITE );
		setLayout( new BorderLayout() );
		
		// initialise the WorkspaceContext
		wspContext = new WorkspaceContext();
		wspContext.addObserver(this);
		
		// create a new ProgramModelStore in our WorkspaceContext
		wspContext.modelStore = project;
		
		initialize();
	}
	
	private void initialize() {
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
		rightSplitPane.setOneTouchExpandable(true);
		rightSplitPane.setDividerLocation(500);
		rightSplitPane.setResizeWeight(1);
		rightSplitPane.setDividerSize(2);
		rightSplitPane.setUI(new WorkspaceSplitPaneUI());
		rightSplitPane.setBorder(null);
		
		// create the left split pane that contains the "Project Explorer" and the "Quality Analysis"
		// panels on the left side of the window
		leftSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, leftPaneTop, leftPaneBottom);
		leftSplitPane.setOneTouchExpandable(false);
		leftSplitPane.setDividerLocation(400);
		leftSplitPane.setResizeWeight(1);
		leftSplitPane.setDividerSize(2);
		leftSplitPane.setUI(new WorkspaceSplitPaneUI());
		leftSplitPane.setBorder(null);
		
		// create the outer split pane that contains the left (inner) split pane and the graph
		// component on the right side of the window
		outerSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftSplitPane, rightSplitPane);
		outerSplitPane.setOneTouchExpandable(true);
		outerSplitPane.setDividerLocation(250);
		outerSplitPane.setDividerSize(2);
		outerSplitPane.setBorder(null);
		outerSplitPane.setUI(new WorkspaceSplitPaneUI());
		this.add(outerSplitPane, BorderLayout.CENTER);
		
		// create the "Project Explorer" panel
		projectExplorerPanel = new JScrollPane();
		projectExplorerPanel.setLayout(new ScrollPaneLayout());
		leftPaneTop.add(projectExplorerPanel, BorderLayout.CENTER);
		leftPaneTop.add(new HeaderLabel(JavaVisConstants.HEADING_PROJECT_EXPLORER, 
						new ImageIcon(JavaVisConstants.ICON_PROJECT_EXPLORER)), BorderLayout.NORTH);
		
		// create the "Quality Analysis" panel
		JTabbedPane qualityAnalysisPane = new JTabbedPane(JTabbedPane.BOTTOM);
		leftPaneBottom.add(qualityAnalysisPane, BorderLayout.CENTER);
		leftPaneBottom.add(new HeaderLabel(JavaVisConstants.HEADING_QUALITY_ANALYSIS, 
						new ImageIcon(JavaVisConstants.ICON_QUALITY_ANALYSIS)), BorderLayout.NORTH);
		
		// ... create the "Metrics" tab + tree
		metricSelectionPanel = new JScrollPane();
		metricSelectionPanel.setLayout(new ScrollPaneLayout());
		qualityAnalysisPane.add("Metrics", metricSelectionPanel);
		qualityAnalysisPane.addTab(JavaVisConstants.HEADING_METRICS, 
				new ImageIcon(JavaVisConstants.ICON_METRICS), metricSelectionPanel);
		
		// ... create the "Warnings" tab
		qualityAnalysisPane.addTab(JavaVisConstants.HEADING_WARNINGS, 
				new ImageIcon(JavaVisConstants.ICON_WARNINGS), new JPanel());
		
		// create the program tree
		programTree = new JTree();
		programTree.setModel( new DefaultTreeModel( new DefaultMutableTreeNode("Program") ) );
		projectExplorerPanel.setViewportView(programTree);
		
		// create the "Metrics" tree
		metricTree = new JTree();
		DefaultTreeModel treeModel = new DefaultTreeModel( new DefaultMutableTreeNode("root") );
		metricTree.setModel( treeModel );
		metricTree.setRootVisible(false);
		metricTree.addTreeSelectionListener(this);
		MutableTreeNode staticMetricsNode = new DefaultMutableTreeNode("Static Metrics");
		MutableTreeNode qualityModelsNode = new DefaultMutableTreeNode("Quality Models");
		treeModel.insertNodeInto(staticMetricsNode, (MutableTreeNode) treeModel.getRoot(), 0);
		treeModel.insertNodeInto(qualityModelsNode, (MutableTreeNode) treeModel.getRoot(), 1);
		
		// ... add metric types
		Map<String, MutableTreeNode> typeNodes = new HashMap<String, MutableTreeNode>();
		for (String metricType : MetricRegistry.getInstance().getSupportedMetricTargets()) {
			MutableTreeNode node = new DefaultMutableTreeNode(metricType + " Metrics");
			staticMetricsNode.insert(node, 0);
			typeNodes.put(metricType, node);
		}
		
		// ... add metrics to type nodes
		for (Entry<String, MutableTreeNode> entries : typeNodes.entrySet()) {
			Collection<MetricAttribute> metrics = 
				MetricRegistry.getInstance().getSupportedMetrics(entries.getKey());
			
			addMetricsToTreeNode(metrics, treeModel, entries.getValue());
		}
		
		// ... add the quality models
		for (QualityModel qm : MetricRegistry.getInstance().getQualityModelMap().values()) {
			MutableTreeNode qualityModelNode = new DefaultMutableTreeNode(qm);
			treeModel.insertNodeInto(qualityModelNode, qualityModelsNode, 0);
			
			addMetricsToTreeNode(qm, treeModel, qualityModelNode);
		}
		
		// refresh and reload the JTrees we have here
		treeModel.reload();
		
		metricTree.expandRow(1);
		metricTree.expandRow(0);
		metricSelectionPanel.setViewportView(metricTree);
		
		// put a decoy visualisation component in place so we're not staring at a drab grey canvas
		JPanel decoy = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(new ImageIcon(JavaVisConstants.IMG_DECOY_PANEL_BG).getImage(), 
						0, 0, getBackground(), null);
			}
		};
		decoy.setOpaque(true);
		decoy.setBackground( new Color(DECOY_BACKGROUND_COLOR_RGB) );
		decoy.setBorder( BorderFactory.createEtchedBorder() );
		setVisualisationComponent(decoy);
	}
	
	private void addMetricsToTreeNode(Collection<MetricAttribute> metrics, DefaultTreeModel treeModel, 
			MutableTreeNode parentNode) {
		
		for (MetricAttribute metric : metrics) {
			MutableTreeNode metricTreeNode = new DefaultMutableTreeNode(metric);
			treeModel.insertNodeInto(metricTreeNode, parentNode, 0);
			
			// ... add applicable visualisations
			List<Visualisation> visualisations = 
				VisualisationRegistry.getInstance().getVisualisationsByType(metric.getType());
			for (Visualisation vis : visualisations)
				treeModel.insertNodeInto(new DefaultMutableTreeNode(vis), metricTreeNode, 0);
		}
	}
	
	private IProgramStatusReporter getNearestStatusReporter() throws NullPointerException {
		Component c = getParent();
		
		while ( !(c instanceof IProgramStatusReporter) ) {
			c = c.getParent();
		}
		
		return (IProgramStatusReporter) c;
	}
	
	private void setVisualisationComponent(Component c) {
		if (curVisualiserComponent != null)
			mainPane.remove(curVisualiserComponent);
		mainPane.add(c, BorderLayout.CENTER);
		curVisualiserComponent = c;
	}
	
	private void setVisualisation(Visualisation vis) {
		
		Class<IVisualiser> visualiser = vis.getVisualiserClass();
		Class<IVisualiserVisitor> visitor = vis.getVisitorClass();
		
		if (visualiser != null
				&& visitor != null) {
			
			try {
				setProgramStatus("Applying Visualisation \"" + vis.getName() + "\"...");
				
				IVisualiser visualiserInstance = 
					visualiser.getDeclaredConstructor(WorkspaceContext.class).newInstance(wspContext);
				curVisualiser = visualiserInstance;
				
				setVisualisationComponent( visualiserInstance.acceptVisualisation( visitor.newInstance() ) );
				
				setProgramStatus("Applied Visualisation \"" + vis.getName() + "\".");
				
			} catch (Exception e1) {
				setProgramStatus("Error: " + e1.getLocalizedMessage());
				e1.printStackTrace();
			}
		}
	}
	
	@Override
	public void update(Observable o, Object arg) {

		if (wspContext == o) {
			ChangeEvent ce = (ChangeEvent) arg;
			switch (ce) {
			case VISUALISATION_CHANGE:
				Visualisation vis = wspContext.getVisualisation();
				if (vis != null)
					setVisualisation(vis);
				break;
			}
		}
	}
	
	@Override
	public void setVisible(boolean aFlag) {
		super.setVisible(aFlag);
		
		programTree.expandRow(0);
		
		wspContext.setSelectedItem(wspContext.getModelStore());
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
			} else {
				wspContext.setVisualisation(null);
			}
		}
	}
	
	@Override
	public void valueChanged(TreeSelectionEvent e) {

		DefaultMutableTreeNode node;
		if (metricTree == e.getSource()) {
			node = (DefaultMutableTreeNode) metricTree.getLastSelectedPathComponent();
			
			if (node.getUserObject() instanceof Visualisation) {
				DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) node.getParent();
				MetricAttribute metric = (MetricAttribute) parentNode.getUserObject();
				
				Visualisation vis = (Visualisation) node.getUserObject();
				
				wspContext.setMetric(metric);
				wspContext.setVisualisation(vis);
			}
		}
		
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
	public void notifyFindMethod(MethodModel method) {
		// TODO Auto-generated method stub
		
	}
	
	public void setProgramStatus(String status) {
		statusTarget.setProgramStatus(status);
	}
	
	public void setVisualisationScale(double scale) {
		if (curVisualiser != null)
			curVisualiser.setScale(scale);
	}
	
}
