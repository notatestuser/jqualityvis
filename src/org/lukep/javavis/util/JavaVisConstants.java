/*
 * JavaVisConstants.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.util;

public class JavaVisConstants {
	
	public static final String APP_NAME					= "JQualityVis";
	public static final String APP_VERSION				= "pre-delivery alpha";
	public static final String APP_WEBPAGE				= "http://www.lancs.ac.uk/ug/plaster";
	
	public static final String SUPPORTED_LANG_JAVA_6	= "Java 6";
	
	public static final String DEFAULT_PACKAGE_NAME 	= "(default package)";
	public static final String DEFAULT_CONSTRUCTOR_NAME = "<init>";
	
	public static final String WORKING_DIR 				= System.getProperty("user.dir");
	
	public static final String METRICS_FILE_NAME 		= WORKING_DIR + "/config/metrics.xml";
	public static final String QUALITYMODELS_FILE_NAME  = WORKING_DIR + "/config/qualityModels.xml";
	public static final String VISUALISATIONS_FILE_NAME = WORKING_DIR + "/config/visualisations.xml";
	
	public static final String WELCOME_HTML_URL			= WORKING_DIR + "/assets/html/welcome.html";
	public static final String HELP_HTML_URL			= WORKING_DIR + "/assets/html/help.html";
	
	public static final String IMG_DECOY_PANEL_BG		= WORKING_DIR + "/assets/img/decoyPanelBackground.png";
	
	public static final String ICON_PROJECT_EXPLORER	= WORKING_DIR + "/assets/icons/folder.png";
	public static final String ICON_QUALITY_ANALYSIS	= WORKING_DIR + "/assets/icons/chart_curve.png";
	public static final String ICON_METRICS				= WORKING_DIR + "/assets/icons/chart_bar.png";
	public static final String ICON_WARNINGS			= WORKING_DIR + "/assets/icons/exclamation.png";
	public static final String ICON_PROJECT_WIZARD_NEW	= WORKING_DIR + "/assets/icons/application-blue.png";
	public static final String ICON_PROJECT_WIZARD_OPEN	= WORKING_DIR + "/assets/icons/folder-horizontal.png";
	public static final String ICON_PROJECT_WIZARD_SAVE = WORKING_DIR + "/assets/icons/document-text.png";
	
	public static final String ICON_MENU_PROJECT_CREATE	= WORKING_DIR + "/assets/icons/document.png";
	public static final String ICON_MENU_PROJECT_OPEN	= WORKING_DIR + "/assets/icons/folder-import.png";
	public static final String ICON_MENU_PROJECT_SAVE	= WORKING_DIR + "/assets/icons/disk-black.png";
	public static final String ICON_MENU_PROJECT_CLOSE	= WORKING_DIR + "/assets/icons/cross-button.png";
	public static final String ICON_MENU_HELP			= WORKING_DIR + "/assets/icons/question-frame.png";
	public static final String ICON_CONFIG_DELETE_ENT   = WORKING_DIR + "/assets/icons/cross-circle.png";
	public static final String ICON_CONFIG_SAVE_ENT     = WORKING_DIR + "/assets/icons/arrow-180-medium.png";
	
	public static final String HEADING_PROJECT_EXPLORER	= "Project Explorer";
	public static final String HEADING_QUALITY_ANALYSIS	= "Quality Analysis";
	public static final String HEADING_METRICS			= "Metrics";
	public static final String HEADING_WARNINGS			= "Warnings";
	
	public static final String METRIC_NUM_OF_STATEMENTS	= "NOS";
	public static final String METRIC_CYCLO_COMPLEX 	= "CC";
	public static final String METRIC_CYCLO_COMPLEX_AVG = "CC_AVG";
	public static final String METRIC_CYCLO_COMPLEX_MAX	= "CC_MAX";
	
	public static final String METRIC_APPLIES_TO_PROJCT	= "project";
	public static final String METRIC_APPLIES_TO_PKG	= "package";
	public static final String METRIC_APPLIES_TO_CLASS  = "class";
	public static final String METRIC_APPLIES_TO_METHOD = "method";
	public static final String METRIC_APPLIES_TO_VAR    = "variable";
	
	public static final String MODEL_MODIFIER_PUBLIC	= "public";
	public static final String MODEL_MODIFIER_PROTECTED	= "protected";
	public static final String MODEL_MODIFIER_PRIVATE	= "private";
	public static final String MODEL_MODIFIER_FINAL		= "final";
	public static final String MODEL_MODIFIER_ABSTRACT	= "abstract";
	public static final String MODEL_MODIFIER_NATIVE	= "native";
	public static final String MODEL_MODIFIER_STATIC	= "static";

}
