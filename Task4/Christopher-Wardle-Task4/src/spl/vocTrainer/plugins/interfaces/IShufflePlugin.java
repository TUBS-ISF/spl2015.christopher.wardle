package spl.vocTrainer.plugins.interfaces;

import java.awt.event.ActionListener;

import javax.swing.JButton;

import spl.vocTrainer.VocTrainer;

public interface IShufflePlugin {
    
    String getButtonText();
    void setApplication(VocTrainer app);
    ActionListener buttonClicked();
    boolean getRandom();
    JButton getRandomizeButton();
}
