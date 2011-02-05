/*
 * ClassPropertiesPanel.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.ui.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;

import org.lukep.javavis.metrics.MetricAttribute;
import org.lukep.javavis.metrics.MetricMeasurement;
import org.lukep.javavis.metrics.MetricRegistry;
import org.lukep.javavis.program.generic.models.measurable.MeasurableClassInfo;

public class ClassPropertiesPanel extends JPanel {
	
	protected JLabel contentLabel;
	protected JTable methodTable;
	
	ClassCompositionComponent classCompositionComponent;
	
	protected MeasurableClassInfo currentClass;

	public ClassPropertiesPanel() {
		setLayout( new GridLayout(1, 0, 3, 0) );
		
		// create content label
		contentLabel = new JLabel();
		contentLabel.setVerticalAlignment(SwingConstants.TOP);
		contentLabel.setBorder( BorderFactory.createEmptyBorder(5, 10, 0, 0) );
		JPanel contentPanel = new JPanel( new BorderLayout() );
		JScrollPane contentLabelPane = new JScrollPane(contentPanel);
		contentPanel.add(contentLabel, BorderLayout.CENTER);
		add(contentLabelPane);
		
		// add class composition component
		classCompositionComponent = new ClassCompositionComponent(null);
		classCompositionComponent.setPreferredSize( new Dimension(100, 100) );
		contentPanel.add(classCompositionComponent, BorderLayout.WEST);
		
		// create method listing table
		methodTable = new JTable( new ClassPropertiesTableModel(null) );
		JScrollPane methodTableScrollPane = new JScrollPane(methodTable);
		add(methodTableScrollPane);
	}
	
	public void setCurrentClass(MeasurableClassInfo clazz) {
		currentClass = clazz;
		
		// build up the html content string
		StringBuilder sb = new StringBuilder("<html>");
		// ... include package name
		sb.append(clazz.getPackageName() + "<br />");
		// ... include class's simple name
		sb.append("<h2>" + clazz.getSimpleName() + "</h2>");
		// ... include methods
		/*if (clazz.getMethodCount() > 0) {
			sb.append("<h3>Methods</h3>");
			for (MethodInfo method : clazz.getMethods())
				sb.append(method.getName() + "<br />");
		}*/
		// ... include metrics
		//sb.append("<h3>Metrics</h3>");
		float result;
		for (MetricAttribute attribute : MetricRegistry.getInstance().getMetricAttributes()) {
			result = clazz.getMetricMeasurementVal(attribute);
			if (result != MetricMeasurement.DEFAULT_RESULT)
				sb.append("<strong>" + attribute.getName() + "</strong>" 
						+ " = " + result + "<br />");
		}
		// ... end
		sb.append("</html>");
		
		// update content label
		contentLabel.setText( sb.toString() );
		
		// update method listing table
		((ClassPropertiesTableModel)(methodTable.getModel())).setSubject(clazz);
		
		// update class composition component
		classCompositionComponent.setCurrentClass(clazz);
	}
	
}
