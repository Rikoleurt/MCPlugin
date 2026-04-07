package my.hns.manager;

import my.hns.Main;
import org.bukkit.inventory.ItemStack;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemManager {
    List<ItemStack> items;

    public ItemManager() {
        items = new ArrayList<>();
    }

    public void addItem(ItemStack item) {
        items.add(item);
    }

    public void removeItem(ItemStack item) {
        items.remove(item);
    }

    public List<ItemStack> getItems() {
        return items;
    }

    public void modifyItem(ItemStack item, ItemStack newItem) {
        items.set(items.indexOf(item), newItem);
    }

    public ItemStack getItem(int index) {
        return items.get(index);
    }

    public void addAll(ItemStack[] itemArray) {
        if(itemArray.length <= 5) items.addAll(Arrays.asList(itemArray));
        else Main.instance.getLogger().warning("Items have to be less than 5 Items");
    }
}
