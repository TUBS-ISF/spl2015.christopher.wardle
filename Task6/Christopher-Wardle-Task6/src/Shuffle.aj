import java.awt.Color;
import java.awt.event.ActionEvent;

import javax.swing.JButton;

import spl.vocTrainer.VocTrainer;

public aspect Shuffle {
	private JButton randomize;

	private final Color RANDOM_OFF = new Color(238, 44, 44);
	private final Color RANDOM_ON = new Color(124, 205, 124);

	pointcut p1(VocTrainer v): execution(VocTrainer.new()) && this(v);

	after(VocTrainer v): p1(v){
		randomize = new JButton();
		randomize.setBackground(RANDOM_OFF);
		randomize.setText("Randomize");
		randomize.addActionListener(v);
		v.getButtonPanel().add(randomize);
	}

	pointcut p2(VocTrainer v, ActionEvent e): execution(void VocTrainer.actionPerformed(ActionEvent)) && this(v) && args(e);

	after(VocTrainer v, ActionEvent e): p2(v,e){
		if (e.getSource() == randomize) {
			v.random = !v.random;
			if (v.random) {
				randomize.setBackground(RANDOM_ON);
			} else {
				randomize.setBackground(RANDOM_OFF);
			}
		}
	}

}