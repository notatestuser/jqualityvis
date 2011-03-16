/*
 * ModelSerializer.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.program.generic.helpers;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.swing.BoundedRangeModel;

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
	
	public static ProjectModel loadProjectFromFile(File file, boolean xml, boolean gzip, 
			final BoundedRangeModel progress) throws IOException, ClassNotFoundException {
		
		InputStream is = new FileInputStream(file);
		
		if (gzip)
			is = new GZIPInputStream(is);
		
		is = new BufferedInputStream(is) {
			private int readBytes = 0;
			private int maxBytes = 0;
			
			@Override
			public synchronized int read() throws IOException {
				updateProgress(1);
				return super.read();
			}

			@Override
			public int read(byte[] b) throws IOException {
				int bytes = super.read(b);
				updateProgress(bytes);
				return bytes;
			}

			@Override
			public synchronized int read(byte[] b, final int off, final int len)
					throws IOException {
				int bytes = super.read(b, off, len);
				updateProgress(bytes);
				return bytes;
			}
			
			@Override
			public synchronized long skip(long n) throws IOException {
				long bytes = super.skip(n);
				updateProgress(bytes);
				return bytes;
			}

			@Override
			public void close() throws IOException {
				progress.setValue(progress.getMaximum());
				super.close();
			}

			private void updateProgress(final long bytesRead) {
				readBytes += bytesRead;
				maxBytes = readBytes + getAvailableBytes();
				progress.setMaximum(maxBytes);
				progress.setValue(readBytes);
			}
			
			private int getAvailableBytes() {
				try {
					return available();
				} catch (IOException e) {
					return 0;
				}
			}
		};
		
		ObjectInputStream ois;
		if (xml) {
			XStream xstream = new XStream();
			setXStreamAliases(xstream);
			ois = xstream.createObjectInputStream(is);
		} else {
			ois = new ObjectInputStream(is);
		}
		ProjectModel project = (ProjectModel) ois.readObject();
		ois.close();
		
		return project;
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
