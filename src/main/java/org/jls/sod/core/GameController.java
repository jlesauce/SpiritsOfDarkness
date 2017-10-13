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

package org.jls.sod.core;

import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom2.JDOMException;
import org.jls.sod.ApplicationController;
import org.jls.sod.core.loader.Loader;
import org.jls.sod.core.model.Story;
import org.jls.sod.core.model.character.Character;
import org.jls.sod.core.model.world.Region;
import org.jls.sod.core.model.world.Room;
import org.jls.sod.core.model.world.World;
import org.jls.sod.core.model.world.Zone;
import org.jls.sod.util.ResourceManager;
import org.jls.toolbox.util.file.FileUtils;

/**
 * The game controller.
 *
 * @author LE SAUCE Julien
 * @date Sep 3, 2015
 */
public class GameController {

    private final GameModel model;
    private final ApplicationController controller;
    private final DisplayController displayController;
    private final Logger logger;

    /**
     * Instantiates the game controller.
     *
     * @param model
     *            The game data model.
     * @param controller
     *            The controller of the application.
     */
    public GameController(final GameModel model, final ApplicationController controller) {
        this.model = model;
        this.controller = controller;
        this.displayController = new DisplayController(model, controller);
        this.logger = LogManager.getLogger();
    }

    /**
     * Shows the new game creation panel.
     */
    public void showNewGamePanel () {
        this.controller.showNewGamePanel();
    }

    /**
     * Shows the load game panel.
     */
    public void showLoadGamePanel () {
        this.controller.showLoadGamePanel();
    }

    /**
     * Exits the application.
     */
    public void exitApplication () {
        this.controller.showLoadGamePanel();
    }

    /**
     * Creates a new game with the selected story and save the new game instance in
     * the save directory.
     *
     * @param storyId
     *            The story identifier (i.e. the name of the story directory).
     * @param saveDirName
     *            The name of the new game instance.
     * @return <code>true</code> if the game instance has been created,
     *         <code>false</code> otherwise.
     * @throws IOException
     *             If an error occurred during the create of the save directory.
     * @throws JDOMException
     *             If one the description files is malformed.
     *
     */
    public boolean createNewGame (final String storyId, final String saveDirName) throws IOException, JDOMException {
        // Checks input
        if (storyId == null) {
            throw new NullPointerException("Story ID cannot be null");
        }
        if (saveDirName == null) {
            throw new NullPointerException("Save directory cannot be null");
        }
        if (storyId.isEmpty()) {
            throw new IllegalArgumentException("Story ID is empty");
        }
        if (saveDirName.isEmpty()) {
            throw new IllegalArgumentException("Save name is empty");
        }

        // If the story directory is known
        if (this.model.getStories().containsKey(storyId)) {
            // Gets the story directory
            File storyDir = this.model.getStories().get(storyId);

            // If the directory still exists
            if (storyDir.exists()) {
                File dstDir = new File(new File(ResourceManager.SAVED_PATH), saveDirName);

                // Checks if a saved game already exists
                if (dstDir.exists()) {
                    this.controller.pop("New Game Error", "A game instance with the same name already exist.",
                            JOptionPane.WARNING_MESSAGE);
                    this.controller.printError("A game instance with the name " + saveDirName + " already exist.");
                    return false;
                } else { // Else creates the new game instance
                    this.logger.info("Creating new game instance {}", saveDirName);

                    // Copy the story to the save directory
                    FileUtils.copyFolder(storyDir, dstDir);
                    this.displayController.printMessage("Game instance " + saveDirName + " has been created.");

                    // Generates the instance XML file
                    XMLConfiguration instanceConfig = new XMLConfiguration();
                    instanceConfig.addProperty("storyId", storyId);
                    try {
                        instanceConfig.save(new File(dstDir, "instance.xml"));
                    } catch (ConfigurationException e1) {
                        this.controller.printError("Cannot generate the instance configuration file.");
                        this.logger.error("Cannot generate the instance configuration file", e1);
                    }

                    // Loads the game
                    try {
                        loadGame(saveDirName);
                    } catch (Exception e) {
                        // If an error occurred, delete the new instance
                        FileUtils.delete(dstDir, true);
                        this.logger.debug("New instance {} deleted because an error occurred at loading", saveDirName);
                        this.controller.printError("Cannot load the game instance.");
                        throw e;
                    }
                    return true;
                }
            } else {
                throw new IllegalStateException("Story directory not found : " + storyDir.getAbsolutePath());
            }
        } else {
            throw new IllegalArgumentException("Story not found : " + saveDirName);
        }
    }

    /**
     * Loads the specified game instance.
     *
     * @param savedGameId
     *            Game instance identifier.
     * @throws IOException
     *             If an error occurred reading the game instance files.
     * @throws JDOMException
     *             If a game instance file is malformed.
     */
    public void loadGame (final String savedGameId) throws JDOMException, IOException {
        // Checks input
        if (savedGameId == null) {
            throw new NullPointerException("Game instance identifier cannot be null");
        }
        if (savedGameId.isEmpty()) {
            throw new IllegalArgumentException("Game instance identifier is empty");
        }

        File gameDir = new File(ResourceManager.SAVED_PATH, savedGameId);
        // If the game instance exists
        if (gameDir.exists()) {
            this.logger.info("Loading game instance {}", savedGameId);

            // Build loader instance
            Loader loader = Loader.build(gameDir);

            // Loads game instance
            try {
                XMLConfiguration instanceConfig = new XMLConfiguration(new File(gameDir, "instance.xml"));
                this.model.setInstanceConfig(instanceConfig);
                this.model.setInstanceDir(gameDir);
            } catch (ConfigurationException e) {
                this.controller.printError("Cannot load the instance configuration file.");
                throw new IllegalArgumentException("Cannot load the instance configuration file", e);
            }

            // Extracts the saved room entry point's path from the instance
            // config file if there is one
            XMLConfiguration config = this.model.getInstanceConfig();
            String entryPoints = config.getString("entryPoint.path");
            String[] entryTokens = StringUtils.countMatches(entryPoints, ".") == 3 ? entryPoints.split("\\.") : null;

            if (entryTokens != null) { // Log
                this.logger.debug("Select entry path from instance configuration file : {}", entryPoints);
            }

            // Gets the world ID entry point using the saved path if there is
            // one, or gets the ID from the story file descriptor
            Story story = Loader.getInstance().loadStory();
            String worldId = entryTokens != null ? entryTokens[0] : story.getDefaultWorld();

            this.logger.debug("World entry point : {}", worldId);

            // Loads world
            this.logger.info("Loading world");
            // Loads the default entry point or the path specified in the
            // instance config file whereas a path is provided or not
            World world = entryTokens != null ? loader.loadWorld(worldId, entryTokens) : loader.loadWorld(worldId);

            // Selects the world entry point
            if (world != null) {
                this.model.setWorld(world);
                this.logger.info("Select world : {}", world.getName());
                Region region = world.getEntryPoint();
                this.model.setRegion(region);
                this.logger.info("Select region : {}", region.getId());
                Zone zone = region.getEntryPoint();
                this.model.setZone(zone);
                this.logger.info("Select zone : {}", zone.getName());
                Room room = zone.getEntryPoint();
                this.model.setRoom(room);
                this.logger.info("Select room : {}", room.getName());

                this.model.getRoom().setVisited(true);
            } else {
                throw new IllegalStateException("World entry point not found : " + story.getDefaultWorld());
            }
            this.displayController.printMessage("Game instance " + savedGameId + " has been loaded.");

            // Loads the character
            Character character = new Character();
            this.model.setCharacter(character);

            // Prints the welcome message
            this.displayController.printWelcomeMessage();
            // Prints the description of the entry point
            this.displayController.printRoomDescription(this.model.getRoom());
        } else {
            throw new IllegalArgumentException("Game instance not found : " + gameDir.getAbsolutePath());
        }
    }

    /**
     * Shows the map to the player.
     */
    public void showMap () {
        this.controller.showMap();
    }

    /**
     * Hides the map.
     */
    public void hideMap () {
        this.controller.hideMap();
    }

    /**
     * Updates the current position of the player. This function update the data
     * model and the instance file descriptor.
     *
     * @param room
     *            The new position of the player.
     * @throws ConfigurationException
     *             If an error occurred saving instance configuration file.
     */
    public void updateCurrentPosition (final Room room) throws ConfigurationException {
        this.logger.info("Updates current position : {}", room.getName());
        this.model.setRoom(room);
        room.setVisited(true);
        // Updates instance config file
        XMLConfiguration config = this.model.getInstanceConfig();
        Zone zone = room.getZone();
        Region region = zone.getRegion();
        World world = region.getWorld();
        config.setProperty("entryPoint.path",
                world.getId() + "." + region.getId() + "." + zone.getId() + "." + room.getId());
        config.save();
    }

    public static boolean hasSavedGames () {
        return new File(ResourceManager.SAVED_PATH).exists();
    }

    /**
     * Returns the game data model.
     *
     * @return The game data model.
     */
    public GameModel getModel () {
        return this.model;
    }

    /**
     * Returns the display delegate controller.
     *
     * @return The display controller.
     */
    public DisplayController getDisplayController () {
        return displayController;
    }
}
