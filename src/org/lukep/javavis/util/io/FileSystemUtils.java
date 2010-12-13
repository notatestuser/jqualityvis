/*
 * FileSystemUtils.java (JavaVis)
 * Copyright 2010 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.util.io;

import java.io.File;
import java.io.FileFilter;
import java.util.AbstractQueue;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Stack;
import java.util.concurrent.ArrayBlockingQueue;

public final class FileSystemUtils {

	public static ArrayList<File> ListFilesRecursive(File rootDirectory, final String[] extensions, IFileSystemScanObserver observer) {
		File currentDirectory;
		ArrayList<File> results = new ArrayList<File>();
		PriorityQueue<File> directoriesToScan = new PriorityQueue<File>();
		directoriesToScan.add(rootDirectory);
		FileFilter filter = new FileFilter() {
			
			@Override
			public boolean accept(File pathname) {
				if (pathname.isDirectory())
					return true;
				for (String ext : extensions) {
					if (pathname.getName().endsWith("." + ext))
						return true;
				}
				return false;
			}
		};
		
		while (directoriesToScan.size() > 0) {
			currentDirectory = directoriesToScan.remove();
			
			// alert observer about directory change
			if (observer != null)
				observer.notifyScanDirectoryChange(currentDirectory);
			
			File[] files = currentDirectory.listFiles(filter);
			if (files != null) {
				for (File f : files) {
					if (f.isDirectory()) {
						// push dir onto the directoriesToScan queue
						directoriesToScan.add(f);
					} else {
						// files are added to the results array
						results.add(f);
					}
				}
			}
		}
		
		return results;
	}
	
}
