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

package org.jls.sod;

import java.util.LinkedList;

import org.jls.sod.util.ResourceManager;
import org.jls.toolbox.gui.AbstractModel;

public class ApplicationModel extends AbstractModel {

    private static final int HISTORY_MAX_SIZE = 100;

    private final String appName;
    private final String appVersion;
    private final LinkedList<String> commandHistory;
    private int currentCommandHistoryIndex;

    public ApplicationModel() {
        ResourceManager props = ResourceManager.getInstance();
        this.appName = props.getString("name");
        this.appVersion = props.getString("version");
        this.commandHistory = new LinkedList<>();
        this.currentCommandHistoryIndex = 0;
    }

    public String getAppName() {
        return this.appName;
    }

    public String getAppVersion() {
        return this.appVersion;
    }

    public LinkedList<String> getCommandHistory() {
        return this.commandHistory;
    }

    public String getCommandHistoryAt(final int historyIndex) {
        if (historyIndex >= 0 && historyIndex < this.commandHistory.size()) {
            return this.commandHistory.get(historyIndex);
        }
        return "";
    }

    public void pushNewCommandToHistory(final String command) {
        this.commandHistory.addLast(command);
        if (this.commandHistory.size() > HISTORY_MAX_SIZE) {
            this.commandHistory.poll();
        }
        setCurrentCommandHistoryIndex(this.commandHistory.size());
    }

    public int getCurrentCommandHistoryIndex() {
        return this.currentCommandHistoryIndex;
    }

    protected void setCurrentCommandHistoryIndex(final int historyIndex) {
        this.currentCommandHistoryIndex = historyIndex;
    }

    public void incrementCommandHistoryIndex() {
        this.currentCommandHistoryIndex++;
        if (this.currentCommandHistoryIndex > this.commandHistory.size()) {
            this.currentCommandHistoryIndex = this.commandHistory.size();
        }
    }

    public void decrementCommandHistoryIndex() {
        this.currentCommandHistoryIndex--;
        if (this.currentCommandHistoryIndex < 0) {
            this.currentCommandHistoryIndex = 0;
        }
    }
}
