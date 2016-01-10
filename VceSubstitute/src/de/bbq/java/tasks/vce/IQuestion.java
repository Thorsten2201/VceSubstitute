package de.bbq.java.tasks.vce;

import java.util.ArrayList;

/**
 * @author Thorsten2201
 *
 */
public interface IQuestion { 
	String toString();
	
	String getQuestionName();

	void addQuestion(IQuestion question);

	void deleteQuestion(IQuestion question);

	int getQuestionCount();

	String getDescription();
	
	boolean hasAnswers();

	IExam getExam();

	boolean hasExam();

	ArrayList<IAnswer> getAnswers();

	void addAnswer(IAnswer answer);

	void removeAnswer(IAnswer answer);

	boolean hasAnswer(IAnswer answer);
}
