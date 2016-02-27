/*#
 * The MIT License (MIT)
 * 
 * Copyright (c) 2016 LE SAUCE Julien
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
 #*/

package org.jls.sod.core.command;

import java.util.Arrays;
import java.util.HashSet;
import java.util.StringTokenizer;

/**
 * Context of the executing command.
 * 
 * @author LE SAUCE Julien
 * @date Sep 2, 2015
 */
public class Command {

	private static final String[] ignoredWords = {"at", "the", "a", "an"};

	private final String commandId;
	private final String[] arguments;

	/**
	 * Instanciates a new {@link Command} specifying the command ID and its
	 * arguments.
	 * 
	 * @param cmd
	 *            Command ID.
	 * @param args
	 *            Arguments of the command.
	 */
	public Command (final String cmd, final String[] args) {
		this.commandId = cmd;
		this.arguments = args;
	}

	/**
	 * Instanciates a new {@link Command} from the complete command input.
	 * 
	 * @param cmd
	 *            Complete command input formatted as <i>[commandID]
	 *            {...args}</i>.
	 */
	public Command (final String cmd) {
		if (cmd == null) {
			throw new NullPointerException("Command cannot be null");
		}
		if (cmd.isEmpty()) {
			throw new IllegalArgumentException("Command is empty");
		}
		StringTokenizer tokenizer = new StringTokenizer(cmd);
		int nbTokens = tokenizer.countTokens();
		if (nbTokens == 1) {
			this.commandId = tokenizer.nextToken();
			this.arguments = null;
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

	/**
	 * Cleanse the command input, meaning that all ignored words are removed
	 * from the specified command.
	 * 
	 * @param cmd
	 *            Command to cleanse.
	 * @return Command cleansed from ignored words.
	 */
	public static String cleanse (final String cmd) {
		// Returns if command is empty
		if (cmd == null || cmd.isEmpty()) {
			return cmd;
		}
		// List of ignored words
		HashSet<String> ignored = new HashSet<>(Arrays.asList(ignoredWords));

		// Cleanses the command
		StringTokenizer tokenizer = new StringTokenizer(cmd);
		StringBuilder builder = new StringBuilder();
		while (tokenizer.hasMoreTokens()) { // For each token
			String token = tokenizer.nextToken();
			// If token is not ignored
			if (!ignored.contains(token)) {
				builder.append(token);
			}
			// Appends a space to seperate tokens
			if (tokenizer.hasMoreTokens()) {
				builder.append(" ");
			}
		}
		return builder.toString();
	}

	/**
	 * Returns the command identifier.
	 * 
	 * @return Command identifier.
	 */
	public String getCommandId () {
		return this.commandId;
	}

	/**
	 * Returns the arguments of the command.
	 * 
	 * @return Arguments of the command.
	 */
	public String[] getArguments () {
		return this.arguments;
	}

	/**
	 * Returns the argument at the specified index. If index is out of bounds,
	 * then an {@link IllegalArgumentException} is thrown.
	 * 
	 * @param index
	 *            Index of the argument {0..n}.
	 * @return The argument at the specified index.
	 */
	public String getArgument (final int index) {
		if (index >= 0 && index < getArgumentCount()) {
			return this.arguments[index];
		} else {
			throw new IllegalArgumentException("Index out of bounds : " + index + " (size=" + getArgumentCount() + ")");
		}
	}

	/**
	 * Returns the number of arguments of this command.
	 * 
	 * @return Number of arguments of this command.
	 */
	public int getArgumentCount () {
		return this.arguments != null ? this.arguments.length : 0;
	}
}