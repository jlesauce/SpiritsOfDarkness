/*
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
 */

package org.jls.sod.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jls.sod.core.GameController;
import org.jls.sod.core.GameModel;
import org.jls.sod.core.loader.Loader;
import org.jls.sod.core.model.Story;
import org.jls.sod.util.ResourceManager;
import org.jls.toolbox.util.ArrayUtils;
import org.jls.toolbox.util.TimeUtils;

/**
 * Creates a dialog for the player to create a new game.
 * 
 * @author LE SAUCE Julien
 * @date Sep 3, 2015
 */
public class NewGameDialog extends JDialog implements ActionListener {

    private static final long serialVersionUID = 6877498171166015584L;

    private final GameModel model;
    private final GameController controller;
    private final Logger logger;

    private JTextField tfSaveName;
    private JComboBox<String> boxStories;
    private JTextArea tfDescription;
    private JButton btnCreate;

    /**
     * Instantiates a new dialog.
     * 
     * @param controller
     *            The game controller.
     * @param owner
     *            The parent frame.
     */
    public NewGameDialog(final GameController controller, final JFrame owner) {
        super(owner, "New Game", true);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        this.model = controller.getModel();
        this.controller = controller;
        this.logger = LogManager.getLogger();
        createComponents();
        setStyle();
        addListeners();
    }

    /**
     * Pops to the user a small dialog with the specified message and message type.
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
                JOptionPane.showMessageDialog(NewGameDialog.this, msg, title, option);
            }
        });
    }

    /**
     * Shows the dialog.
     */
    public void showGui () {
        pack();
        setLocationRelativeTo(getOwner());
        updateDescription(this.boxStories.getSelectedItem().toString());
        setVisible(true);
    }

    /**
     * Instantiates the components of the graphical user interface.
     */
    private void createComponents () {
        this.tfSaveName = new JTextField("SAVED_" + TimeUtils.getFileTimestamp());
        String[] storiesList = ArrayUtils.sort(this.model.getStories().keySet());
        this.boxStories = new JComboBox<>(storiesList);
        this.tfDescription = new JTextArea();
        this.tfDescription.setEditable(false);
        this.tfDescription.setLineWrap(true);
        this.tfDescription.setWrapStyleWord(true);
        this.btnCreate = new JButton("Create");
    }

    /**
     * Adds the created components to the panel to create the graphical user
     * interface.
     */
    private void setStyle () {
        setLayout(new MigLayout("", "", "[]20lp[]"));
        setPreferredSize(new Dimension(300, 400));
        add(new JLabel("Name :"), "split, span");
        add(this.tfSaveName, "growx, pushx, wrap");
        add(new JLabel("Available stories :"), "split, span, center");
        add(this.boxStories, "wrap");
        add(new JScrollPane(this.tfDescription), "span, grow, push, h 75lp:pref, wrap");
        add(this.btnCreate, "span, center");
    }

    /**
     * Adds the listeners to the components of the graphical user interface.
     */
    private void addListeners () {
        this.boxStories.addActionListener(this);
        this.btnCreate.addActionListener(this);
    }

    /**
     * Updates the description text field.
     * 
     * @param storyId
     *            Identifier of the story.
     */
    private void updateDescription (final String storyId) {
        File storyDir = new File(ResourceManager.STORIES_PATH, storyId);
        File storyFile = new File(storyDir, "story.xml");
        Story story;
        try {
            story = Loader.loadStory(storyFile);
            this.tfDescription.setText(story.getDescription());
        } catch (Exception e) {
            this.logger.error("Cannot load story file", e);
            pop("Story File Error", "Cannot load story file :\n\n" + e.getMessage(), JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void actionPerformed (ActionEvent e) {
        /*
         * JButton
         */
        if (e.getSource() instanceof JButton) {
            JButton btn = (JButton) e.getSource();

            // Create
            if (this.btnCreate.equals(btn)) {
                String story = this.boxStories.getSelectedItem().toString();
                String saveName = this.tfSaveName.getText();
                try {
                    this.logger.debug("Create new game {Story={}, SaveName={}}", story, saveName);
                    boolean created = this.controller.createNewGame(story, saveName);
                    if (created) {
                        dispose();
                    }
                } catch (Exception e1) {
                    this.logger.error("Cannot create new game", e1);
                    pop("Game Creation Error", "Cannot create new game : \n\n" + e1.getMessage(),
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        /*
         * JComboBox
         */
        if (e.getSource() instanceof JComboBox<?>) {
            JComboBox<?> box = (JComboBox<?>) e.getSource();

            // Selected story changed
            if (this.boxStories.equals(box)) {
                updateDescription(box.getSelectedItem().toString());
            }
        }
    }
}