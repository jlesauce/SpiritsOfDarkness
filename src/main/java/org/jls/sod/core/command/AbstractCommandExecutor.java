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

/**
 * Abstract class for all command executors.
 * 
 * @author LE SAUCE Julien
 * @date Sep 2, 2015
 */
public abstract class AbstractCommandExecutor {

    protected final GameModel model;
    protected final GameController controller;
    protected final Logger logger;

    /**
     * Instantiates a new abstract command.
     * 
     * @param model
     *            The game data model.
     * @param controller
     *            The game controller.
     */
    public AbstractCommandExecutor(final GameModel model, final GameController controller) {
        this.model = model;
        this.controller = controller;
        this.logger = LogManager.getLogger();

    }

    /**
     * Returns the recognized command identifiers for that command.
     * 
     * @return Recognized command identifiers for that command.
     */
    public abstract String[] getRecognizedCommands ();

    /**
     * Executes this command.
     * 
     * @param cmd
     *            Command arguments.
     */
    public abstract void execute (final Command cmd);

    /**
     * Tells wheres this command identifier is recognized or not.
     * 
     * @param cmdId
     *            The command identifier.
     * @return <code>true</code> if the command is recognized, <code>false</code>
     *         otherwise.
     */
    public boolean isCommandRecognized (final String cmdId) {
        for (String s : getRecognizedCommands()) {
            if (s.equals(cmdId)) {
                return true;
            }
        }
        return false;
    }
}
