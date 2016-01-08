package de.bbq.java.tasks.vce;

import java.util.ArrayList;

/**
 * @author teilnehmer222
 *
 */
public class Answer extends ExamItemAbstract implements IAnswer {
	/////////////////////////////////////////////////////////////////////////////////////
	// Class Properties
	// private transient ICourse course;
	/////////////////////////////////////////////////////////////////////////////////////

	/////////////////////////////////////////////////////////////////////////////////////
	// Construct
	private Answer(String firstName, EDaoSchool eDataAccess) throws Exception {
		super(eDataAccess);
		//super.setFirstName(firstName);
	}
	/////////////////////////////////////////////////////////////////////////////////////

	/////////////////////////////////////////////////////////////////////////////////////
	// Static
	private static final long serialVersionUID = -8838146635169751075L;
	private static ArrayList<IAnswer> allStudents = new ArrayList<>();

	public static boolean load(Answer answer) {
		answer.id = ExamItemAbstract.getNewId();
		allStudents.add(answer);
		return true;
	}

	private static String generateNewName() {
		String[] array = new String[] { "Depp", "Trottel", "Idiot", "Armleuchter", "Hirni", "Totalversager",
				"Baumschulabbrecher", "Volldepp", "Volltrottel", "Extremdepp", "Superidiot", "Dummbeutel",
				"Trottelkopf", "Hirngesicht", "Bauer", "Viehbauer", "Behindikind" };
		int randomNum = 0 + (int) (Math.random() * array.length);
		return array[randomNum];
	}

	public static Answer createStudent(String firstName, EDaoSchool eDataAccess) {
		Answer answer = null;
		try {
			answer = new Answer(firstName, eDataAccess);
			allStudents.add(answer);
		} catch (Exception e) {
			ExamenVerwaltung.showException(e);
		}
		return answer;
	}

	public static Answer createStudent(boolean random, EDaoSchool eDataAccess) {
		String newName = Answer.generateNewName();
		Answer newStudent = null;
		if (!random) {
			newName = ExamenVerwaltung.showInput("Bitte einen Namen eingeben:");
		}
		newStudent = Answer.createStudent(newName, eDataAccess);
		return newStudent;
	}

	public static ArrayList<IAnswer> getStudents() {
		return allStudents;
	}

	public static void answerDeleted(IAnswer answer) {
		if (allStudents.contains(answer)) {
			allStudents.remove(answer);
		}
	}

	public static void reset() {
		allStudents = new ArrayList<>();
	}
	/////////////////////////////////////////////////////////////////////////////////////

	/////////////////////////////////////////////////////////////////////////////////////
	// Getter / Setter IStudent
	@Override
	public ISolution getSolution() {
		for (ISolution c : Solution.getCourses()) {
			if (c.hasAnswer(this)) {
				return c;
			}
		}
		return null;
	}

	@Override
	public String getDescription() {
		StringBuffer bu = new StringBuffer();
//TODO:		bu.append(this.getFirstName() + " " + getLastName() + "\n");
//		if (this.getBirthDate() != null) {
//			bu.append(ExamenVerwaltung.getGermanDate().format(this.getBirthDate()) + "\n");
//		}
//		bu.append(this.getAdress().getDescription());
		return bu.toString();
	}
	// @Override
	// public void setCourse(ICourse course) {
	// this.course = course;
	// }
	//
	// @Override
	// public void removeCourse() {
	// this.course = null;
	// }

	@Override
	public boolean hasSolution(ISolution course) {
		if (course != null) {
			return course.equals(this.getSolution());
		} else {
			return false;
		}
	}

	@Override
	public boolean hasQuestion() {
		return this.getSolution() != null;
	}
	/////////////////////////////////////////////////////////////////////////////////////

	@Override
	public boolean hasQuestion(IQuestion question) {
		if (question != null) {
			return question.equals(this.getSolution());
		} else {
			return false;
		}
		
	}
}
