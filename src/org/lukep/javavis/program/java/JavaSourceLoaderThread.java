/*
 * JavaSourceLoaderThread.java (JQualityVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.program.java;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
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
import org.lukep.javavis.program.generic.models.ProjectModel;
import org.lukep.javavis.ui.IProgramSourceObserver;
import org.lukep.javavis.util.io.FileSystemUtils;
import org.lukep.javavis.util.io.IFileSystemScanObserver;

/**
 * Following the construction of a list of "compilation units", the JavaSourceLoaderThread adds a custom 
 * JavaCodeProcessor (an implementation of AbstractProcessor) to the compiler’s CompilationTask. 
 * When the task is executed, the JavaCodeTreeVisitor scans the parsed tokens and calls methods in the GenericModelFactory 
 * to produce descendants of AbstractModel.
 */
public class JavaSourceLoaderThread implements ISourceLoaderThread {

	static final String[] JAVA_SOURCE_EXTENSIONS = { "java" };
	
	private StandardJavaFileManager fileManager;
	private DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
	private ProjectModel programStore;
	private Vector<IProgramSourceObserver> observers = new Vector<IProgramSourceObserver>();
	private File selectedDirectory;
	private int directoryCount;
	private List<String> compilerOptions;
	
	/**
	 * Instantiates a new JavaSourceLoaderThread.
	 *
	 * @param selectedDirectory the selected directory
	 * @param programStore the program store
	 * @param compilerOptions the compiler options
	 */
	public JavaSourceLoaderThread(File selectedDirectory, ProjectModel programStore, List<String> compilerOptions) {
		this.selectedDirectory = selectedDirectory;
		this.programStore = programStore;
		this.compilerOptions = compilerOptions;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		// initialise compiler and file manager
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		fileManager = compiler.getStandardFileManager(diagnostics, null, null); // TODO: add diagnostic listener
		
		// recursively scan through input directory to locate java files
		directoryCount = 0;
		ArrayList<File> inputFiles = 
			FileSystemUtils.listFilesRecursive(selectedDirectory, JAVA_SOURCE_EXTENSIONS, 
					new IFileSystemScanObserver() {
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
															compilerOptions, null, compilationUnits);
		LinkedList<AbstractProcessor> processors = new LinkedList<AbstractProcessor>();
		processors.add( new JavaCodeProcessor(observers, programStore) );
		compilationTask.setProcessors(processors);
		notifyStatusChange(compilationTask.call() 
						? "Compiled " + inputFiles.size() + " source files in " + directoryCount + " directories successfully." 
						: "Compilation failed (" + diagnostics.getDiagnostics().get(0).getMessage(Locale.ENGLISH) + ").");
		// TODO: fix crash when 0 source files discovered
		
		// perform post-processing to link models together and build up inheritance tree info, etc
		notifyStatusChange("Running post-processor...");
		new JavaCodePostProcessor(programStore).process();
		notifyStatusChange("Source code processed successfully.");
		
		statusFinished();
	}

	/* (non-Javadoc)
	 * @see org.lukep.javavis.program.ISourceLoaderThread#notifyStatusChange(java.lang.String)
	 */
	@Override
	public void notifyStatusChange(String message) {
		// override in caller
	}

	/* (non-Javadoc)
	 * @see org.lukep.javavis.program.ISourceLoaderThread#statusFinished()
	 */
	@Override
	public void statusFinished() {
		// override in caller
	}
	
	/**
	 * Adds an observer wishing to be notified about status updates.
	 *
	 * @param observer the observer to add
	 */
	public void addObserver(IProgramSourceObserver observer) {
		observers.add(observer);
	}
	
}
