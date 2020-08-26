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
package org.jls.sod.core.cmd;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

@Command(name = "help", description = "Print the help page for the specified command")
public class Help extends BasicCommand {

    @Parameters(paramLabel = "command", arity = "0..1",
            description = "The command for which you want to get help (Default : 'help', to get help in general)")
    private String commandId;

    public Help(CommandController commandController) {
        super(commandController);
    }

    @Override
    public String apply(ParsedCommand command) {
        if (command.getContext().isUsageHelpRequested()) {
            printHelp(command);
            return "";
        }

        if (commandId == null) {
            executeHelpWithoutArgument();
        } else {
            executeHelpWithOneArgument(commandId);
        }
        return null;
    }

    private void executeHelpWithoutArgument() {
        try {
            String help = readHelpCommandDescription("help");
            this.displayController.printMessage(help != null ? help : "");
        } catch (Exception e) {
            this.logger.error("An error occurred executing help command", e);
            this.displayController.printError("Something went wrong: " + e.getMessage());
        }
    }

    private void executeHelpWithOneArgument(final String argument) {
        if (isValidCommandId(argument)) {
            String help = readHelpCommandDescription(argument);
            this.displayController.printMessage(help != null ? help : "");
        } else {
            this.logger.error("User typed an invalid command identifier: {}", argument);
            this.displayController.printError(this.props.getString("command.help.invalidCmdId"));
        }
    }

    private boolean isValidCommandId(final String commandId) {
        return commandController.isCommandIdContainedInCommandList(commandId);
    }

    private String readHelpCommandDescription(final String commandId) {
        String filepath = getHelpFilePathFromResources(commandId);
        if (filepath == null) {
            return null;
        }

        InputStream in = getClass().getResourceAsStream("/" + filepath);
        BufferedReader input = new BufferedReader(new InputStreamReader(in));
        return input.lines().collect(Collectors.joining());
    }

    private String getHelpFilePathFromResources(final String commandId) {
        String key = "command." + commandId + ".help";
        String filepath = null;

        try {
            filepath = this.props.getString(key);
        } catch (Exception e) {
            this.logger.error("Help filepath not found in resource files: key={}", key, e);
            this.displayController.printError(this.props.getString("command.help.helpFileNotFound"));
        }
        return filepath;
    }
}
