package spl.mediaPlayer.main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class MainFrame extends JFrame {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTable metadataTable;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
	EventQueue.invokeLater(new Runnable() {
	    public void run() {
		try {
		    MainFrame frame = new MainFrame();
		    frame.setVisible(true);
		}
		catch (Exception e) {
		    e.printStackTrace();
		}
	    }
	});
    }

    /**
     * Create the frame.
     */
    public MainFrame() {
	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	setBounds(300, 300, 600, 400);
	
	JMenuBar menuBar = new JMenuBar();
	setJMenuBar(menuBar);
	
	JMenu fileMenu = new JMenu("File");
	menuBar.add(fileMenu);
	
	JMenuItem openItem = new JMenuItem("Open...");
	fileMenu.add(openItem);
	
	JMenuItem exitItem = new JMenuItem("Exit");
	fileMenu.add(exitItem);
	contentPane = new JPanel();
	contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
	setContentPane(contentPane);
	contentPane.setLayout(new BorderLayout(0, 0));
	
	JPanel btnPanel = new JPanel();
	btnPanel.setBorder(new LineBorder(new Color(0, 0, 0), 2));
	contentPane.add(btnPanel, BorderLayout.SOUTH);
	GridBagLayout gbl_btnPanel = new GridBagLayout();
	gbl_btnPanel.columnWidths = new int[]{0, 60, 60, 0, 60, 60, 0, 0, 0, 0, 0, 0};
	gbl_btnPanel.rowHeights = new int[]{0, 23, 0};
	gbl_btnPanel.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
	gbl_btnPanel.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
	btnPanel.setLayout(gbl_btnPanel);
	
	JLabel label = new JLabel("0:00");
	GridBagConstraints gbc_label = new GridBagConstraints();
	gbc_label.insets = new Insets(0, 0, 5, 5);
	gbc_label.gridx = 1;
	gbc_label.gridy = 0;
	btnPanel.add(label, gbc_label);
	
	JProgressBar songProgressBar = new JProgressBar();
	GridBagConstraints gbc_songProgressBar = new GridBagConstraints();
	gbc_songProgressBar.fill = GridBagConstraints.HORIZONTAL;
	gbc_songProgressBar.gridwidth = 7;
	gbc_songProgressBar.insets = new Insets(0, 0, 5, 5);
	gbc_songProgressBar.gridx = 2;
	gbc_songProgressBar.gridy = 0;
	btnPanel.add(songProgressBar, gbc_songProgressBar);
	
	JLabel lblEnd = new JLabel("end");
	GridBagConstraints gbc_lblEnd = new GridBagConstraints();
	gbc_lblEnd.insets = new Insets(0, 0, 5, 5);
	gbc_lblEnd.gridx = 9;
	gbc_lblEnd.gridy = 0;
	btnPanel.add(lblEnd, gbc_lblEnd);
	
	JButton btnRepeat = new JButton("Repeat");
	btnRepeat.setHorizontalAlignment(SwingConstants.LEFT);
	GridBagConstraints gbc_btnRepeat = new GridBagConstraints();
	gbc_btnRepeat.fill = GridBagConstraints.BOTH;
	gbc_btnRepeat.insets = new Insets(0, 0, 0, 5);
	gbc_btnRepeat.gridx = 1;
	gbc_btnRepeat.gridy = 1;
	btnPanel.add(btnRepeat, gbc_btnRepeat);
	
	JButton btnShuffle = new JButton("Shuffle");
	GridBagConstraints gbc_btnShuffle = new GridBagConstraints();
	gbc_btnShuffle.fill = GridBagConstraints.BOTH;
	gbc_btnShuffle.insets = new Insets(0, 0, 0, 5);
	gbc_btnShuffle.gridx = 2;
	gbc_btnShuffle.gridy = 1;
	btnPanel.add(btnShuffle, gbc_btnShuffle);
	
	JButton btnPlay = new JButton("Play");
	GridBagConstraints gbc_btnPlay = new GridBagConstraints();
	gbc_btnPlay.gridwidth = 2;
	gbc_btnPlay.fill = GridBagConstraints.BOTH;
	gbc_btnPlay.insets = new Insets(0, 0, 0, 5);
	gbc_btnPlay.gridx = 4;
	gbc_btnPlay.gridy = 1;
	btnPanel.add(btnPlay, gbc_btnPlay);
	
	JButton btnPause = new JButton("Pause");
	GridBagConstraints gbc_btnPause = new GridBagConstraints();
	gbc_btnPause.fill = GridBagConstraints.BOTH;
	gbc_btnPause.insets = new Insets(0, 0, 0, 5);
	gbc_btnPause.gridx = 6;
	gbc_btnPause.gridy = 1;
	btnPanel.add(btnPause, gbc_btnPause);
	
	JButton btnStop = new JButton("Stop");
	GridBagConstraints gbc_btnStop = new GridBagConstraints();
	gbc_btnStop.fill = GridBagConstraints.BOTH;
	gbc_btnStop.insets = new Insets(0, 0, 0, 5);
	gbc_btnStop.gridx = 7;
	gbc_btnStop.gridy = 1;
	btnPanel.add(btnStop, gbc_btnStop);
	
	JSlider slider = new JSlider();
	slider.setPaintTicks(true);
	slider.setSnapToTicks(true);
	slider.setValue(80);
	GridBagConstraints gbc_slider = new GridBagConstraints();
	gbc_slider.fill = GridBagConstraints.HORIZONTAL;
	gbc_slider.gridwidth = 4;
	gbc_slider.gridx = 8;
	gbc_slider.gridy = 1;
	btnPanel.add(slider, gbc_slider);
	
	JSplitPane splitPane = new JSplitPane();
	
	JScrollPane scrollPane = new JScrollPane();
	JList songlist = new JList();
	songlist.setMinimumSize(new Dimension(300, 0));
	scrollPane.setViewportView(songlist);
	splitPane.setRightComponent(scrollPane);

	JLabel lblSongList = new JLabel("Song List");
	lblSongList.setFocusable(false);
	scrollPane.setColumnHeaderView(lblSongList);
	contentPane.add(splitPane, BorderLayout.CENTER);
	
	Box metadataVBox = Box.createVerticalBox();
	
	metadataTable = new JTable();
	metadataTable.setMinimumSize(new Dimension(200, 0));
	
	metadataVBox.add(metadataTable);
	
	JLabel lblMetadata = new JLabel("Metadata");
	lblMetadata.setHorizontalAlignment(SwingConstants.LEFT);
	lblMetadata.setFocusable(false);
	lblMetadata.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
	metadataVBox.add(lblMetadata);
	
	splitPane.setLeftComponent(metadataVBox);

    }

}
