/*
 * AboutWindow.java
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

public class AboutWindow {
	private Font font;
	private JFrame aboutFrame;

	/*
	 * Constructor : initializes the about window
	 */
	public AboutWindow() {
		aboutFrame = new JFrame("About TwitterCyphix");
		aboutFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // closes only the about window when closing

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

		aboutFrame.setMinimumSize(new Dimension(300 + 15*fontsize, 300 + 15*fontsize));
		aboutFrame.setPreferredSize(new Dimension(300 + 15*fontsize, 300 + 15*fontsize));
		aboutFrame.setVisible(true);

		createContent();
		createBottomPanel();
	}

	/*
	 * creates the bottom panel with the close button
	 */
	private void createBottomPanel() {
		JPanel aboutBottomPanel = new JPanel();

		// create "Close" button on the bottom panel
		JButton closeButton = new JButton("Close");
		closeButton.setFont(font);
		aboutBottomPanel.add(closeButton);

		aboutFrame.getContentPane().add(aboutBottomPanel, BorderLayout.SOUTH);

		// close the AboutWindow when clicked on the "Close" button
		closeButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				aboutFrame.dispose();
			}
		});
	}

	/*
	 * creates the content of the about window
	 */
	private void createContent() {
		aboutFrame.getContentPane().setLayout(new BorderLayout());

		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new BorderLayout());

		ImageIcon logo = new ImageIcon(this.getClass().getResource("/images/twittercyphix.jpg"), "twittercyphix");
		JLabel logoLbl = new JLabel(logo);
		contentPanel.add(logoLbl, BorderLayout.NORTH);

		JLabel versionLbl = new JLabel("Version: 1.0");
		contentPanel.add(versionLbl, BorderLayout.CENTER);

		JLabel info = new JLabel("<html><br /><br /><p>TwitterCyphix is a Twitter client programmed in Java. This project was " +
						"done as a school project.</p><p style='padding-top: 10px; padding-bottom: 10px;'>This programm was" +
						" programmed by Michael Kohler (michaelkohler@live.com).</p>" +
						"<p>This programm is published under the GPL licence.</p></html>");
		info.setFont(font);
		contentPanel.add(info, BorderLayout.SOUTH);

		aboutFrame.getContentPane().add(contentPanel, BorderLayout.NORTH);
	}
}
