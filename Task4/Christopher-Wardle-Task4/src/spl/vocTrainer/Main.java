package spl.vocTrainer;

import java.awt.EventQueue;

import spl.vocTrainer.plugins.CategoriesPlugin;
import spl.vocTrainer.plugins.FeedbackPlugin;
import spl.vocTrainer.plugins.ImportPlugin;
import spl.vocTrainer.plugins.ShufflePlugin;

public class Main {

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
	EventQueue.invokeLater(new Runnable() {
	    public void run() {
		try {
		    VocTrainer vocTrainer = new VocTrainer(
			    new ShufflePlugin(), 
			    new ImportPlugin("resources"),
			    new CategoriesPlugin(),
			    new FeedbackPlugin());
		    vocTrainer.setVisible(true);
		}
		catch (Exception e) {
		    e.printStackTrace();
		}
	    }
	});
    }
}