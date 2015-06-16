package spl.vocTrainer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;

import spl.vocTrainer.utilities.Category;
import spl.vocTrainer.utilities.Dictionary;
import au.com.bytecode.opencsv.CSVReader;

public class VocTrainer extends JFrame implements ActionListener {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane, successRatePanel;
    private final Box answerElements = Box.createHorizontalBox();
    private final Box toolBar = Box.createVerticalBox();
    private JTextField answerField;
    private JTextArea displayVocArea;
    private JButton submitAnswer, randomize,prevVoc, nextVoc;
    private JMenuBar menuBar;
    private JMenu fileMenu, categoryMenu;
    private JMenuItem importItem, exitItem;
    private JScrollPane scrollPane;
    private JCheckBoxMenuItem checkAllItem;
    private final JFileChooser fileChooser = new JFileChooser("E:\\Documents\\spl2015_git_repository\\Task4\\Christopher-Wardle-Task4\\resources");

    // fields for VocTrainer functionality
    private Dictionary vocabularies;
    private Random r = new Random();
    private Category currentCategory;
    private int currentVocabulary;
    private boolean random = false;
    private boolean[] selectedCategories;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
	EventQueue.invokeLater(new Runnable() {
	    public void run() {
		try {
		    VocTrainer vocTrainer = new VocTrainer();
		    vocTrainer.setVisible(true);
		}
		catch (Exception e) {
		    e.printStackTrace();
		}
	    }
	});
    }

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

	importItem = new JMenuItem("Import...");
	importItem.addActionListener(this);
	fileMenu.add(importItem);

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


	randomize = new JButton("Randomize");
	randomize.setBackground(Color.red);
	randomize.addActionListener(this);
	toolBar.add(randomize);


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

    private void evaluateAnswer(String text) {
	String correctAnswer = vocabularies.getCategory(currentCategory).getValue(currentVocabulary);
	if (correctAnswer.toUpperCase().equals(text.toUpperCase())) {
	    displayVocArea.append("\n" + correctAnswer + " - Correct :)\n\n");
	}
	else {
	    displayVocArea.append("\n" + text + " - Incorrect :(\n\n");
	}
    }

    public Dictionary importFile(File file) {
	// list for all vocabularies ordered by category if provided
	Dictionary vocabularies = null;

	// detect file format for correct import method
	String fileExtension = getFileExtension(file.getName());

	// import file depending on file extension
	if (fileExtension.equals("csv")) {
	    vocabularies = importCSVFile(file);
	}

	else if (fileExtension.equals("txt")) {
	    vocabularies = importTXTFile(file);
	}
	else if (fileExtension.equals("xml")) {
	 vocabularies = importXMLFile(file);
	 }
	else {

	}

	return vocabularies;
    }

    private Dictionary importCSVFile(File file) {
	Dictionary vocabularies = null;
	try {
	    @SuppressWarnings("resource")
	    CSVReader csvReader = new CSVReader(new InputStreamReader(new FileInputStream(file), "UTF8"));
	    String[] nextLine;
	    Category currentCategory = null;
	    while ((nextLine = csvReader.readNext()) != null) {
		if (nextLine[0].startsWith("%DICT") && nextLine.length == 3) {
		    // start of the dictionary
		    vocabularies = new Dictionary(nextLine[1] + " - " + nextLine[2] + " Dictionary");
		    displayVocArea.setText("Welcome to your " + vocabularies.getName() + "!\n");
		    continue;
		}

		if (nextLine[0].startsWith("%CAT")) {
		    // create new category
		    currentCategory = new Category(nextLine[1]);
		    vocabularies.addCategory(currentCategory);
		    continue;
		}

		if (currentCategory == null) {
		    currentCategory = new Category("default");
		    vocabularies.addCategory(currentCategory);
		}
		vocabularies.getCategory(currentCategory).addNewEntry(nextLine[0], nextLine[1]);
	    }
	    System.out.println("File import successful!");
	}
	catch (FileNotFoundException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	catch (ArrayIndexOutOfBoundsException e) {
	    System.err.println("Probably invalid syntax in imported file \"" + file.getPath() + "\"");
	    e.printStackTrace();
	}

	return vocabularies;
    }

    private Dictionary importXMLFile(File file) {
     // TODO Auto-generated method stub
     return null;
     }
    

    private Dictionary importTXTFile(File file) {
	Dictionary vocabularies = null;
	try {
	    @SuppressWarnings("resource")
	    BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"));
	    String nextLine;
	    Category currentCategory = null;
	    while ((nextLine = br.readLine()) != null) {
		String[] entries = nextLine.split(",");
		if (entries[0].startsWith("%DICT") && entries.length == 3) {
		    // start of the dictionary
		    vocabularies = new Dictionary(entries[1] + " - " + entries[2] + " Dictionary");
		    displayVocArea.setText("Welcome to your " + vocabularies.getName() + "!\n");
		    continue;
		}

		if (entries[0].startsWith("%CAT")) {
		    // create new category
		    currentCategory = new Category(entries[1]);
		    vocabularies.addCategory(currentCategory);
		    continue;
		}

		if (currentCategory == null) {
		    currentCategory = new Category("default");
		    vocabularies.addCategory(currentCategory);
		}
		vocabularies.getCategory(currentCategory).addNewEntry(entries[0], entries[1]);
	    }
	    System.out.println("File import successful!");
	}
	catch (FileNotFoundException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	catch (ArrayIndexOutOfBoundsException e) {
	    System.err.println("Probably invalid syntax in imported file \"" + file.getPath() + "\"");
	    e.printStackTrace();
	}
	return vocabularies;
    }

    private static String getFileExtension(String fileName) {
	int index = fileName.lastIndexOf(".");
	String fileExtension = fileName.substring(index + 1);

	return fileExtension;
    }

    private void nextVocabulary() {
	// get next vocabulary
	String nextVocabulary;
	if (random) {
	    nextCategory();
	    currentVocabulary = r.nextInt(vocabularies.getCategory(currentCategory).getSize());
	}
	else {

	    if (currentVocabulary < currentCategory.getSize() - 1) {
		currentVocabulary++;
	    }
	    else {
		nextCategory();
		currentVocabulary = 0;
	    }

	}
	nextVocabulary = currentCategory.getKey(currentVocabulary);
	displayVocArea.append("What does this vocabulary mean?\n" + nextVocabulary);
    }

    private void nextCategory() {
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

    private void checkAll(boolean value) {
	if (categoryMenu.getItemCount() > 0) {
	    for (int i = 0; i < categoryMenu.getItemCount(); i++) {
		categoryMenu.getItem(i).setSelected(value);
		selectedCategories[i] = value;
	    }
	}
    }

    private void createCategoryMenu() {
	categoryMenu = new JMenu("Categories");

	// create CheckBoxMenuItem for checking all items
	checkAllItem = new JCheckBoxMenuItem("Check All");
	checkAllItem.addActionListener(this);
	categoryMenu.add(checkAllItem);

	for (Category c : vocabularies.getCategories()) {
	    JCheckBoxMenuItem item = new JCheckBoxMenuItem(c.getName());
	    item.addActionListener(this);
	    categoryMenu.add(item);
	}
	selectedCategories = new boolean[categoryMenu.getItemCount()];
	menuBar.add(categoryMenu);
	contentPane.revalidate();
    }

    public void actionPerformed(ActionEvent e) {
	if (e.getSource() == this.answerField || e.getSource() == this.submitAnswer) {
	    evaluateAnswer(answerField.getText());
	    nextVocabulary();
	    answerField.setText("");
	}
	else if (e.getSource() == this.importItem) {
	    // import File and show the first vocabulary

	    // add different file filters (silly anonymous types due to Java1.5)
	    fileChooser.addChoosableFileFilter(new FileFilter() {

		@Override
		public String getDescription() {
		    return "CSV Files";
		}

		@Override
		public boolean accept(File f) {
		    return f.getName().endsWith(".csv");
		}
	    });

	    fileChooser.addChoosableFileFilter(new FileFilter() {

		@Override
		public String getDescription() {
		    return "TXT Files";
		}

		@Override
		public boolean accept(File f) {
		    return f.getName().endsWith(".txt");
		}
	    });

	    fileChooser.addChoosableFileFilter(new FileFilter() {
	        
	        @Override
	        public String getDescription() {
	    	    return "XML Files";
	        }
	        
	        @Override
	        public boolean accept(File f) {
	    	    return f.getName().endsWith(".xml");
	        }
	    });
	    int returnVal = fileChooser.showOpenDialog(VocTrainer.this);

	    if (returnVal == JFileChooser.APPROVE_OPTION) {
		vocabularies = importFile(fileChooser.getSelectedFile());
		if (vocabularies != null) {
		    currentCategory = vocabularies.getCategory(0);
		    currentVocabulary = 0;

		    createCategoryMenu();
		    checkAll(true);
		    nextVocabulary();
		}
	    }
	}

	else if (e.getSource() == randomize) {
	    random = !random;
	    if (random) {
		randomize.setBackground(new Color(124, 205, 124));
	    }
	    else {
		randomize.setBackground(new Color(238, 44, 44));
	    }
	}
	else if (e.getSource() == checkAllItem) {
	    if (checkAllItem.isSelected()) {
		checkAll(true);
	    }
	    else {
		checkAll(false);
	    }

	}
	else if (e.getSource() == exitItem) {
	    System.exit(EXIT_ON_CLOSE);
	}
	else if (e.getSource() instanceof JCheckBoxMenuItem) {
	    JCheckBoxMenuItem item = (JCheckBoxMenuItem) e.getSource();
	    Category c = vocabularies.getCategory(item.getText());
	    selectedCategories[vocabularies.getCategoryIndex(c) + 1] = item.isSelected();
	    // change category if needed
	    if (!selectedCategories[vocabularies.getCategoryIndex(currentCategory) + 1]) {
		nextCategory();
	    }
	}
    }
}
