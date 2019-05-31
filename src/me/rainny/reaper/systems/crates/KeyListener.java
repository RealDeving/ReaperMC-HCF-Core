package me.rainny.reaper.systems.crates;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import com.doctordark.utils.InventoryUtils;

import me.rainny.reaper.HCF;
import me.rainny.reaper.eventutils.EventType;

import java.util.List;

/**
 * Listener that listens to when an {@link Key} has been used.
 */
public class KeyListener implements Listener {

    private final HCF plugin;

    public KeyListener(HCF plugin) {
        this.plugin = plugin;
    }

    // Prevent placing the keys on blocks.
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBlockPlace(BlockPlaceEvent event) {
        Key key = plugin.getKeyManager().getKey(event.getItemInHand());
        if (key != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onInventoryDrag(InventoryDragEvent event) {
        Inventory inventory = event.getInventory();
        Inventory topInventory = event.getView().getTopInventory();
        if (inventory != null && topInventory != null && topInventory.equals(inventory) && topInventory.getTitle().endsWith(" Key Reward")) { // TODO: More reliable
            event.setCancelled(true);
        }
    }

    private void decrementHand(Player player) {
        ItemStack stack = player.getItemInHand();
        if (stack.getAmount() <= 1) {
            player.setItemInHand(new ItemStack(Material.AIR, 1));
        } else {
            stack.setAmount(stack.getAmount() - 1);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();
        ItemStack stack = event.getItem();

        // Keys can only be used by right clicking blocks.
        if (action != Action.RIGHT_CLICK_BLOCK)
            return;

        Key key = plugin.getKeyManager().getKey(stack);

        // No keys were used in the making of this video.
        if (key == null)
            return;

        Block block = event.getClickedBlock();
        BlockState state = block.getState();
        if (key instanceof EventKey && state instanceof Chest) {
            EventKey eventKey = (EventKey) key;
            EventKey.EventKeyData eventKeyData = eventKey.getData(stack.getItemMeta().getLore());
            EventType eventType = eventKeyData.getEventType();
            List<Inventory> inventories = eventKey.getInventories(eventType);
            int inventoryNumber = eventKeyData.getInventoryNumber();

            if (inventories.size() < inventoryNumber) {
                player.sendMessage(ChatColor.RED + "This key is for " + eventType.getDisplayName() + ChatColor.RED + " loottable " + inventoryNumber + ", whilst there are only " + inventories.size()
                        + " possible. Please inform an admin.");

                return;
            }

            Inventory inventory = inventories.get(inventoryNumber - 1);
            ItemStack[] contents = inventory.getContents();

            Chest chest = (Chest) state;
            InventoryHolder inventoryHolder = chest.getInventory().getHolder();
            if (inventoryHolder instanceof DoubleChestInventory) {
                inventoryHolder = ((DoubleChestInventory) inventoryHolder).getHolder();
            }

            if (contents.length > chest.getInventory().getSize()) {
                player.sendMessage(ChatColor.RED + "This single chest is too small to fit the contents of this key.");
                return;
            }

            Inventory chestInventory = inventoryHolder.getInventory();

            if (!InventoryUtils.isEmpty(chestInventory)) {
                player.sendMessage(ChatColor.RED + "This chest is not empty.");
                return;
            }

            chestInventory.setContents(inventory.getContents());
            decrementHand(player);
            event.setCancelled(true);

            player.openInventory(chestInventory);
            player.sendMessage(ChatColor.GRAY + "Your " + ChatColor.AQUA + eventType.getDisplayName() + ' ' + eventKey.getDisplayName() + ChatColor.GRAY + " key has transferred loot "
                    + inventoryNumber + ChatColor.GRAY + " to the chest.");
        }
    }
}