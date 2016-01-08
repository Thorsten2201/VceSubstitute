package de.bbq.java.tasks.vce;

import java.util.ArrayList;

/**
 * @author Thorsten2201
 *
 */
public interface ISolution { // extends IDaoSchool {
	boolean hasAnswers();

	String toString();

	IQuestion getQuestion();

	void setQuestion(IQuestion q);

	void removeQuestion();

	boolean hasQuestion();

	ArrayList<IAnswer> getAnswers();

	void addAnswer(IAnswer answer);

	void removeAnswer(IAnswer answer);

	boolean hasAnswer(IAnswer answer);

	String getDescription();
}
