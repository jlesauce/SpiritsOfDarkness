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

import java.util.Random;

/**
 * Represents a die that can be rolled.
 * 
 * @author LE SAUCE Julien
 * @date Nov 30, 2015
 */
public class Die {

    private static final Random random = new Random();

    /**
     * Rolls a 4-sided die (D4).
     * 
     * @return Value in the range 1-4.
     */
    public static int d4 () {
        return d(4);
    }

    /**
     * Rolls a 6-sided die (D6).
     * 
     * @return Value in the range 1-6.
     */
    public static int d6 () {
        return d(6);
    }

    /**
     * Rolls a 8-sided die (D8).
     * 
     * @return Value in the range 1-8.
     */
    public static int d8 () {
        return d(8);
    }

    /**
     * Rolls a 10-sided die (D10).
     * 
     * @return Value in the range 1-10.
     */
    public static int d10 () {
        return d(10);
    }

    /**
     * Rolls a 12-sided die (D12).
     * 
     * @return Value in the range 1-12.
     */
    public static int d12 () {
        return d(12);
    }

    /**
     * Rolls a 20-sided die (D20).
     * 
     * @return Value in the range 1-20.
     */
    public static int d20 () {
        return d(20);
    }

    /**
     * Rolls a die in the range 1 to the specified value.
     * 
     * @param value
     *            The maximum value of the die.
     * @return Value in the range 1 to specified value.
     */
    private static int d (final int value) {
        return random.nextInt(value) + 1;
    }
}
