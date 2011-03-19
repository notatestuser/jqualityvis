/*
 * ClassPropertiesPanel.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.ui.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableColumnModel;

import org.lukep.javavis.metrics.IMeasurableNode;
import org.lukep.javavis.metrics.MetricAttribute;
import org.lukep.javavis.metrics.MetricRegistry;
import org.lukep.javavis.program.generic.models.ClassModel;
import org.lukep.javavis.program.generic.models.IGenericModelNode;
import org.lukep.javavis.ui.swing.WorkspaceContext.ChangeEvent;

@SuppressWarnings("serial")
public class ClassPropertiesPanel extends JPanel implements Observer {
	
	protected JLabel contentLabel;
	protected JTable methodTable;
	
	ClassCompositionComponent classCompositionComponent;
	
	protected WorkspaceContext wspContext;
	protected IGenericModelNode currentModel;

	public ClassPropertiesPanel(WorkspacePane w, WorkspaceContext wspContext) {
		setLayout( new BorderLayout(3, 0) );
		this.wspContext = wspContext;
		wspContext.addObserver(this);
		
		// create content label
		contentLabel = new JLabel();
		contentLabel.setVerticalAlignment(SwingConstants.TOP);
		contentLabel.setBorder( BorderFactory.createEmptyBorder(5, 5, 0, 0) );
		JPanel contentPanel = new JPanel( new BorderLayout() );
		JScrollPane contentLabelPane = new JScrollPane(contentLabel);
		contentLabelPane.setBorder(BorderFactory.createEmptyBorder());
		contentPanel.add(contentLabelPane, BorderLayout.CENTER);
		//add(contentLabelPane);
		
		// add class composition component
		classCompositionComponent = new ClassCompositionComponent(wspContext);
		classCompositionComponent.setPreferredSize( new Dimension(80, 100) );
		contentPanel.add(classCompositionComponent, BorderLayout.WEST);
		
		// create method listing table
		methodTable = new JTable( new ClassPropertiesTableModel(null) );
		JScrollPane methodTableScrollPane = new JScrollPane(methodTable);
		//add(methodTableScrollPane);
		
		// create horizontal split pane to contain context pane on the left and 
		// the method listing table on the right
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, contentPanel, methodTableScrollPane);
		splitPane.setDividerLocation(450);
		splitPane.setDividerSize(2);
		splitPane.setBorder(null);
		splitPane.setUI(w.new WorkspaceSplitPaneUI());
		add(splitPane, BorderLayout.CENTER);
	}
	
	public void setCurrentModel(IGenericModelNode model) {
		currentModel = model;
		
		// build up the html content string
		StringBuilder sb = new StringBuilder("<html>");
		// ... include container name
		sb.append(model.getContainerName() + "<br />");
		// ... include model's modifiers and simple name
		sb.append("<h2>" + model.getModifierNames(" ") + " " + model.getSimpleName() + "</h2>");
		// ... classes: include bloodline statistics
		if (model instanceof ClassModel)
			sb.append(((ClassModel)(model)).getInheritedFieldCount() + " fields and " 
					+ ((ClassModel)(model)).getInheritedMethodCount() + " methods inherited from " 
					+ ((ClassModel)(model)).getAncestorCount() + " ancestors<br />");
		// ... include metrics
		sb.append("<table border=\"0\" style=\"margin-top:5px\">");
		if (model instanceof IMeasurableNode) {
			IMeasurableNode measurableModel = (IMeasurableNode) model;
			double result;
			for (MetricAttribute attribute : MetricRegistry.getInstance().getMetricAttributes()) {
				try {
					result = attribute.measureTarget(measurableModel).getRoundedResult(5);
					sb.append("<tr><td><strong>" 
							+ attribute.getName() 
							+ " (" + attribute.getInternalName() + ")" 
							+ "</strong></td>" 
							+ "<td> " + result + "</td></tr>");
				} catch (NullPointerException ex) {}
			}
		}
		sb.append("</table>");
		// ... end
		sb.append("</html>");
		
		// update content label
		contentLabel.setText( sb.toString() );
		
		// update method listing table
		ClassPropertiesTableModel methodTableModel = 
			(ClassPropertiesTableModel) methodTable.getModel();
		methodTableModel.setSubject(model);
		TableColumnModel tcm = methodTable.getColumnModel();
		if (tcm.getColumnCount() > 0)
			tcm.getColumn(0).setPreferredWidth(300);
		
		// update class composition component
		classCompositionComponent.setCurrentModel(model);
	}

	@Override
	public void update(Observable o, Object arg) {
		if (ChangeEvent.SELECTED_CHANGE == (ChangeEvent) arg
				&& wspContext.getSelectedItem() != currentModel)
			setCurrentModel(wspContext.getSelectedItem());
	}
	
}
