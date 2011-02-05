/*
 * ClassCompositionComponent.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.ui.swing;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JComponent;

import org.lukep.javavis.metrics.MetricAttribute;
import org.lukep.javavis.program.generic.models.measurable.MeasurableClassInfo;
import org.lukep.javavis.program.generic.models.measurable.MeasurableMethodInfo;

public class ClassCompositionComponent extends JComponent {

	private static final Color BACK_COLOR	= Color.gray;
	private static final Color BORDER_COLOR = Color.black;
	private static final Color LABEL_COLOR	= Color.white;
	
	protected MeasurableClassInfo currentClass;

	public ClassCompositionComponent(MeasurableClassInfo currentClass) {
		super();
		this.currentClass = currentClass;
	}
	
	public void setCurrentClass(MeasurableClassInfo clazz) {
		currentClass = clazz;
		repaint();
	}

	@Override
	public void paint(Graphics g) {
		if (currentClass != null) {
			// start out with a blank canvas
			g.setColor(BACK_COLOR);
			g.fillRect(0, 0, getWidth() - 1, getHeight() - 1);
			
			// add the blocks representing methods
			int pixelsPerStatement = (int) Math.ceil(currentClass.getMetricMeasurement(
					MetricAttribute.NUMBER_OF_STATEMENTS).getResult());
			pixelsPerStatement = pixelsPerStatement > 0 ? 
					(int) Math.ceil(getHeight() / (double)pixelsPerStatement) : getHeight();
			
			int rectY, rectHeight, lastY = 0;
			MeasurableMethodInfo method;
			float complexity;
			for (int i = 0; i < currentClass.getMethodCount(); i++) {
				method = (MeasurableMethodInfo)currentClass.getMethods().get(i);
				rectHeight = (int)(pixelsPerStatement * 
						method.getMetricMeasurement(MetricAttribute.NUMBER_OF_STATEMENTS).getResult());
				rectY = lastY;
				lastY += rectHeight;
				g.setColor(BORDER_COLOR);
				g.drawRect(0, rectY, getWidth() - 1, rectHeight);
				
				// TODO revise this awfulness - move metrics to XML
				complexity = method.getMetricMeasurement(MetricAttribute.MCCABE_CYCLOMATIC_COMPLEXITY).getResult();
				g.setColor( new Color(Math.min((int)((complexity / 30.0) * 255), 255), 170, 109) );
				g.fillRect(0 + 1 , rectY + 1 , getWidth() - 2, rectHeight - 1);
			}
			g.setColor(BORDER_COLOR);
			g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
			
			// draw class name
			g.setColor(LABEL_COLOR);
			g.drawString(currentClass.getSimpleName(), 5, 10);
		}
		super.paint(g);
	}
	
}
