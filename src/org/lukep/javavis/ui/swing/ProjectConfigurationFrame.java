/*
 * ProjectConfigurationFrame.java (JQualityVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.ui.swing;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.lukep.javavis.program.generic.models.ProjectModel;
import org.lukep.javavis.ui.UIMain;
import org.lukep.javavis.ui.swing.configPanes.ProjectThresholdConfigurationPanel;
import org.lukep.javavis.util.JavaVisConstants;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

/**
 * The Class ProjectConfigurationFrame.
 */
public class ProjectConfigurationFrame extends JFrame implements ActionListener, ChangeListener {
	
	private ProjectModel project;
	
	private JPanel contentPane;
	
	private JTabbedPane tabbedPane;
	
	private JTextField txtProjectName;
	private JTextField txtCreator;
	
	private JLabel lblCreationDateVal;
	private JLabel lblModelCountVal;
	private JLabel lblClassCountVal;
	private JLabel lblPackageCountVal;

	private JButton btnSaveChanges;

	private JPanel pnlFooter;

	/**
	 * Create the frame.
	 *
	 * @param parent the parent
	 * @param uiInstance the ui instance
	 * @param project the project
	 */
	public ProjectConfigurationFrame(Frame parent, UIMain uiInstance, ProjectModel project) {
		setTitle("Project Configurator");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 500, 350);
		setLocationRelativeTo(parent);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		this.project = project;
		
		JLabel lblProject = new JLabel(project.getSimpleName());
		lblProject.setFont(new Font("Arial", Font.ITALIC, 26));
		contentPane.add(lblProject, BorderLayout.NORTH);
		
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBorder(new EmptyBorder(5, 0, 0, 0));
		contentPane.add(tabbedPane, BorderLayout.CENTER);
		tabbedPane.addChangeListener(this);
		
		JPanel panel = new JPanel();
		panel.setBorder(new EmptyBorder(20, 20, 20, 20));
		tabbedPane.addTab("Configuration", null, panel, null);
		panel.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,}));
		
		JLabel lblProjectName = new JLabel("Project Name:");
		panel.add(lblProjectName, "2, 2, right, default");
		
		txtProjectName = new JTextField(project.getSimpleName());
		panel.add(txtProjectName, "4, 2, fill, default");
		txtProjectName.setColumns(10);
		
		JLabel lblCreator = new JLabel("Creator:");
		panel.add(lblCreator, "2, 4, right, default");
		
		txtCreator = new JTextField(project.getCreationUser());
		panel.add(txtCreator, "4, 4, fill, default");
		txtCreator.setColumns(10);
		
		JLabel lblCreationDate = new JLabel("Creation Date:");
		panel.add(lblCreationDate, "2, 8, right, default");
		
		lblCreationDateVal = new JLabel(
				DateFormat.getInstance().format(project.getCreationDate()));
		panel.add(lblCreationDateVal, "4, 8");
		
		JLabel lblModelCount = new JLabel("Model Count:");
		panel.add(lblModelCount, "2, 10, right, default");
		
		lblModelCountVal = new JLabel(
				Integer.toString(project.getModelCount()));
		panel.add(lblModelCountVal, "4, 10");
		
		JLabel lblClassCount = new JLabel("Class Count:");
		panel.add(lblClassCount, "2, 12, right, default");
		
		lblClassCountVal = new JLabel(
				Integer.toString(project.getClassMap().size()));
		panel.add(lblClassCountVal, "4, 12");
		
		JLabel lblPackageCount = new JLabel("Package Count:");
		panel.add(lblPackageCount, "2, 14, right, default");
		
		lblPackageCountVal = new JLabel(
				Integer.toString(project.getPackageMap().size()));
		panel.add(lblPackageCountVal, "4, 14");
		
		pnlFooter = new JPanel();
		FlowLayout flowLayout = (FlowLayout) pnlFooter.getLayout();
		flowLayout.setAlignment(FlowLayout.RIGHT);
		contentPane.add(pnlFooter, BorderLayout.SOUTH);
		
		btnSaveChanges = new JButton("Save Changes", 
				new ImageIcon(JavaVisConstants.ICON_MENU_PROJECT_SAVE));
		btnSaveChanges.addActionListener(this);
		pnlFooter.add(btnSaveChanges);
		
		// end form, add a threshold configuration tab
		tabbedPane.addTab("Metric Thresholds", 
				new ProjectThresholdConfigurationPanel(uiInstance, project));
		
		pack();
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		
		if (btnSaveChanges == e.getSource()) {
			project.setSimpleName(txtProjectName.getText());
			project.setQualifiedName(txtProjectName.getText());
			project.setCreationUser(txtCreator.getText());
			
			JOptionPane.showMessageDialog(this, 
					"Project saved. Save and re-open it to see your changes.", 
					"Project Saved", JOptionPane.INFORMATION_MESSAGE);
		}
	}

	/* (non-Javadoc)
	 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
	 */
	@Override
	public void stateChanged(ChangeEvent e) {
		if (tabbedPane == e.getSource()
				&& pnlFooter != null)
			if (tabbedPane.getSelectedIndex() == 0)
				pnlFooter.setVisible(true);
			else
				pnlFooter.setVisible(false);
	}

}
