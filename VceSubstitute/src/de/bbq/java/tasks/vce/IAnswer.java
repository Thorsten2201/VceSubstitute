package de.bbq.java.tasks.vce;

/**
 * @author Thorsten2201
 *
 */
public interface IAnswer { // extends IDaoSchool {
	String toString();

	ISolution getSolution();

	boolean hasQuestion();

	boolean hasQuestion(IQuestion question);

	boolean hasSolution(ISolution solution);
		
	// void removeCourse();
	String getDescription();
}
