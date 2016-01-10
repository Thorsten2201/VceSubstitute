package de.bbq.java.tasks.vce;

import java.util.ArrayList;

public interface IExam {
	String toString();

	boolean hasQuestion(IQuestion question);

	int getQuestionCount();

	String getDescription();

	void addQuestion(IQuestion q);
	
	void removeQuestion(IQuestion q);

	ArrayList<IQuestion> getQuestions();

	boolean hasQuestions();
}
