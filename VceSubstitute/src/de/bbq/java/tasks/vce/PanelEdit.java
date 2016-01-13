package de.bbq.java.tasks.vce;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

public class PanelEdit extends JPanel {
	private static final long serialVersionUID = -2186316596347702853L;

	public PanelEdit() {
		setOpaque(false);
		setBackground(Color.GREEN);
		this.setLayout(new GridLayout(1, 1));
	}
}
