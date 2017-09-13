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

package org.jls.sod.core.model.world;

import java.util.HashMap;

/**
 * Data model of a zone.
 *
 * @author LE SAUCE Julien
 * @date Sep 3, 2015
 */
public class Zone {

	private final String id;
	private final String name;
	private final String description;
	private final Region region;
	private final HashMap<String, Room> rooms;
	//private final Room[][] map;

	private Room entryPoint;

	/**
	 * Instantiates a new zone.
	 *
	 * @param id
	 *            The zone identifier.
	 * @param name
	 *            The zone name.
	 * @param desc
	 *            The zone description.
	 * @param region
	 *            Specifies the region which contains this zone.
	 */
	public Zone (final String id, final String name, final String desc, final Region region) {
		this.id = id;
		this.name = name;
		this.description = desc;
		this.rooms = new HashMap<>();
		this.region = region;
		this.entryPoint = null;
		//this.map = null;
	}

	/**
	 * Returns the zone identifier.
	 *
	 * @return Zone idientifier.
	 */
	public String getId () {
		return this.id;
	}

	/**
	 * Returns the zone name.
	 *
	 * @return Zone name.
	 */
	public String getName () {
		return this.name;
	}

	/**
	 * Returns the description of this zone.
	 *
	 * @return Description of this zone.
	 */
	public String getDescription () {
		return this.description;
	}

	/**
	 * Returns the rooms contained in this zone.
	 *
	 * @return Rooms contained in this zone.
	 */
	public HashMap<String, Room> getRooms () {
		return this.rooms;
	}

	/**
	 * Adds the specified room to the map of the rooms. If the map previously
	 * contained a mapping for the room, the old value is replaced.
	 *
	 * @param room
	 *            The room to add to the map.
	 * @return The previous room contained in the map, or <code>null</code> if
	 *         there was no mapping for this room.
	 */
	public Room putRoom (final Room room) {
		return this.rooms.put(room.getId(), room);
	}

	/**
	 * Returns the room to which the specified key is mapped, or
	 * <code>null</code> if this map contains no mapping for the key.
	 *
	 * @param key
	 *            The room identifier.
	 * @return The room contained in the map or <code>null</code> if this map
	 *         contains no mapping for the key.
	 */
	public Room getRoom (final String key) {
		return this.rooms.get(key);
	}

	/**
	 * Returns the region where this zone is located.
	 *
	 * @return The region where this zone is located.
	 */
	public Region getRegion () {
		return this.region;
	}

	/**
	 * Returns the default room where the player should start.
	 *
	 * @return The default room where the player should start.
	 */
	public Room getEntryPoint () {
		return this.entryPoint;
	}

	/**
	 * Specfies the default room where the player should start.
	 *
	 * @param entry
	 *            The default room where the player should start.
	 */
	public void setEntryPoint (final Room entry) {
		this.entryPoint = entry;
	}
}