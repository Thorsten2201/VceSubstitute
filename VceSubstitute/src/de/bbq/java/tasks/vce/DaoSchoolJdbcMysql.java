package de.bbq.java.tasks.vce;

import java.awt.Frame;
import java.awt.HeadlessException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Types;
import java.util.HashMap;

import com.mysql.jdbc.DatabaseMetaData;
import com.mysql.jdbc.Statement;

/**
 * @author Thorsten2201
 *
 */
public class DaoSchoolJdbcMysql extends DaoSchoolJdbcAbstract {
	Connection connection;
	final String DEFAULT_DATABASE = "localhost/jdbc";
	final String DEFAULT_USERNAME = "root";
	final String EXAM_TABLE = "exam";
	final String QUESTION_TABLE = "question";
	final String ANSWER_TABLE = "answer";

	HashMap<Integer, Long> examIds = new HashMap<>();
	HashMap<Long, Integer> examIdsInv = new HashMap<>();
	HashMap<Long, Integer> questionIds = new HashMap<>();
	HashMap<Long, Integer> answerIds = new HashMap<>();

	ResultSet resultSet;

	/////////////////////////////////////////////////////////////////////////////////////
	// Construct
	public DaoSchoolJdbcMysql() {
		super(EDaoSchool.JDBC_MYSQL);
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			ExamenVerwaltung.showErrorMessage("Loading com.mysql.jdbc.Driver failed.");
		}
	}
	/////////////////////////////////////////////////////////////////////////////////////

	/////////////////////////////////////////////////////////////////////////////////////
	// DaoSchoolAbstract properties
	@Override
	public boolean saveElement(ExamItemAbstract schoolItemAbstract) {
		if (elementExists(schoolItemAbstract)) {
			return updateElement(schoolItemAbstract);
		} else {
			return insertElement(schoolItemAbstract);
		}
	}

	private boolean elementExists(ExamItemAbstract schoolItemAbstract) {
		try {
			PreparedStatement preparedStatement = null;
			if (schoolItemAbstract instanceof IQuestion) {
				preparedStatement = this.getConnection()
						.prepareStatement("SELECT `id` FROM " + EXAM_TABLE + " WHERE (`id` = ?);");
				if (this.examIdsInv.containsKey(schoolItemAbstract.getId())) {
					preparedStatement.setInt(1, this.examIdsInv.get(schoolItemAbstract.getId()));
				} else {
					preparedStatement.setInt(1, -1);
				}
			} else if (schoolItemAbstract instanceof IQuestion) {
				preparedStatement = this.getConnection()
						.prepareStatement("SELECT `id` FROM " + QUESTION_TABLE + " WHERE (`id` = ?);");
				if (this.questionIds.containsKey(schoolItemAbstract.getId())) {
					preparedStatement.setInt(1, this.questionIds.get(schoolItemAbstract.getId()));
				} else {
					preparedStatement.setInt(1, -1);
				}
			} else if (schoolItemAbstract instanceof IAnswer) {
				preparedStatement = this.getConnection()
						.prepareStatement("SELECT `id` FROM " + ANSWER_TABLE + " WHERE (`id` = ?);");
				if (this.answerIds.containsKey(schoolItemAbstract.getId())) {
					preparedStatement.setInt(1, this.answerIds.get(schoolItemAbstract.getId()));
				} else {
					preparedStatement.setInt(1, -1);
				}
			}
			this.resultSet = preparedStatement.executeQuery();
			return this.resultSet.next();
		} catch (SQLException e) {
			ExamenVerwaltung.showException(e);
			return false;
		}
	}

	private boolean updateElement(ExamItemAbstract schoolItemAbstract) {
		try {
			if (schoolItemAbstract instanceof IExam) {
				Exam exam = (Exam) schoolItemAbstract;
				String sql = "UPDATE " + EXAM_TABLE
						+ " SET `examName` = ?, `examName` = ?, `language` = ?, `description` = ? WHERE `id` = ?;";
				PreparedStatement statement = this.connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

				// the course had to pass elementExists() to get here, so
				// courseIdsInv contains the key
				int examId = 0;
				if (this.examIdsInv.containsKey(((ExamItemAbstract) exam).getId())) {
					examId = examIdsInv.get(((ExamItemAbstract) exam).getId());
				}
				for (IQuestion q : exam.getQuestions()) {
					if (!this.questionIds.containsKey(((ExamItemAbstract) q).getId())) {
						this.saveElement((ExamItemAbstract) q);
					}
					if (!this.questionIds.containsKey(((ExamItemAbstract) q).getId())) {
						this.saveElement((ExamItemAbstract) q);
					}
					int questionId = questionIds.get(((ExamItemAbstract) q).getId());

					//statement.setInt(1, questionId);
					statement.setString(2, q.getQuestionName());
					// if (course.getEndTime() == null) {
					// statement.setNull(3, Types.DATE);
					// } else {
					// statement.setDate(3, new
					// java.sql.Date(course.getEndTime().getTime()));
					// }
					statement.setString(4, exam.getLanguage());
					// statement.setBoolean(5, course.getNeedsBeamer());
					// statement.setString(6, course.getRoomNumber());
					// if (course.getStartTime() == null) {
					// statement.setNull(7, Types.DATE);
					// } else {
					// statement.setDate(7, new
					// java.sql.Date(course.getStartTime().getTime()));
					// }
					// statement.setString(8, course.getTopic());
					statement.setInt(9, examId);
				}

				int affectedRows = statement.executeUpdate();
				if (affectedRows == 0) {
					throw new SQLException("Kurs aktualisieren fehlgeschlagen user failed, keine Reihen beeinflusst.");
				}
			} else if (schoolItemAbstract instanceof IQuestion) {
				Question teacher = (Question) schoolItemAbstract;
				String sql = "UPDATE " + QUESTION_TABLE
						+ " SET `lastName` = ?,`firstName` = ?,`birthTime` = ?,`city` = ?, `country` = ?, `houseNumber` = ?, `streetName` = ?, `zipCode` = ? WHERE `id` = ?;";
				PreparedStatement statement = this.connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

				// TODO: statement.setString(1, teacher.getLastName());
				// statement.setString(2, teacher.getFirstName());
				// if (teacher.getBirthDate() == null) {
				// statement.setNull(3, Types.DATE);
				// } else {
				// statement.setDate(3, new
				// java.sql.Date(teacher.getBirthDate().getTime()));
				// }
				// statement.setString(4, teacher.getAdress().getCity());
				// statement.setString(5, teacher.getAdress().getCountry());
				// statement.setString(6, teacher.getAdress().getHouseNumber());
				// statement.setString(7, teacher.getAdress().getStreetName());
				// statement.setInt(8, teacher.getAdress().getZipCode());

				// the teacher had to pass elementExists() to get here, so
				// teacherIds contains the key
				int teacherId = questionIds.get(((ExamItemAbstract) teacher).getId());
				statement.setInt(9, teacherId);
				int affectedRows = statement.executeUpdate();
				if (affectedRows == 0) {
					throw new SQLException("Leerer anlegen fehlgeschlagen, keine Reihen beeinflusst.");
				}
			} else if (schoolItemAbstract instanceof IAnswer) {
				Answer student = (Answer) schoolItemAbstract;
				String sql = "UPDATE " + ANSWER_TABLE
						+ " SET `lastName` = ?,`firstName` = ?,`birthTime` = ?,`city` = ?, `country` = ?, `houseNumber` = ?, `streetName` = ?, `zipCode` = ? WHERE `id` = ?;";
				PreparedStatement statement = this.connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

				// TODO: statement.setString(1, student.getLastName());
				// statement.setString(2, student.getFirstName());
				// if (student.getBirthDate() == null) {
				// statement.setNull(3, Types.DATE);
				// } else {
				// statement.setDate(3, new
				// java.sql.Date(student.getBirthDate().getTime()));
				// }
				// statement.setString(4, student.getAdress().getCity());
				// statement.setString(5, student.getAdress().getCountry());
				// statement.setString(6, student.getAdress().getHouseNumber());
				// statement.setString(7, student.getAdress().getStreetName());
				// statement.setInt(8, student.getAdress().getZipCode());

				// the student had to pass elementExists() to get here, so
				// studentIds contains the key
				int studentId = answerIds.get(((ExamItemAbstract) student).getId());
				statement.setInt(9, studentId);
				int affectedRows = statement.executeUpdate();
				if (affectedRows == 0) {
					throw new SQLException("Schüler anlegen fehlgeschlagen, keine Reihen beeinflusst.");
				}
			}
		} catch (SQLException e) {
			ExamenVerwaltung.showException(e);
			return false;
		}
		return true;
	}

	private boolean insertElement(ExamItemAbstract schoolItemAbstract) {
		try {
			if (schoolItemAbstract instanceof IExam) {
				Exam exam = (Exam) schoolItemAbstract;
				String sql = "INSERT INTO " + EXAM_TABLE
						+ " (`examId`, `examName`, `language`, `description`) VALUES(?, ?, ?, ?);";
				PreparedStatement statement = this.connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				int questionId = 0;
				if (exam.hasQuestions()) {
					for (IQuestion qi : exam.getQuestions()) {
						Question q = (Question) qi;
						if (!this.questionIds.containsKey(((ExamItemAbstract) q).getId())) {
							this.saveElement((ExamItemAbstract) q); //
						}
						questionId = questionIds.get(((ExamItemAbstract) q).getId());

						statement.setInt(1, questionId);
						statement.setString(2, q.getQuestionName());
						// if (course.getEndTime() == null) {
						// statement.setNull(3, Types.DATE);
						// } else {
						// statement.setDate(3, new
						// java.sql.Date(course.getEndTime().getTime()));
						// }
						statement.setString(4, q.getLanguage());
						// statement.setBoolean(5, course.getNeedsBeamer());
						// statement.setString(6, course.getRoomNumber());
						// if (course.getStartTime() == null) {
						// statement.setNull(7, Types.DATE);
						// } else {
						// statement.setDate(7, new
						// java.sql.Date(course.getStartTime().getTime()));
						// }
						// statement.setString(8, course.getTopic());

						int affectedRows = statement.executeUpdate();
						if (affectedRows == 0) {
							throw new SQLException(ExamenVerwaltung.getText("create.exam.failed.no.rows"));
						}
						try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
							if (generatedKeys.next()) {
								examIdsInv.put(schoolItemAbstract.getId(), generatedKeys.getInt(1));
							} else {
								throw new SQLException(ExamenVerwaltung.getText("create.exam.failed.no.id"));
							}
						}
					}
				}
			} else if (schoolItemAbstract instanceof IQuestion) {
				Question teacher = (Question) schoolItemAbstract;
				String sql = "INSERT INTO " + QUESTION_TABLE
						+ " (`lastName`,`firstName`,`birthTime`,`city`, `country`, `houseNumber`, `streetName`, `zipCode`) VALUES(?, ?, ?, ?, ?, ?, ?, ?);";
				PreparedStatement statement = this.connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

				// TODO: statement.setString(1, teacher.getLastName());
				// statement.setString(2, teacher.getFirstName());
				// if (teacher.getBirthDate() == null) {
				// statement.setNull(3, Types.DATE);
				// } else {
				// statement.setDate(3, new
				// java.sql.Date((teacher.getBirthDate().getTime())));
				// }
				// statement.setString(4, teacher.getAdress().getCity());
				// statement.setString(5, teacher.getAdress().getCountry());
				// statement.setString(6, teacher.getAdress().getHouseNumber());
				// statement.setString(7, teacher.getAdress().getStreetName());
				// statement.setInt(8, teacher.getAdress().getZipCode());

				int affectedRows = statement.executeUpdate();
				if (affectedRows == 0) {
					throw new SQLException("Leerer anlegen fehlgeschlagen, keine Reihen beeinflusst.");
				}
				try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
					if (generatedKeys.next()) {
						questionIds.put(schoolItemAbstract.getId(), generatedKeys.getInt(1));
					} else {
						throw new SQLException("Leerer anlegen fehlgeschlagen, keine ID bekommen.");
					}
				}
			} else if (schoolItemAbstract instanceof IAnswer) {
				Answer student = (Answer) schoolItemAbstract;
				String sql = "INSERT INTO " + ANSWER_TABLE
						+ " (`courseId`,`lastName`,`firstName`,`birthTime`,`city`, `country`, `houseNumber`, `streetName`, `zipCode`) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?);";
				PreparedStatement statement = this.connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

				int courseId = 0;
				if (student.hasQuestion()) {
					if (!this.examIdsInv.containsKey(((ExamItemAbstract) student.getQuestion()).getId())) {
						this.saveElement((ExamItemAbstract) student.getQuestion());
					}
					courseId = this.examIdsInv.get(((ExamItemAbstract) student.getQuestion()).getId());
				}
				statement.setInt(1, courseId);
				// TODO: statement.setString(2, student.getLastName());
				// statement.setString(3, student.getFirstName());
				// if (student.getBirthDate() == null) {
				// statement.setNull(4, Types.DATE);
				// } else {
				// statement.setDate(4, new
				// java.sql.Date(student.getBirthDate().getTime()));
				// }
				// statement.setString(5, student.getAdress().getCity());
				// statement.setString(6, student.getAdress().getCountry());
				// statement.setString(7, student.getAdress().getHouseNumber());
				// statement.setString(8, student.getAdress().getStreetName());
				// statement.setInt(9, student.getAdress().getZipCode());

				int affectedRows = statement.executeUpdate();
				if (affectedRows == 0) {
					throw new SQLException("Leerer anlegen fehlgeschlagen, keine Reihen beeinflusst.");
				}
				try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
					if (generatedKeys.next()) {
						this.answerIds.put(schoolItemAbstract.getId(), generatedKeys.getInt(1));
					} else {
						throw new SQLException("Leerer anlegen fehlgeschlagen, keine ID bekommen.");
					}
				}
			}
		} catch (SQLException e) {
			ExamenVerwaltung.showException(e);
			return false;
		}
		return true;
	}

	@Override
	public boolean saveAll() {
		boolean ret = true;
		Savepoint savePoint = null;
		try {
			getConnection().setAutoCommit(false);
			savePoint = getConnection().setSavepoint();

			// this.courseIds = new HashMap<>();
			// this.courseIdsInv = new HashMap<>();
			// this.teacherIds = new HashMap<>();
			// this.studentIds = new HashMap<>();

			ret = super.saveAllAhead();
			getConnection().commit();
		} catch (SQLException e) {
			try {
				getConnection().rollback(savePoint);
			} catch (SQLException e1) {
				System.out.println(e1.getStackTrace());
				ret = false;
			}
			ExamenVerwaltung.showException(e);
			ret = false;
		}
		try {
			getConnection().setAutoCommit(true);
		} catch (SQLException e) {
			System.out.println(e.getStackTrace());
		}
		return ret;
	}

	@Override
	public boolean deleteElement(ExamItemAbstract schoolItemAbstract) {
		try {
			PreparedStatement preparedStatement = null;
			if (schoolItemAbstract instanceof IQuestion) {
				preparedStatement = this.getConnection()
						.prepareStatement("DELETE FROM " + EXAM_TABLE + " WHERE (`id` = ?);");
				if (this.examIdsInv.containsKey(schoolItemAbstract.getId())) {
					preparedStatement.setInt(1, this.examIdsInv.get(schoolItemAbstract.getId()));
				} else {
					preparedStatement.setInt(1, -1);
				}
			} else if (schoolItemAbstract instanceof IQuestion) {
				preparedStatement = this.getConnection()
						.prepareStatement("DELETE FROM " + QUESTION_TABLE + " WHERE (`id` = ?);");
				if (this.questionIds.containsKey(schoolItemAbstract.getId())) {
					preparedStatement.setInt(1, this.questionIds.get(schoolItemAbstract.getId()));
				} else {
					preparedStatement.setInt(1, -1);
				}
			} else if (schoolItemAbstract instanceof IAnswer) {
				preparedStatement = this.getConnection()
						.prepareStatement("DELETE FROM " + ANSWER_TABLE + " WHERE (`id` = ?);");
				if (this.answerIds.containsKey(schoolItemAbstract.getId())) {
					preparedStatement.setInt(1, this.answerIds.get(schoolItemAbstract.getId()));
				} else {
					preparedStatement.setInt(1, -1);
				}
			}
			preparedStatement.execute();
			return true;
		} catch (SQLException e) {
			ExamenVerwaltung.showException(e);
			return false;
		}
	}

	@Override
	public boolean loadElement(ExamItemAbstract schoolItemAbstract) {
		try {
			if (schoolItemAbstract instanceof IQuestion) {
				Question sqlCourse = (Question) schoolItemAbstract;
				examIds.put(resultSet.getInt(1), sqlCourse.getId());
				examIdsInv.put(sqlCourse.getId(), resultSet.getInt(1));
				int sqlTeacherId = resultSet.getInt(2);
				sqlCourse.setQuestionName(resultSet.getString(3));
				// sqlCourse.setEndTime(resultSet.getDate(4));
				sqlCourse.setLanguage(resultSet.getString(5));
				// sqlCourse.setNeedsBeamer(resultSet.getBoolean(6));
				// sqlCourse.setRoomNumber(resultSet.getString(7));
				// sqlCourse.setStartTime(resultSet.getDate(8));
				// sqlCourse.setTopic(resultSet.getString(9));
				// get teacher
				// for (ITeacher t : SchoolLauncher.getTeacherList()) {
				// if (sqlTeacherId == ((Teacher) t).getId()) {
				// try {
				// t.addCourse(sqlCourse);
				// } catch (Exception e) {
				// JOptionPane.showMessageDialog(null, e.getMessage());
				// e.printStackTrace();
				// }
				// break;
				// }
				// }
				return checkIds(sqlCourse, sqlTeacherId); // checkIds();
			} else if (schoolItemAbstract instanceof IQuestion) {
				Question sqlTeacher = (Question) schoolItemAbstract;
				int teacherId = resultSet.getInt(1);
				questionIds.put(sqlTeacher.getId(), teacherId);
				// TODO: sqlTeacher.setLastName(resultSet.getString(2));
				// sqlTeacher.setFirstName(resultSet.getString(3));
				// sqlTeacher.setBirthDate(resultSet.getDate(4));
				// sqlTeacher.getAdress().setCity(resultSet.getString(5));
				// sqlTeacher.getAdress().setCountry(resultSet.getString(6));
				// sqlTeacher.getAdress().setHouseNumber(resultSet.getString(7));
				// sqlTeacher.getAdress().setStreetName(resultSet.getString(8));
				// sqlTeacher.getAdress().setZipCode(resultSet.getInt(9));
				return checkIds(sqlTeacher);
			} else if (schoolItemAbstract instanceof IAnswer) {
				Answer sqlStudent = (Answer) schoolItemAbstract;
				int studentId = resultSet.getInt(1);
				int courseId = resultSet.getInt(2);
				answerIds.put(sqlStudent.getId(), studentId);
				// TODO: sqlStudent.setLastName(resultSet.getString(3));
				// sqlStudent.setFirstName(resultSet.getString(4));
				// sqlStudent.setBirthDate(resultSet.getDate(5));
				// sqlStudent.getAdress().setCity(resultSet.getString(6));
				// sqlStudent.getAdress().setCountry(resultSet.getString(7));
				// sqlStudent.getAdress().setHouseNumber(resultSet.getString(8));
				// sqlStudent.getAdress().setStreetName(resultSet.getString(9));
				// sqlStudent.getAdress().setZipCode(resultSet.getInt(10));
				// GetCourse
				// if (this.courseIds.containsKey(courseId)) {
				// Long javaCourseId = this.courseIds.get(courseId);
				// for (ICourse c : SchoolLauncher.getCourseList()) {
				// if (javaCourseId.equals(((Course) c).getId())) {
				// c.addStudent(sqlStudent);
				// break;
				// }
				// }/
				// }
				return checkIds(sqlStudent, courseId);
			}
		} catch (Exception e) {
			return false;
		}
		return false;
	}

	@Override
	public boolean loadAll() {
		int cc, ct, cs;
		cc = ct = cs = 0;
		try {
			if (getConnection() != null) {
				checkTables();
				this.resultSet = null;
				PreparedStatement preparedStatement = null;

				examIds = new HashMap<>();
				examIdsInv = new HashMap<>();
				questionIds = new HashMap<>();
				answerIds = new HashMap<>();

				preparedStatement = this.getConnection()
						.prepareStatement("SELECT `id`,`lastName`,`firstName`,`birthTime`,`city`, "
								+ " `country`, `houseNumber`, `streetName`, `zipCode` FROM " + QUESTION_TABLE + ";");
				this.resultSet = preparedStatement.executeQuery();
				while (this.resultSet.next()) {
					ExamItemAbstract loadItem = (ExamItemAbstract) ExamenVerwaltung.getNewQuestion(true);
					if (this.loadElement(loadItem)) {
						ct++;
					} else {
						ExamenVerwaltung.deleteElement(loadItem);
					}
				}

				preparedStatement = this.getConnection().prepareStatement(
						"SELECT `id`, `teacherId`, `courseName`, `endTime`, `language`, `needsBeamer`, `roomNumber`, `startTime`, `topic` FROM "
								+ EXAM_TABLE + ";");
				this.resultSet = preparedStatement.executeQuery();
				while (this.resultSet.next()) {
					ExamItemAbstract loadItem = (ExamItemAbstract) ExamenVerwaltung.getNewQuestion(true);
					if (this.loadElement(loadItem)) {
						cc++;
					} else {
						ExamenVerwaltung.deleteElement(loadItem);
					}
				}

				preparedStatement = this.getConnection()
						.prepareStatement("SELECT `id`,`courseId`,`lastName`,`firstName`,`birthTime`,`city`, "
								+ " `country`, `houseNumber`, `streetName`, `zipCode` FROM " + ANSWER_TABLE + ";");
				resultSet = preparedStatement.executeQuery();
				while (resultSet.next()) {
					ExamItemAbstract loadItem = (ExamItemAbstract) ExamenVerwaltung.getNewAnswer(true);
					if (this.loadElement(loadItem)) {
						cs++;
					} else {
						ExamenVerwaltung.deleteElement(loadItem);
					}
				}
				resultSet.close();
				preparedStatement.close();
			}
		} catch (HeadlessException e) {
			ExamenVerwaltung.showException(e);
			return false;
		} catch (SQLException e) {
			ExamenVerwaltung.showException(e);
			return false;
		}
		String out = "Aus Datei gelutscht: " + cc + " mal Dummgelaber, " + ct + " Labertaschen und " + cs
				+ " Hohlköpfe.";
		ExamenVerwaltung.showMessage("loadAll fertig!\r\n\r\n" + out);
		return true;
	}

	@Override
	public boolean connect(Frame parent, String database, String username, String password) {
		LoginDialog loginDialog = null;
		if (database.length() == 0) {
			database = DEFAULT_DATABASE;
			username = DEFAULT_USERNAME;
			loginDialog = new LoginDialog(parent, this, database, username, password);
			loginDialog.setVisible(true);
			database = loginDialog.getDatabase();
			username = loginDialog.getUsername();
			password = loginDialog.getPassword();
		}
		if (database.length() == 0 && username.length() == 0) {
			loginDialog.setVisible(false);
			return loginDialog.isSucceeded();
		} else {
			try {
				String conString = "jdbc:mysql://" + database + "?user=" + username + "&password=" + password;
				this.connection = DriverManager.getConnection(conString);
			} catch (SQLException e) {
				ExamenVerwaltung.showErrorMessage("Username oder Passwort sind falsch.");
				this.connection = null;
				loginDialog = null;
			}
			if (loginDialog != null) {
				loginDialog.setVisible(false);
				return loginDialog.isSucceeded();
			} else {
				if (this.connection == null) {
					return false;
				} else {
					boolean ret = true;
					try {
						ret = !this.connection.isClosed();
					} catch (SQLException e) {
						ret = false;
						e.printStackTrace();
					}
					return ret;
				}
			}
		}
	}

	@Override
	public boolean closeConnection() {
		if (this.connection != null) {
			try {
				this.connection.close();
				return true;
			} catch (SQLException e) {
				ExamenVerwaltung.showException(e);
			}
			return false;
		}
		return true;
	}

	private void checkTables() throws SQLException {
		DatabaseMetaData md = (DatabaseMetaData) getConnection().getMetaData();
		ResultSet rs = md.getTables(null, null, "%", null);
		boolean foundCourse = false, foundTeacher = false, foundStudent = false;
		while (rs.next()) {
			if (rs.getString(3).compareTo(EXAM_TABLE) == 0) {
				foundCourse = true;
			}
			if (rs.getString(3).compareTo(QUESTION_TABLE) == 0) {
				foundTeacher = true;
			}
			if (rs.getString(3).compareTo(ANSWER_TABLE) == 0) {
				foundStudent = true;
			}
			System.out.println(rs.getString(3));
			if (foundCourse && foundTeacher && foundStudent) {
				break;
			}
		}
		if (!foundCourse) {
			createTable(0);
		}
		if (!foundCourse) {
			createTable(1);
		}
		if (!foundCourse) {
			createTable(2);
		}
	}

	private boolean checkIds(IQuestion course, int sqlCoursQuestionId) {
		for (IQuestion question : ExamenVerwaltung.getQuestionList()) {
			int sqlQuestionId = -1;
			ExamItemAbstract t = (ExamItemAbstract) question;
			if (this.questionIds.containsKey(t.getId())) {
				sqlQuestionId = this.questionIds.get(t.getId());
			}
			if (sqlCoursQuestionId == sqlQuestionId) {
				course.addQuestion(question);
			}
		}
		// Always, return value for loadElement
		return true;
	}

	private boolean checkIds(IQuestion question) {
		for (IQuestion q : ExamenVerwaltung.getQuestionList()) {
			ExamItemAbstract e = (ExamItemAbstract) q;
			int sqlCourseId = 0, sqlTeacherId = -1;
			if (this.examIdsInv.containsKey(e.getId())) {
				sqlCourseId = this.examIdsInv.get(e.getId());
				for (IQuestion quest : Question.getQuestions()) {
					ExamItemAbstract tc = (ExamItemAbstract) quest;
					if (this.examIdsInv.containsKey(tc.getId())) {
						sqlTeacherId = this.examIdsInv.get(tc.getId());
					}
					if (sqlCourseId == sqlTeacherId) {
						q.addQuestion(question);
					}
				}
			}
		}
		// Always, return value for loadElement
		return true;
	}

	private boolean checkIds(IAnswer student, int sqlCourseId) {
		// int sqlStudentId = this.studentIds.get(((SchoolItemAbstract)
		// student).getId());
		for (IQuestion course : ExamenVerwaltung.getQuestionList()) {
			ExamItemAbstract sac = (ExamItemAbstract) course;
			if (this.examIdsInv.containsKey(sac.getId())) {
				int sqlStudentId = this.examIdsInv.get(sac.getId());
				if (sqlCourseId == sqlStudentId) {
					course.addAnswer(student);
					break;
				}
			}
		}
		// Always, return value for loadElement
		return true;
	}

	private boolean createTable(int table) {
		boolean ret = false;
		Statement stmt = null;
		String sql = "";

		try {
			stmt = (Statement) getConnection().createStatement();
		} catch (SQLException e) {
			ExamenVerwaltung.showException(e);
		}

		switch (table) {
		case 0:
			sql = "CREATE TABLE `course` (id INTEGER NOT NULL AUTO_INCREMENT, `teacherId` INTEGER, `courseName` VARCHAR(255),  `endTime` DATE, `language` VARCHAR(255), "
					+ " `needsBeamer` BOOL,  `roomNumber` VARCHAR(255),  `startTime` DATE,  `topic` VARCHAR(255), primary key(id))";
			break;
		case 1:
			sql = "CREATE TABLE `teacher` (id INTEGER NOT NULL AUTO_INCREMENT,  `lastName` VARCHAR(255), `firstName` VARCHAR(255), `birthTime` DATE, `city` VARCHAR(255), "
					+ " `country` VARCHAR(255),  `houseNumber` VARCHAR(255),  `streetName` VARCHAR(255), `zipCode` INTEGER, primary key(id))";
			break;
		case 2:
			sql = "CREATE TABLE `student` (id INTEGER NOT NULL AUTO_INCREMENT, `courseId` INTEGER, `lastName` VARCHAR(255), `firstName` VARCHAR(255), `birthTime` DATE, `city` VARCHAR(255), "
					+ " `country` VARCHAR(255),  `houseNumber` VARCHAR(255),  `streetName` VARCHAR(255), `zipCode` INTEGER, primary key(id))";
			break;
		}

		try {
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			ExamenVerwaltung.showException(e);
		}
		return ret;
	}

	private Connection getConnection() {
		if (this.connection == null) {
			connect(null, "", "", "");
		}
		return this.connection;
	}
	/////////////////////////////////////////////////////////////////////////////////////

}
