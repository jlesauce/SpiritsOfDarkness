/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2019 LE SAUCE Julien
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
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jls.sod.core.GameController;
import org.jls.sod.core.GameModel;
import org.jls.sod.core.cmd.AbstractCommandCaller;
import org.jls.sod.core.cmd.Command;
import org.jls.sod.core.cmd.CommandController;
import org.jls.sod.core.cmd.ParsedCommand;
import org.jls.sod.util.ResourceManager;
import picocli.CommandLine;
import picocli.CommandLine.ParseResult;

public class ApplicationController {

    private final ApplicationModel model;
    private final ApplicationView view;
    private final GameController gameController;
    private final CommandController commandController;
    private final Logger logger;
    private final ResourceManager props;

    public ApplicationController(final ApplicationModel model) throws Exception {
        this.model = model;
        this.view = new ApplicationView(model, this);
        this.gameController = new GameController(new GameModel(), this);
        this.commandController = new CommandController(this.getGameController().getModel(), this.gameController);
        this.logger = LogManager.getLogger();
        this.props = ResourceManager.getInstance();
    }

    /**
     * Show a pop-up animation with the specified message.
     *
     * @param title
     *                Title of the pop-up.
     * @param msg
     *                The pop-up message.
     * @param msgType
     *                Specify the pop-up message type (see
     *                {@link JOptionPane#setMessageType(int) for the available
     *                options}.
     */
    public synchronized void pop(final String title, final String msg, final int msgType) {
        this.view.pop(title, msg, msgType);
    }

    public void showGui() {
        this.view.showGui();
    }

    public void exitApplication() {
        this.logger.info("Exiting application");
        Runtime.getRuntime().exit(0);
    }

    /**
     * Process the command given by the user in the console view.
     *
     * @param cmd
     *            The user command as a string.
     */
    public void processUserCommand(final String cmd) {
        if (cmd == null) {
            throw new NullPointerException("Command cannot be null");
        }
        if (cmd.isEmpty()) {
            throw new IllegalArgumentException("Command is empty");
        }
        Command command = new Command(cmd);

        printCommandInConsole(command.toString());
        this.logger.info("Processing command : {}", command.toString());
        executeUserCommand(command);
    }

    /**
     * Find the executor associated with the command ID, parse the command and
     * execute it.
     *
     * @param cmd
     *            The command object.
     */
    private void executeUserCommand(final Command cmd) {
        AbstractCommandCaller cmdExecutor = findCommandExecutorInExecutors(cmd.getCommandId());

        if (cmdExecutor == null) {
            this.logger.warn("Unknown command : {}", cmd);
            printError("\t" + this.props.getString("command.error.unknownCommand"));
            return;
        }

        try {
            CommandLine cmdLine = new CommandLine(cmdExecutor);
            ParseResult parsed = cmdLine.parseArgs(cmd.getArguments());
            cmdExecutor.apply(new ParsedCommand(cmdLine, parsed));
        } catch (Exception exception) {
            printError("\t" + this.props.getString("command.error.invalidCommand"));
            printError("ERROR: " + exception.getMessage());
            this.logger.error(exception.getMessage());
        }
    }

    private AbstractCommandCaller findCommandExecutorInExecutors(final String commandId) {
        if (this.commandController.getCommandExecutorList().containsKey(commandId)) {
            return this.commandController.getCommandExecutorList().get(commandId);
        }
        return null;
    }

    public void setApplicationIcon(final Image icon) {
        this.view.setIconImage(icon);
    }

    /**
     * Select the Look&Feel. If the L&F exists then the skin is updated using
     * the {@link UIManager}.
     *
     * @param lafName
     *                Identifier of the Look & Feel.
     */
    public void setSkin(final String lafName) {
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
        }
        else if (lafName.equals("Default")) {
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

    public void showNewGamePanel() {
        this.view.showNewGamePanel();
    }

    public void showLoadGamePanel() {
        this.view.showLoadGamePanel();
    }

    public void showUserMap() {
        this.view.showUserMap();
    }

    public void hideUserMap() {
        this.view.hideUserMap();
    }

    /**
     * Get the previous history from the current history's position.
     *
     * @return Previous history value.
     */
    public String previousHistory() {
        this.model.decrementHistoryPosition();
        this.logger.debug("Showing previous history (history position = {}, size = {})",
                this.model.getCurrentHistoryPosition(), this.model.getHistory().size());
        return this.model.getHistory(this.model.getCurrentHistoryPosition());
    }

    /**
     * Get the next history from the current history's position.
     *
     * @return Next history value.
     */
    public String nextHistory() {
        this.model.incrementHistoryPosition();
        this.logger.debug("Showing next history (history position = {}, size = {})",
                this.model.getCurrentHistoryPosition(), this.model.getHistory().size());
        return this.model.getHistory(this.model.getCurrentHistoryPosition());
    }

    public void printCommandInConsole(final String cmd) {
        this.view.printConsole(">", this.props.getColor("console.color.command.executed.cursor"), Font.BOLD);
        this.view.printConsole("  ");
        this.view.printConsole(cmd + "\n", this.props.getColor("console.color.command.executed"), Font.PLAIN);
    }

    public void printError(final String msg) {
        Color color = this.props.getColor("console.color.error");
        this.view.printConsole(msg + "\n", color, Font.PLAIN);
    }

    public void printConsole(final String text) {
        this.view.printConsole(text);
    }

    public void printConsole(final String text, final int fontStyle) {
        this.view.printConsole(text, fontStyle);
    }

    public void printConsole(final String text, final int fontStyle, final int size) {
        this.view.printConsole(text, fontStyle, size);
    }

    public void printConsole(final String text, final Color textColor) {
        this.view.printConsole(text, textColor);
    }

    public void printConsole(final String text, final Color textColor, final int fontStyle) {
        this.view.printConsole(text, textColor, fontStyle);
    }

    public void printConsole(final String text, final Color textColor, final int fontStyle, final int size) {
        this.view.printConsole(text, textColor, fontStyle, size);
    }

    public void printConsole(final String text, final Color textColor, final Color bgColor, final int fontStyle) {
        this.view.printConsole(text, textColor, bgColor, fontStyle);
    }

    public void printConsole(final String text, final Color textColor, final Color bgColor, final int fontStyle,
            final int size) {
        this.view.printConsole(text, textColor, bgColor, fontStyle, size);
    }

    public GameController getGameController() {
        return this.gameController;
    }
}
