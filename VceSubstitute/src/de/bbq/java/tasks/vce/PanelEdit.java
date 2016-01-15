package de.bbq.java.tasks.vce;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Rectangle;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

public class PanelEdit extends PanelAbstract {
	private static final long serialVersionUID = -2186316596347702853L;
	
	private final int labelWidth = 110;
	private final int buttonWidth = 100;
	private final int lineHeight = 20;
	private final int textFieldWidth = 220;	
	private JTextField topicTextField = new JTextField(), languageTextField = new JTextField(""), examNameTextField = new JTextField();
	private JCheckBox beamerCheckBox = new JCheckBox();;
	private JComboBox<Integer> hourStartComboBox = new JComboBox<>();
	private JLabel labelExamName;
	private JPanel contentPane;


	
	public PanelEdit() {
		setOpaque(true);
		setBackground(Color.GREEN);
		SpringLayout layout = new SpringLayout();
		setLayout(layout);
		
		ReadData (null);
		
				
		JButton a = new JButton();
		a.setText("HALLO WELT");
		a.setBounds(100, 100, 500, 40);
	}

	public void ReadData(Question question) {
		// TODO Auto-generated method stub
		labelExamName = new JLabel();
		labelExamName.setText(ExamenVerwaltung.getText("Exam"));
		labelExamName.setMinimumSize(new Dimension(lineHeight, labelWidth));
		labelExamName.setPreferredSize(new Dimension(lineHeight, labelWidth + (labelWidth / 2)));
		labelExamName.setMaximumSize(new Dimension(lineHeight, labelWidth * 2));	
		
		
//		examNameTextField.setText(question.getExam().toString());
		examNameTextField.setMinimumSize(new Dimension(lineHeight, textFieldWidth));
		examNameTextField.setMaximumSize(new Dimension(lineHeight, 4096));		
		
		
		
	}

	@Override
	public void ReadData() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void ReadData(ExamItemAbstract item) {
		// TODO Auto-generated method stub
		
	}

}
