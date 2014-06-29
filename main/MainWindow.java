/*
 * MainWindow.java
 * Copyright 2009 Michael Kohler
 * See http://java.sun.com for the methods I used.
 * This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package twittercyphix.main;

import twittercyphix.twitter.*;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.*;

public class MainWindow extends TimerTask {
	public JFrame mainFrame;
	private Font font;
	private Twitter twitterHelper;
	private PrefWindow prefWindow;
	public String username;
	public String password;
	private ArrayList<JPanel> tweets;
	public long lastTweetID;
	public ArrayList<Long> displayedTweets = new ArrayList<Long>();
	private JPanel timelinePanel;
	private JScrollPane scrollPane;

	// the field for the tweet input and the variable to save
	// the ID of the tweet we want to reply to
	public JTextArea tweetField;
	public long inReplyToStatus;

	/*
	 * Constructor : initializes the main window and all at
	 * startup needed MainWindow components
	 *
	 * @param
	 *		- String aUsername: entered username
	 *		- String aPassword: entered password
	 */
	public MainWindow(String aUsername, String aPassword) {
		username = aUsername;
		password = aPassword;
		prefWindow = new PrefWindow(false);

		// get the font
		PrefWindow fontgetterObj = new PrefWindow(false);
		String[] fontproperties = fontgetterObj.getFont();
		String fontname = fontproperties[0];
		int fontsize = Integer.parseInt(fontproperties[2]);
		int fontvariant = Integer.parseInt(fontproperties[1]);
		if(fontvariant == 0)
			font = new Font(fontname, Font.PLAIN, fontsize);
		else if(fontvariant == 1)
			font = new Font(fontname, Font.BOLD, fontsize);
		else
			font = new Font(fontname, Font.ITALIC, fontsize);

		// create window TwitterCyphix
		mainFrame = new JFrame("TwitterCyphix");
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Toolkit.getDefaultToolkit().setDynamicLayout(true);
		mainFrame.setVisible(false);
		mainFrame.setMinimumSize(new Dimension(400 + 15*fontsize, 500 + 15*fontsize));
		mainFrame.setPreferredSize(new Dimension(400 + 15*fontsize, 500 + 15*fontsize));

		createMenuBar();
		createBottomPanel();
		createContent();

		twitterHelper = new Twitter(this);
		inReplyToStatus = -1L;

		// create Move-and-Resize-Listener and call saveWindowAttributes()
		// when the window is either moved or resized to save the new
		// position and size
		mainFrame.getContentPane().addHierarchyBoundsListener(new HierarchyBoundsListener(){
			@Override
			public void ancestorMoved(HierarchyEvent e) {
				saveWindowAttributes();
			}
			@Override
			public void ancestorResized(HierarchyEvent e) {
			}
	});

		mainFrame.setVisible(true);
	}

	/* creates the bottom panel for the bottom toolbar
	 */
	private void createBottomPanel() {
		// creates an empty panel on the bottom of the window
		JPanel bottomPanel = new JPanel();
		mainFrame.getContentPane().add(bottomPanel, BorderLayout.SOUTH);

		// create tweetPanel: Panel for tweetTextField
		JPanel tweetPanel = new JPanel();
		bottomPanel.add(tweetPanel);

		// create tweetTextField:  TextInput for writing new tweets
		tweetField = new JTextArea("");
		tweetField.setFont(font);
		tweetField.setLineWrap(true);
		tweetField.setColumns(30);
		tweetField.setRows(4);
		tweetPanel.add(new JScrollPane(tweetField));

		// create charAndSendPanel for left chars label and the "send" button
		JPanel charAndSendPanel = new JPanel();
		tweetPanel.add(charAndSendPanel);

		// create leftChars-Label to display the chars left
		final JLabel leftChars = new JLabel("140");
		leftChars.setFont(font);
		charAndSendPanel.add(leftChars);

		// change the number of chars left every time a key has been pressed
		tweetField.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) { }
			public void keyReleased(KeyEvent e) {
				int leftCharsNumb = 140 - tweetField.getText().length();

				if(tweetField.getText().length() != 0 && leftCharsNumb > 5) {
					leftChars.setText("" + leftCharsNumb);
				}
				// display "140" when there's no text in the field
				else if(tweetField.getText().length() == 0) {
					leftChars.setText("140");
				}
				// If the number is lower or equal 5, change the
				// color to orange (FF8C00).
				else if(leftCharsNumb > 0 && leftCharsNumb < 6) {
					leftChars.setText("<html><p style='color:#FF8C00;'>" + leftCharsNumb + "</p></html>");
				}
				// if the number is 0 or negative, change the color to red (8B0000)
				else if(leftCharsNumb < 1) {
					leftChars.setText("<html><p style='color:#8B0000;'>" + leftCharsNumb +
									  "</p></html>");
				}
			}
			public void keyTyped(KeyEvent e) { }
		});

		// create sendTweet-Button for sending the new written tweet
		JButton sendTweetButton = new JButton("Send");
		sendTweetButton.setFont(font);
		charAndSendPanel.add(sendTweetButton);

		// make "Send" button clickable and call sendTweet()
		sendTweetButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(twitterHelper.sendTweet(tweetField.getText(), inReplyToStatus)) {
					tweetField.setText("");
					inReplyToStatus = -1;
				}
			}
		});
	}

	/*
	 * creates the content container for the timeline
	 */
	public void createContent() {
		mainFrame.getContentPane().setFont(font);

		// create timelinePanel for the normal Timeline
		timelinePanel = new JPanel();

		// create the timeline and add it to the timelinePanel
		timelinePanel.setLayout(new BoxLayout(timelinePanel, BoxLayout.Y_AXIS));
		Timeline timeline = new Timeline(this);
		tweets = timeline.init("http://twitter.com/statuses/friends_timeline.xml", "");
		for(int i = 0; i < tweets.size(); i++) {
			JPanel singleTweet = (JPanel) tweets.get(i);
			timelinePanel.add(singleTweet);
		}

		// automatic updates
		int updateInterval = prefWindow.getUpdateInterval() * 60 * 1000;
		Timer timer = new Timer();
		timer.schedule(this, updateInterval, updateInterval);

		mainFrame.pack();

		// create the scrollbar for the timeline.
		scrollPane = new JScrollPane(timelinePanel);
		scrollPane.createVerticalScrollBar();
		mainFrame.getContentPane().add(scrollPane);

		// restore the window's size and position
		restore();
	}

	/*
	 * creates the menubar at the top of the MainWindow
	 */
	private void createMenuBar() {
		// create the menubar (empty)
		JMenuBar menuBar = new JMenuBar();

		// create "TwitterCyphix" - menu
		JMenu TwitterCyphixMenu = new JMenu("TwitterCyphix");
		TwitterCyphixMenu.setFont(font);

		JMenuItem minMI = new JMenuItem("Minimize");
		minMI.setFont(font);
		TwitterCyphixMenu.add(minMI);

		// make "Minimize" in "TwitterCyphix" clickable and
		// call minimizeWindow()
		minMI.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				mainFrame.setState(mainFrame.ICONIFIED);
			}
		});

		JMenuItem maxMI = new JMenuItem("Maximize");
		maxMI.setFont(font);
		TwitterCyphixMenu.add(maxMI);

		// make "Maximize" in "TwitterCyphix" clickable and
		// call maximizeWindow()
		maxMI.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				mainFrame.setExtendedState(mainFrame.MAXIMIZED_BOTH);
			}
		});

		JMenuItem updateMI = new JMenuItem("Update Timeline");
		updateMI.setFont(font);
		TwitterCyphixMenu.add(updateMI);

		// make "Update" in "TwitterCyphix" clickable and
		// call updateTimeline()
		updateMI.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				updateTimeline();
			}
		});

		JMenuItem preferencesMI = new JMenuItem("Preferences");
		preferencesMI.setFont(font);
		TwitterCyphixMenu.add(preferencesMI);

		// make "Preferences" in "TwitterCyphix" clickable
		// and call showPrefWindow()
		preferencesMI.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				new PrefWindow();
			}
		});

		JMenuItem tinyURLMI = new JMenuItem("tinyURL");
		tinyURLMI.setFont(font);
		TwitterCyphixMenu.add(tinyURLMI);

		// make "tinyURL" in "TwitterCyphix" clickable
		// and call showTinyURLWindow()
		tinyURLMI.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				showTinyURLWindow();
			}
		});

		JMenuItem exitMI = new JMenuItem("Exit");
		exitMI.setFont(font);
		TwitterCyphixMenu.add(exitMI);

		// make "Exit" in "TwitterCyphix" clickable and
		// exit the program
		exitMI.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				System.exit(0);
			}
		});

		// add the "TwitterCyphix" menu to the menubar
		menuBar.add(TwitterCyphixMenu);

		// create "View" - menu
		JMenu viewMenu = new JMenu("View");
		viewMenu.setFont(font);

		JMenuItem mentionsMI = new JMenuItem("@" + username);
		mentionsMI.setFont(font);
		viewMenu.add(mentionsMI);

		// make "@<username>" in "View" clickable and call showMentions()
		mentionsMI.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				showMentions();
			}
		});

		JMenuItem dmMI = new JMenuItem("Direct Messages");
		dmMI.setFont(font);
		viewMenu.add(dmMI);

		// make "Direct Messages" in "View" clickable and call showDMs()
		dmMI.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				showDMs();
			}
		});

		// add the "View" menu to the menubar
		menuBar.add(viewMenu);

		// create "Help" - menu
		JMenu helpMenu = new JMenu("Help");
		helpMenu.setFont(font);

		JMenuItem aboutMI = new JMenuItem("About");
		aboutMI.setFont(font);
		helpMenu.add(aboutMI);

		// make "About" in "Help" clickable and call showAbout()
		aboutMI.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				new AboutWindow();
			}
		});

		// add the "Help" menu to the menubar
		menuBar.add(helpMenu);

		// set menuBar as actual menubar
		mainFrame.setJMenuBar(menuBar);
	}

/************* METHODS FOR DIFFERENT ACTIONS *************/

	/*
	 * used when retweeting. Sets the instance variable to the
	 * appropriate value and sets the tweet field text
	 *
	 * @param
	 *		- String aScreenName: screenname of the user
	 *		- String aText: the text to retweet
	 */
	public void doRT(String aScreenName, String aText) {
		// get the retweet-format
		String retweetFormat = prefWindow.getRTFormat();

		// format the String and replace some information
		String retweetText = retweetFormat.replace("<username>", "@" + aScreenName)
							 .replace("<tweet>", aText);

		// insert the text into the field
		tweetField.setText(retweetText);
	}

	/*
	 * restores the size and the position of the window
	 */
	private void restore() {
		// restore the position and size of the window
		int windowPositionX = Integer.parseInt(prefWindow.getProperty("windowPositionX"));
		int windowPositionY = Integer.parseInt(prefWindow.getProperty("windowPositionY"));

		mainFrame.setLocation(windowPositionX, windowPositionY);
	}

	/*
	 * override the run() method of java.util.TimerTask
	 */
	@Override
	public void run() {
		updateTimeline();
	}

	/*
	 * saves the position and size of the MainWindow
	 */
	private void saveWindowAttributes() {
		// save the position of the window
		int windowPositionX = mainFrame.getX();
		int windowPositionY = mainFrame.getY();
		prefWindow.writeProperty("windowPositionX", "" + windowPositionX);
		prefWindow.writeProperty("windowPositionY", "" + windowPositionY);
	}

	/*
	 * shows the DM-Timeline
	 */
	private void showDMs() {
		new MentionDMWindow("dm", this);
	}

	/*
	 * shows the Mentions-Timeline
	 */
	private void showMentions() {
		new MentionDMWindow("mentions", this);
	}

	/*
	 * shows the tinyURL window
	 */
	private void showTinyURLWindow() {
		new TinyURLWindow(tweetField.getText(), this);
	}

	/*
	 * updates the timeline
	 */
	private void updateTimeline() {
		// get tweets newer than the last tweet's ID
		Timeline timeline = new Timeline(this);
		ArrayList<JPanel> tweets_updated = timeline.init("http://twitter.com/statuses/friends_timeline.xml?since_id=" + lastTweetID, "");

		if(tweets_updated.size() > 0) {
			// add these tweets to the tweets list
			for(int i = 0; i < tweets.size(); i++) {
				tweets_updated.add(tweets.get(i));
			}
			tweets = tweets_updated;

			// repaint the timeline
			mainFrame.getContentPane().remove(scrollPane);
			mainFrame.getContentPane().remove(timelinePanel);
			timelinePanel = new JPanel();
			timelinePanel.setLayout(new BoxLayout(timelinePanel, BoxLayout.Y_AXIS));
			for(int i = 0; i < tweets.size(); i++) {
				JPanel singleTweet = (JPanel) tweets.get(i);
				timelinePanel.add(singleTweet);
			}
			scrollPane = new JScrollPane(timelinePanel);
			scrollPane.createVerticalScrollBar();
			mainFrame.getContentPane().add(scrollPane);
			mainFrame.validate();
		}
	}
}