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

package org.jls.sod.core.loader;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jls.sod.core.model.Direction;
import org.jls.sod.core.model.Sense;
import org.jls.sod.core.model.Story;
import org.jls.sod.core.model.inventory.Inventory;
import org.jls.sod.core.model.inventory.NotCarriableException;
import org.jls.sod.core.model.item.Item;
import org.jls.sod.core.model.item.ItemType;
import org.jls.sod.core.model.world.Region;
import org.jls.sod.core.model.world.Room;
import org.jls.sod.core.model.world.World;
import org.jls.sod.core.model.world.Zone;
import org.jls.toolbox.util.xml.XMLParser;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;

/**
 * Loader that parses the XML files available in the data resources of the game
 * and returns plain old objects used by the game controller.
 * 
 * @author LE SAUCE Julien
 * @date Sep 3, 2015
 */
public class Loader {

	/**
	 * Unique instance of this class.
	 */
	private static Loader INSTANCE = null;

	private final Logger logger;
	private final File instanceDir;
	private final File worldsDir;
	private final File itemsDir;

	/**
	 * Instantiates the game loader.
	 * 
	 * @param instanceDirectory
	 *            Specifies the instance directory containing all the files of
	 *            the game instance.
	 */
	private Loader (final File instanceDirectory) {
		this.logger = LogManager.getLogger();
		this.instanceDir = instanceDirectory;
		this.worldsDir = new File(this.instanceDir, "worlds");
		this.itemsDir = new File(this.instanceDir, "items");
	}

	/**
	 * Builds the unique instance of this class.
	 * 
	 * @param instanceDirectory
	 *            Specifies the instance directory containing all the files of
	 *            the game instance.
	 * @return Unique instance of this class.
	 */
	public final static Loader build (final File instanceDirectory) {
		Loader.INSTANCE = new Loader(instanceDirectory);
		return Loader.INSTANCE;
	}

	/**
	 * Returns the unique instance of this class.
	 * 
	 * @return Unique instance of this class.
	 */
	public final static Loader getInstance () {
		if (Loader.INSTANCE == null) {
			throw new IllegalStateException("Instance not built yet");
		}
		return Loader.INSTANCE;
	}

	/**
	 * Loads the {@link Story} from the instance directory.
	 * 
	 * @param storyFile
	 *            The XML file containing the story.
	 * @return New instance of {@link Story} created from the file
	 *         <i>story.xml</i>.
	 * @throws JDOMException
	 *             If the associated XML file is malformed.
	 * @throws IOException
	 *             If an error occurred reading the associated file.
	 */
	public static Story loadStory (final File storyFile) throws JDOMException, IOException {
		Document doc = XMLParser.parseXML(storyFile);
		Element root = doc.getRootElement();
		String id = XMLParser.getAttributeValue(root, "id");
		String name = XMLParser.getAttributeValue(root, "name");
		String description = root.getChildTextNormalize("Description").replace("\\n", "\n");
		String world = XMLParser.getAttributeValue(root.getChild("EntryPoint"), "id");
		return new Story(id, name, description, world);
	}

	/**
	 * Loads the {@link Story} from the instance directory.
	 * 
	 * @return New instance of {@link Story} created from the file
	 *         <i>story.xml</i>.
	 * @throws JDOMException
	 *             If the associated XML file is malformed.
	 * @throws IOException
	 *             If an error occurred reading the associated file.
	 */
	public Story loadStory () throws JDOMException, IOException {
		return loadStory(new File(this.instanceDir, "story.xml"));
	}

	/**
	 * Loads the specified world instance.
	 * 
	 * @param worldId
	 *            Unique identifier of the world.
	 * @param entryPoints
	 *            Allows to specify the entry points path instead of the default
	 *            path specified in the description files :
	 *            {world}.{region}.{zone}.{room}.
	 * @return New instance of {@link World} loaded from the associated file.
	 * @throws JDOMException
	 *             If the associated XML file is malformed.
	 * @throws IOException
	 *             If an error occurred reading the associated file.
	 */
	public World loadWorld (final String worldId, final String... entryPoints) throws JDOMException, IOException {
		// Checks input
		if (worldId == null || worldId.isEmpty()) {
			throw new IllegalArgumentException("World's identifier cannot be null or empty");
		}

		File worldDir = new File(this.worldsDir, worldId);
		if (worldDir.exists()) {
			File worldFile = new File(worldDir, "world.xml");
			if (worldFile.exists()) {
				// Loads the world
				Document doc = XMLParser.parseXML(worldFile);
				Element root = doc.getRootElement();
				String id = XMLParser.getAttributeValue(root, "id");
				String name = XMLParser.getAttributeValue(root, "name");
				this.logger.info("Loading world {id={}, name={}}", id, name);
				String description = root.getChildTextNormalize("Description").replace("\\n", "\n");
				// Loads the entry point
				String defEntryId = XMLParser.getAttributeValue(root.getChild("EntryPoint"), "id");
				String entryId = entryPoints.length >= 2 ? entryPoints[1] : defEntryId;
				File entryDir = new File(worldDir, entryId);
				World world = new World(id, name, description);
				Region entry = loadRegion(entryDir, world, entryPoints);
				world.setEntryPoint(entry);
				return world;
			} else {
				throw new IllegalArgumentException("World file not found :" + worldDir.getAbsolutePath());
			}
		} else {
			throw new IllegalArgumentException("World directory not found :" + worldDir.getAbsolutePath());
		}
	}

	/**
	 * Loads the specified item instance.
	 * 
	 * @param itemId
	 *            Unique identifier of the item.
	 * @return New instance of {@link Item} loaded from the associated file.
	 * @throws JDOMException
	 *             If the associated XML file is malformed.
	 * @throws IOException
	 *             If an error occurred reading the associated file.
	 */
	public Item loadItem (final String itemId) throws JDOMException, IOException {
		// Checks input
		if (itemId == null || itemId.isEmpty()) {
			throw new IllegalArgumentException("Item's identifier cannot be null or empty");
		}

		File itemFile = new File(this.itemsDir, itemId + ".xml");
		if (itemFile.exists()) {
			// Loads the item
			Document doc = XMLParser.parseXML(itemFile);
			Element root = doc.getRootElement();
			Element description = root.getChild("Description");

			String id = XMLParser.getAttributeValue(root, "id");
			String name = XMLParser.getAttributeValue(root, "name");
			String shortDesc = description.getChildTextNormalize("Short").replace("\\n", "\n");
			String longDesc = description.getChildTextNormalize("Long").replace("\\n", "\n");
			ItemType type = ItemType.valueOf(root.getAttributeValue("type").toUpperCase());
			boolean isCarriable = Boolean.valueOf(root.getAttributeValue("isCarriable"));
			boolean isStackable = Boolean.valueOf(root.getAttributeValue("isStackable"));
			HashMap<Sense, String> sensesDesc = loadSenses(root.getChild("Senses"));

			Item item = new Item(id, name, type, shortDesc, longDesc);
			item.setCarriable(isCarriable);
			item.setStackable(isStackable);
			item.putAllSensesDescription(sensesDesc);
			return item;
		} else {
			throw new IllegalArgumentException("Item file not found :" + itemFile.getAbsolutePath());
		}
	}

	/**
	 * Checks the existence of the specified item.
	 * 
	 * @param itemId
	 *            Unique item identifier.
	 * @return <code>true</code> if the item exists, <code>false</code>
	 *         otherwise.
	 */
	public boolean itemExists (final String itemId) {
		File itemFile = new File(this.itemsDir, itemId + ".xml");
		return itemFile.exists();
	}

	/**
	 * Loads the senses' descriptions from the Senses element.
	 * 
	 * @param sensesElmt
	 *            XML element containing the senses' descriptions.
	 * @return {@link HashMap} containing the provided senses' descriptions.
	 */
	private static HashMap<Sense, String> loadSenses (final Element sensesElmt) {
		HashMap<Sense, String> senses = new HashMap<>();
		// Iterates over the senses description
		for (Element elmt : sensesElmt.getChildren()) {
			Sense sense = Sense.parseValue(elmt.getName());
			// If description is not empty
			if (!elmt.getValue().isEmpty()) {
				senses.put(sense, elmt.getTextNormalize());
			}
		}
		return senses;
	}

	/**
	 * Loads the region instance from the specified directory.
	 * 
	 * @param dir
	 *            Directory containing the region description.
	 * @param world
	 *            Specifies the parent world.
	 * @param entryPoints
	 *            Allows to specify the entry points path instead of the default
	 *            path specified in the description files :
	 *            {world}.{region}.{zone}.{room}.
	 * @return New instance of {@link Region} loaded from the specified file.
	 * @throws JDOMException
	 *             If the associated XML file is malformed.
	 * @throws IOException
	 *             If an error occurred reading the associated file.
	 */
	private Region loadRegion (final File dir, final World world, final String... entryPoints)
			throws JDOMException, IOException {
		Document doc = XMLParser.parseXML(new File(dir, "region.xml"));
		Element root = doc.getRootElement();
		String id = XMLParser.getAttributeValue(root, "id");
		String name = XMLParser.getAttributeValue(root, "name");
		this.logger.info("Loading region {id={}, name={}}", id, name);
		String description = root.getChildTextNormalize("Description").replace("\\n", "\n");
		// Loads the entry point
		String defEntryId = XMLParser.getAttributeValue(root.getChild("EntryPoint"), "id");
		String entryId = entryPoints.length >= 3 ? entryPoints[2] : defEntryId;
		File entryDir = new File(dir, entryId);
		Region region = new Region(id, name, description, world);
		Zone entry = loadZone(entryDir, region, entryPoints);
		region.setEntryPoint(entry);
		return region;
	}

	/**
	 * Loads the zone instance from the specified directory.
	 * 
	 * @param dir
	 *            Directory containing the zone description.
	 * @param region
	 *            Specifies the parent region.
	 * @param entryPoints
	 *            Allows to specify the entry points path instead of the default
	 *            path specified in the description files :
	 *            {world}.{region}.{zone}.{room}.
	 * @return New instance of {@link Zone} loaded from the specified file.
	 * @throws JDOMException
	 *             If the associated XML file is malformed.
	 * @throws IOException
	 *             If an error occurred reading the associated file.
	 */
	private Zone loadZone (final File dir, final Region region, final String... entryPoints)
			throws JDOMException, IOException {
		Document doc = XMLParser.parseXML(new File(dir, "zone.xml"));
		Element root = doc.getRootElement();
		String id = XMLParser.getAttributeValue(root, "id");
		String name = XMLParser.getAttributeValue(root, "name");
		this.logger.info("Loading zone {id={}, name={}}", id, name);
		String description = root.getChildTextNormalize("Description").replace("\\n", "\n");
		// Loads the entry point
		String defEntryId = XMLParser.getAttributeValue(root.getChild("EntryPoint"), "id");
		String entryId = entryPoints.length >= 4 ? entryPoints[3] : defEntryId;
		File entryDir = new File(dir, "rooms");
		File entryFile = new File(entryDir, entryId + ".xml");
		Zone zone = new Zone(id, name, description, region);
		Room entry = loadRoom(entryFile, zone);
		zone.setEntryPoint(entry);
		return zone;
	}

	/**
	 * Loads the room instance from the specified file (the other accessible
	 * rooms are recursively loaded from room to room).
	 * 
	 * @param file
	 *            XML file containing the room description.
	 * @param zone
	 *            Specifies the parent zone.
	 * @return New instance of {@link Room} loaded from the specified file.
	 * @throws JDOMException
	 *             If the associated XML file is malformed.
	 * @throws IOException
	 *             If an error occurred reading the associated file.
	 */
	private Room loadRoom (final File file, final Zone zone) throws JDOMException, IOException {
		Document doc = XMLParser.parseXML(file);
		Element root = doc.getRootElement();
		String id = XMLParser.getAttributeValue(root, "id");
		String name = XMLParser.getAttributeValue(root, "name");
		this.logger.info("Loading room {id={}, name={}}", id, name);
		String shortDesc = root.getChildTextNormalize("ShortDescription").replace("\\n", "\n");
		String longDesc = root.getChildTextNormalize("LongDescription").replace("\\n", "\n");

		Room room = new Room(id, name, shortDesc, longDesc, zone);
		// Puts the room in the zone map
		zone.putRoom(room);

		// Iterates over all the directions to load the neighbors
		HashMap<Direction, Room> neighbors = new HashMap<>();
		HashMap<Direction, String> neighborsDesc = new HashMap<>();
		for (Element elmt : root.getChild("Directions").getChildren()) {
			// If next room ID is specified
			String nextRoomId = elmt.getAttributeValue("id");
			if (nextRoomId != null) {
				Direction direction = Direction.valueOf(elmt.getName().toUpperCase());

				// Gets the next room description
				neighborsDesc.put(direction, elmt.getTextNormalize());

				// Loads the next room
				File nextRoomFile = new File(file.getParent(), nextRoomId + ".xml");
				// If the next room exist
				if (nextRoomFile.exists()) {
					this.logger.debug("Adding neighbor to {} room {direction={}, id={}}", name, direction, nextRoomId);
					// If the room is not already loaded
					if (!zone.getRooms().containsKey(nextRoomId)) {
						// Loads the next room
						Room nextRoom = loadRoom(nextRoomFile, zone);
						neighbors.put(direction, nextRoom);
					} else { // Else just add the room to the neighbors
						neighbors.put(direction, zone.getRoom(nextRoomId));
					}
				} else {
					throw new IllegalStateException("In the room '" + id + "' : '" + nextRoomId + "' room not found");
				}
			}
		}
		// Updates neighbors
		room.putAllNeighbors(neighbors);
		room.putAllNeighborsDescription(neighborsDesc);

		// Loads senses descriptions
		HashMap<Sense, String> senses = loadSenses(root.getChild("Senses"));
		room.putAllSensesDescription(senses);

		// Loads items
		if (root.getChild("Items") != null) {
			Inventory items = loadItems(root.getChild("Items"));
			room.importInventory(items);
		}

		return room;
	}

	/**
	 * Loads the items from the Item element.
	 * 
	 * @param itemElmt
	 *            XML element containing the items.
	 * @return Local {@link Inventory} containing the provided items.
	 * @throws JDOMException
	 *             If the associated XML file is malformed.
	 * @throws IOException
	 *             If an error occurred reading the associated file.
	 */
	private Inventory loadItems (final Element itemElmt) throws JDOMException, IOException {
		Inventory localInventory = new Inventory();
		// Iterates over the items
		for (Element elmt : itemElmt.getChildren()) {
			String itemId = elmt.getAttributeValue("id");
			int itemQty = Integer.parseInt(elmt.getAttributeValue("quantity"));

			// Loads item
			Item item = loadItem(itemId);

			// Update local inventory
			if (item.isCarriable()) {
				try {
					localInventory.addItem(item, itemQty);
				} catch (NotCarriableException e) {
					this.logger.error("An error occurred adding an item to the local inventory", e);
				}
			}
		}
		return localInventory;
	}
}