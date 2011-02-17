/*
 * HeaderLabel.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.ui.swing;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.border.EtchedBorder;

public class HeaderLabel extends JLabel {

	public HeaderLabel(String text, Icon icon) {
		super(text);

		// set the header's border
		setBorder( BorderFactory.createCompoundBorder(
				BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), 
				BorderFactory.createEmptyBorder(1, 3, 1, 3)));
		
		// set the header's icon
		if (icon != null) {
			setIcon(icon);
		}
	}

	@Override
	protected void paintComponent(Graphics g) {

		Graphics2D g2d = (Graphics2D) g;
		
		int w = getWidth();
		int h = getHeight();
		
		Color c1 = getBackground();
		float[] hsbvals = Color.RGBtoHSB(
				c1.getRed(), c1.getGreen(), c1.getBlue(), null);
		Color c2 = Color.getHSBColor(
				hsbvals[0], hsbvals[1], Math.max(hsbvals[2] + 0.85f, 1f));
		
		GradientPaint gp = new GradientPaint(
				0, 0, c1,
				0, h, c2);
		
		g2d.setPaint(gp);
		g2d.fillRect(0, 0, w, h);
		
		super.paintComponent(g);
	}

}
