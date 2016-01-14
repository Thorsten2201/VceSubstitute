package de.bbq.java.tasks.vce;

import java.awt.Image;
import java.util.ArrayList;

/**
 * @author teilnehmer222
 *
 */
public class Answer extends ExamItemAbstract implements IAnswer {
	/////////////////////////////////////////////////////////////////////////////////////
	// Class Properties
	// private transient IExam exam;
	/////////////////////////////////////////////////////////////////////////////////////

	/////////////////////////////////////////////////////////////////////////////////////
	// Properties to serialize
	private char index;
	private String answerText;
	private boolean isTrue;
	private int position;
	/////////////////////////////////////////////////////////////////////////////////////
	
	/////////////////////////////////////////////////////////////////////////////////////
	// Construct
	private Answer(String name, EDaoExam eDataAccess) throws Exception {
		super(eDataAccess);
		super.setName(name);
	}
	/////////////////////////////////////////////////////////////////////////////////////

	/////////////////////////////////////////////////////////////////////////////////////
	// Static
	private static final long serialVersionUID = -8838146635169751075L;
	private static ArrayList<IAnswer> allAnswers = new ArrayList<>();

	public static boolean load(Answer answer) {
		answer.id = ExamItemAbstract.getNewId();
		allAnswers.add(answer);
		return true;
	}

	private static String generateNewName() {
		String[] array = new String[] { "Depp", "Trottel", "Idiot", "Armleuchter", "Hirni", "Totalversager",
				"Baumschulabbrecher", "Volldepp", "Volltrottel", "Extremdepp", "Superidiot", "Dummbeutel",
				"Trottelkopf", "Hirngesicht", "Bauer", "Viehbauer", "Behindikind" };
		int randomNum = 0 + (int) (Math.random() * array.length);
		return ExamenVerwaltung.getText("Answer") + " " + (allAnswers.size() + 1); // array[randomNum];
	}

	public static Answer createAnswer(String name, EDaoExam eDataAccess) {
		Answer answer = null;
		try {
			answer = new Answer(name, eDataAccess);
			allAnswers.add(answer);
		} catch (Exception e) {
			ExamenVerwaltung.showException(e);
		}
		return answer;
	}

	public static Answer createAnwer(boolean random, EDaoExam eDataAccess) {
		String newName = Answer.generateNewName();
		Answer newAnswer = null;
		if (!random) {
			newName = ExamenVerwaltung.showInput("please.enter.name");
		}
		newAnswer = Answer.createAnswer(newName, eDataAccess);
		return newAnswer;
	}

	public static ArrayList<IAnswer> getAnswers() {
		return allAnswers;
	}

	public static void answerDeleted(IAnswer answer) {
		if (allAnswers.contains(answer)) {
			allAnswers.remove(answer);
		}
	}

	public static void reset() {
		allAnswers = new ArrayList<>();
	}
	/////////////////////////////////////////////////////////////////////////////////////

	/////////////////////////////////////////////////////////////////////////////////////
	// Getter / Setter IStudent
	@Override
	public IQuestion getQuestion() {
		for (IQuestion q : Question.getQuestions()) {
			if (q.hasAnswer(this)) {
				return q;
			}
		}
		return null;
	}

	@Override
	public String getDescription() {
		StringBuffer bu = new StringBuffer();
		if (this.hasQuestion()) {
			bu.append(index + " " + this.getQuestion().getQuestionName());
		} else {
			bu.append(index + " " + this.getName().toString());
		}
		return bu.toString();
	}


	@Override
	public String toString() {
		return super.getName();
	}
	
	@Override
	public boolean hasQuestion() {
		return this.getQuestion() != null;
	}

	@Override
	public boolean hasQuestion(IQuestion question) {
		if (question != null) {
			return question.equals(this.getQuestion());
		} else {
			return false;
		}
		
	}
	
	public String getIndex() {
		return String.valueOf(index);
	}

	public void setIndex(char index) {
		this.index = index;
	}

	public String getAnswerText() {
		return answerText;
	}

	public void setAnswerText(String answerText) {
		this.answerText = answerText;
	}

	public boolean isTrue() {
		return isTrue;
	}

	public void setTrue(boolean isTrue) {
		this.isTrue = isTrue;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}
	/////////////////////////////////////////////////////////////////////////////////////
}
