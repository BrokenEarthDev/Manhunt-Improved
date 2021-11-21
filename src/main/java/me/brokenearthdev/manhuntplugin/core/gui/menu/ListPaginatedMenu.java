package me.brokenearthdev.manhuntplugin.core.gui.menu;

import me.brokenearthdev.manhuntplugin.core.gui.buttons.Button;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Represents a paginated menu in which there are a list of items.
 * The items are to be displayed horizontally, and if the number of
 * entries exceeds the number of slots, a new page will be created.
 *
 * @param <T> The type
 */
public class ListPaginatedMenu<T> extends PaginatedMenu {

    protected final List<BiConsumer<T, InventoryClickEvent>> onItemClickList = new ArrayList<>();
    protected List<T> list;
    protected Function<T, ItemStack> function;
    protected Map<T, ItemStack> map = new HashMap<>();
    
    /**
     * Creates a new menu.
     *
     * @param title Menu title
     */
    public ListPaginatedMenu(String title, List<T> collection, Function<T, ItemStack> function) {
        super(title, (int) Math.ceil(collection.size() / 9.0));
        list = collection;
        this.function = function;
    }

    @Override
    public void display(HumanEntity entity) {
        for (int i = 0; i < list.size(); i++) {
            T t = list.get(i);
            ItemStack stack = (map.containsKey(t) ? map.get(t) : function.apply(t));
            Button button = new Button(i, stack);
            
            button.addAction(action -> {
                onItemClickList.forEach(event -> event.accept(t, action));
            });
            this.setButton(button);
        }
        super.display(entity);
    }

    /**
     * @param onItemClick The bi-consumer
     */
    public void addOnItemClick(BiConsumer<T, InventoryClickEvent> onItemClick) {
        onItemClickList.add(onItemClick);
    }
    
    public void setItem(T t, ItemStack stack) {
        map.put(t, stack);
        list.add(t);
    }

    
}
