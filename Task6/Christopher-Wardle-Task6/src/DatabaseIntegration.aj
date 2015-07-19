import java.awt.event.ActionEvent;
import java.net.UnknownHostException;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import spl.vocTrainer.VocTrainer;

import com.mongodb.MongoClient;

public aspect DatabaseIntegration {
	private JMenuItem connectDB;
	public static MongoClient client;
	public boolean connected;

	pointcut p1(VocTrainer v): execution(VocTrainer.new()) && this(v);

	after(VocTrainer v): p1(v){
		connectDB = new JMenuItem("Connect to Database...");
		connectDB.addActionListener(v);
		v.getFileMenu().add(connectDB);
	}

	pointcut p2(VocTrainer v, ActionEvent e): execution(void VocTrainer.actionPerformed(ActionEvent)) && args(e) && this(v);

	after(VocTrainer v, ActionEvent e): p2(v,e){
		if (e.getSource() == connectDB) {
			// show pop-up for db address
			// only the IP is required, the standard port is used (27017)
			String s = JOptionPane
					.showInputDialog(
							v,
							"Please enter the IP of the MongoDB server you want to connect with.",
							"127.0.0.1");
			if (s == null)
				return;

			// connect to database
			try {
				client = new MongoClient(s);
				if (client != null) {
					v.setupTrainerForDatabase();
				}
			} catch (UnknownHostException ex) {
				ex.printStackTrace();
			}
		}
	}

	pointcut p3(VocTrainer v): execution(void VocTrainer.setupTrainerForDatabase()) && this(v);
	
	after(VocTrainer v): p3(v) {
		// for now only a simple connected message will be displayed
		System.out.println("Connected to database");
		connected = true;
	}

	public static MongoClient getDBClientInstance() throws UnknownHostException {
		if (client != null) {
			return client;
		} else {
			return new MongoClient();
		}
	}
}