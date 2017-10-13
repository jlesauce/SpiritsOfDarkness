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

package org.jls.sod;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.util.Arrays;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jls.sod.core.GameController;
import org.jls.sod.core.GameModel;
import org.jls.sod.core.command.AbstractCommandExecutor;
import org.jls.sod.core.command.Command;
import org.jls.sod.core.command.DropCommand;
import org.jls.sod.core.command.GeneralCommand;
import org.jls.sod.core.command.InventoryCommand;
import org.jls.sod.core.command.LookCommand;
import org.jls.sod.core.command.MapCommand;
import org.jls.sod.core.command.NavigateCommand;
import org.jls.sod.core.command.TakeCommand;
import org.jls.sod.util.ResourceManager;

/**
 * Application's main controller.
 *
 * @author LE SAUCE Julien
 * @date Sep 2, 2015
 */
public class ApplicationController {

    private final ApplicationModel model;
    private final ApplicationView view;
    private final GameController gameController;
    private final Logger logger;
    private final ResourceManager props;

    /**
     * Instantiates the main controller.
     *
     * @param model
     *            Application's data model.
     * @throws Exception
     *             If an error occurred creating the differents view of the
     *             application.
     */
    public ApplicationController(final ApplicationModel model) throws Exception {
        this.model = model;
        this.view = new ApplicationView(model, this);
        this.gameController = new GameController(new GameModel(), this);
        this.logger = LogManager.getLogger();
        this.props = ResourceManager.getInstance();
        initCommandExecutors();
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
        this.view.pop(title, msg, msgType);
    }

    /**
     * Shows the graphical user interface.
     */
    public void showGui () {
        this.view.showGui();
    }

    /**
     * Exit the application.
     */
    public void exitApplication () {
        this.logger.info("Exiting application");
        Runtime.getRuntime().exit(0);
    }

    /**
     * Process the user command.
     *
     * @param cmd
     *            User command.
     */
    public void processCommand (final String cmd) {
        // Checks input
        if (cmd == null) {
            throw new NullPointerException("Command cannot be null");
        }
        if (cmd.isEmpty()) {
            throw new IllegalArgumentException("Command is empty");
        }
        // Prints the command in the console
        printCommand(cmd);

        // Cleanse the command
        String cleansedCmd = Command.cleanse(cmd);
        this.logger.info("Processing command : {}", cleansedCmd);

        // Creates a command context
        Command command = new Command(cleansedCmd);

        // Detects the associated executor and executes the command
        boolean hasExecutor = false;
        for (AbstractCommandExecutor executor : this.model.getCommandExecutors()) {
            // If the executor contains the command ID
            if (Arrays.asList(executor.getRecognizedCommands()).contains(command.getCommandId())) {
                hasExecutor = true;
                // Executes the command
                executor.execute(command);
                // Updates history
                this.model.putHistory(cmd);
            }
        }
        // If no executor has been found
        if (!hasExecutor) {
            this.logger.warn("Unknown command : {}", cmd);
            printError("\t" + this.props.getString("command.error.unknownCommand"));
        }
    }

    /**
     * Sets the application's icon.
     *
     * @param icon
     *            Application's icon.
     */
    public void setIcon (Image icon) {
        this.view.setIconImage(icon);
    }

    /**
     * Selects the Look&Feel. If the L&F exists then the skin is updated using the
     * {@link UIManager}.
     *
     * @param lafName
     *            Identifier of the Look & Feel.
     */
    public void setSkin (final String lafName) {
        // System L&F
        if (lafName.equals("System")) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                SwingUtilities.updateComponentTreeUI(this.view);
                this.logger.info("Selected Look&Feel {}", lafName);
            } catch (Exception e) {
                this.logger.error("Failed to set java Look&Feel {}", lafName, e);
            }
            // Default L&F
        } else if (lafName.equals("Default")) {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
                SwingUtilities.updateComponentTreeUI(this.view);
                this.logger.info("Selected Look&Feel {}", lafName);
            } catch (Exception e) {
                this.logger.error("Failed to set java Look&Feel {}", lafName, e);
            }
        }
        // pre-installed L&F
        else {
            for (LookAndFeelInfo laf : UIManager.getInstalledLookAndFeels()) {
                if (laf.getName().equals(lafName)) {
                    try {
                        UIManager.setLookAndFeel(laf.getClassName());
                        SwingUtilities.updateComponentTreeUI(this.view);
                        this.logger.info("Selected Look&Feel {}", lafName);
                    } catch (Exception e) {
                        this.logger.error("Failed to set java Look&Feel {}", lafName, e);
                    }
                    return;
                }
            }
            throw new IllegalArgumentException("Look&Feel not found : " + lafName);
        }
    }

    /**
     * Shows the new game creation panel.
     */
    public void showNewGamePanel () {
        this.view.showNewGamePanel();
    }

    /**
     * Shows the load game panel.
     */
    public void showLoadGamePanel () {
        this.view.showLoadGamePanel();
    }

    /**
     * Shows the map to the player.
     */
    public void showMap () {
        this.view.showMap();
    }

    /**
     * Hides the map.
     */
    public void hideMap () {
        this.view.hideMap();
    }

    /**
     * Gets the previous history from the current history's position.
     *
     * @return Previous history value.
     */
    public String previousHistory () {
        this.model.decrementHistoryPosition();
        this.logger.debug("Showing previous history (history position = {}, size = {})",
                this.model.getCurrentHistoryPosition(), this.model.getHistory().size());
        return this.model.getHistory(this.model.getCurrentHistoryPosition());
    }

    /**
     * Gets the next history from the current history's position.
     *
     * @return Next history value.
     */
    public String nextHistory () {
        this.model.incrementHistoryPosition();
        this.logger.debug("Showing next history (history position = {}, size = {})",
                this.model.getCurrentHistoryPosition(), this.model.getHistory().size());
        return this.model.getHistory(this.model.getCurrentHistoryPosition());
    }

    /**
     * Prints the command in the console.
     *
     * @param cmd
     *            The command to print.
     */
    public void printCommand (final String cmd) {
        this.view.printConsole(">", this.props.getColor("console.color.command.executed.cursor"), Font.BOLD);
        this.view.printConsole("  ");
        this.view.printConsole(cmd + "\n", this.props.getColor("console.color.command.executed"), Font.PLAIN);
    }

    /**
     * Prints an error message in the console.
     *
     * @param msg
     *            The error message.
     */
    public void printError (final String msg) {
        Color color = this.props.getColor("console.color.error");
        this.view.printConsole(msg + "\n", color, Font.PLAIN);
    }

    public void printConsole (String text) {
        this.view.printConsole(text);
    }

    public void printConsole (String text, int fontStyle) {
        this.view.printConsole(text, fontStyle);
    }

    public void printConsole (String text, int fontStyle, int size) {
        this.view.printConsole(text, fontStyle, size);
    }

    public void printConsole (String text, Color textColor) {
        this.view.printConsole(text, textColor);
    }

    public void printConsole (String text, Color textColor, int fontStyle) {
        this.view.printConsole(text, textColor, fontStyle);
    }

    public void printConsole (String text, Color textColor, int fontStyle, int size) {
        this.view.printConsole(text, textColor, fontStyle, size);
    }

    public void printConsole (String text, Color textColor, Color bgColor, int fontStyle) {
        this.view.printConsole(text, textColor, bgColor, fontStyle);
    }

    public void printConsole (String text, Color textColor, Color bgColor, int fontStyle, int size) {
        this.view.printConsole(text, textColor, bgColor, fontStyle, size);
    }

    /**
     * Inits the list of the command executors.
     */
    private void initCommandExecutors () {
        this.model.getCommandExecutors()
                .add(new GeneralCommand(this.getGameController().getModel(), this.gameController));
        this.model.getCommandExecutors()
                .add(new NavigateCommand(this.getGameController().getModel(), this.gameController));
        this.model.getCommandExecutors().add(new LookCommand(this.getGameController().getModel(), this.gameController));
        this.model.getCommandExecutors().add(new TakeCommand(this.getGameController().getModel(), this.gameController));
        this.model.getCommandExecutors().add(new DropCommand(this.getGameController().getModel(), this.gameController));
        this.model.getCommandExecutors()
                .add(new InventoryCommand(this.getGameController().getModel(), this.gameController));
        this.model.getCommandExecutors().add(new MapCommand(this.getGameController().getModel(), this.gameController));
    }

    /**
     * Returns the game controller.
     *
     * @return The game controller.
     */
    public GameController getGameController () {
        return this.gameController;
    }
}