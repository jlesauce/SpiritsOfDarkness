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
import org.jls.sod.core.model.inventory.ItemSlot;
import org.jls.sod.core.model.item.Item;
import org.jls.sod.core.model.world.Room;

public class Look extends SenseBase {

    public Look(final CommandController commandController) {
        super(commandController);
    }

    @Override
    public String apply(final Command command) {
        String target = command.getNamespace().getString("target");

        if (target == null || target.isEmpty()) {
            lookToCurrentPosition();
            return null;
        }
        if (isADirection(target)) {
            Direction direction = Direction.parseValue(target);
            lookToDirection(direction);
            return null;
        } else if (isValidItem(target)) {
            lookAtItem(target);
            return null;
        } else {
            throw new IllegalArgumentException("User argument is neither a direction nor an item");
        }
    }

    private void lookToCurrentPosition() {
        Room currentRoom = this.model.getRoom();
        logger.info("Looking current room : {}", currentRoom.getName());
        displayController.printCommandResult(props.getString("command.look.currentRoom"));
        displayController.printDescription(currentRoom.getLongDescrition());
    }

    private void lookToDirection(final Direction direction) {
        displayController.printCommandResult(props.getString("command.look.to.direction") + " " + direction);

        if (playerCanGoInThis(direction)) {
            Room nextRoom = nextRoomInThis(direction);
            displayController.printDescription(nextRoom.getShortDescription());
            logger.info("Looking {} : room {}", direction, nextRoom.getName());
        } else {
            printYouCannotGoInThis(direction);
        }
    }

    private void lookAtItem(final String itemId) {
        displayController.printCommandResult(props.getString("command.look.at.item") + " " + itemId);

        if (doesCurrentRoomContainsItem(itemId)) {
            printItemDescription(itemId);
        } else {
            printNoItemInCurrentRoom();
        }
    }

    private void printItemDescription(final String itemId) {
        ItemSlot slot = getCurrentRoomInventory().getItemSlot(itemId);
        Item item = slot.getItem();
        displayController.printItemDescription(item);
    }

    private void printNoItemInCurrentRoom() {
        displayController.printError(props.getString("command.look.error.noItemInRoom"));
    }
}
