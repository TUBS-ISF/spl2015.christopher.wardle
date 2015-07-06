package spl.vocTrainer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import spl.vocTrainer.utilities.Category;
import spl.vocTrainer.utilities.Dictionary;

public class VocTrainer {
    private JMenu categoryMenu;
    private JMenuItem checkAllItem;
    private boolean[] selectedCategories;

    public VocTrainer() {

    }

    public void actionPerformed(ActionEvent e) {
	original(e);
	if (e.getSource() == checkAllItem) {
	    if (checkAllItem.isSelected()) {
		checkAll(true);
	    }
	    else {
		checkAll(false);
	    }
	}
	else if (e.getSource() instanceof JCheckBoxMenuItem) {
	    Dictionary vocs = getVocabularies();
	    JCheckBoxMenuItem item = (JCheckBoxMenuItem) e.getSource();
	    Category c = vocs.getCategory(item.getText());
	    selectedCategories[vocs.getCategoryIndex(c) + 1] = item.isSelected();
	    checkAllItem.setSelected(false);
	}
    }

    public void startTest() {
	if (vocabularies != null) {
	    menuBar.add(createCategoryMenu());
	    contentPane.revalidate();
	    checkAll(true);
	}
	original();
    }

    public JMenu createCategoryMenu() {
	categoryMenu = new JMenu("Categories");

	// create CheckBoxMenuItem for checking all items
	checkAllItem = new JCheckBoxMenuItem("Check All");
	checkAllItem.addActionListener(this);
	categoryMenu.add(checkAllItem);
	Dictionary vocs = getVocabularies();
	for (Category c : vocs.getCategories()) {
	    JCheckBoxMenuItem item = new JCheckBoxMenuItem(c.getName());
	    item.addActionListener(this);
	    categoryMenu.add(item);
	}
	selectedCategories = new boolean[categoryMenu.getItemCount()];
	return categoryMenu;
    }

    public void checkAll(boolean value) {
	if (categoryMenu.getItemCount() > 0) {
	    for (int i = 0; i < categoryMenu.getItemCount(); i++) {
		categoryMenu.getItem(i).setSelected(value);
		selectedCategories[i] = value;
	    }
	}
    }

    public boolean[] getSelectedCategories() {
	return selectedCategories;
    }
    
    /**
     * Changes the category to another valid one (valid is every selected category).
     * 
     * @param selectedCategories
     *            Array with true values for every selected category
     */
    private void nextCategory(boolean[] selectedCategories) {
	if (random) {
	    do {
		currentCategory = vocabularies.getCategory(r.nextInt(vocabularies.getCategories().size()));
	    } while (!selectedCategories[vocabularies.getCategoryIndex(currentCategory) + 1]);
	}
	else if(currentVocabulary == vocabularies.getCategory(currentCategory).getSize() - 1){
	    do {
		currentCategory = vocabularies
			.getCategory((vocabularies.getCategoryIndex(currentCategory) + 1) % vocabularies.getCategories().size());
		currentVocabulary = -1;
	    } while (!selectedCategories[vocabularies.getCategoryIndex(currentCategory) + 1]);
	}
    }
    
    private void nextVocabulary(){
	nextCategory(selectedCategories);
	original();
    }
}
