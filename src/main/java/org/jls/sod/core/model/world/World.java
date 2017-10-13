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

package org.jls.sod.core.model.world;

import java.util.HashMap;

/**
 * Data model of a world.
 * 
 * @author LE SAUCE Julien
 * @date Sep 3, 2015
 */
public class World {

    private final String id;
    private final String name;
    private final String description;
    private final HashMap<String, Region> regions;

    private Region entryPoint;

    /**
     * Instantiates a new world.
     * 
     * @param id
     *            The world identifier.
     * @param name
     *            The world name.
     * @param desc
     *            Description of this world.
     */
    public World(final String id, final String name, final String desc) {
        super();
        this.id = id;
        this.name = name;
        this.description = desc;
        this.regions = new HashMap<>();
        this.entryPoint = null;
    }

    /**
     * Returns the world identifier.
     * 
     * @return World idientifier.
     */
    public String getId () {
        return this.id;
    }

    /**
     * Returns the world name.
     * 
     * @return World name.
     */
    public String getName () {
        return this.name;
    }

    /**
     * Returns the description of this world.
     * 
     * @return Description of this world.
     */
    public String getDescription () {
        return this.description;
    }

    /**
     * Returns the regions contained in this world.
     * 
     * @return Regions contained in this world.
     */
    public HashMap<String, Region> getRegions () {
        return this.regions;
    }

    /**
     * Returns the default region where the player should start.
     * 
     * @return The default region where the player should start.
     */
    public Region getEntryPoint () {
        return this.entryPoint;
    }

    /**
     * Specifies the default region where the player should start.
     * 
     * @param entry
     *            The default region where the player should start.
     */
    public void setEntryPoint (final Region entry) {
        this.entryPoint = entry;
    }
}
