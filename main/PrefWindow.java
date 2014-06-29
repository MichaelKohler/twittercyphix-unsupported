/*
 * PrefWindow.java
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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import javax.swing.*;

public class PrefWindow {
	private JFrame prefFrame = new JFrame("Preferences");
	private Font font;

	// create a Properties object which is needed to get the
	// different values out of the .properties file
	private Properties prop = new Properties();

	/*
	 * Constructor : creates the preferences window
	 */
	public PrefWindow() {
		prefFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // closes only the "Preferences" - window

		// call the method to open a newFileInputStream to
		// read from the .properties file.
		openFIS();

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

		// set the minimum size of the Preferences window
		prefFrame.setMinimumSize(new Dimension(400 + 15*fontsize, 250 + 15*fontsize));
		prefFrame.setPreferredSize(new Dimension(400 + 15*fontsize, 250 + 15*fontsize));

		prefFrame.setVisible(true);

		// create the user interface
		createPrefUI();
	}

	/*
	 * Constructor: doesn't open the Preferences window, just used to get the font
	 *
	 * @param
	 *		- boolean aOpen: always false
	 */
	public PrefWindow(boolean aOpen) {
		// call the method to open a newFileInputStream to
		// read from the .properties file
		openFIS();
	}

	/*
	 * actual method to create the window
	 */
	private void createPrefUI() {
		// create TabbedPane + content of the different tabs
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.setFont(font);

		// tab for twittercyphix preferences
		JPanel twittercyphixPref = new JPanel();
		twittercyphixPref.setLayout(new BorderLayout());
		JPanel twittercyphixPrefPanel = new JPanel();
		twittercyphixPrefPanel.setLayout(new GridLayout(2, 2));
		twittercyphixPref.add(twittercyphixPrefPanel, BorderLayout.NORTH);

		JLabel labRTFormat = new JLabel("RT-Format:");
		labRTFormat.setFont(font);
		final JTextField RTFormat = new JTextField(prop.getProperty("twittercyphix.rtformat"), 40);
		RTFormat.setFont(font);
		twittercyphixPrefPanel.add(labRTFormat);
		twittercyphixPrefPanel.add(RTFormat);

		// save the text of the RT format text field when the field
		// loses the focus. Only if the value has changed.
		RTFormat.addFocusListener(new FocusListener() {
			String currentText = RTFormat.getText();
			@Override
			public void focusGained(FocusEvent e) {}
			public void focusLost(FocusEvent e) {
				if(RTFormat.getText().compareTo(currentText) != 0) {
					writeProperty("twittercyphix.rtformat", RTFormat.getText());
				}
			}
		});

		JLabel labTimelineUpFreq = new JLabel("Update Timeline every x minutes:");
		labTimelineUpFreq.setFont(font);
		final JTextField timelineUpFreq = new JTextField(prop.getProperty("twittercyphix.timelineupdate"), 2);
		timelineUpFreq.setFont(font);
		twittercyphixPrefPanel.add(labTimelineUpFreq);
		twittercyphixPrefPanel.add(timelineUpFreq);

		// save the text of the timelineUpFreq textfield when the field
		// loses the focus. Only if the value has changed
		timelineUpFreq.addFocusListener(new FocusListener() {
			String currentText = timelineUpFreq.getText();
			@Override
			public void focusGained(FocusEvent e) {}
			public void focusLost(FocusEvent e) {
				if(timelineUpFreq.getText().compareTo(currentText) != 0) {
					if(!timelineUpFreq.getText().matches("^[0-9]+$")) {
						new AlertWindow("<html>The value has to be: one or more digit(s)</html>", true);
					}
					else {
						writeProperty("twittercyphix.timelineupdate", timelineUpFreq.getText());
					}
				}
			}
		});

		// tab for appearance preferences
		JPanel appearancePref = new JPanel();
		appearancePref.setLayout(new BorderLayout());
		JPanel appearancePrefPanel = new JPanel();
		appearancePrefPanel.setLayout(new GridLayout(4, 2));
		appearancePref.add(appearancePrefPanel, BorderLayout.NORTH);

		JLabel labFontName = new JLabel("Font:");
		labFontName.setFont(font);
		final JComboBox fontName = new JComboBox();
		fontName.setFont(font);
		String[] fontNameChoices = GraphicsEnvironment.getLocalGraphicsEnvironment()
								   .getAvailableFontFamilyNames();
		int i = 0;
		int selectedIndex = 0;
		for(String a : fontNameChoices) {
			fontName.addItem(a);
			if(a.equals(prop.getProperty("appearance.fontname"))) {
				selectedIndex = i;
			}
			i++;
		}
		fontName.setSelectedIndex(selectedIndex);
		appearancePrefPanel.add(labFontName);
		appearancePrefPanel.add(fontName);

		// save the selected item of the font drop down list when the field
		// loses the focus. Only if the value has changed
		fontName.addFocusListener(new FocusListener() {
			int currentSelected = fontName.getSelectedIndex();
			@Override
			public void focusGained(FocusEvent e) {}
			public void focusLost(FocusEvent e) {
				if(fontName.getSelectedIndex() != currentSelected) {
					writeProperty("appearance.fontname", "" + GraphicsEnvironment.getLocalGraphicsEnvironment()
															  .getAvailableFontFamilyNames()[fontName.getSelectedIndex()]);
				}
			}
		});

		JLabel labFontVariant = new JLabel("Font-Variant:");
		labFontVariant.setFont(font);
		final JComboBox fontVariant = new JComboBox();
		fontVariant.setFont(font);
		String[] fontVariantChoices = { "Normal", "Bold" , "Italic" };
		for(String b : fontVariantChoices)
			fontVariant.addItem(b);
		fontVariant.setSelectedIndex(Integer.parseInt(prop.getProperty("appearance.fontvariant")));
		appearancePrefPanel.add(labFontVariant);
		appearancePrefPanel.add(fontVariant);

		// save the selected item of the fontvariant drop down list when the field
		// loses the focus. Only if the value has changed
		fontVariant.addFocusListener(new FocusListener() {
			int currentSelected = fontVariant.getSelectedIndex();
			@Override
			public void focusGained(FocusEvent e) {}
			public void focusLost(FocusEvent e) {
				if(fontVariant.getSelectedIndex() != currentSelected) {
					writeProperty("appearance.fontvariant", "" + fontVariant.getSelectedIndex());
				}
			}
		});

		JLabel labFontSize = new JLabel("Font-Size:");
		labFontSize.setFont(font);
		final JComboBox fontSize = new JComboBox();
		fontSize.setFont(font);
		final int[] fontSizeChoices = { 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20 };
		for(int c : fontSizeChoices) {
			fontSize.addItem(c);
		}
		fontSize.setSelectedIndex(Integer.parseInt(prop.getProperty("appearance.fontsize")) - 8);
		appearancePrefPanel.add(labFontSize);
		appearancePrefPanel.add(fontSize);

		// save the selected item of the fontsize dropdown list when the field
		// loses the focus. Only if the value has changed
		fontSize.addFocusListener(new FocusListener() {
			int currentSelected = fontSize.getSelectedIndex();
			@Override
			public void focusGained(FocusEvent e) {}
			public void focusLost(FocusEvent e) {
				if(fontSize.getSelectedIndex() != currentSelected) {
					writeProperty("appearance.fontsize", "" + fontSizeChoices[fontSize.getSelectedIndex()]);
				}
			}
		});

		// restart warning
		JLabel labRestartWarning = new JLabel("<html><p style='color:#FF0000;'>To see " +
											  "the changes you have to restart twittercyphix!</p></html>");
		labRestartWarning.setFont(font);
		appearancePrefPanel.add(labRestartWarning);

		// tab for the profile manager
		final JPanel profilePref = new JPanel();
		profilePref.setLayout(new BorderLayout());
		final JPanel profilePrefPanel = new JPanel();
		profilePrefPanel.setLayout(new GridLayout(1,1));
		profilePref.add(profilePrefPanel, BorderLayout.NORTH);

		PrefWindow prefWindow = new PrefWindow(false);
		String[] rememberedUser = prefWindow.getRememberedUser();
		if(!rememberedUser[0].equals("") && !rememberedUser[1].equals("")) {
			JLabel rememberedUserLbl = new JLabel(rememberedUser[0] + " is remembered!");
			rememberedUserLbl.setFont(font);
			JButton deleteRememberedUser = new JButton("Delete");
			deleteRememberedUser.setFont(font);
			profilePrefPanel.add(rememberedUserLbl);
			profilePrefPanel.add(deleteRememberedUser);

			// delete the remembered user when clicking on "Delete" button
			deleteRememberedUser.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					writeProperty("username", "");
					writeProperty("password", "");
					new AlertWindow("The remembered user has been deleted!", false);
				}
			});
		}
		else {
			JLabel noRememberedUser = new JLabel("There is no remembered user!");
			noRememberedUser.setFont(font);
			profilePrefPanel.add(noRememberedUser);
		}

		// add all the panels to the JTabbedPane and
		// add the JTabbedPane to the window
		tabbedPane.add("twittercyphixPref", twittercyphixPref);
		tabbedPane.add("Appearance", appearancePref);
		tabbedPane.add("Profile", profilePref);
		prefFrame.add(tabbedPane);

		// create bottom Panel (used for the bottom buttons)
		JPanel prefBottomPanel = new JPanel();
		prefFrame.getContentPane().add(prefBottomPanel, BorderLayout.SOUTH);

		// create "Close" button on bottomPanel
		JButton closeButton = new JButton("Close");
		closeButton.setFont(font);
		prefBottomPanel.add(closeButton);

		// close the preferences window when clicked on "Close" button
		closeButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				prefFrame.dispose();
			}
		});
	}

	/*
	 * getter for the font properties
	 *
	 * @return
	 *		- String[] fontprops with the different properties of the font
	 */
	public String[] getFont() {
		String[] fontprops = new String[3];

		fontprops[0] = prop.getProperty("appearance.fontname");
		fontprops[1] = prop.getProperty("appearance.fontvariant");
		fontprops[2] = prop.getProperty("appearance.fontsize");

		return fontprops;
	}

	/*
	 * get a specific property
	 *
	 * @param
	 *		- String aName: the name of the property
	 * @return
	 *		- the value of the property
	 */
	public String getProperty(String aName) {
		String value = prop.getProperty(aName);
		if(value == null)
			return "";
		return value;
	}

	/*
	 * check if there's a remembered user. If so,it returns
	 * the username and password of it
	 *
	 * @return
	 *		- String[] user: the username and the password in a String array
	 */
	public String[] getRememberedUser() {
		String[] user = new String[2];

		try {
			user[0] = prop.getProperty("username");
			user[1] = new String(new sun.misc.BASE64Decoder().decodeBuffer(prop.getProperty("password")));
		} catch (IOException ex) {
			new AlertWindow("Twittercyphix could not write or read from/to preferences.properties.", true);
		}

		return user;
	}

	/*
	 * getter for the RT format
	 *
	 * @return
	 *		- String retweetFormat: format of the retweet
	 */
	public String getRTFormat() {
		return prop.getProperty("twittercyphix.rtformat");
	}

	/*
	 * gets the update interval of the timeline
	 *
	 * @return
	 *		- the update interval
	 */
	public int getUpdateInterval() {
		return Integer.parseInt(prop.getProperty("twittercyphix.timelineupdate"));
	}

	/*
	 * opens a new FileInputStream to read the preferences.properties
	 * file and loads it into the Properties variable "prop".
	 */
	private void openFIS() {
		try {
			String currentDir = new File("").getAbsolutePath();
			FileInputStream prefInputStream = new FileInputStream(currentDir + "/preferences.properties");
			prop.load(prefInputStream);
		}
		catch(java.io.FileNotFoundException ex) {
			new AlertWindow("The preferences.properties file was not found!", true);
		}
		catch(java.io.IOException ex) {
			new AlertWindow("Twittercyphix could not write or read from/to preferences.properties.", true);
		}
	}

	/*
	 * method to write the value of a specified property
	 *
	 * @param
	 *		- String aName: the name of the property to read from
	 * 		- String aValue: the value of the property
	 */
	public void writeProperty(String aName, String aValue) {
		try {
			prop.setProperty(aName, aValue);
			prop.store(new FileOutputStream("preferences.properties"), null);
		}
		catch(java.io.IOException ex) {
			new AlertWindow("Twittercyphix could not write or read from/to preferences.properties.", true);
		}
	}
}
