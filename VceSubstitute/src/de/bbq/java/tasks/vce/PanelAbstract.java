package de.bbq.java.tasks.vce;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import javax.swing.*;

import java.awt.*;

import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

/**
 * @author Thorsten2201
 *
 */
public abstract class PanelAbstract extends JPanel implements ActionListener {
	// ///////////////////////////////////////////////////////////////////////////////////
	// Static
	private static final long serialVersionUID = -7952586514775627163L;
	// ///////////////////////////////////////////////////////////////////////////////////

	// ///////////////////////////////////////////////////////////////////////////////////
	// Construct
	private int winWidth = 343;
	private int winHight = 400;
	private final int labelWidth = 110;
	private final int buttonWidth = 100;
	// ///////////////////////////////////////////////////////////////////////////////////

	// ///////////////////////////////////////////////////////////////////////////////////
	// Controls
	private JPanel contentPane;
	private ExamItemAbstract editItem = null;
	private JButton exitButton, saveButton = new JButton();
	private JTextField lastNameTextField;
	private JLabel lastNameLabel;
	// ///////////////////////////////////////////////////////////////////////////////////

	// ///////////////////////////////////////////////////////////////////////////////////
	// ActionListener
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == this.exitButton) {
	//TODO
		} else if (e.getSource() == this.saveButton) {
			//TODO		
		}
	}

	// ///////////////////////////////////////////////////////////////////////////////////

	public PanelAbstract() {
		setOpaque(true);
		super.setBackground(Color.GREEN);
		SpringLayout layout = new SpringLayout();
//		setLayout(layout);
//		setLayout(new BorderLayout());
//		PanelEdit2 a = new PanelEdit2();
//		this.add(a,BorderLayout.EAST);
//		a.setBounds(200, 100, 300, 200);
//		layout.putConstraint(SpringLayout.EAST, a, 10, SpringLayout.EAST, this);
		
		


		contentPane = new JPanel();
		contentPane.setLayout(layout);
		contentPane.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		
		// Lay out the panel.
		Container borderPane = this.contentPane;
		borderPane.setLayout(new BorderLayout());
//		borderPane.add(topPanel, BorderLayout.NORTH);
	}

	// ///////////////////////////////////////////////////////////////////////////////////
	// Write control[].value to Object
	public abstract void ReadData(ExamItemAbstract item);
	// ///////////////////////////////////////////////////////////////////////////////////

	// ///////////////////////////////////////////////////////////////////////////////////
	// Construct
	void Construct(ExamItemAbstract item, JPanel labels, JPanel texts) {
		 addTextField(labels, texts, "firstNameTextField", "Nachname:",
		 "lastnameSimulated", this.lastNameTextField,
		 true);
	
	}

	void PanelEdit(IQuestion editItem) { // ,WindowListener windowListener) {
		// this.addWindowListener(windowListener);
		this.editItem = (Question) editItem;
		this.editItem.setInEdit(true);
		ExamenVerwaltung.verifyData(this.editItem);

		SpringLayout layout = new SpringLayout();
		setupSpringLayout(
				this.editItem.getId() + " " + this.editItem.toString(), 20,
				layout);

		JPanel labels = new JPanel();
		labels.setLayout(new BoxLayout(labels, BoxLayout.Y_AXIS));
		labels.setPreferredSize(new Dimension(labelWidth, 10));
		labels.setMaximumSize(new Dimension(labelWidth, Integer.MAX_VALUE));
		JPanel texts = new JPanel();
		texts.setLayout(new BoxLayout(texts, BoxLayout.Y_AXIS));

		Construct(this.editItem, labels, texts);

		contentPane.add(labels);
		contentPane.add(texts);

		// Adjust constraints for the label so it's at (5,5).
		SpringLayout.Constraints labelCons = layout.getConstraints(labels);
		labelCons.setX(Spring.constant(5));
		labelCons.setY(Spring.constant(5));

		// Adjust constraints for the text field so it's at
		// (<label's right edge> + 5, 5).
		SpringLayout.Constraints textFieldCons = layout.getConstraints(texts);
		textFieldCons.setX(Spring.sum(Spring.constant(5),
				labelCons.getConstraint(SpringLayout.EAST)));
		textFieldCons.setY(Spring.constant(5));

		JPanel bottomPanel = new JPanel();
		// bottomPanel.setLayout(null);
		bottomPanel.setAlignmentY(JComponent.CENTER_ALIGNMENT);

		this.exitButton = ExamenVerwaltung.getButton("Abbrechen", 110, 235,
				100, 20, this, "Abbrechen",
				"Nichts wie raus hier, Leerer stinken...");
		// add(this.exitButton);
		this.saveButton = ExamenVerwaltung.getButton("Speichern", 230, 235,
				100, 20, this, "Speichern",
				"Den Leerer fein abspeichern damit auch nichts verloren geht");
		// add(this.saveButton);
		bottomPanel.add(this.exitButton);
		bottomPanel.add(this.saveButton);
		bottomPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
		bottomPanel.setMinimumSize(new Dimension(Integer.MAX_VALUE, 20));

		// Adjust constraints for the content pane.
		setContainerSize(contentPane, 5);
		this.contentPane.add(contentPane, BorderLayout.CENTER);
		this.contentPane.add(bottomPanel, BorderLayout.SOUTH);

		this.winWidth = 343;
		this.winHight = 318;
	}

//	FrameEdit(IAnswer editItem) { // ,WindowListener windowListener) {
//		// this.addWindowListener(windowListener);
//		this.studentDF = (Answer) editItem;
//		this.studentDF.setInEdit(true);
//		ExamenVerwaltung.verifyData(this.studentDF);
//		setTitle("Schüler editieren");
//		SpringLayout layout = new SpringLayout();
//		setupSpringLayout(
//				this.studentDF.getId() + " " + this.studentDF.toString(), 20,
//				layout);
//
//		JPanel labels = new JPanel();
//		labels.setLayout(new BoxLayout(labels, BoxLayout.Y_AXIS));
//		labels.setPreferredSize(new Dimension(labelWidth, 10));
//		labels.setMaximumSize(new Dimension(labelWidth, Integer.MAX_VALUE));
//		JPanel texts = new JPanel();
//		texts.setLayout(new BoxLayout(texts, BoxLayout.Y_AXIS));
//
//		Construct(this.studentDF, labels, texts);
//
//		contentPane.add(labels);
//		contentPane.add(texts);
//
//		// Adjust constraints for the label so it's at (5,5).
//		SpringLayout.Constraints labelCons = layout.getConstraints(labels);
//		labelCons.setX(Spring.constant(5));
//		labelCons.setY(Spring.constant(5));
//
//		// Adjust constraints for the text field so it's at
//		// (<label's right edge> + 5, 5).
//		SpringLayout.Constraints textFieldCons = layout.getConstraints(texts);
//		textFieldCons.setX(Spring.sum(Spring.constant(0),
//				labelCons.getConstraint(SpringLayout.EAST)));
//		textFieldCons.setY(Spring.constant(5));
//
//		JPanel bottomPanel = new JPanel();
//		// bottomPanel.setLayout(null);
//		bottomPanel.setAlignmentY(JComponent.CENTER_ALIGNMENT);
//
//		this.exitButton = ExamenVerwaltung.getButton("Abbrechen", 110, 235,
//				100, 20, this, "Abbrechen",
//				"Nichts wie raus hier, Schüler nerven...");
//		// add(this.exitButton);
//		this.saveButton = ExamenVerwaltung
//				.getButton("Speichern", 230, 235, 100, 20, this, "Speichern",
//						"Schüler fein abspeichern damit er auch nichts über sich vergissst");
//		// add(this.saveButton);
//		bottomPanel.add(this.exitButton);
//		bottomPanel.add(this.saveButton);
//		bottomPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
//		bottomPanel.setMinimumSize(new Dimension(Integer.MAX_VALUE, 20));
//
//		// Adjust constraints for the content pane.
//		setContainerSize(contentPane, 5);
//		this.getContentPane().add(contentPane, BorderLayout.CENTER);
//		this.getContentPane().add(bottomPanel, BorderLayout.SOUTH);
//
//		this.winWidth = 343;
//		this.winHight = 318;
//		Construct();
//	}

	// ///////////////////////////////////////////////////////////////////////////////////

	public boolean hastItem(ExamItemAbstract otherItem) {
		boolean ret = false;
		if (otherItem != null) {
			ret |= otherItem.equals(this.editItem);
		}
		return ret;
	}

	public ExamItemAbstract getItem() {
		if (this.editItem != null) {
			return editItem;
		} 
		return null;
	}

	private void setContainerSize(Container parent, int pad) {
		SpringLayout layout = (SpringLayout) parent.getLayout();
		Component[] components = parent.getComponents();
		Spring maxHeightSpring = Spring.constant(0);
		SpringLayout.Constraints pCons = layout.getConstraints(parent);

		// Set the container's right edge to the right edge
		// of its rightmost component + padding.
		Component rightmost = components[components.length - 1];
		SpringLayout.Constraints rCons = layout.getConstraints(rightmost);
		pCons.setConstraint(
				SpringLayout.EAST,
				Spring.sum(Spring.constant(pad),
						rCons.getConstraint(SpringLayout.EAST)));

		// Set the container's bottom edge to the bottom edge
		// of its tallest component + padding.
		for (int i = 0; i < components.length; i++) {
			SpringLayout.Constraints cons = layout
					.getConstraints(components[i]);
			maxHeightSpring = Spring.max(maxHeightSpring,
					cons.getConstraint(SpringLayout.SOUTH));
		}
		pCons.setConstraint(SpringLayout.SOUTH,
				Spring.sum(Spring.constant(pad), maxHeightSpring));
	}

	void setupSpringLayout(String headline, int height, SpringLayout layout) {
		JPanel topPanel = new JPanel();
		JLabel label = new JLabel(headline);
		// label.setBounds(110, 5, 100, 20);
		topPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, height));
		topPanel.setMinimumSize(new Dimension(Integer.MAX_VALUE, height));
		topPanel.setAlignmentY(JComponent.CENTER_ALIGNMENT);
		topPanel.add(label);


	}

	public void addCheckBox(JPanel labels, JPanel texts, String name,
			String text, Boolean value, int height, JCheckBox checkBox,
			boolean spacer) {
		addLabel(labels, text, height);

		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, height));
		panel.setMinimumSize(new Dimension(Integer.MAX_VALUE, height));

		checkBox.setSelected((boolean) value);
		panel.add(checkBox);
		panel.setAlignmentY(JComponent.LEFT_ALIGNMENT);
		texts.add(panel);

		if (spacer) {
			labels.add(Box.createRigidArea(new Dimension(0, 5)));
			texts.add(Box.createVerticalStrut(5));
		}
	}

	public void addTextField(JPanel labels, JPanel texts, String name,
			String text, String value, JTextField textField, boolean spacer) {
		addLabel(labels, text, 5);
		textField.setText(value);
		addComponent(texts, name, 19, textField);

		if (spacer) {
			labels.add(Box.createRigidArea(new Dimension(0, 5)));
			texts.add(Box.createVerticalStrut(5));
		}
	}

	public void addComponent(JPanel texts, String name, int height,
			JComponent component) {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, height));
		panel.setMinimumSize(new Dimension(Integer.MAX_VALUE, height));

		component.setName(name);
		component.setMaximumSize(new Dimension(Integer.MAX_VALUE, height));
		panel.add(component);
		texts.add(panel);
	}

	public void addLabel(JPanel labels, String text, int height) {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());

		JLabel label = new JLabel(text);
		label.setMaximumSize(new Dimension(labelWidth, height + 10));
		label.setMinimumSize(new Dimension(labelWidth, height + 10));
		panel.add(label);

		labels.add(panel);
	}

	public abstract void saveData();

}
