package spl.vocTrainer.plugins.interfaces;

import java.awt.event.ActionListener;

import javax.swing.JMenu;

import spl.vocTrainer.VocTrainer;

public interface ICategoriesPlugin {

    void setApplication(VocTrainer app);
    JMenu createCategoryMenu();
    ActionListener checkedAll();
    ActionListener checkedCategory();
}
