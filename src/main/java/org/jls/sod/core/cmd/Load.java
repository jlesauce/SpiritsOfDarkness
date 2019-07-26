/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2019 LE SAUCE Julien
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
package org.jls.sod.core.cmd;

import java.io.IOException;
import org.jdom2.JDOMException;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(name = "load", description = "Load a saved game")
public class Load extends BasicCommand {

    @Parameters(paramLabel = "instanceName", arity = "0..1", description = "The name of the game instance")
    private String instanceName;

    public Load(CommandController commandController) {
        super(commandController);
    }

    @Override
    public String apply(ParsedCommand command) {
        if (command.getContext().isUsageHelpRequested()) {
            printHelp(command);
            return null;
        }

        if (instanceName == null) {
            this.controller.showLoadGamePanel();
        }
        else {
            try {
                controller.loadGame(instanceName);
            } catch (JDOMException | IOException e) {
                displayController.printError("Cannot load the game instance.");
                displayController.printError(e.getMessage());
                this.logger.error(e);
            }
        }
        return null;
    }
}
