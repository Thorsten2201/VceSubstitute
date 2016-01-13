package de.bbq.java.tasks.vce;

import java.util.ArrayList;

public class Exam extends ExamItemAbstract implements IExam {
	/////////////////////////////////////////////////////////////////////////////////////
	// Class Properties
	private static ArrayList<IExam> allExams = new ArrayList<>();
	/////////////////////////////////////////////////////////////////////////////////////

	/////////////////////////////////////////////////////////////////////////////////////
	// Properties to serialize
	private String name;
	private String description;
	private String language;
	private ArrayList<IQuestion> questions = new ArrayList<>();
	/////////////////////////////////////////////////////////////////////////////////////

	/////////////////////////////////////////////////////////////////////////////////////
	// Construct
	private Exam(String name, EDaoExam eDataAccess) throws Exception {
		super(eDataAccess);
		this.name = name;
	}
	/////////////////////////////////////////////////////////////////////////////////////

	/////////////////////////////////////////////////////////////////////////////////////
	// Static
	private static final long serialVersionUID = -8838146635169751075L;
	private static ArrayList<IAnswer> allStudents = new ArrayList<>();

	public static boolean load(Exam exam) {
		exam.id = ExamItemAbstract.getNewId();
		allExams.add(exam);
		return true;
	}

	private static String generateNewName() {
		String[] array = new String[] { "Java I", "Java II", "Java WCD", "Java BCD", "HTML 5", "CSS 3", "php 5",
				"Javascript", "Python" };
		int randomNum = 0 + (int) (Math.random() * array.length);
		return array[randomNum];
	}

	public static Exam createExam(String name, EDaoExam eDataAccess) {
		Exam exam = null;
		try {
			exam = new Exam(name, eDataAccess);
			allExams.add(exam);
		} catch (Exception e) {
			ExamenVerwaltung.showException(e);
		}
		return exam;
	}

	public static Exam createExam(boolean random, EDaoExam eDataAccess) {
		String newName = Exam.generateNewName();
		Exam newExam = null;
		if (!random) {
			newName = ExamenVerwaltung.showInput("please.enter.name");
		}
		newExam = Exam.createExam(newName, eDataAccess);
		return newExam;
	}

	/////////////////////////////////////////////////////////////////////////////////////
	// Methods
	public static ArrayList<IExam> getExams() {
		return allExams;
	}

	public static void examDeleted(IExam exam) {
		if (allExams.contains(exam)) {
			allExams.remove(exam);
		}
	}

	public static void reset() {
		allExams = new ArrayList<>();
	}

	@Override
	public int getQuestionCount() {
		return allExams.size();
	}
	/////////////////////////////////////////////////////////////////////////////////////

	/////////////////////////////////////////////////////////////////////////////////////
	// Getter / Setter IExam
	@Override
	public String getDescription() {
		return this.description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	@Override
	public String toString() {
		return ExamenVerwaltung.getText("Exam") + " " + name;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public ArrayList<IQuestion> getQuestions() {
		return questions;
	}

	@Override
	public boolean hasQuestion(IQuestion question) {
		for (IQuestion q : Question.getQuestions()) {
			if (q.hasExam()) {
				if (q.equals(question)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean hasQuestions() {
		return this.getQuestionCount() > 0;
	}

	@Override
	public void removeQuestion(IQuestion q) {
		if (this.getQuestions().contains(q)) {
			this.getQuestions().remove(q);
		}
	}

	@Override
	public void addQuestion(IQuestion q) {
		this.questions.add(q);
	}
	/////////////////////////////////////////////////////////////////////////////////////

}
