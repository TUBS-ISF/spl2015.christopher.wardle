package spl.vocTrainer;

import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import com.mongodb.*;

public class VocTrainer {

    private JMenuItem connectDB;
    public static MongoClient client;
    public boolean connected;

    public VocTrainer() {
	connectDB = new JMenuItem("Connect to Database...");
	connectDB.addActionListener(this);
	fileMenu.add(connectDB);
    }

    public void actionPerformed(ActionEvent e) {
	original(e);
	if (e.getSource() == connectDB) {
	    // show pop-up for db address
	    // only the IP is required, the standard port is used (27017)
	    String s = JOptionPane.showInputDialog(this, "Please enter the IP of the MongoDB server you want to connect with.", "127.0.0.1");
	    if (s == null)
		return;

	    // connect to database
	    try {
		client = new MongoClient(s);
		if (client != null) {
		    setupTrainerForDatabase();
		}
	    }
	    catch (UnknownHostException ex) {
		ex.printStackTrace();
	    }
	}
    }

    private void setupTrainerForDatabase() {
	original();
	// for now only a simple connected message will be displayed
	System.out.println("Connected to database");
	connected = true;
    }
    
    public static MongoClient getDBClientInstance() throws UnknownHostException{
	if(client != null){
	    return client;
	}
	else{
	    return new MongoClient();
	}
    }
}