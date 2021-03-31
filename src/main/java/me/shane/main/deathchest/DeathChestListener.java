package me.shane.main.deathchest;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;


public class DeathChestListener implements Listener
{
    private final List<Material> chestable_blocks;
    private final DeathChest plugin;

    public DeathChestListener(DeathChest P, List<Material> cb) {
        plugin=P;
        chestable_blocks=cb;
    }

    public DeathChestListener(DeathChest P) {
        // This default constructor should never be used
        // If it is there are no chestable block types
        // (chests can only be placed in empty/air blocks)
        plugin = P;
        chestable_blocks = new ArrayList<Material>();
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event)
    {
        Player player = event.getEntity();
        Location deathLocation = player.getLocation();
        int numDrops = event.getDrops().size();
        Inventory inventory;

        if ( plugin.msgLocation() ) {
            player.sendMessage("§c§lYou died at §e" +
                    deathLocation.getBlockX() + ",  " +
                    deathLocation.getBlockY() + ",  " +
                    deathLocation.getBlockZ() + "Use /back to obtain your chest");
        }

        if (numDrops > 0 ) {
            // Create an inventory big enough to hold all the dropped
            // items and put the drops in it.
            // (Inventory sizes must be a multiple of 9 so round up.)
            inventory = plugin.getServer().createInventory(null,
                    (((numDrops + 8) / 9) * 9) );
            for (ItemStack item : event.getDrops()) {
                inventory.addItem(item);
            }

            plugin.getServer().getScheduler().runTaskLater((Plugin)plugin,
                    (Runnable) new DeathChestTask(player, inventory,
                            deathLocation, chestable_blocks, plugin.debug()),
                    plugin.delay());
            event.getDrops().clear();
        }
    }
}