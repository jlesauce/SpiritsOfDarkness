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

public class Help extends BasicCommand {

    public Help(final CommandController commandController) {
        super(commandController);
    }

    @Override
    public String apply(final Command command) {
        String commandArg = command.getNamespace().getString("command");

        if (commandArg == null || commandArg.isEmpty()) {
            printGeneralHelp();
        } else {
            printCommandHelp(commandArg);
        }
        return null;
    }

    private void printGeneralHelp() {
        this.displayController.printMessage(new CommandParser().getHelp());
    }

    private void printCommandHelp(final String command) {
        if (isValidCommandId(command)) {
            this.displayController.printMessage(new CommandParser().getHelp(command));
        } else {
            this.logger.error("User typed an invalid command identifier: {}", command);
            this.displayController.printError(this.props.getString("command.help.invalidCmdId"));
        }
    }

    private boolean isValidCommandId(final String commandId) {
        return commandController.isCommandIdContainedInCommandList(commandId);
    }
}
