/*
 * WorkspaceContext.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.ui.swing;

import java.util.HashMap;
import java.util.Vector;

import org.lukep.javavis.metrics.IMeasurable;
import org.lukep.javavis.metrics.MetricAttribute;
import org.lukep.javavis.program.generic.models.ProgramModelStore;
import org.lukep.javavis.program.generic.models.Relationship;
import org.lukep.javavis.visualisation.Visualisation;

public class WorkspaceContext {

	protected ProgramModelStore modelStore;
	protected HashMap<IMeasurable, Vector<Relationship>> relations; // one-to-many
	protected IMeasurable subject;
	
	protected MetricAttribute metric;
	protected Visualisation visualisation;
	
	public ProgramModelStore getModelStore() {
		return modelStore;
	}
	public void setModelStore(ProgramModelStore modelStore) {
		this.modelStore = modelStore;
	}
	public HashMap<IMeasurable, Vector<Relationship>> getRelations() {
		return relations;
	}
	public void setRelations(
			HashMap<IMeasurable, Vector<Relationship>> relations) {
		this.relations = relations;
	}
	public IMeasurable getSubject() {
		return subject;
	}
	public void setSubject(IMeasurable subject) {
		this.subject = subject;
	}
	public MetricAttribute getMetric() {
		return metric;
	}
	public void setMetric(MetricAttribute metric) {
		this.metric = metric;
	}
	public Visualisation getVisualisation() {
		return visualisation;
	}
	public void setVisualisation(Visualisation visualisation) {
		this.visualisation = visualisation;
	}
	
}
