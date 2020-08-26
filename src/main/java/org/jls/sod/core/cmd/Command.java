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

import java.util.StringTokenizer;

public class Command {

    private final String commandId;
    private final String[] arguments;

    public Command(final String cmdStr) {
        if (cmdStr == null) {
            throw new NullPointerException("Command can't be null");
        }
        if (cmdStr.isEmpty()) {
            throw new IllegalArgumentException("Command can't be empty");
        }

        StringTokenizer tokenizer = new StringTokenizer(cmdStr);
        int nbTokens = tokenizer.countTokens();
        if (nbTokens == 1) {
            this.commandId = tokenizer.nextToken();
            this.arguments = new String[0];
        } else {
            this.commandId = tokenizer.nextToken();
            this.arguments = new String[nbTokens - 1];
            int i = 0;
            while (tokenizer.hasMoreTokens()) {
                this.arguments[i] = tokenizer.nextToken();
                i++;
            }
        }
    }

    public String getCommandId () {
        return this.commandId;
    }

    public String[] getArguments () {
        return this.arguments;
    }

    public String getArgument (final int index) {
        if (index >= 0 && index < getArgumentCount()) {
            return this.arguments[index];
        } else {
            throw new IllegalArgumentException("Index out of bounds : " + index + " (size=" + getArgumentCount() + ")");
        }
    }

    public int getArgumentCount () {
        return this.arguments != null ? this.arguments.length : 0;
    }

    public boolean hasArguments () {
        return getArgumentCount() > 0;
    }

    @Override
    public String toString () {
        StringBuilder builder = new StringBuilder();
        builder.append(this.commandId + " ");
        for (String str : this.arguments) {
            builder.append(str + " ");
        }
        return builder.toString().trim();
    }
}
