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

package org.jls.sod.core.model.character;

import java.util.Arrays;

import org.jls.sod.core.model.Die;

public class CharacterBuilder {

    // private String name;
    // private Race race;
    // private Class clazz;

    /*
     * Base stats
     */
    // Abilities
    private int strength;
    private int dexterity;
    private int constitution;
    private int intellect;
    private int wisdow;
    private int charisma;

    // private int speed;

    /*
     * Stats
     */
    // private int level;
    // private int xp;

    /*
     * Modifiers
     */

    /*
     * Current state
     */

    private static final int abilityRoll () {
        int nbDraws = 4;
        int[] draws = new int[nbDraws];
        // Roll 4xd6
        for (int i = 0; i < nbDraws; i++) {
            draws[i] = Die.d6();
        }
        // Sort the results
        Arrays.sort(draws);
        // Take the 3 highest
        int[] bestDraws = new int[3];
        System.arraycopy(draws, 0, bestDraws, 0, 3);
        // Returns the cumulative total
        int sum = 0;
        for (int n : bestDraws) {
            sum += n;
        }
        return sum;
    }

    /**
     * Generates a random set of abilities.
     */
    public void randomizeAbilites () {
        this.strength = abilityRoll();
        this.dexterity = abilityRoll();
        this.constitution = abilityRoll();
        this.intellect = abilityRoll();
        this.wisdow = abilityRoll();
        this.charisma = abilityRoll();
    }

    /**
     * Generates a standard set of abilities.
     */
    public void standardAbilities () {
        this.strength = 15;
        this.dexterity = 14;
        this.constitution = 13;
        this.intellect = 12;
        this.wisdow = 10;
        this.charisma = 8;
    }

    @Override
    public String toString () {
        return "CharacterBuilder [strength=" + strength + ", dexterity=" + dexterity + ", constitution=" + constitution
                + ", intellect=" + intellect + ", wisdow=" + wisdow + ", charisma=" + charisma + "]";
    }
}