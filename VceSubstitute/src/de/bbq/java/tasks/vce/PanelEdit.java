package de.bbq.java.tasks.vce;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;

public class PanelEdit extends PanelAbstract {
	private static final long serialVersionUID = -2186316596347702853L;

	private final int labelWidth = 210;
	private final int buttonWidth = 100;
	private final int lineHeight = 20;
	private final int textFieldWidth = 220;

	private JTextField languageTextField = new JTextField("", 1),
			examNameTextField = new JTextField("", 1),
			numberTextField = new JTextField("", 1),
			imageLineTextField = new JTextField("", 1),
			questionTextField = new JTextField("", 1),
			imagePathTextField = new JTextField("", 1),
			explanationTextField = new JTextField("", 1),
			descriptionTextField = new JTextField("", 3);
	private JCheckBox beamerCheckBox = new JCheckBox();;
	private JComboBox<Integer> hourStartComboBox = new JComboBox<>();

	private SpringLayout layout = null;
	private ExamItemAbstract selectedItem;

	public PanelEdit() {
		setOpaque(false);
		setBackground(Color.GREEN);
		this.layout = new SpringLayout();
		setLayout(this.layout);
		// contentPane = (JPanel) this;

		// /////////////////////////////////////////////////////////////////////////
		JLabel labelExamName = new JLabel();
		labelExamName.setText(ExamenVerwaltung.getText("Exam"));
		// labelExamName.setMinimumSize(new Dimension(lineHeight, labelWidth));
		// labelExamName.setPreferredSize(new Dimension(lineHeight, labelWidth
		// + (labelWidth / 2)));
		// labelExamName.setMaximumSize(new Dimension(lineHeight, labelWidth *
		// 2));
		labelExamName.setBounds(5, 5, lineHeight, labelWidth * 2);
		this.add(labelExamName);

		// examNameTextField.setText(question.getExam().toString());
		examNameTextField.setMinimumSize(new Dimension(lineHeight,
				textFieldWidth));
		examNameTextField.setMaximumSize(new Dimension(lineHeight, 4096));
		labelExamName.setLabelFor(examNameTextField);
		this.add(examNameTextField);

		// Adjust constraints for the label so it's at (5,5).
		layout.putConstraint(SpringLayout.WEST, labelExamName, 5,
				SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.NORTH, labelExamName, 5,
				SpringLayout.NORTH, this);

		// Adjust constraints for the text field so it's at
		// (<label's right edge> + 5, 5).
		layout.putConstraint(SpringLayout.WEST, examNameTextField, 5,
				SpringLayout.EAST, labelExamName);
		layout.putConstraint(SpringLayout.NORTH, examNameTextField, 5,
				SpringLayout.NORTH, this);

		// Adjust constraints for the content pane: Its right
		// edge should be 5 pixels beyond the text field's right
		// edge, and its bottom edge should be 5 pixels beyond
		// the bottom edge of the tallest component (which we'll
		// assume is textField).
		layout.putConstraint(SpringLayout.EAST, this, 5, SpringLayout.EAST,
				examNameTextField);
		// layout.putConstraint(SpringLayout.SOUTH, this,
		// 5,
		// SpringLayout.SOUTH, examNameTextField);

		layout.putConstraint(SpringLayout.WEST, descriptionTextField, 0,
				SpringLayout.WEST, examNameTextField);
		layout.putConstraint(SpringLayout.NORTH, descriptionTextField, 5,
				SpringLayout.SOUTH, examNameTextField);
		layout.putConstraint(SpringLayout.EAST, descriptionTextField, 0,
				SpringLayout.EAST, examNameTextField);
		layout.putConstraint(SpringLayout.EAST, this, 5, SpringLayout.EAST,
				examNameTextField);
		// /////////////////////////////////////////////////////////////////////////

		// /////////////////////////////////////////////////////////////////////////
		JLabel labelDescription = new JLabel(
				ExamenVerwaltung.getText("Description"), SwingConstants.CENTER);
		// labelDescription.setText(ExamenVerwaltung.getText("Description"));
		// labelDescription.setMinimumSize(new Dimension(lineHeight,
		// labelWidth));
		// labelDescription.setPreferredSize(new Dimension(lineHeight,
		// labelWidth
		// + (labelWidth / 2)));
		// labelDescription.setMaximumSize(new Dimension(lineHeight,
		// labelWidth * 2));
		labelDescription.setBounds(5, 5, lineHeight, labelWidth * 2);

		// Adjust constraints for the label so it's at (5,5).
		layout.putConstraint(SpringLayout.WEST, labelDescription, 0,
				SpringLayout.WEST, labelExamName);
		layout.putConstraint(SpringLayout.NORTH, labelDescription, 0,
				SpringLayout.NORTH, descriptionTextField);
		// examNameTextField.setText(question.getExam().toString());
		descriptionTextField.setMinimumSize(new Dimension(lineHeight,
				textFieldWidth));
		descriptionTextField.setMaximumSize(new Dimension(lineHeight, 4096));
		labelDescription.setLabelFor(descriptionTextField);
		this.add(descriptionTextField);
		this.add(labelDescription);
		// /////////////////////////////////////////////////////////////////////////

		// /////////////////////////////////////////////////////////////////////////
		JLabel labelLanguage = new JLabel();
		labelLanguage.setText(ExamenVerwaltung.getText("Language"));
		labelLanguage.setBounds(5, 5, lineHeight, labelWidth * 2);
		this.add(labelLanguage);

		// examNameTextField.setText(question.getExam().toString());
		languageTextField.setMinimumSize(new Dimension(lineHeight,
				textFieldWidth));
		languageTextField.setMaximumSize(new Dimension(lineHeight, 4096));
		labelLanguage.setLabelFor(languageTextField);
		this.add(languageTextField);

		layout.putConstraint(SpringLayout.WEST, languageTextField, 0,
				SpringLayout.WEST, descriptionTextField);
		layout.putConstraint(SpringLayout.NORTH, languageTextField, 5,
				SpringLayout.SOUTH, descriptionTextField);
		layout.putConstraint(SpringLayout.EAST, languageTextField, 0,
				SpringLayout.EAST, descriptionTextField);
		layout.putConstraint(SpringLayout.EAST, this, 5, SpringLayout.EAST,
				descriptionTextField);

		// Adjust constraints for the label so it's at (5,5).
		layout.putConstraint(SpringLayout.WEST, labelLanguage, 0,
				SpringLayout.WEST, labelDescription);
		layout.putConstraint(SpringLayout.NORTH, labelLanguage, 0,
				SpringLayout.NORTH, languageTextField);
		// /////////////////////////////////////////////////////////////////////////

		// /////////////////////////////////////////////////////////////////////////
		JLabel labelNumber = new JLabel();
		labelNumber.setText(ExamenVerwaltung.getText("Number"));
		labelNumber.setBounds(5, 5, lineHeight, labelWidth * 2);
		this.add(labelNumber);

		// examNameTextField.setText(question.getExam().toString());
		numberTextField
				.setMinimumSize(new Dimension(lineHeight, textFieldWidth));
		numberTextField.setMaximumSize(new Dimension(lineHeight, 4096));
		labelNumber.setLabelFor(numberTextField);
		this.add(numberTextField);

		layout.putConstraint(SpringLayout.WEST, numberTextField, 0,
				SpringLayout.WEST, languageTextField);
		layout.putConstraint(SpringLayout.NORTH, numberTextField, 5,
				SpringLayout.SOUTH, languageTextField);
		layout.putConstraint(SpringLayout.EAST, numberTextField, 0,
				SpringLayout.EAST, languageTextField);
		layout.putConstraint(SpringLayout.EAST, this, 5, SpringLayout.EAST,
				numberTextField);

		// Adjust constraints for the label so it's at (5,5).
		layout.putConstraint(SpringLayout.WEST, labelNumber, 0,
				SpringLayout.WEST, labelLanguage);
		layout.putConstraint(SpringLayout.NORTH, labelNumber, 0,
				SpringLayout.NORTH, numberTextField);
		// /////////////////////////////////////////////////////////////////////////

		// /////////////////////////////////////////////////////////////////////////
		JLabel labelQuestion = new JLabel();
		labelQuestion.setText(ExamenVerwaltung.getText("Question"));
		labelQuestion.setBounds(5, 5, lineHeight, labelWidth * 2);
		this.add(labelQuestion);

		// examNameTextField.setText(question.getExam().toString());
		questionTextField.setMinimumSize(new Dimension(lineHeight,
				textFieldWidth));
		questionTextField.setMaximumSize(new Dimension(lineHeight, 4096));
		labelQuestion.setLabelFor(questionTextField);
		this.add(questionTextField);

		layout.putConstraint(SpringLayout.WEST, questionTextField, 0,
				SpringLayout.WEST, numberTextField);
		layout.putConstraint(SpringLayout.NORTH, questionTextField, 5,
				SpringLayout.SOUTH, numberTextField);
		layout.putConstraint(SpringLayout.EAST, questionTextField, 0,
				SpringLayout.EAST, numberTextField);
		layout.putConstraint(SpringLayout.EAST, this, 5, SpringLayout.EAST,
				questionTextField);

		// Adjust constraints for the label so it's at (5,5).
		layout.putConstraint(SpringLayout.WEST, labelQuestion, 0,
				SpringLayout.WEST, labelNumber);
		layout.putConstraint(SpringLayout.NORTH, labelQuestion, 0,
				SpringLayout.NORTH, questionTextField);
		// /////////////////////////////////////////////////////////////////////////

		// /////////////////////////////////////////////////////////////////////////
		JLabel labelImageLine = new JLabel();
		labelImageLine.setText(ExamenVerwaltung.getText("Image.line"));
		labelImageLine.setBounds(5, 5, lineHeight, labelWidth * 2);
		this.add(labelImageLine);

		// examNameTextField.setText(question.getExam().toString());
		imageLineTextField.setMinimumSize(new Dimension(lineHeight,
				textFieldWidth));
		imageLineTextField.setMaximumSize(new Dimension(lineHeight, 4096));
		labelImageLine.setLabelFor(imageLineTextField);
		this.add(imageLineTextField);

		layout.putConstraint(SpringLayout.WEST, imageLineTextField, 0,
				SpringLayout.WEST, questionTextField);
		layout.putConstraint(SpringLayout.NORTH, imageLineTextField, 5,
				SpringLayout.SOUTH, questionTextField);
		layout.putConstraint(SpringLayout.EAST, imageLineTextField, 0,
				SpringLayout.EAST, questionTextField);
		layout.putConstraint(SpringLayout.EAST, this, 5, SpringLayout.EAST,
				imageLineTextField);

		// Adjust constraints for the label so it's at (5,5).
		layout.putConstraint(SpringLayout.WEST, labelImageLine, 0,
				SpringLayout.WEST, labelQuestion);
		layout.putConstraint(SpringLayout.NORTH, labelImageLine, 0,
				SpringLayout.NORTH, imageLineTextField);
		// /////////////////////////////////////////////////////////////////////////

		// /////////////////////////////////////////////////////////////////////////
		JLabel labelImagePath = new JLabel();
		labelImagePath.setText(ExamenVerwaltung.getText("Picture"));
		labelImagePath.setBounds(5, 5, lineHeight, labelWidth * 2);
		this.add(labelImagePath);

		// examNameTextField.setText(question.getExam().toString());
		imagePathTextField.setMinimumSize(new Dimension(lineHeight,
				textFieldWidth));
		imagePathTextField.setMaximumSize(new Dimension(lineHeight, 4096));
		labelImagePath.setLabelFor(imagePathTextField);
		this.add(imagePathTextField);

		layout.putConstraint(SpringLayout.WEST, imagePathTextField, 0,
				SpringLayout.WEST, imageLineTextField);
		layout.putConstraint(SpringLayout.NORTH, imagePathTextField, 5,
				SpringLayout.SOUTH, imageLineTextField);
		layout.putConstraint(SpringLayout.EAST, imagePathTextField, 0,
				SpringLayout.EAST, imageLineTextField);
		layout.putConstraint(SpringLayout.EAST, this, 5, SpringLayout.EAST,
				imagePathTextField);

		// Adjust constraints for the label so it's at (5,5).
		layout.putConstraint(SpringLayout.WEST, labelImagePath, 0,
				SpringLayout.WEST, labelImageLine);
		layout.putConstraint(SpringLayout.NORTH, labelImagePath, 0,
				SpringLayout.NORTH, imagePathTextField);
		// /////////////////////////////////////////////////////////////////////////

		// /////////////////////////////////////////////////////////////////////////
		JLabel labelExplanation = new JLabel();
		labelExplanation.setText(ExamenVerwaltung.getText("Explanation"));
		labelExplanation.setBounds(5, 5, lineHeight, labelWidth * 2);
		this.add(labelExplanation);

		// examNameTextField.setText(question.getExam().toString());
		explanationTextField.setMinimumSize(new Dimension(lineHeight,
				textFieldWidth));
		explanationTextField.setMaximumSize(new Dimension(lineHeight, 4096));
		labelExplanation.setLabelFor(explanationTextField);
		this.add(explanationTextField);

		layout.putConstraint(SpringLayout.WEST, explanationTextField, 0,
				SpringLayout.WEST, imagePathTextField);
		layout.putConstraint(SpringLayout.NORTH, explanationTextField, 5,
				SpringLayout.SOUTH, imagePathTextField);
		layout.putConstraint(SpringLayout.EAST, explanationTextField, 0,
				SpringLayout.EAST, imagePathTextField);
		layout.putConstraint(SpringLayout.EAST, this, 5, SpringLayout.EAST,
				explanationTextField);

		// Adjust constraints for the label so it's at (5,5).
		layout.putConstraint(SpringLayout.WEST, labelExplanation, 0,
				SpringLayout.WEST, labelImagePath);
		layout.putConstraint(SpringLayout.NORTH, labelExplanation, 0,
				SpringLayout.NORTH, explanationTextField);
		// /////////////////////////////////////////////////////////////////////////

		// Display the window.
	}

	public void ReadData(Question question) {
		// TODO Auto-generated method stub
	}

	@Override
	public void ReadData(ExamItemAbstract item) {
		// TODO Auto-generated method stub

	}

	public void editItem(ExamItemAbstract selectedItem) {
		this.selectedItem = selectedItem;
		if (selectedItem != null) {
			selectedItem.setInEdit(true);
			Question selectedQuestion;
			Exam selectedExam = null;
			if (selectedItem instanceof Exam) {
				selectedQuestion = null;
				selectedExam = (Exam) selectedItem;
			} else if (selectedItem instanceof Question) {
				selectedQuestion = (Question) selectedItem;
				selectedExam = (Exam) selectedQuestion.getExam();
			} else if (selectedItem instanceof Answer) {
				selectedQuestion = ExamenVerwaltung
						.getQuestion((IAnswer) selectedItem);
				selectedExam = (Exam) selectedQuestion.getExam();
			}
			if (selectedExam != null) {
				this.examNameTextField.setText(selectedExam.getName());
				this.descriptionTextField
						.setText(selectedExam.getDescription());
				this.languageTextField.setText(selectedExam.getLanguage());
			}
		}
	}

	@Override
	public void saveData() {
		// TODO:
		Exam selectedExam = null;
		Question selectedQuestion = null;
		if (selectedItem != null) {
			if (selectedItem instanceof Exam) {
				selectedQuestion = null;
				selectedExam = (Exam) selectedItem;
			} else if (selectedItem instanceof Question) {
				selectedQuestion = (Question) selectedItem;
				selectedExam = (Exam) selectedQuestion.getExam();
			} else if (selectedItem instanceof Answer) {
				selectedQuestion = ExamenVerwaltung
						.getQuestion((IAnswer) selectedItem);
				selectedExam = (Exam) selectedQuestion.getExam();
			}
			selectedExam.setDescription(descriptionTextField.getText());
			selectedExam.setDescription(languageTextField.getText());
			if (selectedQuestion != null) {
				selectedQuestion.setNumber(Integer.parseInt(numberTextField.getText()));
				selectedQuestion.setQuestionText(questionTextField.getText());
				selectedQuestion.setImageLine(Integer.parseInt(imageLineTextField.getText()));
				try {
					selectedQuestion.setQuestionImage(null);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				selectedQuestion.setAnswerExpailnation(explanationTextField.getText());
			}

			ExamenVerwaltung.saveItem(this.selectedItem);
			selectedItem.setInEdit(false);
		}
	}

}
