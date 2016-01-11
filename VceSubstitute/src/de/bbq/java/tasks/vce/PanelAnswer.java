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
	private IAnswer selectedAnswer;
	// ///////////////////////////////////////////////////////////////////////////////////

	// ///////////////////////////////////////////////////////////////////////////////////
	// Controls
	private JButton createAnswerButton, deleteAnswerButton, addAnswerButton,
			removeAnswerButton;
	private JList<IAnswer> answerJList;
	private JList<IQuestion> questionPoolJList, questionSelectedJList;
	private DefaultListModel<IAnswer> answerListModel;
	private DefaultListModel<IQuestion> questionPoolListModel,
			questionSelectedListModel;

	// ///////////////////////////////////////////////////////////////////////////////////

	// ///////////////////////////////////////////////////////////////////////////////////
	// Class methods
	public void refresh() {
		int selIndex = answerJList.getSelectedIndex();
		this.refresh = true;

		for (IQuestion q : ExamenVerwaltung.getQuestionList()) {
			this.questionPoolListModel.addElement(q);
		}

		IAnswer answer = null;
		for (int index = this.answerListModel.getSize(); index > 0; index--) {
			try {
				answer = (IAnswer) this.answerListModel.getElementAt(index - 1);
			} catch (Exception e) {
				ExamenVerwaltung.showException(e);
			}
			if (!ExamenVerwaltung.getQuestionList().contains(answer)) {
				try {
					this.answerListModel.remove(index - 1);
				} catch (Exception e) {
					ExamenVerwaltung.showException(e);
				}
			}
		}
		for (IAnswer a : ExamenVerwaltung.getAnswerList()) {
			this.answerListModel.addElement(a);
		}
		int poolIndex = questionPoolJList.getSelectedIndex();
		this.questionPoolListModel.clear();
		this.questionSelectedListModel.clear();
		if (this.selectedAnswer != null) {
			for (IQuestion q : ExamenVerwaltung.getQuestionList()) {
				if (this.selectedAnswer.hasQuestion(q)) {
					this.questionSelectedListModel.addElement(q);
				} else if (!this.selectedAnswer.hasQuestion()) {
					this.questionPoolListModel.addElement(q);
				}
			}
		}
		if (selIndex < 0) {
			// do Nothing
		} else if (selIndex < this.answerListModel.getSize()) {
			this.answerJList.setSelectedIndex(selIndex);
		} else {
			this.answerJList
					.setSelectedIndex(this.answerListModel.getSize() - 1);
		}
		if (poolIndex < 0) {
			// do Nothing
		} else if (poolIndex < this.questionPoolListModel.getSize()) {
			this.questionPoolJList.setSelectedIndex(poolIndex);
		} else {
			this.questionPoolJList.setSelectedIndex(this.questionPoolListModel
					.getSize() - 1);
		}
		checkButtons();
		this.refresh = false;
	}

	public void checkButtons() {
		this.deleteAnswerButton.setEnabled(this.answerJList.getModel()
				.getSize() > 0);
		if (this.questionSelectedJList.getSelectedIndex() < 0) {
			if (this.questionSelectedJList.getModel().getSize() > 0) {
				this.questionSelectedJList.setSelectedIndex(0);
			}
		}
		if (this.questionPoolJList.getSelectedIndex() < 0) {
			if (this.questionPoolJList.getModel().getSize() > 0) {
				this.questionPoolJList.setSelectedIndex(0);
			}
		}
		this.removeAnswerButton.setEnabled(this.questionSelectedJList
				.getModel().getSize() > 0);
		this.addAnswerButton.setEnabled(this.questionPoolJList.getModel()
				.getSize() > 0);
	}

	// ///////////////////////////////////////////////////////////////////////////////////

	// ///////////////////////////////////////////////////////////////////////////////////
	// ListSelectionListener
	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (!this.refresh) {
			if (e.getSource() == this.answerJList) {
				refreshAnswers();
				checkButtons();
			}
		}
	}

	private void refreshAnswers() {
		IAnswer selAnswer = (IAnswer) this.answerJList.getSelectedValue();
		if (selAnswer != null) {
			this.selectedAnswer = selAnswer;
			refresh();
		}
	}

	// ///////////////////////////////////////////////////////////////////////////////////

	// ///////////////////////////////////////////////////////////////////////////////////
	// ActionListener
	@Override
	public void actionPerformed(ActionEvent arg0) {
		this.refresh = true;
		int index = this.answerJList.getSelectedIndex();
		int indexPool = this.questionPoolJList.getSelectedIndex();
		int indexSel = this.questionSelectedJList.getSelectedIndex();
		if (arg0.getSource() == this.createAnswerButton) {
			ExamenVerwaltung.getNewAnswer(true);
			index = this.answerJList.getModel().getSize();
		} else if (arg0.getSource() == deleteAnswerButton) {
			if (this.selectedAnswer == null
					|| !ExamenVerwaltung.getAnswerList().contains(
							this.selectedAnswer)) {
				answerJList.setSelectedIndex(index);
				this.selectedAnswer = this.answerJList.getSelectedValue();
			}
			if (this.selectedAnswer != null) {
				ExamenVerwaltung
						.deleteElement((ExamItemAbstract) selectedAnswer);
				this.selectedAnswer = null;
			}
			if (index >= this.answerJList.getModel().getSize() - 1) {
				index--;
			}
			
			
			
			
		} else if (arg0.getSource() == this.addAnswerButton) {
			IQuestion selectedQuestion = (IQuestion) this.questionPoolJList
					.getSelectedValue();
			if (selectedQuestion != null) {
				if (this.selectedAnswer != null) {
					try {
						selectedQuestion.addAnswer(this.selectedAnswer);
						indexSel = this.questionSelectedJList.getModel()
								.getSize();
					} catch (Exception e) {
						ExamenVerwaltung.showException(e);
					}
				}
			}
			
			
			
			
			
		} else if (arg0.getSource() == this.removeAnswerButton) {
			IQuestion selectedQuestion = (IQuestion) this.questionSelectedJList
					.getSelectedValue();
			if (selectedQuestion != null) {
				if (this.selectedAnswer != null) {
					if (this.selectedAnswer.hasQuestion(selectedQuestion)) {
						try {
							selectedQuestion.removeAnswer(this.selectedAnswer);
							while (indexSel >= this.questionSelectedJList
									.getModel().getSize()) {
								indexSel--;
							}
							if (indexPool < 0) {
								indexPool = 0;
							}
						} catch (Exception e) {
							ExamenVerwaltung.showException(e);
						}
					}
				}
			}
		}
		
		
		this.refresh = false;
		refresh();
		if (index <= this.answerJList.getModel().getSize()) {
			this.answerJList.setSelectedIndex(index);
			this.selectedAnswer = this.answerJList.getSelectedValue();
			refreshAnswers();
		}
		if (indexPool <= this.questionPoolJList.getModel().getSize()) {
			this.questionPoolJList.setSelectedIndex(indexPool);
		}
		if (indexSel <= this.questionSelectedJList.getModel().getSize()) {
			this.questionSelectedJList.setSelectedIndex(indexSel);
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

		this.answerListModel = new DefaultListModel<>();
		this.answerJList = new JList<IAnswer>(this.answerListModel);
		this.answerJList
				.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		this.answerJList.setLayoutOrientation(JList.VERTICAL);
		this.answerJList.setVisibleRowCount(-1);
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
					ExamenVerwaltung.getInstance().editItem(
							(ExamItemAbstract) answerListModel.get(index));
				}
			}
		});
		this.answerJList.setCellRenderer(new ExamListCellRenderer());
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
				"newPupil",
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
				"delPupil",
				110,
				5,
				100,
				20,
				this,
				ExamenVerwaltung.getText("Delete") + " "
						+ ExamenVerwaltung.getText("answer"),
				ExamenVerwaltung.getText("Delete") + " "
						+ ExamenVerwaltung.getText("answer"));
		// this.add(this.deleteStudentButton);
		// this.add(this.addStudentButton);
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
		// this.add(this.addCourseButton);
		panelTop.add(this.addAnswerButton);
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
		this.questionPoolJList.setVisibleRowCount(-1);
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
					ExamenVerwaltung
							.getInstance()
							.editItem(
									(ExamItemAbstract) ((List<IQuestion>) questionPoolJList)
											.get(index));
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
		// this.add(poolScroller);

		this.questionSelectedListModel = new DefaultListModel<>();
		this.questionSelectedJList = new JList<IQuestion>(
				this.questionSelectedListModel);
		this.questionSelectedJList
				.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		this.questionSelectedJList.setLayoutOrientation(JList.VERTICAL);
		this.questionSelectedJList.setVisibleRowCount(-1);
		this.questionSelectedJList.addMouseListener(new MouseAdapter() {
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
									(ExamItemAbstract) ((List<IQuestion>) questionPoolJList)
											.get(index));
					refresh();
				}
			}
		});

		JScrollPane tookScroller = new JScrollPane(this.questionSelectedJList);
		tookScroller.setPreferredSize(new Dimension(206, 300));
		tookScroller
				.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		tookScroller
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		tookScroller.setViewportBorder(new LineBorder(Color.BLACK));
		tookScroller.setBounds(470, 30, 205, 300);
		// this.add(tookScroller);
		panelBottom.add(poolScroller);
		panelBottom.add(tookScroller);
		this.add(panelBottom, BorderLayout.CENTER);
		this.refresh = false;
	}
	// ///////////////////////////////////////////////////////////////////////////////////

}
