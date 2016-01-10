package de.bbq.java.tasks.vce;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JTree;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

/**
 * @author Thorsten2201
 *
 */
public class PanelExam extends JPanel implements ActionListener, ListSelectionListener {
	/////////////////////////////////////////////////////////////////////////////////////
	// Static
	private static final long serialVersionUID = -6951335589393103017L;
	/////////////////////////////////////////////////////////////////////////////////////

	/////////////////////////////////////////////////////////////////////////////////////
	// Class Properties
	private boolean refresh = true;
	private IQuestion selectedQuestion;
	/////////////////////////////////////////////////////////////////////////////////////

	/////////////////////////////////////////////////////////////////////////////////////
	// Controls
	private JButton addExamButton, deleteQuestionButton, addSolutionButton, removeSolutionButton;
	private JList<IQuestion> questionJList;
	private JList<IExam> examJList;
	private JTree examJTree;
	private JList<IQuestion> solutionPoolJList, solutionSelectedJList;
	private DefaultListModel<IQuestion> questionListModel;
	private DefaultListModel<IExam> examListModel;
	private DefaultListModel<IQuestion> solutionPoolListModel, questionSelectedListModel;
	private DefaultMutableTreeNode root;
	/////////////////////////////////////////////////////////////////////////////////////

	/////////////////////////////////////////////////////////////////////////////////////
	// Class methods
	public void refresh() {
		int selIndex = questionJList.getSelectedIndex();
		this.refresh = true;
		IQuestion cindex = null;

		for (int index = this.questionListModel.getSize(); index > 0; index--) {
			try {
				cindex = this.questionListModel.getElementAt(index - 1);
			} catch (Exception e) {
				ExamenVerwaltung.showException(e);
			}

			if (!ExamenVerwaltung.getQuestionList().contains(cindex)) {
				try {
					this.questionListModel.remove(index - 1);
				} catch (Exception e) {
					ExamenVerwaltung.showException(e);
				}

			}
		}

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
				IQuestion solution = (IQuestion) iterQuestion.next();
				TreePath solutionPath = findNode(solution);
				if (solutionPath == null) {
					DefaultMutableTreeNode subNode = new DefaultMutableTreeNode(solution);
					subNode.setUserObject(solution);
					examNode.insert(subNode, examNode.getChildCount());
					solutionPath = findNode(solution);
				}
			}
		}
		// root.insert(new DefaultMutableTreeNode("test"), 1);
		// ((DefaultMutableTreeNode)root.getChildAt(1)).insert(new
		// DefaultMutableTreeNode("test2"), 0);
		((DefaultTreeModel) (examJTree.getModel())).reload();
		expandAllNodes(examJTree);

		int poolIndex = solutionPoolJList.getSelectedIndex();
		this.solutionPoolListModel.clear();
		this.questionSelectedListModel.clear();
		if (this.selectedQuestion != null) {
			for (IQuestion q : ExamenVerwaltung.getQuestionList()) {
				if (this.selectedQuestion.equals(q)) {
					this.questionSelectedListModel.addElement(q);
				} else if (!q.hasExam()) {
					this.solutionPoolListModel.addElement(q);
				}
			}
		}
		if (selIndex < 0) {
			// do Nothing
		} else if (selIndex < this.questionListModel.getSize()) {
			this.questionJList.setSelectedIndex(selIndex);
		} else {
			this.questionJList.setSelectedIndex(this.questionListModel.getSize() - 1);
		}
		if (poolIndex < 0) {
			// do Nothing
		} else if (poolIndex < this.solutionPoolListModel.getSize()) {
			this.solutionPoolJList.setSelectedIndex(poolIndex);
		} else {
			this.solutionPoolJList.setSelectedIndex(this.solutionPoolListModel.getSize() - 1);
		}
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
		this.deleteQuestionButton.setEnabled(this.questionJList.getModel().getSize() > 0);
		if (this.solutionSelectedJList.getSelectedIndex() < 0) {
			if (this.solutionSelectedJList.getModel().getSize() > 0) {
				this.solutionSelectedJList.setSelectedIndex(0);
			}
		}
		if (this.solutionPoolJList.getSelectedIndex() < 0) {
			if (this.solutionPoolJList.getModel().getSize() > 0) {
				this.solutionPoolJList.setSelectedIndex(0);
			}
		}
		this.removeSolutionButton.setEnabled(this.solutionSelectedJList.getModel().getSize() > 0);
		this.addSolutionButton.setEnabled(this.solutionPoolJList.getModel().getSize() > 0);
	}
	/////////////////////////////////////////////////////////////////////////////////////

	/////////////////////////////////////////////////////////////////////////////////////
	// ListSelectionListener
	@Override
	public void valueChanged(ListSelectionEvent arg0) {
		if (!this.refresh) {
			if (arg0.getSource() == this.questionJList) {
				refreshQuestions();
				checkButtons();
			}
		}
	}

	private void refreshQuestions() {
		IQuestion selTeacher = this.questionJList.getSelectedValue();
		if (selTeacher != null) {
			this.selectedQuestion = selTeacher;
			refresh();
		}
	}
	/////////////////////////////////////////////////////////////////////////////////////

	/////////////////////////////////////////////////////////////////////////////////////
	// ActionListener
	@Override
	public void actionPerformed(ActionEvent arg0) {
		this.refresh = true;
		int index = this.questionJList.getSelectedIndex();
		int indexPool = this.solutionPoolJList.getSelectedIndex();
		int indexSel = this.solutionSelectedJList.getSelectedIndex();
		if (arg0.getSource() == this.addExamButton) {
			// ExamenVerwaltung.getNewQuestion(true);
			ExamenVerwaltung.getNewExam(true);
			index = this.examJTree.getRowCount();
			// index = this.questionJList.getModel().getSize();
		} else if (arg0.getSource() == this.deleteQuestionButton) {
			if (!ExamenVerwaltung.getQuestionList().contains(this.selectedQuestion)) {
				this.questionJList.setSelectedIndex(index);
				this.selectedQuestion = this.questionJList.getSelectedValue();
			}
			if (this.selectedQuestion != null) {
				ExamenVerwaltung.deleteElement((ExamItemAbstract) this.selectedQuestion);
				this.selectedQuestion = null;
			}
			if (index >= this.questionJList.getModel().getSize() - 1) {
				index--;
			}
		}
		if (arg0.getSource() == this.addSolutionButton) {
			IQuestion selectedCourse = this.solutionPoolJList.getSelectedValue();
			if (selectedCourse != null) {
				if (selectedQuestion != null) {
					try {
						if (!selectedCourse.hasExam()) {
							try {
								this.selectedQuestion.addQuestion(selectedCourse);
								indexSel = this.solutionSelectedJList.getModel().getSize();
								if (indexPool >= this.solutionPoolJList.getModel().getSize() - 1) {
									indexPool--;
								}
							} catch (Exception e) {
								ExamenVerwaltung.showException(e);
							}
						}
					} catch (Exception e) {
						ExamenVerwaltung.showException(e);
					}
				}
			}
		} else if (arg0.getSource() == this.removeSolutionButton) {
			IQuestion selectedCourse = this.solutionSelectedJList.getSelectedValue();
			if (selectedCourse != null) {
				if (this.selectedQuestion != null) {
					try {
						this.selectedQuestion.deleteQuestion(selectedCourse);
						if (indexSel >= this.solutionSelectedJList.getModel().getSize() - 1) {
							indexSel--;
						}
						if (indexPool < 0) {
							indexPool = 0;
						}
					} catch (Exception e) {
						ExamenVerwaltung.showException(e);
					}
				}
			}
		}
		this.refresh = false;
		refresh();
		if (index <= this.questionJList.getModel().getSize()) {
			this.questionJList.setSelectedIndex(index);
			this.selectedQuestion = this.questionJList.getSelectedValue();
			refreshQuestions();
		}
		if (indexPool <= this.solutionPoolJList.getModel().getSize()) {
			this.solutionPoolJList.setSelectedIndex(indexPool);
		}
		if (indexSel <= this.solutionSelectedJList.getModel().getSize()) {
			this.solutionSelectedJList.setSelectedIndex(indexSel);
		}
		checkButtons();
	}
	/////////////////////////////////////////////////////////////////////////////////////

	/////////////////////////////////////////////////////////////////////////////////////
	// Construct
	public PanelExam() {
		// this.setLayout(null); // new GridLayout(1, 1));
		this.setLayout(new BorderLayout(5, 5));
		JPanel panelCreate = new JPanel(new GridLayout(1, 2, 5, 5));
		setBorder(new EmptyBorder(5, 5, 5, 5));
		JPanel panelTop = new JPanel(new GridLayout(1, 3, 10, 5));
		JPanel panelBottom = new JPanel(new GridLayout(1, 3, 10, 10));
		panelTop.add(panelCreate);
		this.add(panelTop, BorderLayout.NORTH);

		this.questionListModel = new DefaultListModel<>();
		this.questionJList = new JList<IQuestion>(this.questionListModel);
		this.questionJList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		this.questionJList.setLayoutOrientation(JList.VERTICAL);
		this.questionJList.setVisibleRowCount(-1);
		this.questionJList.addListSelectionListener(this);
		this.questionJList.addMouseListener(new MouseAdapter() {
			@SuppressWarnings("rawtypes")
			public void mouseClicked(MouseEvent evt) {
				JList list = (JList) evt.getSource();
				int index = -1;
				if (evt.getClickCount() == 2) {
					// Double-click detected
					index = list.locationToIndex(evt.getPoint());
				} else if (evt.getClickCount() == 3) {
					// Triple-click detected
					index = list.locationToIndex(evt.getPoint());
				}
				if (index >= 0) {
					ExamenVerwaltung.getInstance().editItem((ExamItemAbstract) questionListModel.get(index));
				}
			}
		});

		this.questionJList.setCellRenderer(new ExamListCellRenderer());

		this.root = new DefaultMutableTreeNode(ExamenVerwaltung.getText("Exams"));
		examJTree = new JTree(root);
		examJTree.setShowsRootHandles(true);
		examJTree.setRootVisible(false);

		// ImageIcon imageIcon = new
		// ImageIcon(TreeExample.class.getResource("/leaf.jpg"));
		DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
		// renderer.setLeafIcon(imageIcon);

		examJTree.setCellRenderer(renderer);
		expandAllNodes(examJTree);
		// JScrollPane teacherScroller = new JScrollPane(this.questionJList);
		JScrollPane teacherScroller = new JScrollPane(this.examJTree);
		teacherScroller.setPreferredSize(new Dimension(206, 300));
		teacherScroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		teacherScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		teacherScroller.setViewportBorder(new LineBorder(Color.BLACK));
		teacherScroller.setBounds(5, 30, 205, 300);
		// this.add(teacherScroller);
		panelBottom.add(teacherScroller);

		this.addExamButton = ExamenVerwaltung.getButton("newTeacher", 5, 5, 100, 20, this, "Erstellen", "Neuer Leerer");
		this.deleteQuestionButton = ExamenVerwaltung.getButton("delTeacher", 110, 5, 100, 20, this, "Löschen",
				"Leerer löschen");
		// this.add(this.addTeacherButton);
		// this.add(this.deleteTeacherButton);
		panelCreate.add(this.addExamButton);
		panelCreate.add(this.deleteQuestionButton);

		this.addSolutionButton = ExamenVerwaltung.getButton("addCourse", 235, 5, 205, 20, this, "Hinzufügen ->",
				"Kurs Hinzufügen");
		// this.add(this.addCourseButton);
		panelTop.add(this.addSolutionButton);

		this.removeSolutionButton = ExamenVerwaltung.getButton("remCourse", 470, 5, 205, 20, this, "<- Entfernen",
				"Kurs Entfernen");
		// this.add(this.removeCourseButton);
		panelTop.add(this.removeSolutionButton);

		this.solutionPoolListModel = new DefaultListModel<>();
		this.solutionPoolJList = new JList<IQuestion>(this.solutionPoolListModel);
		this.solutionPoolJList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		this.solutionPoolJList.setLayoutOrientation(JList.VERTICAL);
		this.solutionPoolJList.setVisibleRowCount(-1);
		this.solutionPoolJList.addMouseListener(new MouseAdapter() {
			@SuppressWarnings({ "rawtypes", "unchecked" })
			public void mouseClicked(MouseEvent evt) {
				JList list = (JList) evt.getSource();
				int index = -1;
				if (evt.getClickCount() == 2) {
					// Double-click detected
					index = list.locationToIndex(evt.getPoint());
				} else if (evt.getClickCount() == 3) {
					// Triple-click detected
					index = list.locationToIndex(evt.getPoint());
				}
				if (index >= 0) {
					ExamenVerwaltung.getInstance()
							.editItem((ExamItemAbstract) ((List<IQuestion>) solutionPoolJList).get(index));
					refresh();
				}
			}
		});

		JScrollPane poolScroller = new JScrollPane(this.solutionPoolJList);

		poolScroller.setPreferredSize(new Dimension(206, 300));
		poolScroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		poolScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		poolScroller.setViewportBorder(new LineBorder(Color.BLACK));
		poolScroller.setBounds(235, 30, 205, 300);
		// this.add(poolScroller);

		this.questionSelectedListModel = new DefaultListModel<>();
		this.solutionSelectedJList = new JList<IQuestion>(this.questionSelectedListModel);
		this.solutionSelectedJList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		this.solutionSelectedJList.setLayoutOrientation(JList.VERTICAL);
		this.solutionSelectedJList.setVisibleRowCount(-1);
		this.solutionSelectedJList.addMouseListener(new MouseAdapter() {
			@SuppressWarnings({ "rawtypes", "unchecked" })
			public void mouseClicked(MouseEvent evt) {
				JList list = (JList) evt.getSource();
				int index = -1;
				if (evt.getClickCount() == 2) {
					// Double-click detected
					index = list.locationToIndex(evt.getPoint());
				} else if (evt.getClickCount() == 3) {
					// Triple-click detected
					index = list.locationToIndex(evt.getPoint());
				}
				if (index >= 0) {
					ExamenVerwaltung.getInstance()
							.editItem((ExamItemAbstract) ((List<IQuestion>) solutionPoolJList).get(index));
					refresh();
				}
			}
		});

		JScrollPane tookScroller = new JScrollPane(this.solutionSelectedJList);
		tookScroller.setPreferredSize(new Dimension(206, 300));
		tookScroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		tookScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		tookScroller.setViewportBorder(new LineBorder(Color.BLACK));
		tookScroller.setBounds(470, 30, 205, 300);
		// this.add(tookScroller);
		panelBottom.add(poolScroller);
		panelBottom.add(tookScroller);
		this.add(panelBottom, BorderLayout.CENTER);
		this.refresh = false;
	}
	/////////////////////////////////////////////////////////////////////////////////////

}
