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

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.*;

import java.util.HashMap;

public class CommandParser {

    private final ArgumentParser parser;
    private final HashMap<String, String> helpMessages;

    public CommandParser() {
        helpMessages = new HashMap<>();
        parser = createParser();
    }

    public Namespace parseCommand(final String userInput) throws ArgumentParserException {
        try {
            return parser.parseArgs(userInput.split(" "));
        } catch (ArgumentParserException e) {
            parser.handleError(e);
            throw e;
        }
    }

    public String getHelp() {
        return helpMessages.get("command");
    }

    public String getHelp(final String command) {
        return helpMessages.get(command);
    }

    private ArgumentParser createParser() {
        ArgumentParser parser = ArgumentParsers.newFor("command").build().description("To " +
                "interact with the game, you need to use commands. Simply type the command you " +
                "want (full command list provided bellow) and the expected argument(s), if " +
                "further argument(s) are needed. You can also get help for specific command by " +
                "typing: 'help <command>'.");
        parser.usage("<command> [argument(s)...]");
        Subparsers subparsers = parser.addSubparsers().help("This is the full list of the game " +
                "commands.");
        createSubParsers(subparsers);
        helpMessages.put("command", parser.formatHelp());
        return parser;
    }

    private void createSubParsers(Subparsers subparsers) {
        createHelpParser(subparsers);
        createNewGameParser(subparsers);
        createLoadGameParser(subparsers);
        createExitParser(subparsers);

        createMoveParser(subparsers);
        createLookParser(subparsers);
        createInspectParser(subparsers);
        createFeelParser(subparsers);
        createTouchParser(subparsers);
        createSmellParser(subparsers);
        createTasteParser(subparsers);
        createListenParser(subparsers);

        createInventoryParser(subparsers);
        createMapParser(subparsers);
        createTakeParser(subparsers);
        createDropParser(subparsers);
    }

    private void createHelpParser(Subparsers subparsers) {
        Subparser parser = subparsers.addParser("help");
        parser.usage("help [command]");
        parser.help("Print help about specific commands (for example: 'help help')");
        parser.description("Use this command to get a more detailed help about specific game " +
                "commands, " +
                "for example: 'help look'.");
        parser.addArgument("command").help("The command for which you want to get a more detailed" +
                " help message.").nargs("?");
        helpMessages.put("help", parser.formatHelp());
    }

    private void createNewGameParser(Subparsers subparsers) {
        Subparser parser = subparsers.addParser("new");
        parser.usage("new");
        parser.help("Open the new game creation panel.");
        parser.description("Use this command to open the new game creation panel.");
        helpMessages.put("new", parser.formatHelp());
    }

    private void createLoadGameParser(Subparsers subparsers) {
        Subparser parser = subparsers.addParser("load");
        parser.usage("load [instanceName]");
        parser.help("Load the specified game instance name.");
        parser.description("Use this command to either open the load game panel, or to load a " +
                "specific instance name.");
        parser.addArgument("instanceName").help("Game instance name to be loaded.").nargs("?");
        helpMessages.put("load", parser.formatHelp());
    }

    private void createExitParser(Subparsers subparsers) {
        Subparser parser = subparsers.addParser("exit");
        parser.usage("exit");
        parser.help("Exit the game.");
        parser.description("Use this command to exit the game.");
        helpMessages.put("exit", parser.formatHelp());
    }

    private void createMoveParser(Subparsers subparsers) {
        Subparser parser = subparsers.addParser("move");
        parser.usage("move [direction]");
        parser.help("Move into the specified direction.");
        parser.description("Use this command to move the character in the specified direction.");

        Argument directionArg = parser.addArgument("direction");
        directionArg.nargs("?");
        directionArg.help("The direction in which you wish to go.");
        directionArg.choices("north", "n", "north_east", "ne", "east", "e", "south_east", "se",
                "south", "s", "south_west", "sw", "west", "w", "north_west", "nw", "center");

        helpMessages.put("move", parser.formatHelp());
    }

    private void createLookParser(Subparsers subparsers) {
        Subparser parser = subparsers.addParser("look");
        parser.usage("look [direction or item]");
        parser.help("Look to the specified direction or at an object.");
        parser.description("Use this command to look to the specified direction or to get a " +
                "visual description of an item.");

        parser.addArgument("target").nargs("?").help("The direction or item you wish to look " +
                "to/at.");

        helpMessages.put("look", parser.formatHelp());
    }

    private void createInspectParser(Subparsers subparsers) {
        Subparser parser = subparsers.addParser("inspect");
        parser.usage("inspect [direction or item]");
        parser.help("Inspect the specified direction or an object.");
        parser.description("Use this command to inspect the specified direction or an item.");

        parser.addArgument("target").nargs("?").help("The direction or item you wish to inspect.");

        helpMessages.put("inspect", parser.formatHelp());
    }

    private void createFeelParser(Subparsers subparsers) {
        Subparser parser = subparsers.addParser("feel");
        parser.usage("feel [direction or item]");
        parser.help("Use your instinct in the specified direction or on an object.");
        parser.description("Use this command to use your instinct in the specified direction or " +
                "on an object.");

        parser.addArgument("target").nargs("?").help("The direction or item you wish to feel.");

        helpMessages.put("feel", parser.formatHelp());
    }

    private void createTouchParser(Subparsers subparsers) {
        Subparser parser = subparsers.addParser("touch");
        parser.usage("touch [direction or item]");
        parser.help("Touch an object or raise your arms in the specified direction.");
        parser.description("Use this command to touch an object or raise your arms in the " +
                "specified direction.");

        parser.addArgument("target").nargs("?").help("The direction or item you wish to touch.");

        helpMessages.put("touch", parser.formatHelp());
    }

    private void createSmellParser(Subparsers subparsers) {
        Subparser parser = subparsers.addParser("smell");
        parser.usage("smell [direction or item]");
        parser.help("Smell in the specified direction or an object.");
        parser.description("Use this command to smell the specified direction or an item.");

        parser.addArgument("target").nargs("?").help("The direction or item from which you wish " +
                "to smell.");

        helpMessages.put("smell", parser.formatHelp());
    }

    private void createTasteParser(Subparsers subparsers) {
        Subparser parser = subparsers.addParser("taste");
        parser.usage("taste [direction or item]");
        parser.help("Taste an item or stick out your tongue in the specified direction.");
        parser.description("Use this command to taste an item or stick out your tongue in the " +
                "specified direction (no one will judge you...)");

        parser.addArgument("target").nargs("?").help("The direction or item you wish to taste.");

        helpMessages.put("taste", parser.formatHelp());
    }

    private void createListenParser(Subparsers subparsers) {
        Subparser parser = subparsers.addParser("listen");
        parser.usage("listen [direction or item]");
        parser.help("Listen from the specified direction or an object.");
        parser.description("Use this command to listen from the specified direction or an item.");

        parser.addArgument("target").nargs("?").help("The direction or item you wish to listen " +
                "from.");

        helpMessages.put("listen", parser.formatHelp());
    }

    private void createInventoryParser(Subparsers subparsers) {
        Subparser parser = subparsers.addParser("inventory");
        parser.usage("inventory");
        parser.help("Show your inventory.");
        parser.description("Use this command to show the content of your inventory.");
        helpMessages.put("inventory", parser.formatHelp());
    }

    private void createMapParser(Subparsers subparsers) {
        Subparser parser = subparsers.addParser("map");
        parser.usage("map");
        parser.help("Open the map of the current region.");
        parser.description("Use this command to open the map of the current region.");
        helpMessages.put("map", parser.formatHelp());
    }

    private void createTakeParser(Subparsers subparsers) {
        Subparser parser = subparsers.addParser("take");
        parser.help("Take item(s) from the current room.");
        parser.description("Use this command to take item(s) from the current room.");

        parser.addArgument("item").nargs("?").help("The item to take.");
        parser.addArgument("quantity").nargs("?").help("The quantity of items to take (default " +
                "is: 1).").type(Integer.class).setDefault(1);

        helpMessages.put("take", parser.formatHelp());
    }

    private void createDropParser(Subparsers subparsers) {
        Subparser parser = subparsers.addParser("drop");
        parser.help("Drop item(s) from your inventory.");
        parser.description(
                "Use this command to drop item(s) from your inventory. Item(s) will be dropped on" +
                        " the floor of the current room.");

        parser.addArgument("item").nargs("?").help("The item to drop.");
        parser.addArgument("quantity").nargs("?").help("The quantity of items to drop (default " +
                "is: 1).").type(Integer.class).setDefault(1);

        helpMessages.put("drop", parser.formatHelp());
    }
}
