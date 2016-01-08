package de.bbq.java.tasks.vce;

import java.util.ArrayList;
import java.util.Date;

/**
 * @author teinlehmer222
 *
 */
public class Solution extends ExamItemAbstract implements ISolution {

	/////////////////////////////////////////////////////////////////////////////////////
	// Class Properties
	private IQuestion question;
	private ArrayList<IAnswer> answers = new ArrayList<>();
	/////////////////////////////////////////////////////////////////////////////////////

	/////////////////////////////////////////////////////////////////////////////////////
	// Properties to serialize
	private String examName;
	private int number;
//	private String topic;
//	private Date endTime;
//	private Date startTime;
//	private String roomNumber;
//	private Boolean needsBeamer = false;
	private String language;
	/////////////////////////////////////////////////////////////////////////////////////

	/////////////////////////////////////////////////////////////////////////////////////
	// Construct
	private Solution(String examName, EDaoSchool eDataAccess) throws Exception {
		super(eDataAccess);
		this.examName = examName;
		allSolutions.add(this);
	}
	/////////////////////////////////////////////////////////////////////////////////////

	/////////////////////////////////////////////////////////////////////////////////////
	// Static
	private static final long serialVersionUID = -4457277057001458163L;
	private static ArrayList<ISolution> allSolutions = new ArrayList<>();

	private static String generateNewName() {
		String[] names = new String[] { "Schlafuntericht", "Nichtstun 2.0", "Däumchendrehen", "Aus dem Fenster schauen",
				"Stinken für Dummies", "Wie saue ich das Klo voll", "Waschbecken verstopfen", "Feueralarm drücken",
				"Aufzug blockieren", "Türen verkeilen", "Kernschmelze leichtgemacht", "Deppenradar",
				"Mülleimer vollkotzen", "Beamer sabotieren", "Ans Fenster rotzen", "Kaffee Verschütten",
				"Professionelles Furzen" };
		int randomNum = 0 + (int) (Math.random() * names.length);
		return names[randomNum];
	}

	public static ISolution createSolution(String examName, EDaoSchool store) {
		Solution solution = null;
		try {
			solution = new Solution(examName, store);
		} catch (Exception e) {
			ExamenVerwaltung.showException(e);
		}
		return solution;
	};

	public static boolean load(Solution solution) {
		allSolutions.add(solution);
		solution.id = ExamItemAbstract.getNewId();
		return true;
	}

	public static ISolution createSolution(boolean random, EDaoSchool store) {
		String newName = Solution.generateNewName();
		ISolution newCourse = null;
		if (!random) {
			newName = ExamenVerwaltung.showInput("Bitte einen Kursnamen eingeben:");
		}
		newCourse = Solution.createSolution(newName, store);
		return newCourse;
	}

	public static ArrayList<ISolution> getCourses() {
		return allSolutions;
	}

	public static void courseDeleted(ISolution course) {
		allSolutions.remove(course);
	}

	public static void studentDeleted(IAnswer student) {
		for (ISolution c : allSolutions) {
			if (c.hasAnswers()) {
				// Exception in thread "AWT-EventQueue-0"
				// java.util.ConcurrentModificationException
				for (int index = 0; index < c.getAnswers().size(); index++) {
					IAnswer s = c.getAnswers().get(index);
					if (s != null) {
						if (s.equals(student)) {
							c.removeAnswer(s);
						}
					}
				}
			}
		}
	}

	public static void teacherDeleted(IQuestion teacher) {
		for (ISolution c : allSolutions) {
			if (c.getQuestion() != null) {
				if (c.getQuestion().equals(teacher)) {
					c.removeQuestion();
				}
			}
		}
	}
	/////////////////////////////////////////////////////////////////////////////////////

	/////////////////////////////////////////////////////////////////////////////////////
	// Getter / Setter ICourse
	@Override
	public String toString() {
		return this.examName;
	}

	@Override
	public IQuestion getQuestion() {
		return this.question;
	}

	@Override
	public void setQuestion(IQuestion t) {
		this.question = t;
	}

	@Override
	public void removeQuestion() {
		this.question = null;
	}

	@Override
	public boolean hasQuestion() {
		return (this.getQuestion() != null);
	}

	@Override
	public ArrayList<IAnswer> getAnswers() {
		if (this.answers == null) {
			this.answers = new ArrayList<>();
		}
		return this.answers;
	}

	@Override
	public void addAnswer(IAnswer student) {
		if (student.hasQuestion()) {
			ISolution oldCourse = student.getSolution();
			oldCourse.removeAnswer(student);
		}
		this.getAnswers().add(student);
		// student.setCourse(this);
	}

	@Override
	public void removeAnswer(IAnswer student) {
		if (this.getAnswers().contains(student)) {
			this.getAnswers().remove(student);
		}
		// student.removeCourse();
	}

	@Override
	public boolean hasAnswers() {
		return (this.getAnswers().size() > 0);
	}

	@Override
	public boolean hasAnswer(IAnswer student) {
		return this.getAnswers().contains(student);
	}
	/////////////////////////////////////////////////////////////////////////////////////

	/////////////////////////////////////////////////////////////////////////////////////
	// Getter / Setter Properties
	public String getCourseName() {
		return this.examName;
	}

	public void setCourseName(String courseName) {
		this.examName = courseName;
	}

	public String getLanguage() {
		return this.language;
	}

	public void setStudents(ArrayList<IAnswer> students) {
		this.answers = students;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	@Override
	public String getDescription() {
		StringBuffer bu = new StringBuffer();
		bu.append(this.getCourseName() + "\n");
		bu.append("Fach: " + ((this.getName() == null) ? "" : this.getName()) + "\n");
		bu.append("Sprache: " + ((this.getLanguage() == null) ? "" : this.getLanguage()) + "\n");
		return bu.toString();
	}
	/////////////////////////////////////////////////////////////////////////////////////

	public static void reset() {
		allSolutions = new ArrayList<>();
	}

}
