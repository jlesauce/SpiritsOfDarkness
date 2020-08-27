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

public class Drop extends BasicCommand {

    public Drop(final CommandController commandController) {
        super(commandController);
    }

    @Override
    public String apply(final Command command) {
        String item = command.getNamespace().getString("item");
        int quantity = command.getNamespace().getInt("quantity");

        if (item == null || item.isEmpty()) {
            displayController.printError(props.getString("command.take.error.noItemSpecified"));
            return null;
        } else if (isValidItem(item)) {
            try {
                dropItem(item, quantity);
            } catch (ItemNotFoundException e) {
                logger.warn("Item not found in inventory : {}", item);
                displayController.printError(props.getString("command.take.error.itemNotFound"));
            } catch (InventoryQuantityException e) {
                logger.warn("Not enough {} items in inventory", item);
                displayController.printError(
                        this.props.getString("command.take.error.notEnoughItems"));
            }
        } else {
            printItemDoesNotExist(item);
        }
        return null;
    }

    private void dropItem(final String itemId, final int quantity)
            throws ItemNotFoundException, InventoryQuantityException {
        Room room = model.getRoom();
        Inventory inventory = model.getCharacter().getInventory();

        Item item = inventory.removeItem(itemId, quantity);
        try {
            logger.info("Dropping item(s) [{}] x{} in the room {}", itemId, quantity,
                    room.getId());
            displayController.printCommandResult(
                    props.getString("command.drop.item") + " [" + itemId + "] x" + quantity);
            room.getInventory().addItem(item, quantity);
        } catch (NotCarriableException e) {
            logger.error("Trying to add a not transportable item : {}", item.getId(), e);
        }
    }
}
