/*
 * WorkspaceContext.java (JQualityVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.ui.swing;

import java.util.Observable;

import org.lukep.javavis.metrics.MetricAttribute;
import org.lukep.javavis.program.generic.models.IGenericModelNode;
import org.lukep.javavis.program.generic.models.ProjectModel;
import org.lukep.javavis.visualisation.Visualisation;

/**
 * The workspace context object handles events that occur in a workspace.
 */
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
	
	/**
	 * Gets the model store.
	 *
	 * @return the model store
	 */
	public ProjectModel getModelStore() {
		return modelStore;
	}
	
	/**
	 * Sets the model store.
	 *
	 * @param modelStore the new model store
	 */
	public void setModelStore(ProjectModel modelStore) {
		this.modelStore = modelStore;
		fireEvent(ChangeEvent.MODELSTORE_CHANGE);
	}
	
	/**
	 * Gets the metric.
	 *
	 * @return the metric
	 */
	public MetricAttribute getMetric() {
		return metric;
	}
	
	/**
	 * Sets the metric.
	 *
	 * @param metric the new metric
	 */
	public void setMetric(MetricAttribute metric) {
		this.metric = metric;
		fireEvent(ChangeEvent.METRIC_CHANGE);
	}
	
	/**
	 * Gets the visualisation.
	 *
	 * @return the visualisation
	 */
	public Visualisation getVisualisation() {
		return visualisation;
	}
	
	/**
	 * Sets the visualisation.
	 *
	 * @param visualisation the new visualisation
	 */
	public void setVisualisation(Visualisation visualisation) {
		this.visualisation = visualisation;
		fireEvent(ChangeEvent.VISUALISATION_CHANGE);
	}
	
	/**
	 * Gets the subjects.
	 *
	 * @return the subjects
	 */
	public IGenericModelNode[] getSubjects() {
		return subjects;
	}
	
	/**
	 * Sets the subjects.
	 *
	 * @param subjects the new subjects
	 */
	public void setSubjects(IGenericModelNode[] subjects) {
		this.subjects = subjects;
		fireEvent(ChangeEvent.SUBJECT_CHANGE);
	}
	
	/**
	 * Sets the subject.
	 *
	 * @param subject the new subject
	 */
	public void setSubject(IGenericModelNode subject) {
		setSubjects(new IGenericModelNode[] { subject });
	}
	
	/**
	 * Gets the selected item.
	 *
	 * @return the selected item
	 */
	public IGenericModelNode getSelectedItem() {
		return selectedItem;
	}
	
	/**
	 * Sets the selected item.
	 *
	 * @param selectedItem the new selected item
	 */
	public void setSelectedItem(IGenericModelNode selectedItem) {
		this.selectedItem = selectedItem;
		fireEvent(ChangeEvent.SELECTED_CHANGE);
	}
	
	/**
	 * Fire a ChangeEvent.
	 *
	 * @param event the event to fire
	 */
	private void fireEvent(ChangeEvent event) {
		setChanged();
		notifyObservers(event);
	}
	
}
