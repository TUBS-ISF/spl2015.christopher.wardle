import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.filechooser.FileFilter;

import spl.vocTrainer.VocTrainer;
import spl.vocTrainer.utilities.Category;
import spl.vocTrainer.utilities.Dictionary;
import spl.vocTrainer.utilities.Entry;

public aspect csvExport {
    private JFileChooser fileChooser;
    private JMenuItem exportItem;
    String defaultPath = "E:\\Documents\\spl2015_git_repository\\Task6\\Christopher-Wardle-Task6\\resources";

    pointcut constructor(VocTrainer v):execution(VocTrainer.new()) && this(v);

    after(VocTrainer v): constructor(v){
	fileChooser = new JFileChooser(defaultPath);
	exportItem = new JMenuItem("Export...");
	exportItem.addActionListener(v);
	v.getFileMenu().add(exportItem);
    }

    pointcut action(VocTrainer v, ActionEvent e):execution(void VocTrainer.actionPerformed(ActionEvent)) && this(v) && args(e);

    after(VocTrainer v, ActionEvent e):action(v,e){
	if (e.getSource() == exportItem) {
	    fileChooser.addChoosableFileFilter(new FileFilter() {

		public boolean accept(File pathname) {
		    return pathname.getName().endsWith(".csv");
		}

		@Override
		public String getDescription() {
		    return "CSV File";
		}
	    });

	    int returnVal = fileChooser.showSaveDialog(v);
	    if (returnVal == JFileChooser.APPROVE_OPTION) {
		// export file
		try {
		    Dictionary vocabularies = v.getVocabularies();
		    if (vocabularies == null) {
			return;
		    }
		    String filename = fileChooser.getSelectedFile().toString();
		    if(!filename.endsWith(".csv")){
			filename = filename.concat(".csv");
		    }
		    FileWriter writer = new FileWriter(filename, false);
		    writer.write("%DICT," + vocabularies.getName());
		    for (Category c : vocabularies.getCategories()) {
			writer.write("\n%CAT," + c.getName());
			for (Entry entry : c.getVocabularies()) {
			    writer.write("\n" + entry.getKey() + "," + entry.getValue());
			}
		    }
		    writer.close();
		}
		catch (IOException e1) {
		    // TODO Auto-generated catch block
		    e1.printStackTrace();
		}
		System.out.println("Saved as file: " + fileChooser.getSelectedFile().getAbsolutePath());
	    }
	}
    }
}