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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.jls.sod.core.model.Direction;
import org.jls.sod.core.model.Sense;
import org.jls.sod.core.model.inventory.Inventory;

/**
 * Data model of a room.
 * 
 * @author LE SAUCE Julien
 * @date Sep 3, 2015
 */
public class Room {

	private final String id;
	private final String name;
	private final String shortDescription;
	private final String longDescrition;
	private final HashMap<Sense, String> sensesDescription;
	private final HashMap<Direction, Room> neighbors;
	private final HashMap<Direction, String> neighborsDescription;
	private final Zone zone;
	private final Inventory inventory;

	private boolean isVisited;

	/**
	 * Instantiates a new default room.
	 * 
	 * @param id
	 *            The room identifier.
	 * @param name
	 *            The room name.
	 * @param shortDesc
	 *            The short description of this room.
	 * @param longDesc
	 *            The long description of this room.
	 * @param zone
	 *            Specifies the zone which contains this room.
	 */
	public Room (final String id, final String name, final String shortDesc, final String longDesc, final Zone zone) {
		this.id = id;
		this.name = name;
		this.shortDescription = shortDesc;
		this.longDescrition = longDesc;
		this.sensesDescription = new HashMap<>();
		this.neighbors = new HashMap<>();
		this.neighborsDescription = new HashMap<>();
		this.zone = zone;
		this.inventory = new Inventory();
		this.isVisited = false;
	}

	/**
	 * Updates the room's inventory with the specified loot (the loot is also
	 * updated to remove the moved items).
	 * 
	 * @param loot
	 *            The loot used to update the room's inventory.
	 */
	public void importInventory (final Inventory loot) {
		this.inventory.importInventory(loot);
	}

	/**
	 * Returns the room identifier.
	 * 
	 * @return Room idientifier.
	 */
	public String getId () {
		return this.id;
	}

	/**
	 * Returns the room name.
	 * 
	 * @return Room name.
	 */
	public String getName () {
		return this.name;
	}

	/**
	 * Returns the short description of this room.
	 * 
	 * @return Short description of this room.
	 */
	public String getShortDescription () {
		return this.shortDescription;
	}

	/**
	 * Returns the long description of this room.
	 * 
	 * @return Long description of this room.
	 */
	public String getLongDescrition () {
		return this.longDescrition;
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
	 * Returns the description associated with the specified sense for this
	 * room.
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
	 * Copies all of the senses descriptions from the specified map to this
	 * senses's map. These mappings will replace any mappings that this map had
	 * for any of the keys currently in the specified map.
	 *
	 * @param senseDesc
	 *            Mappings to be stored in the map.
	 * @throws NullPointerException
	 *             If the specified map is <code>null</code>.
	 */
	public void putAllSensesDescription (Map<? extends Sense, ? extends String> senseDesc) {
		this.sensesDescription.putAll(senseDesc);
	}

	/**
	 * Tells if there is a room in the specified direction.
	 * 
	 * @param direction
	 *            Direction of the next room.
	 * @return <code>true</code> if there is a next room in the specified
	 *         direction, <code>false</code> otherwise.
	 */
	public boolean hasNeighbor (final Direction direction) {
		return this.neighbors.containsKey(direction);
	}

	/**
	 * Returns the next room in the specified direction.
	 * 
	 * @param direction
	 *            Direction of the next room.
	 * @return Next room in the specified direction or <code>null</code> if it's
	 *         a dead end.
	 */
	public Room getNeighbor (final Direction direction) {
		return this.neighbors.get(direction);
	}

	/**
	 * Returns the next room description in the specified direction.
	 * 
	 * @param direction
	 *            Direction of the next room.
	 * @return Next room description in the specified direction or
	 *         <code>null</code> if it's a dead end.
	 */
	public String getNeighborDescription (final Direction direction) {
		return this.neighborsDescription.get(direction);
	}

	/**
	 * Returns a copy of the neighbor's list for this room.
	 * 
	 * @return List of the available neighbors for this room.
	 */
	public ArrayList<Room> getNeighbors () {
		return new ArrayList<>(this.neighbors.values());
	}

	/**
	 * Copies all of the neighbors from the specified map to this neighbor's
	 * map. These mappings will replace any mappings that this map had for any
	 * of the keys currently in the specified map.
	 *
	 * @param neighbors
	 *            Mappings to be stored in the map.
	 * @throws NullPointerException
	 *             If the specified map is <code>null</code>.
	 */
	public void putAllNeighbors (Map<? extends Direction, ? extends Room> neighbors) {
		this.neighbors.putAll(neighbors);
	}

	/**
	 * Copies all of the neighbors descriptions from the specified map to this
	 * neighbor's map. These mappings will replace any mappings that this map
	 * had for any of the keys currently in the specified map.
	 *
	 * @param neighborsDesc
	 *            Mappings to be stored in the map.
	 * @throws NullPointerException
	 *             If the specified map is <code>null</code>.
	 */
	public void putAllNeighborsDescription (Map<? extends Direction, ? extends String> neighborsDesc) {
		this.neighborsDescription.putAll(neighborsDesc);
	}

	/**
	 * Returns the zone where this room is located.
	 * 
	 * @return The zone where this room is located.
	 */
	public Zone getZone () {
		return this.zone;
	}

	public Inventory getInventory () {
		return inventory;
	}

	/**
	 * Tells if the room has been visited.
	 * 
	 * @return <code>true</code> if the room has been visited,
	 *         <code>false</code> otherwise.
	 */
	public boolean isVisited () {
		return isVisited;
	}

	/**
	 * Specifies if the if the room has been visited.
	 * 
	 * @param isVisited
	 *            <code>true</code> if the room has been visited,
	 *            <code>false</code> otherwise.
	 */
	public void setVisited (boolean isVisited) {
		this.isVisited = isVisited;
	}
}