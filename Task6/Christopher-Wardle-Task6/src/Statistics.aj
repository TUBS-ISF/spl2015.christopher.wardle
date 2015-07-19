import java.awt.event.ActionEvent;
import java.net.UnknownHostException;

import javax.swing.JButton;

import spl.vocTrainer.VocTrainer;
import spl.vocTrainer.utilities.Entry;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;

public aspect Statistics {
    private JButton statistics;
    public static MongoClient client;

    pointcut p1(VocTrainer v): execution(VocTrainer.new()) && this(v);

    after(VocTrainer v): p1(v){
	statistics = new JButton("Get Statistics");
	statistics.addActionListener(v);
	v.getButtonPanel().add(statistics);
    }

    pointcut p2(VocTrainer v, ActionEvent e):execution(void VocTrainer.actionPerformed(ActionEvent)) && this(v) && args(e);

    after(VocTrainer v, ActionEvent e): p2(v,e){
	if (e.getSource() == statistics) {
	    try {
		client = VocTrainer.getDBClientInstance();
	    }
	    catch (UnknownHostException e1) {
		System.err.println("Couldn't find database");
		e1.printStackTrace();
	    }
	    if (client != null) {

		// get statistics for a user, if a user is logged in, otherwise get statistics of all users
		int nAll, nCorrect;
		DB db = client.getDB("voctrainer");
		DBCollection statisticCollection = db.getCollection("statistics");
		if (v.loggedIn) {
		    String username = v.user;
		    nAll = statisticCollection.find(new BasicDBObject().append("username", username)).count();
		    nCorrect = statisticCollection.find(new BasicDBObject().append("username", username).append("status", "correct")).count();
		}
		else {
		    nAll = statisticCollection.find().count();
		    nCorrect = statisticCollection.find(new BasicDBObject().append("status", "correct")).count();
		}
		double successrate = (nCorrect * 100 / nAll);
		v.setText("Current Statistics:\n\nOverall Vocabularies: " + nAll + "\nCorrect Answers: " + nCorrect + "\nSuccess Rate: " + successrate
			+ "%");
	    }
	}
    }

    pointcut p3(VocTrainer v, Entry solution): execution(String VocTrainer.correctAnswer(Entry)) && this(v) && args(solution);

    before(VocTrainer v, Entry solution):p3(v, solution){
	String username = v.user;
	try {
	    client = VocTrainer.getDBClientInstance();
	}
	catch (UnknownHostException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	DB db = client.getDB("voctrainer");
	DBCollection statisticCollection = db.getCollection("statistics");
	statisticCollection.insert(new BasicDBObject("username", username).append("key", solution.getKey()).append("value", solution.getValue())
		.append("answer", solution.getValue()).append("status", "correct"));
    }

    pointcut p4(VocTrainer v, String answer, Entry solution): execution(String VocTrainer.wrongAnswer(String, Entry)) && this(v) && args(answer, solution);

    before(VocTrainer v, String answer, Entry solution):p4(v, answer, solution){
	String username = v.user;
	try {
	    client = VocTrainer.getDBClientInstance();
	}
	catch (UnknownHostException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	DB db = client.getDB("voctrainer");
	DBCollection statisticCollection = db.getCollection("statistics");
	statisticCollection.insert(new BasicDBObject("username", username).append("key", solution.getKey()).append("value", solution.getValue())
		.append("answer", answer).append("status", "incorrect"));
    }

}