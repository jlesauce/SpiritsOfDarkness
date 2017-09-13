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

package org.jls.sod.core.model;

/**
 * Data model of a story.
 * 
 * @author LE SAUCE Julien
 * @date Sep 3, 2015
 */
public class Story {

	private final String id;
	private final String name;
	private final String description;
	private final String defaultWorld;

	/**
	 * Instantiates a new story.
	 * 
	 * @param id
	 *            Identifier of the story.
	 * @param name
	 *            Name of the story.
	 * @param desc
	 *            Description of the story.
	 * @param defWorld
	 *            Default world.
	 */
	public Story (final String id, final String name, final String desc, final String defWorld) {
		this.id = id;
		this.name = name;
		this.description = desc;
		this.defaultWorld = defWorld;
	}

	/**
	 * Returns the identifier of the story.
	 * 
	 * @return Identifier of the story.
	 */
	public String getId () {
		return this.id;
	}

	/**
	 * Returns the name of the story.
	 * 
	 * @return Name of the story.
	 */
	public String getName () {
		return this.name;
	}

	/**
	 * Returns the description of the story.
	 * 
	 * @return Description of the story.
	 */
	public String getDescription () {
		return this.description;
	}

	/**
	 * Returns the default world to load.
	 * 
	 * @return The default world to load.
	 */
	public String getDefaultWorld () {
		return this.defaultWorld;
	}
}