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
package org.jls.sod.core.cmd;

import org.jls.sod.core.loader.Loader;
import org.jls.sod.core.model.Direction;
import org.jls.sod.core.model.world.Room;

import picocli.CommandLine.Option;

public abstract class BasicCommand extends AbstractCommandCaller {

    @Option(names = { "-h", "--help" }, usageHelp = true, description = "Display help for this command")
    boolean help;

    public BasicCommand(CommandController commandController) {
        super(commandController);
    }

    protected void printHelp(final ParsedCommand command) {
        this.displayController.printCommandResult(command.getCommandLine().getUsageMessage());
    }

    protected boolean playerCanGoInThis(final Direction direction) {
        return this.model.getRoom().hasNeighbor(direction);
    }

    protected Room nextRoomInThis(final Direction direction) {
        return this.model.getRoom().getNeighbor(direction);
    }

    protected void printYouCannotGoInThis(final Direction direction) {
        this.logger.warn("No room in the direction {}", direction);
        this.displayController.printError(this.props.getString("command.navigate.error.deadEnd"));
    }

    protected boolean isValidItem(final String item) {
        return Loader.getInstance().itemExists(item);
    }

    protected void printItemDoesNotExist(final String itemId) {
        this.logger.warn("User tried to take unknown item : {}", itemId);
        this.displayController.printError(this.props.getString("command.take.error.unknownItem"));
    }
}
