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

import java.util.HashMap;
import org.jls.sod.core.GameController;
import org.jls.sod.core.GameModel;
import org.jls.sod.core.model.Direction;

public class CommandController {

    private final GameModel gameModel;
    private final GameController gameController;
    private final HashMap<String, AbstractCommandCaller> commandExecutorMap;

    public CommandController(final GameModel gameModel, final GameController gameController) {
        this.gameModel = gameModel;
        this.gameController = gameController;
        this.commandExecutorMap = new HashMap<>();
        initCommandExecutorsList();
    }

    private void initCommandExecutorsList() {
        this.commandExecutorMap.put("help", new Help(this));
        this.commandExecutorMap.put("new", new New(this));
        this.commandExecutorMap.put("load", new Load(this));
        this.commandExecutorMap.put("exit", new Exit(this));

        this.commandExecutorMap.put("move", new Move(this));
        this.commandExecutorMap.put("n", new Move(this, Direction.NORTH));
        this.commandExecutorMap.put("nw", new Move(this, Direction.NORTH_WEST));
        this.commandExecutorMap.put("w", new Move(this, Direction.WEST));
        this.commandExecutorMap.put("sw", new Move(this, Direction.SOUTH_WEST));
        this.commandExecutorMap.put("s", new Move(this, Direction.SOUTH));
        this.commandExecutorMap.put("se", new Move(this, Direction.SOUTH_EAST));
        this.commandExecutorMap.put("e", new Move(this, Direction.EAST));
        this.commandExecutorMap.put("ne", new Move(this, Direction.NORTH_EAST));

        this.commandExecutorMap.put("look", new Look(this));
        this.commandExecutorMap.put("inspect", new Inspect(this));
        this.commandExecutorMap.put("feel", new Feel(this));
        this.commandExecutorMap.put("touch", new Touch(this));
        this.commandExecutorMap.put("smell", new Smell(this));
        this.commandExecutorMap.put("taste", new Taste(this));
        this.commandExecutorMap.put("listen", new Listen(this));

        this.commandExecutorMap.put("inventory", new Inventory(this));
        this.commandExecutorMap.put("map", new Map(this));
        this.commandExecutorMap.put("take", new Take(this));
        this.commandExecutorMap.put("drop", new Drop(this));
    }

    public HashMap<String, AbstractCommandCaller> getCommandExecutorList() {
        return commandExecutorMap;
    }

    public boolean isCommandIdContainedInCommandList(final String commandId) {
        return commandExecutorMap.containsKey(commandId);
    }

    public GameModel getGameModel() {
        return gameModel;
    }

    public GameController getGameController() {
        return gameController;
    }
}
