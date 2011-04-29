/*
 * IFileSystemScanObserver.java (JQualityVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.util.io;

import java.io.File;

/**
 * An asynchronous update interface for receiving notifications
 * about directory scanning as it is carried out by the FileSystemUtils class.
 */
public interface IFileSystemScanObserver {

	/**
	 * This method is called when information pertaining to a currently active scan 
	 * of the file system is available to be collected.
	 *
	 * @param directory the directory currently being scanned
	 */
	public void notifyScanDirectoryChange(File directory);
	
}
