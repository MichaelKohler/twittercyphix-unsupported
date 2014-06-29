/*
 * MentionDMWindow.java
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

public class MentionDMWindow {
	Font font;
	JFrame mentionDMFrame;
	MainWindow mainWindow;

	/*
	 * Constructor : initializes either the "Mention" or the "DM"
	 * window.
	 *
	 * @param
	 *		- String aAction: can be either "mention" or "dm"
	 *		- String aMainWindow: a reference to the mainWindow
	 */
	public MentionDMWindow(String aAction, MainWindow aMainWindowRef) {
		mainWindow = aMainWindowRef;

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

		String windowLabel = "";
		if(aAction.equals("mentions")) {
			windowLabel = "Mentions";
		}
		else if(aAction.equals("dm")) {
			windowLabel = "Direct Messages";
		}

		mentionDMFrame = new JFrame(windowLabel);
		mentionDMFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // closes only this window and not this window
		mentionDMFrame.setMinimumSize(new Dimension(300 + 15*fontsize, 300 + 15*fontsize));
		mentionDMFrame.setPreferredSize(new Dimension(300 + 15*fontsize, 300 + 15*fontsize));
		mentionDMFrame.setVisible(true);

		// get the appropriate timeline
		createContent(aAction);
		createBottomPanel();
	}

	/*
	 * create the bottom panel
	 */
	private void createBottomPanel() {
		// Create bottom Panel (used for the "Close" button).
		JPanel bottomPanel = new JPanel();
		mentionDMFrame.getContentPane().add(bottomPanel, BorderLayout.SOUTH);

		// Create "Close" button on bottomPanel.
		JButton closeButton = new JButton("Close");
		closeButton.setFont(font);
		bottomPanel.add(closeButton);

		// Close the window when clicked on "Close" button.
		closeButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				mentionDMFrame.dispose();
			}
		});
	}

	/* creates the content container with the different tabs
	 * for the timeline
	 *
	 * @param
	 *		- String aAction: action to do
	 */
	public void createContent(String aAction) {
		mentionDMFrame.getContentPane().setFont(font);

		// Create timelinePanel for the normal Timeline.
		JPanel timelinePanel = new JPanel();

		// Create the timeline and it to the timelinePanel.
		timelinePanel.setLayout(new BoxLayout(timelinePanel, BoxLayout.Y_AXIS));
		Timeline timeline = new Timeline(mainWindow);
		String url = "";
		String type = "";
		if(aAction.equals("mentions")) {
			url = "http://twitter.com/statuses/mentions.xml";
			type = "mention";
		}
		else if(aAction.equals("dm")) {
			url = "http://twitter.com/direct_messages.xml";
			type = "dm";
		}
		ArrayList tweets = timeline.init(url, type);
		for(int i = 0; i < tweets.size(); i++) {
			JPanel singleTweet = (JPanel) tweets.get(i);
			timelinePanel.add(singleTweet);
		}

		// create the scrollbar
		JScrollPane scrollPane = new JScrollPane(timelinePanel);
		scrollPane.createVerticalScrollBar();
		mentionDMFrame.getContentPane().add(scrollPane);

		mentionDMFrame.pack();
	}
}
