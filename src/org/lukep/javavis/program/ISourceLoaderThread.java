/*
 * ISourceLoaderThread.java (JavaVis)
 * Copyright 2010 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.program;

public interface ISourceLoaderThread extends Runnable {

	public abstract void notifyStatusChange(String message);
	public abstract void statusFinished();
	
}
