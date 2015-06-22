package spl.vocTrainer.plugins.interfaces;

import spl.vocTrainer.VocTrainer;
import spl.vocTrainer.utilities.Entry;

public interface IFeedbackPlugin {

    void setApplication(VocTrainer app);
    void wrongAnswersFeedback(String answer, Entry solution);
    void evaluationFeedback();
}
