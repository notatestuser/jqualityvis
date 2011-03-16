/*
 * ModelSerializer.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.program.generic.helpers;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

import org.lukep.javavis.program.generic.models.ClassAncestor;
import org.lukep.javavis.program.generic.models.ClassModel;
import org.lukep.javavis.program.generic.models.MethodModel;
import org.lukep.javavis.program.generic.models.PackageModel;
import org.lukep.javavis.program.generic.models.ProjectModel;
import org.lukep.javavis.program.generic.models.Relationship;
import org.lukep.javavis.program.generic.models.VariableModel;
import org.lukep.javavis.util.JavaVisConstants;

import com.thoughtworks.xstream.XStream;

public class ModelSerializer {

	public static final String XML_ROOT_ELEMENT_NAME = JavaVisConstants.APP_NAME + "ProjectFile";
	
	public static void serializeProjectToFile(File file, ProjectModel project, boolean xml, boolean gzip) 
			throws IOException {
		
		OutputStream os = new FileOutputStream(file);
		
		if (gzip)
			os = new GZIPOutputStream(os);
		
		os = new BufferedOutputStream(os);
		
		ObjectOutputStream oos;
		if (xml) {
			XStream xstream = new XStream();
			setXStreamAliases(xstream);
			oos = xstream.createObjectOutputStream(os, XML_ROOT_ELEMENT_NAME);
		} else {
			oos = new ObjectOutputStream(os);
		}
		oos.writeObject(project);
		oos.close();
	}
	
	private static void setXStreamAliases(XStream xstream) {
		xstream.alias("project", ProjectModel.class);
		xstream.alias("package", PackageModel.class);
		xstream.alias("class", ClassModel.class);
		xstream.alias("ancestor", ClassAncestor.class);
		xstream.alias("relationship", Relationship.class);
		xstream.alias("method", MethodModel.class);
		xstream.alias("variable", VariableModel.class);
	}
	
}
