package spl.vocTrainer;

import java.awt.EventQueue;


public class Main {

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
}