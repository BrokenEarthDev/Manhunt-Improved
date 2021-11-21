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
 * Represents a button which increases a numeric value
 */
public class NumberIncreaseButton extends Button {

    private final Binder<Integer> value;
    
    /**
     * Creates a new number-increase button
     *
     * @param original     Original value
     * @param increaseItem Item that increases the value
     */
    public NumberIncreaseButton(int slot, Binder<Integer> original, ItemStack increaseItem) {
        super(slot, increaseItem);
        value = original;
    }
    
    public NumberIncreaseButton(int slot, int original, ItemStack increaseItem) {
        super(slot, increaseItem);
        value = new Binder<>();
        value.setValue(original);
    }
    
    // 20 - 20 - 21 - 22 - 23
    //
    
    
    // todo REPLCHECK value.getValue() % 64 WITH 1
    public NumberIncreaseButton register(ItemStack increaseItem, NumberDecreaseButton decreaseButton, BiConsumer<InventoryClickEvent, Integer> valueChange) {
        increaseItem.setAmount(itemVal(value.getValue()));
        decreaseButton.getItem().setAmount(itemVal(value.getValue()));
        addAction(event -> {
            int increase = event.isRightClick() ? 5 : 1;
            value.setValue(value.getValue() + increase);
            getItem().setAmount(itemVal(value.getValue()));
            decreaseButton.getValue().setValue(value.getValue());
            decreaseButton.getItem().setAmount(itemVal(value.getValue()));
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