package de.bbq.java.tasks.vce;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

public class PanelEdit extends JPanel {
	private static final long serialVersionUID = -2186316596347702853L;

	public PanelEdit() {
		setOpaque(true);
		setBackground(Color.GREEN);
		SpringLayout layout = new SpringLayout();
//		setLayout(layout);
		setLayout(new BorderLayout());
		PanelEdit2 a = new PanelEdit2();
		this.add(a,BorderLayout.EAST);
		a.setBounds(200, 100, 300, 200);
		layout.putConstraint(SpringLayout.EAST, a, 10, SpringLayout.EAST, this);
	}
}
