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

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(name = "look", description = "Look to a direction or at an object to get a visual description")
public class Look extends SenseBase {

    @Parameters(paramLabel = "target", arity = "0..1", defaultValue = "null", description = "The direction or the object to look at")
    String target;

    public Look(CommandController commandController) {
        super(commandController);
    }

    @Override
    public String apply(ParsedCommand command) {
        if (command.getContext().isUsageHelpRequested()) {
            printHelp(command);
            return "";
        }

        if (noArgSpecified()) {
            lookToCurrentPosition();
            return "";
        }

        if (argumentIsADirection(this.target)) {
            Direction direction = Direction.parseValue(this.target);
            lookToDirection(direction);
            return "";
        }
        else if (argumentIsAnItem(this.target)) {
            String itemId = this.target;
            lookAtItem(itemId);
            return "";
        }
        else {
            throw new IllegalArgumentException("User argument is neither a direction nor an item");
        }
    }

    /**
     * Look to current position to get a visual description.
     */
    private void lookToCurrentPosition() {
        Room currentRoom = this.model.getRoom();
        this.logger.info("Looking current room : {}", currentRoom.getName());
        displayController.printCommandResult(this.props.getString("command.look.currentRoom"));
        this.displayController.printDescription(currentRoom.getLongDescrition());
    }

    /**
     * Look to the specified direction to get a short visual description.
     *
     * @param direction The direction to look to.
     */
    private void lookToDirection(final Direction direction) {
        displayController.printCommandResult(this.props.getString("command.look.to.direction") + " " + direction);

        if (playerCanGoInThis(direction)) {
            Room nextRoom = nextRoomInThis(direction);
            this.displayController.printDescription(nextRoom.getShortDescription());
            this.logger.info("Looking {} : room {}", direction, nextRoom.getName());
        }
        else {
            printYouCannotGoInThis(direction);
        }
    }

    /**
     * Look at specified item to get a visual description of it.
     *
     * @param itemId The item short identifier.
     */
    private void lookAtItem(final String itemId) {
        displayController.printCommandResult(this.props.getString("command.look.at.item") + " " + itemId);

        if (doesCurrentRoomContainsItem(itemId)) {
            printItemDescription(itemId);
        }
        else {
            printNoItemInCurrentRoom();
        }
    }

    private void printItemDescription(final String itemId) {
        ItemSlot slot = getCurrentRoomInventory().getItemSlot(itemId);
        Item item = slot.getItem();
        this.displayController.printItemDescription(item);
    }

    private void printNoItemInCurrentRoom() {
        this.displayController.printError(this.props.getString("command.look.error.noItemInRoom"));
    }
}
