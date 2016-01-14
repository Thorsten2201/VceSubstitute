package de.bbq.java.tasks.vce;

import java.awt.Color;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

public class PanelEdit2 extends JPanel {
	private static final long serialVersionUID = -2186316596347702853L;

	public PanelEdit2() {
		setOpaque(true);
		setBackground(Color.BLUE);
		SpringLayout layout = new SpringLayout();
		setLayout(layout);
		JButton a = new JButton();
		a.setText("HALLO WELT");
		a.setBounds(100, 100, 500, 40);
		
		this.add(a);
		layout.putConstraint(SpringLayout.EAST, a, 10, SpringLayout.EAST, this);
	}
}
