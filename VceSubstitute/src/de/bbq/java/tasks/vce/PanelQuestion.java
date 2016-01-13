package de.bbq.java.tasks.vce;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.peer.PanelPeer;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

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
public class PanelQuestion extends JPanel implements ActionListener, ListSelectionListener {
	/////////////////////////////////////////////////////////////////////////////////////
	// Static
	private static final long serialVersionUID = -7720278844639602571L;
	/////////////////////////////////////////////////////////////////////////////////////

	/////////////////////////////////////////////////////////////////////////////////////
	// Class Properties
	private boolean refresh = true;
	/////////////////////////////////////////////////////////////////////////////////////

	/////////////////////////////////////////////////////////////////////////////////////
	// Controls
	private JButton addQuestionButton, deleteQuestionButton, addQuestioneButton, removeQuestionButton;
	private JList<IQuestion> questionJList;
	private JList<IExam> examPoolJList;
	private JList<IQuestion> questionPoolJList;
	// private JList<IAnswer> answersJList;

	private DefaultListModel<IQuestion> questionPoolListModel;
	private DefaultListModel<IQuestion> questionListModel;
	private DefaultListModel<IExam> examPoolListModel;
	// private DefaultListModel<IAnswer> answerListModel;

	private IExam selectedExam;
	/////////////////////////////////////////////////////////////////////////////////////

	/////////////////////////////////////////////////////////////////////////////////////
	// Class methods
	public void refresh() {
		this.refresh = true;
		IQuestion cindex = null;
		IExam exindex = null;
		for (int index = this.questionPoolListModel.getSize(); index > 0; index--) {
			try {
				cindex = this.questionPoolListModel.getElementAt(index - 1);
			} catch (Exception e) {
				ExamenVerwaltung.showException(e);
			}

			if (!ExamenVerwaltung.getQuestionList().contains(cindex)) {
				try {
					this.questionPoolListModel.remove(index - 1);
				} catch (Exception e) {
					ExamenVerwaltung.showException(e);
				}

			}
		}
		for (IQuestion c : ExamenVerwaltung.getQuestionList()) {
			if (!this.questionPoolListModel.contains(c)) {
				this.questionPoolListModel.addElement(c);
			}
		}

		for (int index = this.examPoolListModel.getSize(); index > 0; index--) {
			try {
				exindex = this.examPoolListModel.getElementAt(index - 1);
			} catch (Exception e) {
				ExamenVerwaltung.showException(e);
			}

			if (!ExamenVerwaltung.getExamList().contains(exindex)) {
				try {
					this.examPoolListModel.remove(index - 1);
				} catch (Exception e) {
					ExamenVerwaltung.showException(e);
				}

			}
		}
		for (IExam ex : ExamenVerwaltung.getExamList()) {
			if (!this.examPoolListModel.contains(ex)) {
				this.examPoolListModel.addElement(ex);
			}
		}
		// int poolIndex = questionPoolJList.getSelectedIndex();
		// this.questionPoolListModel.clear();
		// this.questionListModel.clear();
		// if (this.selectedExam != null) {
		// for (IQuestion q : ExamenVerwaltung.getQuestionList()) {
		// if (this.selectedExam.hasQuestion (q)) {
		// this.questionListModel.addElement(q);
		// } else {
		// this.questionPoolListModel.addElement(q);
		// }
		// }
		// }
		// if (poolIndex < 0) {
		// // do Nothing
		// } else if (poolIndex < this.questionPoolListModel.getSize()) {
		// this.questionPoolJList.setSelectedIndex(poolIndex);
		// } else {
		// this.questionPoolJList.setSelectedIndex(this.questionPoolListModel.getSize()
		// - 1);
		// }
		// this.deleteQuestionButton.setEnabled(this.questionPoolJList.getSelectedValue()
		// != null);
		// TODO?? int index = this.questionPoolJList.getSelectedIndex();
		// if (index >= 0) {
		// selectQuestion(this.questionPoolJList.getModel().getElementAt(index));
		// }
		checkButtons();
		this.refresh = false;
	}

	private void refreshExams() {
		IExam selExam = (IExam) this.examPoolJList.getSelectedValue();
		if (selExam != null) {
			this.selectedExam = selExam;
			refresh();
		}
	}

	public void checkButtons() {
		this.removeQuestionButton.setEnabled(this.questionPoolJList.getModel().getSize() > 0);
		this.addQuestioneButton.setEnabled(this.questionPoolJList.getModel().getSize() > 0);
	}
	/////////////////////////////////////////////////////////////////////////////////////

	/////////////////////////////////////////////////////////////////////////////////////
	// ListSelectionListener
	@Override
	public void valueChanged(ListSelectionEvent arg0) {
		IQuestion selectedQuestion = this.questionPoolJList.getSelectedValue();
		if (!this.refresh && arg0.getSource() == this.questionPoolJList) {
			// TODO selectQuestion(selectedQuestion);
		}
	}

	private void selectQuestion(IQuestion selectedQuestion) {
		this.questionListModel.clear();
		if (selectedQuestion != null) {
			this.questionListModel.addElement(selectedQuestion);
		}
	}
	/////////////////////////////////////////////////////////////////////////////////////

	/////////////////////////////////////////////////////////////////////////////////////
	// ActionListener
	@Override
	public void actionPerformed(ActionEvent arg0) {
		this.refresh = true;
		int index = this.questionPoolJList.getSelectedIndex();
		if (arg0.getSource() == addQuestionButton) {
			ExamenVerwaltung.getNewSolution(true);
			index = this.questionPoolJList.getModel().getSize();
		} else if (arg0.getSource() == deleteQuestionButton) {
			IQuestion selected = this.questionPoolJList.getSelectedValue();
			if (selected != null) {
				ExamenVerwaltung.deleteElement((ExamItemAbstract) selected);
			}
			if (index >= this.questionPoolJList.getModel().getSize() - 1) {
				index--;
			}
		}
		this.refresh = false;
		refresh();
		if (index <= this.questionPoolJList.getModel().getSize()) {
			this.questionPoolJList.setSelectedIndex(index);
			// selectQuestion(this.questionPoolJList.getSelectedValue());
		}
		this.deleteQuestionButton.setEnabled(this.questionPoolJList.getSelectedValue() != null);
	}
	/////////////////////////////////////////////////////////////////////////////////////

	/////////////////////////////////////////////////////////////////////////////////////
	// Construct
	public PanelQuestion() {
		// this.daoSchoolAbstract = daoSchoolAbstract;
		// this.setLayout(null); // new GridLayout(1, 1));
		this.setLayout(new BorderLayout(5, 5));
		JPanel panelCreate = new JPanel(new GridLayout(1, 2, 5, 5));
		JPanel panelTeacher = new JPanel(new GridLayout(1, 1, 5, 5));
		JPanel panelLoadSave = new JPanel(new GridLayout(1, 2, 5, 5));
		setBorder(new EmptyBorder(5, 5, 5, 5));

		// JLabel spacer = new JLabel("");

		JPanel panelTop = new JPanel(new GridLayout(1, 3, 10, 5));
		panelTop.add(panelCreate);
		panelTop.add(panelTeacher);
		panelTop.add(panelLoadSave);
		this.add(panelTop, BorderLayout.NORTH);

		JPanel panelBottom = new JPanel(new GridLayout(1, 3, 10, 10));
		this.questionPoolListModel = new DefaultListModel<IQuestion>();
		this.questionPoolJList = new JList<IQuestion>(this.questionPoolListModel);
		this.questionPoolJList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		this.questionPoolJList.setLayoutOrientation(JList.VERTICAL);
		this.questionPoolJList.setVisibleRowCount(-1);
		this.questionPoolJList.addListSelectionListener(this);
		this.questionPoolJList.addMouseListener(new MouseAdapter() {
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
					ExamenVerwaltung.getInstance().editItem((ExamItemAbstract) questionPoolListModel.get(index));
					refresh();
				}
			}
		});
		JScrollPane poolScroller = new JScrollPane(this.questionPoolJList);
		poolScroller.setPreferredSize(new Dimension(206, 300));
		poolScroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		poolScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		poolScroller.setViewportBorder(new LineBorder(Color.BLACK));
		poolScroller.setBounds(235, 30, 205, 300);
		panelBottom.add(poolScroller);
		// panelBottom.add(previewJList);

		this.addQuestioneButton = ExamenVerwaltung.getButton("addCourse", 235, 5, 205, 20, this,
				ExamenVerwaltung.getText("Add") + " ->", "Kurs Hinzufügen");
		// this.add(this.addCourseButton);
		panelTeacher.add(this.addQuestioneButton);

		this.removeQuestionButton = ExamenVerwaltung.getButton("remCourse", 470, 5, 205, 20, this,
				"<- " + ExamenVerwaltung.getText("Remove"), "Kurs Entfernen");
		// this.add(this.removeCourseButton);
		panelLoadSave.add(this.removeQuestionButton);

		this.addQuestionButton = ExamenVerwaltung.getButton("newCourse", 5, 5, 130, 20, this,
				ExamenVerwaltung.getText("New") + " " + ExamenVerwaltung.getText("question"),
				ExamenVerwaltung.getText("New") + " " + ExamenVerwaltung.getText("question"));

		this.deleteQuestionButton = ExamenVerwaltung.getButton("delCourse", 110, 5, 130, 20, this,
				ExamenVerwaltung.getText("Delete") + " " + ExamenVerwaltung.getText("question"), "Kurs löschen");
		panelCreate.add(this.addQuestionButton);
		panelCreate.add(this.deleteQuestionButton);

		// this.add(panelTop);

		this.examPoolListModel = new DefaultListModel<>();
		this.examPoolJList = new JList<IExam>(examPoolListModel);
		this.examPoolJList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		this.examPoolJList.setLayoutOrientation(JList.VERTICAL);
		this.examPoolJList.setVisibleRowCount(-1);
		this.examPoolJList.addMouseListener(new MouseAdapter() {
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
					ExamenVerwaltung.getInstance()
							.editItem((ExamItemAbstract) ((List<IQuestion>) examPoolJList).get(index));
					refresh();
				}
			}
		});

		JScrollPane examScroller = new JScrollPane(this.examPoolJList);
		examScroller.setPreferredSize(new Dimension(206, 300));
		examScroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		examScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		examScroller.setViewportBorder(new LineBorder(Color.BLACK));
		examScroller.setBounds(235, 30, 205, 300);
		panelBottom.add(examScroller);
		// panelBottom.add(previewJList);

		this.questionListModel = new DefaultListModel<IQuestion>();
		this.questionJList = new JList<IQuestion>(this.questionListModel);
		this.questionJList.setSelectionModel(new DisabledItemSelectionModel());
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
					refresh();
				}
			}
		});
		this.questionJList.setCellRenderer(new ExamListCellRenderer());
		JScrollPane selQuestionScroller = new JScrollPane(this.questionJList);
		// coursScroller.setPreferredSize(new Dimension(206, 300));
		selQuestionScroller.setBounds(235, 30, 205, 300);
		selQuestionScroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		selQuestionScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		selQuestionScroller.setViewportBorder(new LineBorder(Color.BLACK));
		// this.add(coursScroller);
		panelBottom.add(selQuestionScroller);

		this.add(panelBottom, BorderLayout.CENTER);
		this.refresh = false;
	}

	private class DisabledItemSelectionModel extends DefaultListSelectionModel {
		private static final long serialVersionUID = 1L;

		@Override
		public void setSelectionInterval(int index0, int index1) {
			super.setSelectionInterval(-1, -1);
		}
	}
	/////////////////////////////////////////////////////////////////////////////////////

}
