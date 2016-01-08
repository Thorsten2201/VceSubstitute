package de.bbq.java.tasks.vce;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class LoginDialog extends JDialog implements ActionListener {
	private static final long serialVersionUID = 1L;
	private final int labelWidth = 110;
	// private final int buttonWidth = 100;

	private JTextField username = new JTextField(), database = new JTextField();
	private JPasswordField password = new JPasswordField();
	private JButton saveButton, exitButton;
	private boolean succeeded;
	private DaoSchoolJdbcAbstract jdbcAbstract;
	private JPanel contentPane;

	public LoginDialog(Frame parent, DaoSchoolJdbcAbstract jdbcAbstract, String database, String username,
			String password) {
		super(parent, "JDBC Anmelden", true);
		this.jdbcAbstract = jdbcAbstract;
		addControls();
		this.database.setText(database);
		this.username.setText(username);
		this.password.setText(password);
		pack();
		setResizable(false);
		setLocationRelativeTo(parent);
	}

	public LoginDialog(Frame parent, DaoSchoolJdbcAbstract jdbcAbstract) {
		super(parent, "JDBC Anmelden", true);
		this.jdbcAbstract = jdbcAbstract;
		addControls();
		pack();
		setResizable(false);
		setLocationRelativeTo(parent);
	}

	private void addControls() {
		SpringLayout layout = new SpringLayout();

		JPanel topPanel = new JPanel();
		Container borderPane = this.getContentPane();
		borderPane.setLayout(new BorderLayout());
		borderPane.add(topPanel, BorderLayout.NORTH);

		contentPane = new JPanel();
		contentPane.setLayout(layout);
		contentPane.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

		// JPanel panel = new JPanel(new GridBagLayout());
		// GridBagConstraints cs = new GridBagConstraints();
		// cs.fill = GridBagConstraints.HORIZONTAL;

		JPanel labels = new JPanel();
		labels.setLayout(new BoxLayout(labels, BoxLayout.Y_AXIS));
		labels.setPreferredSize(new Dimension(labelWidth, 10));
		labels.setMaximumSize(new Dimension(labelWidth, Integer.MAX_VALUE));
		JPanel texts = new JPanel();
		texts.setLayout(new BoxLayout(texts, BoxLayout.Y_AXIS));

		addTextField(labels, texts, "dataBase", "Datenbank:", "", this.database, true);
		addTextField(labels, texts, "userName", "Username:", "", this.username, true);
		addTextField(labels, texts, "passWord", "Passwort:", "", this.password, true);

		contentPane.add(labels);
		contentPane.add(texts);

		// Adjust constraints for the label so it's at (5,5).
		SpringLayout.Constraints labelCons = layout.getConstraints(labels);
		labelCons.setX(Spring.constant(5));
		labelCons.setY(Spring.constant(5));

		// Adjust constraints for the text field so it's at
		// (<label's right edge> + 5, 5).
		SpringLayout.Constraints textFieldCons = layout.getConstraints(texts);
		textFieldCons.setX(Spring.sum(Spring.constant(5), labelCons.getConstraint(SpringLayout.EAST)));
		textFieldCons.setY(Spring.constant(5));

		JPanel bottomPanel = new JPanel();
		// bottomPanel.setLayout(null);
		bottomPanel.setAlignmentY(JComponent.CENTER_ALIGNMENT);

		saveButton = new JButton("Anmelden");
		saveButton.addActionListener(this); // new ActionListener() {
		exitButton = new JButton("Abbrechen");

		bottomPanel.add(this.exitButton);
		bottomPanel.add(this.saveButton);
		bottomPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
		bottomPanel.setMinimumSize(new Dimension(Integer.MAX_VALUE, 20));

		// Adjust constraints for the content pane.
		setContainerSize(contentPane, 5);
		this.getContentPane().add(contentPane, BorderLayout.CENTER);
		this.getContentPane().add(bottomPanel, BorderLayout.SOUTH);

		// this.winWidth = 343;
		// this.winHight = 318;

		this.exitButton.addActionListener(this);
		JPanel bp = new JPanel();
		bp.add(saveButton);
		bp.add(this.exitButton);

		getContentPane().add(contentPane, BorderLayout.CENTER);
		getContentPane().add(bp, BorderLayout.PAGE_END);
	}

	public String getUsername() {
		return username.getText().trim();
	}

	public String getDatabase() {
		return database.getText().trim();
	}

	public String getPassword() {
		return new String(password.getPassword());
	}

	public boolean isSucceeded() {
		return succeeded;
	}

	public void addTextField(JPanel labels, JPanel texts, String name, String text, String value, JTextField textField,
			boolean spacer) {
		addLabel(labels, text, 5);
		textField.setText(value);
		addComponent(texts, name, 19, textField);

		if (spacer) {
			labels.add(Box.createRigidArea(new Dimension(0, 5)));
			texts.add(Box.createVerticalStrut(5));
		}
	}

	public void addComponent(JPanel texts, String name, int height, JComponent component) {
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

	private void setContainerSize(Container parent, int pad) {
		SpringLayout layout = (SpringLayout) parent.getLayout();
		Component[] components = parent.getComponents();
		Spring maxHeightSpring = Spring.constant(0);
		SpringLayout.Constraints pCons = layout.getConstraints(parent);

		// Set the container's right edge to the right edge
		// of its rightmost component + padding.
		Component rightmost = components[components.length - 1];
		SpringLayout.Constraints rCons = layout.getConstraints(rightmost);
		pCons.setConstraint(SpringLayout.EAST,
				Spring.sum(Spring.constant(pad), rCons.getConstraint(SpringLayout.EAST)));

		// Set the container's bottom edge to the bottom edge
		// of its tallest component + padding.
		for (int i = 0; i < components.length; i++) {
			SpringLayout.Constraints cons = layout.getConstraints(components[i]);
			maxHeightSpring = Spring.max(maxHeightSpring, cons.getConstraint(SpringLayout.SOUTH));
		}
		pCons.setConstraint(SpringLayout.SOUTH, Spring.sum(Spring.constant(pad), maxHeightSpring));
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (arg0.getSource() == this.saveButton) {
			if (jdbcAbstract.connect(null, getDatabase(), getUsername(), getPassword())) {
				// JOptionPane.showMessageDialog(LoginDialog.this,
				// "Hallo " + getUsername() + "! Datenbankverbindung
				// hergestellt.", "JDBC Anmelden",
				// JOptionPane.INFORMATION_MESSAGE);
				succeeded = true;
				dispose();
			} else {
				// JOptionPane.showMessageDialog(LoginDialog.this, "Username
				// oder Passwort sind falsch.", "JDBC
				// Anmelden",JOptionPane.ERROR_MESSAGE);
				// reset username and password
				// database.setText("");
				// username.setText("");
				password.setText("");
				succeeded = false;
			}
		} else if (arg0.getSource() == this.exitButton) {
			{
				database.setText("");
				username.setText("");
				password.setText("");
				succeeded = false;
				this.dispose();
			}
		}
	}
}