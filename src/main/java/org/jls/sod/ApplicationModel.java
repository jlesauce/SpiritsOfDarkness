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

package org.jls.sod;

import java.util.ArrayList;
import java.util.LinkedList;

import org.jls.sod.core.command.AbstractCommandExecutor;
import org.jls.sod.util.ResourceManager;
import org.jls.toolbox.gui.AbstractModel;

/**
 * Data model of the application.
 * 
 * @author LE SAUCE Julien
 * @date Sep 2, 2015
 */
public class ApplicationModel extends AbstractModel {

    private static final int HISTORY_MAX_SIZE = 100;

    private final String appName;
    private final String appVersion;

    private final ArrayList<AbstractCommandExecutor> commandExecutors;
    private final LinkedList<String> history;

    private int currentHistoryPosition;

    /**
     * Instantiates a default data model.
     */
    public ApplicationModel() {
        ResourceManager props = ResourceManager.getInstance();
        this.appName = props.getString("name");
        this.appVersion = props.getString("version");
        this.commandExecutors = new ArrayList<>();
        this.history = new LinkedList<>();
        this.currentHistoryPosition = 0;
    }

    /**
     * Returns the application's name.
     * 
     * @return Application's name.
     */
    public String getAppName () {
        return this.appName;
    }

    /**
     * Returns the application's version.
     * 
     * @return Application's version.
     */
    public String getAppVersion () {
        return this.appVersion;
    }

    /**
     * Returns the list of command executors.
     * 
     * @return List of command executors.
     */
    public ArrayList<AbstractCommandExecutor> getCommandExecutors () {
        return this.commandExecutors;
    }

    /**
     * Returns the command history.
     * 
     * @return The command history.
     */
    public LinkedList<String> getHistory () {
        return this.history;
    }

    /**
     * Returns the command history at the specified index. If the index is out of
     * bounds, an empty string is returned.
     * 
     * @param index
     *            The index of the history.
     * @return The command history at the specified index or an empty string if
     *         index is out of bounds.
     */
    public String getHistory (final int index) {
        if (index >= 0 && index < this.history.size()) {
            return this.history.get(index);
        } else {
            return "";
        }
    }

    /**
     * Updates the current history with a new entry.
     * 
     * @param cmd
     *            New command to put in the history.
     */
    public void putHistory (final String cmd) {
        this.history.addLast(cmd);
        if (this.history.size() > HISTORY_MAX_SIZE) {
            this.history.poll();
        }
        setCurrentHistoryPosition(this.history.size());
    }

    /**
     * Returns the current history position.
     * 
     * @return The current history position.
     */
    public int getCurrentHistoryPosition () {
        return this.currentHistoryPosition;
    }

    /**
     * Sets the current history position.
     * 
     * @param position
     *            The current history position.
     */
    protected void setCurrentHistoryPosition (int position) {
        this.currentHistoryPosition = position;
    }

    /**
     * Increments the history position.
     */
    public void incrementHistoryPosition () {
        this.currentHistoryPosition++;
        if (this.currentHistoryPosition > this.history.size()) {
            this.currentHistoryPosition = this.history.size();
        }
    }

    /**
     * Decrements the history position.
     */
    public void decrementHistoryPosition () {
        this.currentHistoryPosition--;
        if (this.currentHistoryPosition < 0) {
            this.currentHistoryPosition = 0;
        }
    }
}
