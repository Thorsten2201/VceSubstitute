package de.bbq.java.tasks.vce;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.ComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.Spring;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.SpringLayout.Constraints;
import javax.swing.event.ListDataListener;

public class PanelEdit extends PanelAbstract {
	private static final long serialVersionUID = -2186316596347702853L;

	private Constraints constraintsSouthAnswers;

	private final int labelWidth = 210;
	private final int buttonWidth = 100;
	private final int lineHeight = 20;
	private final int textFieldWidth = 220;

	private JTextField languageTextField = new JTextField("", 1), examNameTextField = new JTextField("", 1),
			imageLineTextField = new JTextField("", 1), imagePathTextField = new JTextField("", 1),
			explanationTextField = new JTextField("", 1), descriptionTextField = new JTextField("", 3);
	private JCheckBox beamerCheckBox = new JCheckBox();
	private JTextArea questionTextArea = new JTextArea(4, 30),explanationTextArea = new JTextArea(4, 30);
	private PanelSingleAnswer answerPanel;
	private JComboBox<String> languageBox;

	JComboBox<Integer> numberBox = new JComboBox<Integer>();
	JComboBox<Integer> imageLineBox = new JComboBox<Integer>();

	private SpringLayout layout = null;
	private ExamItemAbstract selectedItem;
	private static final String[] languages = { ExamenVerwaltung.getText("German"),
			ExamenVerwaltung.getText("English") };
	// private JTextField maxWidthItem = descriptionTextField;
	// private JTextField questionTextArea = new JTextField("", 1);
	private JButton pictureButton = null;

	public void refresh() {
		languageBox = new JComboBox<String>(languages);
		Integer[] index = new Integer[ExamenVerwaltung.getExamList().size()];
		for (Integer i = 0; i < ExamenVerwaltung.getExamList().size(); i++) {
			index[i] = i;

		}
		refreshComboBox(1, ExamenVerwaltung.getExamList().size(), numberBox);
		Question question = (Question) this.getQuestion();
		if (question != null) {
			refreshComboBox(1, countLines(question.getQuestionText()), imageLineBox);
			numberBox.setSelectedIndex(question.getNumber());
			imageLineBox.setSelectedIndex(question.getImageLine());
		}
	}

	private void refreshComboBox(Integer start, Integer maximum, JComboBox<Integer> comboBox) {
		for (Integer index = start; index <= maximum; index++) {
			boolean found = false;
			for (Integer items = 0; items < comboBox.getItemCount(); items++) {
				if (index.equals(comboBox.getItemAt(items))) {
					found = true;
				} else if (comboBox.getItemAt(items) > ExamenVerwaltung.getExamList().size()) {
					comboBox.removeItemAt(items);
				}
			}
			if (!found) {
				comboBox.addItem(index);
			}
		}
	}

	private static int countLines(String str) {
		String[] lines = str.split("\r\n|\r|\n");
		return lines.length;
	}

	public PanelEdit() {
		setOpaque(false);
		setBackground(Color.GREEN);

		this.layout = new SpringLayout();
		setLayout(this.layout );
		// contentPane = (JPanel) this;

		// /////////////////////////////////////////////////////////////////////////
		JLabel labelExamName = new JLabel();
		labelExamName.setText(ExamenVerwaltung.getText("Exam") + ":");
		// labelExamName.setMinimumSize(new Dimension(lineHeight, labelWidth));
		// labelExamName.setPreferredSize(new Dimension(lineHeight, labelWidth
		// + (labelWidth / 2)));
		// labelExamName.setMaximumSize(new Dimension(lineHeight, labelWidth *
		// 2));
		labelExamName.setBounds(5, 5, lineHeight, labelWidth * 2);
		this.add(labelExamName);
		// examNameTextField.setText(question.getExam().toString());

		examNameTextField.setMinimumSize(new Dimension(lineHeight, textFieldWidth));
		examNameTextField.setMaximumSize(new Dimension(lineHeight, 4096));
		labelExamName.setLabelFor(examNameTextField);
		this.add(examNameTextField);

		layout.putConstraint(SpringLayout.WEST, labelExamName, 5, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.NORTH, labelExamName, 5, SpringLayout.NORTH, this);
		layout.putConstraint(SpringLayout.NORTH, examNameTextField, 0, SpringLayout.NORTH, labelExamName);
		// /////////////////////////////////////////////////////////////////////////

		// /////////////////////////////////////////////////////////////////////////
		JLabel labelLanguage = new JLabel();
		labelLanguage.setText(ExamenVerwaltung.getText("Language") + ":");
		labelLanguage.setBounds(5, 5, lineHeight, labelWidth * 2);
		this.add(labelLanguage);
		languageBox = new JComboBox<String>(languages);
		languageBox.setSelectedIndex(1);
		languageBox.addActionListener(this);

		labelLanguage.setLabelFor(languageBox);
		this.add(languageBox);

		layout.putConstraint(SpringLayout.NORTH, labelLanguage, 0, SpringLayout.NORTH, labelExamName);
		layout.putConstraint(SpringLayout.EAST, labelLanguage, -5, SpringLayout.WEST, languageBox);

		layout.putConstraint(SpringLayout.EAST, examNameTextField, -10, SpringLayout.WEST, labelLanguage);

		layout.putConstraint(SpringLayout.NORTH, languageBox, 0, SpringLayout.NORTH, examNameTextField);
		layout.putConstraint(SpringLayout.EAST, languageBox, 0, SpringLayout.EAST, this);
		// /////////////////////////////////////////////////////////////////////////

		// /////////////////////////////////////////////////////////////////////////
		JLabel labelDescription = new JLabel();
		labelDescription.setText(ExamenVerwaltung.getText("Description") + ":");
		labelDescription.setBounds(5, 5, lineHeight, labelWidth * 2);
		this.add(labelDescription);
		this.add(descriptionTextField);
		layout.putConstraint(SpringLayout.NORTH, labelDescription, 5, SpringLayout.SOUTH, languageBox);
		layout.putConstraint(SpringLayout.WEST, labelDescription, 0, SpringLayout.WEST, labelExamName);

		layout.putConstraint(SpringLayout.NORTH, descriptionTextField, 0, SpringLayout.NORTH, labelDescription);
		layout.putConstraint(SpringLayout.EAST, descriptionTextField, 0, SpringLayout.EAST, languageBox);

		descriptionTextField.setMinimumSize(new Dimension(lineHeight, textFieldWidth));
		descriptionTextField.setMaximumSize(new Dimension(lineHeight, 4096));
		labelDescription.setLabelFor(descriptionTextField);
		// /////////////////////////////////////////////////////////////////////////

		// /////////////////////////////////////////////////////////////////////////
		JLabel labelNumber = new JLabel();
		labelNumber.setText(ExamenVerwaltung.getText("Number") + ":");
		labelNumber.setBounds(5, 5, lineHeight, labelWidth * 2);
		this.add(labelNumber);

		// examNameTextField.setText(question.getExam().toString());
		numberBox.setMinimumSize(new Dimension(lineHeight, textFieldWidth));
		numberBox.setMaximumSize(new Dimension(lineHeight, 4096));
		labelNumber.setLabelFor(numberBox);
		this.add(numberBox);

		layout.putConstraint(SpringLayout.NORTH, labelNumber, 5, SpringLayout.SOUTH, descriptionTextField);
		layout.putConstraint(SpringLayout.WEST, labelNumber, 0, SpringLayout.WEST, labelExamName);

		layout.putConstraint(SpringLayout.NORTH, numberBox, 0, SpringLayout.NORTH, labelNumber);

		// /////////////////////////////////////////////////////////////////////////

		// /////////////////////////////////////////////////////////////////////////
		JLabel labelImageLine = new JLabel();
		labelImageLine.setText(ExamenVerwaltung.getText("Image.line") + ":");
		labelImageLine.setBounds(5, 5, lineHeight, labelWidth * 2);
		this.add(labelImageLine);

		imageLineBox = new JComboBox<Integer>();
		// imageLineBox.setSelectedIndex(1);
		imageLineBox.addActionListener(this);
		// imageLineBox.setBounds(new Rectangle(lineHeight,
		// textFieldWidth*2));
		this.add(imageLineBox);
		labelImageLine.setLabelFor(imageLineBox);
		// layout.putConstraint(SpringLayout.EAST, numberBox, 0,
		// SpringLayout.EAST, this);
		// layout.putConstraint(SpringLayout.WEST, labelImageLine, 10,
		// SpringLayout.EAST, numberBox);
		layout.putConstraint(SpringLayout.NORTH, labelImageLine, 0, SpringLayout.NORTH, labelNumber);
		layout.putConstraint(SpringLayout.EAST, labelImageLine, -5, SpringLayout.WEST, imageLineBox);

		layout.putConstraint(SpringLayout.NORTH, imageLineBox, 0, SpringLayout.NORTH, labelNumber);
		layout.putConstraint(SpringLayout.EAST, imageLineBox, 0, SpringLayout.EAST, descriptionTextField);
		// /////////////////////////////////////////////////////////////////////////

		// /////////////////////////////////////////////////////////////////////////
		JLabel labelQuestion = new JLabel();
		labelQuestion.setText(ExamenVerwaltung.getText("Question") + ":");
		labelQuestion.setBounds(5, 5, lineHeight, labelWidth * 2);
		this.add(labelQuestion);
		layout.putConstraint(SpringLayout.NORTH, labelQuestion, 5, SpringLayout.SOUTH, numberBox);
		layout.putConstraint(SpringLayout.WEST, labelQuestion, 0, SpringLayout.WEST, labelExamName);

		// questionTextArea.setMinimumSize(new Dimension(lineHeight,
		// textFieldWidth));
		// questionTextArea.setMaximumSize(new Dimension(lineHeight, 4096));
		JScrollPane scr1 = new JScrollPane(this.questionTextArea);
		this.add(scr1);
		labelQuestion.setLabelFor(scr1);
		layout.putConstraint(SpringLayout.NORTH, scr1, 0, SpringLayout.NORTH, labelQuestion);
		layout.putConstraint(SpringLayout.EAST, scr1, 0, SpringLayout.EAST, this);
		// /////////////////////////////////////////////////////////////////////////

		// /////////////////////////////////////////////////////////////////////////
		JLabel labelImagePath = new JLabel();
		labelImagePath.setText(ExamenVerwaltung.getText("Picture"));
		labelImagePath.setBounds(5, 5, lineHeight, labelWidth * 2);
		this.add(labelImagePath);

		// examNameTextField.setText(question.getExam().toString());
		imagePathTextField.setMinimumSize(new Dimension(lineHeight, textFieldWidth));
		imagePathTextField.setMaximumSize(new Dimension(lineHeight, 4096));
		labelImagePath.setLabelFor(imagePathTextField);
		this.add(imagePathTextField);
		layout.putConstraint(SpringLayout.WEST, labelImagePath, 0, SpringLayout.WEST, labelExamName);
		layout.putConstraint(SpringLayout.NORTH, labelImagePath, 5, SpringLayout.SOUTH, scr1);

		layout.putConstraint(SpringLayout.NORTH, imagePathTextField, 0, SpringLayout.NORTH, labelImagePath);

		this.pictureButton = new JButton();
		this.pictureButton.setText(ExamenVerwaltung.getText("Browse"));
		this.pictureButton.setToolTipText(ExamenVerwaltung.getText("select.picture.question"));
		this.pictureButton.setBounds(new Rectangle(100, lineHeight));
		this.pictureButton.addActionListener(this);

		// add(this.saveButton);
		this.add(this.pictureButton);
		layout.putConstraint(SpringLayout.EAST, pictureButton, 0, SpringLayout.EAST, descriptionTextField);
		layout.putConstraint(SpringLayout.NORTH, pictureButton, 0, SpringLayout.NORTH, labelImagePath);
		layout.putConstraint(SpringLayout.EAST, imagePathTextField, 0, SpringLayout.WEST, pictureButton);
		// /////////////////////////////////////////////////////////////////////////
		
		
		// /////////////////////////////////////////////////////////////////////////
		JLabel labelAnswer1 = new JLabel();
		labelAnswer1.setText(ExamenVerwaltung.getText("Answer") + " 1:");
		labelAnswer1.setBounds(5, 5, lineHeight, labelWidth * 2);
		this.add(labelAnswer1);
		layout.putConstraint(SpringLayout.NORTH, labelAnswer1, 5, SpringLayout.SOUTH, pictureButton);
		layout.putConstraint(SpringLayout.WEST, labelAnswer1, 0, SpringLayout.WEST, labelExamName);

		// questionTextArea.setMinimumSize(new Dimension(lineHeight,
		// textFieldWidth));
		// questionTextArea.setMaximumSize(new Dimension(lineHeight, 4096));
		this.answerPanel = new PanelSingleAnswer();
		
		this.add(answerPanel);
		labelQuestion.setLabelFor(answerPanel);
		layout.putConstraint(SpringLayout.NORTH, answerPanel, 0, SpringLayout.NORTH, labelAnswer1);
		layout.putConstraint(SpringLayout.EAST, answerPanel, 0, SpringLayout.EAST, this);
//		layout.putConstraint(SpringLayout.SOUTH, answerPanel, 0, SpringLayout.SOUTH, this);
		// /////////////////////////////////////////////////////////////////////////


		// /////////////////////////////////////////////////////////////////////////
		JLabel labelExplanation = new JLabel();
		labelExplanation.setText(ExamenVerwaltung.getText("Question") + ":");
		labelExplanation.setBounds(5, 5, lineHeight, labelWidth * 2);
		this.add(labelExplanation);
		layout.putConstraint(SpringLayout.NORTH, labelExplanation, 5, SpringLayout.SOUTH, answerPanel);
		layout.putConstraint(SpringLayout.WEST, labelExplanation, 0, SpringLayout.WEST, labelExamName);
		// questionTextArea.setMinimumSize(new Dimension(lineHeight,
		// textFieldWidth));
		// questionTextArea.setMaximumSize(new Dimension(lineHeight, 4096));
		JScrollPane scr2 = new JScrollPane(this.explanationTextArea);
		this.add(scr2);
		labelQuestion.setLabelFor(scr2);
		layout.putConstraint(SpringLayout.NORTH, scr2, 0, SpringLayout.NORTH, labelExplanation);
		layout.putConstraint(SpringLayout.EAST, scr2, 0, SpringLayout.EAST, this);	

		// /////////////////////////////////////////////////////////////////////////
		
		// widest
		JComponent widests = labelDescription;
		layout.putConstraint(SpringLayout.EAST, labelExamName, 0, SpringLayout.EAST, widests);
		layout.putConstraint(SpringLayout.WEST, examNameTextField, 5, SpringLayout.EAST, labelExamName);
		layout.putConstraint(SpringLayout.WEST, descriptionTextField, 5, SpringLayout.EAST, widests);
		layout.putConstraint(SpringLayout.WEST, numberBox, 5, SpringLayout.EAST, widests);
		layout.putConstraint(SpringLayout.WEST, scr1, 5, SpringLayout.EAST, widests);
		layout.putConstraint(SpringLayout.WEST, imagePathTextField, 5, SpringLayout.EAST, widests);
		layout.putConstraint(SpringLayout.WEST, answerPanel, 5, SpringLayout.EAST, widests);

	      
		refresh();
		if (false) {

			layout.putConstraint(SpringLayout.NORTH, questionTextArea, 5, SpringLayout.SOUTH, numberBox);

			layout.putConstraint(SpringLayout.EAST, this, 5, SpringLayout.EAST, questionTextArea);

			// Adjust constraints for the label so it's at (5,5).
			layout.putConstraint(SpringLayout.WEST, labelQuestion, 0, SpringLayout.WEST, labelNumber);
			layout.putConstraint(SpringLayout.NORTH, labelQuestion, 0, SpringLayout.NORTH, questionTextArea);
			//
			// /////////////////////////////////////////////////////////////////////////

			// /////////////////////////////////////////////////////////////////////////
			// JLabel labelImageLine = new JLabel();
			// labelImageLine.setText(ExamenVerwaltung.getText("Image.line"));
			// labelImageLine.setBounds(5, 5, lineHeight, labelWidth * 2);
			// this.add(labelImageLine);
			//
			// // examNameTextField.setText(question.getExam().toString());
			// imageLineTextField.setMinimumSize(new Dimension(lineHeight,
			// textFieldWidth));
			// imageLineTextField.setMaximumSize(new Dimension(lineHeight,
			// 4096));
			// labelImageLine.setLabelFor(imageLineTextField);
			// this.add(imageLineTextField);
			//
			// layout.putConstraint(SpringLayout.WEST, imageLineTextField, 0,
			// SpringLayout.EAST, questionTextField);
			// layout.putConstraint(SpringLayout.NORTH, imageLineTextField, 5,
			// SpringLayout.SOUTH, questionTextField);
			// layout.putConstraint(SpringLayout.EAST, imageLineTextField, 0,
			// SpringLayout.EAST, questionTextField);
			// layout.putConstraint(SpringLayout.EAST, this, 5,
			// SpringLayout.EAST,
			// imageLineTextField);
			//
			// // Adjust constraints for the label so it's at (5,5).
			// layout.putConstraint(SpringLayout.WEST, labelImageLine, 0,
			// SpringLayout.WEST, labelQuestion);
			// layout.putConstraint(SpringLayout.NORTH, labelImageLine, 0,
			// SpringLayout.NORTH, imageLineTextField);
			// //
			// /////////////////////////////////////////////////////////////////////////
			//
			// //
			// /////////////////////////////////////////////////////////////////////////
			// JLabel labelImagePath = new JLabel();
			// labelImagePath.setText(ExamenVerwaltung.getText("Picture"));
			// labelImagePath.setBounds(5, 5, lineHeight, labelWidth * 2);
			// this.add(labelImagePath);
			//
			// // examNameTextField.setText(question.getExam().toString());
			// imagePathTextField.setMinimumSize(new Dimension(lineHeight,
			// textFieldWidth));
			// imagePathTextField.setMaximumSize(new Dimension(lineHeight,
			// 4096));
			// labelImagePath.setLabelFor(imagePathTextField);
			// this.add(imagePathTextField);
			//
			// layout.putConstraint(SpringLayout.WEST, imagePathTextField, 0,
			// SpringLayout.WEST, imageLineTextField);
			// layout.putConstraint(SpringLayout.NORTH, imagePathTextField, 5,
			// SpringLayout.SOUTH, imageLineTextField);
			// layout.putConstraint(SpringLayout.EAST, imagePathTextField, 0,
			// SpringLayout.EAST, imageLineTextField);
			// layout.putConstraint(SpringLayout.EAST, this, 5,
			// SpringLayout.EAST,
			// imagePathTextField);
			//
			// // Adjust constraints for the label so it's at (5,5).
			// layout.putConstraint(SpringLayout.WEST, labelImagePath, 0,
			// SpringLayout.WEST, labelImageLine);
			// layout.putConstraint(SpringLayout.NORTH, labelImagePath, 0,
			// SpringLayout.NORTH, imagePathTextField);
			// //
			// /////////////////////////////////////////////////////////////////////////
			//
			// //
			// /////////////////////////////////////////////////////////////////////////
			// JLabel labelExplanation = new JLabel();
			// labelExplanation.setText(ExamenVerwaltung.getText("Explanation"));
			// labelExplanation.setBounds(5, 5, lineHeight, labelWidth * 2);
			// this.add(labelExplanation);
			//
			// // examNameTextField.setText(question.getExam().toString());
			// explanationTextField.setMinimumSize(new Dimension(lineHeight,
			// textFieldWidth));
			// explanationTextField.setMaximumSize(new Dimension(lineHeight,
			// 4096));
			// labelExplanation.setLabelFor(explanationTextField);
			// this.add(explanationTextField);
			//
			// layout.putConstraint(SpringLayout.WEST, explanationTextField, 0,
			// SpringLayout.WEST, imagePathTextField);
			// layout.putConstraint(SpringLayout.NORTH, explanationTextField, 5,
			// SpringLayout.SOUTH, imagePathTextField);
			// layout.putConstraint(SpringLayout.EAST, explanationTextField, 0,
			// SpringLayout.EAST, imagePathTextField);
			// layout.putConstraint(SpringLayout.EAST, this, 5,
			// SpringLayout.EAST,
			// explanationTextField);
			//
			// // Adjust constraints for the label so it's at (5,5).
			// layout.putConstraint(SpringLayout.WEST, labelExplanation, 0,
			// SpringLayout.WEST, labelImagePath);
			// layout.putConstraint(SpringLayout.NORTH, labelExplanation, 0,
			// SpringLayout.NORTH, explanationTextField);
			// /////////////////////////////////////////////////////////////////////////

			// Display the window.

		}
	}

	public void ReadData(Question question) {
		// TODO Auto-generated method stub
	}

	@Override
	public void ReadData(ExamItemAbstract item) {
		// TODO Auto-generated method stub

	}

	public Question getQuestion() {
		if (this.selectedItem != null) {
			if (this.selectedItem instanceof Exam) {
				return null;
			} else if (selectedItem instanceof Question) {
				return (Question) selectedItem;
			} else if (selectedItem instanceof Answer) {
				return ExamenVerwaltung.getQuestion((IAnswer) selectedItem);
			}
		}
		return null;
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
				selectedQuestion = ExamenVerwaltung.getQuestion((IAnswer) selectedItem);
				selectedExam = (Exam) selectedQuestion.getExam();
			}
			if (selectedExam != null) {
				this.examNameTextField.setText(selectedExam.getName());
				this.descriptionTextField.setText(selectedExam.getDescription());
				this.languageTextField.setText(selectedExam.getLanguage());
			}
		}
		refresh();
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
				selectedQuestion = ExamenVerwaltung.getQuestion((IAnswer) selectedItem);
				selectedExam = (Exam) selectedQuestion.getExam();
			}
			selectedExam.setDescription(descriptionTextField.getText());
			selectedExam.setDescription(languageTextField.getText());
			if (selectedQuestion != null) {
				selectedQuestion.setNumber(Integer.parseInt(numberBox.getSelectedItem().toString()));
				selectedQuestion.setQuestionText(questionTextArea.getText());
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
