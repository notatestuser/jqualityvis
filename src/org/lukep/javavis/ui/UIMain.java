package org.lukep.javavis.ui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import java.awt.BorderLayout;

import javax.swing.JInternalFrame;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JPanel;
import javax.swing.JDesktopPane;
import javax.swing.UIManager;

import java.awt.Color;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class UIMain {

	private JFrame frmJavavis;
	private JDesktopPane desktopPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
			        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					
					UIMain window = new UIMain();
					window.frmJavavis.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public UIMain() {
		initialize();
		addChildFrame();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmJavavis = new JFrame();
		frmJavavis.setTitle("JavaVis Software Quality Visualiser");
		frmJavavis.setBounds(100, 100, 800, 600);
		frmJavavis.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmJavavis.getContentPane().setLayout(new BorderLayout(0, 0));
		
		JSplitPane splitPane = new JSplitPane();
		frmJavavis.getContentPane().add(splitPane, BorderLayout.CENTER);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		splitPane.setLeftComponent(tabbedPane);
		
		JPanel panel = new JPanel();
		tabbedPane.addTab("Code Overview", null, panel, null);
		panel.setLayout(new BorderLayout(0, 0));
		
		JTree tree = new JTree();
		tree.setModel(new DefaultTreeModel(
			new DefaultMutableTreeNode("com.test") {
				{
					DefaultMutableTreeNode node_1;
					node_1 = new DefaultMutableTreeNode("abc");
						node_1.add(new DefaultMutableTreeNode("a.java"));
						node_1.add(new DefaultMutableTreeNode("b.java"));
						node_1.add(new DefaultMutableTreeNode("c.java"));
						node_1.add(new DefaultMutableTreeNode("d.java"));
					add(node_1);
					node_1 = new DefaultMutableTreeNode("def");
						node_1.add(new DefaultMutableTreeNode("a.java"));
						node_1.add(new DefaultMutableTreeNode("b.java"));
						node_1.add(new DefaultMutableTreeNode("c.java"));
						node_1.add(new DefaultMutableTreeNode("d.java"));
					add(node_1);
					node_1 = new DefaultMutableTreeNode("ghi");
						node_1.add(new DefaultMutableTreeNode("a.java"));
						node_1.add(new DefaultMutableTreeNode("b.java"));
						node_1.add(new DefaultMutableTreeNode("c.java"));
						node_1.add(new DefaultMutableTreeNode("d.java"));
					add(node_1);
				}
			}
		));
		panel.add(tree, BorderLayout.CENTER);
		
		JPanel panel_1 = new JPanel();
		tabbedPane.addTab("Applied Metrics", null, panel_1, null);
		
		desktopPane = new JDesktopPane();
		desktopPane.setBackground(Color.LIGHT_GRAY);
		splitPane.setRightComponent(desktopPane);
		
		frmJavavis.setVisible(true);
		splitPane.setDividerLocation(.25);
		
		JMenuBar menuBar = new JMenuBar();
		frmJavavis.setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		JMenuItem mntmNewCanvas = new JMenuItem("New canvas");
		mntmNewCanvas.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				addChildFrame();
			}
		});
		mnFile.add(mntmNewCanvas);
		
		JMenuItem mntmOpenCodeDirectory = new JMenuItem("Open code directory...");
		mnFile.add(mntmOpenCodeDirectory);
		
		JMenuItem mntmExit = new JMenuItem("Exit");
		mntmExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});
		mnFile.add(mntmExit);
		
		JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);
		
		JMenuItem mntmAbout = new JMenuItem("About");
		mnHelp.add(mntmAbout);
	}
	
	private void addChildFrame()
	{
		JInternalFrame frame = new JInternalFrame();
		frame.setSize(500, 500);
		frame.setMaximizable(true);
		frame.setResizable(true);
		frame.setClosable(true);
		frame.setVisible(true);
		desktopPane.add( frame );
	}
}
