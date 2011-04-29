/*
 * MetricCalculatorDialog.java (JQualityVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.ui.swing;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.lukep.javavis.metrics.IMeasurableNode;
import org.lukep.javavis.metrics.MetricAttribute;
import org.lukep.javavis.metrics.MetricMeasurement;
import org.lukep.javavis.metrics.MetricRegistry;
import org.lukep.javavis.program.generic.models.ClassModel;
import org.lukep.javavis.program.generic.models.IGenericModelNodeVisitor;
import org.lukep.javavis.program.generic.models.MethodModel;
import org.lukep.javavis.program.generic.models.PackageModel;
import org.lukep.javavis.program.generic.models.ProjectModel;
import org.lukep.javavis.program.generic.models.Relationship;
import org.lukep.javavis.program.generic.models.VariableModel;
import org.lukep.javavis.thirdparty.WrapLayout;

/**
 * The Class MetricCalculatorDialog.
 */
public class MetricCalculatorDialog extends JFrame implements ActionListener {

	private WorkspacePane workspace;
	
	private Set<String> targets = new HashSet<String>();
	
	List<MetricAttribute> metrics = new Vector<MetricAttribute>();
	
	Set<String> metricInternalNameExclusions = new HashSet<String>();
	
	private JButton btnCalculate;
	private JEditorPane epOutput;
	
	private JPanel pnlNorth;
	private JPanel pnlMetrics;
	
	/**
	 * Create the dialog.
	 *
	 * @param workspace the workspace
	 */
	public MetricCalculatorDialog(WorkspacePane workspace) {
		setTitle("Batch Metric Calculator for " + workspace.getContext().getModelStore());
		setBounds(100, 100, 1024, 768);
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		this.workspace = workspace;
		
		epOutput = new JEditorPane();
		getContentPane().add(new JScrollPane(epOutput), BorderLayout.CENTER);
		epOutput.setContentType("text/html");
		
		btnCalculate = new JButton("Display Metrics");
		getContentPane().add(btnCalculate, BorderLayout.SOUTH);
		
		pnlNorth = new JPanel();
		getContentPane().add(pnlNorth, BorderLayout.NORTH);
		pnlNorth.setLayout(new BorderLayout(0, 0));
		
		JPanel pnlTargets = new JPanel();
		pnlNorth.add(pnlTargets, BorderLayout.NORTH);
		pnlTargets.add(new JLabel("Targets: "));
		fillTargetPanel(pnlTargets);
		
		pnlMetrics = new JPanel();
		pnlMetrics.setLayout(new WrapLayout());
		pnlNorth.add(pnlMetrics, BorderLayout.CENTER);
		
		JLabel lblTargets = new JLabel("Metrics: ");
		pnlMetrics.add(lblTargets);
		btnCalculate.addActionListener(this);
		fillMetricsPanel(pnlMetrics);
	}
	
	/**
	 * Fill target panel.
	 *
	 * @param pnlTargets the pnl targets
	 */
	private void fillTargetPanel(JPanel pnlTargets) {
		final Set<String> targets = MetricRegistry.getInstance().getSupportedMetricTargets();
		JCheckBox cbx;
		for (String target : targets) {
			cbx = new JCheckBox(target);
			pnlTargets.add(cbx);
			cbx.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					JCheckBox cbx = (JCheckBox) e.getSource();
					if (cbx.isSelected())
						MetricCalculatorDialog.this.targets.add(
								cbx.getText());
					else
						MetricCalculatorDialog.this.targets.remove(
								cbx.getText());
				}
			});
		}
	}
	
	/**
	 * Fill metrics panel.
	 *
	 * @param pnlMetrics the pnl metrics
	 */
	private void fillMetricsPanel(JPanel pnlMetrics) {
		JCheckBox cbx;
		for (MetricAttribute m : MetricRegistry.getInstance().getMetricAttributes()) {
			cbx = new JCheckBox(m.getInternalName());
			cbx.setSelected(true);
			pnlMetrics.add(cbx);
			cbx.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					JCheckBox c = (JCheckBox) e.getSource();
					if (c.isSelected())
						metricInternalNameExclusions.remove(c.getText());
					else
						metricInternalNameExclusions.add(c.getText());
				}
			});
		}
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {

		if (btnCalculate == e.getSource()) {
			// calculate metrics and output to the editor-pane
			epOutput.setText("<html><b>Performing calculation...</b></html>");
			displayCalculations();
		}
	}
	
	/**
	 * Display calculations.
	 */
	private void displayCalculations() {
		metrics.clear();
		
		ProjectModel project = workspace.getContext().getModelStore();
		final StringBuilder sb = new StringBuilder();

		project.accept(new IGenericModelNodeVisitor() {
			
			private synchronized void measureTarget(IMeasurableNode model) {
				if (targets.contains(model.getModelTypeName())) {
					sb.append("<tr><td style=\"font-weight: bold;\">" + model.getQualifiedName() + "</td>");
					
					String modelTypeName = model.getModelTypeName();
					if (targets.contains(modelTypeName)) {
						// run calculation of all applicable metrics
						Collection<MetricAttribute> metricsToCalc = 
							MetricRegistry.getInstance().getSupportedMetrics(modelTypeName);
						
						for (MetricAttribute m : metricsToCalc)
							if (!metrics.contains(m)
									&& !metricInternalNameExclusions.contains(m.getInternalName()))
								metrics.add(m);
						
						MetricMeasurement mm;
						for (MetricAttribute m : metrics) {
							if (m.testAppliesTo(modelTypeName)) {
								mm = m.measureTargetCached(model);
								sb.append("<td>" + mm.getRoundedResult(3) + "</td>");
							} else {
								sb.append("<td></td>");
							}
						}
					}
					
					sb.append("</tr>");
				}
				
				// visit child elements
				if (model.getChildren() != null)
					for (Relationship child : model.getChildren())
						child.getTarget().accept(this);
			}
			
			@Override
			public void visit(VariableModel model) {
				measureTarget(model);
			}
			
			@Override
			public void visit(MethodModel model) {
				measureTarget(model);
			}
			
			@Override
			public void visit(ClassModel model) {
				measureTarget(model);
			}
			
			@Override
			public void visit(PackageModel model) {
				measureTarget(model);
			}
			
			@Override
			public void visit(ProjectModel model) {
				measureTarget(model);
			}
		});
		
		StringBuilder headersb = new StringBuilder("<html><table style=\"font-size: 9px\">");
		headersb.append("<tr style=\"font-weight: bold;\"><td></td>");
		for (MetricAttribute m : metrics) {
			headersb.append("<td>" + m.getInternalName() + "</td>");
		}
		headersb.append("</tr>");
		sb.append("</table></html>");
		
		epOutput.setText(headersb.toString() + sb.toString());
	}

}
