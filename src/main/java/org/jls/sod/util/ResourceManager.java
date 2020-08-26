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

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;

import javax.swing.ImageIcon;

import org.apache.commons.configuration.CombinedConfiguration;
import org.apache.commons.configuration.DefaultConfigurationBuilder;
import org.apache.logging.log4j.LogManager;
import org.jls.sod.SpiritsOfDarkness;

public class ResourceManager {

    private static final String slash = File.separator;

    public static final String RESOURCES_DIR = "resources";
    public static final String RESOURCES_PATH = SpiritsOfDarkness.class.getProtectionDomain().getCodeSource()
            .getLocation().getPath() + RESOURCES_DIR;
    public static final String IMG_PATH = "img";

    public static final String USER_DIR = System.getProperty("user.dir");
    public static final String DATA_PATH = USER_DIR + slash + "data";
    public static final String STORIES_PATH = DATA_PATH + slash + "stories";
    public static final String SAVED_PATH = DATA_PATH + slash + "saved";

    public static final String LOG4J_FILE = "log4j2.xml";

    private static ResourceManager INSTANCE = null;

    private CombinedConfiguration configuration;

    private ResourceManager() {
        DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();
        builder.setConfigurationBasePath(RESOURCES_PATH);
        builder.setBasePath(RESOURCES_PATH);
        try {
            builder.setFile(getResourceAsFile("configuration-descriptor.xml"));
            builder.setEncoding("UTF8");
            this.configuration = builder.getConfiguration(true);
        } catch (Exception e) {
            LogManager.getLogger().fatal("An error occurred while building application properties", e);
            Runtime.getRuntime().exit(-1);
        }
    }

    public static ResourceManager getInstance() {
        if (ResourceManager.INSTANCE == null) {
            ResourceManager.INSTANCE = new ResourceManager();
        }
        return ResourceManager.INSTANCE;
    }

    public static URL getResource(final String name) throws FileNotFoundException {
        URL url = Thread.currentThread().getContextClassLoader().getResource(name);
        if (url == null) {
            url = Thread.currentThread().getContextClassLoader().getResource(RESOURCES_DIR + File.separator + name);
        }
        if (url == null) {
            throw new FileNotFoundException("Resource not found");
        }
        return url;
    }

    public static File getResourceAsFile(final String name) throws FileNotFoundException {
        return new File(getResource(name).getPath());
    }

    public String getString(final String key) throws IllegalArgumentException {
        if (this.configuration.containsKey(key)) {
            return this.configuration.getString(key);
        }
        throw new IllegalArgumentException("Key does not exist : " + key);
    }

    public int getInt(final String key) {
        String str = getString(key);
        if (!str.isEmpty()) {
            try {
                return Integer.parseInt(str);
            } catch (Exception e) {
                throw new NumberFormatException("Cannot parse value to integer : " + str);
            }
        }
        throw new IllegalStateException("Empty value");
    }

    public Color getColor(final String key) {
        String str = getString(key);
        if (!str.isEmpty()) {
            try {
                return Color.decode(str);
            } catch (Exception e) {
                throw new IllegalArgumentException("Cannot create color : " + str, e);
            }
        }
        throw new IllegalStateException("Empty value");
    }

    public ImageIcon getIcon(final String key) throws FileNotFoundException {
        String filename = getString(key);
        String path = IMG_PATH + slash + filename;
        URL url = getResource(path);
        return new ImageIcon(url);
    }
}
