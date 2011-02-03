/*
 * VisualisationDesktopPane.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.ScrollPaneLayout;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.lukep.javavis.program.generic.models.ClassInfo;
import org.lukep.javavis.program.generic.models.ClassModelStore;
import org.lukep.javavis.program.generic.models.MethodInfo;
import org.lukep.javavis.program.generic.models.VariableInfo;
import org.lukep.javavis.program.generic.models.measurable.MeasurableClassInfo;
import org.lukep.javavis.program.java.JavaSourceLoaderThread;
import org.lukep.javavis.visualisation.mxgraph.jvmxCircleLayout;

import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.layout.mxGraphLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.util.mxMorphing;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource.mxIEventListener;
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
	
	private HashMap<ClassInfo, mxCell> classVertexMap;

	public VisualisationDesktopPane(IProgramStatusReporter statusTarget) throws Exception {
		super(statusTarget);
		
		this.setBackground(Color.WHITE);
		this.setLayout(  new BorderLayout() );
		
		classModel = new ClassModelStore();
		classVertexMap = new HashMap<ClassInfo, mxCell>();
		
		// initialize the mxGraph, its container component and the circle layout
		graph = new mxGraph();
		
		// initialize the containing mxGraphComponent
		graphComponent = new mxGraphComponent(graph);
		graphComponent.getViewport().setOpaque(false);
		graphComponent.setBackground( new Color(0xF9FFFB) );
		
		// initialize graph layout algorithm
		graphLayout = new jvmxCircleLayout(graph);
		((mxCircleLayout)(graphLayout)).setMoveCircle(true);
		((mxCircleLayout)(graphLayout)).setX0(20);
		((mxCircleLayout)(graphLayout)).setY0(20);
		
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
				graph.getModel().beginUpdate();
				
				try {
					// route edges between classes that have variables of a type we know of
					for (Map.Entry<String, ClassInfo> classEntry : classModel.getClassMap().entrySet()) {
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
					}
					
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
		new Thread(jslt).start();
	}

	@Override
	public void notifyFindClass(ClassInfo clazz) {
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
				addClass( clazz, packageName ); // create cell graph
			}
		}
		((DefaultTreeModel)programTree.getModel()).reload();
	}
	
	@Override
	public void notifyFindMethod(MethodInfo method) {
		// TODO Auto-generated method stub
		
	}
	
	public void setGraphScale(double scale) {
		graph.getView().setScale(scale);
	}
	
	//private HashMap<String, mxCell> packageMap = new HashMap<String, mxCell>();
	private void addClass(ClassInfo clazz, String packageName) {
		mxCell cell = null;
		/*if (packageMap.containsKey(packageName))
			parent = packageMap.get(packageName);
		else {
			parent = (mxCell) graph.insertVertex(null, null, packageName, 250, 100, 150, 80);
			packageMap.put(packageName, parent);
		}*/
		cell = (mxCell) graph.insertVertex(graph.getDefaultParent(), null, clazz, 20, 20, 150, 50);
		classVertexMap.put(clazz, cell);
	}
	
	private void bindGraphEvents() {
		graphComponent.getGraphControl().addMouseListener(new MouseAdapter() {

			@Override
			public void mouseReleased(MouseEvent e) {
				mxCell currentSelectionCell = (mxCell)graphComponent.getCellAt(e.getX(), e.getY());
				if (currentSelectionCell != null && graph.getModel().isVertex(currentSelectionCell)) {
					MeasurableClassInfo mclass = (MeasurableClassInfo) currentSelectionCell.getValue();
					propertiesPane.setCurrentClass(mclass);
				}
				super.mouseClicked(e);
			}
			
		});
	}

}
