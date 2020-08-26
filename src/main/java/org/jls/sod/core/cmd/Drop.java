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

import org.jls.sod.core.model.inventory.Inventory;
import org.jls.sod.core.model.inventory.InventoryQuantityException;
import org.jls.sod.core.model.inventory.ItemNotFoundException;
import org.jls.sod.core.model.inventory.NotCarriableException;
import org.jls.sod.core.model.item.Item;
import org.jls.sod.core.model.world.Room;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(name = "drop", description = "Drop an item from your inventory")
public class Drop extends BasicCommand {

    @Parameters(paramLabel = "item", index = "0", description = "Identifier of the item to drop")
    private String itemId;

    @Parameters(paramLabel = "quantity", index = "1", defaultValue = "1", description = "Quantity of items to drop (Default: 1)")
    private int quantity;

    public Drop(CommandController commandController) {
        super(commandController);
    }

    @Override
    public String apply(ParsedCommand command) {
        if (command.getContext().isUsageHelpRequested()) {
            printHelp(command);
            return "";
        }

        if (isValidItem(itemId)) {
            try {
                dropItem(itemId, quantity);
            } catch (ItemNotFoundException e) {
                this.logger.warn("Item not found in inventory : {}", itemId);
                this.displayController.printError(this.props.getString("command.take.error.itemNotFound"));
            } catch (InventoryQuantityException e) {
                this.logger.warn("Not enough {} items in inventory", itemId);
                this.displayController.printError(this.props.getString("command.take.error.notEnoughItems"));
            }
        }
        else {
            printItemDoesNotExist(itemId);
        }

        return null;
    }

    /**
     * Drop the specified item and put it in the current room.
     *
     * @param itemId
     *                 Unique item identifier.
     * @param quantity
     *                 Quantity of items to drop.
     * @throws ItemNotFoundException
     *                                    If the specified item is not contained
     *                                    in the character's inventory.
     * @throws InventoryQuantityException
     *                                    If the quantity is inconsistent with
     *                                    content of inventory.
     */
    private void dropItem(final String itemId, final int quantity)
            throws ItemNotFoundException, InventoryQuantityException {
        Room room = this.model.getRoom();
        Inventory inventory = this.model.getCharacter().getInventory();

        this.logger.info("Dropping item(s) [{}] x{} in the room {}", itemId, quantity, room.getId());
        Item item = inventory.removeItem(itemId, quantity);
        try {
            room.getInventory().addItem(item, quantity);
        } catch (NotCarriableException e) {
            this.logger.error("Trying to add a not carriable item : {}", item.getId(), e);
        }
    }
}
