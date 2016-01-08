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

/**
 * @author Thorsten2201
 *
 */
public class PanelExam extends JPanel implements ActionListener, ListSelectionListener {
	/////////////////////////////////////////////////////////////////////////////////////
	// Static
	private static final long serialVersionUID = -6951335589393103017L;
	/////////////////////////////////////////////////////////////////////////////////////

	/////////////////////////////////////////////////////////////////////////////////////
	// Class Properties
	private boolean refresh = true;
	private IQuestion selectedQuestion;
	/////////////////////////////////////////////////////////////////////////////////////

	/////////////////////////////////////////////////////////////////////////////////////
	// Controls
	private JButton addQuestionButton, deleteQuestionButton, addSolutionButton, removeSolutionButton;
	private JList<IQuestion> questionJList;
	private JList<ISolution> solutionPoolJList, solutionSelectedJList;
	private DefaultListModel<IQuestion> questionListModel;
	private DefaultListModel<ISolution> solutionPoolListModel, solutionSelectedListModel;
	/////////////////////////////////////////////////////////////////////////////////////

	/////////////////////////////////////////////////////////////////////////////////////
	// Class methods
	public void refresh() {
		int selIndex = questionJList.getSelectedIndex();
		this.refresh = true;
		IQuestion cindex = null;
		for (int index = this.questionListModel.getSize(); index > 0; index--) {
			try {
				cindex = this.questionListModel.getElementAt(index - 1);
			} catch (Exception e) {
				ExamenVerwaltung.showException(e);
			}

			if (!ExamenVerwaltung.getSolutionList().contains(cindex)) {
				try {
					this.questionListModel.remove(index - 1);
				} catch (Exception e) {
					ExamenVerwaltung.showException(e);
				}

			}
		}
		for (IQuestion q : ExamenVerwaltung.getQuestionList()) {
			this.questionListModel.addElement(q);
		}
		int poolIndex = solutionPoolJList.getSelectedIndex();
		this.solutionPoolListModel.clear();
		this.solutionSelectedListModel.clear();
		if (this.selectedQuestion != null) {
			for (ISolution c : ExamenVerwaltung.getSolutionList()) {
				if (this.selectedQuestion.equals(c.getQuestion())) {
					this.solutionSelectedListModel.addElement(c);
				} else if (!c.hasQuestion()) {
					this.solutionPoolListModel.addElement(c);
				}
			}
		}
		if (selIndex < 0) {
			// do Nothing
		} else if (selIndex < this.questionListModel.getSize()) {
			this.questionJList.setSelectedIndex(selIndex);
		} else {
			this.questionJList.setSelectedIndex(this.questionListModel.getSize() - 1);
		}
		if (poolIndex < 0) {
			// do Nothing
		} else if (poolIndex < this.solutionPoolListModel.getSize()) {
			this.solutionPoolJList.setSelectedIndex(poolIndex);
		} else {
			this.solutionPoolJList.setSelectedIndex(this.solutionPoolListModel.getSize() - 1);
		}
		checkButtons();
		this.refresh = false;
	}

	void checkButtons() {
		this.deleteQuestionButton.setEnabled(this.questionJList.getModel().getSize() > 0);
		if (this.solutionSelectedJList.getSelectedIndex() < 0) {
			if (this.solutionSelectedJList.getModel().getSize() > 0) {
				this.solutionSelectedJList.setSelectedIndex(0);
			}
		}
		if (this.solutionPoolJList.getSelectedIndex() < 0) {
			if (this.solutionPoolJList.getModel().getSize() > 0) {
				this.solutionPoolJList.setSelectedIndex(0);
			}
		}
		this.removeSolutionButton.setEnabled(this.solutionSelectedJList.getModel().getSize() > 0);
		this.addSolutionButton.setEnabled(this.solutionPoolJList.getModel().getSize() > 0);
	}
	/////////////////////////////////////////////////////////////////////////////////////

	/////////////////////////////////////////////////////////////////////////////////////
	// ListSelectionListener
	@Override
	public void valueChanged(ListSelectionEvent arg0) {
		if (!this.refresh) {
			if (arg0.getSource() == this.questionJList) {
				refreshQuestions();
				checkButtons();
			}
		}
	}

	private void refreshQuestions() {
		IQuestion selTeacher = this.questionJList.getSelectedValue();
		if (selTeacher != null) {
			this.selectedQuestion = selTeacher;
			refresh();
		}
	}
	/////////////////////////////////////////////////////////////////////////////////////

	/////////////////////////////////////////////////////////////////////////////////////
	// ActionListener
	@Override
	public void actionPerformed(ActionEvent arg0) {
		this.refresh = true;
		int index = this.questionJList.getSelectedIndex();
		int indexPool = this.solutionPoolJList.getSelectedIndex();
		int indexSel = this.solutionSelectedJList.getSelectedIndex();
		if (arg0.getSource() == this.addQuestionButton) {
			ExamenVerwaltung.getNewQuestion(true);
			index = this.questionJList.getModel().getSize();
		} else if (arg0.getSource() == this.deleteQuestionButton) {
			if (!ExamenVerwaltung.getQuestionList().contains(this.selectedQuestion)) {
				this.questionJList.setSelectedIndex(index);
				this.selectedQuestion = this.questionJList.getSelectedValue();
			}
			if (this.selectedQuestion != null) {
				ExamenVerwaltung.deleteElement((ExamItemAbstract) this.selectedQuestion);
				this.selectedQuestion = null;
			}
			if (index >= this.questionJList.getModel().getSize() - 1) {
				index--;
			}
		}
		if (arg0.getSource() == this.addSolutionButton) {
			ISolution selectedCourse = this.solutionPoolJList.getSelectedValue();
			if (selectedCourse != null) {
				if (selectedQuestion != null) {
					try {
						if (!selectedCourse.hasQuestion()) {
							try {
								this.selectedQuestion.addSolution(selectedCourse);
								indexSel = this.solutionSelectedJList.getModel().getSize();
								if (indexPool >= this.solutionPoolJList.getModel().getSize() - 1) {
									indexPool--;
								}
							} catch (Exception e) {
								ExamenVerwaltung.showException(e);
							}
						}
					} catch (Exception e) {
						ExamenVerwaltung.showException(e);
					}
				}
			}
		} else if (arg0.getSource() == this.removeSolutionButton) {
			ISolution selectedCourse = this.solutionSelectedJList.getSelectedValue();
			if (selectedCourse != null) {
				if (this.selectedQuestion != null) {
					try {
						this.selectedQuestion.deleteSolution(selectedCourse);
						if (indexSel >= this.solutionSelectedJList.getModel().getSize() - 1) {
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
		this.refresh = false;
		refresh();
		if (index <= this.questionJList.getModel().getSize()) {
			this.questionJList.setSelectedIndex(index);
			this.selectedQuestion = this.questionJList.getSelectedValue();
			refreshQuestions();
		}
		if (indexPool <= this.solutionPoolJList.getModel().getSize()) {
			this.solutionPoolJList.setSelectedIndex(indexPool);
		}
		if (indexSel <= this.solutionSelectedJList.getModel().getSize()) {
			this.solutionSelectedJList.setSelectedIndex(indexSel);
		}
		checkButtons();
	}
	/////////////////////////////////////////////////////////////////////////////////////

	/////////////////////////////////////////////////////////////////////////////////////
	// Construct
	public PanelExam() {
		// this.setLayout(null); // new GridLayout(1, 1));
		this.setLayout(new BorderLayout(5, 5));
		JPanel panelCreate = new JPanel(new GridLayout(1, 2, 5, 5));
		setBorder(new EmptyBorder(5, 5, 5, 5));
		JPanel panelTop = new JPanel(new GridLayout(1, 3, 10, 5));
		JPanel panelBottom = new JPanel(new GridLayout(1, 3, 10, 10));
		panelTop.add(panelCreate);
		this.add(panelTop, BorderLayout.NORTH);

		this.questionListModel = new DefaultListModel<>();
		this.questionJList = new JList<IQuestion>(this.questionListModel);
		this.questionJList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		this.questionJList.setLayoutOrientation(JList.VERTICAL);
		this.questionJList.setVisibleRowCount(-1);
		this.questionJList.addListSelectionListener(this);
		this.questionJList.addMouseListener(new MouseAdapter() {
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
					ExamenVerwaltung.getInstance().editItem((ExamItemAbstract) questionListModel.get(index));
				}
			}
		});

		this.questionJList.setCellRenderer(new SchoolListCellRenderer());
		JScrollPane teacherScroller = new JScrollPane(this.questionJList);
		teacherScroller.setPreferredSize(new Dimension(206, 300));
		teacherScroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		teacherScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		teacherScroller.setViewportBorder(new LineBorder(Color.BLACK));
		teacherScroller.setBounds(5, 30, 205, 300);
		// this.add(teacherScroller);
		panelBottom.add(teacherScroller);

		this.addQuestionButton = ExamenVerwaltung.getButton("newTeacher", 5, 5, 100, 20, this, "Erstellen",
				"Neuer Leerer");
		this.deleteQuestionButton = ExamenVerwaltung.getButton("delTeacher", 110, 5, 100, 20, this, "Löschen",
				"Leerer löschen");
		// this.add(this.addTeacherButton);
		// this.add(this.deleteTeacherButton);
		panelCreate.add(this.addQuestionButton);
		panelCreate.add(this.deleteQuestionButton);

		this.addSolutionButton = ExamenVerwaltung.getButton("addCourse", 235, 5, 205, 20, this, "Hinzufügen ->",
				"Kurs Hinzufügen");
		// this.add(this.addCourseButton);
		panelTop.add(this.addSolutionButton);

		this.removeSolutionButton = ExamenVerwaltung.getButton("remCourse", 470, 5, 205, 20, this, "<- Entfernen",
				"Kurs Entfernen");
		// this.add(this.removeCourseButton);
		panelTop.add(this.removeSolutionButton);

		this.solutionPoolListModel = new DefaultListModel<>();
		this.solutionPoolJList = new JList<ISolution>(this.solutionPoolListModel);
		this.solutionPoolJList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		this.solutionPoolJList.setLayoutOrientation(JList.VERTICAL);
		this.solutionPoolJList.setVisibleRowCount(-1);
		this.solutionPoolJList.addMouseListener(new MouseAdapter() {
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
					ExamenVerwaltung.getInstance().editItem((ExamItemAbstract) ((List<ISolution>) solutionPoolJList).get(index));
					refresh();
				}
			}
		});
		
		JScrollPane poolScroller = new JScrollPane(this.solutionPoolJList);
		poolScroller.setPreferredSize(new Dimension(206, 300));
		poolScroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		poolScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		poolScroller.setViewportBorder(new LineBorder(Color.BLACK));
		poolScroller.setBounds(235, 30, 205, 300);
		// this.add(poolScroller);

		this.solutionSelectedListModel = new DefaultListModel<>();
		this.solutionSelectedJList = new JList<ISolution>(this.solutionSelectedListModel);
		this.solutionSelectedJList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		this.solutionSelectedJList.setLayoutOrientation(JList.VERTICAL);
		this.solutionSelectedJList.setVisibleRowCount(-1);
		this.solutionSelectedJList.addMouseListener(new MouseAdapter() {
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
					ExamenVerwaltung.getInstance().editItem((ExamItemAbstract) ((List<ISolution>) solutionPoolJList).get(index));
					refresh();
				}
			}
		});
		
		JScrollPane tookScroller = new JScrollPane(this.solutionSelectedJList);
		tookScroller.setPreferredSize(new Dimension(206, 300));
		tookScroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		tookScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		tookScroller.setViewportBorder(new LineBorder(Color.BLACK));
		tookScroller.setBounds(470, 30, 205, 300);
		// this.add(tookScroller);
		panelBottom.add(poolScroller);
		panelBottom.add(tookScroller);
		this.add(panelBottom, BorderLayout.CENTER);
		this.refresh = false;
	}
	/////////////////////////////////////////////////////////////////////////////////////

}
