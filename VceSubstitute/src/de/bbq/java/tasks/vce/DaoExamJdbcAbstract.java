package de.bbq.java.tasks.vce;

import java.awt.Frame;

/**
 * @author Thorsten2201
 *
 */
public abstract class DaoExamJdbcAbstract extends DaoExamAbstract {

	/////////////////////////////////////////////////////////////////////////////////////
	// Construct
	protected DaoExamJdbcAbstract(EDaoExam eDao) {
		super(eDao);
	}

	/////////////////////////////////////////////////////////////////////////////////////
	public abstract boolean connect(Frame parent, String database, String username, String password);

	public abstract boolean closeConnection();
}
