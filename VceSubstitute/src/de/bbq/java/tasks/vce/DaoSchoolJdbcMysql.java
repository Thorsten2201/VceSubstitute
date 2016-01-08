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
	final String COURSE_TABLE = "course";
	final String TEACHER_TABLE = "teacher";
	final String STUDENT_TABLE = "student";

	HashMap<Integer, Long> courseIds = new HashMap<>();
	HashMap<Long, Integer> courseIdsInv = new HashMap<>();
	HashMap<Long, Integer> teacherIds = new HashMap<>();
	HashMap<Long, Integer> studentIds = new HashMap<>();

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
			if (schoolItemAbstract instanceof ISolution) {
				preparedStatement = this.getConnection()
						.prepareStatement("SELECT `id` FROM " + COURSE_TABLE + " WHERE (`id` = ?);");
				if (this.courseIdsInv.containsKey(schoolItemAbstract.getId())) {
					preparedStatement.setInt(1, this.courseIdsInv.get(schoolItemAbstract.getId()));
				} else {
					preparedStatement.setInt(1, -1);
				}
			} else if (schoolItemAbstract instanceof IQuestion) {
				preparedStatement = this.getConnection()
						.prepareStatement("SELECT `id` FROM " + TEACHER_TABLE + " WHERE (`id` = ?);");
				if (this.teacherIds.containsKey(schoolItemAbstract.getId())) {
					preparedStatement.setInt(1, this.teacherIds.get(schoolItemAbstract.getId()));
				} else {
					preparedStatement.setInt(1, -1);
				}
			} else if (schoolItemAbstract instanceof IAnswer) {
				preparedStatement = this.getConnection()
						.prepareStatement("SELECT `id` FROM " + STUDENT_TABLE + " WHERE (`id` = ?);");
				if (this.studentIds.containsKey(schoolItemAbstract.getId())) {
					preparedStatement.setInt(1, this.studentIds.get(schoolItemAbstract.getId()));
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
			if (schoolItemAbstract instanceof ISolution) {
				Solution course = (Solution) schoolItemAbstract;
				String sql = "UPDATE " + COURSE_TABLE
						+ " SET `teacherId` = ?, `courseName` = ?, `endTime` = ?, `language` = ?, `needsBeamer` = ?, `roomNumber` = ?, `startTime` = ?, `topic` = ? WHERE `id` = ?;";
				PreparedStatement statement = this.connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

				// the course had to pass elementExists() to get here, so
				// courseIdsInv contains the key
				int courseId = 0;
				if (this.courseIdsInv.containsKey(((ExamItemAbstract) course).getId())) {
					courseId = courseIdsInv.get(((ExamItemAbstract) course).getId());
				}
				if (!this.teacherIds.containsKey(((ExamItemAbstract) course.getQuestion()).getId())) {
					this.saveElement((ExamItemAbstract) course.getQuestion());
				}
				int teacherId = teacherIds.get(((ExamItemAbstract) course.getQuestion()).getId());

				statement.setInt(1, teacherId);
				statement.setString(2, course.getCourseName());
//				if (course.getEndTime() == null) {
//					statement.setNull(3, Types.DATE);
//				} else {
//					statement.setDate(3, new java.sql.Date(course.getEndTime().getTime()));
//				}
				statement.setString(4, course.getLanguage());
//				statement.setBoolean(5, course.getNeedsBeamer());
//				statement.setString(6, course.getRoomNumber());
//				if (course.getStartTime() == null) {
//					statement.setNull(7, Types.DATE);
//				} else {
//					statement.setDate(7, new java.sql.Date(course.getStartTime().getTime()));
//				}
//				statement.setString(8, course.getTopic());
				statement.setInt(9, courseId);

				int affectedRows = statement.executeUpdate();
				if (affectedRows == 0) {
					throw new SQLException("Kurs aktualisieren fehlgeschlagen user failed, keine Reihen beeinflusst.");
				}
			} else if (schoolItemAbstract instanceof IQuestion) {
				Question teacher = (Question) schoolItemAbstract;
				String sql = "UPDATE " + TEACHER_TABLE
						+ " SET `lastName` = ?,`firstName` = ?,`birthTime` = ?,`city` = ?, `country` = ?, `houseNumber` = ?, `streetName` = ?, `zipCode` = ? WHERE `id` = ?;";
				PreparedStatement statement = this.connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

//TODO:				statement.setString(1, teacher.getLastName());
//				statement.setString(2, teacher.getFirstName());
//				if (teacher.getBirthDate() == null) {
//					statement.setNull(3, Types.DATE);
//				} else {
//					statement.setDate(3, new java.sql.Date(teacher.getBirthDate().getTime()));
//				}
//				statement.setString(4, teacher.getAdress().getCity());
//				statement.setString(5, teacher.getAdress().getCountry());
//				statement.setString(6, teacher.getAdress().getHouseNumber());
//				statement.setString(7, teacher.getAdress().getStreetName());
//				statement.setInt(8, teacher.getAdress().getZipCode());

				// the teacher had to pass elementExists() to get here, so
				// teacherIds contains the key
				int teacherId = teacherIds.get(((ExamItemAbstract) teacher).getId());
				statement.setInt(9, teacherId);
				int affectedRows = statement.executeUpdate();
				if (affectedRows == 0) {
					throw new SQLException("Leerer anlegen fehlgeschlagen, keine Reihen beeinflusst.");
				}
			} else if (schoolItemAbstract instanceof IAnswer) {
				Answer student = (Answer) schoolItemAbstract;
				String sql = "UPDATE " + STUDENT_TABLE
						+ " SET `lastName` = ?,`firstName` = ?,`birthTime` = ?,`city` = ?, `country` = ?, `houseNumber` = ?, `streetName` = ?, `zipCode` = ? WHERE `id` = ?;";
				PreparedStatement statement = this.connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

//TODO:			statement.setString(1, student.getLastName());
//				statement.setString(2, student.getFirstName());
//				if (student.getBirthDate() == null) {
//					statement.setNull(3, Types.DATE);
//				} else {
//					statement.setDate(3, new java.sql.Date(student.getBirthDate().getTime()));
//				}
//				statement.setString(4, student.getAdress().getCity());
//				statement.setString(5, student.getAdress().getCountry());
//				statement.setString(6, student.getAdress().getHouseNumber());
//				statement.setString(7, student.getAdress().getStreetName());
//				statement.setInt(8, student.getAdress().getZipCode());

				// the student had to pass elementExists() to get here, so
				// studentIds contains the key
				int studentId = studentIds.get(((ExamItemAbstract) student).getId());
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
			if (schoolItemAbstract instanceof ISolution) {
				Solution course = (Solution) schoolItemAbstract;
				String sql = "INSERT INTO " + COURSE_TABLE
						+ " (`teacherId`, `courseName`, `endTime`, `language`, `needsBeamer`, `roomNumber`, `startTime`, `topic`) VALUES(?, ?, ?, ?, ?, ?, ?, ?);";
				PreparedStatement statement = this.connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				int teacherId = 0;
				if (course.hasQuestion()) {
					if (!this.teacherIds.containsKey(((ExamItemAbstract) course.getQuestion()).getId())) {
						this.saveElement((ExamItemAbstract) course.getQuestion()); //
					}
					teacherId = teacherIds.get(((ExamItemAbstract) course.getQuestion()).getId());
				}
				statement.setInt(1, teacherId);
				statement.setString(2, course.getCourseName());
//				if (course.getEndTime() == null) {
//					statement.setNull(3, Types.DATE);
//				} else {
//					statement.setDate(3, new java.sql.Date(course.getEndTime().getTime()));
//				}
				statement.setString(4, course.getLanguage());
//				statement.setBoolean(5, course.getNeedsBeamer());
//				statement.setString(6, course.getRoomNumber());
//				if (course.getStartTime() == null) {
//					statement.setNull(7, Types.DATE);
//				} else {
//					statement.setDate(7, new java.sql.Date(course.getStartTime().getTime()));
//				}
//				statement.setString(8, course.getTopic());

				int affectedRows = statement.executeUpdate();
				if (affectedRows == 0) {
					throw new SQLException("Kurs anlegen fehlgeschlagen user failed, keine Reihen beeinflusst.");
				}
				try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
					if (generatedKeys.next()) {
						courseIdsInv.put(schoolItemAbstract.getId(), generatedKeys.getInt(1));
					} else {
						throw new SQLException("Kurs anlegen fehlgeschlagen, keine ID bekommen.");
					}
				}
			} else if (schoolItemAbstract instanceof IQuestion) {
				Question teacher = (Question) schoolItemAbstract;
				String sql = "INSERT INTO " + TEACHER_TABLE
						+ " (`lastName`,`firstName`,`birthTime`,`city`, `country`, `houseNumber`, `streetName`, `zipCode`) VALUES(?, ?, ?, ?, ?, ?, ?, ?);";
				PreparedStatement statement = this.connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

//TODO:			statement.setString(1, teacher.getLastName());
//				statement.setString(2, teacher.getFirstName());
//				if (teacher.getBirthDate() == null) {
//					statement.setNull(3, Types.DATE);
//				} else {
//					statement.setDate(3, new java.sql.Date((teacher.getBirthDate().getTime())));
//				}
//				statement.setString(4, teacher.getAdress().getCity());
//				statement.setString(5, teacher.getAdress().getCountry());
//				statement.setString(6, teacher.getAdress().getHouseNumber());
//				statement.setString(7, teacher.getAdress().getStreetName());
//				statement.setInt(8, teacher.getAdress().getZipCode());

				int affectedRows = statement.executeUpdate();
				if (affectedRows == 0) {
					throw new SQLException("Leerer anlegen fehlgeschlagen, keine Reihen beeinflusst.");
				}
				try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
					if (generatedKeys.next()) {
						teacherIds.put(schoolItemAbstract.getId(), generatedKeys.getInt(1));
					} else {
						throw new SQLException("Leerer anlegen fehlgeschlagen, keine ID bekommen.");
					}
				}
			} else if (schoolItemAbstract instanceof IAnswer) {
				Answer student = (Answer) schoolItemAbstract;
				String sql = "INSERT INTO " + STUDENT_TABLE
						+ " (`courseId`,`lastName`,`firstName`,`birthTime`,`city`, `country`, `houseNumber`, `streetName`, `zipCode`) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?);";
				PreparedStatement statement = this.connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

				int courseId = 0;
				if (student.hasQuestion()) {
					if (!this.courseIdsInv.containsKey(((ExamItemAbstract) student.getSolution()).getId())) {
						this.saveElement((ExamItemAbstract) student.getSolution());
					}
					courseId = this.courseIdsInv.get(((ExamItemAbstract) student.getSolution()).getId());
				}
				statement.setInt(1, courseId);
//TODO:			statement.setString(2, student.getLastName());
//				statement.setString(3, student.getFirstName());
//				if (student.getBirthDate() == null) {
//					statement.setNull(4, Types.DATE);
//				} else {
//					statement.setDate(4, new java.sql.Date(student.getBirthDate().getTime()));
//				}
//				statement.setString(5, student.getAdress().getCity());
//				statement.setString(6, student.getAdress().getCountry());
//				statement.setString(7, student.getAdress().getHouseNumber());
//				statement.setString(8, student.getAdress().getStreetName());
//				statement.setInt(9, student.getAdress().getZipCode());

				int affectedRows = statement.executeUpdate();
				if (affectedRows == 0) {
					throw new SQLException("Leerer anlegen fehlgeschlagen, keine Reihen beeinflusst.");
				}
				try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
					if (generatedKeys.next()) {
						this.studentIds.put(schoolItemAbstract.getId(), generatedKeys.getInt(1));
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
			if (schoolItemAbstract instanceof ISolution) {
				preparedStatement = this.getConnection()
						.prepareStatement("DELETE FROM " + COURSE_TABLE + " WHERE (`id` = ?);");
				if (this.courseIdsInv.containsKey(schoolItemAbstract.getId())) {
					preparedStatement.setInt(1, this.courseIdsInv.get(schoolItemAbstract.getId()));
				} else {
					preparedStatement.setInt(1, -1);
				}
			} else if (schoolItemAbstract instanceof IQuestion) {
				preparedStatement = this.getConnection()
						.prepareStatement("DELETE FROM " + TEACHER_TABLE + " WHERE (`id` = ?);");
				if (this.teacherIds.containsKey(schoolItemAbstract.getId())) {
					preparedStatement.setInt(1, this.teacherIds.get(schoolItemAbstract.getId()));
				} else {
					preparedStatement.setInt(1, -1);
				}
			} else if (schoolItemAbstract instanceof IAnswer) {
				preparedStatement = this.getConnection()
						.prepareStatement("DELETE FROM " + STUDENT_TABLE + " WHERE (`id` = ?);");
				if (this.studentIds.containsKey(schoolItemAbstract.getId())) {
					preparedStatement.setInt(1, this.studentIds.get(schoolItemAbstract.getId()));
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
			if (schoolItemAbstract instanceof ISolution) {
				Solution sqlCourse = (Solution) schoolItemAbstract;
				courseIds.put(resultSet.getInt(1), sqlCourse.getId());
				courseIdsInv.put(sqlCourse.getId(), resultSet.getInt(1));
				int sqlTeacherId = resultSet.getInt(2);
				sqlCourse.setCourseName(resultSet.getString(3));
//				sqlCourse.setEndTime(resultSet.getDate(4));
				sqlCourse.setLanguage(resultSet.getString(5));
//				sqlCourse.setNeedsBeamer(resultSet.getBoolean(6));
//				sqlCourse.setRoomNumber(resultSet.getString(7));
//				sqlCourse.setStartTime(resultSet.getDate(8));
//				sqlCourse.setTopic(resultSet.getString(9));
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
				teacherIds.put(sqlTeacher.getId(), teacherId);
//TODO:			sqlTeacher.setLastName(resultSet.getString(2));
//				sqlTeacher.setFirstName(resultSet.getString(3));
//				sqlTeacher.setBirthDate(resultSet.getDate(4));
//				sqlTeacher.getAdress().setCity(resultSet.getString(5));
//				sqlTeacher.getAdress().setCountry(resultSet.getString(6));
//				sqlTeacher.getAdress().setHouseNumber(resultSet.getString(7));
//				sqlTeacher.getAdress().setStreetName(resultSet.getString(8));
//				sqlTeacher.getAdress().setZipCode(resultSet.getInt(9));
				return checkIds(sqlTeacher);
			} else if (schoolItemAbstract instanceof IAnswer) {
				Answer sqlStudent = (Answer) schoolItemAbstract;
				int studentId = resultSet.getInt(1);
				int courseId = resultSet.getInt(2);
				studentIds.put(sqlStudent.getId(), studentId);
//TODO:			sqlStudent.setLastName(resultSet.getString(3));
//				sqlStudent.setFirstName(resultSet.getString(4));
//				sqlStudent.setBirthDate(resultSet.getDate(5));
//				sqlStudent.getAdress().setCity(resultSet.getString(6));
//				sqlStudent.getAdress().setCountry(resultSet.getString(7));
//				sqlStudent.getAdress().setHouseNumber(resultSet.getString(8));
//				sqlStudent.getAdress().setStreetName(resultSet.getString(9));
//				sqlStudent.getAdress().setZipCode(resultSet.getInt(10));
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

				courseIds = new HashMap<>();
				courseIdsInv = new HashMap<>();
				teacherIds = new HashMap<>();
				studentIds = new HashMap<>();

				preparedStatement = this.getConnection()
						.prepareStatement("SELECT `id`,`lastName`,`firstName`,`birthTime`,`city`, "
								+ " `country`, `houseNumber`, `streetName`, `zipCode` FROM " + TEACHER_TABLE + ";");
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
								+ COURSE_TABLE + ";");
				this.resultSet = preparedStatement.executeQuery();
				while (this.resultSet.next()) {
					ExamItemAbstract loadItem = (ExamItemAbstract) ExamenVerwaltung.getNewSolution(true);
					if (this.loadElement(loadItem)) {
						cc++;
					} else {
						ExamenVerwaltung.deleteElement(loadItem);
					}
				}

				preparedStatement = this.getConnection()
						.prepareStatement("SELECT `id`,`courseId`,`lastName`,`firstName`,`birthTime`,`city`, "
								+ " `country`, `houseNumber`, `streetName`, `zipCode` FROM " + STUDENT_TABLE + ";");
				resultSet = preparedStatement.executeQuery();
				while (resultSet.next()) {
					ExamItemAbstract loadItem = (ExamItemAbstract) ExamenVerwaltung.getNewStudent(true);
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
			if (rs.getString(3).compareTo(COURSE_TABLE) == 0) {
				foundCourse = true;
			}
			if (rs.getString(3).compareTo(TEACHER_TABLE) == 0) {
				foundTeacher = true;
			}
			if (rs.getString(3).compareTo(STUDENT_TABLE) == 0) {
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

	private boolean checkIds(ISolution course, int sqlCoursTeacherId) {
		for (IQuestion teacher : ExamenVerwaltung.getQuestionList()) {
			int sqlTeacherId = -1;
			ExamItemAbstract t = (ExamItemAbstract) teacher;
			if (this.teacherIds.containsKey(t.getId())) {
				sqlTeacherId = this.teacherIds.get(t.getId());
			}
			if (sqlCoursTeacherId == sqlTeacherId) {
				course.setQuestion(teacher);
				try {
					teacher.addSolution(course);
				} catch (Exception e) {
					ExamenVerwaltung.showException(e);
				}
			}
		}
		// Always, return value for loadElement
		return true;
	}

	private boolean checkIds(IQuestion teacher) {
		for (ISolution course : ExamenVerwaltung.getSolutionList()) {
			ExamItemAbstract c = (ExamItemAbstract) course;
			int sqlCourseId = 0, sqlTeacherId = -1;
			if (this.courseIdsInv.containsKey(c.getId())) {
				sqlCourseId = this.courseIdsInv.get(c.getId());
				for (ISolution teacherCourse : Solution.getCourses()) {
					ExamItemAbstract tc = (ExamItemAbstract) teacherCourse;
					if (this.courseIdsInv.containsKey(tc.getId())) {
						sqlTeacherId = this.courseIdsInv.get(tc.getId());
					}
					if (sqlCourseId == sqlTeacherId) {
						course.setQuestion(teacher);
						try {
							teacher.addSolution(course);
						} catch (Exception e) {
							ExamenVerwaltung.showException(e);
						}
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
		for (ISolution course : ExamenVerwaltung.getSolutionList()) {
			ExamItemAbstract sac = (ExamItemAbstract) course;
			if (this.courseIdsInv.containsKey(sac.getId())) {
				int sqlStudentId = this.courseIdsInv.get(sac.getId());
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
