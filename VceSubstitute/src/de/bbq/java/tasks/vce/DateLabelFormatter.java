package de.bbq.java.tasks.vce;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.JFormattedTextField.AbstractFormatter;

/**
 * @author Thorsten2201
 *
 */
public class DateLabelFormatter extends AbstractFormatter {
	/////////////////////////////////////////////////////////////////////////////////////
	// Static
	private static final long serialVersionUID = -1393801674470791396L;
	/////////////////////////////////////////////////////////////////////////////////////

	/////////////////////////////////////////////////////////////////////////////////////
	// Construct
	private final String datePattern = "dd.MM.yyyy"; // hh:mm:ss
	private SimpleDateFormat dateFormatter = new SimpleDateFormat(datePattern);
	/////////////////////////////////////////////////////////////////////////////////////

	/////////////////////////////////////////////////////////////////////////////////////
	// AbstractFormatter
	@Override
	public Object stringToValue(String text) throws ParseException {
		return dateFormatter.parseObject(text);
	}

	@Override
	public String valueToString(Object value) throws ParseException {
		if (value != null) {
			Calendar cal = (Calendar) value;
			return dateFormatter.format(cal.getTime());
		}

		return "";
	}
	/////////////////////////////////////////////////////////////////////////////////////

}