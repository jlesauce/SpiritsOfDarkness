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

import org.apache.commons.configuration.ConfigurationException;
import org.jls.sod.core.model.Direction;
import org.jls.sod.core.model.world.Room;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(name = "move", description = "Move the player in the specified direction")
public class Move extends BasicCommand {

    @Parameters(paramLabel = "direction", arity = "0..1", defaultValue = "CENTER", description = "The direction where to move")
    private String givenDirection;

    private Direction direction;

    public Move(CommandController commandController) {
        super(commandController);
        this.direction = null;
    }

    public Move(CommandController commandController, Direction direction) {
        super(commandController);
        this.direction = direction;
    }

    @Override
    public String apply(ParsedCommand command) {
        if (command.getContext().isUsageHelpRequested()) {
            printHelp(command);
            return "";
        }

        try {
            goInDirection(getUserDirection());
        } catch (ConfigurationException e) {
            this.logger.error("An error occurred updating current position", e);
            throw new RuntimeException("Cannot update current position", e);
        }
        return "";
    }

    private Direction getUserDirection() {
        return this.direction != null ? this.direction : Direction.parseValue(this.givenDirection);
    }

    /**
     * Move the player in the specified direction.
     *
     * @param direction The direction where to go.
     * @throws ConfigurationException If an error occurred updating the current
     *                                position.
     */
    public void goInDirection(final Direction direction) throws ConfigurationException {
        if (playerCanGoInThis(direction)) {
            Room nextRoom = nextRoomInThis(direction);

            printGoingInThis(direction);
            this.displayController.printRoomDescription(nextRoom);
            this.controller.updateCurrentPosition(nextRoom);
        }
        else {
            printYouCannotGoInThis(direction);
        }
    }

    private void printGoingInThis(final Direction direction) {
        this.logger.info("Going {} in room {}", direction, nextRoomInThis(direction).getName());
        this.displayController.printCommandResult(
                this.props.getString("command.navigate.text.goingInTheDirection") + " " + direction + ".\n");
    }
}
