package de.bbq.java.tasks.vce;

import java.util.ArrayList;

/**
 * @author teilnehmer222
 *
 */
public class Question extends ExamItemAbstract implements IQuestion {
	/////////////////////////////////////////////////////////////////////////////////////
	// Class Properties
	private transient ArrayList<ISolution> solutions = new ArrayList<>();
	/////////////////////////////////////////////////////////////////////////////////////

	/////////////////////////////////////////////////////////////////////////////////////
	// Construct
	private Question(String name, EDaoSchool eDataAccess) throws Exception {
		super(eDataAccess);
		super.setName(name);
	}
	/////////////////////////////////////////////////////////////////////////////////////

	/////////////////////////////////////////////////////////////////////////////////////
	// Static
	private static final long serialVersionUID = -3548796163205043453L;
	private static ArrayList<IQuestion> allTeachers = new ArrayList<>();

	public static boolean load(Question teacher) {
		allTeachers.add(teacher);
		teacher.id = ExamItemAbstract.getNewId();
		return true;
	}

	private static String generateNewName() {
		String[] array = new String[] { "Geistig Abwesender", "Laubbläsleer", "Labersack", "Zutexter", "Volllaberer",
				"Berieseler", "Hintergrundrauschen", "Verstörendes Geräusch", "Dildogesicht", "Halodri",
				"Birkenstockdepp", "Fotzenkopf", "Hirschfresse", "Althippy", "Schnarchnase" };
		int randomNum = 0 + (int) (Math.random() * array.length);
		return array[randomNum];
	}

	public static Question createQuestion(String firstName, EDaoSchool eDataAccess) {
		Question teacher = null;
		try {
			teacher = new Question(firstName, eDataAccess);
			allTeachers.add(teacher);
		} catch (Exception e) {
			ExamenVerwaltung.showException(e);
		}
		return teacher;
	}

	public static IQuestion createQuestion(boolean random, EDaoSchool eDataAccess) {
		String newName = Question.generateNewName();
		Question newTeacher = null;
		if (!random) {
			newName = ExamenVerwaltung.showInput("Bitte einen Namen eingeben:");
		}
		newTeacher = Question.createQuestion(newName, eDataAccess);
		return newTeacher;
	}

	public static ArrayList<IQuestion> getTeachers() {
		return allTeachers;
	}

	public static void teacherDeleted(ExamItemAbstract editItem) {
		if (allTeachers.contains(editItem)) {
			allTeachers.remove(editItem);
		}
	}

	private static int getCourseCount(Question teacher) {
		int cnt = 0;
		for (ISolution c : Solution.getCourses()) {
			if (c.hasQuestion()) {
				if (c.getQuestion().equals(teacher)) {
					cnt++;
				}
			}
		}
		return cnt;
	}

	public static void reset() {
		allTeachers = new ArrayList<>();
	}
	/////////////////////////////////////////////////////////////////////////////////////

	/////////////////////////////////////////////////////////////////////////////////////
	// Getter / Setter ITeacher
	@Override
	public void addSolution(ISolution course) {
		this.getCourses().add(course);
		course.setQuestion(this);
	}

	private ArrayList<ISolution> getCourses() {
		if (this.solutions == null) {
			this.solutions = new ArrayList<>();
		}
		return solutions;
	}

	@Override
	public void deleteSolution(ISolution course) {
		course.removeQuestion();
		for (ISolution courses : this.getCourses()) {
			if (courses.equals(course)) {
				this.getCourses().remove(course);
				break;
			}

		}
	}

	@Override
	public String getDescription() {
		StringBuffer bu = new StringBuffer();
		bu.append(this.getName() + "\n");
//		bu.append(this.getAdress().getDescription());
		return bu.toString();
	}

	@Override
	public String toString() {
		return super.getName();
	}

	@Override
	public int getSolutionCount() {
		return Question.getCourseCount(this);
	}
	/////////////////////////////////////////////////////////////////////////////////////

}
