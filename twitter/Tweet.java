/*
 * Tweet.java
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

package twittercyphix.twitter;

import twittercyphix.main.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class Tweet {
	private Font font;
	private int fontsize;
	private MainWindow mainWindow;
	private Long id;
	private String screenName;
	private String type;

	/*
	 * Constructor : initializes the single tweet
	 *
	 * @param
	 *		- MainWindow aMainWindowRef: reference to the MainWindow class
	 *		- String aType: type of tweet
	 * @return none
	 */
	public Tweet(MainWindow aMainWindowRef, String aType) {
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

		mainWindow = aMainWindowRef;
		type = aType;
	}

	/*
	 * creates a single tweet
	 *
	 * @param
	 *		- long aID: the id of the tweet
	 *		- String aDate: the tweet's creation date (relative or absolute)
	 *		- String aText: the twittered text
	 *		- long aInReplyToStatus (optional): to which tweet this tweet is a reply
	 *		- String aInReplyToUser (optional): to which user's tweet this tweet is a reply
	 *		- boolean aFavorited: favorited by the user or not
	 *		- long aUserID: user ID of the twittering user
	 *		- String aScreenName: the user's screen name
	 *		- ImageIcon aAvatar: the user's avatar
	 * @return
	 *		- JPanel tweet: the returned JPanel is the whole tweet design
	 */
	public JPanel init(final long aID, String aDate, final String aText,
					   long aInReplyToStatus, String aInReplyToUser,
					   boolean aFavorited, final long aUserID, final String aScreenName,
					   ImageIcon aAvatar) {
		id = aID;
		screenName = aScreenName;

		// create a panel for the tweet (works as container).
		final JPanel tweet = new JPanel();
		tweet.setLayout(new BorderLayout());
		tweet.setBorder(new EmptyBorder(10, 0, 10, 0));
		tweet.setBackground(new Color(238, 220, 130));

		// avatar
		JLabel avatar;
		final JPanel avatarPanel = new JPanel();
		avatarPanel.setBackground(new Color(238, 220, 130));
		if(aAvatar.getIconHeight() > 48 || aAvatar.getIconWidth() > 48) {
			Image oldAvatar = aAvatar.getImage();
			Image newimg = oldAvatar.getScaledInstance(48, 48, java.awt.Image.SCALE_SMOOTH);
			ImageIcon newAvatar = new ImageIcon(newimg);
			avatar = new JLabel(newAvatar);
		}
		else {
			avatar = new JLabel(aAvatar);
		}
		avatarPanel.add(avatar);
		tweet.add(avatarPanel, BorderLayout.WEST);

		// content
		final JPanel textPanel = new JPanel();
		textPanel.setLayout(new BorderLayout());
		textPanel.setBorder(new EmptyBorder(0, 5, 0, 5));
		textPanel.setBackground(new Color(238, 220, 130));

		final JTextArea twitteredText = new JTextArea(aScreenName + ": " + aText);
		twitteredText.setFont(font);
		twitteredText.setEditable(false);
		twitteredText.setLineWrap(true);
		twitteredText.setWrapStyleWord(true);
		twitteredText.setBackground(new Color(238, 220, 130));
		twitteredText.setMinimumSize(new Dimension(100, 20 + 5*fontsize));
		textPanel.add(twitteredText, BorderLayout.NORTH);

		JLabel dateLabel;
		if(aInReplyToStatus == -1 || aInReplyToUser.equals("")) {
			dateLabel =  new JLabel(aDate);
		}
		else {
			dateLabel = new JLabel(aDate + ", in reply to " + aInReplyToUser);
		}
		dateLabel.setFont(font);
		textPanel.add(dateLabel, BorderLayout.SOUTH);

		tweet.add(textPanel, BorderLayout.CENTER);

		// only show the context menu if it isn't a direct message
		// and don't display the "Show this user's profile"
		// menuitem if it's already the user's timeline
		if (!type.equals("dm")) {
			/****************** CONTEXT-MENU *****************/
			JPopupMenu contextMenu = new JPopupMenu("");

			// reply-menuitem
			JMenuItem replyMI = new JMenuItem("Reply");
			replyMI.setFont(font);
			contextMenu.add(replyMI);

			replyMI.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					// set the instance variable in MainWindow to the ID
					// of the tweet we want to reply to
					mainWindow.inReplyToStatus = aID;
					mainWindow.tweetField.setText("@" + aScreenName);
				}
			});

			// retweet-menuitem
			JMenuItem retweetMI = new JMenuItem("Retweet");
			retweetMI.setFont(font);
			contextMenu.add(retweetMI);

			retweetMI.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					// input the ScreenName and the text to retweet
					// into the tweetfield
					mainWindow.doRT(aScreenName, aText);
				}
			});

			if(!type.equals("usertimeline")) {
				// show profile menuitem
				JMenuItem showProfileMI = new JMenuItem("Show this user's timeline");
				showProfileMI.setFont(font);
				contextMenu.add(showProfileMI);

				showProfileMI.addMouseListener(new MouseAdapter() {
					@Override
					public void mousePressed(MouseEvent e) {
						// display the profile window
						new ProfileWindow(aUserID, mainWindow);
					}
				});
			}

			tweet.setComponentPopupMenu(contextMenu);
			textPanel.setInheritsPopupMenu(true);
			twitteredText.setInheritsPopupMenu(true);

			// mark the tweet as unread.
			tweet.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					// set the tweet as read (gray background)
					tweet.setBackground(new Color(238, 238, 238));
					textPanel.setBackground(new Color(238, 238, 238));
					avatarPanel.setBackground(new Color(238, 238, 238));
					twitteredText.setBackground(new Color(238, 238, 238));
				}
			});

			twitteredText.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					// set the tweet as read (gray background)
					tweet.setBackground(new Color(238, 238, 238));
					textPanel.setBackground(new Color(238, 238, 238));
					avatarPanel.setBackground(new Color(238, 238, 238));
					twitteredText.setBackground(new Color(238, 238, 238));
				}
			});
		}

		return tweet;
	}
}