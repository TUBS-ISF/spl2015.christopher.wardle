package spl.mediaPlayer.main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

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
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;

import org.farng.mp3.MP3File;
import org.farng.mp3.TagException;
import org.farng.mp3.id3.ID3v1;

public class GUI extends JFrame {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    // GUI fields
    private JPanel contentPane, btnPanel, songProgressPanel, ctrlPanel;
    private JLabel lblSongList, lblMetadata, lblSongEnd;
    private JTable metadataTable;
    private JButton btnRepeat, btnShuffle, btnPlay, btnStop, btnPause, btnPrevious, btnNext;
    private JSlider volumeSlider, songProgressSlider;
    private JSplitPane splitPane;
    private JScrollPane scrollPane;
    private Box metadataVBox;

    // MediaPlayer fields
    private final JFileChooser fileChooser = new JFileChooser("C:\\Users\\Christopher\\Downloads\\SFX");
    private MediaPlayer player;
    private File[] mediaFiles;
    private Timer songProgress;
    private int lastVolumeSliderValue;
    static SimpleDateFormat dateFormat = new SimpleDateFormat("mm:ss");

    private String[] repeatLables = { "Repeat Off", "Repeat One", "Repeat All" };

    public GUI() {
	this(new boolean[2]);
    }

    /**
     * Create the frame.
     */
    public GUI(boolean[] conf) {
	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	setTitle("Media Player");
	setBounds(300, 300, 800, 600);

	fileChooser.setMultiSelectionEnabled(true);

	contentPane = new JPanel();
	contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
	setContentPane(contentPane);
	contentPane.setLayout(new BorderLayout(0, 0));

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

	// Main panel holding the progress bar and all buttons
	btnPanel = new JPanel(new GridLayout(2, 1));
	btnPanel.setBorder(new LineBorder(new Color(0, 0, 0), 2));

	// Panel for the progress bar and the label showing the length of the song
	songProgressPanel = new JPanel(new FlowLayout());

	songProgressSlider = new JSlider();

	songProgressPanel.add(songProgressSlider);

	// Label for displaying the end of the song
	lblSongEnd = new JLabel("end");
	songProgressPanel.add(lblSongEnd);

	// Panel for play, pause etc. buttons
	ctrlPanel = new JPanel(new GridLayout(1, 8));

	if (conf[0]) {	//if option repeat is selected
	    btnRepeat = new JButton(repeatLables[0]);
	    ctrlPanel.add(btnRepeat);
	    btnRepeat.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    if (player != null) {
			player.setRepeatMode();
			btnRepeat.setText(repeatLables[player.getRepeatMode()]);
		    }
		}
	    });
	}

	if (conf[1]) {	//if option shuffle is selected
	    btnShuffle = new JButton("Shuffle Off");
	    ctrlPanel.add(btnShuffle);
	    btnShuffle.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    if (player != null) {
			player.setShuffle();
			btnShuffle.setText(player.getShuffle());
		    }
		}
	    });
	}

	btnPrevious = new JButton("<");
	ctrlPanel.add(btnPrevious);
	btnPrevious.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		if (player != null) {
		    player.playNewSong(false);
		    setMetadata(tableModel, mediaFiles[player.getCurrentlyPlayingSongIndex()]);
		}
	    }
	});

	btnPlay = new JButton("Play");
	ctrlPanel.add(btnPlay);
	btnPlay.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		if (player != null) {
		    player.play();
		}
	    }
	});

	btnNext = new JButton(">");
	ctrlPanel.add(btnNext);
	btnNext.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		if (player != null) {
		    player.playNewSong(true);
		    setMetadata(tableModel, mediaFiles[player.getCurrentlyPlayingSongIndex()]);
		}
	    }
	});

	btnPause = new JButton("Pause");
	ctrlPanel.add(btnPause);
	btnPause.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		if (player != null) {
		    player.pause();
		}
	    }
	});

	btnStop = new JButton("Stop");
	ctrlPanel.add(btnStop);
	btnStop.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		if (player != null) {
		    player.stop();
		}
	    }
	});

	volumeSlider = new JSlider();
	volumeSlider.setPaintTicks(true);
	volumeSlider.setSnapToTicks(true);
	volumeSlider.setEnabled(false);
	lastVolumeSliderValue = 80;
	volumeSlider.setValue(lastVolumeSliderValue);
	ctrlPanel.add(volumeSlider);

	volumeSlider.addChangeListener(new ChangeListener() {
	    public void stateChanged(ChangeEvent e) {
		if (player != null) {
		    player.setVolume(volumeSlider.getValue() - lastVolumeSliderValue);
		    lastVolumeSliderValue = volumeSlider.getValue();
		}
	    }
	});

	btnPanel.add(songProgressPanel);
	btnPanel.add(ctrlPanel);
	contentPane.add(btnPanel, BorderLayout.SOUTH);

	final JMenuBar menuBar = new JMenuBar();
	setJMenuBar(menuBar);

	final JMenu fileMenu = new JMenu("File");
	menuBar.add(fileMenu);

	final JMenuItem openItem = new JMenuItem("Open...");
	fileMenu.add(openItem);
	openItem.addActionListener(new ActionListener() {

	    public void actionPerformed(ActionEvent e) {

		if (e.getSource() == openItem) {
		    int answer = fileChooser.showOpenDialog(GUI.this);

		    if (answer == JFileChooser.APPROVE_OPTION) {
			mediaFiles = fileChooser.getSelectedFiles();
			setSonglist(listModel, mediaFiles);

			// if song is still playing, stop that song
			if (player != null) {
			    player.stop();
			}
			//stop the timer that updates song progress
			if (songProgress != null) {
			    songProgress.stop();
			}
			player = new MediaPlayer(mediaFiles);
			volumeSlider.setEnabled(true);
			songProgress = new Timer(100, updateSongProgress);
			songProgressSlider = new JSlider(0, player.getDuration(), 0);
			player.play();
			songProgress.start();
			setMetadata(tableModel, mediaFiles[player.getCurrentlyPlayingSongIndex()]);
		    }
		}
	    }
	});// openItem.addActionListener

	final JMenuItem exitItem = new JMenuItem("Exit");
	fileMenu.add(exitItem);
	exitItem.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		System.exit(EXIT_ON_CLOSE);
	    }
	});

    }

    ActionListener updateSongProgress = new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	    lblSongEnd.setText(GUI.dateFormat.format(player.getCurrentPosition()));
	    songProgressSlider.setValue(player.getCurrentPosition());
	    if (player.getCurrentPosition() == player.getDuration()) {
		player.onFinish();
	    }
	}
    };

    private void setMetadata(DefaultTableModel tableModel, File playingSong) {
	while (tableModel.getRowCount() > 0) {
	    tableModel.removeRow(0);
	}

	ID3v1 mp3File = null;
	try {
	    mp3File = new MP3File(playingSong).getID3v1Tag();
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
	    length = dateFormat.format(player.getDuration());
	    lblSongEnd.setText(length);
	    tableModel.addRow(new Object[] { "Length", length });
	}
	else {
	    tableModel.addRow(new Object[] { "No ID3v1 Tags available", "" });
	    tableModel.addRow(new Object[] { "Filename", playingSong.getName() });
	    lblSongEnd.setText(dateFormat.format(player.getDuration()));
	}
    }

    private void setSonglist(DefaultListModel listModel, File[] files) {
	listModel.clear();
	for (File file : files) {
	    listModel.addElement(file.getName());
	}
    }
}
