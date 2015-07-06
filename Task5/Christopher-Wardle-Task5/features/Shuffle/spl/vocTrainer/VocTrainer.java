package spl.vocTrainer;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import spl.vocTrainer.VocTrainer;

public class VocTrainer{
 
    private JButton randomize;

    private final Color RANDOM_OFF = new Color(238, 44, 44);
    private final Color RANDOM_ON = new Color(124, 205, 124);
    
    public VocTrainer(){
	randomize = new JButton();
	randomize.setBackground(RANDOM_OFF);
	randomize.setText("Randomize");
	randomize.addActionListener(this);
	buttonPanel.add(randomize);
    }

    public void actionPerformed(ActionEvent e){
	original(e);
	if(e.getSource() == randomize){
	    setRandom(!getRandom());
	    if(getRandom()){
		randomize.setBackground(RANDOM_ON);
	    }
	    else{
		randomize.setBackground(RANDOM_OFF);
	    }
	}
    }

    public boolean getRandom() {
	return random;
    }

    private void setRandom(boolean value) {
	this.random = value;
    }
}
