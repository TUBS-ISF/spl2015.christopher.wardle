package spl.vocTrainer.plugins.interfaces;

import java.awt.event.ActionListener;

import spl.vocTrainer.VocTrainer;

public interface IImportPlugin {

    void setApplication(VocTrainer app);
    String getButtonText();
    ActionListener buttonClicked();
}
