/*
 * StatefulWorkspacePane.java (JavaVis)
 * Copyright 2010 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.ui;

import java.awt.Component;

import javax.swing.JDesktopPane;

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
