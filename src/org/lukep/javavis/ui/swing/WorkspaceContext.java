/*
 * WorkspaceContext.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.ui.swing;

import java.util.Observable;

import org.lukep.javavis.metrics.MetricAttribute;
import org.lukep.javavis.program.generic.models.IGenericModelNode;
import org.lukep.javavis.program.generic.models.ProjectModel;
import org.lukep.javavis.visualisation.Visualisation;

public class WorkspaceContext extends Observable {

	public static enum ChangeEvent {
		MODELSTORE_CHANGE, SUBJECT_CHANGE, METRIC_CHANGE, 
		VISUALISATION_CHANGE, SELECTED_CHANGE
	}
	
	///////////////////////////////////////////////////////
	
	protected ProjectModel modelStore;
	
	protected MetricAttribute metric;
	protected Visualisation visualisation;
	
	protected IGenericModelNode[] subjects;
	
	protected IGenericModelNode selectedItem;
	
	///////////////////////////////////////////////////////
	
	public ProjectModel getModelStore() {
		return modelStore;
	}
	
	public void setModelStore(ProjectModel modelStore) {
		this.modelStore = modelStore;
		fireEvent(ChangeEvent.MODELSTORE_CHANGE);
	}
	
	public MetricAttribute getMetric() {
		return metric;
	}
	
	public void setMetric(MetricAttribute metric) {
		this.metric = metric;
		fireEvent(ChangeEvent.METRIC_CHANGE);
	}
	
	public Visualisation getVisualisation() {
		return visualisation;
	}
	
	public void setVisualisation(Visualisation visualisation) {
		this.visualisation = visualisation;
		fireEvent(ChangeEvent.VISUALISATION_CHANGE);
	}
	
	public IGenericModelNode[] getSubjects() {
		return subjects;
	}
	
	public void setSubjects(IGenericModelNode[] subjects) {
		this.subjects = subjects;
		fireEvent(ChangeEvent.SUBJECT_CHANGE);
	}
	
	public void setSubject(IGenericModelNode subject) {
		setSubjects(new IGenericModelNode[] { subject });
	}
	
	public IGenericModelNode getSelectedItem() {
		return selectedItem;
	}
	
	public void setSelectedItem(IGenericModelNode selectedItem) {
		this.selectedItem = selectedItem;
		fireEvent(ChangeEvent.SELECTED_CHANGE);
	}
	
	private void fireEvent(ChangeEvent event) {
		setChanged();
		notifyObservers(event);
	}
	
}
