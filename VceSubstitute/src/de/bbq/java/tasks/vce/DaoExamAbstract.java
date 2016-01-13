package de.bbq.java.tasks.vce;

/**
 * @author Thorsten2201
 *
 */
public abstract class DaoExamAbstract {

	// ///////////////////////////////////////////////////////////////////////////////////
	// Class Properties
	private EDaoExam eDao = EDaoExam.ABSTACT;

	// ///////////////////////////////////////////////////////////////////////////////////

	// ///////////////////////////////////////////////////////////////////////////////////
	// Construct
	protected DaoExamAbstract(EDaoExam eDao) {
		this.seteDao(eDao);
	}

	// ///////////////////////////////////////////////////////////////////////////////////

	// ///////////////////////////////////////////////////////////////////////////////////
	// Getter / Setter
	public EDaoExam getDaoType() {
		return eDao;
	}

	private void seteDao(EDaoExam eDao) {
		this.eDao = eDao;
	}

	private static DaoExamFile daoFile = new DaoExamFile();
	private static DaoExamJdbcMysql daoJdbcMysql = new DaoExamJdbcMysql();

	public static DaoExamAbstract getDaoExam(EDaoExam eDao) {
		switch (eDao) {
		case FILE:

			return daoFile;
		case JDBC_MYSQL:
			return daoJdbcMysql;
		default:
			return null;
		}
	}

	// ///////////////////////////////////////////////////////////////////////////////////

	// ///////////////////////////////////////////////////////////////////////////////////
	// IDaoSchool
	public abstract boolean saveElement(ExamItemAbstract examItemAbstract);

	public abstract boolean loadElement(ExamItemAbstract examItemAbstract);

	public abstract boolean deleteElement(ExamItemAbstract examItemAbstract);

	private boolean unsavedQuestion(ExamItemAbstract seed) {
		for (IQuestion question : Question.getQuestions()) {
			if (!((ExamItemAbstract) question).isSaved()) {
				if (!((ExamItemAbstract) question).equals(seed)) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean unsavedAnswer(ExamItemAbstract seed) {
		for (IAnswer answer : Answer.getAnswers()) {
			if (!((ExamItemAbstract) answer).isSaved()) {
				if (!((ExamItemAbstract) answer).equals(seed)) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean unsavedExam(ExamItemAbstract seed) {
		for (IQuestion exam : Question.getQuestions()) {
			if (!((ExamItemAbstract) exam).isSaved()) {
				if (!((ExamItemAbstract) exam).equals(seed)) {
					return true;
				}
			}
		}
		return false;
	}

	private void unset() {
		for (IExam exam : Exam.getExams()) {
			((ExamItemAbstract) exam).setSaved(false);
		}
		for (IQuestion question : Question.getQuestions()) {
			((ExamItemAbstract) question).setSaved(false);
		}
		for (IAnswer answer : Answer.getAnswers()) {
			((ExamItemAbstract) answer).setSaved(false);
		}
	}

	public boolean saveAllAhead() {
		boolean ret = false;
		for (IExam e : ExamenVerwaltung.getExamList()) {
			if (!(((ExamItemAbstract) e).isSaved())) {
				if (!unsavedQuestion((ExamItemAbstract) e)
						&& !unsavedAnswer((ExamItemAbstract) e)) {
					((ExamItemAbstract) e).setLast(true);
				}
				ret &= DaoExamAbstract.getDaoExam(
						ExamenVerwaltung.getSelectedDao()).saveElement(
						(ExamItemAbstract) e);
				for (IQuestion q : e.getQuestions()) {
					if (!(((ExamItemAbstract) q).isSaved())) {
						if (!unsavedQuestion((ExamItemAbstract) q)
								&& !unsavedAnswer((ExamItemAbstract) q)) {
							((ExamItemAbstract) q).setLast(true);
						}
						ret &= DaoExamAbstract.getDaoExam(
								ExamenVerwaltung.getSelectedDao()).saveElement(
								(ExamItemAbstract) q);
					}
					for (IAnswer a : q.getAnswers()) {
						if (!((ExamItemAbstract) a).isSaved()) {
							if (!unsavedAnswer((ExamItemAbstract) a)) {
								((ExamItemAbstract) a).setLast(true);
							}
							ret &= DaoExamAbstract.getDaoExam(
									ExamenVerwaltung.getSelectedDao())
									.saveElement((ExamItemAbstract) a);
						}
					}
				}
			}
		}
		unset();
		return ret;
	}

	public boolean saveAll() {
		boolean ret = false;
		for (IExam e : ExamenVerwaltung.getExamList()) {
			if (!unsavedQuestion((ExamItemAbstract) e)
					&& !unsavedExam((ExamItemAbstract) e)
					&& !unsavedAnswer((ExamItemAbstract) e)) {
				((ExamItemAbstract) e).setLast(true);
			}
			ret &= DaoExamAbstract
					.getDaoExam(ExamenVerwaltung.getSelectedDao()).saveElement(
							(ExamItemAbstract) e);
			if (e.hasQuestions()) {
				for (IQuestion q : e.getQuestions()) {
					if (!unsavedQuestion((ExamItemAbstract) q)
							&& !unsavedExam((ExamItemAbstract) q)
							&& !unsavedAnswer((ExamItemAbstract) q)) {
						((ExamItemAbstract) q).setLast(true);
					}
					ret &= DaoExamAbstract.getDaoExam(
							ExamenVerwaltung.getSelectedDao()).saveElement(
							(ExamItemAbstract) q);

					if (q.hasAnswers()) {
						for (IAnswer a : q.getAnswers()) {
							if (!unsavedQuestion((ExamItemAbstract) a)
									&& !unsavedExam((ExamItemAbstract) a)
									&& !unsavedAnswer((ExamItemAbstract) a)) {
								((ExamItemAbstract) a).setLast(true);
							}
							ret &= DaoExamAbstract.getDaoExam(
									ExamenVerwaltung.getSelectedDao())
									.saveElement((ExamItemAbstract) a);
						}
					}
				}
			}
		}
		for (IQuestion qunsaved : ExamenVerwaltung.getQuestionList()) {
			if (!(((ExamItemAbstract) qunsaved).isSaved())) {
				if (!unsavedQuestion((ExamItemAbstract) qunsaved)
						&& !unsavedAnswer((ExamItemAbstract) qunsaved)) {
					((ExamItemAbstract) qunsaved).setLast(true);
				}
				ret &= DaoExamAbstract.getDaoExam(
						ExamenVerwaltung.getSelectedDao()).saveElement(
						(ExamItemAbstract) qunsaved);
			}
		}
		for (IAnswer aunsaved : ExamenVerwaltung.getAnswerList()) {
			if (!((ExamItemAbstract) aunsaved).isSaved()) {
				if (!unsavedAnswer((ExamItemAbstract) aunsaved)) {
					((ExamItemAbstract) aunsaved).setLast(true);
				}
				ret &= DaoExamAbstract.getDaoExam(
						ExamenVerwaltung.getSelectedDao()).saveElement(
						(ExamItemAbstract) aunsaved);
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
	// ///////////////////////////////////////////////////////////////////////////////////

}