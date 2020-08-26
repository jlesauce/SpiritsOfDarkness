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

package org.jls.sod.core;

import java.awt.Color;
import java.awt.Font;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.jls.sod.ApplicationController;
import org.jls.sod.core.model.Direction;
import org.jls.sod.core.model.Sense;
import org.jls.sod.core.model.inventory.Inventory;
import org.jls.sod.core.model.inventory.ItemSlot;
import org.jls.sod.core.model.item.Item;
import org.jls.sod.core.model.world.Room;
import org.jls.sod.util.ResourceManager;

/**
 * Delegate controller used to print all the information to the player in the
 * console.
 * 
 * @author LE SAUCE Julien
 * @date Nov 27, 2015
 */
public class DisplayController {

    private final ApplicationController controller;
    private final ResourceManager props;

    /**
     * Instantiates a new display delegate controller.
     * 
     * @param model
     *            The game data model.
     * @param controller
     *            The controller of the application.
     */
    public DisplayController(final GameModel model, final ApplicationController controller) {
        this.controller = controller;
        this.props = ResourceManager.getInstance();
    }

    /**
     * Prints an error message in the console.
     * 
     * @param msg
     *            The error message.
     */
    public void printError (final String msg) {
        this.controller.printError("\t" + msg);
    }

    /**
     * Prints the specified message in the console.
     * 
     * @param msg
     *            Message to print.
     */
    public void printMessage (final String msg) {
        printMessage(msg, this.props.getColor("console.color.info"));
    }

    /**
     * Prints the specified message in the console.
     * 
     * @param msg
     *            Message to print.
     * @param color
     *            The color of the message.
     */
    public void printMessage (final String msg, final Color color) {
        this.controller.printConsole(msg + "\n", color, Font.PLAIN);
    }

    /**
     * Prints the result of a command.
     * 
     * @param result
     *            Result of a command.
     */
    public void printCommandResult (final String result) {
        this.controller.printConsole("\t" + result + "\n", this.props.getColor("console.color.command.result"),
                Font.PLAIN);
    }

    /**
     * Prints a short description in the game console.
     * 
     * @param text
     *            Short description to print.
     */
    public void printDescription (final String text) {
        this.controller.printConsole("\t" + text + "\n", this.props.getColor("console.color.text.description.short"),
                Font.PLAIN);
    }

    /**
     * Prints the welcome message to the player.
     */
    public void printWelcomeMessage () {
        Color color = this.props.getColor("console.color.info");
        this.controller.printConsole("\n\t******************************************************\n", color, Font.PLAIN);
        this.controller.printConsole("\t\tWelcome to Spirits of Darkness !\n\n", color, Font.PLAIN);
        this.controller.printConsole("\t\tType 'help' to get the command list.\n", color, Font.PLAIN);
        this.controller.printConsole("\t******************************************************\n\n", color, Font.PLAIN);
    }

    /**
     * Prints the description of the specified room to the player.
     * 
     * @param room
     *            Room data model.
     */
    public void printRoomDescription (final Room room) {
        Color tag = this.props.getColor("console.color.text.description.tag.bold");
        Color tagText = this.props.getColor("console.color.text.description.tag.plain");
        Color color = this.props.getColor("console.color.text.description.long");

        this.controller.printConsole("## " + room.getName() + " ##\n\n", color, Font.PLAIN);
        this.controller.printConsole(room.getLongDescrition() + "\n\n", color, Font.PLAIN);

        // Count the max nb of chars
        int nbMaxChars = 0;
        for (Direction direction : Direction.values()) {
            int n = direction.name().toLowerCase().length();
            nbMaxChars = n > nbMaxChars ? n : nbMaxChars;
        }

        // Prints the available directions
        for (Direction direction : Direction.values()) {
            if (room.hasNeighbor(direction)) {
                String dirStr = StringUtils.rightPad(direction.name().toLowerCase(), nbMaxChars);
                this.controller.printConsole("\t" + dirStr, tag, Font.BOLD);
                this.controller.printConsole("\t" + room.getNeighborDescription(direction) + "\n", tagText, Font.PLAIN);
            }
        }
        this.controller.printConsole("\n");
    }

    /**
     * Prints the description of the specified item to the player.
     * 
     * @param item
     *            Item data model.
     */
    public void printItemDescription (final Item item) {
        Color color = this.props.getColor("console.color.text.description.long");

        this.controller.printConsole("## " + item.getName() + " ##\n\n", color, Font.PLAIN);
        this.controller.printConsole(item.getLongDescrition() + "\n", color, Font.PLAIN);
    }

    /**
     * Prints the list of the items contained in the specified room to the player.
     * 
     * @param inventory
     *            The inventory to print.
     */
    public void printItems (final Inventory inventory) {
        Color color = this.props.getColor("console.color.command.result");
        Color itemColor = this.props.getColor("console.color.item.name");
        Color itemIdColor = this.props.getColor("console.color.item.id");

        if (inventory.size() > 0) {
            printMessage(this.props.getString("command.look.items.text.listItems"), color);

            // Iterates over the items
            for (Entry<String, ItemSlot> entry : inventory.getSlots().entrySet()) {
                ItemSlot slot = entry.getValue();
                this.controller.printConsole("\tx" + slot.getQuantity(), color, Font.PLAIN);
                this.controller.printConsole("  [" + slot.getItem().getName() + "] (", itemColor, Font.BOLD);
                this.controller.printConsole(slot.getItem().getId(), itemIdColor, Font.ITALIC);
                this.controller.printConsole(")\n", itemColor, Font.BOLD);
            }
            this.controller.printConsole("\n");
        } else {
            this.controller.printConsole("\t" + this.props.getString("comand.inventory.noItems") + "\n", color);
        }
    }

    /**
     * Prints the description of the specified sense for the room to the player.
     * 
     * @param room
     *            Room data model.
     * @param sense
     *            Specified sense.
     */
    public void printSenseDescription (final Room room, final Sense sense) {
        // If a description is provided
        if (room.hasSense(sense)) {
            printCommandResult(room.getSenseDescription(sense) + "\n");
        } else {
            // Else print the default description
            String defaultSenseDesc = this.props
                    .getString("command.look." + sense.name().toLowerCase() + ".default.noData");
            printCommandResult(defaultSenseDesc + "\n");
        }
    }

    /**
     * Prints the specified item's description for the specified sense to the
     * player.
     * 
     * @param item
     *            Item data model.
     * @param sense
     *            Specified sense.
     */
    public void printSenseDescription (final Item item, final Sense sense) {
        // If a description is provided
        if (item.hasSense(sense)) {
            printCommandResult(item.getSenseDescription(sense) + "\n");
        } else {
            // Else print the default description
            String defaultSenseDesc = this.props
                    .getString("command.look." + sense.name().toLowerCase() + ".default.noData");
            printCommandResult(defaultSenseDesc + "\n");
        }
    }
}
