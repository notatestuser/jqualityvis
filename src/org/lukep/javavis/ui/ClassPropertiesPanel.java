/*
 * ClassPropertiesPanel.java (JavaVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.ui;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.lukep.javavis.program.generic.models.ClassInfo;

public class ClassPropertiesPanel extends JPanel {
	
	protected ClassInfo currentClass;

	public ClassPropertiesPanel() {
		super( new BorderLayout() );
		
		// create title label
		JLabel titleLabel = new JLabel("Class Properties");
		add(titleLabel, BorderLayout.NORTH);
	}
	
}
