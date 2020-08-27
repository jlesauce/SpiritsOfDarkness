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

import org.jls.sod.core.model.Direction;
import org.jls.sod.core.model.Sense;
import org.jls.sod.core.model.inventory.Inventory;
import org.jls.sod.core.model.inventory.ItemSlot;
import org.jls.sod.core.model.item.Item;
import org.jls.sod.core.model.world.Room;

public abstract class SenseBase extends BasicCommand {

    protected Sense sense;

    public SenseBase(final CommandController commandController) {
        super(commandController);
        sense = null;
    }

    protected String applyCommandWith(final Sense sense, final Command command) {
        String target = command.getNamespace().getString("target");

        if (target == null || target.isEmpty()) {
            printSenseDescription(sense);
            return null;
        }

        if (isADirection(target)) {
            Direction direction = Direction.parseValue(target);
            printSenseDescription(sense, direction);
            return null;
        } else if (isValidItem(target)) {
            printItemSenseDescription(target, sense);
            return null;
        } else {
            throw new IllegalArgumentException("User argument is neither a direction nor an item");
        }
    }

    protected void printSenseDescription(final Sense sense) {
        displayController.printSenseDescription(model.getRoom(), sense);
    }

    protected void printSenseDescription(final Sense sense, final Direction direction) {
        if (playerCanGoInThis(direction)) {
            Room nextRoom = nextRoomInThis(direction);
            displayController.printSenseDescription(nextRoom, sense);
            logger.info("Using sense {} to {} : room {}", sense, direction, nextRoom.getName());
        } else {
            printYouCannotGoInThis(direction);
        }
    }

    protected void printItemSenseDescription(final String itemId, final Sense sense) {
        if (doesCurrentRoomContainsItem(itemId)) {
            ItemSlot slot = model.getRoom().getInventory().getItemSlot(itemId);
            Item item = slot.getItem();
            displayController.printSenseDescription(item, sense);
        } else {
            displayController.printError(props.getString("command.look.error.noItemInRoom"));
        }
    }

    protected boolean doesCurrentRoomContainsItem(final String item) {
        return getCurrentRoomInventory().containsItem(item);
    }

    protected Inventory getCurrentRoomInventory() {
        return model.getRoom().getInventory();
    }

    protected boolean isADirection(final String arg) {
        return Direction.matchDirection(arg.toLowerCase());
    }
}
