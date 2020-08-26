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
package org.jls.sod.util;

import java.io.File;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.builder.fluent.PropertiesBuilderParameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Settings {

    private final String SETTINGS_FILENAME = "settings.properties";

    private final Logger logger;
    private final FileBasedConfigurationBuilder<FileBasedConfiguration> builder;

    public Settings() {
        this.logger = LogManager.getLogger();
        this.builder = new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class);

        configureBuilder(builder, new File(ResourceManager.DATA_PATH, SETTINGS_FILENAME));
    }

    public void configureBuilder(FileBasedConfigurationBuilder<FileBasedConfiguration> builder, final File file) {
        Parameters params = new Parameters();

        PropertiesBuilderParameters builderParams = params.properties().setFile(file);
        builder.configure(builderParams);
    }

    public void setProperty(final String key, final String value) {
        try {
            Configuration config = builder.getConfiguration();
            config.setProperty(key, value);
            builder.save();
        } catch (final Exception e) {
            this.logger.error(e);
        }
    }

    public String getString(final String key) {
        Configuration config;
        try {
            config = builder.getConfiguration();
            return config.getString(key);
        } catch (ConfigurationException e) {
            this.logger.error(e);
        }
        return null;
    }

    public boolean isLastPlayedGameAutoloadEnabled() {
        String autoload = getString("settings.game.autoloadLastPlayedGame");
        return Boolean.valueOf(autoload);
    }

    public String getLastPlayedGame() {
        return getString("settings.game.lastPlayedGame");
    }

    public void setLastPlayedGame(final String instanceName) {
        setProperty("settings.game.lastPlayedGame", instanceName);
    }
}
