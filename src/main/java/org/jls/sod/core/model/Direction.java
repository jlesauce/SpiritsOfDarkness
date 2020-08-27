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

package org.jls.sod.core.model;

public enum Direction {

    NORTH("north", "n"), NORTH_EAST("northeast", "north-east", "north_east", "ne"), EAST("east",
            "e"),
    SOUTH_EAST("southeast", "south-east", "south_east", "se"), SOUTH("south", "s"),
    SOUTH_WEST("southwest", "south-west", "south_west", "sw"), WEST("west", "w"),
    NORTH_WEST("northwest", "north-west", "north_west", "nw"), CENTER("center");

    private final String[] matchingLabels;

    Direction(final String... labels) {
        this.matchingLabels = labels;
    }

    public static Direction parseValue(final String directionAsString) {
        // If value matches the enum name
        try {
            return Direction.valueOf(directionAsString);
        } catch (Exception e) {
            // Silent exception
        }
        // Try to find a matching label
        for (Direction direction : Direction.values()) {
            for (String label : direction.getMatchingLabels()) {
                if (label.equals(directionAsString)) {
                    return direction;
                }
            }
        }
        throw new IllegalArgumentException("No matching direction for: " + directionAsString);
    }

    public static boolean matchDirection(final String directionAsString) {
        for (Direction d : Direction.values()) {
            for (String s : d.getMatchingLabels()) {
                if (s.equals(directionAsString)) {
                    return true;
                }
            }
        }
        return false;
    }

    public String[] getMatchingLabels() {
        return this.matchingLabels;
    }
}
