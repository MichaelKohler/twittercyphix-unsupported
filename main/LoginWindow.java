/*
 * LoginWindow.java
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

import twittercyphix.twitter.Twitter;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.swing.*;

public class LoginWindow {
	private JFrame loginFrame;
	private Font font;

	private JTextField usernameField;
	private JPasswordField passwordField;
	private JCheckBox alwaysLoginCheckBox;

	/*
	 * Constructor : initializes the login window.
	 */
	public LoginWindow() {
		// copy the properties file to the local hard drive if it doesn't
		// exist already. This is needed because reading and writing properties
		// in/from a jar file is not nice at all.
		String currentDir = new File("").getAbsolutePath();
		File localPropsFile = new File(currentDir + "/preferences.properties");
		if(!localPropsFile.exists()) {
			// copy the file because it doesn't exist yet.
			try{
				String filename = "preferences.properties";
				InputStream inputStream = this.getClass().getResourceAsStream(filename);
				File outputFile = new File(filename);
				outputFile.createNewFile();

				FileOutputStream outputStream = new FileOutputStream(filename);

				int bufferLength;
				byte[] buffer = new byte[512];
				while((bufferLength = inputStream.read(buffer)) != -1){
					outputStream.write(buffer, 0, bufferLength);
				}
				inputStream.close();
				outputStream.close();
			} catch(IOException ex) {
				System.out.println("Could not write to the output file!");
			}
		}

		// check if there is a remembered user. If so, connect to Twitter.
		String[] rememberedUser = new PrefWindow(false).getRememberedUser();
		if(!rememberedUser[0].equals("") && !rememberedUser[1].equals("")) {
			LoadingWindow loadingWindow = new LoadingWindow();
			connectRemembered(rememberedUser[0], rememberedUser[1]);
			loadingWindow.dispose();
			return;
		}

		loginFrame = new JFrame("Login");
		loginFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		loginFrame.setVisible(false);

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

		loginFrame.setMinimumSize(new Dimension(300 + 15*fontsize, 15*fontsize));
		loginFrame.setPreferredSize(new Dimension(300 + 15*fontsize, 15*fontsize));
		loginFrame.setVisible(true);

		createContent();
	}

	// THE PROGRAM BEGINS HERE!
	public static void main(String[] args) {
		new LoginWindow();
	}

	/*
	 * connect the remembered user
	 *
	 * @param
	 *		- String aUsername: the user's username
	 *		- String aPassword: the user's password
	 */
	private void connectRemembered(String aUsername, String aPassword) {
		Twitter twitterHelper = new Twitter();
		if(twitterHelper.connect(aUsername, aPassword))
			new MainWindow(aUsername, aPassword);
	}

	/*
	 * creates the content of the window
	 */
	private void createContent() {
		// create a container for the login form, that is
		// needed for displaying it at the top of the window
		JPanel loginPanel = new JPanel();
		loginPanel.setLayout(new BorderLayout());

		// create the login form
		JPanel loginForm = new JPanel();
		loginForm.setLayout(new GridLayout(3,3));

		// create the login windows' content
		JLabel usernameLabel = new JLabel("Username:");
		usernameLabel.setFont(font);
		loginForm.add(usernameLabel);

		usernameField = new JTextField();
		usernameField.setFont(font);
		usernameField.requestFocus();
		loginForm.add(usernameField);

		JLabel passwordLabel = new JLabel("Password:");
		passwordLabel.setFont(font);
		loginForm.add(passwordLabel);

		passwordField = new JPasswordField();
		passwordField.setFont(font);
		loginForm.add(passwordField);

		passwordField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					onLoginButtonClick();
				}
			}
		});

		JLabel alwaysLogin = new JLabel("Remember me:");
		alwaysLogin.setFont(font);
		loginForm.add(alwaysLogin);

		alwaysLoginCheckBox = new JCheckBox();
		alwaysLoginCheckBox.setFont(font);
		loginForm.add(alwaysLoginCheckBox);

		// add the form to the container
		loginPanel.add(loginForm, BorderLayout.NORTH);

		// Create bottomPanel with close and login buttons
		JPanel bottomPanel = new JPanel();

		JButton loginButton = new JButton("Login");
		loginButton.setFont(font);
		bottomPanel.add(loginButton);

		// Call connect() when clicked on the login button
		loginButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				onLoginButtonClick();
			}
		});

		JButton closeButton = new JButton("Exit");
		closeButton.setFont(font);
		bottomPanel.add(closeButton);

		// close the LoginWindow when clicked on "Close"
		closeButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				System.exit(0);
			}
		});

		// add the bottom panel to the container (at the
		// bottom of it -> SOUTH)
		loginPanel.add(bottomPanel, BorderLayout.SOUTH);

		// add the container to the window and pack the
		// whole window (is needed)
		loginFrame.getContentPane().add(loginPanel);
		loginFrame.pack();
	}

	/*
	 * connect to Twitter after user has clicked "Login"
	 */
	private void onLoginButtonClick() {
		String username = usernameField.getText();
		String password = new String(passwordField.getPassword());
		if(username.equals("") || password.equals("")) {
			new AlertWindow("One of the fields were empty. Please type in a" +
							"username and a password!", true);
			return;
		}
		if(alwaysLoginCheckBox.isSelected())
			setupRememberMe(username, password);
		Twitter twitterHelper = new Twitter();
		if(twitterHelper.connect(username, password)) {
			new MainWindow(username, password);
			loginFrame.dispose();
		}
	}

	/*
	 * writes the username and the password encoded to
	 * the preferences file
	 *
	 * @param
	 *		- String aUsername: the user's username
	 *		- String aPassword: the user's password
	 */
	private void setupRememberMe(String aUsername, String aPassword) {
		PrefWindow prefWindow = new PrefWindow(false);
		prefWindow.writeProperty("username", aUsername);
		prefWindow.writeProperty("password", new sun.misc.BASE64Encoder()
											 .encode(aPassword.getBytes()));
	}
}
