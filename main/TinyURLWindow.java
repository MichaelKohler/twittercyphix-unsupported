/*
 * TinyURLWindow.java
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

import java.awt.*;
import java.awt.event.*;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.*;

public class TinyURLWindow {
	Font font;
	JFrame tinyURLFrame;
	MainWindow mainWindow;
	String currentText;
	JTextArea inputField;

	/*
	 * Constructor : initializes the tinyURL window
	 *
	 * @param
	 *		- String aCurrentText: the current text of the tweet field
	 *		- MainWindow aMainWindowRef: a reference to the MainWindow
	 */
	public TinyURLWindow(String aCurrentText, MainWindow aMainWindowRef) {
		currentText = aCurrentText;
		mainWindow = aMainWindowRef;

		tinyURLFrame = new JFrame("Shorten a link with tinyURL");
		tinyURLFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // closes only the tinyURL - window

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

		tinyURLFrame.setMinimumSize(new Dimension(300 + 15*fontsize, 10*fontsize));
		tinyURLFrame.setPreferredSize(new Dimension(300 + 15*fontsize, 10*fontsize));
		tinyURLFrame.setVisible(true);

		createContent();
		createBottomPanel();
	}

	/*
	 * create the bottom panel
	 */
	private void createBottomPanel() {
		JPanel bottomPanel = new JPanel();
		tinyURLFrame.getContentPane().add(bottomPanel, BorderLayout.SOUTH);

		// create "Insert" button on bottomPanel
		JButton insertButton = new JButton("Insert");
		insertButton.setFont(font);
		bottomPanel.add(insertButton);

		// close the window when clicked on "Insert" button and
		// insert the shortened URL into the tweet field
		insertButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				String shortedURL = shortLink(inputField.getText());
				mainWindow.tweetField.setText(currentText + " " + shortedURL);
				tinyURLFrame.dispose();
			}
		});

		// create "Close" button on bottomPanel
		JButton closeButton = new JButton("Close");
		closeButton.setFont(font);
		bottomPanel.add(closeButton);

		// close the window when clicked on "Close" button.
		closeButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				tinyURLFrame.dispose();
			}
		});
	}

	/* creates the input field for putting in the text which needs to be shortened
	 */
	public void createContent() {
		JPanel inputPanel = new JPanel();
		inputField = new JTextArea();
		inputField.setFont(font);
		inputField.setLineWrap(true);
		inputField.setColumns(30);
		inputField.setRows(3);
		inputPanel.add(new JScrollPane(inputField));
		tinyURLFrame.getContentPane().add(inputPanel);
	}

	/*
	 * shortens a link via tinyurl.com
	 *
	 * @param
	 *		- String aURLtoShorten
	 * @return
	 *		- String shortenedURL
	 */
	private String shortLink(String aURLtoShorten) {
		String shortenedURL = "";
		String apiURL = "http://tinyurl.com/api-create.php?url=";

		URL url;
		try {
			url = new URL(apiURL + aURLtoShorten);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			InputStream inStream = connection.getInputStream();
			BufferedInputStream bis = new BufferedInputStream(inStream);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			int read = bis.read();
			while(read != -1) {
				byte b = (byte) read;
				bos.write(b);
				read = bis.read();
			}

			shortenedURL = bos.toString();
		} catch (MalformedURLException ex) {
			new AlertWindow("URL is not valid!", true);
		} catch (IOException ex) {
			new AlertWindow("Connection could not be opened!", true);
		}

		return shortenedURL;
	}
}
