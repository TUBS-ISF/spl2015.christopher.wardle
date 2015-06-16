package spl.vocTrainer.utilities;

public class Entry {

	private String key;
	private String value;
	
	public Entry(String key, String value){
		this.key = key;
		this.value = value;
	}
	
	public String getValue(){
		return this.value;
	}
	
	public String getKey(){
		return this.key;
	}
	
}
