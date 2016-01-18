package de.bbq.java.tasks.vce;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import javafx.scene.control.SelectionModel;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

/**
 * @author Thorsten2201
 *
 */
public class PanelExam extends JPanel implements ActionListener, TreeSelectionListener {
	// ///////////////////////////////////////////////////////////////////////////////////
	// Static
	private static final long serialVersionUID = -6951335589393103017L;
	// ///////////////////////////////////////////////////////////////////////////////////

	// ///////////////////////////////////////////////////////////////////////////////////
	// Class Properties
	private boolean refresh = true;
	private IQuestion selectedQuestion;
	// ///////////////////////////////////////////////////////////////////////////////////

	// ///////////////////////////////////////////////////////////////////////////////////
	// Controls
	private JButton addExamButton, deleteQuestionButton, addSolutionButton, removeSolutionButton;

	private JTree examJTree;
	private DefaultMutableTreeNode root;

	private int labelWidth = 200;
	public PanelEdit panelEdit;
	// ///////////////////////////////////////////////////////////////////////////////////

	// ///////////////////////////////////////////////////////////////////////////////////
	// Class methods
	private ExamItemAbstract getSelectedItem() {
		TreePath selPath = examJTree.getSelectionModel().getSelectionPath();
		DefaultMutableTreeNode selNode = getNodeFromPath(selPath);
		if (selNode != null) {
			if (selNode.getUserObject() instanceof Exam) {
				return (ExamItemAbstract) selNode.getUserObject();
			} else if (selNode.getUserObject() instanceof Question) {
				return (ExamItemAbstract) selNode.getUserObject();
			} else if (selNode.getUserObject() instanceof Answer) {
				return (ExamItemAbstract) selNode.getUserObject();
			}
		}
		return null;
	}

	public void refresh() {
		this.refresh = true;
		//
		// for (int index = this.questionListModel.getSize(); index > 0;
		// index--) {
		// try {
		// qindex = this.questionListModel.getElementAt(index - 1);
		// } catch (Exception e) {
		// ExamenVerwaltung.showException(e);
		// }
		//
		// if (!ExamenVerwaltung.getQuestionList().contains(qindex)) {
		// try {
		// this.questionListModel.remove(index - 1);
		// } catch (Exception e) {
		// ExamenVerwaltung.showException(e);
		// }
		//
		// }
		// }

		for (Iterator<IExam> iterExam = ExamenVerwaltung.getExamList().iterator(); iterExam.hasNext();) {
			IExam exam = (IExam) iterExam.next();
			TreePath examPath = findNode(exam);
			if (examPath == null) {
				DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(exam);
				newNode.setUserObject(exam);
				int childCount = this.root.getChildCount();
				this.root.insert(newNode, childCount);
				examPath = findNode(exam);
				// TreePath[] sel = { findNode(exam) };
				// examJTree.addSelectionPaths(sel);
				// examJTree.scrollPathToVisible(findNode(exam));
			}

			DefaultMutableTreeNode examNode = getNodeFromPath(examPath);
			for (Iterator<IQuestion> iterQuestion = exam.getQuestions().iterator(); iterQuestion.hasNext();) {
				IQuestion question = (IQuestion) iterQuestion.next();
				TreePath questionPath = findNode(question);
				if (questionPath == null) {
					DefaultMutableTreeNode subNode = new DefaultMutableTreeNode(question);
					subNode.setUserObject(question);
					examNode.insert(subNode, examNode.getChildCount());
					questionPath = findNode(question);
				}
				DefaultMutableTreeNode answerNode = getNodeFromPath(questionPath);
				for (Iterator<IAnswer> iterAnswer = question.getAnswers().iterator(); iterAnswer.hasNext();) {
					IAnswer answer = (IAnswer) iterAnswer.next();
					TreePath answerPath = findNode(answer);
					if (answerPath == null) {
						DefaultMutableTreeNode subNode = new DefaultMutableTreeNode(answer);
						subNode.setUserObject(answer);
						answerNode.insert(subNode, answerNode.getChildCount());
					}
				}
			}
		}
		// root.insert(new DefaultMutableTreeNode("test"), 1);
		// ((DefaultMutableTreeNode)root.getChildAt(1)).insert(new
		// DefaultMutableTreeNode("test2"), 0);
		((DefaultTreeModel) (examJTree.getModel())).reload();
		expandAllNodes(examJTree);

		checkButtons();
		this.refresh = false;
	}

	private TreePath findNode(IQuestion solution) {
		Enumeration<DefaultMutableTreeNode> deepE = root.depthFirstEnumeration();
		while (deepE.hasMoreElements()) {
			DefaultMutableTreeNode node = deepE.nextElement();
			if (node.getUserObject() instanceof IQuestion) {
				IQuestion s = (IQuestion) node.getUserObject();
				if (s.equals(solution)) {
					return new TreePath(node.getPath());
				}
			}
		}
		return null;
	}

	private TreePath findNode(IAnswer solution) {
		Enumeration<DefaultMutableTreeNode> deepE = root.depthFirstEnumeration();
		while (deepE.hasMoreElements()) {
			DefaultMutableTreeNode node = deepE.nextElement();
			if (node.getUserObject() instanceof IAnswer) {
				IAnswer a = (IAnswer) node.getUserObject();
				if (a.equals(solution)) {
					return new TreePath(node.getPath());
				}
			}
		}
		return null;
	}

	private void expandAllNodes(JTree tree) {
		DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) tree.getModel().getRoot();
		int count = tree.getModel().getChildCount(rootNode);
		for (int i = 0; i < count; i++) {
			DefaultMutableTreeNode tempNode = (DefaultMutableTreeNode) tree.getModel().getChild(rootNode, i);
			TreePath path = new TreePath(tempNode.getPath());
			tree.expandPath(path);
			// tree.expandRow(i);
		}
	}

	private DefaultMutableTreeNode getNodeFromPath(TreePath p) {
		Enumeration<DefaultMutableTreeNode> deepE = root.depthFirstEnumeration();
		while (deepE.hasMoreElements()) {
			DefaultMutableTreeNode node = deepE.nextElement();
			TreePath checkPath = new TreePath(node.getPath());
			if (checkPath.equals(p)) {
				return node;
			}
		}
		return null;
	}

	private TreePath findNode(String s) {
		Enumeration<DefaultMutableTreeNode> deepE = root.depthFirstEnumeration();
		while (deepE.hasMoreElements()) {
			DefaultMutableTreeNode node = deepE.nextElement();
			if (node.toString().equalsIgnoreCase(s)) {
				return new TreePath(node.getPath());
			}
		}
		return null;
	}

	private TreePath findNode(IExam e) {
		Enumeration<DefaultMutableTreeNode> deepE = root.depthFirstEnumeration();
		while (deepE.hasMoreElements()) {
			DefaultMutableTreeNode node = deepE.nextElement();
			if (node.getUserObject() instanceof IExam) {
				IExam exam = (IExam) node.getUserObject();
				if (exam.equals(e)) {
					return new TreePath(node.getPath());
				}
			}
		}
		return null;
	}

	void checkButtons() {
		this.deleteQuestionButton.setEnabled(getSelectedItem() != null);

		this.removeSolutionButton.setEnabled(false);
		this.addSolutionButton.setEnabled(true);
	}

	// ///////////////////////////////////////////////////////////////////////////////////

	// ///////////////////////////////////////////////////////////////////////////////////
	// TreeSelectionListener
	@Override
	public void valueChanged(TreeSelectionEvent arg0) {
		System.out.println(arg0.getPath());
		checkButtons();
		if (getSelectedItem() != null) {
			this.panelEdit.editItem(getSelectedItem());
		}
	}

	// ///////////////////////////////////////////////////////////////////////////////////

	// ///////////////////////////////////////////////////////////////////////////////////
	// ActionListener
	@Override
	public void actionPerformed(ActionEvent arg0) {
		this.refresh = true;
		if (arg0.getSource() == this.addExamButton) {
			// ExamenVerwaltung.getNewQuestion(true);
			ExamenVerwaltung.getNewExam(true);
			// index = this.questionJList.getModel().getSize();
		} else if (arg0.getSource() == this.deleteQuestionButton) {
			if (!ExamenVerwaltung.getQuestionList().contains(this.selectedQuestion)) {
			}
			if (this.selectedQuestion != null) {
				ExamenVerwaltung.deleteElement((ExamItemAbstract) this.selectedQuestion);
				this.selectedQuestion = null;
			}
		}
		if (arg0.getSource() == this.addSolutionButton) {
			panelEdit.saveData();

		} else if (arg0.getSource() == this.removeSolutionButton) {
			TreePath selected = this.examJTree.getSelectionPath();
			DefaultMutableTreeNode check = getNodeFromPath(selected);
			if (check.getUserObject() instanceof IExam) {

			}
		}
		this.refresh = false;
		refresh();
		// if (indexPool <= this.solutionPoolJList.getModel().getSize()) {
		// this.solutionPoolJList.setSelectedIndex(indexPool);
		// }
		// if (indexSel <= this.solutionSelectedJList.getModel().getSize()) {
		// this.solutionSelectedJList.setSelectedIndex(indexSel);
		// }
		checkButtons();
	}

	// ///////////////////////////////////////////////////////////////////////////////////

	// ///////////////////////////////////////////////////////////////////////////////////
	// Construct
	public PanelExam() {
		// this.setLayout(null); // new GridLayout(1, 1));
		this.setLayout(new BorderLayout(5, 5));
		JPanel panelCreate = new JPanel(new GridLayout(1, 2, 5, 5));
		setBorder(new EmptyBorder(5, 5, 5, 5));
		JPanel panelTop = new JPanel(new GridLayout(1, 3, 10, 5));
		// JPanel panelBottom = new JPanel(new GridLayout(1, 3, 10, 10));
		SpringLayout layout = new SpringLayout();

		JPanel panelBottom = new JPanel(layout);
		panelBottom.setLayout(layout);
		panelBottom.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

		// layout.putConstraint(SpringLayout.WEST, label, 5, SpringLayout.WEST,
		// contentPane);

		panelTop.add(panelCreate);

		this.add(panelTop, BorderLayout.NORTH);

		this.root = new DefaultMutableTreeNode(ExamenVerwaltung.getText("Exams"));
		examJTree = new JTree(root);
		examJTree.setShowsRootHandles(true);
		examJTree.setRootVisible(false);
		examJTree.addTreeSelectionListener(this);
		examJTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		// ImageIcon imageIcon = new
		// ImageIcon(TreeExample.class.getResource("/leaf.jpg"));
		DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
		// renderer.setLeafIcon(imageIcon);

		examJTree.setCellRenderer(renderer);
		expandAllNodes(examJTree);

		// JScrollPane teacherScroller = new JScrollPane(this.questionJList);
		JScrollPane teacherScroller = new JScrollPane(examJTree); //
		teacherScroller.setPreferredSize(new Dimension(206, 300));
		teacherScroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		teacherScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		teacherScroller.setViewportBorder(new LineBorder(Color.BLACK));
		teacherScroller.setBounds(5, 30, 205, 300);
		// this.add(teacherScroller);
		this.add(teacherScroller, BorderLayout.WEST);


		// editScroller.setViewportBorder(new LineBorder(Color.BLACK));
//		editScroller.setBounds(5, 30, 205, 300);
		// this.add(teacherScroller);

		layout.putConstraint(SpringLayout.SOUTH, teacherScroller, 0, SpringLayout.SOUTH, panelBottom);
		layout.putConstraint(SpringLayout.NORTH, teacherScroller, 0, SpringLayout.NORTH, panelBottom);

		this.addExamButton = ExamenVerwaltung.getButton("newTeacher", 5, 5, 100, 20, this, "Erstellen", "Neuer Leerer");
		this.deleteQuestionButton = ExamenVerwaltung.getButton("delTeacher", 110, 5, 100, 20, this, "Löschen",
				"Leerer löschen");
		// this.add(this.addTeacherButton);
		// this.add(this.deleteTeacherButton);
		panelCreate.add(this.addExamButton);
		panelCreate.add(this.deleteQuestionButton);

		this.addSolutionButton = ExamenVerwaltung.getButton("saveQuestion", 235, 5, 205, 20, this, "Speichern",
				"Speichern");
		// this.add(this.addCourseButton);
		panelTop.add(this.addSolutionButton);

		this.removeSolutionButton = ExamenVerwaltung.getButton("remCourse", 470, 5, 205, 20, this, "<- Entfernen",
				"Kurs Entfernen");
		// this.add(this.removeCourseButton);
		panelTop.add(this.removeSolutionButton);

		// PanelEdit theNewEditPanel = new PanelEdit();
		// panelBottom.add(theNewEditPanel);

		// this.add(tookScroller);
		// TODO: panelBottom.add(poolScroller);
		// TODO: panelBottom.add(tookScroller);

		JPanel labels = new JPanel();
		labels.setLayout(new BoxLayout(labels, BoxLayout.Y_AXIS));
		labels.setPreferredSize(new Dimension(labelWidth, 10));
		labels.setMaximumSize(new Dimension(labelWidth, Integer.MAX_VALUE));

		JPanel texts = new JPanel();
		texts.setLayout(new BoxLayout(texts, BoxLayout.Y_AXIS));

		// panelBottom.add(editPanel);
		// panelBottom.setOpaque(true);
		// panelBottom.setBackground(Color.BLUE);
		// tookScroller.setVisible(false);
		// layout.putConstraint(SpringLayout.EAST, teacherScroller, 0,
		// SpringLayout.EAST, editPanel);

		this.add(panelTop, BorderLayout.NORTH);

		this.add(panelBottom, BorderLayout.SOUTH);

		

		JScrollPane editScroller = new JScrollPane(new JPanel(new PanelEdit()),
		        JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
		        JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		this.add(editScroller, BorderLayout.CENTER);
		editScroller.doLayout();
		editScroller.setSize(new Dimension(100000,20000));
		editScroller.setPreferredSize(new Dimension(15, 20));
		editScroller.getViewport().revalidate();
//        setLayout(null);
		this.refresh = false;

	}

	// ///////////////////////////////////////////////////////////////////////////////////

}
