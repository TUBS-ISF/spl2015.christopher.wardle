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

import spl.vocTrainer.plugins.CategoriesPlugin;
import spl.vocTrainer.plugins.FeedbackPlugin;
import spl.vocTrainer.plugins.ImportPlugin;
import spl.vocTrainer.plugins.ShufflePlugin;
import spl.vocTrainer.utilities.Category;
import spl.vocTrainer.utilities.Dictionary;
import spl.vocTrainer.utilities.Entry;

public class VocTrainer extends JFrame implements ActionListener {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane, successRatePanel;
    private final Box answerElements = Box.createHorizontalBox();
    private final Box toolBar = Box.createVerticalBox();
    private JTextField answerField;
    private JTextArea displayVocArea;
    private JButton submitAnswer, randomize, prevVoc, nextVoc;
    private JMenuBar menuBar;
    private JMenu fileMenu;
    private JMenuItem importItem, exitItem;
    private JScrollPane scrollPane;

    // fields for VocTrainer functionality
    private Dictionary vocabularies;
    private Random r = new Random();
    private Category currentCategory;
    private int currentVocabulary;

    // fields for Plugins
    ShufflePlugin shufflePlugin;
    ImportPlugin importPlugin;
    CategoriesPlugin categoriesPlugin;
    FeedbackPlugin feedbackPlugin;

    /**
     * Create the frame.
     */
    public VocTrainer(ShufflePlugin shufflePlugin, ImportPlugin importPlugin, CategoriesPlugin categoriesPlugin, FeedbackPlugin feedbackPlugin) {
	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	setBounds(100, 100, 720, 500);
	setTitle("Vocabulary Trainer");

	// set shuffle plugin
	this.shufflePlugin = shufflePlugin;
	if (this.shufflePlugin != null) {
	    this.shufflePlugin.setApplication(this);
	}
	// set import plugin
	this.importPlugin = importPlugin;
	if (this.importPlugin != null) {
	    this.importPlugin.setApplication(this);
	}
	// set categories plugin
	this.categoriesPlugin = categoriesPlugin;
	if (this.categoriesPlugin != null) {
	    this.categoriesPlugin.setApplication(this);
	}
	// set feedback plugin
	this.feedbackPlugin = feedbackPlugin;
	if (this.feedbackPlugin != null) {
	    this.feedbackPlugin.setApplication(this);
	}

	menuBar = new JMenuBar();
	setJMenuBar(menuBar);

	fileMenu = new JMenu("File");
	menuBar.add(fileMenu);

	if (importPlugin != null) {
	    importItem = new JMenuItem(importPlugin.getButtonText());
	    importItem.addActionListener(importPlugin.buttonClicked());
	    fileMenu.add(importItem);
	}

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

	// add shuffle button if plugin is activated
	if (shufflePlugin != null) {
	    randomize = shufflePlugin.getRandomizeButton();
	    randomize.addActionListener(shufflePlugin.buttonClicked());
	    toolBar.add(randomize);
	}

	toolBar.add(Box.createRigidArea(new Dimension(0, 5)));

	successRatePanel = new JPanel();
	successRatePanel.setBackground(Color.RED);
	toolBar.add(successRatePanel);

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
    private void evaluateAnswer(String answer, Entry solution) {
	if (solution.getValue().toUpperCase().equals(answer.toUpperCase())) {
	    displayVocArea.append("\n" + solution.getValue() + " - Correct :)\n\n");
	}
	else {
	    displayVocArea.append("\n" + answer + " - Incorrect :(\n\n");
	}
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
     * Method for import plugin to tell it has finished with the import.
     * 
     * @param voc
     *            The imported Dictionary
     */
    public void importFinished(Dictionary voc) {
	if (voc != null) {
	    vocabularies = voc;
	    currentCategory = vocabularies.getCategory(0);
	    currentVocabulary = 0;

	    if (categoriesPlugin != null) {
		menuBar.add(categoriesPlugin.createCategoryMenu());
		contentPane.revalidate();
		categoriesPlugin.checkAll(true);
	    }

	    nextVocabulary();
	}
    }

    /**
     * Gets next vocabulary according to random and/or category variables.
     */
    private void nextVocabulary() {
	// get next vocabulary
	Entry nextEntry;
	boolean random = false;
	if (shufflePlugin != null) {
	    random = shufflePlugin.getRandom();
	}
	if (categoriesPlugin != null) {
	    if (!categoriesPlugin.getSelectedCategories()[vocabularies.getCategoryIndex(currentCategory) + 1]) {
		nextCategory(categoriesPlugin.getSelectedCategories());
	    }
	}
	if (random) {
	    if (categoriesPlugin != null) {
		nextCategory(categoriesPlugin.getSelectedCategories());
	    }

	    currentVocabulary = r.nextInt(vocabularies.getCategory(currentCategory).getSize());
	}
	else {

	    if (currentVocabulary < currentCategory.getSize() - 1) {
		currentVocabulary++;
	    }
	    else {
		if (categoriesPlugin != null) {
		    nextCategory(categoriesPlugin.getSelectedCategories());
		}
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
     * Changes the category to another valid one (valid is every selected category).
     * 
     * @param selectedCategories
     *            Array with true values for every selected category
     */
    private void nextCategory(boolean[] selectedCategories) {
	boolean random = false;
	if (shufflePlugin != null) {
	    random = shufflePlugin.getRandom();
	}
	if (random) {
	    do {
		currentCategory = vocabularies.getCategory(r.nextInt(vocabularies.getCategories().size()));
	    } while (!selectedCategories[vocabularies.getCategoryIndex(currentCategory) + 1]);
	}
	else {
	    do {
		currentCategory = vocabularies
			.getCategory((vocabularies.getCategoryIndex(currentCategory) + 1) % vocabularies.getCategories().size());
	    } while (!selectedCategories[vocabularies.getCategoryIndex(currentCategory) + 1]);
	}
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
	    // if last round is active, get that solution
	    if (feedbackPlugin != null && feedbackPlugin.isLastRound()) {
		solution = feedbackPlugin.getCurrentEntry();
	    }
	    // otherwise use the currentCategory and currentVocabulary variables
	    else {
		solution = getCurrentEntry();
	    }
	    // evaluate the answer
	    if (feedbackPlugin != null) {
		feedbackPlugin.wrongAnswersFeedback(answer, solution);
	    }
	    else {
		evaluateAnswer(answer, solution);
	    }
	    // get next vocabulary, depending on last round variable
	    if (feedbackPlugin != null && feedbackPlugin.isLastRound()) {
		Entry next = feedbackPlugin.getNextEntry();
		if (next != null) {
		    nextVocabulary(next);
		}
		else {
		    // for now just exit and print message on command line
		    System.out.println("Good bye!");
		    System.exit(EXIT_ON_CLOSE);
		}
	    }
	    else {
		nextVocabulary();
	    }
	    answerField.setText("");
	}
	else if (e.getSource() == exitItem) {
	    if (feedbackPlugin != null) {
		int n = JOptionPane.showConfirmDialog(null, "Do you want to go through your "
			+ "incorrectly answered vocabularies once more before you leave?", "Last round", JOptionPane.YES_NO_CANCEL_OPTION);
		switch (n) {
		case 0:
		    feedbackPlugin.evaluationFeedback();
		    displayVocArea.append("\nLet's start the last round of vocabularies!\n");
		    Entry entry = feedbackPlugin.getCurrentEntry();
		    if (entry != null) {
			nextVocabulary(entry);
		    }
		    else {
			//for now just exit and print message on command line
			System.out.println("You answered everything correctly. Well done!");
			System.exit(EXIT_ON_CLOSE);
		    }
		    break;
		case 1:
		    // exit
		    System.exit(EXIT_ON_CLOSE);
		    break;
		default:
		    // ignore these cases -> do nothing
		}
	    }
	    else {
		System.exit(EXIT_ON_CLOSE);
	    }
	}
    }
}
