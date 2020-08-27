/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020 Julien LE SAUCE
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
 * FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.jls.sod;

import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jls.sod.core.GameController;
import org.jls.sod.core.GameModel;
import org.jls.sod.core.cmd.*;
import org.jls.sod.util.ResourceManager;
import org.jls.sod.util.Settings;

import java.awt.*;

public class ApplicationController {

    private final ApplicationModel model;
    private final ApplicationView view;
    private final GameController gameController;
    private final CommandController commandController;
    private final Logger logger;
    private final ResourceManager props;
    private final Settings settings;
    private final CommandParser commandParser;

    public ApplicationController(final ApplicationModel model) {
        this.model = model;
        view = new ApplicationView(model, this);
        settings = new Settings();
        gameController = new GameController(new GameModel(), this, settings);
        commandController = new CommandController(this.getGameController().getModel(),
                gameController);
        logger = LogManager.getLogger();
        props = ResourceManager.getInstance();
        commandParser = new CommandParser();
    }

    public synchronized void pop(final String title, final String msg, final int msgType) {
        this.view.pop(title, msg, msgType);
    }

    public void showGui() {
        this.view.showGui();
    }

    public void startGame() {
        if (settings.isLastPlayedGameAutoloadEnabled()) {
            loadLastPlayedGame();
        }
    }

    private void loadLastPlayedGame() {
        String lastPlayedGame = settings.getLastPlayedGame();

        if (!lastPlayedGame.isBlank()) {
            try {
                gameController.loadGame(lastPlayedGame);
            } catch (Exception e) {
                gameController.getDisplayController().printError("Cannot load the game instance: "
                        + lastPlayedGame);
                gameController.getDisplayController().printError(e.getMessage());
                logger.error(e);
            }
        } else {
            this.logger.info("Tried to load last played game but lastPlayedGame was blank");
        }
    }

    public void exitApplication() {
        logger.info("Exiting application");
        Runtime.getRuntime().exit(0);
    }

    public void processUserCommand(final String userInput) {
        if (userInput == null) {
            throw new NullPointerException("Command cannot be null");
        }
        if (userInput.isEmpty()) {
            throw new IllegalArgumentException("Command is empty");
        }
        printCommandInConsole(userInput);
        model.pushNewCommandToHistory(userInput);
        logger.info("Process user command : {}", userInput);

        Command command = parseUserCommand(userInput);
        if (command != null) {
            executeUserCommand(command);
        }
    }

    private Command parseUserCommand(final String userInput) {
        try {
            Namespace commandNamespace = commandParser.parseCommand(userInput);
            return new Command(userInput, commandNamespace);
        } catch (ArgumentParserException e) {
            logger.error("Failed to parse user command input", e);
            printError("\t" + props.getString("command.error.invalidCommand"));
            printError("ERROR: " + e.getMessage());
        }
        return null;
    }

    private void executeUserCommand(final Command command) {
        AbstractCommandExecutor cmdExecutor =
                findCommandExecutorInExecutors(command.getCommandId());
        logger.info("Execute user command: " + command);

        if (cmdExecutor == null) {
            logger.warn("Unknown command : {}", command);
            printError("\t" + props.getString("command.error.unknownCommand"));
            return;
        }
        logger.debug("Found command executor: " + cmdExecutor);

        try {
            cmdExecutor.apply(command);
        } catch (Exception exception) {
            printError("\t" + props.getString("command.error.invalidCommand"));
            printError("ERROR: " + exception.getMessage());
            logger.error(exception.getMessage());
        }
    }

    private AbstractCommandExecutor findCommandExecutorInExecutors(final String commandId) {
        if (commandController.getCommandExecutorList().containsKey(commandId)) {
            return commandController.getCommandExecutorList().get(commandId);
        }
        return null;
    }

    public void setApplicationIcon(final Image icon) {
        view.setIconImage(icon);
    }

    public void showNewGamePanel() {
        view.showNewGamePanel();
    }

    public void showLoadGamePanel() {
        view.showLoadGamePanel();
    }

    public void showUserMap() {
        view.showUserMap();
    }

    public void hideUserMap() {
        view.hideUserMap();
    }

    public String getPreviousCommandHistory() {
        model.decrementCommandHistoryIndex();
        logger.debug("Showing previous history (history position = {}, size = {})",
                model.getCurrentCommandHistoryIndex(), model.getCommandHistory().size());
        return model.getCommandHistoryAt(model.getCurrentCommandHistoryIndex());
    }

    public String getNextCommandHistory() {
        model.incrementCommandHistoryIndex();
        logger.debug("Showing next history (history position = {}, size = {})",
                model.getCurrentCommandHistoryIndex(), model.getCommandHistory().size());
        return model.getCommandHistoryAt(model.getCurrentCommandHistoryIndex());
    }

    public void printCommandInConsole(final String cmd) {
        view.printConsole(">", props.getColor("console.color.command.executed.cursor"), Font.BOLD);
        view.printConsole("  ");
        view.printConsole(cmd + "\n", props.getColor("console.color.command.executed"), Font.PLAIN);
    }

    public void printError(final String msg) {
        Color color = props.getColor("console.color.error");
        view.printConsole(msg + "\n", color, Font.PLAIN);
    }

    public void printConsole(final String text) {
        view.printConsole(text);
    }

    public void printConsole(final String text, final int fontStyle) {
        view.printConsole(text, fontStyle);
    }

    public void printConsole(final String text, final int fontStyle, final int size) {
        view.printConsole(text, fontStyle, size);
    }

    public void printConsole(final String text, final Color textColor) {
        view.printConsole(text, textColor);
    }

    public void printConsole(final String text, final Color textColor, final int fontStyle) {
        view.printConsole(text, textColor, fontStyle);
    }

    public void printConsole(final String text, final Color textColor, final int fontStyle,
                             final int size) {
        view.printConsole(text, textColor, fontStyle, size);
    }

    public void printConsole(final String text, final Color textColor, final Color bgColor,
                             final int fontStyle) {
        view.printConsole(text, textColor, bgColor, fontStyle);
    }

    public void printConsole(final String text, final Color textColor, final Color bgColor,
                             final int fontStyle,
                             final int size) {
        view.printConsole(text, textColor, bgColor, fontStyle, size);
    }

    public GameController getGameController() {
        return gameController;
    }
}
