/*#
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
 #*/

package org.jls.sod.core.command;

import org.jls.sod.core.DisplayController;
import org.jls.sod.core.GameController;
import org.jls.sod.core.GameModel;
import org.jls.sod.core.loader.Loader;
import org.jls.sod.core.model.character.Character;
import org.jls.sod.core.model.inventory.Inventory;
import org.jls.sod.core.model.inventory.InventoryQuantityException;
import org.jls.sod.core.model.inventory.ItemNotFoundException;
import org.jls.sod.core.model.inventory.NotCarriableException;
import org.jls.sod.core.model.item.Item;
import org.jls.sod.core.model.world.Room;
import org.jls.sod.util.ResourceManager;

/**
 * Command that allows the player to take items in the environment.
 *
 * @author LE SAUCE Julien
 * @date Nov 27, 2015
 */
public class TakeCommand extends AbstractCommandExecutor {

	private final DisplayController displayController;
	private final ResourceManager props;

	/**
	 * Instantiates the take command.
	 *
	 * @param model
	 *            The game data model.
	 * @param controller
	 *            The game controller.
	 */
	public TakeCommand (final GameModel model, final GameController controller) {
		super(model, controller);
		this.displayController = controller.getDisplayController();
		this.props = ResourceManager.getInstance();
	}

	@Override
	public String[] getRecognizedCommands () {
		String[] cmds = {"take"};
		return cmds;
	}

	@Override
	public void execute (final Command cmd) {
		// If no argument specified
		if (cmd.getArgumentCount() == 0) {
			this.displayController.printError(this.props.getString("command.take.error.noItemSpecified"));
		}
		// One argument : take [item] or take [keyword]
		else if (cmd.getArgumentCount() == 1) {
			String arg = cmd.getArgument(0).toLowerCase();

			// If it's an existing item
			if (Loader.getInstance().itemExists(arg)) {
				// Tries to take it
				try {
					takeItem(arg);
				} catch (ItemNotFoundException e) {
					this.logger.warn("Item not found in room's inventory : {}", arg);
					this.displayController.printError(this.props.getString("command.take.error.itemNotFound"));
				} catch (InventoryQuantityException e) {
					this.logger.warn("Not enough {} items in room's inventory", arg);
					this.displayController.printError(this.props.getString("command.take.error.notEnoughItems"));
				}
			} else {
				// Else we check if it's a special keyword
				switch (arg) {
					case "all": // Take all the items in the room
						takeAllItems();
						break;
					default:
						// We consider that player wanted to type an item
						// that we didn't know
						this.displayController.printError(this.props.getString("command.take.error.unknownItem"));
				}
			}
		}
		// Two arguments : take [item] [quantity]
		else if (cmd.getArgumentCount() == 2) {
			String arg0 = cmd.getArgument(0).toLowerCase();
			String arg1 = cmd.getArgument(1);

			// If it's an existing item
			if (Loader.getInstance().itemExists(arg0)) {
				// If arg1 matches a positive integer excluding 0
				if (arg1.matches("^[1-9]\\d*$")) {

					// Tries to take them
					int quantity = Integer.parseInt(arg1);
					try {
						takeItem(arg0, quantity);
					} catch (ItemNotFoundException e) {
						this.logger.warn("Item not found in room's inventory : {}", arg0);
						this.displayController.printError(this.props.getString("command.take.error.itemNotFound"));
					} catch (InventoryQuantityException e) {
						this.logger.warn("Not enough {} items in room's inventory", arg0);
						this.displayController.printError(this.props.getString("command.take.error.notEnoughItems"));
					}
				} else { // Invalid quantity
					this.displayController.printError(this.props.getString("command.take.error.invalidQuantity"));
				}
			} else { // Unknown item
				this.displayController.printError(this.props.getString("command.take.error.unknownItem"));
			}
		} else { // Invalid command
			this.displayController.printError(this.props.getString("command.error.invalidNbArgs"));
		}
	}

	/**
	 * Takes the specified item.
	 *
	 * @param itemId
	 *            Unique item identifier.
	 * @throws ItemNotFoundException
	 *             If the specified item is not contained in the room's
	 *             inventory.
	 * @throws InventoryQuantityException
	 *             If the quantity of the specified item is too low.
	 */
	private void takeItem (final String itemId) throws ItemNotFoundException, InventoryQuantityException {
		takeItem(itemId, 1);
	}

	/**
	 * Takes the specified item and put it in the character's inventory.
	 *
	 * @param itemId
	 *            Unique item identifier.
	 * @param n
	 *            Quantity of items to take.
	 * @throws ItemNotFoundException
	 *             If the specified item is not contained in the room's
	 *             inventory.
	 * @throws InventoryQuantityException
	 *             If the quantity of the specified item is too low.
	 */
	private void takeItem (final String itemId, final int n) throws ItemNotFoundException, InventoryQuantityException {
		Room room = this.model.getRoom();
		Inventory inventory = this.model.getCharacter().getInventory();

		this.logger.info("Taking item(s) [{}] x{} from room {}", itemId, n, room.getId());
		Item item = room.getInventory().removeItem(itemId, n);
		try {
			inventory.addItem(item, n);
		} catch (NotCarriableException e) {
			this.logger.error("Trying to add a not carriable item : {}", item.getId(), e);
		}
	}

	/**
	 * Takes all items in the current room.
	 */
	private void takeAllItems () {
		Room room = this.model.getRoom();
		Character character = this.model.getCharacter();

		this.logger.info("Taking all items from room {}", room.getId());

		// Transfer all items in the room to the character's inventory
		character.getInventory().importInventory(room.getInventory());

		// TODO prints the items picked by the player in the console
	}
}