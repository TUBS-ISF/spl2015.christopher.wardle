import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import spl.vocTrainer.VocTrainer;
import spl.vocTrainer.utilities.Entry;

public aspect WrongAnswersFeedback {

	private List<Entry> wrongAnswers = new ArrayList<Entry>();
	private boolean lastRound;
	private int lastRoundIndex;

	public boolean isLastRound() {
		return lastRound;
	}

	public Entry getCurrentWrongAnswer() {
		if (lastRoundIndex < wrongAnswers.size()) {
			return wrongAnswers.get(lastRoundIndex);
		} else {
			return null;
		}
	}

	public Entry getNextWrongAnswer() {
		if (lastRoundIndex < wrongAnswers.size() - 1) {
			return wrongAnswers.get(++lastRoundIndex);
		} else {
			return null;
		}
	}

	pointcut p1(VocTrainer v, ActionEvent e): execution(void VocTrainer.actionPerformed(ActionEvent)) && args(e) && this(v);

	void around(VocTrainer v, ActionEvent e): p1(v,e){
		if (e.getSource() == v.exitItem) {
			if (wrongAnswers != null && wrongAnswers.size() > 0) {
				int n = JOptionPane
						.showConfirmDialog(
								null,
								"Do you want to go through your wrong answers once more before you leave?",
								"Last Round", JOptionPane.YES_NO_CANCEL_OPTION);
				switch (n) {
				case 0:
					lastRound = true;
					lastRoundIndex = 0;
					v.displayVocArea
							.append("\nLet's start the last round of vocabularies!\n");
					Entry entry = v.getCurrentEntry();
					if (entry != null) {
						v.nextVocabulary(entry);
					}
					break;
				case 1:
					System.exit(JFrame.EXIT_ON_CLOSE);
					break;
				}
				return; // don't execute the basic answering code if this is
				// executed.
			}
		}
		if (e.getSource() == v.answerField
				|| e.getSource() == v.submitAnswer) {
			String answer = v.getUserAnswer();
			Entry solution;
			if (isLastRound()) {
				solution = getCurrentWrongAnswer();
			} else {
				solution = v.getCurrentEntry();
			}
			v.evaluateAnswer(answer, solution);
			if (isLastRound()) {
				Entry next = getNextWrongAnswer();
				if (next != null) {
					v.nextVocabulary(next);
				} else {
					Object[] options = { "Thanks!", "Bite me!" };
					JOptionPane.showOptionDialog(v,
							"You've answered everything correctly. Well done!",
							"Last Round successful", JOptionPane.YES_NO_OPTION,
							JOptionPane.PLAIN_MESSAGE, null, options,
							options[0]);
					System.exit(JFrame.EXIT_ON_CLOSE);
				}
			} else {
				v.nextVocabulary();
			}
			v.answerField.setText("");

			return; // don't execute the basic answering code if this is
					// executed.
		}
		proceed(v,e);
	}

	pointcut p2(String answer, Entry solution): execution(String wrongAnswer(String, Entry)) && args(answer, solution);

	String around(String answer, Entry solution): p2(answer, solution){
		wrongAnswers.add(solution);
		return proceed(answer, solution);
	}
}