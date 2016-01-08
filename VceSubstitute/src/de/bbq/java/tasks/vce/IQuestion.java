package de.bbq.java.tasks.vce;

/**
 * @author Thorsten2201
 *
 */
public interface IQuestion { 
	String toString();

	void addSolution(ISolution exam) throws Exception;

	void deleteSolution(ISolution exam);

	int getSolutionCount();

	String getDescription();
}
