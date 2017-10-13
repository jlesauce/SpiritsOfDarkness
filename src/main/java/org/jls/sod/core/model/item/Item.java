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

package org.jls.sod.core.model.item;

import java.util.HashMap;
import java.util.Map;

import org.jls.sod.core.model.Sense;

/**
 * Represents a physical item in the game.
 * 
 * @author LE SAUCE Julien
 * @date Nov 26, 2015
 */
public class Item {

    private final String id;
    private final String name;
    private final ItemType type;

    private final String shortDescription;
    private final String longDescrition;
    private final HashMap<Sense, String> sensesDescription;

    private boolean isCarriable;
    private boolean isStackable;

    /**
     * Instantiates a new item.
     * 
     * @param id
     *            Item's unique identifier.
     * @param name
     *            Item's displayable name.
     * @param shortDesc
     *            The short description of this item.
     * @param longDesc
     *            The long description of this item.
     * @param type
     *            Item's type.
     */
    public Item(final String id, final String name, final ItemType type, final String shortDesc,
            final String longDesc) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.shortDescription = shortDesc;
        this.longDescrition = longDesc;
        this.sensesDescription = new HashMap<>();
        this.isCarriable = false;
        this.isStackable = false;
    }

    @Override
    public String toString () {
        return "Item [id=" + id + ", name=" + name + ", type=" + type + ", isCarriable=" + isCarriable
                + ", isStackable=" + isStackable + "]";
    }

    public String getId () {
        return id;
    }

    public String getName () {
        return name;
    }

    public ItemType getType () {
        return type;
    }

    public String getShortDescription () {
        return shortDescription;
    }

    public String getLongDescrition () {
        return longDescrition;
    }

    /**
     * Tells if a description is provided for the specified sense.
     * 
     * @param sense
     *            Sense used by the player.
     * @return <code>true</code> if a description is provided for the specified
     *         sense, <code>false</code> otherwise.
     */
    public boolean hasSense (final Sense sense) {
        return this.sensesDescription.containsKey(sense);
    }

    /**
     * Returns the description associated with the specified sense for this item.
     * 
     * @param sense
     *            The sense used by the player.
     * @return The description associated with the specified sense or
     *         <code>null</code> if no description is provided.
     */
    public String getSenseDescription (final Sense sense) {
        return this.sensesDescription.get(sense);
    }

    /**
     * Copies all of the senses descriptions from the specified map to this senses's
     * map. These mappings will replace any mappings that this map had for any of
     * the keys currently in the specified map.
     *
     * @param senseDesc
     *            Mappings to be stored in the map.
     * @throws NullPointerException
     *             If the specified map is <code>null</code>.
     */
    public void putAllSensesDescription (Map<? extends Sense, ? extends String> senseDesc) {
        this.sensesDescription.putAll(senseDesc);
    }

    public boolean isCarriable () {
        return isCarriable;
    }

    public void setCarriable (boolean isCarriable) {
        this.isCarriable = isCarriable;
    }

    public boolean isStackable () {
        return isStackable;
    }

    public void setStackable (boolean isStackable) {
        this.isStackable = isStackable;
    }
}
