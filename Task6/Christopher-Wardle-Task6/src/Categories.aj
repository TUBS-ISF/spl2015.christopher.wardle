import java.awt.event.ActionEvent;
import java.util.Random;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import spl.vocTrainer.VocTrainer;
import spl.vocTrainer.utilities.Category;
import spl.vocTrainer.utilities.Dictionary;

public aspect Categories {
    private JMenu categoryMenu;
    private JMenuItem checkAllItem;
    private boolean[] selectedCategories;

    pointcut p1(VocTrainer v, ActionEvent e): execution(void VocTrainer.actionPerformed(ActionEvent)) && args(e) && this(v);

    after(VocTrainer v, ActionEvent e): p1(v,e){
	if (e.getSource() == checkAllItem) {
	    if (checkAllItem.isSelected()) {
		checkAll(true);
	    }
	    else {
		checkAll(false);
	    }
	}
	else if (e.getSource() instanceof JCheckBoxMenuItem) {
	    Dictionary vocs = v.getVocabularies();
	    JCheckBoxMenuItem item = (JCheckBoxMenuItem) e.getSource();
	    Category c = vocs.getCategory(item.getText());
	    selectedCategories[vocs.getCategoryIndex(c) + 1] = item.isSelected();
	    checkAllItem.setSelected(false);
	}
    }

    pointcut p2(VocTrainer v): execution(void VocTrainer.startTest()) && this(v);

    after(VocTrainer v): p2(v){
	categoryMenu = new JMenu("Categories");

	// create CheckBoxMenuItem for checking all items
	checkAllItem = new JCheckBoxMenuItem("Check All");
	checkAllItem.addActionListener(v);
	categoryMenu.add(checkAllItem);
	Dictionary vocs = v.getVocabularies();
	for (Category c : vocs.getCategories()) {
	    JCheckBoxMenuItem item = new JCheckBoxMenuItem(c.getName());
	    item.addActionListener(v);
	    categoryMenu.add(item);
	}
	selectedCategories = new boolean[categoryMenu.getItemCount()];

	v.getJMenuBar().add(categoryMenu);
	v.getContentPane().revalidate();
	checkAll(true);
    }

    public void checkAll(boolean value) {
	if (categoryMenu.getItemCount() > 0) {
	    for (int i = 0; i < categoryMenu.getItemCount(); i++) {
		categoryMenu.getItem(i).setSelected(value);
		selectedCategories[i] = value;
	    }
	}
    }

    pointcut p3(VocTrainer v): execution(void VocTrainer.nextVocabulary()) && this(v);

    before(VocTrainer v): p3(v){
	Dictionary vocabularies = v.getVocabularies();
	Random r = new Random();
	if (v.random) {
	    do {
		v.currentCategory = vocabularies.getCategory(r.nextInt(vocabularies.getCategories().size()));
	    } while (!selectedCategories[vocabularies.getCategoryIndex(v.currentCategory) + 1]);
	}
	else if (v.currentVocabulary == vocabularies.getCategory(v.currentCategory).getSize() - 1) {
	    do {
		v.currentCategory = vocabularies.getCategory((vocabularies.getCategoryIndex(v.currentCategory) + 1)
			% vocabularies.getCategories().size());
		v.currentVocabulary = -1;
	    } while (!selectedCategories[vocabularies.getCategoryIndex(v.currentCategory) + 1]);
	}
    }

    public boolean[] getSelectedCategories() {
	return selectedCategories;
    }
}