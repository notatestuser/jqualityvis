/*
 * WorkspacePane.java (JMetricVis)
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
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
import javax.swing.tree.TreePath;

import org.lukep.javavis.metrics.MetricAttribute;
import org.lukep.javavis.metrics.MetricRegistry;
import org.lukep.javavis.metrics.qualityModels.DesignQualityAttribute;
import org.lukep.javavis.metrics.qualityModels.QualityModel;
import org.lukep.javavis.program.generic.models.ClassModel;
import org.lukep.javavis.program.generic.models.IGenericModelNode;
import org.lukep.javavis.program.generic.models.PackageModel;
import org.lukep.javavis.program.generic.models.ProjectModel;
import org.lukep.javavis.ui.IProgramStatusReporter;
import org.lukep.javavis.ui.swing.WorkspaceContext.ChangeEvent;
import org.lukep.javavis.util.JavaVisConstants;
import org.lukep.javavis.visualisation.Visualisation;
import org.lukep.javavis.visualisation.VisualisationRegistry;
import org.lukep.javavis.visualisation.views.IVisualiserVisitor;
import org.lukep.javavis.visualisation.visualisers.IVisualiser;

public class WorkspacePane extends JPanel implements 
		Observer, ActionListener, TreeSelectionListener {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1442832577578014026L;
	
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
		
		initialise();
		
		// make the ProjectModel the subject of our attention
		wspContext.setSelectedItem(project);
		wspContext.setSubject(project);
	}
	
	private void initialise() {
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
		metricSelectionPanel.setBorder(BorderFactory.createEmptyBorder());
		qualityAnalysisPane.add("Metrics", metricSelectionPanel);
		qualityAnalysisPane.addTab(JavaVisConstants.HEADING_METRICS, 
				new ImageIcon(JavaVisConstants.ICON_METRICS), metricSelectionPanel);
		
		// ... create the "Warnings" tab
		qualityAnalysisPane.addTab(JavaVisConstants.HEADING_WARNINGS, 
				new ImageIcon(JavaVisConstants.ICON_WARNINGS), new JPanel());
		
		// create the "Project" tree
		programTree = new JTree();
		DefaultTreeModel projectTreeModel = new DefaultTreeModel( new DefaultMutableTreeNode(wspContext.getModelStore()) );
		programTree.setModel(projectTreeModel);
		programTree.addTreeSelectionListener(this);
		projectExplorerPanel.setViewportView(programTree);
		fillProjectTree(projectTreeModel);
		
		// create the "Metrics" tree
		metricTree = new JTree();
		DefaultTreeModel treeModel = new DefaultTreeModel( new DefaultMutableTreeNode("root") );
		metricTree.setModel( treeModel );
		metricTree.setRootVisible(false);
		metricTree.addTreeSelectionListener(this);
		metricSelectionPanel.setViewportView(metricTree);
		fillMetricTree(treeModel);
		
		// put a decoy visualisation component in place so we're not staring at a drab grey canvas
		@SuppressWarnings("serial")
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
	
	private void fillProjectTree(DefaultTreeModel treeModel) {
		ProjectModel project = wspContext.getModelStore();
		Map<String, DefaultMutableTreeNode> parentNodeMap = new HashMap<String, DefaultMutableTreeNode>();
		int i = 0;
		
		// add package nodes
		DefaultMutableTreeNode node;
		for (PackageModel pkg : project.getPackageMap().values()) {
			node = new DefaultMutableTreeNode(pkg);
			treeModel.insertNodeInto(node, (MutableTreeNode) treeModel.getRoot(), i++);
			parentNodeMap.put(pkg.getQualifiedName(), node);
		}
		
		// add classes to package nodes
		for (ClassModel clazz : project.getClassMap().values()) {
			node = new DefaultMutableTreeNode(clazz);
			if (parentNodeMap.containsKey(clazz.getContainerName()))
					treeModel.insertNodeInto(node, parentNodeMap.get(clazz.getContainerName()), 0);
			if (clazz.getChildCount() > 0)
				parentNodeMap.put(clazz.getQualifiedName(), node);
		}
		
		// refresh and reload the TreeModel
		treeModel.reload();
		programTree.expandRow(0);
	}
	
	private void fillMetricTree(DefaultTreeModel treeModel) {
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
			
			addMetricsToTreeNode(metrics, treeModel, entries.getValue(), true);
		}
		
		// ... add the quality models
		int i = 0;
		for (QualityModel qm : MetricRegistry.getInstance().getQualityModelMap().values()) {
			MutableTreeNode qualityModelNode = new DefaultMutableTreeNode(qm);
			treeModel.insertNodeInto(qualityModelNode, qualityModelsNode, i++);
			
			addMetricsToTreeNode(qm, treeModel, qualityModelNode, false);
		}
		
		// refresh and reload the TreeModel
		treeModel.reload();
		metricTree.expandRow(1);
		metricTree.expandRow(0);
	}
	
	private void addMetricsToTreeNode(Collection<MetricAttribute> metrics, DefaultTreeModel treeModel, 
			MutableTreeNode parentNode, boolean skipQualityAttributes) {
		
		int i = 0;
		for (MetricAttribute metric : metrics) {
			if (skipQualityAttributes && metric instanceof DesignQualityAttribute)
				continue;
			
			MutableTreeNode metricTreeNode = new DefaultMutableTreeNode(metric);
			treeModel.insertNodeInto(metricTreeNode, parentNode, i++);
			
			// ... add applicable visualisations
			List<Visualisation> visualisations = 
				VisualisationRegistry.getInstance().getVisualisationsByType(metric.getType());
			for (Visualisation vis : visualisations)
				treeModel.insertNodeInto(new DefaultMutableTreeNode(vis), metricTreeNode, 0);
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

		if (programTree == e.getSource()
				&& programTree.getSelectionPaths() != null) {
			Set<IGenericModelNode> selectedModels = new HashSet<IGenericModelNode>();
			
			DefaultMutableTreeNode treeNode;
			IGenericModelNode modelNode;
			for (TreePath path : programTree.getSelectionPaths()) {
				treeNode = (DefaultMutableTreeNode) path.getLastPathComponent();
				if (treeNode != null
						&& treeNode.getUserObject() instanceof IGenericModelNode) {
					modelNode = (IGenericModelNode) treeNode.getUserObject();
					selectedModels.add(modelNode);
					wspContext.setSelectedItem(modelNode);
				}
			}
			
			if (selectedModels.size() > 0) {
				IGenericModelNode[] selectedModelsArray = new IGenericModelNode[selectedModels.size()];
				wspContext.setSubjects(selectedModels.toArray(selectedModelsArray));
				
				if (wspContext.getMetric() instanceof MetricAttribute
						&& wspContext.getVisualisation() instanceof Visualisation)
					setVisualisation(wspContext.getVisualisation());
			}
		} else if (metricTree == e.getSource()) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) metricTree.getLastSelectedPathComponent();
			
			if (node != null
					&& node.getUserObject() instanceof Visualisation) {
				
				DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) node.getParent();
				MetricAttribute metric = (MetricAttribute) parentNode.getUserObject();
				
				Visualisation vis = (Visualisation) node.getUserObject();
				
				wspContext.setMetric(metric);
				wspContext.setVisualisation(vis);
			}
		}
		
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
				String progressText = "Applying Visualisation \"" + vis.getName() + "\"...";
				setProgramStatus(progressText);
				
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
	
	public void setProgramStatus(String status) {
		statusTarget.setProgramStatus(status);
	}
	
	public void setVisualisationScale(double scale) {
		if (curVisualiser != null)
			curVisualiser.setScale(scale);
	}
	
	public WorkspaceContext getContext() {
		return wspContext;
	}
	
}
