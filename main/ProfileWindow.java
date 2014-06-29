/*
 * ProfileWindow.java
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
import javax.swing.*;

public class ProfileWindow {
	private Font font;
	private JFrame profileFrame;
	private MainWindow mainWindow;
	private long userID;

	/*
	 * Constructor : initializes the profile window
	 *
	 * @param
	 *		- long aUserID: user id of the profile to show
	 *		- MainWindow aMainWindowRef: reference to the MainWindow
	 */
	public ProfileWindow(long aUserID, MainWindow aMainWindowRef) {
		userID = aUserID;
		mainWindow = aMainWindowRef;

		profileFrame = new JFrame("Profile");
		profileFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // closes only the "Profile" - window

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

		profileFrame.setMinimumSize(new Dimension(300 + 15*fontsize, 300 + 15*fontsize));
		profileFrame.setPreferredSize(new Dimension(300 + 15*fontsize, 300 + 15*fontsize));
		profileFrame.setVisible(true);

		createContent(aUserID);
		createBottomPanel();
	}

	/*
	 * create the bottom panel
	 */
	private void createBottomPanel() {
		// create bottom Panel (used for the "Close" button)
		JPanel bottomPanel = new JPanel();
		profileFrame.getContentPane().add(bottomPanel, BorderLayout.SOUTH);

		// create "Close" button on bottomPanel
		JButton closeButton = new JButton("Close");
		closeButton.setFont(font);
		bottomPanel.add(closeButton);

		// close the window when clicked on "Close" button
		closeButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				profileFrame.dispose();
			}
		});
	}

	/* creates the content container with the different tabs
	 * for the timeline
	 *
	 * @param
	 *		- long aUserID: user id of the user to get data from
	 */
	public void createContent(long aUserID) {
		profileFrame.getContentPane().setFont(font);
		profileFrame.getContentPane().setLayout(new BorderLayout());

		// create timelinePanel for the user's timeline
		JPanel timelinePanel = new JPanel();

		// create the timeline and it to the timelinePanel
		timelinePanel.setLayout(new BoxLayout(timelinePanel, BoxLayout.Y_AXIS));
		Timeline timeline = new Timeline(mainWindow);
		ArrayList tweets = timeline.init("http://twitter.com/statuses/user_timeline.xml?id=" + userID, "usertimeline");
		for(int i = 0; i < tweets.size(); i++) {
			JPanel singleTweet = (JPanel) tweets.get(i);
			timelinePanel.add(singleTweet);
		}

		// create the scrollbar
		JScrollPane scrollPane = new JScrollPane(timelinePanel);
		scrollPane.createVerticalScrollBar();
		profileFrame.getContentPane().add(scrollPane);

		profileFrame.pack();
	}

}
