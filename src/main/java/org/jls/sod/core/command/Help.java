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

package org.jls.sod.core.command;

import java.io.IOException;

import org.jls.sod.core.DisplayController;
import org.jls.sod.util.ResourceManager;
import org.jls.toolbox.util.file.SimpleFile;

public class Help extends AbstractCommandExecutor {

    private final DisplayController displayController;
    private final ResourceManager resources;

    public Help(final CommandController commandController) {
        super(commandController);
        this.displayController = controller.getDisplayController();
        this.resources = ResourceManager.getInstance();
    }

    @Override
    public String[] getRecognizedCommands () {
        String[] cmds = { "help" };
        return cmds;
    }

    @Override
    public String getSmallId () {
        return "help";
    }

    @Override
    public void execute (final Command cmd) {
        if (!cmd.hasArguments()) {
            executeHelpWithoutArgument();
        } else if (cmd.getArgumentCount() == 1) {
            executeHelpWithOneArgument(cmd.getArgument(0).toLowerCase());
        } else {
            this.displayController.printError(ResourceManager.getInstance().getString("command.error.tooManyArgs"));
        }
    }

    private void executeHelpWithoutArgument () {
        try {
            String help = readHelpCommandDescription("help");
            this.displayController.printMessage(help != null ? help : "");
        } catch (Exception e) {
            this.logger.error("An error occurred executing help command", e);
            this.displayController.printError("Something went wrong: " + e.getMessage());
        }
    }

    private void executeHelpWithOneArgument (final String argument) {
        if (isValidCommandId(argument)) {
            String help = readHelpCommandDescription(argument);
            this.displayController.printMessage(help != null ? help : "");
        } else {
            this.logger.error("User typed an invalid command identifier: {}", argument);
            this.displayController.printError(this.resources.getString("command.help.invalidCmdId"));
        }
    }

    private boolean isValidCommandId (final String commandId) {
        return commandController.isCommandIdContainedInCommandList(commandId);
    }

    private String getHelpFilePathFromResources (final String commandId) {
        String key = "command." + commandId + ".help";
        String filepath = null;

        try {
            filepath = this.resources.getString(key);
        } catch (Exception e) {
            this.logger.error("Help filepath not found in resource files: key={}", key, e);
            this.displayController.printError(this.resources.getString("command.help.helpFileNotFound"));
        }
        return filepath;
    }

    private String readHelpCommandDescription (final String commandId) {
        String filepath = getHelpFilePathFromResources(commandId);
        if (filepath == null) {
            return null;
        }

        SimpleFile helpFile = getHelpCommandDescriptionFile(filepath);
        if (helpFile != null) {
            return readFileContent(helpFile);
        } else {
            return null;
        }
    }

    private SimpleFile getHelpCommandDescriptionFile (final String filepath) {
        try {
            return new SimpleFile(ResourceManager.getResourceAsFile(filepath));
        } catch (Exception e) {
            this.logger.error("Help file not found: {}", filepath, e);
            this.displayController.printError(this.resources.getString("command.help.helpFileNotFound"));
        }
        return null;
    }

    private String readFileContent (final SimpleFile file) {
        try {
            return file.readFileContentAsString();
        } catch (IOException e) {
            this.logger.error("An error occurred reading {} content", file, e);
            this.displayController.printError("Failed to read file content: " + e.getMessage());
            return "";
        }
    }
}
