package spl.vocTrainer;

import javax.swing.JButton;
import javax.swing.JOptionPane;

import spl.vocTrainer.utilities.LoginDialog;

public class VocTrainer {

    private JButton login;
    LoginDialog loginDialog;
    private boolean loggedIn;

    private void setupTrainerForDatabase() {
	original();
	login = new JButton("Login");
	login.addActionListener(this);
	buttonPanel.add(login);
	contentPane.revalidate();
    }

    public void actionPerformed(ActionEvent e) {
	original(e);
	if (e.getSource() == login) {
	    if (loggedIn) {
		System.out.println("Logged out!");
		loggedIn = false;
		login.setText("Login");
	    }
	    else {
		loginDialog = new LoginDialog(this);
		loginDialog.setVisible(true);

		if (loginDialog.isSucceeded()) {
		    // for now only display simple welcome message
		    System.out.println("Logged in as " + loginDialog.getUsername());
		    loggedIn = true;
		    login.setText("Logout");
		}
	    }
	}
    }
}