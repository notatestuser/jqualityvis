/*
 * IFileSystemScanObserver.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.util.io;

import java.io.File;

public interface IFileSystemScanObserver {

	public void notifyScanDirectoryChange(File directory);
	
}
