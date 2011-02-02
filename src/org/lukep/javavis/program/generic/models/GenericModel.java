/*
 * GenericModel.java (JavaVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.program.generic.models;

import java.io.Serializable;

abstract class GenericModel implements Serializable {

	protected GenericModelSourceLang sourceLang;

	public GenericModel(GenericModelSourceLang sourceLang) {
		super();
		this.sourceLang = sourceLang;
	}

	public GenericModelSourceLang getSourceLang() {
		return sourceLang;
	}
	
}
