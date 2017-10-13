/*
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
 */

package org.jls.sod.core.model.inventory;

import java.util.HashMap;
import java.util.Map.Entry;

import org.jls.sod.core.model.item.Item;

/**
 * Represents the inventory as a set of {@link ItemSlot item slots}.
 * 
 * @author LE SAUCE Julien
 * @date Nov 26, 2015
 */
public class Inventory {

    private final HashMap<String, ItemSlot> slots;

    /**
     * Instantiates a new inventory.
     */
    public Inventory() {
        this.slots = new HashMap<>();
    }

    /**
     * Merges the items from the specified inventory to this one (the specified
     * inventory is updated).
     * 
     * @param inventory
     *            The inventory to merge from.
     */
    public void importInventory (final Inventory inventory) {
        HashMap<String, ItemSlot> inputSlots = inventory.getSlots();
        // Iterates over the map entries
        for (Entry<String, ItemSlot> entry : inputSlots.entrySet()) {

            // If item is already contained
            if (this.slots.containsKey(entry.getKey())) {
                // If item is stackable
                if (entry.getValue().getItem().isStackable()) {
                    // Updates the slot
                    this.slots.get(entry.getKey()).incrementQuantity(entry.getValue().getQuantity());
                } else {
                    // Transfers the slot
                    this.slots.put(entry.getKey(), entry.getValue());
                }
            } else {
                // Transfers the slot
                this.slots.put(entry.getKey(), entry.getValue());
            }
        }
        inventory.clearInventory();
    }

    /**
     * Adds the specified item to the inventory.
     * <p>
     * If the item is not carriable a {@link NotCarriableException} is thrown.
     * </p>
     * 
     * @param item
     *            The item to add.
     * @throws NotCarriableException
     *             If the specified item is not carriable.
     */
    public void addItem (final Item item) throws NotCarriableException {
        addItem(item, 1);
    }

    /**
     * Adds the specified item(s) to the inventory.
     * <p>
     * If the item is not carriable a {@link NotCarriableException} is thrown.
     * </p>
     * 
     * @param item
     *            The item to add.
     * @param quantity
     *            The quantity of this item to add.
     * @throws NotCarriableException
     *             If the specified item is not carriable.
     */
    public void addItem (final Item item, final int quantity) throws NotCarriableException {
        // Check quantity
        if (quantity <= 0) {
            throw new IllegalArgumentException("Item quantity must be a positive integer : " + quantity);
        }
        // Checks if item is carriable
        if (!item.isCarriable()) {
            throw new NotCarriableException("Item " + item.getId() + " is not carriable");
        }

        // If the item is stackable
        if (item.isStackable()) {
            // If the item is already contained
            if (this.slots.containsKey(item.getId())) {
                this.slots.get(item.getId()).incrementQuantity(quantity);
            } else {
                this.slots.put(item.getId(), new ItemSlot(item, quantity));
            }
        } else {
            // Adds all the items one by one
            for (int i = 0; i < quantity; i++) {
                this.slots.put(item.getId(), new ItemSlot(item));
            }
        }
    }

    /**
     * Removes the specified item from the inventory.
     * 
     * @param itemId
     *            Unique item identifier.
     * @return The {@link Item} instance that has been updated.
     * @throws ItemNotFoundException
     *             If the specified item is not contained in this inventory.
     * @throws InventoryQuantityException
     *             If the quantity of the specified item is too low.
     */
    public Item removeItem (final String itemId) throws ItemNotFoundException, InventoryQuantityException {
        return removeItem(itemId, 1);
    }

    /**
     * Removes the specified item(s) from the inventory.
     * 
     * @param itemId
     *            Unique item identifier.
     * @param quantity
     *            The quantity of this item to remove.
     * @return The {@link Item} instance that has been updated.
     * @throws ItemNotFoundException
     *             If the specified item is not contained in this inventory.
     * @throws InventoryQuantityException
     *             If the quantity of the specified item is too low.
     */
    public Item removeItem (final String itemId, final int quantity)
            throws ItemNotFoundException, InventoryQuantityException {
        // Check if quantity is valid
        if (quantity <= 0) {
            throw new IllegalArgumentException("Item quantity must be a positive integer : " + quantity);
        }

        // If the item is contained
        if (this.slots.containsKey(itemId)) {
            ItemSlot slot = this.slots.get(itemId);
            int newQty = slot.getQuantity() - quantity;

            // If quantity after remove is positive
            if (newQty > 0) {
                // Just decrement quantity
                slot.decrementQuantity(quantity);
                return slot.getItem();
            }
            // If quantity falls to absolute zero
            else if (newQty == 0) {
                // We have to remove this slot
                this.slots.remove(itemId);
                return slot.getItem();
            } else { // Else throw an exception
                throw new InventoryQuantityException("Not enough " + itemId + " : " + slot.getQuantity());
            }
        } else {
            throw new ItemNotFoundException();
        }
    }

    /**
     * Tells whereas this inventory contains the specified item or not.
     * 
     * @param itemId
     *            The item unique identifier.
     * @return <code>true</code> if this inventory contains the specified item,
     *         <code>false</code> otherwise.
     */
    public boolean containsItem (final String itemId) {
        return this.slots.containsKey(itemId);
    }

    /**
     * Removes all the items from this inventory.
     */
    public void clearInventory () {
        this.slots.clear();
    }

    /**
     * Returns the specified {@link ItemSlot item slot} if this inventory actually
     * contains it or <code>null</code> otherwise.
     * 
     * @param itemId
     *            The item unique identifier.
     * @return The item slot or <code>null</code> if the specified item is not
     *         contained in this inventory.
     */
    public ItemSlot getItemSlot (final String itemId) {
        return this.slots.get(itemId);
    }

    /**
     * Returns the number of occupied slots in this inventory.
     * 
     * @return The number of occupied slots in this inventory.
     */
    public int size () {
        return this.slots.size();
    }

    /**
     * Returns the items slots of this inventory.
     * 
     * @return The items slots of this inventory.
     */
    public HashMap<String, ItemSlot> getSlots () {
        return this.slots;
    }
}
