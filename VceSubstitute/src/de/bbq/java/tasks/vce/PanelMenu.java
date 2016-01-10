package de.bbq.java.tasks.vce;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class PanelMenu extends JPanel implements ActionListener, ChangeListener{
	/////////////////////////////////////////////////////////////////////////////////////
	// Static
	private static final long serialVersionUID = -7331762455017810175L;
	/////////////////////////////////////////////////////////////////////////////////////

	/////////////////////////////////////////////////////////////////////////////////////
	// Class Properties
	private boolean refresh = true;
	/////////////////////////////////////////////////////////////////////////////////////
	
	/////////////////////////////////////////////////////////////////////////////////////
	// Controls
	private JSlider dataBase;
	private JButton saveAllButton, loadAllButton;
	/////////////////////////////////////////////////////////////////////////////////////

	/////////////////////////////////////////////////////////////////////////////////////
	// Construct
	public PanelMenu() {
		this.setLayout(new FlowLayout(3));

		this.saveAllButton = ExamenVerwaltung.getButton("saveAll", 450, 5, 130, 20, this, "Alles speichern",
				"Kurse, Leerer und Schüler speichern");
		this.loadAllButton = ExamenVerwaltung.getButton("loadAll", 590, 5, 130, 20, this, "Alles laden",
				"Kurse, Leerer und Schüler laden");
		add(this.saveAllButton);
		add(this.loadAllButton);

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
		add(this.dataBase);	
	}

	/////////////////////////////////////////////////////////////////////////////////////
	// Class methods
	public void refresh() {
		this.refresh = true;
		boolean enableSave = false, enableLoad = true;
		if (!ExamenVerwaltung.getExamList().isEmpty()) {
			enableSave = true;
		} else if (!ExamenVerwaltung.getQuestionList().isEmpty()) {
			enableSave = true;
		} else if (!ExamenVerwaltung.getAnswerList().isEmpty()) {
			enableSave = true;
		}
		this.saveAllButton.setEnabled(enableSave);
		this.loadAllButton.setEnabled(enableLoad); // ??
		this.refresh = false;
	}
	/////////////////////////////////////////////////////////////////////////////////////
	
	/////////////////////////////////////////////////////////////////////////////////////
	// ActionListener
	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (arg0.getSource() == saveAllButton) {
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
		ExamenVerwaltung.refresh();
	}
	/////////////////////////////////////////////////////////////////////////////////////

	
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

}
