/*
 * JavaSourceLoaderThread.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.program.java;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Vector;

import javax.annotation.processing.AbstractProcessor;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.lukep.javavis.program.ISourceLoaderThread;
import org.lukep.javavis.ui.IProgramSourceObserver;
import org.lukep.javavis.util.io.FileSystemUtils;
import org.lukep.javavis.util.io.IFileSystemScanObserver;

public class JavaSourceLoaderThread implements ISourceLoaderThread {

	static final String[] JAVA_SOURCE_EXTENSIONS = { "java" };
	
	protected StandardJavaFileManager fileManager;
	protected DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
	protected Vector<IProgramSourceObserver> observers = new Vector<IProgramSourceObserver>();
	protected File selectedDirectory;
	protected int directoryCount;
	
	public JavaSourceLoaderThread(File selectedDirectory) {
		this.selectedDirectory = selectedDirectory;
	}

	@Override
	public void run() {
		// initialise compiler and file manager
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		fileManager = compiler.getStandardFileManager(diagnostics, null, null); // TODO: add diagnostic listener
		
		// recursively scan through input directory to locate java files
		//Collection<File> inputFiles = 
		//	FileUtils.listFiles(selectedDirectory, new String[] { "java" }, true);
		directoryCount = 0;
		ArrayList<File> inputFiles = 
			FileSystemUtils.ListFilesRecursive(selectedDirectory, JAVA_SOURCE_EXTENSIONS, new IFileSystemScanObserver() {
				
				@Override
				public void notifyScanDirectoryChange(File directory) {
					notifyStatusChange("Scanning for source files in " + directory.toString() + "...");
					directoryCount++;
				}
			});
		assert (inputFiles.size() > 0);
		
		// build compilation unit list
		Iterable<? extends JavaFileObject> compilationUnits = 
			fileManager.getJavaFileObjectsFromFiles(inputFiles);
		
		// perform compilation of source files
		notifyStatusChange("Compiling " + inputFiles.size() + " source files in " + directoryCount + " directories...");
		CompilationTask compilationTask = compiler.getTask(null, fileManager, diagnostics, 
															null, null, compilationUnits);
		LinkedList<AbstractProcessor> processors = new LinkedList<AbstractProcessor>();
		processors.add( new JavaCodeProcessor(observers) );
		compilationTask.setProcessors(processors);
		notifyStatusChange(compilationTask.call() 
						? "Compiled " + inputFiles.size() + " source files in " + directoryCount + " directories successfully." 
						: "Compilation failed (" + diagnostics.getDiagnostics().get(0).getMessage(Locale.ENGLISH) + ").");
		// TODO: fix crash when 0 source files discovered
		
		
		
		statusFinished();
	}

	@Override
	public void notifyStatusChange(String message) {
		// override in caller
	}

	@Override
	public void statusFinished() {
		// override in caller
	}
	
	public void addObserver(IProgramSourceObserver observer) {
		observers.add(observer);
	}
	
}
