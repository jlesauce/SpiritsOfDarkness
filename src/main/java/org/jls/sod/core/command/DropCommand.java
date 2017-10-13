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
import org.jls.sod.core.model.inventory.Inventory;
import org.jls.sod.core.model.inventory.InventoryQuantityException;
import org.jls.sod.core.model.inventory.ItemNotFoundException;
import org.jls.sod.core.model.inventory.NotCarriableException;
import org.jls.sod.core.model.item.Item;
import org.jls.sod.core.model.world.Room;
import org.jls.sod.util.ResourceManager;

/**
 * Command that allows the player to drop items from its inventory.
 *
 * @author LE SAUCE Julien
 * @date Nov 27, 2015
 */
public class DropCommand extends AbstractCommandExecutor {

    private final DisplayController displayController;
    private final ResourceManager props;

    /**
     * Instantiates the Drop command.
     *
     * @param model
     *            The game data model.
     * @param controller
     *            The game controller.
     */
    public DropCommand(final GameModel model, final GameController controller) {
        super(model, controller);
        this.displayController = controller.getDisplayController();
        this.props = ResourceManager.getInstance();
    }

    @Override
    public String[] getRecognizedCommands () {
        String[] cmds = { "drop" };
        return cmds;
    }

    @Override
    public void execute (final Command cmd) {
        // If no argument specified
        if (cmd.getArgumentCount() == 0) {
            this.displayController.printError(this.props.getString("command.drop.error.noItemSpecified"));
        }
        // One argument : drop [item]
        else if (cmd.getArgumentCount() == 1) {
            String itemId = cmd.getArgument(0).toLowerCase();

            // If it's an existing item
            if (Loader.getInstance().itemExists(itemId)) {
                // Tries to drop it
                try {
                    dropItem(itemId);
                } catch (ItemNotFoundException e) {
                    this.logger.warn("Item not found in character's inventory : {}", itemId);
                    this.displayController.printError(this.props.getString("command.drop.error.itemNotFound"));
                } catch (InventoryQuantityException e) {
                    this.logger.warn("Not enough {} items in character's inventory", itemId);
                    this.displayController.printError(this.props.getString("command.drop.error.notEnoughItems"));
                }
            } else {
                this.displayController.printError(this.props.getString("command.drop.error.unknownItem"));
            }
        }
        // Two arguments : drop [item] [quantity]
        else if (cmd.getArgumentCount() == 2) {
            String arg0 = cmd.getArgument(0).toLowerCase();
            String arg1 = cmd.getArgument(1);

            // If it's an existing item
            if (Loader.getInstance().itemExists(arg0)) {
                // If arg1 matches a positive integer excluding 0
                if (arg1.matches("^[1-9]\\d*$")) {

                    // Tries to drop them
                    int quantity = Integer.parseInt(arg1);
                    try {
                        dropItem(arg0, quantity);
                    } catch (ItemNotFoundException e) {
                        this.logger.warn("Item not found in character's inventory : {}", arg0);
                        this.displayController.printError(this.props.getString("command.drop.error.itemNotFound"));
                    } catch (InventoryQuantityException e) {
                        this.logger.warn("Not enough {} items in character's inventory", arg0);
                        this.displayController.printError(this.props.getString("command.drop.error.notEnoughItems"));
                    }
                } else { // Invalid quantity
                    this.displayController.printError(this.props.getString("command.drop.error.invalidQuantity"));
                }
            } else { // Unknown item
                this.displayController.printError(this.props.getString("command.drop.error.unknownItem"));
            }
        } else { // Invalid command
            this.displayController.printError(this.props.getString("command.error.invalidNbArgs"));
        }
    }

    /**
     * Drops the specified item.
     *
     * @param itemId
     *            Unique item identifier.
     * @throws ItemNotFoundException
     *             If the specified item is not contained in the character's
     *             inventory.
     * @throws InventoryQuantityException
     *             If the quantity of the specified item is too low.
     */
    private void dropItem (final String itemId) throws ItemNotFoundException, InventoryQuantityException {
        dropItem(itemId, 1);
    }

    /**
     * Drops the specified item and put it in the character's inventory.
     *
     * @param itemId
     *            Unique item identifier.
     * @param n
     *            Quantity of items to drop.
     * @throws ItemNotFoundException
     *             If the specified item is not contained in the character's
     *             inventory.
     * @throws InventoryQuantityException
     *             If the quantity of the specified item is too low.
     */
    private void dropItem (final String itemId, final int n) throws ItemNotFoundException, InventoryQuantityException {
        Room room = this.model.getRoom();
        Inventory inventory = this.model.getCharacter().getInventory();

        this.logger.info("Dropping item(s) [{}] x{} in the room {}", itemId, n, room.getId());
        Item item = inventory.removeItem(itemId, n);
        try {
            room.getInventory().addItem(item, n);
        } catch (NotCarriableException e) {
            this.logger.error("Trying to add a not carriable item : {}", item.getId(), e);
        }
    }
}