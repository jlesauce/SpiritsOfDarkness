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

import java.util.ArrayList;

import org.jls.sod.core.GameController;
import org.jls.sod.core.GameModel;

public class CommandController {

    private final GameModel gameModel;
    private final GameController gameController;
    private final ArrayList<AbstractCommandExecutor> commandExecutorList;

    public CommandController(final GameModel gameModel, final GameController gameController) {
        this.gameModel = gameModel;
        this.gameController = gameController;
        commandExecutorList = new ArrayList<>();
        initCommandExecutorsList();
    }

    private void initCommandExecutorsList () {
        commandExecutorList.add(new Help(this));
        commandExecutorList.add(new GeneralCommand(this));
        commandExecutorList.add(new NavigateCommand(this));
        commandExecutorList.add(new LookCommand(this));
        commandExecutorList.add(new TakeCommand(this));
        commandExecutorList.add(new DropCommand(this));
        commandExecutorList.add(new InventoryCommand(this));
        commandExecutorList.add(new MapCommand(this));
    }

    public ArrayList<AbstractCommandExecutor> getCommandExecutorList () {
        return commandExecutorList;
    }

    public GameModel getGameModel () {
        return gameModel;
    }

    public GameController getGameController () {
        return gameController;
    }
}
