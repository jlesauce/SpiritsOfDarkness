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

package org.jls.sod.core.model.world;

import java.util.HashMap;

/**
 * Data model of a region.
 * 
 * @author LE SAUCE Julien
 * @date Sep 3, 2015
 */
public class Region {

    private final String id;
    private final String name;
    private final String description;
    private final HashMap<String, Zone> zones;
    private final World world;

    private Zone entryPoint;

    /**
     * Instantiates a new region.
     * 
     * @param id
     *            The region identifier.
     * @param name
     *            The region name.
     * @param desc
     *            The region description.
     * @param world
     *            Specifies the world which contains this region.
     */
    public Region(final String id, final String name, final String desc, final World world) {
        this.id = id;
        this.name = name;
        this.description = desc;
        this.zones = new HashMap<>();
        this.world = world;
        this.entryPoint = null;
    }

    /**
     * Returns the region identifier.
     * 
     * @return Region idientifier.
     */
    public String getId () {
        return this.id;
    }

    /**
     * Returns the region name.
     * 
     * @return Region name.
     */
    public String getName () {
        return this.name;
    }

    /**
     * Returns the description of this region.
     * 
     * @return Description of this region.
     */
    public String getDescription () {
        return this.description;
    }

    /**
     * Returns the zones contained in this region.
     * 
     * @return Zones contained in this region.
     */
    public HashMap<String, Zone> getZones () {
        return this.zones;
    }

    /**
     * Returns the world where this region is located.
     * 
     * @return The world where this region is located.
     */
    public World getWorld () {
        return this.world;
    }

    /**
     * Returns the default zone where the player should start.
     * 
     * @return The default zone where the player should start.
     */
    public Zone getEntryPoint () {
        return this.entryPoint;
    }

    /**
     * Specifies the default zone where the player should start.
     * 
     * @param entry
     *            The default zone where the player should start.
     */
    public void setEntryPoint (final Zone entry) {
        this.entryPoint = entry;
    }
}
