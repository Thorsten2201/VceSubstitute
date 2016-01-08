package de.bbq.java.tasks.vce;

import java.awt.Frame;

/**
 * @author Thorsten2201
 *
 */
public abstract class DaoSchoolJdbcAbstract extends DaoSchoolAbstract {

	/////////////////////////////////////////////////////////////////////////////////////
	// Construct
	protected DaoSchoolJdbcAbstract(EDaoSchool eDao) {
		super(eDao);
	}

	/////////////////////////////////////////////////////////////////////////////////////
	public abstract boolean connect(Frame parent, String database, String username, String password);

	public abstract boolean closeConnection();
}
