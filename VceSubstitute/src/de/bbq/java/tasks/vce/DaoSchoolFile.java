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
public class DaoSchoolFile extends DaoSchoolAbstract {
	/////////////////////////////////////////////////////////////////////////////////////
	// Class Properties
	private File safeFile;
	private boolean occupied = false, loading = false;
	String out = "";
	int cc, ct, cs, ca;
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	/////////////////////////////////////////////////////////////////////////////////////

	/////////////////////////////////////////////////////////////////////////////////////
	// Construct
	public DaoSchoolFile() {
		super(EDaoSchool.FILE);
	}
	/////////////////////////////////////////////////////////////////////////////////////

	/////////////////////////////////////////////////////////////////////////////////////
	// Class Properties
	void chooseFile(boolean export) {
		JFileChooser fileChooser = new JFileChooser();
		int returnValue = -1;
		if (ExamenVerwaltung.getShell()) {
			if (export) {
				this.safeFile = new File(ExamenVerwaltung.showInput("Bitte die Datei zum exportieren auswählen"));
			} else {
				this.safeFile = new File(ExamenVerwaltung.showInput("Bitte die Datei zum importieren auswählen"));
			}
		} else {
			if (export) {
				fileChooser.setDialogTitle("Bitte die Datei zum exportieren auswählen");
				returnValue = fileChooser.showSaveDialog(null);
			} else {
				fileChooser.setDialogTitle("Bitte die Datei zum importieren auswählen");
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
			cc = ct = cs = ca = 0;
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
		if (schoolItemAbstract instanceof IQuestion) {
			cc++;
		} else if (schoolItemAbstract instanceof IQuestion) {
			ct++;
		} else if (schoolItemAbstract instanceof IAnswer) {
			cs++;
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
			out += "In Datei gekotzt: " + cc + " mal Dummgelaber, " + ct + " Labertaschen und " + cs + " Hohlköpfe.";
			if (deleted) {
				out += "\r\nVorhandene Datei gelöscht.";
			}
			ExamenVerwaltung.showMessage("Dreck fertig!\r\n\r\n" + out);
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
				cs++;
			}
			if (schoolItemAbstract instanceof Question) {
				Question.load((Question) schoolItemAbstract);
				System.out.println(((Question) schoolItemAbstract).toString());
				ct++;
			}
			if (schoolItemAbstract instanceof Question) {
				Question course = (Question) schoolItemAbstract;
				System.out.println(course.toString());
				Question.load(course);
				cc++;
			}
			// if (course.hasTeacher()) {
			// Teacher.load((Teacher) course.getTeacher());
			// ct++;
			// }
			// if (course.hasStudents()) {
			// for (int index = 0; index < course.getStudents().size(); index++)
			// {
			//// course.getStudents().get(index).setCourse(course);
			// // Student.load((Student) course.getStudents().get(index));
			// // cs++;
			// }
			// }
		}
		return true;
	}

	@Override
	public boolean loadAll() {
		boolean ret = false;
		cc = ct = cs = ca = 0;
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
				this.out = "Aus Datei gelutscht: " + cc + " mal Dummgelaber, " + ct + " Labertaschen und " + cs
						+ " Hohlköpfe.";
				ExamenVerwaltung.showMessage("Dreck fertig!\r\n\r\n" + out);
				try {
					ois.close();
				} catch (IOException e) {
					ExamenVerwaltung.showException(e);
					ret = false;
				}
			}
		}
		ExamenVerwaltung.setSelectedDao(EDaoSchool.FILE);
		this.safeFile = null;
		return ret;
	}

	@Override
	public boolean deleteElement(ExamItemAbstract schoolItemAbstract) {
		return true;
	}
	/////////////////////////////////////////////////////////////////////////////////////
}
