/*
 * FormValidationException.java (JQualityVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.util;

/**
 * Thrown when a Wizard form fails to validate.
 */
public class FormValidationException extends Exception {

	/**
	 * Instantiates a new form validation exception.
	 *
	 * @param field the field
	 * @param validationRule the validation rule
	 */
	public FormValidationException(String field, String validationRule) {
		super("Field '" + field + "' failed validation rule: " + validationRule);
	}

}
