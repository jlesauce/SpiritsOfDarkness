/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017 LE SAUCE Julien
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

package org.jls.sod;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jls.sod.core.GameController;
import org.jls.sod.gui.LoadGameDialog;
import org.jls.sod.gui.MapPanel;
import org.jls.sod.gui.NewGameDialog;
import org.jls.sod.util.ResourceManager;
import org.jls.toolbox.widget.Console;
import org.jls.toolbox.widget.InternalFrame;
import org.jls.toolbox.widget.dialog.Dialog;

import net.miginfocom.swing.MigLayout;

/**
 * Main frame of the application.
 *
 * @author LE SAUCE Julien
 * @date Sep 2, 2015
 */
public class ApplicationView extends JFrame implements ActionListener, KeyListener {

    private static final long serialVersionUID = 5624587162291736726L;

    public static ApplicationView APP_FRAME = null;

    private final ApplicationController controller;
    private final Logger logger;
    private final ResourceManager props;

    private final HashMap<String, JMenu> menus;
    private final HashMap<String, JMenuItem> menuItems;
    private JMenuBar menuBar;

    private Console console;
    private Dialog mapFrame;
    private MapPanel mapPanel;
    private JTextField tfCommandLine;

    /**
     * Instantiates the application's main frame.
     *
     * @param model
     *            Data model of the application.
     * @param controller
     *            Controller of the application.
     */
    public ApplicationView(final ApplicationModel model, final ApplicationController controller) {
        super(model.getAppName() + " - Version " + model.getAppVersion());
        ApplicationView.APP_FRAME = this;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.controller = controller;
        this.logger = LogManager.getLogger();
        this.props = ResourceManager.getInstance();
        this.menus = new HashMap<>();
        this.menuItems = new HashMap<>();
        createComponents();
        createGui();
        addListeners();
    }

    /**
     * Changes the mouse's cursor to the wait cursor {@link Cursor#WAIT_CURSOR}.
     *
     * @param isWaiting
     *            <code>true</code> to use the waiting cursor, <code>false</code> to
     *            use the default cursor.
     */
    public static void setWaitCursor (final boolean isWaiting) {
        Cursor cursor = isWaiting ? Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR) : Cursor.getDefaultCursor();
        APP_FRAME.getGlassPane().setCursor(cursor);
        APP_FRAME.getGlassPane().setVisible(isWaiting);
    }

    /**
     * Shows a pop-up animation with the specified message.
     *
     * @param title
     *            Title of the pop-up.
     * @param msg
     *            The pop-up message.
     * @param msgType
     *            Specifies the pop-up message type (see
     *            {@link JOptionPane#setMessageType(int) for the availables
     *            options}.
     */
    public synchronized void pop (final String title, final String msg, final int msgType) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run () {
                JOptionPane.showMessageDialog(ApplicationView.this, msg, title, msgType);
            }
        });
    }

    /**
     * Shows the graphical user interface.
     */
    public void showGui () {
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        this.tfCommandLine.requestFocus();
    }

    /**
     * Shows the map to the player.
     */
    public void showMap () {
        this.controller.getGameController().getModel().addObserver(this.mapPanel);
        this.mapFrame.setVisible(true);
        this.mapFrame.setLocation((int) (getLocation().getX() + getSize().getWidth()), (int) getLocation().getY());
        this.tfCommandLine.requestFocus();
    }

    /**
     * Shows the new game creation panel.
     */
    public void showNewGamePanel () {
        NewGameDialog dialog = new NewGameDialog(this.controller.getGameController(), this);
        dialog.showGui();
    }

    /**
     * Shows the load game panel.
     */
    public void showLoadGamePanel () {
        LoadGameDialog dialog = new LoadGameDialog(this.controller.getGameController(), this);
        dialog.showGui();
    }

    /**
     * Hides the map.
     */
    public void hideMap () {
        this.controller.getGameController().getModel().deleteObserver(this.mapPanel);
        this.mapFrame.setVisible(false);
    }

    public void printConsole (final String text) {
        this.console.print(text);
    }

    public void printConsole (final String text, final int fontStyle) {
        this.console.print(text, fontStyle);
    }

    public void printConsole (final String text, final int fontStyle, final int size) {
        this.console.print(text, fontStyle, size);
    }

    public void printConsole (final String text, final Color textColor) {
        this.console.print(text, textColor);
    }

    public void printConsole (final String text, final Color textColor, final int fontStyle) {
        this.console.print(text, textColor, fontStyle);
    }

    public void printConsole (final String text, final Color textColor, final int fontStyle, final int size) {
        this.console.print(text, textColor, fontStyle, size);
    }

    public void printConsole (final String text, final Color textColor, final Color bgColor, final int fontStyle) {
        this.console.print(text, textColor, bgColor, fontStyle);
    }

    public void printConsole (final String text, final Color textColor, final Color bgColor, final int fontStyle,
            final int size) {
        this.console.print(text, textColor, bgColor, fontStyle, size);
    }

    /**
     * Instantiates the different components composing the user graphical interface.
     */
    private void createComponents () {
        createMenus();
        this.console = new Console();
        String fontName = this.props.getString("mainView.console.default.font");
        int fontSize = this.props.getInt("mainView.console.default.font.size");
        this.console.setFont(new Font(fontName, Font.PLAIN, fontSize));
        this.console.setBackground(Color.black);
        this.console.setDefaultTextBackgroundColor(Color.black);
        this.console.setDefaultTextColor(Color.white);
        this.console.setPreferredSize(new Dimension(800, 600));

        // Creates the map
        this.mapPanel = new MapPanel();
        this.mapFrame = new Dialog(this, "Map", this.mapFrame);
        this.mapFrame.setModal(false);
        this.mapFrame.setDefaultCloseOperation(InternalFrame.HIDE_ON_CLOSE);
        this.mapFrame.setContentPane(new MapPanel());
        this.mapFrame.pack();

        this.tfCommandLine = new JTextField();
    }

    /**
     * Creates the different menus of the main frame.
     */
    private void createMenus () {
        this.menuBar = new JMenuBar();
        // Creates the menus
        this.menus.put("file", new JMenu(this.props.getString("mainView.menu.file.label")));
        this.menus.put("help", new JMenu(this.props.getString("mainView.menu.help.label")));
        // Creates the items
        this.menuItems.put("file.newGame", new JMenuItem(this.props.getString("mainView.menu.item.newGame.label")));
        this.menuItems.put("file.loadGame", new JMenuItem(this.props.getString("mainView.menu.item.loadGame.label")));
        this.menuItems.put("file.exit", new JMenuItem(this.props.getString("mainView.menu.item.exit.label")));
        this.menuItems.put("help.about", new JMenuItem(this.props.getString("mainView.menu.item.about.label")));
        // Adds the items
        this.menus.get("file").add(this.menuItems.get("file.newGame"));
        this.menus.get("file").add(this.menuItems.get("file.loadGame"));
        this.menus.get("file").add(this.menuItems.get("file.exit"));
        this.menus.get("help").add(this.menuItems.get("help.about"));
        // Adds the menus
        this.menuBar.add(this.menus.get("file"));
        this.menuBar.add(this.menus.get("help"));
        setJMenuBar(this.menuBar);
    }

    /**
     * Adds the components to the panel to create the user graphical interface.
     */
    private void createGui () {
        setLayout(new MigLayout("fill"));
        add(this.console, "grow, push, wrap");
        add(this.tfCommandLine, "spanx, growx");
    }

    /**
     * Adds the listeners to the components of the panel.
     */
    private void addListeners () {
        this.menuItems.get("file.newGame").addActionListener(this);
        this.menuItems.get("file.loadGame").addActionListener(this);
        this.menuItems.get("file.exit").addActionListener(this);
        this.menuItems.get("help.about").addActionListener(this);
        this.tfCommandLine.addKeyListener(this);
    }

    @Override
    public void actionPerformed (final ActionEvent e) {
        /*
         * JMenuItem
         */
        if (e.getSource() instanceof JMenuItem) {
            JMenuItem item = (JMenuItem) e.getSource();

            // New Game
            if (this.menuItems.get("file.newGame").equals(item)) {
                showNewGamePanel();
            }
            // Load Game
            else if (this.menuItems.get("file.loadGame").equals(item)) {
                if (GameController.hasSavedGames()) {
                    showLoadGamePanel();
                } else {
                    pop("No saved games", this.props.getString("mainView.loadGameDialog.noSavedGame"),
                            JOptionPane.WARNING_MESSAGE);
                }
            }
            // Exit application
            else if (this.menuItems.get("file.exit").equals(item)) {
                this.logger.debug("Exit application");
                this.controller.exitApplication();
            }
            // About application
            else if (this.menuItems.get("help.about").equals(item)) {
                this.logger.debug("Show About panel");
            }
        }
    }

    @Override
    public void keyTyped (final KeyEvent e) {
        /*
         * JTextField
         */
        if (e.getSource() instanceof JTextField) {
            JTextField tf = (JTextField) e.getSource();

            // Command textfield
            if (this.tfCommandLine.equals(tf)) {

                // Process command
                if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                    String cmd = tf.getText();
                    if (!cmd.isEmpty()) {
                        this.logger.debug("Process command : {}", cmd);
                        this.controller.processCommand(cmd);
                        tf.setText("");
                    }
                }
            }
        }
    }

    @Override
    public void keyPressed (final KeyEvent e) {
        /*
         * JTextField
         */
        if (e.getSource() instanceof JTextField) {
            JTextField tf = (JTextField) e.getSource();

            // Command textfield
            if (this.tfCommandLine.equals(tf)) {

                // Up in the history
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    tf.setText(this.controller.previousHistory());
                }
                // Down in the history
                else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    tf.setText(this.controller.nextHistory());
                }
            }
        }
    }

    @Override
    public void keyReleased (final KeyEvent e) {
    }
}
