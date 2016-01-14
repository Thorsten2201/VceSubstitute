package de.bbq.java.tasks.vce;

import java.awt.Image;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * @author teilnehmer222
 *
 */
public class Question extends ExamItemAbstract implements IQuestion {
	/////////////////////////////////////////////////////////////////////////////////////
	// Class Properties
	private ArrayList<IAnswer> answers = new ArrayList<>();
	private ArrayList<String> categories = new ArrayList<>();
	/////////////////////////////////////////////////////////////////////////////////////

	/////////////////////////////////////////////////////////////////////////////////////
	// Properties to serialize
	private int number;
	private String language;
	private String questionText;
	private Image questionImage;
	private String questionFooter;
	private String answerExplanation;
	private int imageLine;
	/////////////////////////////////////////////////////////////////////////////////////

	/////////////////////////////////////////////////////////////////////////////////////
	// Construct
	private Question(String name, EDaoExam eDataAccess) throws Exception {
		super(eDataAccess);
		super.setName(name);
		this.number = allQuestions.size() + 1;
	}
	/////////////////////////////////////////////////////////////////////////////////////

	/////////////////////////////////////////////////////////////////////////////////////
	// Static
	private static final long serialVersionUID = -3548796163205043453L;
	private static ArrayList<IQuestion> allQuestions = new ArrayList<>();

	public static boolean load(Question question) {
		allQuestions.add(question);
		question.id = ExamItemAbstract.getNewId();
		return true;
	}

	private static String generateNewName() {
		String[] array = new String[] { "Geistig Abwesender", "Laubbläsleer", "Labersack", "Zutexter", "Volllaberer",
				"Berieseler", "Hintergrundrauschen", "Verstörendes Geräusch", "Dildogesicht", "Halodri",
				"Birkenstockdepp", "Fotzenkopf", "Hirschfresse", "Althippy", "Schnarchnase" };
		@SuppressWarnings("unused")
		int randomNum = 0 + (int) (Math.random() * array.length);
		return ExamenVerwaltung.getText("Question") + " " + (allQuestions.size() + 1);// :array[randomNum];
	}

	public static Question createQuestion(String firstName, EDaoExam eDataAccess) {
		Question teacher = null;
		try {
			teacher = new Question(firstName, eDataAccess);
			allQuestions.add(teacher);
		} catch (Exception e) {
			ExamenVerwaltung.showException(e);
		}
		return teacher;
	}

	public static IQuestion createQuestion(boolean random, EDaoExam eDataAccess) {
		String newName = Question.generateNewName();
		Question newTeacher = null;
		if (!random) {
			newName = ExamenVerwaltung.showInput("please.enter.name");
		}
		newTeacher = Question.createQuestion(newName, eDataAccess);
		return newTeacher;
	}

	public static ArrayList<IQuestion> getQuestions() {
		return allQuestions;
	}

	public static void questionDeleted(ExamItemAbstract editItem) {
		if (allQuestions.contains(editItem)) {
			allQuestions.remove(editItem);
		}
	}

	private static int getQuestionCount(Question question) {
		int cnt = 0;
		for (IExam e : Exam.getExams()) {
			for (IQuestion q : e.getQuestions()) {
				if (q.equals(question)) {
					cnt++;
				}
			}
		}
		return cnt;
	}

	public static void reset() {
		allQuestions = new ArrayList<>();
	}
	/////////////////////////////////////////////////////////////////////////////////////

	private static ArrayList<IQuestion> getAllQuestions() {
		if (allQuestions == null) {
			allQuestions = new ArrayList<>();
		}
		return allQuestions;
	}

	@Override
	public void deleteQuestion(IQuestion question) {
		question.deleteQuestion(question);
		for (IQuestion q : getAllQuestions()) {
			if (q.equals(question)) {
				getAllQuestions().remove(question);
				break;
			}

		}
	}

	@Override
	public boolean hasAnswers() {
		return this.getAnswers().size() > 0;
	}

	@Override
	public boolean hasExam() {
		for (IExam e : ExamenVerwaltung.getExamList()) {
			if (e.getQuestions().contains(this)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public ArrayList<IAnswer> getAnswers() {
		return answers;
	}

	@Override
	public void addAnswer(IAnswer answer) {
		answers.add(answer);
	}

	@Override
	public void removeAnswer(IAnswer answer) {
		if (answers.contains(answer)) {
			answers.remove(answer);
		}
	}

	@Override
	public boolean hasAnswer(IAnswer answer) {
		if (answers.contains(answer)) {
			return true;
		}
		return false;
	}

	@Override
	public IExam getExam() {
		for (IExam e : ExamenVerwaltung.getExamList()) {
			if (e.getQuestions().contains(this)) {
				return e;
			}
		}
		return null;
	}

	@Override
	public void addQuestion(IQuestion question) {
		getAllQuestions().add(question);
	}

	@Override
	public String getDescription() {
		StringBuffer bu = new StringBuffer();
		bu.append(this.getName() + "\n");
		// bu.append(this.getAdress().getDescription());
		return bu.toString();
	}

	@Override
	public String toString() {
		return super.getName();
	}

	public String getQuestionName() {
		return super.getName();
	}

	public void setQuestionName(String questionName) {
		this.setName(questionName);
	}

	@Override
	public int getQuestionCount() {
		return Question.getQuestionCount(this);
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getQuestionText() {
		return questionText;
	}

	public void setQuestionText(String questionText) {
		this.questionText = questionText;
	}

	public Image getQuestionImage() {
		return questionImage;
	}

	public void setQuestionImage(Image questionImage) {
		this.questionImage = questionImage;
	}

	public String getQuestionFooter() {
		return questionFooter;
	}

	public void setQuestionFooter(String questionFooter) {
		this.questionFooter = questionFooter;
	}

	public String getAnswerExplanation() {
		return answerExplanation;
	}

	public void setAnswerExpailnation(String answerExpailnation) {
		this.answerExplanation = answerExpailnation;
	}

	public int getImageLine() {
		return imageLine;
	}

	public void setImageLine(int imageLine) {
		this.imageLine = imageLine;
	}

	public ArrayList<String> getCategories() {
		return categories;
	}

	public void setCategories(ArrayList<String> categories) {
		this.categories = categories;
	}
	
	public InputStream getImageStream() {
		// TODO
//		File blob = new File("/path/to/picture.png");
//		FileInputStream in = new FileInputStream(blob);
		return null;
	}
	/////////////////////////////////////////////////////////////////////////////////////
}
