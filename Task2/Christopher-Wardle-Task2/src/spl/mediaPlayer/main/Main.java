package spl.mediaPlayer.main;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class Main {

    /**
     * Launch the application.
     */
    public static void main(final String[] args) {
	// setup configuration
	boolean[] configValues = null;
	boolean invalidConf = false;
	if (args.length > 0) {
	    configValues = new boolean[2]; // two features: shuffle and repeat
	    for (String s : args) {
		switch (s.toUpperCase()) {
		case "REPEAT":
		    configValues[0] = true;
		    break;
		case "SHUFFLE":
		    configValues[1] = true;
		    break;
		default:
		    invalidConf = true;
		    break;
		}
	    }
	}
	
	final boolean[] conf = configValues;
	final boolean invalid = invalidConf;

	SwingUtilities.invokeLater(new Runnable() {	    
	    public void run() {
		try {
		    UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");

		    GUI mp3Player;
		    
		    if(conf != null && !invalid){
			mp3Player = new GUI(conf);
		    }
		    else{
			mp3Player = new GUI();
		    }
		    
		    mp3Player.setVisible(true);
		}
		catch (UnsupportedLookAndFeelException e) {
		    e.printStackTrace();
		}
		catch (ClassNotFoundException e) {
		    e.printStackTrace();
		}
		catch (InstantiationException e) {
		    e.printStackTrace();
		}
		catch (IllegalAccessException e) {
		    e.printStackTrace();
		}
		catch (Exception e) {
		    e.printStackTrace();
		}
	    }
	});
    }

}
