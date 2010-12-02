/*
 * VisualisationDesktopPane.java (JavaVis)
 * Copyright 2010 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.ui;

import java.awt.Color;
import java.io.File;

import org.lukep.javavis.visualisation.java.JavaSourceLoaderThread;

public class VisualisationDesktopPane extends StatefulWorkspacePane {
	
	public VisualisationDesktopPane(IProgramStatusReporter statusTarget) throws Exception {
		super(statusTarget);
		
		setBackground(Color.WHITE);
		setLayout(null);
	}

	public void loadCodeBase(File selectedDirectory) {
		new Thread(new JavaSourceLoaderThread(selectedDirectory) {

			@Override
			public void notifyStatusChange(String message) {
				setProgramStatus(message);
			}

			@Override
			public void statusFinished() {
				// TODO Auto-generated method stub
			}
			
		}).start();
	}

}
