package de.bbq.java.tasks.vce;

import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.Dimension;
import java.awt.GridLayout;
//import java.awt.Image;
//import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * @author Thorsten2201
 *
 */
public class ExamenVerwaltung extends JFrame implements WindowListener {

	/////////////////////////////////////////////////////////////////////////////////////
	// Class Properties
	private static final long serialVersionUID = -2039718453107554584L;
	private static final int winLength = 800;
	private static final int winHight = 430;
	private static final int workStart = 7;
	private static final int workEnd = 17;
	private static EDaoSchool selectedDao = EDaoSchool.FILE;
	private static ExamenVerwaltung launcher;
	private static ArrayList<FrameEdit> editFrames = new ArrayList<>();
	private static boolean console = false;
	private static Shell shell;
	private static DateFormat dateFormatGermany;

	private PanelQuestion panelQuestion;
	private PanelExam panelExam;
	private PanelAnswer panelAnswer;
	/////////////////////////////////////////////////////////////////////////////////////

	public static EDaoSchool getSelectedDao() {
		return selectedDao;
	}

	public static void setSelectedDao(EDaoSchool selectedDao) {
		ExamenVerwaltung.selectedDao = selectedDao;
		Solution.dataAccessObject = DaoSchoolAbstract.getDaoSchool(selectedDao);
		Question.dataAccessObject = DaoSchoolAbstract.getDaoSchool(selectedDao);
		Answer.dataAccessObject = DaoSchoolAbstract.getDaoSchool(selectedDao);
	}

	/////////////////////////////////////////////////////////////////////////////////////
	// Construct
	public static void main(String[] args) {
		console = false;
		if (getGermanDate() == null) {
			Locale locDE = new Locale(Locale.GERMANY.getCountry());
			setDateFormatGermany(DateFormat.getDateInstance(DateFormat.FULL, locDE));
		}
		launcher = new ExamenVerwaltung();
		// args = new String[] { "asasas", "54908" };
		String osname = System.getProperty("os.name");

		if (!(osname.regionMatches(true, 0, "Windows", 0, 7))) {
			System.out.println(
					"Was ist das denn für ein Dreck, wo ist meine Windows API hin?\nNa da darfst du jetzt schön tippen mein Freund, klicken is nicht.\r\n");
			console = true;
		} else if (args.length > 0) {
			if (args[0].equalsIgnoreCase("DEBUG")) {
				System.out.println("<Rebug-Mode>");
				console = true;
				launcher.setVisible(true);
			} else {
				console = true;
				String out = "";
				String add = "hat er";
				String it = "er steht";
				String param = "deinen";
				for (int index = 0; index < args.length; index++) {
					if (index != args.length - 1 && index > 0) {
						out += ", ";
						add = "haben sie";
						it = "sie stehen";
						param = "deine";
					} else if (index == args.length - 1) {
						out += " und ";
						add = "haben sie";
						it = "sie stehen";
						param = "deine";
					}
					out += args[index];
				}
				System.out.println("Vielen Dank für " + param + " Parameter " + out + ",\ngebracht " + add
						+ " dir nichts, aber dafür " + it + " jetzt da wo früher mal deine GUI gestanden hat.");
				System.out.println();
				launcher.setVisible(false);
			}
		} else {
			launcher.setVisible(!console);
		}
		if (console) {
			shell = new Shell();
			shell.Start(console);
			getInstance().closeConnections();
			System.exit(0);
		}
	}

	private void refresh() {
		panelQuestion.refresh();
		panelExam.refresh();
		panelAnswer.refresh();
	}

	private ExamenVerwaltung() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				ExamenVerwaltung.getInstance().closeConnections();
				System.exit(0);
			}
		});
		// Image icon = Toolkit.getDefaultToolkit().getImage("form.gif");
		setTitle("Schulverwaltung");
		setSize(winLength, winHight);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(null);
		addControls();
	}

	public static Date parseDate(String st) throws ParseException {
		return dateFormatGermany.parse(st);
	}

	public static String formatDate(Date date) throws ParseException {
		return dateFormatGermany.format(date);
	}

	@SuppressWarnings("static-access")
	public static void showErrorMessage(String s) {
		if (console) {
			getInstance().shell.showMessage("Error", s);
		} else {
			JOptionPane.showInternalMessageDialog(null, s, "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	@SuppressWarnings("static-access")
	public static String showInput(String s) {
		if (console) {
			return getInstance().shell.showInput(s);
		} else {
			return JOptionPane.showInternalInputDialog(null, s, "Information", JOptionPane.QUESTION_MESSAGE);
		}
	}

	@SuppressWarnings("static-access")
	public static void showMessage(String s) {
		if (console) {
			getInstance().shell.showMessage(s);
		} else {
			// JOptionPane.showMessageDialog(null, s, "Information",
			// JOptionPane.INFORMATION_MESSAGE);
			JOptionPane.showInternalMessageDialog(null, s, "Information", JOptionPane.INFORMATION_MESSAGE);
		}
	}

	@SuppressWarnings("static-access")
	public static void showException(Exception e) {
		if (console) {
			getInstance().shell.showMessage("Exception", e.getMessage(), e.getStackTrace().toString());
		} else {
			JOptionPane.showInternalMessageDialog(null, e.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
			System.out.println(e.getStackTrace());
		}
	}

	protected void closeConnections() {
		DaoSchoolAbstract.closeConnections();
	}

	private void addControls() {
		this.setLayout(new GridLayout(1, 1));

		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		ImageIcon icon = createImageIcon("exam.jpg");

		panelExam = new PanelExam();
		tabbedPane.addTab("Examen", icon, panelExam, "Examen verwalten");
		tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);

		icon = createImageIcon("question.png");
		panelQuestion = new PanelQuestion();
		tabbedPane.addTab("Fragen", icon,panelQuestion , "Fragen verwalten");
		tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);

		icon = createImageIcon("exclamation.png");
		panelAnswer = new PanelAnswer();
		tabbedPane.addTab("Antworten", icon, panelAnswer, "Antworten verwalten");
		panelAnswer.setPreferredSize(new Dimension(410, 50));
		tabbedPane.setMnemonicAt(2, KeyEvent.VK_3);

		tabbedPane.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				JTabbedPane source = (JTabbedPane) e.getSource();
				switch (source.getSelectedIndex()) {
				case 0:
					panelQuestion.refresh();
					break;
				case 1:
					panelExam.refresh();
					break;
				case 2:
					panelAnswer.refresh();
					break;

				}
			}
		});
		add(tabbedPane);
		tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		panelQuestion.refresh();
	}
	/////////////////////////////////////////////////////////////////////////////////////

	/////////////////////////////////////////////////////////////////////////////////////
	// Static for Panels, etc.
	/** Returns an ImageIcon, or null if the path was invalid. */
	protected static ImageIcon createImageIcon(String path) {
		java.net.URL imgURL = ExamenVerwaltung.class.getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL);
		} else {
			System.err.println("Datei nicht gefunden: " + path);
			return null;
		}
	}

	public static JButton getButton(String buName, int x, int y, int width, int height, ActionListener linr,
			String display, String tooltip) {
		JButton button = new JButton(buName);
		button.setName(buName);
		if (display.length() > 0) {
			button.setText(display);
		}
		if (tooltip.length() > 0) {
			button.setToolTipText(tooltip);
		}
		button.setBounds(x, y, width, height);
		button.addActionListener(linr);
		return button;
	}

	public static Integer getWorkStart() {
		return workStart;
	}

	public static Integer getWorkEnd() {
		return workEnd;
	}

	public static DateFormat getGermanDate() {
		return dateFormatGermany;
	}

	public static void setDateFormatGermany(DateFormat dateFormatGermany) {
		ExamenVerwaltung.dateFormatGermany = dateFormatGermany;
	}

	public static void toggleFrame() {
		launcher.setVisible(!launcher.isVisible());
		launcher.refresh();
	}

	public static ExamenVerwaltung getInstance() {
		return launcher;
	}

	public static void reset() {
		ExamItemAbstract.highestMemberId = 1000;
		Solution.reset();
		Question.reset();
		Answer.reset();
	}
	/////////////////////////////////////////////////////////////////////////////////////

	/////////////////////////////////////////////////////////////////////////////////////
	// Class factories returning interfaces
	public static ArrayList<ISolution> getSolutionList() {
		return Solution.getCourses();
	}

	public static ArrayList<IQuestion> getQuestionList() {
		return Question.getTeachers();
	}

	public static ArrayList<IAnswer> getStudentList() {
		return Answer.getStudents();
	}

	public static ArrayList<ISolution> getCourses(IQuestion teacher) {
		ArrayList<ISolution> ret = new ArrayList<>();
		for (ISolution c : getSolutionList()) {
			if (c.hasQuestion()) {
				if (c.getQuestion().equals(teacher)) {
					ret.add(c);
				}
			}
		}
		return ret;
	}

	public static ISolution getNewSolution(boolean random) {
		return Solution.createSolution(true, selectedDao);
	}

	public static ISolution getNewSolution(String name) {
		return Solution.createSolution(name, selectedDao);
	}

	public static IQuestion getNewQuestion(boolean random) {
		return Question.createQuestion(random, selectedDao);
	}

	public static IQuestion getNewQuestion(String name) {
		return Question.createQuestion(name, selectedDao);
	}

	public static IAnswer getNewStudent(boolean random) {
		return Answer.createStudent(random, selectedDao);
	}

	public static IAnswer getNewStudent(String name) {
		return Answer.createStudent(name, selectedDao);
	}

	/***
	 * Updates the data on the object from the data source, if possible
	 * 
	 * @param schoolItemAbstract
	 */
	public static void verifyData(ExamItemAbstract schoolItemAbstract) {
		DaoSchoolAbstract.getDaoSchool(getSelectedDao()).loadElement(schoolItemAbstract);
	}

	public static void saveItem(ExamItemAbstract schoolItemAbstract) {
		DaoSchoolAbstract.getDaoSchool(getSelectedDao()).saveElement(schoolItemAbstract);
	}

	public void editItem(ExamItemAbstract editItem) {
		FrameEdit edit = null;
		boolean found = false;
		if (editItem.isInEdit()) {
			for (FrameEdit edited : editFrames) {
				if (edited.hastItem(editItem)) {
					edited.setVisible(true);
					edited.toFront();
					edited.repaint();
					found = true;
					System.out.println("reactivate");
				}
			}
		}
		if (!found) {
			if (editItem.getClass() == Question.class) {
				edit = new FrameEdit((IQuestion) editItem);
			} else if (editItem.getClass() == Answer.class) {
				edit = new FrameEdit((IAnswer) editItem);
			} else if (editItem.getClass() == Solution.class) {
				edit = new FrameEdit((ISolution) editItem);
			}
		}
		if (edit != null) {
			editItem.setInEdit(true);

			edit.addWindowListener(ExamenVerwaltung.getInstance());
			System.out.println("opened");
			editFrames.add(edit);
		}
	}

	public static boolean getShell() {
		return console;
	}

	public static boolean deleteElement(ExamItemAbstract loadItem) {
		boolean ret = DaoSchoolAbstract.getDaoSchool(selectedDao).deleteElement(loadItem);
		if (ret) {
			if (loadItem instanceof ISolution) {
				Solution.courseDeleted((ISolution) loadItem);
			} else if (loadItem instanceof IQuestion) {
				Question.teacherDeleted(loadItem);
				Solution.teacherDeleted((IQuestion) loadItem);
			} else if (loadItem instanceof IAnswer) {
				Answer.answerDeleted((IAnswer) loadItem);
				Solution.studentDeleted((IAnswer) loadItem);
			} 
		}
		return ret;
	}

	@Override
	public void windowActivated(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowClosed(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowClosing(WindowEvent arg0) {
		FrameEdit edit = (FrameEdit) arg0.getSource();
		if (editFrames.contains(edit)) {
			System.out.println("destory:" + edit.toString());
			editFrames.remove(edit);
			edit = null;
		}
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowIconified(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowOpened(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}
	/////////////////////////////////////////////////////////////////////////////////////
}