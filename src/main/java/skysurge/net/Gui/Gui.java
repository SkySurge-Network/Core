package skysurge.net.Gui;


import java.util.*;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import skysurge.net.Main;

public class Gui implements Listener {
    private final GuiPage[] pages = new GuiPage[100];
    private Map<UUID, GuiPage> viewing = new HashMap<>();
    private final Plugin plugin;
    private final Listener li;

    // Quantity of pages
    public int size = 0;

    public Gui(Plugin plugin) {
        this.plugin = plugin;

        // Event Listener
        this.li = new Listener() {
            @EventHandler
            public void onPluginDisable(PluginDisableEvent event) {
                for (Player p : getViewers())
                    close(p);
            }

            @EventHandler
            public void onInventoryClick(InventoryClickEvent e) {
                Player p = (Player) e.getWhoClicked();
                if (viewing.containsKey(p.getUniqueId())) {
                    if (!viewing.get(p.getUniqueId()).i.equals(e.getView().getTopInventory()))
                        return;
                    if (viewing.get(p.getUniqueId()).shift && e.getClick().isShiftClick()) {
                        e.setCancelled(true);
                        p.updateInventory();
                    }
                    if (viewing.get(p.getUniqueId()).cancel
                            && viewing.get(p.getUniqueId()).i.equals(e.getClickedInventory())) {
                        e.setCancelled(true);
                        p.updateInventory();
                    }
                    if (viewing.get(p.getUniqueId()).click == null)
                        return;
                    viewing.get(p.getUniqueId()).click.click(e);
                    if (e.isCancelled())
                        p.updateInventory();
                }
            }

            @EventHandler
            public void onInventoryDrag(InventoryDragEvent e) {
                Player p = (Player) e.getWhoClicked();
                if (viewing.containsKey(p.getUniqueId())) {
                    if (!viewing.get(p.getUniqueId()).i.equals(e.getView().getTopInventory()))
                        return;
                    if (e.getInventory().getType() != viewing.get(p.getUniqueId()).i.getType())
                        return;
                    if (viewing.get(p.getUniqueId()).click != null)
                        e.setCancelled(true);
                    if (viewing.get(p.getUniqueId()).drag == null)
                        return;
                    viewing.get(p.getUniqueId()).drag.drag(e);
                }
            }

            @EventHandler
            void onClose(InventoryCloseEvent e) {
                Player p = (Player) e.getPlayer();
                if (viewing.containsKey(p.getUniqueId())) {
                    if (!viewing.get(p.getUniqueId()).i.equals(e.getInventory()))
                        return;
                    if (viewing.get(p.getUniqueId()).close != null)
                        viewing.get(p.getUniqueId()).close.close(e);
                    viewing.remove(p.getUniqueId());
                }
            }

            @EventHandler
            public void onInventoryOpen(InventoryOpenEvent e) {
                Player p = (Player) e.getPlayer();
                if (viewing.containsKey(p.getUniqueId())) {
                    if (!viewing.get(p.getUniqueId()).i.equals(e.getInventory()))
                        return;
                    if (viewing.get(p.getUniqueId()).open != null)
                        viewing.get(p.getUniqueId()).open.open(e);
                }
            }

        };
    }

    // Manually open a gui page to a player
    public Gui show(Player p, int page) {
        this.close(p);
        p.updateInventory();
        Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
            if (viewing.size() == 0)
                Bukkit.getPluginManager().registerEvents(this.li, plugin);
            this.viewing.put(p.getUniqueId(), pages[page]);
            p.openInventory(pages[page].i);
        },2L);
        return this;
    }

    // Manually close a players inventory
    public Gui close(Player p) {
        p.closeInventory();
        this.viewing.remove(p.getUniqueId());
        if (viewing.size() == 0)
            HandlerList.unregisterAll(this.li);
        return this;
    }

    // List of players viewing the current gui not specific to pages
    public List<Player> getViewers() {
        System.out.println("viewers");
        List<Player> viewers = new ArrayList<>();
        for (UUID u : this.viewing.keySet())
            viewers.add(Bukkit.getPlayer(u));
        return viewers;
    }

    // Create a page inventory
    public NoobPage create(String name, int size) {
        NoobPage page = new NoobPage(name, size);
        pages[this.size] = page;
        this.size += 1;
        return page;
    }

    public GuiPage getViewerPage(Player p) {
        return viewing.get(p.getUniqueId());
    }

    public GuiPage createTemplate(String name, int size) {
        return new GuiPage(color(name)[0], size);
    }


    public NoobPage create(GuiPage template, int amount, ItemStack... items) {
        for (int i = 0; i <= items.length / amount; i++) {
            GuiPage page = template.clone().addItems(subArray(items, amount * i, amount * i + amount - 1));
            pages[this.size] = new NoobPage(page);
            this.size += 1;
        }
        return (NoobPage) pages[this.size - 1];
    }

    public NoobPage create(GuiPage template) {
        GuiPage page = template.clone();
        pages[this.size] = new NoobPage(page);
        this.size += 1;
        return (NoobPage) pages[0];
    }


    // Macro for getting a subarray
    public static <T> T[] subArray(T[] array, int beg, int end) {
        return Arrays.copyOfRange(array, beg, end + 1);
    }

    /**
     * Create many pages based on Itemstack[] size
     *
     * @param name   the title for the page
     * @param size   the page inventory size: 1 = dispenser, 5 = hopper, 9-54 =
     *               default 9 x size / 9
     * @param amount the quantity of items from the array per page
     * @param items  the itemstack array to split for pages
     * @return First created page
     */
    public NoobPage create(String name, int size, int amount, ItemStack... items) {
        for (int i = 0; i <= items.length / amount; i++) {
            NoobPage page = new NoobPage(name, size, subArray(items, amount * i, amount * i + amount - 1));
            pages[this.size] = page;
            this.size += 1;
        }
        return (NoobPage) pages[0];
    }

    public NoobPage getPage(int page) {
        return (NoobPage) pages[Math.max(0, Math.min(this.size - 1, page))];
    }

    // Opens the next page for a player
    public Gui nextPage(Player p) {
        return openPage(p, this.viewing.get(p.getUniqueId()).page + 1);
    }

    // Opens the previous page for a player
    public Gui prevPage(Player p) {
        return openPage(p, this.viewing.get(p.getUniqueId()).page - 1);
    }

    // Manually open a certain page for a player
    public Gui openPage(Player p, int page) {
        int to = Math.max(0, Math.min(this.size - 1, page));
        if (!this.viewing.get(p.getUniqueId()).equals(pages[to]))
            show(p, to);
        return this;
    }

    //Christos Naming Version of GUIPage
    public class NoobPage extends GuiPage {

        public NoobPage(String name, int size, ItemStack... items) {
            super(name, size, items);
        }

        // Disable the ability to click items in the page
        public NoobPage c() {
            super.noClick();
            return this;
        }

        // Disable the ability to shift items into the page
        public NoobPage s() {
            super.noShift();
            return this;
        }

        public NoobPage cl() {
            super.clear();
            return this;
        }

        public NoobPage clR(int row) {
            super.clearRow(row);
            return this;
        }

        public NoobPage a(ItemStack item) {
            super.addItem(item);
            return this;
        }

        public NoobPage a(Material item) {
            super.addItem(item);
            return this;
        }

        public NoobPage a(ItemStack item, String name, String... lore) {
            super.addItem(getItem(item, name, lore));
            return this;
        }

        public NoobPage a(Material item, String name, String... lore) {
            super.addItem(getItem(new ItemStack(item), name, lore));
            return this;
        }

        public NoobPage a(ItemStack... items) {
            super.addItems(items);
            return this;
        }

        public NoobPage a(Material... items) {
            super.addItems(items);
            return this;
        }

        public NoobPage i(int position, ItemStack item) {
            super.setItem(position, item);
            return this;
        }

        public NoobPage i(int position, Material item) {
            super.setItem(position, item);
            return this;
        }

        public NoobPage i(int position, ItemStack item, String name, String... lore) {
            super.setItem(position, getItem(item, name, lore));
            return this;
        }

        public NoobPage i(int position, Material item, String name, String... lore) {
            super.setItem(position, getItem(new ItemStack(item), name, lore));
            return this;
        }

        public NoobPage i(int position, Material item, String name, List<String> lore) {
            super.setItem(position, getItem(new ItemStack(item), name, lore));
            return this;
        }

        public NoobPage i(int position, Material item, short data, int amount, String name, List<String> lore) {
            super.setItem(position, getItem(new ItemStack(item, amount, data), name, lore));
            return this;
        }

        public NoobPage i(int row, int column, ItemStack item) {
            super.setItem(column + row * 9, item);
            return this;
        }

        public NoobPage i(int row, int column, Material item) {
            super.setItem(column + row * 9, item);
            return this;
        }

        public NoobPage i(int row, int column, ItemStack item, String name, String... lore) {
            super.setItem(column + row * 9, getItem(item, name, lore));
            return this;
        }

        public NoobPage i(int row, int column, Material item, String name, String... lore) {
            super.setItem(column + row * 9, getItem(new ItemStack(item), name, lore));
            return this;
        }

        public NoobPage i(ItemStack item, int... slots) {
            super.setItems(item, slots);
            return this;
        }

        public NoobPage i(Material item, int... slots) {
            super.setItems(item, slots);
            return this;
        }


        public NoobPage i(List<Integer> slots, ItemStack item) {
            super.setItems(slots, item);
            return this;
        }

        public NoobPage i(List<Integer> slots, Material item) {
            super.setItems(slots, item);
            return this;
        }

        public NoobPage i(List<Integer> slots, ItemStack item, String name, String... lore) {
            super.setItems(slots, item, name, lore);
            return this;
        }

        public NoobPage i(List<Integer> slots, Material item, String name, String... lore) {
            super.setItems(slots, item, name, lore);
            return this;
        }

        public NoobPage fc(int column, ItemStack item) {
            super.fillColumn(column, item);
            return this;
        }

        public NoobPage fc(int column, Material item) {
            super.fillColumn(column, new ItemStack(item));
            return this;
        }

        public NoobPage fc(int column, ItemStack item, String name, String... lore) {
            super.fillColumn(column, getItem(item, name, lore));
            return this;
        }

        public NoobPage fc(int column, Material item, String name, String... lore) {
            super.fillColumn(column, getItem(new ItemStack(item), name, lore));
            return this;
        }

        public NoobPage fr(int row, ItemStack item) {
            super.fillRow(row, item);
            return this;
        }

        public NoobPage fr(int row, Material item) {
            super.fillRow(row, item);
            return this;
        }

        public NoobPage fr(int row, ItemStack item, String name, String... lore) {
            super.fillRow(row, item, name, lore);
            return this;
        }

        public NoobPage fr(int row, Material item, String name, String... lore) {
            super.fillRow(row, item, name, lore);
            return this;
        }

        public NoobPage f(ItemStack item) {
            super.fill(item);
            return this;
        }

        public NoobPage f(Material item) {
            super.fill(item);
            return this;
        }

        public NoobPage f(ItemStack item, String name, String... lore) {
            super.fill(getItem(item, name, lore));
            return this;
        }

        public NoobPage f(Material item, String name, String... lore) {
            super.fill(getItem(new ItemStack(item), name, lore));
            return this;
        }

        public NoobPage onClick(clickEvent event) {
            super.onClick(event);
            return this;
        }

        public NoobPage onClose(closeEvent event) {
            super.onClose(event);
            return this;
        }

        public NoobPage onOpen(openEvent event) {
            super.onOpen(event);
            return this;
        }

        public NoobPage onDrag(dragEvent event) {
            super.onDrag(event);
            return this;
        }

        public NoobPage(GuiPage template) {
            super(template);
        }

    }

    public class GuiPage {
        public final int size;
        public final int page;
        public final String name;
        public final Inventory i;
        private clickEvent click;
        private openEvent open;
        private closeEvent close;
        private dragEvent drag;
        protected boolean cancel = false;
        protected boolean shift = false;

        public GuiPage(String name, int size, ItemStack... items) {
            this.name = name;
            this.i = getInventory(name, size);
            this.size = i.getSize();
            this.i.setContents(items);
            this.page = Gui.this.size;
        }

        public GuiPage(GuiPage template) {
            this.name = template.name;
            this.i = getInventory(template.name, template.size);
            this.size = this.i.getSize();
            this.i.setContents(template.getContents());
            this.page = Gui.this.size;
            this.cancel = template.cancel;
            this.shift = template.shift;
            this.onClick(template.getClick());
            this.onDrag(template.getDrag());
            this.onClose(template.getClose());
            this.onOpen(template.getOpen());
        }

        public String getName() {
            return name;
        }

        public int getSize() {
            return size;
        }

        public ItemStack[] getContents() {
            return i.getContents();
        }

        public GuiPage setContents(ItemStack[] contents) {
            i.setContents(contents);
            return this;
        }

        // Disable the ability to click items in the page
        public GuiPage noClick() {
            this.cancel = true;
            return this;
        }

        // Disable the ability to shift items into the page
        public GuiPage noShift() {
            this.shift = true;
            return this;
        }

        public GuiPage addItem(ItemStack item) {
            if (i.firstEmpty() == -1) {
                return this;
            }
            setItem(i.firstEmpty(), item);
            return this;
        }

        public GuiPage clear() {
            i.setContents(new ItemStack[i.getContents().length]);
            return this;
        }

        public GuiPage clone() {
            return new GuiPage(this);
        }

        public GuiPage addItem(Material item) {
            setItem(i.firstEmpty(), new ItemStack(item));
            return this;
        }

        public GuiPage addItem(ItemStack item, String name, String... lore) {
            return addItem(getItem(item, name, lore));
        }

        public GuiPage addItem(Material item, String name, String... lore) {
            return addItem(getItem(new ItemStack(item), name, lore));
        }

        public GuiPage addItem(Material item, String name, List<String> lore) {
            return addItem(getItem(new ItemStack(item), name, lore));
        }

        public GuiPage setItem(int position, ItemStack item) {
            i.setItem(position, item);
            return this;
        }

        public GuiPage setItem(int position, Material item) {
            return setItem(position, new ItemStack(item));
        }

        public GuiPage setItem(int position, ItemStack item, String name, String... lore) {
            return setItem(position, getItem(item, name, lore));
        }

        public GuiPage setItem(int position, ItemStack item, String name, List<String> lore) {
            return setItem(position, getItem(item, name, lore));
        }

        public GuiPage setItem(int position, Material item, String name, String... lore) {
            return setItem(position, getItem(new ItemStack(item), name, lore));
        }

        public GuiPage setItem(int row, int column, ItemStack item) {
            return setItem(column + row * 9, item);
        }

        public GuiPage setItem(int row, int column, Material item) {
            return setItem(column + row * 9, item);
        }

        public GuiPage setItem(int row, int column, ItemStack item, String name, String... lore) {
            return setItem(column + row * 9, getItem(item, name, lore));
        }

        public GuiPage setItem(int row, int column, Material item, String name, String... lore) {
            return setItem(column + row * 9, getItem(new ItemStack(item), name, lore));
        }

        public GuiPage fillColumn(int column, ItemStack item) {
            for (int i = 0; i < 9; i++) {
                if (i * 9 + column >= this.i.getSize())
                    break;
                if (this.i.getContents()[i * 9 + column] == null)
                    setItem(i * 9 + column, item);
            }
            return this;
        }

        public GuiPage fillColumn(int column, Material item) {
            return fillColumn(column, new ItemStack(item));
        }

        public GuiPage fillColumn(int column, ItemStack item, String name, String... lore) {
            return fillColumn(column, getItem(item, name, lore));
        }

        public GuiPage fillColumn(int column, Material item, String name, String... lore) {
            return fillColumn(column, getItem(new ItemStack(item), name, lore));
        }

        public GuiPage fillRow(int row, ItemStack item) {
            for (int i = 0; i < 9; i++) {
                if (row * 9 + i >= this.i.getSize())
                    break;
                if (this.i.getContents()[row * 9 + i] == null)
                    setItem(row * 9 + i, item);
            }
            return this;
        }

        public GuiPage fillRow(int row, Material item) {
            return fillRow(row, new ItemStack(item));
        }

        public GuiPage fillRow(int row, ItemStack item, String name, String... lore) {
            return fillRow(row, getItem(item, name, lore));
        }

        public GuiPage fillRow(int row, Material item, String name, String... lore) {
            return fillRow(row, getItem(new ItemStack(item), name, lore));
        }

        public GuiPage addItems(ItemStack... items) {
            for (ItemStack item : items)
                if (item != null)
                    addItem(item);
            return this;
        }

        public GuiPage addItems(Material... items) {
            for (Material item : items)
                if (item != null)
                    addItem(item);
            return this;
        }

        public GuiPage fill(ItemStack item) {
            for (int i = 0; i < this.i.getSize(); i++)
                if (this.i.getContents()[i] == null)
                    setItem(i, item);
            return this;
        }

        public GuiPage fill(Material item) {
            return fill(new ItemStack(item));
        }

        public GuiPage fill(ItemStack item, String name, String... lore) {
            return fill(getItem(item, name, lore));
        }

        public GuiPage fill(Material item, String name, String... lore) {
            return fill(getItem(new ItemStack(item), name, lore));
        }

        public GuiPage setItems(ItemStack item, int... slots) {
            for (int i = 0; i < slots.length; i++)
                this.i.setItem(slots[i], item);
            return this;
        }

        public GuiPage setItems(Material item, int... slots) {
            return setItems(new ItemStack(item), slots);
        }

        public GuiPage setItems(List<Integer> slots, ItemStack item) {
            for (int slot : slots)
                setItem(slot, item);
            return this;
        }

        public GuiPage setItems(List<Integer> slots, Material item) {
            return setItems(slots, new ItemStack(item));
        }

        public GuiPage setItems(List<Integer> slots, ItemStack item, String name, String... lore) {
            for (int slot : slots)
                setItem(slot, item, name, lore);
            return this;
        }

        public GuiPage setItems(List<Integer> slots, Material item, String name, String... lore) {
            return setItems(slots, getItem(new ItemStack(item), name, lore));
        }

        public GuiPage clearRow(int row) {
            for (int i = 0; i < 9; i++) {
                if (row * 9 + i >= this.i.getSize())
                    break;
                if (this.i.getContents()[row * 9 + i] != null)
                    setItem(row * 9 + i, new ItemStack(Material.AIR));
            }
            return this;
        }

        public GuiPage onClick(clickEvent event) {
            this.click = event;
            return this;
        }

        public GuiPage onClose(closeEvent event) {
            this.close = event;
            return this;
        }

        public GuiPage onOpen(openEvent event) {
            this.open = event;
            return this;
        }

        public GuiPage onDrag(dragEvent event) {
            this.drag = event;
            return this;
        }

        public clickEvent getClick() {
            return click;
        }

        public openEvent getOpen() {
            return open;
        }

        public closeEvent getClose() {
            return close;
        }

        public dragEvent getDrag() {
            return drag;
        }
    }

    public ItemStack getItem(ItemStack item, String name, String... lore) {
        ItemMeta im = item.getItemMeta();
        if (name != null)
            im.setDisplayName(color(name)[0]);
        im.setLore(Arrays.asList(color(lore)));
        item.setItemMeta(im);
        return item;
    }

    public ItemStack getItem(ItemStack item, String name, List<String> lore) {
        ItemMeta im = item.getItemMeta();
        if (name != null)
            im.setDisplayName(color(name)[0]);
        im.setLore(lore);
        item.setItemMeta(im);
        return item;
    }


    public String[] color(String... strings) {
        String[] s = new String[strings.length];
        for (int i = 0; i < strings.length; i++) {
            if (strings[i] != null)
                s[i] = ChatColor.translateAlternateColorCodes('&', strings[i]);
        }
        return s;
    }

    public Inventory getInventory(String name, int size) {
        if (size == 1) {
            return Bukkit.createInventory(null, InventoryType.DROPPER, name);
        } else if (size == 5) {
            return Bukkit.createInventory(null, InventoryType.HOPPER, name);
        } else {
            return Bukkit.createInventory(null, Math.min(54, Math.max(9, (int) Math.ceil(size / 9) * 9)), name);
        }
    }

    public static interface clickEvent {
        void click(InventoryClickEvent e);
    }

    public static interface closeEvent {
        void close(InventoryCloseEvent e);
    }

    public static interface dragEvent {
        void drag(InventoryDragEvent e);
    }

    public static interface openEvent {
        void open(InventoryOpenEvent e);
    }
}