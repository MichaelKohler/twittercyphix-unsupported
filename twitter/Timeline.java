/*
 * Timeline.java
 * Copyright 2009 Michael Kohler
 * See http://java.sun.com for the methods I used.
 * The JDOM Library is downloaded from http://www.jdom.org/
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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import org.jdom.*;
import org.jdom.input.SAXBuilder;

public class Timeline {
	private ArrayList<JPanel> tweets;
	private Twitter twitterHelper;
	private MainWindow mainWindow;

	/*
	 * Constructor : initalizes the timeline
	 *
	 * @param
	 *		- MainWindow aMainWindowRef: a reference to the MainWindow class
	 */
	public Timeline(MainWindow aMainWindowRef) {
		mainWindow = aMainWindowRef;
		twitterHelper = new Twitter(aMainWindowRef);
	}

	/*
	 * creates the date of the tweet's creation (absolute
	 * or relative)
	 *
	 * @param
	 *     - String aDate: raw format date string (directly
	 *                      from the XML document)
	 * @return String dateString: Date string
	 */
	private String createDate(String aDate) {
		// map for the allocation of the months
		Map months = new HashMap();
		months.put("Jan", 1);
		months.put("Feb", 2);
		months.put("Mar", 3);
		months.put("Apr", 4);
		months.put("May", 5);
		months.put("Jun", 6);
		months.put("Jul", 7);
		months.put("Aug", 8);
		months.put("Sep", 9);
		months.put("Oct", 10);
		months.put("Nov", 11);
		months.put("Dec", 12);

		// extract the information
		String day = aDate.substring(8, 10);
		String month = aDate.substring(4, 7).toString();

		String year = aDate.substring(aDate.length() - 4, aDate.length());
		String hour = aDate.substring(11, 13);
		String minute = aDate.substring(14, 16);
		String seconds = aDate.substring(17, 19);
		String dateString = "on " + day + ". " + month + " " + year + " at " + hour + ":" + minute + ":" + seconds;

		return dateString;
	}

	/*
	 * initializes and returns the timeline
	 *
	 * @param
	 *		- String aURL: URL to get the XML document
	 *		- String aType: type of timeline to create
	 * @return ArrayList tweets: an array list with all the tweets
	 */
	public ArrayList init(String aURL, String aType) {
		InputStream in = twitterHelper.getXMLDocument(aURL);

		SAXBuilder parser = new SAXBuilder();
		Document document;
		try {
			document = parser.build(in);
			List statuses = document.getRootElement().getChildren();
			// create an ArrayList for the tweets
			tweets = new ArrayList(statuses.size());
			if(aType.equals("") || aType.equals("usertimeline") || aType.equals("mention")) {
				for(int i = 0; i < statuses.size(); i++) {
					long id = Long.parseLong(((Element) statuses.get(i)).getChildText("id"));
					if(i == 0)
						mainWindow.lastTweetID = id;

					// Hack: Check if this tweet is already displayed. Otherwise save the id to the
					// ArrayList of the already displayed tweets, so we can make sure we don't
					// display a tweet twice. This is unfortunately necessary because Twitter intermittently
					// serves too much tweets when getting tweets from the friends_timeline using the since_id parameter.
					boolean isAlreadyDisplayed = false;
					if(aType.equals("")) {
						for(int j = 0; j < mainWindow.displayedTweets.size(); j++) {
							if(mainWindow.displayedTweets.get(j).compareTo(id) == 0)
								isAlreadyDisplayed = true;
						}
					}

					if(!isAlreadyDisplayed) {
						mainWindow.displayedTweets.add(id);

						String text = ((Element) statuses.get(i)).getChildText("text");
						String inReplyToStatusTmp = ((Element) statuses.get(i)).getChildText("in_reply_to_status_id");

						// check if inReplyToStatusTmp is not empty.
						// if so, it becomes "null", would otherwise produce
						// an error when parsing to Long
						long inReplyToStatus;
						if(inReplyToStatusTmp.isEmpty()) {
							inReplyToStatus = -1L;
						}
						else {
							inReplyToStatus = Long.parseLong(inReplyToStatusTmp);
						}

						String inReplyToUser = ((Element) statuses.get(i)).getChildText("in_reply_to_screen_name");
						boolean favorited = Boolean.parseBoolean(((Element) statuses.get(i)).getChildText("favorited"));
						long userId = Long.parseLong(((Element) statuses.get(i)).getChild("user").getChildText("id"));
						String screenName = ((Element) statuses.get(i)).getChild("user").getChildText("screen_name");
						URL avatarURL = new URL(((Element) statuses.get(i)).getChild("user").getChildText("profile_image_url"));
						ImageIcon avatar = new ImageIcon(avatarURL, "user's avatar");

						// set the date
						String dateTmp = ((Element) statuses.get(i)).getChildText("created_at");
						String date = createDate(dateTmp);

						JPanel singleTweet = new Tweet(mainWindow, aType).init(id, date, text,
																			   inReplyToStatus, inReplyToUser,
																			   favorited, userId, screenName, avatar);
						tweets.add(singleTweet);
					}
				}
			}
			else if(aType.equals("dm")) {
				for(int i = 0; i < statuses.size(); i++) {
					long id =  Long.parseLong(((Element) statuses.get(i)).getChildText("id"));
					String dateTmp = ((Element) statuses.get(i)).getChildText("created_at");
					String date = createDate(dateTmp);
					String text = ((Element) statuses.get(i)).getChildText("text");
					long userId = Long.parseLong(((Element) statuses.get(i)).getChildText("sender_id"));
					String screenName = ((Element) statuses.get(i)).getChildText("sender_screen_name");
					URL avatarURL = new URL(((Element) statuses.get(i)).getChild("sender").getChildText("profile_image_url"));
					ImageIcon avatar = new ImageIcon(avatarURL, "user's avatar");

					JPanel singleDM = new Tweet(mainWindow, "dm").init(id, date, text,
																	   -1L, "", false, userId,
																	   screenName, avatar);
					tweets.add(singleDM);
				}
			}
		} catch (JDOMException ex) {
			new AlertWindow(ex.getMessage(), true);
		} catch (IOException ex) {
			new AlertWindow("Twittercyphix could not read from the input file which Twitter has sent.", true);
		}
		return tweets;
	}

}
