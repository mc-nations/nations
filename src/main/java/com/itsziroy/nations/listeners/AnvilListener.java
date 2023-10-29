package com.itsziroy.nations.listeners;

import com.itsziroy.nations.Config;
import com.itsziroy.nations.Nations;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

public class AnvilListener implements Listener {

    private final Nations plugin;

    public AnvilListener(Nations plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onAnvilEvent(PrepareAnvilEvent event) {
        if(plugin.getConfig().getBoolean(Config.Path.DISABLE_MENDING)) {
            AnvilInventory anvilInventory = event.getInventory();

            for (ItemStack itemStack : anvilInventory.getContents()) {
                if (itemStack != null) {
                    if (itemStack.getType() == Material.ENCHANTED_BOOK) {
                        EnchantmentStorageMeta bookMeta = (EnchantmentStorageMeta) itemStack.getItemMeta();
                        if (bookMeta != null) {
                            if (bookMeta.hasStoredEnchant(Enchantment.MENDING)) {
                                event.setResult(null);
                            }
                        }
                    }
                }
            }
        }
    }
}
