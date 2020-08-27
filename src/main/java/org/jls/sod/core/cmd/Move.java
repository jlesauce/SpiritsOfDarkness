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

public class Move extends BasicCommand {

    private final Direction direction;

    public Move(final CommandController commandController) {
        this(commandController, Direction.CENTER);
    }

    public Move(final CommandController commandController, final Direction direction) {
        super(commandController);
        this.direction = direction;
    }

    @Override
    public String apply(final Command command) {
        try {
            goInDirection(getUserDirection(command));
        } catch (ConfigurationException e) {
            logger.error("An error occurred updating current position", e);
            throw new RuntimeException("Cannot update current position", e);
        }
        return null;
    }

    private Direction getUserDirection(final Command command) {
        if (direction != Direction.CENTER) {
            return direction;
        }

        String directionAsString = command.getNamespace().getString("direction");
        if (directionAsString == null || directionAsString.isEmpty()) {
            return Direction.CENTER;
        } else {
            return Direction.parseValue(directionAsString);
        }
    }

    public void goInDirection(final Direction direction) throws ConfigurationException {
        if (direction == Direction.CENTER) {
            logger.warn("Player is moving nowhere");
            this.displayController.printError(props.getString("command.navigate.error.nowhere"));
            return;
        }

        if (playerCanGoInThis(direction)) {
            Room nextRoom = nextRoomInThis(direction);
            printGoingInThis(direction);
            displayController.printRoomDescription(nextRoom);
            controller.updateCurrentPosition(nextRoom);
        } else {
            printYouCannotGoInThis(direction);
        }
    }

    private void printGoingInThis(final Direction direction) {
        logger.info("Going {} in room {}", direction, nextRoomInThis(direction).getName());
        displayController.printCommandResult(props.getString("command.navigate.text" +
                ".goingInTheDirection") + " " + direction + ".\n");
    }
}
