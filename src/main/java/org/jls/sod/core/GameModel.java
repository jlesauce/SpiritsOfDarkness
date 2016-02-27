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

package org.jls.sod.core;

import java.io.File;
import java.util.HashMap;

import org.apache.commons.configuration.XMLConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jls.sod.core.model.character.Character;
import org.jls.sod.core.model.world.Region;
import org.jls.sod.core.model.world.Room;
import org.jls.sod.core.model.world.World;
import org.jls.sod.core.model.world.Zone;
import org.jls.sod.util.ResourceManager;
import org.jls.toolbox.gui.AbstractModel;
import org.jls.toolbox.util.file.FileFilter;

/**
 * Game data model.
 * 
 * @author LE SAUCE Julien
 * @date Sep 2, 2015
 */
public class GameModel extends AbstractModel {

	private static final int DEFAULT_MAP_SIZE = 10;

	private final Logger logger;
	private final HashMap<String, File> stories;
	private final Room[][] map;

	private XMLConfiguration instanceConfig;
	private File instanceDir;

	private Character character;
	private World world;
	private Region region;
	private Zone zone;
	private Room room;

	/**
	 * Instanciates a default data model.
	 */
	public GameModel () {
		this.logger = LogManager.getLogger();
		this.stories = new HashMap<>();
		this.map = new Room[DEFAULT_MAP_SIZE][DEFAULT_MAP_SIZE];
		this.instanceConfig = null;
		this.instanceDir = null;
		this.character = null;
		this.world = null;
		this.region = null;
		this.zone = null;
		this.room = null;
		listStories(new File(ResourceManager.STORIES_PATH));
	}

	/**
	 * List the available stories.
	 * 
	 * @param parentDir
	 *            The directory containing the stories.
	 */
	private void listStories (final File parentDir) {
		if (parentDir.exists()) {
			FileFilter filter = new FileFilter(FileFilter.ONLY_DIRECTORIES);
			File[] files = parentDir.listFiles(filter);
			this.logger.info("Listing game stories");
			for (File file : files) {
				this.stories.put(file.getName(), file);
			}
		} else {
			throw new IllegalStateException("Stories directory not found : " + parentDir.getAbsolutePath());
		}
	}

	/**
	 * Returns the current world.
	 * 
	 * @return Current world.
	 */
	public World getWorld () {
		return this.world;
	}

	/**
	 * Returns the available game stories.
	 * 
	 * @return The available game stories.
	 */
	public HashMap<String, File> getStories () {
		return this.stories;
	}

	/**
	 * Returns the instance's map.
	 * 
	 * @return The instance's map.
	 */
	public Room[][] getMap () {
		return map;
	}

	/**
	 * Returns the {@link XMLConfiguration} instance used to describe the game
	 * instance.
	 * 
	 * @return Configuration file used to describe the game instance.
	 */
	public XMLConfiguration getInstanceConfig () {
		return instanceConfig;
	}

	/**
	 * Sets the {@link XMLConfiguration} instance used to describe the game
	 * instance.
	 * 
	 * @param config
	 *            Configuration file used to describe the game instance.
	 */
	public void setInstanceConfig (final XMLConfiguration config) {
		this.instanceConfig = config;
	}

	/**
	 * Returns the instance directory.
	 * 
	 * @return The instance directory.
	 */
	public File getInstanceDir () {
		return instanceDir;
	}

	/**
	 * Sets the instance directory.
	 * 
	 * @param dir
	 *            The instance directory.
	 */
	public void setInstanceDir (final File dir) {
		this.instanceDir = dir;
	}

	/**
	 * Returns the character's instance.
	 * 
	 * @return The character's instance.
	 */
	public Character getCharacter () {
		return character;
	}

	/**
	 * Sets the character's instance.
	 * 
	 * @param character
	 *            The character's instance.
	 */
	public void setCharacter (Character character) {
		this.character = character;
	}

	/**
	 * Specifies the current world.
	 * 
	 * @param world
	 *            Current world.
	 */
	public void setWorld (World world) {
		this.world = world;
	}

	/**
	 * Returns the current region.
	 * 
	 * @return Current region.
	 */
	public Region getRegion () {
		return this.region;
	}

	/**
	 * Specifies the current region.
	 * 
	 * @param region
	 *            Current region.
	 */
	public void setRegion (Region region) {
		this.region = region;
	}

	/**
	 * Returns the current zone.
	 * 
	 * @return Current zone.
	 */
	public Zone getZone () {
		return this.zone;
	}

	/**
	 * Specifies the current zone.
	 * 
	 * @param zone
	 *            Current zone.
	 */
	public void setZone (Zone zone) {
		this.zone = zone;
	}

	/**
	 * Returns the current room.
	 * 
	 * @return Current room.
	 */
	public Room getRoom () {
		return this.room;
	}

	/**
	 * Specifies the current room.
	 * 
	 * @param room
	 *            Current room.
	 */
	public void setRoom (Room room) {
		this.room = room;
	}
}