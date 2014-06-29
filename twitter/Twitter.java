/*
 * Twitter.java
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

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class Twitter {
	private MainWindow mainWindow;
	private InputStream inputStream;

	/*
	 * Constructor : just assigns the global variables.
	 *
	 * @param
	 *		- String aUsername: the username to authenticate
	 *		- String aPassword: the password to authenticate
	 */
	public Twitter(MainWindow aMainWindowRef) {
		mainWindow = aMainWindowRef;
	}

	/*
	 * Constructor : doesn't do anything
	 */
	public Twitter() {
	}

	/*
	 * authenticates the user on twitter
	 *
	 * @return HttpURLConnection connection: the connection
	 */
	public HttpURLConnection authenticate(String aURL) {
		HttpURLConnection connection = null;
		try {
			URL url = new URL(aURL);
			connection = (HttpURLConnection) url.openConnection();
			String userpasswd = mainWindow.username + ":" + mainWindow.password;
			String base64encoded = "Basic " + new sun.misc.BASE64Encoder().encode(userpasswd.getBytes());
			connection.setRequestMethod("GET");
			connection.setRequestProperty("Authorization", base64encoded);
		}
		catch (MalformedURLException ex) {
			new AlertWindow("The URL was wrong: " + ex.getMessage(), true);
		}
		catch (IOException ex) {
			new AlertWindow("Twittercyphix could not read from the file which Twitter has sent.", true);
		}
		return connection;
	}

	/*
	 * connects to Twitter, checks if entered credentials are valid
	 *
	 * @param
	 *		- String aUsername : entered username.
	 *		- String aPassword : entered password
	 * @return
	 *		- true if connection is established
	 */
	public boolean connect(String aUsername, String aPassword) {
		boolean ok = false;

		// check if credentials are valid
		try {
			URL url = new URL("http://twitter.com/account/verify_credentials.xml");
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			String userpasswd = aUsername + ":" + aPassword;
			String base64encoded = "Basic " + new sun.misc.BASE64Encoder().encode(userpasswd.getBytes());
			connection.setRequestProperty("Authorization", base64encoded);

			int responseCode = connection.getResponseCode();
			String alertMessage = "<html><p>Response Code: " + connection.getResponseCode() + "!<br />" +
								  "Reponse Message: " + connection.getResponseMessage();

			// set state (ok) to true when response code is okay, if not okay,
			// display an alert window with the according error message
			if(responseCode == connection.HTTP_OK) {
				ok = true;
			}
			else if(responseCode == connection.HTTP_UNAUTHORIZED) {
				ok = false;
				new AlertWindow(alertMessage + "<br /><br />Wrong password or username! Try again!</p></html>", true);
			}
			else {
				new AlertWindow(alertMessage, true);
				ok = false;
			}
		}
		catch (MalformedURLException ex) {
			new AlertWindow("The URL was wrong: " + ex.getMessage(), true);
		}
		catch (IOException ex) {
			new AlertWindow("Twittercyphix could not read from the file which Twitter has sent.", true);
		}

		return ok;
	}

	/*
	 * Gets the XML document from Twitter
	 *
	 * @param
	 *		- String aURLString: url to get the xml from
	 * @return
	 *		- InputStream inputStream with the XML document
	 */
	public InputStream getXMLDocument(String aURLString) {
		try {
			HttpURLConnection connection = authenticate(aURLString);
			inputStream = connection.getInputStream();
		}
		catch (IOException ex) {
			new AlertWindow("Twittercyphix could not read from the file which Twitter has sent.", true);
		}

		return inputStream;
	}

	/*
	 * sends the tweet to twitter
	 *
	 * @param
	 *		- String aMessage: text to send
	 *		- String aInReplyToStatus: send as reply to tweet with this ID
	 * @return
	 		- true if submission was successful
	 */
	public boolean sendTweet(String aMessage, long aInReplyToStatus) {
		boolean ok = false;
		try {
			HttpURLConnection connection = authenticate("http://twitter.com/statuses/update.xml");

			// send POST data (message and ID which this tweet is a reply to)
			String data = URLEncoder.encode("status", "UTF-8") + "=" + URLEncoder.encode(aMessage, "UTF-8");
			connection.setDoInput(true);
			connection.setDoOutput(true);
			PrintStream printStream = new PrintStream(connection.getOutputStream());
			if(aInReplyToStatus != -1) {
				data += "&"+  URLEncoder.encode("in_reply_to_status_id", "UTF-8") + "=" +
						  URLEncoder.encode(String.valueOf(aInReplyToStatus), "UTF-8");
			}
			printStream.println(data);
			printStream.close();

			// set state (ok) to true when response code is okay, if not okay,
			// display an alert window with the according error message
			if(connection.getResponseCode() == connection.HTTP_OK) {
				ok = true;
			}
			else {
				new AlertWindow("<html><p>Response Code: " + connection.getResponseCode() + "!<br />" +
								"Reponse Message: " + connection.getResponseMessage() + "<br /><br />" +
								"Your tweet was not submitted, please try again!!</p></html>", true);
				ok = false;
			}
		}
		catch (MalformedURLException ex) {
			new AlertWindow("The URL was wrong: " + ex.getMessage(), true);
		}
		catch (IOException ex) {
			new AlertWindow("Twittercyphix could not read from the file which Twitter has sent.", true);
		}

		return ok;
	}
}