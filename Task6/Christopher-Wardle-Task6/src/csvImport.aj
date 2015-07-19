import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.filechooser.FileFilter;

import spl.vocTrainer.VocTrainer;
import spl.vocTrainer.utilities.Category;
import spl.vocTrainer.utilities.Dictionary;
import au.com.bytecode.opencsv.CSVReader;

public aspect csvImport {
    private JFileChooser fileChooser;
    private JMenuItem importItem;
    String defaultPath = "E:\\Documents\\spl2015_git_repository\\Task6\\Christopher-Wardle-Task6\\resources";

    pointcut constructor(VocTrainer v):execution(VocTrainer.new()) && this(v);

    after(VocTrainer v): constructor(v){
	fileChooser = new JFileChooser(defaultPath);
	importItem = new JMenuItem("Import...");
	importItem.addActionListener(v);
	JMenu menu = v.getFileMenu();
	menu.add(importItem);
    }

    pointcut action(VocTrainer v, ActionEvent e):execution(void VocTrainer.actionPerformed(ActionEvent)) && this(v) && args(e);

    after(VocTrainer v, ActionEvent e):action(v,e){
	if (e.getSource() == importItem) {
	    fileChooser.addChoosableFileFilter(new FileFilter() {

		public boolean accept(File pathname) {
		    return pathname.getName().endsWith(".csv");
		}

		@Override
		public String getDescription() {
		    return "CSV File";
		}
	    });

	    int returnVal = fileChooser.showOpenDialog(v);
	    if (returnVal == JFileChooser.APPROVE_OPTION) {
		v.setVocabularies(importFile(fileChooser.getSelectedFile()));
	    }
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
		    // setText("Welcome to your " + vocabularies.getName() +
		    // "!\n");
		    continue;
		}

		if (nextLine[0].startsWith("%CAT") && vocabularies != null) {
		    // create new category
		    currentCategory = new Category(nextLine[1]);
		    vocabularies.addCategory(currentCategory);
		    continue;
		}

		if (currentCategory == null && vocabularies != null) {
		    currentCategory = new Category("default");
		    vocabularies.addCategory(currentCategory);
		}
		if (vocabularies != null) {
		    vocabularies.getCategory(currentCategory).addNewEntry(nextLine[0], nextLine[1]);
		}
	    }
	    if (vocabularies != null) {
		System.out.println("File import successful!");
	    }
	    else {
		System.out.println("File couldn't be imported");
	    }
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
}