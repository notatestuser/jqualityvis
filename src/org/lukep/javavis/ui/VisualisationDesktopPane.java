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
import org.lukep.javavis.visualisation.java.JavaSourceLoaderThread;
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
		graphComponent = new mxGraphComponent(graph);
		graphLayout = new jvmxCircleLayout(graph);
		((mxCircleLayout)(graphLayout)).setMoveCircle(true);
		((mxCircleLayout)(graphLayout)).setX0(20);
		((mxCircleLayout)(graphLayout)).setY0(20);
		
		// create a panel to contain the left side of the JSplitPane's components
		JPanel leftPane = new JPanel( new BorderLayout() );
		leftPane.setVisible(true);
		
		// create the outer split pane that contains the left (inner) split pane and the graph
		// component on the right side of the window
		JSplitPane outerSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPane, 
				graphComponent);
		outerSplitPane.setOneTouchExpandable(true);
		outerSplitPane.setDividerLocation(200);
		outerSplitPane.setDividerSize(6);
		outerSplitPane.setBorder(null);
		this.add(outerSplitPane, BorderLayout.CENTER);
		
		// create the tabbed pane on the left split
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		leftPane.add(tabbedPane, BorderLayout.CENTER);
		
		// create the "Code Overview" tab
		codeOverviewPanel = new JScrollPane();
		tabbedPane.addTab("Code Overview", null, codeOverviewPanel, null);
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
		String classQualName = clazz.getQualifiedName();
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
	
	//private HashMap<String, mxCell> packageMap = new HashMap<String, mxCell>();
	private void addClass(ClassInfo clazz, String packageName) {
		mxCell cell = null;
		/*if (packageMap.containsKey(packageName))
			parent = packageMap.get(packageName);
		else {
			parent = (mxCell) graph.insertVertex(null, null, packageName, 250, 100, 150, 80);
			packageMap.put(packageName, parent);
		}*/
		cell = (mxCell) graph.insertVertex(graph.getDefaultParent(), null, clazz, 250, 100, 150, 80);
		classVertexMap.put(clazz, cell);
	}

}