/*
 * IProgramSourceObserver.java (JavaVis)
 * Copyright 2010 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.ui;

import com.sun.tools.javac.code.Symbol.ClassSymbol;

public interface IProgramSourceObserver {

	public void notifyFindClass(ClassSymbol clazz);
	
}
