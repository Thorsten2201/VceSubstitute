package de.bbq.java.tasks.vce;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Hashtable;

import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * @author Thorsten2201
 *
 */
public class PanelQuestion extends JPanel implements ActionListener,
		ListSelectionListener {
	// ///////////////////////////////////////////////////////////////////////////////////
	// Static
	private static final long serialVersionUID = 7720278844639602571L;
	// ///////////////////////////////////////////////////////////////////////////////////

	// ///////////////////////////////////////////////////////////////////////////////////
	// Class Properties
	private boolean refresh = true;
	// ///////////////////////////////////////////////////////////////////////////////////

	// ////////////////////////////////////////////////////////////////////////////////////
	// Controls
	private JButton createQuestionButton, deleteQuestionButton,
			addQuestionButton, removeQuestionButton;
	private JList<IQuestion> questionPoolJList, questionsJList;
	private DefaultListModel<IQuestion> questionPoolListModel,
			questionListModel;
	private JList<IExam> examJList;
	private DefaultListModel<IExam> examListModel;

	// ////////////////////////////////////////////////////////////////////////////////////

	// ///////////////////////////////////////////////////////////////////////////////////
	// Class methods
	public void refresh() {
		this.refresh = true;

		IQuestion qindex = null;
		for (IQuestion q : ExamenVerwaltung.getQuestionList()) {
			if (q.hasExam()) {
				if (this.questionPoolListModel.contains(q)) {
					this.questionPoolListModel.removeElement(q);
				}
			} else {
				if (!this.questionPoolListModel.contains(q)) {
					this.questionPoolListModel.addElement(q);
				}
			}
		}
		for (int index = this.questionPoolListModel.getSize(); index > 0; index--) {
			try {
				qindex = this.questionPoolListModel.getElementAt(index - 1);
			} catch (Exception e) {
				ExamenVerwaltung.showException(e);
			}
			if (!ExamenVerwaltung.getQuestionList().contains(qindex)) {
				this.questionPoolListModel.removeElementAt(index - 1);
			}
		}
		selectQuestion(this.questionPoolJList.getSelectedValue());
		if (this.examJList.getSelectedValue() == null
				&& !this.examListModel.isEmpty()) {
			this.examJList.setSelectedIndex(0);
		}
		selectExam(this.examJList.getSelectedValue());
		this.refresh = false;
		checkButtons();
	}

	private void refreshExams() {
		this.refresh = true;
		IExam exindex = null;
		for (IExam c : ExamenVerwaltung.getExamList()) {
			if (!this.examListModel.contains(c)) {
				this.examListModel.addElement(c);
			}
		}
		for (int index = this.examListModel.getSize(); index > 0; index--) {
			try {
				exindex = this.examListModel.getElementAt(index - 1);
			} catch (Exception e) {
				ExamenVerwaltung.showException(e);
			}
			if (!ExamenVerwaltung.getExamList().contains(exindex)) {
				this.examListModel.removeElementAt(index - 1);
			}
		}
		if (!this.examListModel.isEmpty()) {
			this.examJList.setSelectedValue(exindex, true);
			selectExam(exindex);
		}
		this.refresh = false;
	}

	public void checkButtons() {
		this.deleteQuestionButton.setEnabled(this.questionPoolJList.getModel()
				.getSize() > 0
				&& this.questionPoolJList.getSelectedValue() != null);
		if (this.questionsJList.getSelectedIndex() < 0) {
			if (this.questionsJList.getModel().getSize() > 0) {
				this.questionsJList.setSelectedIndex(0);
			}
		}
		if (this.questionPoolJList.getSelectedIndex() < 0) {
			if (this.questionPoolJList.getModel().getSize() > 0) {
				this.questionPoolJList.setSelectedIndex(0);
			}
		}
		this.removeQuestionButton.setEnabled(this.questionsJList
				.getSelectedValue() != null);
		boolean enable = this.questionPoolJList.getModel().getSize() > 0
				&& this.questionPoolJList.getSelectedValue() != null;
		this.addQuestionButton.setEnabled(enable);
	}

	private void selectExam(IExam selectedExam) {
		this.questionListModel.clear();
		if (selectedExam != null) {
			for (IQuestion question : selectedExam.getQuestions()) {
				this.questionListModel.addElement(question);
			}
		}
	}

	private void selectQuestion(IQuestion selectedQuestion) {
		this.questionListModel.clear();
		if (selectedQuestion != null) {
			if (selectedQuestion.hasExam()) {
				for (IQuestion question : selectedQuestion.getExam()
						.getQuestions()) {
					if (question != selectedQuestion) {
						this.questionListModel.addElement(selectedQuestion);
					}
				}
			}
			refreshExams();
		}
	}
	// ///////////////////////////////////////////////////////////////////////////////////

	// ///////////////////////////////////////////////////////////////////////////////////
	// ListSelectionListener
	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (e.getSource() == this.questionPoolJList) {
			IQuestion selectedQuestion = (IQuestion) this.questionPoolJList.getSelectedValue();
			if (!this.refresh) {
				selectQuestion(selectedQuestion);
			}
		} else if (e.getSource() == this.examJList) {
			IExam selectedExam = (IExam) this.examJList.getSelectedValue();
			if (!this.refresh) {
				questionListModel.clear();
				for (IQuestion question : selectedExam.getQuestions()) {
					this.questionListModel.addElement(question);
				}
			}
		}
		checkButtons();
	}
	// ///////////////////////////////////////////////////////////////////////////////////

	// ///////////////////////////////////////////////////////////////////////////////////
	// ActionListener
	@Override
	public void actionPerformed(ActionEvent arg0) {
		this.refresh = true;
		boolean selectExam = false;
		
		IQuestion questionPool = this.questionPoolJList.getSelectedValue();
		IQuestion selectedQuestion = this.questionsJList.getSelectedValue();

		IExam exam = this.examJList.getSelectedValue();
		
		int index = this.questionPoolJList.getSelectedIndex();
		
		if (arg0.getSource() == createQuestionButton) {
			ExamenVerwaltung.getNewQuestion(true);
			index = this.questionPoolJList.getModel().getSize();
			
		} else if (arg0.getSource() == deleteQuestionButton) {
			if (questionPool != null) {
				ExamenVerwaltung.deleteElement((ExamItemAbstract) questionPool);
				index = this.questionPoolJList.getModel().getSize();
				selectExam = true;
			}

		} else if (arg0.getSource() == addQuestionButton) {
			if (questionPool != null && exam != null) {
				if (!exam.hasQuestion(questionPool)) {
					exam.addQuestion(questionPool);
				}
			}
			selectExam = true;

		} else if (arg0.getSource() == removeQuestionButton) {
			if (selectedQuestion != null) {
				selectedQuestion.getExam().removeQuestion(selectedQuestion);
				selectExam = true;
			}
		}
		this.refresh = false;
		refresh();
//		if (index <= this.questionPoolJList.getModel().getSize()) {
//			this.questionPoolJList.setSelectedIndex(index);
//		}
		selectQuestion(this.questionPoolJList.getSelectedValue());
//		if (index <= this.examJList.getModel().getSize()) {
//			this.examJList.setSelectedIndex(index);
//		}
		if (selectExam) {
			selectExam(exam);
		}
		checkButtons();
	}

	// ///////////////////////////////////////////////////////////////////////////////////
	// Construct
	public PanelQuestion() {
		this.setLayout(new BorderLayout(5, 5));
		JPanel panelCreate = new JPanel(new GridLayout(1, 2, 5, 5));
		setBorder(new EmptyBorder(5, 5, 5, 5));
		JPanel panelTop = new JPanel(new GridLayout(1, 3, 10, 5));
		JPanel panelBottom = new JPanel(new GridLayout(1, 3, 10, 10));
		panelTop.add(panelCreate);
		this.add(panelTop, BorderLayout.NORTH);

		panelBottom = new JPanel(new GridLayout(1, 3, 10, 10));
		this.questionPoolListModel = new DefaultListModel<IQuestion>();
		this.questionPoolJList = new JList<IQuestion>(
				this.questionPoolListModel);
		this.questionPoolJList
				.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		this.questionPoolJList.setLayoutOrientation(JList.VERTICAL);
		this.questionPoolJList.setVisibleRowCount(1);
		this.questionPoolJList.addListSelectionListener(this);
		this.questionPoolJList.addMouseListener(new MouseAdapter() {
			@SuppressWarnings("rawtypes")
			public void mouseClicked(MouseEvent evt) {
				JList list = (JList) evt.getSource();
				int index = 1;
				if (evt.getClickCount() == 2) {
					// Double-click detected
					index = list.locationToIndex(evt.getPoint());
				} else if (evt.getClickCount() == 3) {
					// Triple-click detected
					index = list.locationToIndex(evt.getPoint());
				}
				if (index >= 0) {
					try {
						ExamenVerwaltung.getInstance().editItem(
								(ExamItemAbstract) examListModel.get(index));
					} catch (Exception e) {
						// TODO: ExamenVerwaltung.showException(e);
					}
					refresh();
				}
			}
		});
		this.questionPoolJList.setCellRenderer(new ExamListCellRenderer());
		JScrollPane questionPoolScroller = new JScrollPane(
				this.questionPoolJList);
		questionPoolScroller.setBounds(5, 30, 265, 300);
		questionPoolScroller
				.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		questionPoolScroller
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		questionPoolScroller.setViewportBorder(new LineBorder(Color.BLACK));
		// this.add(coursScroller);
		panelBottom.add(questionPoolScroller);

		this.createQuestionButton = ExamenVerwaltung.getButton(
				"newQuestion",
				5,
				5,
				130,
				20,
				this,
				ExamenVerwaltung.getText("New") + " "
						+ ExamenVerwaltung.getText("question"),
				ExamenVerwaltung.getText("New") + " "
						+ ExamenVerwaltung.getText("question"));

		this.deleteQuestionButton = ExamenVerwaltung.getButton(
				"delQuestion",
				110,
				5,
				130,
				20,
				this,
				ExamenVerwaltung.getText("Delete") + " "
						+ ExamenVerwaltung.getText("question"),
				ExamenVerwaltung.getText("Delete") + " "
						+ ExamenVerwaltung.getText("question"));
		panelCreate.add(this.createQuestionButton);
		panelCreate.add(this.deleteQuestionButton);

		this.addQuestionButton = ExamenVerwaltung.getButton(
				"addQuestion",
				235,
				5,
				205,
				20,
				this,
				ExamenVerwaltung.getText("Add") + " ->",
				ExamenVerwaltung.getText("Add") + " "
						+ ExamenVerwaltung.getText("question"));
		panelTop.add(this.addQuestionButton);

		this.removeQuestionButton = ExamenVerwaltung.getButton(
				"remQuestion",
				470,
				5,
				205,
				20,
				this,
				"<- " + ExamenVerwaltung.getText("Remove"),
				ExamenVerwaltung.getText("Remove")
						+ ExamenVerwaltung.getText("question"));
		panelTop.add(this.removeQuestionButton);

		this.examListModel = new DefaultListModel<IExam>();
		this.examJList = new JList<IExam>(this.examListModel);
		this.examJList
				.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		this.examJList.setLayoutOrientation(JList.VERTICAL);
		this.examJList.setVisibleRowCount(1);
		this.examJList.addListSelectionListener(this);
		this.examJList.addMouseListener(new MouseAdapter() {
			@SuppressWarnings("rawtypes")
			public void mouseClicked(MouseEvent evt) {
				JList list = (JList) evt.getSource();
				int index = -1;
				if (evt.getClickCount() == 2) {
					// Double-click detected
					index = list.locationToIndex(evt.getPoint());
				} else if (evt.getClickCount() == 3) {
					// Triple-click detected
					index = list.locationToIndex(evt.getPoint());
				}
				if (index >= 0) {
					ExamenVerwaltung.getInstance().editItem(
							(ExamItemAbstract) examListModel.get(index));
					refresh();
				}
			}
		});
		this.examJList.setCellRenderer(new ExamListCellRenderer());
		JScrollPane pupScroller = new JScrollPane(this.examJList);
		pupScroller.setPreferredSize(new Dimension(206, 300));
		pupScroller
				.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		pupScroller
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		pupScroller.setViewportBorder(new LineBorder(Color.BLACK));
		pupScroller.setBounds(235, 30, 265, 300);
		panelBottom.add(pupScroller);

		this.questionListModel = new DefaultListModel<IQuestion>();
		this.questionsJList = new JList<IQuestion>(this.questionListModel);
		this.questionsJList.setLayoutOrientation(JList.VERTICAL);
		this.questionsJList.setVisibleRowCount(1);
		this.questionsJList.addListSelectionListener(this);
		this.questionsJList.addMouseListener(new MouseAdapter() {
			@SuppressWarnings("rawtypes")
			public void mouseClicked(MouseEvent evt) {
				JList list = (JList) evt.getSource();
				int index = -1;
				if (evt.getClickCount() == 2) {
					// Double-click detected
					index = list.locationToIndex(evt.getPoint());
				} else if (evt.getClickCount() == 3) {
					// Triple-click detected
					index = list.locationToIndex(evt.getPoint());
				}
				if (index >= 0) {
					ExamenVerwaltung.getInstance().editItem(
							(ExamItemAbstract) questionListModel.get(index));
					refresh();
				}
			}
		});
		JScrollPane selQuestions = new JScrollPane(this.questionsJList);
		selQuestions.setPreferredSize(new Dimension(206, 300));
		selQuestions
				.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		selQuestions
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		selQuestions.setViewportBorder(new LineBorder(Color.BLACK));
		selQuestions.setBounds(235, 30, 265, 300);
		panelBottom.add(selQuestions);

		this.add(panelBottom, BorderLayout.CENTER);
		this.refresh = false;
	}

	// ///////////////////////////////////////////////////////////////////////////////////
}