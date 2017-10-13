/*
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
 */

package org.jls.sod.core.model;

/**
 * Enumerates the different senses the player can use to explore its
 * environment.
 *
 * @author LE SAUCE Julien
 * @date Nov 25, 2015
 */
public enum Sense {

    INSPECT("inspect"), FEEL("feel"), TOUCH("touch"), SMELL("smell"), TASTE("taste"), LISTEN("listen");

    private final String[] matchingLabels;

    /**
     * Instantiates a new direction and specifies the matching sense's labels.
     *
     * @param lbls
     *            The matching direction's labels.
     */
    private Sense(final String... lbls) {
        this.matchingLabels = lbls;
    }

    /**
     * Parses the specified sense.
     *
     * @param value
     *            Sense to parse.
     * @return Corresponding {@link Sense} enum if an existing sense is found, else
     *         throw an {@link IllegalArgumentException}.
     */
    public static Sense parseValue (final String value) {
        // If value matches the enum name
        try {
            return Sense.valueOf(value.toUpperCase());
        } catch (Exception e) {
            // Silent exception
        }
        // Else try to find a matching label
        for (Sense d : Sense.values()) {
            for (String lbl : d.getMatchingLabels()) {
                // If value matches an existing label
                if (lbl.equals(value)) {
                    return d;
                }
            }
        }
        throw new IllegalArgumentException("No matching sense for the value : " + value);
    }

    /**
     * Returns <code>true</code> if the specified argument matches a sense.
     *
     * @param value
     *            The value to test.
     * @return <code>true</code> if the specified argument matches a sense value,
     *         <code>false</code> otherwise.
     */
    public static boolean matchSense (final String value) {
        for (Sense d : Sense.values()) {
            for (String s : d.getMatchingLabels()) {
                if (s.equals(value)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns the matching labels for this sense.
     *
     * @return The matching labels for this sense.
     */
    public String[] getMatchingLabels () {
        return this.matchingLabels;
    }
}
