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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.jls.sod.ApplicationController;
import org.jls.sod.ApplicationModel;
import org.jls.sod.core.GameController;
import org.jls.sod.core.GameModel;
import org.junit.Before;
import org.junit.Test;

public class AttackCommandTest {

    private CommandController cmdController;
    private AttackCommand attackCmd;

    @Before
    public void setUp () throws IOException {
        createCommandController();
        attackCmd = new AttackCommand(cmdController);
    }

    private void createCommandController () {
        try {
            ApplicationModel appModel = new ApplicationModel();
            GameModel gameModel = new GameModel();
            ApplicationController appController = new ApplicationController(appModel);
            cmdController = new CommandController(gameModel, new GameController(gameModel, appController));
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertNotNull(cmdController);
    }

    @Test
    public void listOfRecognizedCommandsIsNotNull () {
        assertNotNull(attackCmd.getRecognizedCommands());
    }

    @Test
    public void listOfRecognizedCommandsContainsOnlyAttackCommand () {
        String[] recognizedCmds = attackCmd.getRecognizedCommands();
        assertEquals(1, recognizedCmds.length);
        assertEquals("attack", recognizedCmds[0]);
    }

    @Test
    public void getSmallIdReturnsAttackCommand () {
        String smallId = attackCmd.getSmallId();
        assertEquals("attack", smallId);
    }
}
