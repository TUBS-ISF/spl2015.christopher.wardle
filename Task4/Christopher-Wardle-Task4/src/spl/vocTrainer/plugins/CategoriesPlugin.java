package spl.vocTrainer.plugins;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import spl.vocTrainer.VocTrainer;
import spl.vocTrainer.plugins.interfaces.ICategoriesPlugin;
import spl.vocTrainer.utilities.Category;
import spl.vocTrainer.utilities.Dictionary;

public class CategoriesPlugin implements ICategoriesPlugin {

    private VocTrainer app;
    private JMenu categoryMenu;
    private JMenuItem checkAllItem;
    private boolean[] selectedCategories;

    public void setApplication(VocTrainer app) {
	this.app = app;
    }

    public JMenu createCategoryMenu() {
	categoryMenu = new JMenu("Categories");

	// create CheckBoxMenuItem for checking all items
	checkAllItem = new JCheckBoxMenuItem("Check All");
	checkAllItem.addActionListener(checkedAll());
	categoryMenu.add(checkAllItem);
	Dictionary vocs = app.getVocabularies();
	for (Category c : vocs.getCategories()) {
	    JCheckBoxMenuItem item = new JCheckBoxMenuItem(c.getName());
	    item.addActionListener(checkedCategory());
	    categoryMenu.add(item);
	}
	selectedCategories = new boolean[categoryMenu.getItemCount()];
	return categoryMenu;
    }

    public ActionListener checkedAll() {
	ActionListener checkAllListener = new ActionListener() {

	    public void actionPerformed(ActionEvent e) {
		if (checkAllItem.isSelected()) {
		    checkAll(true);
		}
		else {
		    checkAll(false);
		}

	    }
	};
	return checkAllListener;
    }

    public ActionListener checkedCategory() {
	ActionListener checkCategoryListener = new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		Dictionary vocs = app.getVocabularies();
		JCheckBoxMenuItem item = (JCheckBoxMenuItem) e.getSource();
		Category c = vocs.getCategory(item.getText());
		selectedCategories[vocs.getCategoryIndex(c) + 1] = item.isSelected();
	    }
	};
	return checkCategoryListener;
    }

    public void checkAll(boolean value) {
	if (categoryMenu.getItemCount() > 0) {
	    for (int i = 0; i < categoryMenu.getItemCount(); i++) {
		categoryMenu.getItem(i).setSelected(value);
		selectedCategories[i] = value;
	    }
	}
    }
    
    public boolean[] getSelectedCategories(){
	return selectedCategories;
    }
}
