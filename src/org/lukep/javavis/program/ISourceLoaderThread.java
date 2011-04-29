/*
 * ISourceLoaderThread.java (JQualityVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.program;

/**
 * This represents a threaded Runnable process whose job is to parse a bunch of source code files and convert them 
 * in to abstract program models. This interface allows a class to listen for new events when this is carried out.
 */
public interface ISourceLoaderThread extends Runnable {

	/**
	 * Notify of a status change (e.g. "Compiling 66 source files in xx")
	 *
	 * @param message the message to display as status feedback
	 */
	public abstract void notifyStatusChange(String message);
	
	/**
	 * The compilation process has finished.
	 */
	public abstract void statusFinished();
	
}
