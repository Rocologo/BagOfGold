package one.lindegaard.MobHunting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class IconMenu implements Listener {
	 
    private String name;
    private int size;
    private OnClick click;
    List<String> viewing = new ArrayList<String>();
 
    private ItemStack[] items;
 
    public IconMenu(String name, int size, OnClick onClick) {
        this.name = name;
        this.size = size * 9;
        items = new ItemStack[this.size];
        this.click = onClick;
        Bukkit.getPluginManager().registerEvents(this, Bukkit.getPluginManager().getPlugins()[0]);
    }
 
    @EventHandler
    public void onPluginDisable(PluginDisableEvent event) {
        for (Player p : this.getViewers())
            close(p);
    }
 
    public IconMenu open(Player p) {
        p.openInventory(getInventory(p));
        viewing.add(p.getName());
        return this;
    }
 
    private Inventory getInventory(Player p) {
        Inventory inv = Bukkit.createInventory(p, size, name);
        for (int i = 0; i < items.length; i++)
            if (items[i] != null)
                inv.setItem(i, items[i]);
        return inv;
    }
 
    public IconMenu close(Player p) {
        if (p.getOpenInventory().getTitle().equals(name))
            p.closeInventory();
        return this;
    }
 
    @SuppressWarnings("deprecation")
	public List<Player> getViewers() {
        List<Player> viewers = new ArrayList<Player>();
        for (String s : viewing)
            viewers.add(Bukkit.getPlayer(s));
        return viewers;
    }
 
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (viewing.contains(event.getWhoClicked().getName())) {
            event.setCancelled(true);
            Player p = (Player) event.getWhoClicked();
            Row row = getRowFromSlot(event.getSlot());
            if (!click.click(p, this, row, event.getSlot() - row.getRow() * 9, event.getCurrentItem()))
                close(p);
        }
    }
 
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (viewing.contains(event.getPlayer().getName()))
            viewing.remove(event.getPlayer().getName());
    }
 
    public IconMenu addButton(Row row, int position, ItemStack item, String name, String... lore) {
        items[row.getRow() * 9 + position] = getItem(item, name, lore);
        return this;
    }
 
    public Row getRowFromSlot(int slot) {
        return new Row(slot / 9, items);
    }
 
    public Row getRow(int row) {
        return new Row(row, items);
    }
 
    public interface OnClick {
        public abstract boolean click(Player clicker, IconMenu menu, Row row, int slot, ItemStack item);

    }
 
    public class Row {
        private ItemStack[] rowItems = new ItemStack[9];
        int row;
 
        public Row(int row, ItemStack[] items) {
            this.row = row;
            int j = 0;
            for (int i = (row * 9); i < (row * 9) + 9; i++) {
                rowItems[j] = items[i];
                j++;
            }
        }
 
        public ItemStack[] getRowItems() {
            return rowItems;
        }
 
        public ItemStack getRowItem(int item) {
            return rowItems[item] == null ? new ItemStack(Material.AIR) : rowItems[item];
        }
 
        public int getRow() {
            return row;
        }
    }
 
    private ItemStack getItem(ItemStack item, String name, String... lore) {
        ItemMeta im = item.getItemMeta();
        im.setDisplayName(name);
        im.setLore(Arrays.asList(lore));
        item.setItemMeta(im);
        return item;
    }
    
    @SuppressWarnings("unused")
	private void exampleOfUse(Player p){
    	
        IconMenu menu = new IconMenu("IconMenu", 2, new OnClick() {
            @Override
            public boolean click(Player p, IconMenu menu, Row row, int slot, ItemStack item) {
                if(row.getRow() == 1){
                    Bukkit.broadcastMessage(row.getRowItem(slot).getType().name());
                }
                return true;
            }
        });
        menu.addButton(menu.getRow(1), 0, new ItemStack(Material.STONE), "Stone Button ;)");
        menu.addButton(menu.getRow(1), 1, new ItemStack(Material.WOOD), "Wood Button ;)");
        menu.addButton(menu.getRow(1), 2, new ItemStack(Material.DIAMOND), "Diamond Button ;)");
        menu.addButton(menu.getRow(1), 3, new ItemStack(Material.GOLD_BLOCK), "Gold Button ;)");
        menu.addButton(menu.getRow(1), 4, new ItemStack(Material.IRON_BLOCK), "Iron Button ;)");
        menu.addButton(menu.getRow(1), 5, new ItemStack(Material.OBSIDIAN), "Obby Button ;)");
        menu.addButton(menu.getRow(1), 6, new ItemStack(Material.ANVIL), "Anvil Button ;)");
        menu.addButton(menu.getRow(1), 7, new ItemStack(Material.STONE_BUTTON), "Button Button ;)");
        menu.addButton(menu.getRow(1), 8, new ItemStack(Material.PORTAL), "Portal Button ;)");
        menu.open(p.getPlayer());
    }
 
}
