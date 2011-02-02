/*
 * ClassPropertiesPanel.java (JavaVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.ui;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import org.lukep.javavis.metrics.MetricAttribute;
import org.lukep.javavis.program.generic.models.MethodInfo;
import org.lukep.javavis.program.generic.models.measurable.MeasurableClassInfo;

public class ClassPropertiesPanel extends JPanel {
	
	protected JLabel contentLabel;
	
	protected MeasurableClassInfo currentClass;

	public ClassPropertiesPanel() {
		super( new BorderLayout() );
		
		// create title label
		//JLabel titleLabel = new JLabel("Selected Class Properties");
		//add(titleLabel, BorderLayout.NORTH);
		
		// create content label
		contentLabel = new JLabel();
		contentLabel.setVerticalAlignment(SwingConstants.TOP);
		JScrollPane contentLabelPane = new JScrollPane(contentLabel);
		contentLabelPane.setBorder(null);
		add(contentLabelPane, BorderLayout.CENTER);
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
		if (clazz.getMethodCount() > 0) {
			sb.append("<h3>Methods</h3>");
			for (MethodInfo method : clazz.getMethods())
				sb.append(method.getName() + "<br />");
		}
		// ... include metrics
		sb.append("<h3>Metrics</h3>");
		for (MetricAttribute attribute : MetricAttribute.values())
			sb.append("<strong>" + attribute.toString() + "</strong>" 
					+ " = " + clazz.getMetricMeasurementVal(attribute)
					+ "<br />");
		// ... end
		sb.append("</html>");
		
		// update content label
		contentLabel.setText( sb.toString() );
	}
	
}
