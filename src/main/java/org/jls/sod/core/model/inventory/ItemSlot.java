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

package org.jls.sod.core.model.inventory;

import org.jls.sod.core.model.item.Item;

/**
 * Represents an item in the inventory. It differs from the {@link Item} which
 * contains only the description of the specified item, whereas {@link ItemSlot}
 * represents the specified item in the inventory and contains for example the
 * quantity.
 * 
 * @author LE SAUCE Julien
 * @date Nov 26, 2015
 */
public class ItemSlot {

	private final Item item;

	private int quantity;

	/**
	 * Instantiates a new item slot.
	 * 
	 * @param item
	 *            The {@link Item} instance associated with this slot.
	 */
	public ItemSlot (final Item item) {
		this.item = item;
		this.quantity = 1;
	}

	@Override
	public String toString () {
		return "ItemSlot [" + item + ", quantity=" + quantity + "]";
	}

	/**
	 * Instantiates a new item slot.
	 * 
	 * @param item
	 *            The {@link Item} instance associated with this slot.
	 * @param quantity
	 *            The quantity of items.
	 */
	public ItemSlot (final Item item, int quantity) {
		this.item = item;
		this.quantity = quantity;
	}

	public int getQuantity () {
		return quantity;
	}

	public void setQuantity (int quantity) {
		this.quantity = quantity;
	}

	/**
	 * Increments the current item's quantity by the specified number of items.
	 * 
	 * @param incValue
	 *            The number of items to add to this slot. If the increment is
	 *            lesser or equal to zero, an {@link IllegalArgumentException}
	 *            is thrown.
	 */
	public void incrementQuantity (int incValue) {
		if (incValue <= 0) {
			throw new IllegalArgumentException("Increment must be a positive integer : " + incValue);
		}
		this.quantity += incValue;
	}

	/**
	 * Decrements the current item's quantity by the specified number of items.
	 * 
	 * @param decValue
	 *            The number of items to remove from this slot. If the decrement
	 *            is lesser or equal to zero, an
	 *            {@link IllegalArgumentException} is thrown.
	 */
	public void decrementQuantity (int decValue) {
		if (decValue <= 0) {
			throw new IllegalArgumentException("Decrement must be a positive integer : " + decValue);
		}
		this.quantity -= decValue;
	}

	public Item getItem () {
		return item;
	}
}