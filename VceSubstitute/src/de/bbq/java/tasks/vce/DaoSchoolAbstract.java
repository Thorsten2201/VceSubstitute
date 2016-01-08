package de.bbq.java.tasks.vce;

/**
 * @author Thorsten2201
 *
 */
public abstract class DaoSchoolAbstract {

	/////////////////////////////////////////////////////////////////////////////////////
	// Class Properties
	private EDaoSchool eDao = EDaoSchool.ABSTACT;
	/////////////////////////////////////////////////////////////////////////////////////

	/////////////////////////////////////////////////////////////////////////////////////
	// Construct
	protected DaoSchoolAbstract(EDaoSchool eDao) {
		this.seteDao(eDao);
	}
	/////////////////////////////////////////////////////////////////////////////////////

	/////////////////////////////////////////////////////////////////////////////////////
	// Getter / Setter
	public EDaoSchool getDaoType() {
		return eDao;
	}

	private void seteDao(EDaoSchool eDao) {
		this.eDao = eDao;
	}

	private static DaoSchoolFile daoFile = new DaoSchoolFile();
	private static DaoSchoolJdbcMysql daoJdbcMysql = new DaoSchoolJdbcMysql();

	public static DaoSchoolAbstract getDaoSchool(EDaoSchool eDao) {
		switch (eDao) {
		case FILE:

			return daoFile;
		case JDBC_MYSQL:
			return daoJdbcMysql;
		default:
			return null;
		}
	}
	/////////////////////////////////////////////////////////////////////////////////////

	/////////////////////////////////////////////////////////////////////////////////////
	// IDaoSchool
	public abstract boolean saveElement(ExamItemAbstract schoolItemAbstract);

	public abstract boolean loadElement(ExamItemAbstract schoolItemAbstract);

	public abstract boolean deleteElement(ExamItemAbstract schoolItemAbstract);

	private boolean unsavedTeacher(ExamItemAbstract seed) {
		for (IQuestion teacher : Question.getTeachers()) {
			if (!((ExamItemAbstract) teacher).isSaved()) {
				if (!((ExamItemAbstract) teacher).equals(seed)) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean unsavedStudent(ExamItemAbstract seed) {
		for (IAnswer student : Answer.getStudents()) {
			if (!((ExamItemAbstract) student).isSaved()) {
				if (!((ExamItemAbstract) student).equals(seed)) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean unsavedCourse(ExamItemAbstract seed) {
		for (ISolution course : Solution.getCourses()) {
			if (!((ExamItemAbstract) course).isSaved()) {
				if (!((ExamItemAbstract) course).equals(seed)) {
					return true;
				}
			}
		}
		return false;
	}

	private void unset() {
		for (ISolution course : Solution.getCourses()) {
			((ExamItemAbstract) course).setSaved(false);
		}
		for (IQuestion teacher : Question.getTeachers()) {
			((ExamItemAbstract) teacher).setSaved(false);
		}
		for (IAnswer student : Answer.getStudents()) {
			((ExamItemAbstract) student).setSaved(false);
		}
	}

	public boolean saveAllAhead() {
		boolean ret = false;
		for (IQuestion t : ExamenVerwaltung.getQuestionList()) {
			if (!(((ExamItemAbstract) t).isSaved())) {
				if (!unsavedTeacher((ExamItemAbstract) t) && !unsavedStudent((ExamItemAbstract) t)) {
					((ExamItemAbstract) t).setLast(true);
				}
				ret &= DaoSchoolAbstract.getDaoSchool(ExamenVerwaltung.getSelectedDao())
						.saveElement((ExamItemAbstract) t);
			}
		}
		for (int index = 0; index < Solution.getCourses().size(); index++) {
			ISolution c = Solution.getCourses().get(index);
			if (!unsavedTeacher((ExamItemAbstract) c) && !unsavedCourse((ExamItemAbstract) c)
					&& !unsavedStudent((ExamItemAbstract) c)) {
				((ExamItemAbstract) c).setLast(true);
			}
			ret &= DaoSchoolAbstract.getDaoSchool(ExamenVerwaltung.getSelectedDao()).saveElement((ExamItemAbstract) c);
		}
		for (IAnswer s : ExamenVerwaltung.getStudentList()) {
			if (!((ExamItemAbstract) s).isSaved()) {
				if (!unsavedStudent((ExamItemAbstract) s)) {
					((ExamItemAbstract) s).setLast(true);
				}
				ret &= DaoSchoolAbstract.getDaoSchool(ExamenVerwaltung.getSelectedDao())
						.saveElement((ExamItemAbstract) s);
			}
		}
		unset();
		return ret;
	}

	public boolean saveAll() {
		boolean ret = false;
		for (int index = 0; index < Solution.getCourses().size(); index++) {
			ISolution c = Solution.getCourses().get(index);
			if (!unsavedTeacher((ExamItemAbstract) c) && !unsavedCourse((ExamItemAbstract) c)
					&& !unsavedStudent((ExamItemAbstract) c)) {
				((ExamItemAbstract) c).setLast(true);
			}
			ret &= DaoSchoolAbstract.getDaoSchool(ExamenVerwaltung.getSelectedDao()).saveElement((ExamItemAbstract) c);
			if (c.hasQuestion()) {
				if (!unsavedTeacher((ExamItemAbstract) c.getQuestion())
						&& !unsavedCourse((ExamItemAbstract) c.getQuestion())
						&& !unsavedStudent((ExamItemAbstract) c.getQuestion())) {
					((ExamItemAbstract) c.getQuestion()).setLast(true);
				}
				ret &= DaoSchoolAbstract.getDaoSchool(ExamenVerwaltung.getSelectedDao())
						.saveElement((ExamItemAbstract) c.getQuestion());
			}
			if (c.hasAnswers()) {
				for (IAnswer s : c.getAnswers()) {
					if (!unsavedTeacher((ExamItemAbstract) s) && !unsavedCourse((ExamItemAbstract) s)
							&& !unsavedStudent((ExamItemAbstract) s)) {
						((ExamItemAbstract) s).setLast(true);
					}
					ret &= DaoSchoolAbstract.getDaoSchool(ExamenVerwaltung.getSelectedDao())
							.saveElement((ExamItemAbstract) s);
				}
			}
		}
		for (IQuestion t : ExamenVerwaltung.getQuestionList()) {
			if (!(((ExamItemAbstract) t).isSaved())) {
				if (!unsavedTeacher((ExamItemAbstract) t) && !unsavedStudent((ExamItemAbstract) t)) {
					((ExamItemAbstract) t).setLast(true);
				}
				ret &= DaoSchoolAbstract.getDaoSchool(ExamenVerwaltung.getSelectedDao())
						.saveElement((ExamItemAbstract) t);
			}
		}
		for (IAnswer s : ExamenVerwaltung.getStudentList()) {
			if (!((ExamItemAbstract) s).isSaved()) {
				if (!unsavedStudent((ExamItemAbstract) s)) {
					((ExamItemAbstract) s).setLast(true);
				}
				ret &= DaoSchoolAbstract.getDaoSchool(ExamenVerwaltung.getSelectedDao())
						.saveElement((ExamItemAbstract) s);
			}
		}
		unset();
		return ret;
	}

	public static boolean closeConnections() {
		boolean ret = false;
		daoFile = null;
		if (daoJdbcMysql != null) {
			ret = daoJdbcMysql.closeConnection();
		} else {
			ret = true;
		}
		daoJdbcMysql = null;
		return ret;
	}

	public abstract boolean loadAll();
	/////////////////////////////////////////////////////////////////////////////////////

}