/*
 * * Copyright 2020 github.com/ReflxctionDev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package me.brokenearthdev.manhuntplugin.core.gui.buttons;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.BiConsumer;

/**
 * Represents a button which decreases a numeric value
 */
public class NumberDecreaseButton extends Button {

    private final Binder<Integer> value;

    /**
     * Creates a new number-decreasing button
     *
     * @param original     Original value
     * @param decreaseItem Item that decreases the value
     */
    public NumberDecreaseButton(int slot, Binder<Integer> original, ItemStack decreaseItem) {
        super(slot, decreaseItem);
        value = original;
    }
    
    public NumberDecreaseButton(int slot, int original, ItemStack decreaseItem) {
        super(slot, decreaseItem);
        value = new Binder<>();
        value.setValue(original);
    }

    public NumberDecreaseButton register(int minimum, NumberIncreaseButton increaseButton, ItemStack decreaseItem, BiConsumer<InventoryClickEvent, Integer> valueChange) {
        ItemStack increaseItem = increaseButton.getItem();
        decreaseItem.setAmount(itemVal(value.getValue()));
        addAction(event -> {
            int decrease = event.isRightClick() ? 5 : 1;
            if (value.getValue() - decrease <= minimum) {
                getItem().setAmount(minimum);
                increaseButton.getItem().setAmount(minimum);
                //increaseItem.setAmount(minimum);
                increaseButton.getValue().setValue(value.getValue()); // test this
                value.setValue(minimum);
                valueChange.accept(event, minimum);
                return;
            }
            value.setValue(value.getValue() - decrease);
            //getItem().setAmount(value.getValue() < 1 ? 1 : value.getValue() > 64 ? 1 : value.getValue() > 64 ? 1 : value.getValue());
            increaseButton.getItem().setAmount(itemVal(value.getValue()));
            increaseButton.getValue().setValue(value.getValue()); // test this
            getItem().setAmount(itemVal(value.getValue()));
            valueChange.accept(event, value.getValue());
        });
        addAction(CANCEL_ACTION);
        return this;
    }
    
    private int itemVal(int val) {
        return val  > 64 ? val % 64 == 0 ? 1 :val % 64 : Math.max(val, 1);
    }
    
    /**
     * Returns the value
     *
     * @return The value
     */
    public Binder<Integer> getValue() {
        return value;
    }
}