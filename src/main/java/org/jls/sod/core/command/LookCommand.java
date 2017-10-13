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

import org.jls.sod.core.DisplayController;
import org.jls.sod.core.GameController;
import org.jls.sod.core.GameModel;
import org.jls.sod.core.loader.Loader;
import org.jls.sod.core.model.Direction;
import org.jls.sod.core.model.Sense;
import org.jls.sod.core.model.inventory.Inventory;
import org.jls.sod.core.model.inventory.ItemSlot;
import org.jls.sod.core.model.item.Item;
import org.jls.sod.core.model.world.Room;
import org.jls.sod.util.ResourceManager;

/**
 * Command that allows the player to inspect rooms or objects.
 * 
 * @author LE SAUCE Julien
 * @date Sep 4, 2015
 */
public class LookCommand extends AbstractCommandExecutor {

    private final DisplayController displayController;
    private final ResourceManager props;

    /**
     * Instantiates a new command.
     * 
     * @param model
     *            The game data model.
     * @param controller
     *            The game controller.
     */
    public LookCommand(final GameModel model, final GameController controller) {
        super(model, controller);
        this.displayController = controller.getDisplayController();
        this.props = ResourceManager.getInstance();
    }

    @Override
    public String[] getRecognizedCommands () {
        String[] cmds = { "look", "inspect", "feel", "touch", "smell", "taste", "listen", "items" };
        return cmds;
    }

    @Override
    public void execute (Command cmd) {
        Room room = this.model.getRoom();

        // If no argument
        if (cmd.getArgumentCount() == 0) {
            // Then all commands apply to the room
            switch (cmd.getCommandId()) {
                case "look":
                    this.displayController.printRoomDescription(room);
                    break;
                case "inspect":
                case "feel":
                case "touch":
                case "smell":
                case "taste":
                case "listen":
                    this.displayController.printSenseDescription(room, Sense.parseValue(cmd.getCommandId()));
                    break;
                case "items":
                    this.displayController.printItems(room.getInventory());
                    break;
                default:
                    throw new IllegalArgumentException("Unknown command : " + cmd.getCommandId());
            }
        }
        // One argument
        else if (cmd.getArgumentCount() == 1) {

            // If argument is a direction
            if (Direction.matchDirection(cmd.getArgument(0))) {

                // Gets the next room's description
                switch (cmd.getCommandId()) {
                    case "look":
                        lookToDirection(Direction.parseValue(cmd.getArgument(0)));
                        break;
                    case "inspect":
                    case "feel":
                    case "touch":
                    case "smell":
                    case "taste":
                    case "listen":
                        this.displayController.printError(this.props.getString("command.error.notAvailable"));
                        break;
                    default:
                        this.displayController.printError(this.props.getString("command.error.unknownCommand"));
                }
            }
            // If argument is an item
            else if (Loader.getInstance().itemExists(cmd.getArgument(0).toLowerCase())) {
                String itemId = cmd.getArgument(0).toLowerCase();

                // Gets the item's description
                switch (cmd.getCommandId()) {
                    case "look":
                        lookItem(itemId);
                        break;
                    case "inspect":
                    case "feel":
                    case "touch":
                    case "smell":
                    case "taste":
                    case "listen":
                        lookItem(itemId, Sense.parseValue(cmd.getCommandId()));
                        break;
                    default:
                        this.displayController.printError(this.props.getString("command.error.unknownCommand"));
                }
            }
            // Unknown argument
            else {
                this.displayController.printError(this.props.getString("command.look.error.unkownArg"));
            }
        } else { // Too many arguments
            this.displayController.printError(this.props.getString("command.error.invalidNbArgs"));
        }
    }

    /**
     * Looks to the specified direction.
     * 
     * @param direction
     *            The direction where to look.
     */
    private void lookToDirection (final Direction direction) {
        Room room = this.model.getRoom();
        // If player can go in the specified direction
        if (room.hasNeighbor(direction)) {
            Room nextRoom = room.getNeighbor(direction);
            this.displayController.printDescription(nextRoom.getShortDescription());
            this.logger.info("Looking {} : room {}", direction, nextRoom.getName());
        } else {
            this.logger.warn("No room in the direction {}", direction);
            this.displayController.printError("There is nothing here.");
        }
    }

    /**
     * Prints the description of the item.
     * 
     * @param itemId
     *            The specified item.
     */
    private void lookItem (final String itemId) {
        Room room = this.model.getRoom();
        Inventory localInvent = room.getInventory();

        // If the room effectively contains this item
        if (localInvent.containsItem(itemId)) {
            ItemSlot slot = localInvent.getItemSlot(itemId);
            Item item = slot.getItem();
            this.displayController.printItemDescription(item);
        } else {
            this.displayController.printError(this.props.getString("command.look.error.noItemInRoom"));
        }
    }

    /**
     * Prints the specified description of the item.
     * 
     * @param itemId
     *            The specified item.
     * @param sense
     *            The associated sense.
     */
    private void lookItem (final String itemId, final Sense sense) {
        Room room = this.model.getRoom();
        Inventory localInvent = room.getInventory();

        // If the room effectively contains this item
        if (localInvent.containsItem(itemId)) {
            ItemSlot slot = localInvent.getItemSlot(itemId);
            Item item = slot.getItem();
            this.displayController.printSenseDescription(item, sense);
        } else {
            this.displayController.printError(this.props.getString("command.look.error.noItemInRoom"));
        }
    }
}