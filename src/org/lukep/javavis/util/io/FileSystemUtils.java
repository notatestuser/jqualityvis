/*
 * FileSystemUtils.java (JavaVis)
 * Copyright 2010 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.util.io;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Stack;

public final class FileSystemUtils {

	public static ArrayList<File> ListFilesRecursive(File rootDirectory, final String[] extensions, IFileSystemScanObserver observer) {
		File currentDirectory;
		ArrayList<File> results = new ArrayList<File>();
		Stack<File> directoriesToScan = new Stack<File>();
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
			currentDirectory = directoriesToScan.pop();
			
			// alert observer about directory change
			if (observer != null)
				observer.notifyScanDirectoryChange(currentDirectory);
			
			File[] files = currentDirectory.listFiles(filter);
			if (files != null) {
				for (File f : files) {
					if (f.isDirectory()) {
						// push dirs onto the directoriesToScan stack
						directoriesToScan.push(f);
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
