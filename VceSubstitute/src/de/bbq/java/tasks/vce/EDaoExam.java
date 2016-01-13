package de.bbq.java.tasks.vce;

/**
 * @author Thorsten2201
 *
 */
public enum EDaoExam {
	ABSTACT(0), FILE(1), JDBC(2), JDBC_MYSQL(3),DERBY(4);

	/////////////////////////////////////////////////////////////////////////////////////
	// Class Properties
	int enumId = 0;
	/////////////////////////////////////////////////////////////////////////////////////

	public String toString() {
		switch (this.enumId) {
		case 0:
			return "Abstrakt";
		case 1:
			return "Dateisystem";
		case 2:
			return "Jdbc abstrakt";
		case 3:
			return "Jdbc MySql";
		case 4:
			return "Derby";	
		default:
			return "Nicht existent";
		}
	}

	/////////////////////////////////////////////////////////////////////////////////////
	// Construct
	EDaoExam(int enumId) {
		this.enumId = enumId;
	};
	/////////////////////////////////////////////////////////////////////////////////////

}
