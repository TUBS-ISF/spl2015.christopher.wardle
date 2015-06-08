package spl.vocTrainer.utilities;

import java.util.ArrayList;
import java.util.List;


public class Category {
	
	private String name;

	private List<Entry> vocabularies;
		
	
	public Category(String n){
		setName(n);
		vocabularies = new ArrayList<Entry>();
	}
	
	public void addNewEntry(String key, String value){
		vocabularies.add(new Entry(key, value));
	}
	
	public String getKey(int index){
		return vocabularies.get(index).getKey();
	}
	
	public String getKey(String value){
		return vocabularies.get(vocabularies.indexOf(value)).getKey();
	}
	
	public String getValue(int index){
		return vocabularies.get(index).getValue();
	}
	
	public String getValue(String key){
		return vocabularies.get(vocabularies.indexOf(key)).getValue();
	}
	
	public String getName() {
		return name;
	}

	private void setName(String name) {
		this.name = name;
	}

	public List<Entry> getVocabularies() {
		return vocabularies;
	}

	public int getSize() {
		return vocabularies.size();
	}
}
