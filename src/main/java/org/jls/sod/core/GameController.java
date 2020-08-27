/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020 Julien LE SAUCE
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
 * FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
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
import org.jls.sod.util.Settings;
import org.jls.toolbox.util.file.FileUtils;

public class GameController {

    private final GameModel model;
    private final ApplicationController controller;
    private final Settings settings;
    private final DisplayController displayController;
    private final Logger logger;

    public GameController(final GameModel model, final ApplicationController controller,
                          final Settings settings) {
        this.model = model;
        this.controller = controller;
        this.settings = settings;
        displayController = new DisplayController(model, controller);
        logger = LogManager.getLogger();
    }

    public void showNewGamePanel() {
        controller.showNewGamePanel();
    }

    public void showLoadGamePanel() {
        controller.showLoadGamePanel();
    }

    public void exitApplication() {
        controller.exitApplication();
    }

    public void showMap() {
        controller.showUserMap();
    }

    public void hideMap() {
        controller.hideUserMap();
    }

    public boolean createNewGame(final String storyId, final String saveDirName) throws IOException, JDOMException {
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
        if (model.getStories().containsKey(storyId)) {
            // Gets the story directory
            File storyDir = model.getStories().get(storyId);

            // If the directory still exists
            if (storyDir.exists()) {
                File dstDir = new File(new File(ResourceManager.SAVED_PATH), saveDirName);

                // Checks if a saved game already exists
                if (dstDir.exists()) {
                    controller.pop("New Game Error",
                            "A game instance with the same name already exist.",
                            JOptionPane.WARNING_MESSAGE);
                    controller.printError(
                            "A game instance with the name " + saveDirName + " already exist.");
                    return false;
                } else { // Else creates the new game instance
                    logger.info("Creating new game instance {}", saveDirName);

                    // Copy the story to the save directory
                    FileUtils.copyFolder(storyDir, dstDir);
                    displayController.printMessage(
                            "Game instance " + saveDirName + " has been created.");

                    // Generates the instance XML file
                    XMLConfiguration instanceConfig = new XMLConfiguration();
                    instanceConfig.addProperty("storyId", storyId);
                    try {
                        instanceConfig.save(new File(dstDir, "instance.xml"));
                    } catch (ConfigurationException e1) {
                        controller.printError(
                                "Cannot generate the instance configuration file.");
                        logger.error("Cannot generate the instance configuration file", e1);
                    }

                    // Loads the game
                    try {
                        loadGame(saveDirName);
                    } catch (Exception e) {
                        // If an error occurred, delete the new instance
                        FileUtils.delete(dstDir, true);
                        logger.debug(
                                "New instance {} deleted because an error occurred at loading",
                                saveDirName);
                        controller.printError("Cannot load the game instance.");
                        throw e;
                    }
                    return true;
                }
            } else {
                throw new IllegalStateException(
                        "Story directory not found : " + storyDir.getAbsolutePath());
            }
        } else {
            throw new IllegalArgumentException("Story not found : " + saveDirName);
        }
    }

    public void loadGame(final String savedGameId) throws JDOMException, IOException {
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
            logger.info("Loading game instance {}", savedGameId);
            logger.info("Game instance is located under: {}", gameDir);

            // Build loader instance
            Loader loader = Loader.build(gameDir);

            // Loads game instance
            try {
                XMLConfiguration instanceConfig = new XMLConfiguration(
                        new File(gameDir, "instance.xml"));
                model.setInstanceConfig(instanceConfig);
                model.setInstanceDir(gameDir);
            } catch (ConfigurationException e) {
                controller.printError("Cannot load the instance configuration file.");
                throw new IllegalArgumentException("Cannot load the instance configuration file",
                        e);
            }

            // Extracts the saved room entry point's path from the instance
            // config file if there is one
            XMLConfiguration config = model.getInstanceConfig();
            String entryPoints = config.getString("entryPoint.path");
            String[] entryTokens = StringUtils.countMatches(entryPoints,
                    ".") == 3 ? entryPoints.split("\\.") : null;

            if (entryTokens != null) { // Log
                logger.debug("Select entry path from instance configuration file : {}",
                        entryPoints);
            }

            // Gets the world ID entry point using the saved path if there is
            // one, or gets the ID from the story file descriptor
            Story story = Loader.getInstance().loadStory();
            String worldId = entryTokens != null ? entryTokens[0] : story.getDefaultWorld();

            logger.debug("World entry point : {}", worldId);

            // Loads world
            logger.info("Loading world");
            // Loads the default entry point or the path specified in the
            // instance config file whereas a path is provided or not
            World world = entryTokens != null ? loader.loadWorld(worldId,
                    entryTokens) : loader.loadWorld(worldId);

            // Selects the world entry point
            if (world != null) {
                model.setWorld(world);
                logger.info("Select world : {}", world.getName());
                Region region = world.getEntryPoint();
                model.setRegion(region);
                logger.info("Select region : {}", region.getId());
                Zone zone = region.getEntryPoint();
                model.setZone(zone);
                logger.info("Select zone : {}", zone.getName());
                Room room = zone.getEntryPoint();
                model.setRoom(room);
                logger.info("Select room : {}", room.getName());

                model.getRoom().setVisited(true);
            } else {
                throw new IllegalStateException(
                        "World entry point not found : " + story.getDefaultWorld());
            }
            displayController.printMessage(
                    "Game instance " + savedGameId + " has been loaded.");

            // Loads the character
            Character character = new Character();
            model.setCharacter(character);

            displayController.printWelcomeMessage();
            displayController.printRoomDescription(model.getRoom());
            updateLastPlayedGameInSettings(savedGameId);
        } else {
            throw new IllegalArgumentException(
                    "Game instance not found : " + gameDir.getAbsolutePath());
        }
    }

    private void updateLastPlayedGameInSettings(final String instanceName) {
        logger.info("Update last played game: " + instanceName);
        settings.setLastPlayedGame(instanceName);
    }

    public void updateCurrentPosition(final Room room) throws ConfigurationException {
        logger.info("Updates current position : {}", room.getName());
        model.setRoom(room);
        room.setVisited(true);
        // Update instance config file
        XMLConfiguration config = model.getInstanceConfig();
        Zone zone = room.getZone();
        Region region = zone.getRegion();
        World world = region.getWorld();
        config.setProperty("entryPoint.path",
                world.getId() + "." + region.getId() + "." + zone.getId() + "." + room.getId());
        config.save();
    }

    public static boolean hasSavedGames() {
        return new File(ResourceManager.SAVED_PATH).exists();
    }

    public GameModel getModel() {
        return model;
    }

    public DisplayController getDisplayController() {
        return displayController;
    }
}
