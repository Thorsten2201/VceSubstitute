package de.bbq.java.tasks.vce;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class PanelAnswer extends JPanel implements ActionListener,
		ListSelectionListener {
	// ///////////////////////////////////////////////////////////////////////////////////
	// Static
	private static final long serialVersionUID = 7573321200815259292L;
	// ///////////////////////////////////////////////////////////////////////////////////

	// ///////////////////////////////////////////////////////////////////////////////////
	// Class Properties
	private boolean refresh = true;
	// ///////////////////////////////////////////////////////////////////////////////////

	// ///////////////////////////////////////////////////////////////////////////////////
	// Controls
	private JButton createAnswerButton, deleteAnswerButton, addAnswerButton,
			removeAnswerButton;
	private JList<IAnswer> answerJList, answerSelectedJList;
	private JList<IQuestion> questionPoolJList;
	private DefaultListModel<IAnswer> answerListModel, answerSelectedListModel;
	private DefaultListModel<IQuestion> questionPoolListModel;

	// ///////////////////////////////////////////////////////////////////////////////////

	// ///////////////////////////////////////////////////////////////////////////////////
	// Class methods
	public void refresh() {
		this.refresh = true;

		IAnswer aindex = null;
		for (IAnswer a : ExamenVerwaltung.getAnswerList()) {
			if (a.hasQuestion()) {
				if (this.answerListModel.contains(a)) {
					this.answerListModel.removeElement(a);
				}
			} else {
				if (!this.answerListModel.contains(a)) {
					this.answerListModel.addElement(a);
				}
			}
		}
		for (int index = this.answerListModel.getSize(); index > 0; index--) {
			try {
				aindex = this.answerListModel.getElementAt(index - 1);
			} catch (Exception e) {
				ExamenVerwaltung.showException(e);
			}
			if (!ExamenVerwaltung.getAnswerList().contains(aindex)) {
				this.answerListModel.remove(index - 1);
			}
		}
		if (this.answerJList.getSelectedIndices().length < 1) {
			this.answerJList.setSelectedIndex(0);
		}
		selectAnswer(this.answerJList.getSelectedValue());
		if (this.questionPoolJList.getSelectedIndices().length < 1) {
			this.questionPoolJList.setSelectedIndex(0);
		}
		selectQuestion(this.questionPoolJList.getSelectedValue());
		this.refresh = false;
		checkButtons();
	}

	private void refresQuestions() {
		this.refresh = true;
		IQuestion qindex = null;
		for (IQuestion q : ExamenVerwaltung.getQuestionList()) {
			if (!this.questionPoolListModel.contains(q)) {
				this.questionPoolListModel.addElement(q);
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
		this.refresh = false;
	}

	public void checkButtons() {
		if (this.answerSelectedJList.getSelectedIndex() < 0) {
			if (this.answerSelectedJList.getModel().getSize() > 0) {
				this.answerSelectedJList.setSelectedIndex(0);
			}
		}
		if (this.questionPoolJList.getSelectedIndex() < 0) {
			if (this.questionPoolJList.getModel().getSize() > 0) {
				this.questionPoolJList.setSelectedIndex(0);
			}
		}
		this.removeAnswerButton.setEnabled(this.answerSelectedJList
				.getSelectedValue() != null);
		this.deleteAnswerButton.setEnabled(this.answerJList.getModel()
				.getSize() > 0);
		boolean enable = this.answerJList.getModel().getSize() > 0
				&& this.answerJList.getSelectedValue() != null;
		this.addAnswerButton.setEnabled(enable);
	}

	private void selectAnswer(IAnswer selectedAnswer) {
		// this.answerListModel.clear();
		if (selectedAnswer != null) {
			if (selectedAnswer.hasQuestion()) {
				for (IAnswer answer : selectedAnswer.getQuestion().getAnswers()) {
					if (!this.answerListModel.contains(answer)) {
						this.answerListModel.addElement(answer);
					}
				}
			}
			refresQuestions();
		}
	}

	private void selectQuestion(IQuestion selectedQuestion) {
		if (selectedQuestion != null) {
			for (IAnswer answer : selectedQuestion.getAnswers()) {
				if (!this.answerSelectedListModel.contains(answer)) {
					this.answerSelectedListModel.addElement(answer);
				}
			}
			for (int index = 0; index < this.answerSelectedListModel.size(); index++) {
				if (!selectedQuestion.hasAnswer(this.answerSelectedListModel
						.elementAt(index))) {
					this.answerSelectedListModel.remove(index);
				}
			}
		}
	}

	// ///////////////////////////////////////////////////////////////////////////////////

	// ///////////////////////////////////////////////////////////////////////////////////
	// ListSelectionListener
	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (e.getSource() == this.answerJList) {
			IAnswer selectedAnswer = (IAnswer) this.answerJList
					.getSelectedValue();
			if (!this.refresh) {
				selectAnswer(selectedAnswer);
			}
		} else if (e.getSource() == this.questionPoolJList) {
			IQuestion selectedQuestion = (IQuestion) this.questionPoolJList
					.getSelectedValue();
			if (!this.refresh) {
				answerSelectedListModel.clear();
				for (IAnswer answer : selectedQuestion.getAnswers()) {
					this.answerSelectedListModel.addElement(answer);
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
		boolean selectQuestion = false;

		int index = 0;
		IQuestion questionPool = this.questionPoolJList.getSelectedValue();
		IAnswer selectedAnswer = this.answerSelectedJList.getSelectedValue();
		IAnswer answer = this.answerJList.getSelectedValue();

		if (arg0.getSource() == this.createAnswerButton) {
			ExamenVerwaltung.getNewAnswer(true);
			index = this.answerJList.getModel().getSize();

		} else if (arg0.getSource() == deleteAnswerButton) {
			if (answer != null) {
				ExamenVerwaltung.deleteElement((ExamItemAbstract) answer);
				index = this.answerJList.getModel().getSize();
				selectQuestion = true;
			}

		} else if (arg0.getSource() == this.addAnswerButton) {
			if (questionPool != null && answer != null) {
				if (!questionPool.hasAnswer(answer)) {
					questionPool.addAnswer(answer);
				}
			}
			selectQuestion = true;

		} else if (arg0.getSource() == this.removeAnswerButton) {
			if (selectedAnswer != null && questionPool != null) {
				questionPool.removeAnswer(selectedAnswer);
				selectQuestion = true;
			}
		}
		this.refresh = false;
		refresh();
		if (index <= this.answerJList.getModel().getSize()) {
			this.answerJList.setSelectedIndex(index);
		}
		// if (index <= this.questionPoolJList.getModel().getSize()) {
		// this.questionPoolJList.setSelectedIndex(index);
		// }
		if (selectQuestion) {
			selectQuestion(this.questionPoolJList.getSelectedValue());
		}
		checkButtons();
	}

	// ///////////////////////////////////////////////////////////////////////////////////

	// ///////////////////////////////////////////////////////////////////////////////////
	// Construct
	public PanelAnswer() {
		this.setLayout(new BorderLayout(5, 5));
		JPanel panelCreate = new JPanel(new GridLayout(1, 2, 5, 5));
		setBorder(new EmptyBorder(5, 5, 5, 5));
		JPanel panelTop = new JPanel(new GridLayout(1, 3, 10, 5));
		JPanel panelBottom = new JPanel(new GridLayout(1, 3, 10, 10));
		panelTop.add(panelCreate);
		this.add(panelTop, BorderLayout.NORTH);

		panelBottom = new JPanel(new GridLayout(1, 3, 10, 10));
		this.answerListModel = new DefaultListModel<IAnswer>();
		this.answerJList = new JList<IAnswer>(this.answerListModel);
		this.answerJList
				.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		this.answerJList.setLayoutOrientation(JList.VERTICAL);
		this.answerJList.setVisibleRowCount(1);
		this.answerJList.addListSelectionListener(this);
		this.answerJList.addMouseListener(new MouseAdapter() {
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
					try {
						ExamenVerwaltung.getInstance().editItem(
								(ExamItemAbstract) answerListModel.get(index));
					} catch (Exception e) {
						ExamenVerwaltung.showException(e);
					}
					refresh();
				}
			}
		});
		// this.answerJList.setCellRenderer(new ExamListCellRenderer());
		JScrollPane answerScroller = new JScrollPane(this.answerJList);
		answerScroller.setPreferredSize(new Dimension(206, 300));
		answerScroller
				.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		answerScroller
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		answerScroller.setViewportBorder(new LineBorder(Color.BLACK));
		answerScroller.setBounds(5, 30, 205, 300);
		// this.add(pupScroller);
		panelBottom.add(answerScroller);

		this.createAnswerButton = ExamenVerwaltung.getButton(
				"newAnswer",
				5,
				5,
				100,
				20,
				this,
				ExamenVerwaltung.getText("New") + " "
						+ ExamenVerwaltung.getText("answer"),
				ExamenVerwaltung.getText("New") + " "
						+ ExamenVerwaltung.getText("answer"));

		this.deleteAnswerButton = ExamenVerwaltung.getButton(
				"delAnswer",
				110,
				5,
				100,
				20,
				this,
				ExamenVerwaltung.getText("Delete") + " "
						+ ExamenVerwaltung.getText("answer"),
				ExamenVerwaltung.getText("Delete") + " "
						+ ExamenVerwaltung.getText("answer"));
		panelCreate.add(this.createAnswerButton);
		panelCreate.add(this.deleteAnswerButton);

		this.addAnswerButton = ExamenVerwaltung.getButton(
				"addQuestion",
				235,
				5,
				205,
				20,
				this,
				ExamenVerwaltung.getText("Add") + " ->",
				ExamenVerwaltung.getText("Add") + " "
						+ ExamenVerwaltung.getText("question"));
		panelTop.add(this.addAnswerButton);

		this.removeAnswerButton = ExamenVerwaltung.getButton(
				"remCourse",
				470,
				5,
				205,
				20,
				this,
				"<- " + ExamenVerwaltung.getText("Remove"),
				ExamenVerwaltung.getText("Remove") + " "
						+ ExamenVerwaltung.getText("question"));
		panelTop.add(this.removeAnswerButton);

		this.questionPoolListModel = new DefaultListModel<>();
		this.questionPoolJList = new JList<IQuestion>(questionPoolListModel);
		this.questionPoolJList
				.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		this.questionPoolJList.setLayoutOrientation(JList.VERTICAL);
		this.questionPoolJList.setVisibleRowCount(1);
		this.questionPoolJList.addListSelectionListener(this);
		this.questionPoolJList.addMouseListener(new MouseAdapter() {
			@SuppressWarnings({ "rawtypes", "unchecked" })
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
							(ExamItemAbstract) (questionPoolJList).getModel()
									.getElementAt(index));
					refresh();
				}
			}
		});
		this.questionPoolJList.setCellRenderer(new ExamListCellRenderer());
		JScrollPane poolScroller = new JScrollPane(this.questionPoolJList);
		poolScroller.setPreferredSize(new Dimension(206, 300));
		poolScroller
				.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		poolScroller
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		poolScroller.setViewportBorder(new LineBorder(Color.BLACK));
		poolScroller.setBounds(235, 30, 205, 300);
		panelBottom.add(poolScroller);

		this.answerSelectedListModel = new DefaultListModel<>();
		this.answerSelectedJList = new JList<IAnswer>(
				this.answerSelectedListModel);
		this.answerSelectedJList
				.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		this.answerSelectedJList.setLayoutOrientation(JList.VERTICAL);
		this.answerSelectedJList.setVisibleRowCount(1);
		this.answerSelectedJList.addMouseListener(new MouseAdapter() {
			@SuppressWarnings({ "rawtypes", "unchecked" })
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
					ExamenVerwaltung
							.getInstance()
							.editItem(
									(ExamItemAbstract)questionPoolJList.getModel().getElementAt(index));
					refresh();
				}
			}
		});
		JScrollPane tookScroller = new JScrollPane(this.answerSelectedJList);
		tookScroller.setPreferredSize(new Dimension(206, 300));
		tookScroller
				.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		tookScroller
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		tookScroller.setViewportBorder(new LineBorder(Color.BLACK));
		tookScroller.setBounds(470, 30, 205, 300);
		panelBottom.add(tookScroller);

		this.add(panelBottom, BorderLayout.CENTER);
		this.refresh = false;
	}
	// ///////////////////////////////////////////////////////////////////////////////////
}