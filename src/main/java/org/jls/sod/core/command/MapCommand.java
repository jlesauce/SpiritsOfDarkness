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
import org.jls.sod.util.ResourceManager;

/**
 * Command used to show the map to the player.
 * 
 * @author AwaX
 * @date 27 nov. 2015
 */
public class MapCommand extends AbstractCommandExecutor {

	private final DisplayController displayController;
	private final ResourceManager props;

	/**
	 * Instantiates the Map command.
	 * 
	 * @param model
	 *            The game data model.
	 * @param controller
	 *            The game controller.
	 */
	public MapCommand (final GameModel model, final GameController controller) {
		super(model, controller);
		this.displayController = controller.getDisplayController();
		this.props = ResourceManager.getInstance();
	}

	@Override
	public String[] getRecognizedCommands () {
		String[] cmds = {"map"};
		return cmds;
	}

	@Override
	public void execute (final Command cmd) {
		// If no argument specified
		if (cmd.getArgumentCount() == 0) {
			this.logger.info("Showing the map");
			this.controller.showMap();
		}
		// One argument : map [show | hide]
		else if (cmd.getArgumentCount() == 1) {
			String arg = cmd.getArgument(0);
			switch (arg) {
				case "show":
					this.controller.showMap();
					break;
				case "hide":
					this.controller.hideMap();
					break;
				default:
					this.displayController.printError(this.props.getString("command.error.unknownCommand"));
			}
		} else {
			this.displayController.printError(this.props.getString("command.error.invalidNbArgs"));
		}
	}
}