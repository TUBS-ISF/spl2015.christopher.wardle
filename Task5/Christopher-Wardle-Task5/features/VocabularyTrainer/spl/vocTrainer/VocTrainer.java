package spl.vocTrainer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;

import spl.vocTrainer.utilities.Category;
import spl.vocTrainer.utilities.Dictionary;
import spl.vocTrainer.utilities.Entry;

public class VocTrainer extends JFrame implements ActionListener {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane, successRatePanel, buttonPanel;
    private final Box answerElements = Box.createHorizontalBox();
    private final Box toolBar = Box.createVerticalBox();
    private JTextField answerField;
    private JTextArea displayVocArea;
    private JButton submitAnswer, prevVoc, nextVoc, startTest;
    private JMenuBar menuBar;
    private JMenu fileMenu;
    private JMenuItem exitItem;
    private JScrollPane scrollPane;

    // fields for VocTrainer functionality
    private Dictionary vocabularies;
    private Random r = new Random();
    private Category currentCategory;
    private int currentVocabulary;
    private boolean random;

    // utility for test purposes
    String defaultPath = "E:\\Documents\\spl2015_git_repository\\Task5\\Christopher-Wardle-Task5\\resources";

    /**
     * Create the frame.
     */
    public VocTrainer() {
	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	setBounds(100, 100, 720, 500);
	setTitle("Vocabulary Trainer");

	menuBar = new JMenuBar();
	setJMenuBar(menuBar);

	fileMenu = new JMenu("File");
	menuBar.add(fileMenu);

	exitItem = new JMenuItem("Exit");
	exitItem.addActionListener(this);
	fileMenu.add(exitItem);
	contentPane = new JPanel();
	contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
	setContentPane(contentPane);
	contentPane.setLayout(new BorderLayout(25, 25));

	displayVocArea = new JTextArea();
	displayVocArea.setMargin(new Insets(20, 20, 20, 20));
	displayVocArea.setEditable(false);
	displayVocArea.setBackground(new Color(224, 224, 224));
	displayVocArea.setLineWrap(true);
	displayVocArea
		.setText("To start your training, import a file from the File->Import menu.\nEnsure that you use the correct syntax for your dictionary:\n\n"
			+ "- to start a dictionary, start your line with \'%DICT\' followed by your two dictionary languages and seperated with a comma\n"
			+ "(so for an english-german dictionary you would type \'%DICT, English, German\')\n\n"
			+ "- to create a new category of words, simply start the line with \'%CAT\' followed by a comma and the name of your category\n"
			+ "(so for the category \'Planets\' you would type \'%CAT, Planets\')\n\n"
			+ "- each entry consists of two words seperated by a comma; multiple answers for one vocabulary are not supported, yet\n\n"
			+ "- make sure not to have empty lines in your file as the program isn't handling those up until now (will be implemented in the future)");
	scrollPane = new JScrollPane(displayVocArea);
	scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
	contentPane.add(scrollPane, BorderLayout.CENTER);

	toolBar.add(Box.createRigidArea(new Dimension(0, 5)));

	successRatePanel = new JPanel();
	successRatePanel.setBackground(Color.RED);
	toolBar.add(successRatePanel);
	toolBar.add(Box.createRigidArea(new Dimension(0, 5)));

	buttonPanel = new JPanel();
	startTest = new JButton("Start");
	startTest.addActionListener(this);
	buttonPanel.add(startTest);
	toolBar.add(buttonPanel);

	contentPane.add(toolBar, BorderLayout.NORTH);

	prevVoc = new JButton("<");
	contentPane.add(prevVoc, BorderLayout.WEST);

	nextVoc = new JButton(">");
	contentPane.add(nextVoc, BorderLayout.EAST);

	answerField = new JTextField();
	answerField.setText("Type text here...");
	answerField.setColumns(10);
	answerField.addActionListener(this);
	answerField.addMouseListener(new MouseAdapter() {
	    public void mouseClicked(MouseEvent e) {
		if (answerField.getText().equals("Type text here...")) {
		    answerField.setText("");
		}
	    }
	});
	answerElements.add(answerField);

	submitAnswer = new JButton("Submit Answer");
	submitAnswer.addActionListener(this);
	answerElements.add(submitAnswer);

	contentPane.add(answerElements, BorderLayout.SOUTH);
    }

    /**
     * Standard evaluation method for answers.
     * 
     * @param answer
     *            Answer from the answerField text field the user gave as String
     * @param solution
     *            spl.vocTrainer.utilities.Entry object of the currently asked vocabulary
     */
    private void evaluateAnswer(String answer, Entry solution){
	if (solution.getValue().toUpperCase().equals(answer.toUpperCase())) {
	    displayVocArea.append(correctAnswer(solution));
	}
	else {
	    displayVocArea.append(wrongAnswer(answer, solution));
	}
    }
    
    private String correctAnswer(Entry solution){
	return "\n" + solution.getValue() + " - Correct :)\n\n";
    }
    
    private String wrongAnswer(String answer, Entry solution){
	return "\n" + answer + " - Incorrect :(\n\n";
    }

    /**
     * Utility method for plugins to write text.
     * 
     * @param text
     */
    public void setText(String text) {
	displayVocArea.setText(text);
    }

    /**
     * Utility method for plugins to append text
     * 
     * @param text
     */
    public void appendText(String text) {
	displayVocArea.append("\n" + text);
    }

    /**
     * Gets next vocabulary according to random and/or category variables.
     */
    private void nextVocabulary() {
	// get next vocabulary
	Entry nextEntry;

	if (random) {
	    currentVocabulary = r.nextInt(vocabularies.getCategory(currentCategory).getSize());
	}
	else {

	    if (currentVocabulary < currentCategory.getSize() - 1) {
		currentVocabulary++;
	    }
	    else {
		currentVocabulary = 0;
	    }

	}
	nextEntry = getCurrentEntry();
	displayVocArea.append("\nWhat does this vocabulary mean?\n" + nextEntry.getKey());
    }

    /**
     * Gets one specific vocabulary.
     * 
     * @param entry
     *            The specific Entry object.
     */
    private void nextVocabulary(Entry entry) {
	displayVocArea.append("\nWhat does this vocabulary mean?\n" + entry.getKey());
    }



    /**
     * Getter for the dictionary currently used.
     * 
     * @return The current dictionary
     */
    public Dictionary getVocabularies() {
	return vocabularies;
    }

    /**
     * Gets the answer that the user typed into the answer field.
     * 
     * @return Answer as String
     */
    public String getUserAnswer() {
	return answerField.getText();
    }

    /**
     * Gets the currently asked entry.
     * 
     * @return Current Entry object
     */
    public Entry getCurrentEntry() {
	return currentCategory.getEntry(currentVocabulary);
    }

    /**
     * Gets the category of the currently asked entry.
     * 
     * @return Current Category object
     */
    public Category getCurrentCategory() {
	return currentCategory;
    }

    /**
     * ActionPerformed method is triggered whenever the user submits an answer or exits via the menu item.
     */
    public void actionPerformed(ActionEvent e) {
	if (e.getSource() == this.answerField || e.getSource() == this.submitAnswer) {
	    String answer = getUserAnswer();
	    Entry solution;
	    solution = getCurrentEntry();
	    evaluateAnswer(answer, solution);
	    nextVocabulary();
	    answerField.setText("");
	}
	if (e.getSource() == exitItem) {
	    System.exit(EXIT_ON_CLOSE);
	}
	if (e.getSource() == startTest) {
	    startTest();
	}
    }

    public void startTest() {
	if (vocabularies != null) {
	    currentCategory = vocabularies.getCategory(0);
	    currentVocabulary = 0;
	    nextVocabulary();
	}
    }
    
    private void setupTrainerForDatabase(){
	//dummy method - without this in the basic implementation I couldn't refine it from two 
	//different features (DatabaseIntegration and UserManagement) for whatever reason.
    }
}
