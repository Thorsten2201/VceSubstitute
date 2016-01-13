package de.bbq.java.tasks.vce;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.swing.JFileChooser;

/**
 * @author Thorsten2201
 *
 */
public class DaoExamFile extends DaoExamAbstract {
	/////////////////////////////////////////////////////////////////////////////////////
	// Class Properties
	private File safeFile;
	private boolean occupied = false, loading = false;
	String out = "";
	int countExams, countQuestions, countAnswers;
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	/////////////////////////////////////////////////////////////////////////////////////

	/////////////////////////////////////////////////////////////////////////////////////
	// Construct
	public DaoExamFile() {
		super(EDaoExam.FILE);
	}
	/////////////////////////////////////////////////////////////////////////////////////

	/////////////////////////////////////////////////////////////////////////////////////
	// Class Properties
	void chooseFile(boolean export) {
		JFileChooser fileChooser = new JFileChooser();
		int returnValue = -1;
		if (ExamenVerwaltung.getShell()) {
			if (export) {
				this.safeFile = new File(ExamenVerwaltung.showInput("select.file.export"));
			} else {
				this.safeFile = new File(ExamenVerwaltung.showInput("select.file.import"));
			}
		} else {
			if (export) {
				fileChooser.setDialogTitle(ExamenVerwaltung.getText("select.file.export"));
				returnValue = fileChooser.showSaveDialog(null);
			} else {
				fileChooser.setDialogTitle(ExamenVerwaltung.getText("select.file.import"));
				returnValue = fileChooser.showOpenDialog(null);
			}
			if (returnValue == JFileChooser.APPROVE_OPTION) {
				this.safeFile = fileChooser.getSelectedFile();
			}
		}
		System.out.println(this.safeFile.getName());
	}
	/////////////////////////////////////////////////////////////////////////////////////

	/////////////////////////////////////////////////////////////////////////////////////
	// DaoSchoolAbstract properties
	@Override
	public boolean saveElement(ExamItemAbstract schoolItemAbstract) {
		boolean ret = false;
		boolean deleted = false;
		// 4sql
		if (schoolItemAbstract.isInEdit()) {
			return true;
		}
		if (!this.occupied) {
			this.occupied = true;
			out = "";
			countExams = countQuestions = countAnswers = 0;
			ret = true;
			if (this.safeFile == null) {
				chooseFile(true);
			}
			if (this.safeFile != null) {
				try {
					oos = new ObjectOutputStream(new FileOutputStream(this.safeFile, false));
				} catch (FileNotFoundException e) {
					ExamenVerwaltung.showException(e);
				} catch (IOException e) {
					ExamenVerwaltung.showException(e);
				}
			}
		}
		if (schoolItemAbstract instanceof IExam) {
			countExams++;
		} else if (schoolItemAbstract instanceof IQuestion) {
			countQuestions++;
		} else if (schoolItemAbstract instanceof IAnswer) {
			countAnswers++;
		}
		try {
			oos.writeObject(schoolItemAbstract);
			schoolItemAbstract.setSaved(true);
			ret = true;
		} catch (IOException e) {
			ExamenVerwaltung.showException(e);
			ret = false;
		}
		if (schoolItemAbstract.isLast()) {
			this.occupied = false;
			out += ExamenVerwaltung.getText("saved.to.file") + " " + countExams + " " + ExamenVerwaltung.getText("exams") + ", " + countQuestions + " " + ExamenVerwaltung.getText("questions") + " " + ExamenVerwaltung.getText("and") + " " + countAnswers + " " + ExamenVerwaltung.getText("answers") + ".";
			if (deleted) {
				out += "\r\n" + ExamenVerwaltung.getText("existing.file.deleted");
			}
			ExamenVerwaltung.showMessage(ExamenVerwaltung.getText("ready") + "\r\n\r\n" + out);
			try {
				oos.close();
			} catch (IOException e) {
				ExamenVerwaltung.showException(e);
				ret = false;
			}
			this.safeFile = null;
		}
		return ret;
	}

	@Override
	public boolean loadElement(ExamItemAbstract schoolItemAbstract) {
		if (loading) {
			if (schoolItemAbstract instanceof Answer) {
				Answer.load((Answer) schoolItemAbstract);
				System.out.println(((Answer) schoolItemAbstract).toString());
				countAnswers++;
			}
			if (schoolItemAbstract instanceof Question) {
				Question.load((Question) schoolItemAbstract);
				System.out.println(((Question) schoolItemAbstract).toString());
				countQuestions++;
			}
			if (schoolItemAbstract instanceof Exam) {
				Exam exam = (Exam) schoolItemAbstract;
				System.out.println(exam.toString());
				Exam.load(exam);
				countExams++;
			}
		}
		return true;
	}

	@Override
	public boolean loadAll() {
		boolean ret = false;
		countExams = countQuestions = countAnswers = 0;
		chooseFile(false);
		if (this.safeFile != null) {
			try {
				this.ois = new ObjectInputStream(new FileInputStream(this.safeFile));
			} catch (FileNotFoundException e) {
				ExamenVerwaltung.showException(e);
			} catch (IOException e) {
				ExamenVerwaltung.showException(e);
			}
			if (this.ois != null) {
				Object read = null;
				try {
					read = this.ois.readObject();
				} catch (ClassNotFoundException e) {
					ExamenVerwaltung.showException(e);
				} catch (IOException e) {
					// We're done
					read = null;
				}
				ret = true;
				this.loading = true;
				while (read != null) {
					ret &= this.loadElement((ExamItemAbstract) read);
					read = null;
					try {
						read = ois.readObject();
					} catch (ClassNotFoundException e) {
						ExamenVerwaltung.showException(e);
						ret = false;
					} catch (IOException e) {
						// We're done
					}
				}
				this.loading = false;
				this.out = ExamenVerwaltung.getText("loaded.from.file") + " " + countExams + " " + ExamenVerwaltung.getText("exams") + ", " + countQuestions + " " +  ExamenVerwaltung.getText("questions") + " " + ExamenVerwaltung.getText("and") + " " + countAnswers
						+ " " + ExamenVerwaltung.getText("answers")  + ".";
				ExamenVerwaltung.showMessage(ExamenVerwaltung.getText("ready") + "\r\n\r\n" + out);
				try {
					ois.close();
				} catch (IOException e) {
					ExamenVerwaltung.showException(e);
					ret = false;
				}
			}
		}
		ExamenVerwaltung.setSelectedDao(EDaoExam.FILE);
		this.safeFile = null;
		return ret;
	}

	@Override
	public boolean deleteElement(ExamItemAbstract schoolItemAbstract) {
		return true;
	}
	/////////////////////////////////////////////////////////////////////////////////////
}
