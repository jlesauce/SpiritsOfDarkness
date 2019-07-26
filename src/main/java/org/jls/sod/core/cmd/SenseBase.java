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
import org.jls.sod.core.model.Sense;
import org.jls.sod.core.model.inventory.Inventory;
import org.jls.sod.core.model.inventory.ItemSlot;
import org.jls.sod.core.model.item.Item;
import org.jls.sod.core.model.world.Room;

import picocli.CommandLine.Parameters;

public abstract class SenseBase extends BasicCommand {

    @Parameters(paramLabel = "target", arity = "0..1", defaultValue = "null")
    protected String target;

    protected Sense sense;

    public SenseBase(CommandController commandController) {
        super(commandController);
        this.sense = null;
    }

    protected String applyCommandWith(final Sense sense, final ParsedCommand command) {
        if (noArgSpecified()) {
            printSenseDescription(this.sense);
            return "";
        }

        if (argumentIsADirection(this.target)) {
            Direction direction = Direction.parseValue(this.target);
            printSenseDescription(this.sense, direction);
            return "";
        }
        else if (argumentIsAnItem(this.target)) {
            String itemId = this.target;
            printItemSenseDescription(itemId, this.sense);
            return "";
        }
        else {
            throw new IllegalArgumentException("User argument is neither a direction nor an item");
        }
    }

    protected void printSenseDescription(final Sense sense) {
        this.displayController.printSenseDescription(this.model.getRoom(), sense);
    }

    protected void printSenseDescription(final Sense sense, final Direction direction) {
        if (playerCanGoInThis(direction)) {
            Room nextRoom = nextRoomInThis(direction);
            this.displayController.printSenseDescription(nextRoom, sense);
            this.logger.info("Using sense {} to {} : room {}", sense, direction, nextRoom.getName());
        }
        else {
            printYouCannotGoInThis(direction);
        }
    }

    protected void printItemSenseDescription(final String itemId, final Sense sense) {
        if (doesCurrentRoomContainsItem(itemId)) {
            ItemSlot slot = this.model.getRoom().getInventory().getItemSlot(itemId);
            Item item = slot.getItem();
            this.displayController.printSenseDescription(item, sense);
        }
        else {
            this.displayController.printError(this.props.getString("command.look.error.noItemInRoom"));
        }
    }

    protected boolean doesCurrentRoomContainsItem(final String item) {
        return getCurrentRoomInventory().containsItem(item);
    }

    protected Inventory getCurrentRoomInventory() {
        return this.model.getRoom().getInventory();
    }

    protected boolean noArgSpecified() {
        return this.target.equals("null");
    }

    protected boolean argumentIsADirection(final String arg) {
        return Direction.matchDirection(arg.toLowerCase());
    }

    protected boolean argumentIsAnItem(final String arg) {
        return Loader.getInstance().itemExists(arg.toLowerCase());
    }
}
