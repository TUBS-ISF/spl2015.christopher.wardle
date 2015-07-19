import java.awt.event.ActionEvent;

import javax.swing.JButton;

import spl.vocTrainer.VocTrainer;
import spl.vocTrainer.utilities.LoginDialog;

public aspect UserManagement {
	private JButton login;
	LoginDialog loginDialog;

	pointcut p1(VocTrainer v): execution(void *.setupTrainerForDatabase()) && this(v);

	after(VocTrainer v):p1(v){
		login = new JButton("Login");
		login.addActionListener(v);
		v.getButtonPanel().add(login);
		v.getContentPane().revalidate();
	}

	pointcut p2(VocTrainer v, ActionEvent e): execution(void VocTrainer.actionPerformed(ActionEvent)) && args(e) && this(v);

	after(VocTrainer v, ActionEvent e): p2(v, e){
		if (e.getSource() == login) {
			if (v.loggedIn) {
				System.out.println("Logged out!");
				v.loggedIn = false;
				login.setText("Login");
			} else {
				loginDialog = new LoginDialog(v);
				loginDialog.setVisible(true);

				if (loginDialog.isSucceeded()) {
					// for now only display simple welcome message
					System.out.println("Logged in as "
							+ loginDialog.getUsername());
					v.user = loginDialog.getUsername();
					v.loggedIn = true;
					login.setText("Logout");
				}
			}
		}
	}

}