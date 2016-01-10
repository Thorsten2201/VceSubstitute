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

	private boolean unsavedQuestion(ExamItemAbstract seed) {
		for (IQuestion teacher : Question.getQuestions()) {
			if (!((ExamItemAbstract) teacher).isSaved()) {
				if (!((ExamItemAbstract) teacher).equals(seed)) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean unsavedAnswer(ExamItemAbstract seed) {
		for (IAnswer student : Answer.getAnswers()) {
			if (!((ExamItemAbstract) student).isSaved()) {
				if (!((ExamItemAbstract) student).equals(seed)) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean unsavedExam(ExamItemAbstract seed) {
		for (IQuestion course : Question.getQuestions()) {
			if (!((ExamItemAbstract) course).isSaved()) {
				if (!((ExamItemAbstract) course).equals(seed)) {
					return true;
				}
			}
		}
		return false;
	}

	private void unset() {
		for (IQuestion course : Question.getQuestions()) {
			((ExamItemAbstract) course).setSaved(false);
		}
		for (IQuestion teacher : Question.getQuestions()) {
			((ExamItemAbstract) teacher).setSaved(false);
		}
		for (IAnswer student : Answer.getAnswers()) {
			((ExamItemAbstract) student).setSaved(false);
		}
	}

	public boolean saveAllAhead() {
		boolean ret = false;
		for (IQuestion t : ExamenVerwaltung.getQuestionList()) {
			if (!(((ExamItemAbstract) t).isSaved())) {
				if (!unsavedQuestion((ExamItemAbstract) t) && !unsavedAnswer((ExamItemAbstract) t)) {
					((ExamItemAbstract) t).setLast(true);
				}
				ret &= DaoSchoolAbstract.getDaoSchool(ExamenVerwaltung.getSelectedDao())
						.saveElement((ExamItemAbstract) t);
			}
		}
		for (int index = 0; index < Question.getQuestions().size(); index++) {
			IQuestion c = Question.getQuestions().get(index);
			if (!unsavedQuestion((ExamItemAbstract) c) && !unsavedExam((ExamItemAbstract) c)
					&& !unsavedAnswer((ExamItemAbstract) c)) {
				((ExamItemAbstract) c).setLast(true);
			}
			ret &= DaoSchoolAbstract.getDaoSchool(ExamenVerwaltung.getSelectedDao()).saveElement((ExamItemAbstract) c);
		}
		for (IAnswer s : ExamenVerwaltung.getAnswerList()) {
			if (!((ExamItemAbstract) s).isSaved()) {
				if (!unsavedAnswer((ExamItemAbstract) s)) {
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
		for (int index = 0; index < Question.getQuestions().size(); index++) {
			IExam e = Exam.getExams().get(index);
			if (!unsavedQuestion((ExamItemAbstract) e) && !unsavedExam((ExamItemAbstract) e)
					&& !unsavedAnswer((ExamItemAbstract) e)) {
				((ExamItemAbstract) e).setLast(true);
			}
			ret &= DaoSchoolAbstract.getDaoSchool(ExamenVerwaltung.getSelectedDao()).saveElement((ExamItemAbstract) e);
			if (e.hasQuestions()) {
				for (IQuestion q : e.getQuestions()) {
					if (!unsavedQuestion((ExamItemAbstract) q) && !unsavedExam((ExamItemAbstract) q)
							&& !unsavedAnswer((ExamItemAbstract) q)) {
						((ExamItemAbstract) q).setLast(true);
					}
					ret &= DaoSchoolAbstract.getDaoSchool(ExamenVerwaltung.getSelectedDao())
							.saveElement((ExamItemAbstract) q);

					if (q.hasAnswers()) {
						for (IAnswer a : q.getAnswers()) {
							if (!unsavedQuestion((ExamItemAbstract) a) && !unsavedExam((ExamItemAbstract) a)
									&& !unsavedAnswer((ExamItemAbstract) a)) {
								((ExamItemAbstract) a).setLast(true);
							}
							ret &= DaoSchoolAbstract.getDaoSchool(ExamenVerwaltung.getSelectedDao())
									.saveElement((ExamItemAbstract) a);
						}
					}
				}
			}
		}
		for (IQuestion qunsaved : ExamenVerwaltung.getQuestionList()) {
			if (!(((ExamItemAbstract) qunsaved).isSaved())) {
				if (!unsavedQuestion((ExamItemAbstract) qunsaved) && !unsavedAnswer((ExamItemAbstract) qunsaved)) {
					((ExamItemAbstract) qunsaved).setLast(true);
				}
				ret &= DaoSchoolAbstract.getDaoSchool(ExamenVerwaltung.getSelectedDao())
						.saveElement((ExamItemAbstract) qunsaved);
			}
		}
		for (IAnswer aunsaved : ExamenVerwaltung.getAnswerList()) {
			if (!((ExamItemAbstract) aunsaved).isSaved()) {
				if (!unsavedAnswer((ExamItemAbstract) aunsaved)) {
					((ExamItemAbstract) aunsaved).setLast(true);
				}
				ret &= DaoSchoolAbstract.getDaoSchool(ExamenVerwaltung.getSelectedDao())
						.saveElement((ExamItemAbstract) aunsaved);
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