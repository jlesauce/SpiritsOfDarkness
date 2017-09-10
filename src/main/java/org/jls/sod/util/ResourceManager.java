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

package org.jls.sod.util;

import java.awt.Color;
import java.io.File;
import java.io.InputStream;
import java.net.URL;

import javax.swing.ImageIcon;

import org.apache.commons.configuration.CombinedConfiguration;
import org.apache.commons.configuration.DefaultConfigurationBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jls.sod.SpiritsOfDarkness;

/**
 * Singleton managing the access to the resources of the application.
 *
 * @author LE SAUCE Julien
 * @date Aug 31, 2015
 */
public class ResourceManager {

	private static final String slash = File.separator;

	/*
	 * Directories
	 */
	public static final String RESOURCES_DIR = "resources";

	/*
	 * Package resources
	 */
	public static final String RESOURCES_PATH =
			SpiritsOfDarkness.class.getProtectionDomain().getCodeSource().getLocation().getPath() + RESOURCES_DIR;
	public static final String IMG_PATH = "img";
	public static final String PROPERTIES_PATH = "properties";

	/*
	 * User resources
	 */
	public static final String USER_DIR = System.getProperty("user.dir");
	public static final String DATA_PATH = USER_DIR + slash + "data";
	public static final String STORIES_PATH = DATA_PATH + slash + "stories";
	public static final String SAVED_PATH = DATA_PATH + slash + "saved";

	public static final String LOG4J_FILE = "log4j2.xml";

	/**
	 * Unique instance of the class.
	 */
	private static ResourceManager INSTANCE = null;

	private final Logger logger;
	private CombinedConfiguration configuration;

	/**
	 * Instanciates the resources manager.
	 */
	private ResourceManager () {
		this.logger = LogManager.getLogger();
		DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();
		builder.setConfigurationBasePath(RESOURCES_PATH);
		builder.setBasePath(RESOURCES_PATH);
		try {
			builder.setFile(getResourceAsFile("configuration-descriptor.xml"));
			builder.setEncoding("UTF8");
			this.configuration = builder.getConfiguration(true);
		} catch (Exception e) {
			this.logger.fatal("An error occured while building application properties", e);
			Runtime.getRuntime().exit(-1);
		}
	}

	/**
	 * Returns the unique instance of this class.
	 *
	 * @return Unique instance of this class.
	 */
	public final static ResourceManager getInstance () {
		if (ResourceManager.INSTANCE == null) {
			ResourceManager.INSTANCE = new ResourceManager();
		}
		return ResourceManager.INSTANCE;
	}

	/**
	 * Permet d'accéder à une resource de l'application via le nom du fichier.
	 *
	 * @param name
	 *            Nom du fichier à récupérer.
	 * @return {@link URL} pointant vers la resource recherchée ou
	 *         <code>null</code> si la resource n'existe pas.
	 */
	public final static URL getResource (final String name) {
		URL url = Thread.currentThread().getContextClassLoader().getResource(name);
		if (url == null) {
			url = Thread.currentThread().getContextClassLoader().getResource(RESOURCES_DIR + File.separator + name);
		}
		return url;
	}

	/**
	 * Permet d'accéder à une resource de l'application via le nom du fichier.
	 *
	 * @param name
	 *            Nom du fichier à récupérer.
	 * @return {@link InputStream} permettant d'accéder à la ressource
	 *         recherchée ou <code>null</code> si la resource n'existe pas.
	 */
	public final static InputStream getResourceAsStream (final String name) {
		return Thread.currentThread().getContextClassLoader().getResourceAsStream(name);
	}

	/**
	 * Returns a file to access the specified resource.
	 *
	 * @param name
	 *            The resource name.
	 * @return A file object to access the resource.
	 */
	public final static File getResourceAsFile (final String name) {
		return new File(getResource(name).getPath());
	}

	/**
	 * Permet de mettre à jour la valeur associée à la clé de propriété
	 * spécifiée.
	 *
	 * @param key
	 *            Clé de propriété.
	 * @param value
	 *            Valeur associée à la clé spécifiée.
	 * @return Valeur associée à la clé avant modification.
	 */
	public String setProperty (final String key, final String value) {
		if (value != null && !value.isEmpty()) {
			if (this.configuration.containsKey(key)) {
				String oldValue = this.getString(key);
				this.configuration.setProperty(key, value);
				return oldValue;
			}
			throw new IllegalArgumentException("Key does not exist : " + key);
		}
		throw new IllegalArgumentException("Value cannot be null or empty");
	}

	/**
	 * Renvoie la chaîne de texte associée à la clé de propriété spécifiée. Si
	 * la clé n'existe pas alors une exception de type
	 * {@link IllegalArgumentException} est lancée.
	 *
	 * @param key
	 *            Clé de propriété.
	 * @return Chaîne de texte associée à la clé spécifiée.
	 */
	public String getString (final String key) {
		if (this.configuration.containsKey(key)) {
			return this.configuration.getString(key);
		}
		throw new IllegalArgumentException("Key does not exist : " + key);
	}

	/**
	 * Renvoie la valeur de type <code>int</code> associée à la clé de propriété
	 * spécifiée. Si la clé n'existe pas, alors une exception de type
	 * {@link IllegalArgumentException} est lancée. Si la valeur ne peut être
	 * parsée, alors une exception de type {@link NumberFormatException} est
	 * lancée.
	 *
	 * @param key
	 *            Clé de propriété.
	 * @return Valeur de type <code>int</code> associée à la clé spécifiée
	 */
	public int getInt (final String key) {
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

	/**
	 * Renvoie une couleur de type {@link Color} construite à partir de la
	 * valeur associée à la clé de propriété spécifiée. Si la clé n'existe pas,
	 * alors une exception de type {@link IllegalArgumentException} est lancée.
	 * Si la valeur ne peut être parsée, alors une exception de type
	 * {@link IllegalArgumentException} est lancée.
	 *
	 * @param key
	 *            Clé de propriété.
	 * @return Nouvelle instance de {@link Color} construite à partir de la
	 *         valeur de propriété récupérée.
	 */
	public Color getColor (final String key) {
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

	/**
	 * Renvoie une icône instanciée à partir du chemin récupéré grâce à la clé
	 * de propriété spécifiée. Si la clé n'existe pas, alors une exception du
	 * type {@link IllegalArgumentException} est lancée.
	 *
	 * @param key
	 *            Clé de propriété.
	 * @return Nouvelle instance de {@link ImageIcon}.
	 */
	public ImageIcon getIcon (final String key) {
		String filename = getString(key);
		String path = IMG_PATH + slash + filename;
		URL url = getResource(path);
		if (url == null) {
			throw new IllegalStateException("The file associated with key '" + key
					+ "' does not exist or the invoker doesn't have adequate  privileges to get the resource");
		}
		return new ImageIcon(url);
	}
}