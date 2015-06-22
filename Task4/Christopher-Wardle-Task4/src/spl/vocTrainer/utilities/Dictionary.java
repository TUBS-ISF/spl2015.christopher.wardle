package spl.vocTrainer.utilities;

import java.util.ArrayList;
import java.util.List;

public class Dictionary {

    private List<Category> categories;
    private String name;

    public Dictionary(String name) {
	setName(name);
	categories = new ArrayList<Category>();
    }

    public String getName() {
	return name;
    }

    private void setName(String name) {
	this.name = name;
    }

    public List<Category> getCategories() {
	return categories;
    }

    public void addCategory(Category c) {
	categories.add(c);
    }

    public Category getCategory(int index) {
	return categories.get(index);
    }

    public Category getCategory(String name) {
	for (Category c : categories) {
	    if (c.getName().equals(name)) {
		return c;
	    }
	}
	return null;
    }

    public Category getCategory(Category c) {
	return categories.get(categories.indexOf(c));
    }

    public int getCategoryIndex(Category c) {
	return categories.indexOf(c);
    }
}
