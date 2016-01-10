package de.bbq.java.tasks.vce;

/**
 * @author Thorsten2201
 *
 */
public interface IAnswer { // extends IDaoSchool {
	String toString();

	IQuestion getQuestion();

	boolean hasQuestion();

	boolean hasQuestion(IQuestion question);
		
	// void removeCourse();
	String getDescription();
}
