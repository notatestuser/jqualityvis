/*
 * WorkspacePane.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.ui.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.util.Collection;
import java.util.Formatter;
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
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.ScrollPaneLayout;
import javax.swing.border.Border;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;

import org.lukep.javavis.metrics.IMeasurableNode;
import org.lukep.javavis.metrics.MetricAttribute;
import org.lukep.javavis.metrics.MetricRegistry;
import org.lukep.javavis.metrics.MetricThreshold;
import org.lukep.javavis.metrics.qualityModels.DesignQualityAttribute;
import org.lukep.javavis.metrics.qualityModels.QualityModel;
import org.lukep.javavis.program.generic.models.ClassModel;
import org.lukep.javavis.program.generic.models.IGenericModelNode;
import org.lukep.javavis.program.generic.models.IGenericModelNodeVisitor;
import org.lukep.javavis.program.generic.models.MethodModel;
import org.lukep.javavis.program.generic.models.PackageModel;
import org.lukep.javavis.program.generic.models.ProjectModel;
import org.lukep.javavis.program.generic.models.Relationship;
import org.lukep.javavis.program.generic.models.VariableModel;
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
	
	private static final int TITLE_LABEL_COLOR_RGB = 0xF9FFFB;
	private static final int DECOY_BACKGROUND_COLOR_RGB = 0xFFF9FFFB;
	private static final int PROJECT_EXPLORER_BGCOLOR_RGB = 0xFFF9FFFB;
	private static final int QUALITY_ANALYSIS_BGCOLOR_RGB = 0xFFF9FFFB;
	
	public class WorkspaceSplitPaneUI extends BasicSplitPaneUI {
		@Override
		public BasicSplitPaneDivider createDefaultDivider() {
			return new BasicSplitPaneDivider(this) {
				@Override
				public void setBorder(Border border) { }
			};
		}
	}
	
	private JPanel mainPane;
	private JPanel leftPaneTop;
	private JPanel leftPaneBottom;
	private JLabel titleLabel;
	
	private JScrollPane projectExplorerPanel;
	private JScrollPane warningsPanel;
	private JScrollPane metricsTreePanel;
	private JPanel metricFancyPanel;
	
	private JSplitPane rightSplitPane;
	private JSplitPane leftSplitPane;
	private JSplitPane outerSplitPane;
	
	private JComboBox metricComboBox;
	private JComboBox visComboBox;
	
	private JTree programTree;
	private JTree metricTree;
	private JTree warningTree;
	
	private IVisualiser curVisualiser;
	private Component curVisualiserComponent;
	
	private ClassPropertiesPanel propertiesPane;
	
	private MetricCharacteristicGraph metricCharacteristicGraph;
	
	private IProgramStatusReporter statusTarget;
	
	private WorkspaceContext wspContext;
	
	// maintain a mapping of warning TreeNodes to the models they represent
	Map<MutableTreeNode, IGenericModelNode> warningMap = new HashMap<MutableTreeNode, IGenericModelNode>();

	private JLabel lblMetricDescription;

	private JLabel lblVisDescription;
	
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
		
		initialise(project);
		
		// make the ProjectModel the subject of our attention
		wspContext.setSelectedItem(project);
		wspContext.setSubject(project);
	}
	
	private void initialise(ProjectModel project) {
		// create a panel to contain the left TOP side of the JSplitPane's components
		leftPaneTop = new JPanel( new BorderLayout() );
		leftPaneTop.setVisible(true);
		
		// create a panel to contain the left BOTTOM side of the JSplitPane's components
		leftPaneBottom = new JPanel( new BorderLayout() );
		leftPaneBottom.setVisible(true);
		
		// create a panel to contain the graph component and toolbar
		mainPane = new JPanel( new BorderLayout() );
		mainPane.setVisible(true);
		
		// create the "title label"
		titleLabel = new JLabel(project.getSimpleName() + " (created " + 
				DateFormat.getInstance().format(project.getCreationDate()) +
				" by "+project.getCreationUser()+")");
		titleLabel.setOpaque(true);
		titleLabel.setBackground(new Color(TITLE_LABEL_COLOR_RGB));
		titleLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 0));
		titleLabel.setFont(new Font("Tahoma", Font.PLAIN, 18));
		mainPane.add(titleLabel, BorderLayout.NORTH);
		
		// create the properties panel to show the attributes of the currently selected class
		propertiesPane = new ClassPropertiesPanel(this, wspContext);
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
		outerSplitPane.setDividerLocation(280);
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
		
		// create the "Metrics (fancy)" tab
		metricFancyPanel = new JPanel( new GridLayout(2, 1) );
		metricFancyPanel.setBackground(new Color(0xFFFFFF));
		metricFancyPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		{
			// create top and bottom panels
			JPanel metricSelectionTop = new JPanel(new BorderLayout());
			JPanel metricSelectionBottom = new JPanel(new BorderLayout());
			metricSelectionTop.setOpaque(false);
			metricSelectionBottom.setOpaque(false);
			metricFancyPanel.add(metricSelectionTop);
			metricFancyPanel.add(metricSelectionBottom);
			
			// create metric combo box to sit in the north portion of the metricSelectionTop
			metricComboBox = new JComboBox( new MetricComboBoxModel() );
			metricComboBox.setEnabled(true);
			metricComboBox.setSelectedIndex(0);
			metricComboBox.addActionListener(this);
			metricSelectionTop.add(metricComboBox, BorderLayout.NORTH);
			
			// create visualisation combo box to sit in the north portion of the metricSelectionBottom
			visComboBox = new JComboBox( new VisualisationComboBoxModel() );
			visComboBox.setEnabled(false);
			visComboBox.setSelectedIndex(0);
			visComboBox.addActionListener(this);
			metricSelectionBottom.add(visComboBox, BorderLayout.NORTH);
			
			// initialise a description label for metrics
			lblMetricDescription = new JLabel("<html><p>Select a metric to view information.</p></html>");
			lblMetricDescription.setFont(new Font("Tahoma", Font.BOLD, 11));
			metricSelectionTop.add(lblMetricDescription, BorderLayout.CENTER);
			
			// initialise a description label for visualisations
			lblVisDescription = new JLabel("<html><p>Select a visualisation to view information.</p></html>");
			lblVisDescription.setFont(new Font("Tahoma", Font.BOLD, 11));
			metricSelectionBottom.add(lblVisDescription, BorderLayout.CENTER);
		}
		qualityAnalysisPane.addTab(JavaVisConstants.HEADING_METRICS_FANCY, 
				new ImageIcon(JavaVisConstants.ICON_METRICS), metricFancyPanel);
		
		// ... create the "Metrics (tree)" tab
		metricsTreePanel = new JScrollPane();
		metricsTreePanel.setLayout(new ScrollPaneLayout());
		{
			// initialise the tree
			metricTree = new JTree();
			metricsTreePanel.setBackground(new Color(PROJECT_EXPLORER_BGCOLOR_RGB));
			DefaultTreeModel treeModel = new DefaultTreeModel( new DefaultMutableTreeNode("root") );
			metricTree.setModel( treeModel );
			metricTree.setRootVisible(false);
			metricTree.addTreeSelectionListener(this);
			metricsTreePanel.setViewportView(metricTree);
			fillMetricTree();
		}
		qualityAnalysisPane.addTab(JavaVisConstants.HEADING_METRICS_TREE,
				new ImageIcon(JavaVisConstants.ICON_METRICS), metricsTreePanel);
		
		// ... create the "Overview" tab containing the MetricCharacteristicGraph
		metricCharacteristicGraph = new MetricCharacteristicGraph(wspContext);
		qualityAnalysisPane.addTab(JavaVisConstants.HEADING_CHARACTERISTICS,
				new ImageIcon(JavaVisConstants.ICON_BREAKDOWN), 
				metricCharacteristicGraph.getGraphComponent());
		
		// ... create the "Warnings" tab + JScrollPane
		warningsPanel = new JScrollPane();
		warningsPanel.setLayout(new ScrollPaneLayout());
		warningsPanel.setBorder(BorderFactory.createEmptyBorder());
		{
			// create the "Warnings" tree
			warningTree = new JTree();
			DefaultTreeModel treeModel = new DefaultTreeModel( new DefaultMutableTreeNode("root") );
			warningTree.setModel( treeModel );
			warningTree.setRootVisible(false);
			// set the leaf node icon
			DefaultTreeCellRenderer cellRenderer = new DefaultTreeCellRenderer();
			cellRenderer.setLeafIcon(new ImageIcon(JavaVisConstants.ICON_WARNINGS));
			warningTree.setCellRenderer(cellRenderer);
			warningTree.addTreeSelectionListener(this);
			warningsPanel.setViewportView(warningTree);
			fillWarningsTree(project);
		}
		qualityAnalysisPane.addTab(JavaVisConstants.HEADING_WARNINGS, 
				new ImageIcon(JavaVisConstants.ICON_WARNINGS), warningsPanel);
		
		// create the "Project" tree
		programTree = new JTree();
		DefaultTreeModel projectTreeModel = new DefaultTreeModel( new DefaultMutableTreeNode(wspContext.getModelStore()) );
		programTree.setModel(projectTreeModel);
		programTree.addTreeSelectionListener(this);
		projectExplorerPanel.setViewportView(programTree);
		fillProjectTree();
		
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
	
	private void fillProjectTree() {
		
		DefaultTreeModel treeModel = (DefaultTreeModel) programTree.getModel();
		
		// clear the tree
		((DefaultMutableTreeNode)(treeModel.getRoot())).removeAllChildren();
		
		Map<String, DefaultMutableTreeNode> parentNodeMap = new HashMap<String, DefaultMutableTreeNode>();
		
		// add package nodes
		ProjectModel project = wspContext.getModelStore();
		DefaultMutableTreeNode node;
		int i = 0;
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
	
	public void fillMetricTree() {
		
		DefaultTreeModel treeModel = (DefaultTreeModel) metricTree.getModel();
		
		// clear the tree
		((DefaultMutableTreeNode)(treeModel.getRoot())).removeAllChildren();
		
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
	
	public void fillWarningsTree(ProjectModel project) {
		
		final DefaultTreeModel treeModel = (DefaultTreeModel) warningTree.getModel();
		
		// clear the tree and warning mappings
		((DefaultMutableTreeNode)(treeModel.getRoot())).removeAllChildren();
		warningMap.clear();
		
		// build up the list of metrics that we have threshold objects for
		final List<MetricThreshold> thresholds = wspContext.getModelStore().getMetricThresholds();
		
		if (thresholds.size() > 0) {
			// build up a list of targets we actually care about (for optimisation)
			final Set<String> thresMetricTargets = new HashSet<String>();
			for (MetricThreshold thres : thresholds)
				thresMetricTargets.addAll(thres.getMetric().getAppliesTo());
			
			// visit all of the nodes in the graph and test applicable ones
			new IGenericModelNodeVisitor() {
				
				private int warningIdx = 0;
				
				private void testWarning(IMeasurableNode model) {
					
					// do we have a thresMetricTarget for this node type?
					if (thresMetricTargets.contains(model.getModelTypeName())) {
						
						// gather the metric measurements and test our results
						for (MetricThreshold thres : thresholds) {
							
							// skip if the metric doesn't apply to this model
							if (!thres.getMetric().testAppliesTo(model.getModelTypeName()))
								continue;
							
							// do bound checking
							double bound1 = thres.getBound1(),
							       bound2 = thres.getBound2(),
							       result = thres.getMetric().measureTargetCached(model).getResult();
							if (!(result <= bound2 && result >= bound1)
									&& !(result >= bound2 && result <= bound1)) {
								// raise a warning
								DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(
										thres.getName() + ": " + model.getSimpleName() 
										+ " (" + result + " out of " + bound1 + "-" + bound2 + ")");
								treeModel.insertNodeInto(treeNode, (MutableTreeNode) treeModel.getRoot(), warningIdx++);
								warningMap.put(treeNode, model);
							}
						}
					}
					
					// visit all of the child nodes of this measurable node
					if (model.getChildren() != null)
						for (Relationship r : model.getChildren())
							r.getTarget().accept(this);
				}
				
				@Override
				public void visit(VariableModel model) {
					testWarning(model);
				}
				@Override
				public void visit(MethodModel model) {
					testWarning(model);
				}
				@Override
				public void visit(ClassModel model) {
					testWarning(model);
				}
				@Override
				public void visit(PackageModel model) {
					testWarning(model);
				}
				@Override
				public void visit(ProjectModel model) {
					testWarning(model);
				}
			}.visit(project);
		} else {
			// no thresholds defined
			treeModel.insertNodeInto(new DefaultMutableTreeNode(
					"No thresholds defined for " + project.getSimpleName()), 
					(MutableTreeNode) treeModel.getRoot(), 0);
		}
		
		// refresh and reload the TreeModel
		treeModel.reload();
		warningTree.expandRow(0);
	}
	
	public void reloadMetricCombo() {
		metricComboBox.setModel( new MetricComboBoxModel() );
		metricComboBox.setSelectedIndex(0);
		lblMetricDescription.setText(null);
	}
	
	public void reloadVisualisationCombo() {
		visComboBox.setModel( new VisualisationComboBoxModel() );
		visComboBox.setSelectedIndex(0);
		lblVisDescription.setText(null);
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
				// set the currently selected metric
				MetricAttribute metric = (MetricAttribute) metricComboBox.getSelectedItem();
				
				wspContext.setMetric(metric);
				
				List<Visualisation> visualisations = 
					VisualisationRegistry.getInstance().getVisualisationsByType(metric.getType());
				((VisualisationComboBoxModel)(visComboBox.getModel())).setVisualisations(visualisations);
				visComboBox.setEnabled(true);
				
				// set the metric description label
				lblMetricDescription.setText(
						new Formatter().format("<html><p style=\"font-weight:normal;\">Measures %s</p><p>%s</p></html>", 
								metric.getCharacteristic(), metric.getDescription()).toString());
				
			} else {
				// clear the currently selected metric + visualisation
				wspContext.setMetric(null);
				wspContext.setVisualisation(null);
				visComboBox.setEnabled(false);
				
				lblMetricDescription.setText(null);
			}
		} else if (visComboBox == e.getSource()
				&& visComboBox.isEnabled()) {
			
			if (visComboBox.getSelectedItem() instanceof Visualisation) {
				// set the currently selected visualisation
				Visualisation vis = (Visualisation) visComboBox.getSelectedItem();
				wspContext.setVisualisation(vis);
				
				// set the visualisation description label
				lblVisDescription.setText(
						new Formatter().format("<html><p style=\"font-weight:normal;\">%s</p><p>%s</p></html>", 
								vis.getType(), vis.getDescription()).toString());
			} else {
				// clear the currently selected visualisation
				wspContext.setVisualisation(null);
				
				lblVisDescription.setText(null);
			}
		}
	}
	
	@Override
	public void valueChanged(TreeSelectionEvent e) {

		// program tree selection events
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
					if (modelNode.getChildCount() > 0)
						selectedModels.add(modelNode);
					wspContext.setSelectedItem(modelNode);
				}
			}
			
			if (selectedModels.size() > 0) {
				IGenericModelNode[] selectedModelsArray = new IGenericModelNode[selectedModels.size()];
				wspContext.setSubjects(selectedModels.toArray(selectedModelsArray));
				
				MetricAttribute metric = wspContext.getMetric();
				Visualisation vis = wspContext.getVisualisation();
				if (metric instanceof MetricAttribute && vis instanceof Visualisation) {
					setVisualisation(vis);
				}
			}
		// metric tree selection events
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
		// warning tree selection events
		} else if (warningTree == e.getSource()) {
			MutableTreeNode node = (MutableTreeNode) warningTree.getLastSelectedPathComponent();
			
			if (node != null
					&& warningMap.containsKey(node)) {
				wspContext.setSelectedItem(warningMap.get(node));
			}
		}
		
	}
	
	private void setVisualisationComponent(JComponent c) {
		if (curVisualiserComponent != null)
			mainPane.remove(curVisualiserComponent);
		c.setBorder(BorderFactory.createEmptyBorder());
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
			
			// set workspace title
			StringBuilder sb = new StringBuilder();
			sb.append(vis + " of " + wspContext.getMetric() + " in ");
			for (IGenericModelNode node : wspContext.getSubjects())
				sb.append(node.getSimpleName() + ", ");
			String str = sb.toString();
			titleLabel.setText(str.substring(0, str.length() - 2));
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
