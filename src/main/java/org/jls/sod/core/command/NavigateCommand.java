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

package org.jls.sod.core.command;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.lang.ArrayUtils;
import org.jls.sod.core.DisplayController;
import org.jls.sod.core.GameController;
import org.jls.sod.core.GameModel;
import org.jls.sod.core.model.Direction;
import org.jls.sod.core.model.world.Room;
import org.jls.sod.util.ResourceManager;

/**
 * Command that allows the player to navigate from room to room.
 * 
 * @author LE SAUCE Julien
 * @date Sep 3, 2015
 */
public class NavigateCommand extends AbstractCommandExecutor {

    private final DisplayController displayController;
    private final ResourceManager props;

    /**
     * Instantiates the navigate command.
     * 
     * @param model
     *            The game data model.
     * @param controller
     *            The game controller.
     */
    public NavigateCommand(final GameModel model, final GameController controller) {
        super(model, controller);
        this.displayController = controller.getDisplayController();
        this.props = ResourceManager.getInstance();
    }

    @Override
    public String[] getRecognizedCommands () {
        String[] cmds = new String[0];
        for (Direction d : Direction.values()) {
            cmds = (String[]) ArrayUtils.addAll(cmds, d.getMatchingLabels());
        }
        return cmds;
    }

    @Override
    public void execute (final Command cmd) {
        Direction direction = Direction.parseValue(cmd.getCommandId());
        try {
            goInDirection(direction);
        } catch (ConfigurationException e) {
            this.logger.error("An error occurred updating current position", e);
            this.displayController.printError("Cannot update current position : " + e.getMessage());
        }
    }

    /**
     * Moves the player in the specified direction.
     * 
     * @param direction
     *            The direction where to go.
     * @throws ConfigurationException
     *             If an error occurred updating the current position.
     */
    public void goInDirection (final Direction direction) throws ConfigurationException {
        Room room = this.model.getRoom();

        // If player can go in the specified direction
        if (room.hasNeighbor(direction)) {
            Room nextRoom = room.getNeighbor(direction);

            // Prints the result
            this.logger.info("Going {} in room {}", direction, nextRoom.getName());
            this.displayController.printCommandResult(
                    this.props.getString("command.navigate.text.goingInTheDirection") + " " + direction + ".\n");
            this.displayController.printRoomDescription(nextRoom);

            // Updates the player current position
            this.controller.updateCurrentPosition(nextRoom);
        } else { // Prints an error
            this.logger.warn("No room in the direction {}", direction);
            this.displayController.printError(this.props.getString("command.navigate.error.deadEnd"));
        }
    }
}
