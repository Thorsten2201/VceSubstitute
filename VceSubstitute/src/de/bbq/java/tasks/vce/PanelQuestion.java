package de.bbq.java.tasks.vce;

import java.awt.BorderLayout;
import java.awt.Color;
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
public class PanelQuestion extends JPanel implements ActionListener, ListSelectionListener, ChangeListener {
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
	private JButton addCourseButton, deleteCourseButton, saveAllButton, loadAllButton;
	private JList<ISolution> coursesJList;
	private JList<IAnswer> studentsJList;
	private JTextField teacherTextField;
	private JSlider dataBase;
	private DefaultListModel<ISolution> courseListModel;
	private DefaultListModel<IAnswer> studentListModel;
	/////////////////////////////////////////////////////////////////////////////////////

	/////////////////////////////////////////////////////////////////////////////////////
	// Class methods
	public void refresh() {
		this.refresh = true;
		this.teacherTextField.setText(null);
		ISolution cindex = null;
		for (int index = this.courseListModel.getSize(); index > 0; index--) {
			try {
				cindex = this.courseListModel.getElementAt(index - 1);
			} catch (Exception e) {
				ExamenVerwaltung.showException(e);
			}

			if (!ExamenVerwaltung.getSolutionList().contains(cindex)) {
				try {
					this.courseListModel.remove(index - 1);
				} catch (Exception e) {
					ExamenVerwaltung.showException(e);
				}

			}
		}
		for (ISolution c : ExamenVerwaltung.getSolutionList()) {
			if (!this.courseListModel.contains(c)) {
				this.courseListModel.addElement(c);
			}
		}
		this.deleteCourseButton.setEnabled(this.coursesJList.getSelectedValue() != null);
		boolean enableSave = false, enableLoad = true;
		if (!ExamenVerwaltung.getSolutionList().isEmpty()) {
			enableSave = true;
		} else if (!ExamenVerwaltung.getQuestionList().isEmpty()) {
			enableSave = true;
		} else if (!ExamenVerwaltung.getStudentList().isEmpty()) {
			enableSave = true;
		}
		int index = this.coursesJList.getSelectedIndex();
		if (index >= 0) {
			selectCourse(this.coursesJList.getModel().getElementAt(index));
		}
		this.saveAllButton.setEnabled(enableSave);
		this.loadAllButton.setEnabled(enableLoad); // ??
		this.refresh = false;
	}
	/////////////////////////////////////////////////////////////////////////////////////

	/////////////////////////////////////////////////////////////////////////////////////
	// ListSelectionListener
	@Override
	public void valueChanged(ListSelectionEvent arg0) {
		ISolution selectedCourse = this.coursesJList.getSelectedValue();
		if (!this.refresh && arg0.getSource() == this.coursesJList) {
			selectCourse(selectedCourse);
		}
	}

	private void selectCourse(ISolution selectedCourse) {
		if (selectedCourse != null) {
			if (selectedCourse.hasQuestion()) {
				this.teacherTextField.setText(selectedCourse.getQuestion().toString());
			} else
				this.teacherTextField.setText("");
		}
		this.studentListModel.clear();
		if (selectedCourse != null) {
			if (selectedCourse.hasAnswers()) {
				for (IAnswer student : selectedCourse.getAnswers()) {
					this.studentListModel.addElement(student);
				}
			}
		}
	}

	@Override
	public void stateChanged(ChangeEvent arg0) {
		if (arg0.getSource() == dataBase) {
			if (dataBase.getValue() == 0) {
				if (!ExamenVerwaltung.getSelectedDao().equals(EDaoSchool.FILE)) {
					ExamenVerwaltung.setSelectedDao(EDaoSchool.FILE);
				}
			} else if (dataBase.getValue() == 1) {
				if (!ExamenVerwaltung.getSelectedDao().equals(EDaoSchool.JDBC_MYSQL)) {
					ExamenVerwaltung.setSelectedDao(EDaoSchool.JDBC_MYSQL);
				}
			}
		}
	}
	/////////////////////////////////////////////////////////////////////////////////////

	/////////////////////////////////////////////////////////////////////////////////////
	// ActionListener
	@Override
	public void actionPerformed(ActionEvent arg0) {
		this.refresh = true;
		int index = this.coursesJList.getSelectedIndex();
		if (arg0.getSource() == addCourseButton) {
			ExamenVerwaltung.getNewSolution(true);
			index = this.coursesJList.getModel().getSize();
		} else if (arg0.getSource() == deleteCourseButton) {
			ISolution selected = this.coursesJList.getSelectedValue();
			if (selected != null) {
				ExamenVerwaltung.deleteElement((ExamItemAbstract) selected);
			}
			if (index >= this.coursesJList.getModel().getSize() - 1) {
				index--;
			}
		} else if (arg0.getSource() == saveAllButton) {
			// if (!SchoolLauncher.getCourseList().isEmpty()) {
			DaoSchoolAbstract.getDaoSchool(ExamenVerwaltung.getSelectedDao()).saveAll();
			// } else if (!SchoolLauncher.getTeacherList().isEmpty()) {
			// SchoolLauncher.getTeacherList().get(0).saveAll();
			// } else if (!SchoolLauncher.getStudentList().isEmpty()) {
			// SchoolLauncher.getStudentList().get(0).saveAll();
			// }
		} else if (arg0.getSource() == loadAllButton) {
			DaoSchoolAbstract.getDaoSchool(ExamenVerwaltung.getSelectedDao()).loadAll();
		}
		this.refresh = false;
		refresh();
		if (index <= this.coursesJList.getModel().getSize()) {
			this.coursesJList.setSelectedIndex(index);
			selectCourse(this.coursesJList.getSelectedValue());
		}
		this.deleteCourseButton.setEnabled(this.coursesJList.getSelectedValue() != null);
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
		this.courseListModel = new DefaultListModel<ISolution>();
		this.coursesJList = new JList<ISolution>(this.courseListModel);
		this.coursesJList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		this.coursesJList.setLayoutOrientation(JList.VERTICAL);
		this.coursesJList.setVisibleRowCount(-1);
		this.coursesJList.addListSelectionListener(this);
		this.coursesJList.addMouseListener(new MouseAdapter() {
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
					ExamenVerwaltung.getInstance().editItem((ExamItemAbstract) courseListModel.get(index));
					refresh();
				}
			}
		});
		this.coursesJList.setCellRenderer(new SchoolListCellRenderer());
		JScrollPane coursScroller = new JScrollPane(this.coursesJList);
		// coursScroller.setPreferredSize(new Dimension(206, 300));
		coursScroller.setBounds(5, 30, 265, 300);
		coursScroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		coursScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		coursScroller.setViewportBorder(new LineBorder(Color.BLACK));
		// this.add(coursScroller);
		panelBottom.add(coursScroller);

		this.dataBase = new JSlider();
		this.dataBase.setMinimum(0);
		this.dataBase.setMaximum(1);
		this.dataBase.setMajorTickSpacing(1);
		this.dataBase.setMinorTickSpacing(1);
		this.dataBase.createStandardLabels(1);
		this.dataBase.setPaintTicks(true);
		this.dataBase.setPaintLabels(true);
		this.dataBase.setValue(0);
		Hashtable<Integer, JLabel> ht = new Hashtable<Integer, JLabel>();

		JLabel label = new JLabel(EDaoSchool.FILE.toString()); // new
																// JLabel("Filesystem");
		ht.put(0, label);
		label = new JLabel(EDaoSchool.JDBC_MYSQL.toString()); // new
																// JLabel("Jdbc
																// MySql");
		ht.put(1, label);
		this.dataBase.setLabelTable(ht);
		this.dataBase.setPaintLabels(true);
		// this.dataBase.setInverted(true);
		this.dataBase.addChangeListener(this);
		this.dataBase.setBounds(5, 30, 130, 20);

		this.addCourseButton = ExamenVerwaltung.getButton("newCourse", 5, 5, 130, 20, this, "Neuer Kurs", "Neuer Kurs");

		this.deleteCourseButton = ExamenVerwaltung.getButton("delCourse", 110, 5, 130, 20, this, "Löschen",
				"Kurs löschen");
		panelCreate.add(this.addCourseButton);
		panelCreate.add(this.deleteCourseButton);

		this.saveAllButton = ExamenVerwaltung.getButton("saveAll", 450, 5, 130, 20, this, "Alles speichern",
				"Kurse, Leerer und Schüler speichern");
		this.loadAllButton = ExamenVerwaltung.getButton("loadAll", 590, 5, 130, 20, this, "Alles laden",
				"Kurse, Leerer und Schüler laden");
		panelLoadSave.add(this.saveAllButton);
		panelLoadSave.add(this.loadAllButton);

		this.teacherTextField = new JTextField();
		this.teacherTextField.setBounds(235, 5, 265, 20);
		this.teacherTextField.setText("");
		this.teacherTextField.setEditable(false);
		this.teacherTextField.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				int index = -1;
				if (evt.getClickCount() == 2) {
					// Double-click detected
					index = coursesJList.getSelectedIndex();
				} else if (evt.getClickCount() == 3) {
					// Triple-click detected
					index = coursesJList.getSelectedIndex();
				}
				if (index >= 0) {
					ExamenVerwaltung.getInstance().editItem((ExamItemAbstract) courseListModel.get(index).getQuestion());
					refresh();
				}
			}
		});
		panelTeacher.add(this.teacherTextField);
		// this.add(panelTop);

		this.studentListModel = new DefaultListModel<IAnswer>();
		this.studentsJList = new JList<IAnswer>(this.studentListModel);
		this.studentsJList.setSelectionModel(new DisabledItemSelectionModel());
		this.studentsJList.setLayoutOrientation(JList.VERTICAL);
		this.studentsJList.setVisibleRowCount(-1);
		this.studentsJList.addListSelectionListener(this);
		this.studentsJList.addMouseListener(new MouseAdapter() {
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
					ExamenVerwaltung.getInstance().editItem((ExamItemAbstract) studentListModel.get(index));
					refresh();
				}
			}
		});

		JScrollPane pupScroller = new JScrollPane(this.studentsJList);
		// pupScroller.setPreferredSize(new Dimension(206, 300));
		pupScroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		pupScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		pupScroller.setViewportBorder(new LineBorder(Color.BLACK));
		pupScroller.setBounds(235, 30, 265, 300);
		// this.add(pupScroller);
		panelBottom.add(pupScroller);

		// panelBottom.add(previewJList);
		panelBottom.add(this.dataBase);

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
