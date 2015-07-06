package spl.vocTrainer;

import javax.swing.JOptionPane;

import spl.vocTrainer.utilities.Entry;

import java.util.List;
import java.util.ArrayList;

public class VocTrainer {

    private List<Entry> wrongAnswers = new ArrayList<Entry>();
    private boolean lastRound;
    private int lastRoundIndex;

    public boolean isLastRound() {
	return lastRound;
    }

    public void actionPerformed(ActionEvent e) {
	if (e.getSource() == exitItem) {
	    if (wrongAnswers != null && wrongAnswers.size() > 0) {
		int n = JOptionPane.showConfirmDialog(null, "Do you want to go through your wrong answers once more before you leave?",
			"Last Round", JOptionPane.YES_NO_CANCEL_OPTION);
		switch (n) {
		case 0:
		    lastRound = true;
		    lastRoundIndex = 0;
		    displayVocArea.append("\nLet's start the last round of vocabularies!\n");
		    Entry entry = getCurrentEntry();
		    if (entry != null) {
			nextVocabulary(entry);
		    }
		    break;
		case 1:
		    System.exit(EXIT_ON_CLOSE);
		    break;
		}
	    }
	    return; // don't execute the basic answering code if this is executed.
	}
	if (e.getSource() == this.answerField || e.getSource() == this.submitAnswer) {
	    String answer = getUserAnswer();
	    Entry solution;
	    if (isLastRound()) {
		solution = getCurrentWrongAnswer();
	    }
	    else {
		solution = getCurrentEntry();
	    }
	    evaluateAnswer(answer, solution);
	    if (isLastRound()) {
		Entry next = getNextWrongAnswer();
		if (next != null) {
		    nextVocabulary(next);
		}
		else {
		    Object[] options = { "Thanks!", "Bite me!" };
		    JOptionPane.showOptionDialog(this, "You've answered everything correctly. Well done!", "Last Round successful",
			    JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
		    System.exit(EXIT_ON_CLOSE);
		}
	    }
	    else {
		nextVocabulary();
	    }
	    answerField.setText("");

	    return; // don't execute the basic answering code if this is executed.
	}
	original(e);
    }

    public Entry getCurrentWrongAnswer() {
	if (lastRoundIndex < wrongAnswers.size()) {
	    return wrongAnswers.get(lastRoundIndex);
	}
	else {
	    return null;
	}
    }

    public Entry getNextWrongAnswer() {
	if (lastRoundIndex < wrongAnswers.size() - 1) {
	    return wrongAnswers.get(++lastRoundIndex);
	}
	else {
	    return null;
	}
    }

    private String wrongAnswer(String answer, Entry solution){
	wrongAnswers.add(solution);
	return original(answer, solution);
    }
}
