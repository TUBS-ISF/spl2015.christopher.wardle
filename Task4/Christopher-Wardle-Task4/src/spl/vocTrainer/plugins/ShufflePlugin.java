package spl.vocTrainer.plugins;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import spl.vocTrainer.VocTrainer;
import spl.vocTrainer.plugins.interfaces.IShufflePlugin;

public class ShufflePlugin implements IShufflePlugin {
 
    @SuppressWarnings("unused")
    private VocTrainer app;
    private boolean random;
    private JButton randomizeButton;

    private final Color RANDOM_OFF = new Color(238, 44, 44);
    private final Color RANDOM_ON = new Color(124, 205, 124);
    
    public ShufflePlugin(){
	randomizeButton = new JButton();
	randomizeButton.setBackground(RANDOM_OFF);
	randomizeButton.setText(getButtonText());
    }

    public String getButtonText() {
	return "Randomize";
    }

    public ActionListener buttonClicked() {
	ActionListener buttonListener = new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		setRandom(!getRandom());
		if (getRandom()) {
		    randomizeButton.setBackground(RANDOM_ON);
		}
		else {
		    randomizeButton.setBackground(RANDOM_OFF);
		}
	    }
	};
	return buttonListener;

    }

    public void setApplication(VocTrainer app) {
	this.app = app;
    }

    public boolean getRandom() {
	return random;
    }

    private void setRandom(boolean value) {
	this.random = value;
    }
    
    public JButton getRandomizeButton(){
	return randomizeButton;
    }
}
