/*
 * ClassPropertiesPanel.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.ui.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;

import org.lukep.javavis.metrics.IMeasurableNode;
import org.lukep.javavis.metrics.MetricAttribute;
import org.lukep.javavis.metrics.MetricRegistry;
import org.lukep.javavis.program.generic.models.ClassModel;
import org.lukep.javavis.program.generic.models.IGenericModelNode;
import org.lukep.javavis.ui.swing.WorkspaceContext.ChangeEvent;

public class ClassPropertiesPanel extends JPanel implements Observer {
	
	protected JLabel contentLabel;
	protected JTable methodTable;
	
	ClassCompositionComponent classCompositionComponent;
	
	protected WorkspaceContext wspContext;
	protected IGenericModelNode currentModel;

	public ClassPropertiesPanel(WorkspaceContext wspContext) {
		setLayout( new GridLayout(1, 0, 3, 0) );
		this.wspContext = wspContext;
		wspContext.addObserver(this);
		
		// create content label
		contentLabel = new JLabel();
		contentLabel.setVerticalAlignment(SwingConstants.TOP);
		contentLabel.setBorder( BorderFactory.createEmptyBorder(5, 10, 0, 0) );
		JPanel contentPanel = new JPanel( new BorderLayout() );
		JScrollPane contentLabelPane = new JScrollPane(contentPanel);
		contentPanel.add(contentLabel, BorderLayout.CENTER);
		add(contentLabelPane);
		
		// add class composition component
		classCompositionComponent = new ClassCompositionComponent(wspContext);
		classCompositionComponent.setPreferredSize( new Dimension(100, 100) );
		contentPanel.add(classCompositionComponent, BorderLayout.WEST);
		
		// create method listing table
		methodTable = new JTable( new ClassPropertiesTableModel(null) );
		JScrollPane methodTableScrollPane = new JScrollPane(methodTable);
		add(methodTableScrollPane);
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
			float result;
			for (MetricAttribute attribute : MetricRegistry.getInstance().getMetricAttributes()) {
				try {
					result = attribute.measureTarget(measurableModel).getResult();
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
