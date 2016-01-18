package de.bbq.java.tasks.vce;

import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

public class PanelSingleAnswer extends JPanel {
	PanelSingleAnswer() {
		setOpaque(false);
		setBackground(Color.GREEN);
//		this.setLayout(new SpringLayout());
		this.setLayout(new GridLayout(1,1));
	this.add(new JButton("Hallo"));
	}

}
