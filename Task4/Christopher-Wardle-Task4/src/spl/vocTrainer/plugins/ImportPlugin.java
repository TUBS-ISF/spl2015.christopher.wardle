package spl.vocTrainer.plugins;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import au.com.bytecode.opencsv.CSVReader;
import spl.vocTrainer.VocTrainer;
import spl.vocTrainer.plugins.interfaces.IImportPlugin;
import spl.vocTrainer.utilities.Category;
import spl.vocTrainer.utilities.Dictionary;

public class ImportPlugin implements IImportPlugin {

    private JFileChooser fileChooser;
    private VocTrainer app;

    public ImportPlugin(){
	this("");
    }
    
    public ImportPlugin(String defaultPath) {
	fileChooser = new JFileChooser(defaultPath);
    }

    public void setApplication(VocTrainer app) {
	this.app = app;

    }

    public String getButtonText() {
	return "Import...";
    }

    public ActionListener buttonClicked() {
	ActionListener importListener = new ActionListener() {
	    public void actionPerformed(ActionEvent e) {

		// add different file filters
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
		int returnVal = fileChooser.showOpenDialog(app);
		Dictionary vocabularies;
		if (returnVal == JFileChooser.APPROVE_OPTION) {
		    vocabularies = importFile(fileChooser.getSelectedFile());
		    app.importFinished(vocabularies);
		}
	    }
	};
	
	return importListener;
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
		    app.setText("Welcome to your " + vocabularies.getName() + "!\n");
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
		    app.setText("Welcome to your " + vocabularies.getName() + "!\n");
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
