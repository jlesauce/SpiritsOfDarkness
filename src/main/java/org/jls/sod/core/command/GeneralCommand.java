/*
 * The MIT License (MIT)
 * 
 * Copyright (c) 2017 LE SAUCE Julien
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
import org.jls.sod.util.ResourceManager;

/**
 * Provides a set of standard commands that the player can use.
 * 
 * @author AwaX
 * @date 27 nov. 2015
 */
public class GeneralCommand extends AbstractCommandExecutor {

    private final DisplayController displayController;
    private final ResourceManager props;

    /**
     * Instantiates a new command.
     * 
     * @param model
     *            The game data model.
     * @param controller
     *            The game controller.
     */
    public GeneralCommand(final GameModel model, final GameController controller) {
        super(model, controller);
        this.displayController = controller.getDisplayController();
        this.props = ResourceManager.getInstance();
    }

    @Override
    public String[] getRecognizedCommands () {
        String[] args = { "load", "new", "exit" };
        return args;
    }

    @Override
    public void execute (Command cmd) {
        // No argument
        if (cmd.getArgumentCount() == 0) {
            switch (cmd.getCommandId()) {
                case "new":
                    this.controller.showNewGamePanel();
                    break;
                case "load":
                    this.controller.showLoadGamePanel();
                    break;
                case "exit":
                    this.controller.exitApplication();
                    break;
                default:
                    this.displayController.printError(this.props.getString("command.error.unknownCommand"));
            }
        } else {
            this.displayController.printError(this.props.getString("command.error.unknownCommand"));
        }
    }
}
