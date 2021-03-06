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

import org.jdom2.JDOMException;

import java.io.IOException;

public class Load extends BasicCommand {

    public Load(final CommandController commandController) {
        super(commandController);
    }

    @Override
    public String apply(final Command command) {
        String instanceName = command.getNamespace().getString("instanceName");

        if (instanceName == null || instanceName.isEmpty()) {
            controller.showLoadGamePanel();
        } else {
            try {
                controller.loadGame(instanceName);
            } catch (JDOMException | IOException e) {
                displayController.printError("Cannot load game instance name: " + instanceName);
                displayController.printError(e.getMessage());
                logger.error(e);
            }
        }
        return null;
    }
}
