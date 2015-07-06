package spl.vocTrainer;

import spl.vocTrainer.utilities.Entry;

public class VocTrainer{

    private String correctAnswer(Entry solution){
	return original(solution) + " Well done!";
    }
    
    private String wrongAnswer(String answer, Entry solution){
	return original(answer, solution) + "\nThe correct answer would have been \""
		+solution.getValue() + "\".\n\n";
    }
}
