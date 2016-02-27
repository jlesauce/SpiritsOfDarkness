/*#
 * The MIT License (MIT)
 * 
 * Copyright (c) 2016 LE SAUCE Julien
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 #*/

package org.jls.sod.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jls.sod.core.GameController;
import org.jls.sod.util.ResourceManager;
import org.jls.toolbox.util.file.FileFilter;

/**
 * Creates a dialog for the player to create a new game.
 * 
 * @author LE SAUCE Julien
 * @date Sep 3, 2015
 */
public class LoadGameDialog extends JDialog implements ActionListener {

	private static final long serialVersionUID = 2250306038370416265L;

	private final GameController controller;
	private final Logger logger;

	private JComboBox<String> boxSavedGames;
	private JButton btnLoad;
	private JButton btnCancel;

	/**
	 * Instanciates a new dialog.
	 * 
	 * @param controller
	 *            The game controller.
	 * @param owner
	 *            The parent frame.
	 */
	public LoadGameDialog (final GameController controller, final JFrame owner) {
		super(owner, "Load Game", true);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		this.controller = controller;
		this.logger = LogManager.getLogger();
		createComponents();
		setStyle();
		addListeners();
	}

	/**
	 * Pops to the user a small dialog with the specified message and message
	 * type.
	 * 
	 * @param title
	 *            Title of the pop-up.
	 * @param msg
	 *            The pop-up message.
	 * @param option
	 *            Specifies the message type (see
	 *            {@link JOptionPane#setOptionType(int)} to know the different
	 *            message types).
	 */
	public synchronized void pop (final String title, final String msg, final int option) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run () {
				JOptionPane.showMessageDialog(LoadGameDialog.this, msg, title, option);
			}
		});
	}

	/**
	 * Shows the dialog.
	 */
	public void showGui () {
		pack();
		setLocationRelativeTo(getOwner());
		setVisible(true);
	}

	/**
	 * Instanciates the components of the graphical user interface.
	 */
	private void createComponents () {
		// Lists the saved games
		File savedDir = new File(ResourceManager.SAVED_PATH);
		File[] files = savedDir.listFiles(new FileFilter(FileFilter.ONLY_DIRECTORIES));
		String[] filenames = new String[files.length];
		for (int i = 0; i < filenames.length; i++) {
			filenames[i] = files[i].getName();
		}
		Arrays.sort(filenames);
		if (filenames.length == 0) {
			pop("Load Game", "No saved game detected.", JOptionPane.ERROR_MESSAGE);
		}
		this.boxSavedGames = new JComboBox<>(filenames);
		this.btnLoad = new JButton("Load");
		this.btnCancel = new JButton("Cancel");
	}

	/**
	 * Adds the created components to the panel to create the graphical user
	 * interface.
	 */
	private void setStyle () {
		setLayout(new MigLayout("", "", "[]20lp[]"));
		add(new JLabel("Saved games :"), "split, span");
		add(this.boxSavedGames, "wrap");
		add(this.btnLoad, "split, span, center");
		add(this.btnCancel, "");
	}

	/**
	 * Adds the listeners to the components of the graphical user interface.
	 */
	private void addListeners () {
		this.boxSavedGames.addActionListener(this);
		this.btnLoad.addActionListener(this);
		this.btnCancel.addActionListener(this);
	}

	@Override
	public void actionPerformed (ActionEvent e) {
		/*
		 * JButton
		 */
		if (e.getSource() instanceof JButton) {
			JButton btn = (JButton) e.getSource();

			// Create
			if (this.btnLoad.equals(btn)) {
				try {
					String savedGame = this.boxSavedGames.getSelectedItem().toString();
					this.logger.debug("Load game {SavedGame={}}", savedGame);
					this.controller.loadGame(savedGame);
					dispose();
				} catch (Exception e1) {
					this.logger.error("Cannot create new game", e1);
					pop("Game Creation Error", "Cannot create new game : \n\n" + e1.getMessage(), JOptionPane.ERROR_MESSAGE);
				}
			}
			// Cancel
			else if (this.btnCancel.equals(btn)) {
				this.logger.debug("Saved game selection aborted by user");
				dispose();
			}
		}
	}
}