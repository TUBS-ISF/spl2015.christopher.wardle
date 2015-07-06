package spl.vocTrainer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.filechooser.FileFilter;

import au.com.bytecode.opencsv.CSVReader;
import spl.vocTrainer.VocTrainer;
import spl.vocTrainer.utilities.Category;
import spl.vocTrainer.utilities.Dictionary;

public class VocTrainer {

    private JFileChooser fileChooser;
    private JMenuItem importItem;

    public VocTrainer() {
	fileChooser = new JFileChooser(defaultPath);
	importItem = new JMenuItem("Import...");
	importItem.addActionListener(this);
	fileMenu.add(importItem);
    }

    public void actionPerformed(ActionEvent e) {
	original(e);
	if (e.getSource() == importItem) {
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

	    int returnVal = fileChooser.showOpenDialog(this);
	    if (returnVal == JFileChooser.APPROVE_OPTION) {
		vocabularies = importFile(fileChooser.getSelectedFile());
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
		    setText("Welcome to your " + vocabularies.getName() + "!\n");
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

    private static String getFileExtension(String fileName) {
	int index = fileName.lastIndexOf(".");
	String fileExtension = fileName.substring(index + 1);

	return fileExtension;
    }
}
