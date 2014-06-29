/*
 * LoadingWindow.java
 * Copyright 2009 Michael Kohler
 * See http://java.sun.com for the methods I used.
 * The throbber (loading icons) is from http://www.lokeshdhakar.com/projects/lightbox2/.
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

import javax.swing.*;

public class LoadingWindow {
	private Font font;
	private JFrame loadingFrame;

	/*
	 * Constructor : initializes the loading window
	 */
	public LoadingWindow() {
		loadingFrame = new JFrame("Loading...");
		loadingFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		// get the font
		PrefWindow fontgetterObj = new PrefWindow(false);
		String[] fontproperties = fontgetterObj.getFont();
		String fontname = fontproperties[0];
		int fontsize = Integer.parseInt(fontproperties[2]);
		font = new Font(fontname, Font.BOLD, fontsize + 4);

		loadingFrame.setMinimumSize(new Dimension(100 + 15*fontsize, 50 + 15*fontsize));
		loadingFrame.setPreferredSize(new Dimension(100 + 15*fontsize, 50 + 15*fontsize));
		loadingFrame.setVisible(false);

		createContent();
		loadingFrame.setVisible(true);
	}

	/*
	 * creates the content of the loading window
	 */
	private void createContent() {
		loadingFrame.getContentPane().setLayout(new BorderLayout());

		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new BorderLayout());

		JLabel loadingLbl = new JLabel("Loading...");
		loadingLbl.setFont(font);
		contentPanel.add(loadingLbl, BorderLayout.NORTH);

		ImageIcon loadingIcon = new ImageIcon(this.getClass().getResource("/images/loading.gif"), "loading");
		JLabel iconLbl = new JLabel(loadingIcon);
		contentPanel.add(iconLbl, BorderLayout.CENTER);

		loadingFrame.getContentPane().add(contentPanel);
	}

	/*
	 * closes the loading window
	 */
	public void dispose() {
		loadingFrame.dispose();
	}
}
