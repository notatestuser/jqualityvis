/*
 * VisualisationDesktopPane.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.ui.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Enumeration;
import java.util.HashMap;

import javax.swing.CellRendererPane;
import javax.swing.JComponent;
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
import org.lukep.javavis.program.java.JavaSourceLoaderThread;
import org.lukep.javavis.ui.IProgramSourceObserver;
import org.lukep.javavis.ui.IProgramStatusReporter;

import com.mxgraph.canvas.mxICanvas;
import com.mxgraph.canvas.mxImageCanvas;
import com.mxgraph.layout.mxCompactTreeLayout;
import com.mxgraph.layout.mxGraphLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.util.mxMorphing;
import com.mxgraph.swing.view.mxInteractiveCanvas;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource.mxIEventListener;
import com.mxgraph.view.mxCellState;
import com.mxgraph.view.mxGraph;

public class VisualisationDesktopPane extends StatefulWorkspacePane implements IProgramSourceObserver {
	
	private JTree programTree;
	private JScrollPane codeOverviewPanel;
	private JTabbedPane tabbedPane;
	private ClassPropertiesPanel propertiesPane;
	
	private ClassModelStore classModel;
	
	private mxGraph graph;
	private mxGraphComponent graphComponent;
	private mxGraphLayout graphLayout;
	
	private HashMap<String, mxCell> packageMap = new HashMap<String, mxCell>();
	private HashMap<ClassModel, mxCell> classVertexMap;

	public class SwingCanvas extends mxInteractiveCanvas
	{
		protected CellRendererPane rendererPane = new CellRendererPane();
		protected mxGraphComponent graphComponent;
		protected JComponent vertexRenderer;

		public SwingCanvas(mxGraphComponent graphComponent)
		{
			this.graphComponent = graphComponent;
		}

		public void drawVertex(mxCellState state, JComponent vertexRenderer)
		{
			this.vertexRenderer = vertexRenderer;
			vertexRenderer.setOpaque(true);

			rendererPane.paintComponent(g, vertexRenderer, graphComponent,
					(int) state.getX() + translate.x, (int) state.getY()
							+ translate.y, (int) state.getWidth(), (int) state
							.getHeight(), true);
		}

	}
	
	public VisualisationDesktopPane(IProgramStatusReporter statusTarget) throws Exception {
		super(statusTarget);
		
		this.setBackground(Color.WHITE);
		this.setLayout(  new BorderLayout() );
		
		classModel = new ClassModelStore();
		classVertexMap = new HashMap<ClassModel, mxCell>();
		
		// initialise the mxGraph, its container component and the circle layout
		graph = new mxGraph()
		{
			public void drawState(mxICanvas canvas, mxCellState state,
					String label)
			{
				// Indirection for wrapped swing canvas inside image canvas (used for creating
				// the preview image when cells are dragged)
				mxCell cell = (mxCell) state.getCell();
				if (getModel().isVertex(state.getCell())
						&& canvas instanceof mxImageCanvas
						&& ((mxImageCanvas) canvas).getGraphicsCanvas() instanceof SwingCanvas
						&& cell.getValue() instanceof ClassModel)
				{
					((SwingCanvas) ((mxImageCanvas) canvas).getGraphicsCanvas())
							.drawVertex(state, new ClassCompositionComponent( (ClassModel) cell.getValue() ));
				}
				// Redirection of drawing vertices in SwingCanvas
				else if (getModel().isVertex(state.getCell())
						&& canvas instanceof SwingCanvas
						&& cell.getValue() instanceof ClassModel)
				{
					((SwingCanvas) canvas).drawVertex(state, new ClassCompositionComponent( (ClassModel) cell.getValue() ));
				}
				else
				{
					super.drawState(canvas, state, label);
				}
			}
		};
		
		// initialize the containing mxGraphComponent
		graphComponent = new mxGraphComponent(graph)
		{
			/**
			 * 
			 */
			private static final long serialVersionUID = 4683716829748931448L;

			public mxInteractiveCanvas createCanvas()
			{
				return new SwingCanvas(this);
			}
		};
		graphComponent.getViewport().setOpaque(false);
		graphComponent.setBackground( new Color(0xF9FFFB) );
		
		// initialize graph layout algorithm
		/*graphLayout = new jvmxCircleLayout(graph);
		((mxCircleLayout)(graphLayout)).setMoveCircle(true);
		((mxCircleLayout)(graphLayout)).setX0(20);
		((mxCircleLayout)(graphLayout)).setY0(20);*/
		//graphLayout = new mxStackLayout(graph, false);
		graphLayout = new mxCompactTreeLayout(graph);
		
		// bind mxGraph events
		bindGraphEvents();
		
		// create a panel to contain the left side of the JSplitPane's components
		JPanel leftPane = new JPanel( new BorderLayout() );
		leftPane.setVisible(true);
		
		// create a panel to show the properties of the currently selected class
		propertiesPane = new ClassPropertiesPanel();
		propertiesPane.setVisible(true);
		
		// create the right split pane that contains the graph component on the top and the class
		// properties pane on the bottom
		JSplitPane rightSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, graphComponent, propertiesPane);
		rightSplitPane.setOneTouchExpandable(true);
		rightSplitPane.setDividerLocation(500);
		rightSplitPane.setResizeWeight(1);
		rightSplitPane.setDividerSize(6);
		rightSplitPane.setBorder(null);
		
		// create the outer split pane that contains the left (inner) split pane and the graph
		// component on the right side of the window
		JSplitPane outerSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPane, rightSplitPane);
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

	public void loadCodeBase(File selectedDirectory) {
		JavaSourceLoaderThread jslt = new JavaSourceLoaderThread(selectedDirectory) {
			
			@Override
			public void notifyStatusChange(String message) {
				setProgramStatus(message);
			}

			@Override
			public void statusFinished() {
				((DefaultTreeModel)programTree.getModel()).reload();

				graph.getModel().endUpdate();
				graph.getModel().beginUpdate();
				
				try {
					// route edges between classes that have variables of a type we know of
					/*for (Map.Entry<String, ClassInfo> classEntry : classModel.getClassMap().entrySet()) {
						ClassInfo sourceClass = classEntry.getValue();
						mxCell sourceCell = classVertexMap.get(sourceClass);
						assert(sourceCell != null);
						if (sourceClass.getVariableCount() > 0) {
							ClassInfo targetClass;
							for (VariableInfo varInfo : sourceClass.getVariables()) {
								targetClass = varInfo.getTypeInternalClass();
								if (targetClass != null 
										&& classVertexMap.containsKey(targetClass)) {
									
									// insert a connecting edge between the source and the target cells
									graph.insertEdge(graph.getDefaultParent(), null, 
											varInfo.getName(), sourceCell, classVertexMap.get(targetClass));
								}
							}
						}
					}*/
					
					graphLayout.execute(graph.getDefaultParent());
				} finally {
					mxMorphing morph = new mxMorphing(graphComponent, 20, 1.2, 20);
					
					morph.addListener(mxEvent.DONE, new mxIEventListener() {
						
						@Override
						public void invoke(Object sender, mxEventObject evt) {
							graph.getModel().endUpdate();
						}
					});
					
					morph.startAnimation();
				}
			}
			
		};
		jslt.addObserver(this);
		jslt.addObserver(classModel);
		graph.getModel().beginUpdate();
		new Thread(jslt).start();
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
	
	public void setGraphScale(double scale) {
		graphComponent.zoomTo(scale, true);
	}
	
	private synchronized mxCell getOrCreatePackageCell(String packageName) {
		// return the package if we already have it
		if (packageMap.containsKey(packageName))
			return packageMap.get(packageName);
		
		// parent package exists?
		int lastDotIndex = packageName.lastIndexOf('.');
		String parentPackageName;
		mxCell parentPackageCell = null;
		if (packageName.length() > 0 && lastDotIndex != -1) { // class is member of a named package (non-default)
			parentPackageName = packageName.substring(0, lastDotIndex);
			parentPackageCell = getOrCreatePackageCell(parentPackageName);
		} else {
			// we're at the root
		}
		
		// create the current package cell and link it up to the parent
		mxCell curPackageCell = 
			(mxCell) graph.insertVertex(graph.getDefaultParent(), null, packageName, 250, 100, 150, 80);
		if (parentPackageCell != null) {
			graph.insertEdge(graph.getDefaultParent(), null, null, 
					parentPackageCell, curPackageCell);
		}
		packageMap.put(packageName, curPackageCell);
		
		return curPackageCell;
	}
	
	private void addClass(ClassModel clazz, String packageName) {
		mxCell parent = getOrCreatePackageCell(packageName), 
			   cell = null;
		cell = (mxCell) graph.insertVertex(graph.getDefaultParent(), null, clazz, 20, 20, 150, 50);
		graph.insertEdge(graph.getDefaultParent(), null, null, parent, cell);
		classVertexMap.put(clazz, cell);
	}
	
	private void bindGraphEvents() {
		graphComponent.getGraphControl().addMouseListener(new MouseAdapter() {

			@Override
			public void mouseReleased(MouseEvent e) {
				mxCell currentSelectionCell = (mxCell)graphComponent.getCellAt(e.getX(), e.getY());
				if (currentSelectionCell != null && graph.getModel().isVertex(currentSelectionCell)
						&& currentSelectionCell.getValue() instanceof ClassModel) {
					ClassModel mclass = (ClassModel) currentSelectionCell.getValue();
					propertiesPane.setCurrentClass(mclass);
				}
				super.mouseClicked(e);
			}
			
		});
	}

}
