import spl.vocTrainer.VocTrainer;
import spl.vocTrainer.utilities.Entry;


public aspect EvaluationFeedback {
	pointcut p1(VocTrainer v, Entry solution): execution(String VocTrainer.correctAnswer(Entry)) && this(v) && args(solution);
	
	pointcut p2(VocTrainer v, String answer, Entry solution): execution(String VocTrainer.wrongAnswer(String, Entry)) && this(v) && args(answer, solution);
	
	after(VocTrainer v, Entry solution): p1(v,solution){
	    v.appendText(" Well done!");
	}
	
	after(VocTrainer v, String answer, Entry solution): p2(v,answer, solution){
	    v.appendText("\nThe correct answer would have been \"" + solution.getValue() + "\".\n\n");
	}
}