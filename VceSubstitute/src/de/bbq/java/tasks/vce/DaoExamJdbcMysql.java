package de.bbq.java.tasks.vce;

import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.Image;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLType;
import java.sql.Savepoint;
import java.sql.Types;
import java.util.HashMap;

import com.mysql.jdbc.DatabaseMetaData;
import com.mysql.jdbc.Statement;

/**
 * @author Thorsten2201
 *
 */
public class DaoExamJdbcMysql extends DaoExamJdbcAbstract {
	Connection connection;
	final String DEFAULT_DATABASE = "localhost/jdbc";
	final String DEFAULT_USERNAME = "root";
	final String EXAM_TABLE = "exam";
	final String QUESTION_TABLE = "question";
	final String ANSWER_TABLE = "answer";
	
	final String CATEGORY_TABLE = "category";
	final String EXAM_QUESTION_MAP_TABLE = "questionMap";
	final String QUESTION_ANSWER_MAP_TABLE = "answerMap";
	
	HashMap<Integer, Long> examIds = new HashMap<>();
	HashMap<Long, Integer> examIdsInv = new HashMap<>();
	HashMap<Long, Integer> questionIds = new HashMap<>();
	HashMap<Integer, Long> questionIdsInv = new HashMap<>();
	HashMap<Long, Integer> answerIds = new HashMap<>();
	HashMap<Integer, Long> answerIdsInv = new HashMap<>();

	ResultSet resultSet;

	/////////////////////////////////////////////////////////////////////////////////////
	// Construct
	public DaoExamJdbcMysql() {
		super(EDaoExam.JDBC_MYSQL);
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
	public boolean saveElement(ExamItemAbstract examItemAbstract) {
		if (elementExists(examItemAbstract)) {
			return updateElement(examItemAbstract);
		} else {
			return insertElement(examItemAbstract);
		}
	}

	private boolean elementExists(ExamItemAbstract examItemAbstract) {
		try {
			PreparedStatement preparedStatement = null;
			if (examItemAbstract instanceof IExam) {
				preparedStatement = this.getConnection()
						.prepareStatement("SELECT `id` FROM " + EXAM_TABLE + " WHERE (`id` = ?);");
				if (this.examIdsInv.containsKey(examItemAbstract.getId())) {
					preparedStatement.setInt(1, this.examIdsInv.get(examItemAbstract.getId()));
				} else {
					preparedStatement.setInt(1, -1);
				}
			} else if (examItemAbstract instanceof IQuestion) {
				preparedStatement = this.getConnection()
						.prepareStatement("SELECT `id` FROM " + QUESTION_TABLE + " WHERE (`id` = ?);");
				if (this.questionIds.containsKey(examItemAbstract.getId())) {
					preparedStatement.setInt(1, this.questionIds.get(examItemAbstract.getId()));
				} else {
					preparedStatement.setInt(1, -1);
				}
			} else if (examItemAbstract instanceof IAnswer) {
				preparedStatement = this.getConnection()
						.prepareStatement("SELECT `id` FROM " + ANSWER_TABLE + " WHERE (`id` = ?);");
				if (this.answerIds.containsKey(examItemAbstract.getId())) {
					preparedStatement.setInt(1, this.answerIds.get(examItemAbstract.getId()));
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

	private boolean updateElement(ExamItemAbstract examItemAbstract) {
		try {
			if (examItemAbstract instanceof IExam) {
				Exam exam = (Exam) examItemAbstract;
				int examId = 0;
				if (this.examIdsInv.containsKey(((ExamItemAbstract) exam).getId())) {
					examId = examIdsInv.get(((ExamItemAbstract) exam).getId());
				}
				String sql = "UPDATE " + EXAM_TABLE
						+ " SET `examName` = ?, `language` = ?, `description` = ? WHERE `id` = ?;";
				PreparedStatement statement = this.connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

				// the exam had to pass elementExists() to get here, so
				// examIdsInv contains the key
				statement.setString(1, exam.getName());
				statement.setString(2, exam.getLanguage());
				statement.setString(3, exam.getDescription());
				statement.setInt(4, examId);
				for (IQuestion q : exam.getQuestions()) {
					if (!this.questionIds.containsKey(((ExamItemAbstract) q).getId())) {
						this.saveElement((ExamItemAbstract) q);
					}
				}

				int affectedRows = statement.executeUpdate();
				if (affectedRows == 0) {
					throw new SQLException(ExamenVerwaltung.getText("save.failed"));
				}
			} else if (examItemAbstract instanceof IQuestion) {
				Question question = (Question) examItemAbstract;
				int questionId = questionIds.get(((ExamItemAbstract) question).getId());
				String sql = "UPDATE " + QUESTION_TABLE
						+ " SET `number` = ?,`language` = ?,`questionText` = ?,`image` = ?, `footer` = ?, `explanation` = ?, `imageLine` = ? WHERE `id` = ?;";
				PreparedStatement statement = this.connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
//				statement.setString(1, question.getQuestionName()); `name` = ?, 
				statement.setInt(1, question.getNumber());
				statement.setString(2, question.getQuestionText());
				if(question.getImageStream() == null) {
					statement.setNull(3, java.sql.Types.BLOB);
				} else {
					statement.setBinaryStream(3, question.getImageStream()); 
				}
				statement.setString(4, question.getAnswerExplanation());
				statement.setInt(5, question.getImageLine());
				statement.setInt(6, questionId);
				int affectedRows = statement.executeUpdate();
				if (affectedRows == 0) {
					throw new SQLException(ExamenVerwaltung.getText("save.failed"));
				}
			} else if (examItemAbstract instanceof IAnswer) {
				Answer answer = (Answer) examItemAbstract;
				String sql = "UPDATE " + ANSWER_TABLE
						+ " SET `index` = ?,`answerText` = ?,`position` = ?,`isTrue` = ? WHERE `id` = ?;";
				PreparedStatement statement = this.connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				statement.setString(1, answer.getIndex()); //index
				statement.setString(2, answer.getAnswerText());
				statement.setInt(3, answer.getPosition());
				statement.setBoolean(4, answer.isTrue());
				// the student had to pass elementExists() to get here, so
				// studentIds contains the key
				int answerId = answerIds.get(((ExamItemAbstract) answer).getId());
				statement.setInt(5, answerId);
				int affectedRows = statement.executeUpdate();
				if (affectedRows == 0) {
					throw new SQLException(ExamenVerwaltung.getText("save.failed"));
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
				Question question = (Question) schoolItemAbstract;
				String sql = "INSERT INTO " + QUESTION_TABLE
						+ " (`number`,`language`,`questionText`,`image`, `footer`, `explanation`, `imageLine`) VALUES(?, ?, ?, ?, ?, ?, ?);";
				PreparedStatement statement = this.connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

				statement.setInt(1, question.getNumber());
				statement.setString(2, question.getQuestionText());
				statement.setBinaryStream(3, question.getImageStream());
				statement.setString(4, question.getAnswerExplanation());
				statement.setInt(5, question.getImageLine());

				int affectedRows = statement.executeUpdate();
				if (affectedRows == 0) {
					throw new SQLException(ExamenVerwaltung.getText("create.question.failed.no.rows"));
				}
				try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
					if (generatedKeys.next()) {
						questionIds.put(schoolItemAbstract.getId(), generatedKeys.getInt(1));
					} else {
						throw new SQLException(ExamenVerwaltung.getText("create.question.failed.no.id"));
					}
				}
			} else if (schoolItemAbstract instanceof IAnswer) {
				Answer answer = (Answer) schoolItemAbstract;
				String sql = "INSERT INTO " + ANSWER_TABLE
						+ " (`index`,`answerText`,`isTrue`,`position`) VALUES(?, ?, ?, ?);";
				PreparedStatement statement = this.connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

//				int answerId = 0;
				if (answer.hasQuestion()) {
					if (!this.examIdsInv.containsKey(((ExamItemAbstract) answer.getQuestion()).getId())) {
						this.saveElement((ExamItemAbstract) answer.getQuestion());
					}
//					answerId = this.examIdsInv.get(((ExamItemAbstract) answer.getQuestion()).getId());
				}
//				statement.setInt(1, answerId);
				statement.setString(1, answer.getIndex());
				statement.setString(2, answer.getAnswerText());
				statement.setBoolean(3, answer.isTrue());
				statement.setInt(4, answer.getPosition());

				int affectedRows = statement.executeUpdate();
				if (affectedRows == 0) {
					throw new SQLException(ExamenVerwaltung.getText("create.answer.failed.no.rows"));
				}
				try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
					if (generatedKeys.next()) {
						this.answerIds.put(schoolItemAbstract.getId(), generatedKeys.getInt(1));
					} else {
						throw new SQLException(ExamenVerwaltung.getText("create.answer.failed.no.id"));
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
	public boolean loadElement(ExamItemAbstract examItemAbstract) {
		try {
			if (examItemAbstract instanceof IQuestion) {
				Question sqlQuestion = (Question) examItemAbstract;
				long sqlQuestionId = sqlQuestion.getId();
				questionIdsInv.put(resultSet.getInt(1), sqlQuestion.getId());
				questionIds.put(sqlQuestion.getId(), resultSet.getInt(1));
				sqlQuestion.setNumber(resultSet.getInt(2));
				sqlQuestion.setQuestionText(resultSet.getString(3));
//				sqlQuestion.setQuestionName(resultSet.getString(3));
				sqlQuestion.setQuestionImage(resultSet.getBinaryStream(4));
//				sqlQuestion.setCategories(categories);
				sqlQuestion.setAnswerExpailnation(resultSet.getString(5));
				sqlQuestion.setImageLine(resultSet.getInt(6));
			
				//TODO multivalue category
				return checkIds(sqlQuestion);	 // checkIds();
		
			} else if (examItemAbstract instanceof IExam) {
				Exam sqlExam = (Exam) examItemAbstract;
				int examId = resultSet.getInt(1);
				examIds.put(resultSet.getInt(1), sqlExam.getId());
				examIdsInv.put(sqlExam.getId(), resultSet.getInt(1));
				sqlExam.setName(resultSet.getString(2));
				sqlExam.setDescription(resultSet.getString(3));		
				sqlExam.setLanguage(resultSet.getString(4));

				return checkIds(sqlExam, examId);
			} else if (examItemAbstract instanceof IAnswer) {
				Answer sqlAnswer = (Answer) examItemAbstract;
				int answerId = resultSet.getInt(1);
//				int examId = resultSet.getInt(2);
				char inx =resultSet.getString(2).charAt(0);
				sqlAnswer.setIndex(inx);
				sqlAnswer.setAnswerText(resultSet.getString(3));
				sqlAnswer.setTrue(resultSet.getBoolean(4));
				sqlAnswer.setPosition(resultSet.getInt(5));
				
				answerIds.put(sqlAnswer.getId(), answerId);
				answerIdsInv.put(resultSet.getInt(1), sqlAnswer.getId());
				return checkIds(sqlAnswer, answerId);
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
				questionIdsInv = new HashMap<>();
				answerIds = new HashMap<>();
				answerIdsInv = new HashMap<>();

				preparedStatement = this.getConnection()
						.prepareStatement("SELECT `id`,`number`,`language`,`questionText`,`image`, "
								+ " `footer`, `explanation`, `imageLine` FROM " + QUESTION_TABLE + ";");
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
						"SELECT `id`,`examName`,`language`,`description` FROM "
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
						.prepareStatement("SELECT `id`,`answerText`,`isTrue`,`position` FROM " + ANSWER_TABLE + ";");
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
		String out = ExamenVerwaltung.getText("data.loaded.from.database") + " " + cc + " " + ExamenVerwaltung.getText("exams") + ", " + ct + " "+ ExamenVerwaltung.getText("questions") + " " + ExamenVerwaltung.getText("and") + " " + cs
				+ " " + ExamenVerwaltung.getText("answers") + ".";
		ExamenVerwaltung.showMessage(ExamenVerwaltung.getText("load.success") + "\r\n\r\n" + out);
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
				ExamenVerwaltung.showErrorMessage("user.passwort.error");
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

	private boolean checkIds(IExam examCheck, long sqlExamId) {
		for (IQuestion question : ExamenVerwaltung.getQuestionList()) {
			int sqlQuestionId = -1;
			ExamItemAbstract q = (ExamItemAbstract) question;
			if (this.questionIds.containsKey(q.getId())) {
				sqlQuestionId = this.questionIds.get(q.getId());
			}
			if (sqlExamId == sqlQuestionId) {
				examCheck.addQuestion(question);
			}
		}
		// Always, return value for loadElement
		return true;
	}

	private boolean checkIds(IQuestion questionCheck) {
		for (IExam iExam : ExamenVerwaltung.getExamList()) {
			ExamItemAbstract itemAbstract = (ExamItemAbstract) iExam;
			int sqlExamId = 0, sqlQuestionId = -1;
			if (this.examIdsInv.containsKey(itemAbstract.getId())) {
				sqlExamId = this.examIdsInv.get(itemAbstract.getId());
				for (IQuestion iQuestion : Question.getQuestions()) {
					ExamItemAbstract questionAbstact = (ExamItemAbstract) iQuestion;
					if (this.examIdsInv.containsKey(questionAbstact.getId())) {
						sqlQuestionId = this.examIdsInv.get(questionAbstact.getId());
					}
					if (sqlExamId == sqlQuestionId) {
						iExam.addQuestion(questionCheck);
					}
				}
			}
		}
		// Always, return value for loadElement
		return true;
	}

	private boolean checkIds(IAnswer answer, int sqlExamId) {
		// int sqlStudentId = this.studentIds.get(((SchoolItemAbstract)
		// student).getId());
		for (IQuestion question : ExamenVerwaltung.getQuestionList()) {
			ExamItemAbstract qabstract = (ExamItemAbstract) question;
			if (this.examIdsInv.containsKey(qabstract.getId())) {
				int sqlAnswerId = this.examIdsInv.get(qabstract.getId());
				if (sqlExamId == sqlAnswerId) {
					question.addAnswer(answer);
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
			sql = "CREATE TABLE `" + EXAM_TABLE + "` (id INTEGER NOT NULL AUTO_INCREMENT, `examName` NVARCHAR(255), `language` NVARCHAR(255), "
					+ " `description` NVARCHAR(4096), primary key(id))";
			break;
		case 1:
			sql = "CREATE TABLE `" + QUESTION_TABLE + "` (id INTEGER NOT NULL AUTO_INCREMENT, `number` INTEGER, `language` NVARCHAR(255), `questionText` TEXT, `image` BLOB, `footer` NVARCHAR(255), "
					+ " `explanation` TEXT, `imageLine` INTEGER, primary key(id))";
			break;
		case 2:
			sql = "CREATE TABLE `" + ANSWER_TABLE + "` (id INTEGER NOT NULL AUTO_INCREMENT, `index` NVARCHAR(1), `answerText` TEXT, "
					+ " `isTrue` BOOL, `position` INTEGER, primary key(id))";
			break;
		case 3:
			sql = "CREATE TABLE `" + CATEGORY_TABLE + "` (id INTEGER NOT NULL AUTO_INCREMENT, `categoryName` NVARCHAR(255), `language` NVARCHAR(255), "
					+ " `description` NVARCHAR(4096), primary key(id))";		
			break;
		case 4:
			sql = "CREATE TABLE `" + CATEGORY_TABLE + "` (id INTEGER NOT NULL AUTO_INCREMENT, `categoryName` NVARCHAR(255), `language` NVARCHAR(255), "
					+ " `description` NVARCHAR(4096), primary key(id))";	
			break;
		case 5:
			sql = "CREATE TABLE `" + EXAM_QUESTION_MAP_TABLE + "` (`examId` INTEGER NOT NULL, `questionId` INTEGER NOT NULL)";	
			break;
		case 6:
			sql = "CREATE TABLE `" + QUESTION_ANSWER_MAP_TABLE + "` (`questionId` INTEGER NOT NULL, `answerId` INTEGER NOT NULL)";	
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
