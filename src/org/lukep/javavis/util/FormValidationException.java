/*
 * FormValidationException.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.util;

public class FormValidationException extends Exception {

	public FormValidationException(String field, String validationRule) {
		super("Field '" + field + "' failed validation rule: " + validationRule);
	}

}
