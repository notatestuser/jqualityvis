/*
 * IFileSystemScanObserver.java (JavaVis)
 * Copyright 2010 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.util.io;

import java.io.File;

public interface IFileSystemScanObserver {

	public void notifyScanDirectoryChange(File directory);
	
}
