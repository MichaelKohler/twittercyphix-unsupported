/*
 * AlertWindow.java
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

import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class AlertWindow {
	private Font font;
	private JFrame alertFrame;

	/*
	 * Constructor : initializes the AlertWindow with all
	 * needed elements
	 *
	 * @param
	 *		- String aMessage: message to display
	 *		- boolean aWarning: if this should display a warning icon
	 */
	public AlertWindow(String aMessage, boolean aWarning) {
		String windowTitle = "Failure!";
		if(!aWarning)
			windowTitle = "Everything okay!";
		alertFrame = new JFrame(windowTitle);
		alertFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // closes only the "Alert" - window
		alertFrame.getContentPane().setLayout(new BorderLayout());

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

		alertFrame.setMinimumSize(new Dimension(400 + 15*fontsize, 15*fontsize));
		alertFrame.setPreferredSize(new Dimension(400 + 15*fontsize, 15*fontsize));
		alertFrame.setVisible(true);

		createContent(aMessage, aWarning);
		createBottomPanel();

		alertFrame.pack();
	}

	/*
	* creates the panel for the buttons on the bottom of the window
	*/
	private void createBottomPanel() {
	// create a bottom JPanel for the "Close" button and
	// add this to this panel.
	JPanel bottomPanel = new JPanel();
	alertFrame.getContentPane().add(bottomPanel, BorderLayout.SOUTH);
	JButton closeButton = new JButton("Close");
	closeButton.setFont(font);
	bottomPanel.add(closeButton);

	closeButton.addMouseListener(new MouseAdapter() {
		@Override
		public void mouseClicked(MouseEvent e) {
			alertFrame.dispose();
		}
	});
	}

	/*
	 * creates the content of the AlertWindow
	 *
	 * @param
	 *		- String aMessage: message to display as error
	 *		- boolean aWarning: if this should display a warning icon
	 */
	private void createContent(String aMessage, boolean aWarning) {
		// create a JLabel for the message and add the label
		// to the content pane.
		JLabel alertMessage = new JLabel(aMessage);
		alertMessage.setFont(font);
		alertFrame.getContentPane().add(alertMessage, BorderLayout.CENTER);

		// create a panel with the icon left of the alert message
		ImageIcon logo;
		if(aWarning)
			logo = new ImageIcon(this.getClass().getResource("/images/warning.jpg"), "failure");
		else
			logo = new ImageIcon(this.getClass().getResource("/images/ok.jpg"), "ok");
		JLabel logoLbl = new JLabel(logo);
		logoLbl.setBorder(new EmptyBorder(0, 0, 0, 50));
		alertFrame.getContentPane().add(logoLbl, BorderLayout.WEST);
	}
}
