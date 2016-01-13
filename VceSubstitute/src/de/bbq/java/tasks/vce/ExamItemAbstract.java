package de.bbq.java.tasks.vce;

import java.io.Serializable;

/**
 * @author Thorsten2201
 *
 */
public abstract class ExamItemAbstract implements Serializable {

	/////////////////////////////////////////////////////////////////////////////////////
	// Class Properties
	protected transient long id;
	private transient boolean isInEdit;
	private transient boolean isLastObject;
	private transient boolean saved;
	private transient String name;
	protected static DaoExamAbstract dataAccessObject;
	/////////////////////////////////////////////////////////////////////////////////////

	/////////////////////////////////////////////////////////////////////////////////////
	// Construct
	protected static long getNewId() {
		return ExamItemAbstract.highestMemberId++;
	}

	public ExamItemAbstract(EDaoExam eDataAccess) throws Exception {
		id = ExamItemAbstract.highestMemberId++;
		DaoExamAbstract accessObject = null;
		try {
			accessObject = DaoExamAbstract.getDaoExam(eDataAccess);
		} catch (Exception e) {
			throw e;
		}
		if (accessObject == null)
			throw new Exception("Datenzugriffs-Objekt \"" + eDataAccess.name() + "\2 nicht gefunden.");

		if (dataAccessObject != null && accessObject != null) {
			if (!dataAccessObject.getClass().equals(accessObject.getClass())) {
				dataAccessObject = accessObject;
			}
		} else if (dataAccessObject == null) {
			dataAccessObject = accessObject;
		}
	}
	/////////////////////////////////////////////////////////////////////////////////////

	/////////////////////////////////////////////////////////////////////////////////////
	// Static
	private static final long serialVersionUID = -1582579411217913540L;
	protected static long highestMemberId = 1000;
	/////////////////////////////////////////////////////////////////////////////////////

	/////////////////////////////////////////////////////////////////////////////////////
	// Getter / Setter
	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isInEdit() {
		return isInEdit;
	}

	public void setInEdit(boolean isInEdit) {
		this.isInEdit = isInEdit;
	}

	public boolean isSaved() {
		return this.saved;
	}

	public void setSaved(boolean saved) {
		this.saved = saved;
	}

	protected boolean isLast() {
		return this.isLastObject;
	}

	public void setLast(boolean isLast) {
		this.isLastObject = isLast;
	}

	public abstract String getDescription();
	/////////////////////////////////////////////////////////////////////////////////////

}
