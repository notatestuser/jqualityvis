/*
 * StatefulWorkspacePane.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.ui.swing;

import java.awt.Component;

import javax.swing.JDesktopPane;

import org.lukep.javavis.ui.IProgramStatusReporter;

public class StatefulWorkspacePane extends JDesktopPane {

	IProgramStatusReporter statusTarget;
	
	public StatefulWorkspacePane(IProgramStatusReporter statusTarget) throws Exception {
		super();
		
		this.statusTarget = statusTarget;
	}
	
	private IProgramStatusReporter getNearestStatusReporter() throws NullPointerException {
		Component c = getParent();
		
		while ( !(c instanceof IProgramStatusReporter) ) {
			c = c.getParent();
		}
		
		return (IProgramStatusReporter) c;
	}
	
	public void setProgramStatus(String status) {
		statusTarget.setProgramStatus(status);
	}
	
}
