package spl.mediaPlayer.main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
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
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

import javazoom.jl.decoder.JavaLayerException;

import org.farng.mp3.MP3File;
import org.farng.mp3.TagException;
import org.farng.mp3.id3.ID3v1;

public class MainFrame extends JFrame {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private JPanel contentPane, btnPanel;
    private JLabel songLabelStart, songLabelEnd, lblSongList, lblMetadata;
    private JTable metadataTable;
    private JProgressBar songProgressBar;
    private JButton btnRepeat, btnShuffle, btnPlay, btnStop, btnPause;
    private JSlider volumeSlider;
    private JSplitPane splitPane;
    private JScrollPane scrollPane;
    private Box metadataVBox;
    private File lastPlayed;
    private final JFileChooser fileChooser = new JFileChooser("E:\\Musik\\V,W,X,Y,Z\\Yanagi Nagi");
    private MediaPlayer player;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
	SwingUtilities.invokeLater(new Runnable() {
	    public void run() {
		try {
		    UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");

		    MainFrame frame = new MainFrame();
		    frame.setVisible(true);
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

    /**
     * Create the frame.
     */
    public MainFrame() {
	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	setBounds(300, 300, 600, 400);

	fileChooser.setMultiSelectionEnabled(true);

	final JMenuBar menuBar = new JMenuBar();
	setJMenuBar(menuBar);

	final JMenu fileMenu = new JMenu("File");
	menuBar.add(fileMenu);

	final JMenuItem openItem = new JMenuItem("Open...");

	fileMenu.add(openItem);

	final JMenuItem exitItem = new JMenuItem("Exit");
	fileMenu.add(exitItem);
	contentPane = new JPanel();
	contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
	setContentPane(contentPane);
	contentPane.setLayout(new BorderLayout(0, 0));

	btnPanel = new JPanel();
	btnPanel.setBorder(new LineBorder(new Color(0, 0, 0), 2));
	contentPane.add(btnPanel, BorderLayout.SOUTH);
	GridBagLayout gbl_btnPanel = new GridBagLayout();
	gbl_btnPanel.columnWidths = new int[] { 0, 60, 60, 0, 60, 60, 0, 0, 0, 0, 0, 0 };
	gbl_btnPanel.rowHeights = new int[] { 0, 23, 0 };
	gbl_btnPanel.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
	gbl_btnPanel.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
	btnPanel.setLayout(gbl_btnPanel);

	// Labels for displaying the start and end of the song
	songLabelStart = new JLabel("0:00");
	GridBagConstraints gbc_label = new GridBagConstraints();
	gbc_label.insets = new Insets(0, 0, 5, 5);
	gbc_label.gridx = 1;
	gbc_label.gridy = 0;
	btnPanel.add(songLabelStart, gbc_label);

	songLabelEnd = new JLabel("end");
	GridBagConstraints gbc_lblEnd = new GridBagConstraints();
	gbc_lblEnd.insets = new Insets(0, 0, 5, 5);
	gbc_lblEnd.gridx = 9;
	gbc_lblEnd.gridy = 0;
	btnPanel.add(songLabelEnd, gbc_lblEnd);

	songProgressBar = new JProgressBar();
	GridBagConstraints gbc_songProgressBar = new GridBagConstraints();
	gbc_songProgressBar.fill = GridBagConstraints.HORIZONTAL;
	gbc_songProgressBar.gridwidth = 7;
	gbc_songProgressBar.insets = new Insets(0, 0, 5, 5);
	gbc_songProgressBar.gridx = 2;
	gbc_songProgressBar.gridy = 0;
	btnPanel.add(songProgressBar, gbc_songProgressBar);

	btnRepeat = new JButton("Repeat");
	btnRepeat.setHorizontalAlignment(SwingConstants.LEFT);
	GridBagConstraints gbc_btnRepeat = new GridBagConstraints();
	gbc_btnRepeat.fill = GridBagConstraints.BOTH;
	gbc_btnRepeat.insets = new Insets(0, 0, 0, 5);
	gbc_btnRepeat.gridx = 1;
	gbc_btnRepeat.gridy = 1;
	btnPanel.add(btnRepeat, gbc_btnRepeat);

	btnShuffle = new JButton("Shuffle");
	GridBagConstraints gbc_btnShuffle = new GridBagConstraints();
	gbc_btnShuffle.fill = GridBagConstraints.BOTH;
	gbc_btnShuffle.insets = new Insets(0, 0, 0, 5);
	gbc_btnShuffle.gridx = 2;
	gbc_btnShuffle.gridy = 1;
	btnPanel.add(btnShuffle, gbc_btnShuffle);

	btnPlay = new JButton("Play");
	GridBagConstraints gbc_btnPlay = new GridBagConstraints();
	gbc_btnPlay.gridwidth = 2;
	gbc_btnPlay.fill = GridBagConstraints.BOTH;
	gbc_btnPlay.insets = new Insets(0, 0, 0, 5);
	gbc_btnPlay.gridx = 4;
	gbc_btnPlay.gridy = 1;
	btnPanel.add(btnPlay, gbc_btnPlay);

	btnPause = new JButton("Pause");
	GridBagConstraints gbc_btnPause = new GridBagConstraints();
	gbc_btnPause.fill = GridBagConstraints.BOTH;
	gbc_btnPause.insets = new Insets(0, 0, 0, 5);
	gbc_btnPause.gridx = 6;
	gbc_btnPause.gridy = 1;
	btnPanel.add(btnPause, gbc_btnPause);

	btnStop = new JButton("Stop");
	GridBagConstraints gbc_btnStop = new GridBagConstraints();
	gbc_btnStop.fill = GridBagConstraints.BOTH;
	gbc_btnStop.insets = new Insets(0, 0, 0, 5);
	gbc_btnStop.gridx = 7;
	gbc_btnStop.gridy = 1;
	btnPanel.add(btnStop, gbc_btnStop);

	volumeSlider = new JSlider();
	volumeSlider.setPaintTicks(true);
	volumeSlider.setSnapToTicks(true);
	volumeSlider.setValue(80);
	GridBagConstraints gbc_slider = new GridBagConstraints();
	gbc_slider.fill = GridBagConstraints.HORIZONTAL;
	gbc_slider.gridwidth = 4;
	gbc_slider.gridx = 8;
	gbc_slider.gridy = 1;
	btnPanel.add(volumeSlider, gbc_slider);

	splitPane = new JSplitPane();

	final DefaultListModel listModel = new DefaultListModel();
	final JList songlist = new JList(listModel);
	scrollPane = new JScrollPane();
	songlist.setMinimumSize(new Dimension(300, 0));
	scrollPane.setViewportView(songlist);
	splitPane.setRightComponent(scrollPane);

	lblSongList = new JLabel("Song List");
	lblSongList.setFocusable(false);
	scrollPane.setColumnHeaderView(lblSongList);
	contentPane.add(splitPane, BorderLayout.CENTER);

	metadataVBox = Box.createVerticalBox();

	final DefaultTableModel tableModel = new DefaultTableModel(new Object[] { "Info", "Value" }, 0);
	metadataTable = new JTable(tableModel);
	metadataTable.setMinimumSize(new Dimension(200, 0));

	metadataVBox.add(metadataTable);

	lblMetadata = new JLabel("Metadata");
	lblMetadata.setHorizontalAlignment(SwingConstants.LEFT);
	lblMetadata.setFocusable(false);
	lblMetadata.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
	metadataVBox.add(lblMetadata);

	splitPane.setLeftComponent(metadataVBox);

	openItem.addActionListener(new ActionListener() {

	    public void actionPerformed(ActionEvent e) {

		if (e.getSource() == openItem) {
		    int answer = fileChooser.showOpenDialog(MainFrame.this);

		    if (answer == JFileChooser.APPROVE_OPTION) {
			final File[] file = fileChooser.getSelectedFiles();
			lastPlayed = file[0];
			setSonglist(listModel, file);

			// if song is still playing, stop that song
			if (player != null) {
			    player.stop();
			}
			// create a new MediaPlayer for the new media
			player = new MediaPlayer();

			try {
			    player.play(file[0]);
			    setMetadata(tableModel, file[0]);
			}
			catch (JavaLayerException e1) {
			    e1.printStackTrace();
			}
		    }
		}
	    }
	});// openItem.addActionListener

	btnPause.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		player.pause();
	    }
	});

	btnPlay.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		try {
		    player.play(lastPlayed);
		}
		catch (JavaLayerException e1) {
		    e1.printStackTrace();
		}
	    }
	});

	btnStop.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		player.stop();
	    }
	});

	exitItem.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		System.exit(EXIT_ON_CLOSE);
	    }
	});
    }

    private void setMetadata(DefaultTableModel tableModel, File file) {
	while (tableModel.getRowCount() > 0) {
	    tableModel.removeRow(0);
	}

	ID3v1 mp3File = null;
	try {
	    mp3File = new MP3File(file).getID3v1Tag();
	}
	catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	catch (TagException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	if (mp3File != null) {
	    tableModel.addRow(new Object[] { "Title", mp3File.getSongTitle() });
	    tableModel.addRow(new Object[] { "Artist", mp3File.getArtist() });
	    tableModel.addRow(new Object[] { "Album", mp3File.getAlbumTitle() });
	    
	    String length = "not accessible";
	    try {
		length = player.getDuration(file);
	    }
	    catch (UnsupportedAudioFileException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	    catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	    tableModel.addRow(new Object[] { "Length", length });
	}
	else {
	    tableModel.addRow(new Object[] { "No ID3v1 Tags available", "" });
	    tableModel.addRow(new Object[] { "Filename", file.getName() });
	}
    }

    private void setSonglist(DefaultListModel listModel, File[] files) {
	listModel.clear();
	for (File file : files) {
	    listModel.addElement(file.getName());
	}
    }
}
