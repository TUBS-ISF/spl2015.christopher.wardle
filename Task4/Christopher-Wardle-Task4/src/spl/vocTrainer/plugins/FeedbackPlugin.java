package spl.vocTrainer.plugins;

import java.util.ArrayList;
import java.util.List;

import spl.vocTrainer.VocTrainer;
import spl.vocTrainer.plugins.interfaces.IFeedbackPlugin;
import spl.vocTrainer.utilities.Entry;

public class FeedbackPlugin implements IFeedbackPlugin {

    private VocTrainer app;
    private List<Entry> incorrectAnswers;
    private boolean lastRound;
    private int lastRoundIndex;

    public FeedbackPlugin() {
	incorrectAnswers = new ArrayList<Entry>();
    }

    public void setApplication(VocTrainer app) {
	this.app = app;
    }

    public void wrongAnswersFeedback(String answer, Entry solution) {
	if (solution.getValue().toUpperCase().equals(answer.toUpperCase())) {
	    app.appendText("\"" + answer + "\" is correct. Well done!\n");
	}
	else {
	    app.appendText("\"" + answer + "\" is not correct. " + "The correct answer would have been \"" + solution.getValue() + "\"\n");
	    incorrectAnswers.add(solution);
	}
    }

    public void evaluationFeedback() {
	lastRound = true;
	lastRoundIndex = 0;
    }

    public boolean isLastRound() {
	return lastRound;
    }

    public Entry getNextEntry() {
	if (lastRoundIndex < incorrectAnswers.size() - 1) {
	    return incorrectAnswers.get(++lastRoundIndex);
	}
	else {
	    return null;
	}
    }

    public Entry getCurrentEntry() {
	if (lastRoundIndex < incorrectAnswers.size()) {
	    return incorrectAnswers.get(lastRoundIndex);
	}
	else {
	    return null;
	}
    }
}
