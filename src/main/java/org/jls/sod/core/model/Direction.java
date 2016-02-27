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

package org.jls.sod.core.model;

/**
 * Enumerates the possible directions when the player is navigating.
 * 
 * @author LE SAUCE Julien
 * @date Sep 2, 2015
 */
public enum Direction {

	NORTH("north", "n"),
	NORTH_EAST("northeast", "north-east", "north_east", "ne"),
	EAST("east", "e"),
	SOUTH_EAST("southeast", "south-east", "south_east", "se"),
	SOUTH("south", "s"),
	SOUTH_WEST("southwest", "south-west", "south_west", "sw"),
	WEST("west", "w"),
	NORTH_WEST("northwest", "north-west", "north_west", "nw");

	private final String[] matchingLabels;

	/**
	 * Instanciates a new direction and specifies the matching direction's
	 * labels.
	 * 
	 * @param lbls
	 *            The matching direction's labels.
	 */
	private Direction (final String... lbls) {
		this.matchingLabels = lbls;
	}

	/**
	 * Parses the specified direction.
	 * 
	 * @param value
	 *            Direction to parse.
	 * @return Corresponding {@link Direction} enum if an existing direction is
	 *         found, else throw an {@link IllegalArgumentException}.
	 */
	public static Direction parseValue (final String value) {
		// If value matches the enum name
		try {
			return Direction.valueOf(value);
		} catch (@SuppressWarnings("unused") Exception e) {
			// Silent exception
		}
		// Else try to find a matching label
		for (Direction d : Direction.values()) {
			for (String lbl : d.getMatchingLabels()) {
				// If value matches an existing label
				if (lbl.equals(value)) {
					return d;
				}
			}
		}
		throw new IllegalArgumentException("No matching direction for the value : " + value);
	}

	/**
	 * Returns <code>true</code> if the specified argument matches a direction.
	 * 
	 * @param value
	 *            The value to test.
	 * @return <code>true</code> if the specified argument matches a direction
	 *         value, <code>false</code> otherwise.
	 */
	public static boolean matchDirection (final String value) {
		for (Direction d : Direction.values()) {
			for (String s : d.getMatchingLabels()) {
				if (s.equals(value)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Returns the matching labels for this direction.
	 * 
	 * @return The matching labels for this direction.
	 */
	public String[] getMatchingLabels () {
		return this.matchingLabels;
	}
}