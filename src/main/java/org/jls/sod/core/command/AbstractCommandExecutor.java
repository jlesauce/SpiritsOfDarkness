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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jls.sod.core.GameController;
import org.jls.sod.core.GameModel;

public abstract class AbstractCommandExecutor {

    protected final CommandController commandController;
    protected final GameModel model;
    protected final GameController controller;
    protected final Logger logger;

    public AbstractCommandExecutor(final CommandController commandController) {
        this.commandController = commandController;
        this.model = commandController.getGameModel();
        this.controller = commandController.getGameController();
        this.logger = LogManager.getLogger();

    }

    public abstract String[] getRecognizedCommands ();

    public abstract void execute (final Command cmd);

    public abstract String getSmallId ();

    public boolean isCommandRecognized (final String cmdId) {
        for (String s : getRecognizedCommands()) {
            if (s.equals(cmdId)) {
                return true;
            }
        }
        return false;
    }
}
