package com.deejay.lifesteal.basic;

import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class KillDeath implements Listener {

    private static final double MAX_HEALTH = 40.0; // 20 hearts
    private static final double HEART_VALUE = 2.0;

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {

        Player victim = event.getEntity();
        Player killer = victim.getKiller();

        // === REMOVE HEART FROM VICTIM (ALL DEATHS) ===
        double victimHealth = victim.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();

        double newVictimHealth = Math.max(0, victimHealth - HEART_VALUE);
        victim.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(newVictimHealth);

        // === HANDLE KILLER ===
        if (killer == null) return;

        double killerHealth = killer.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();

        // If killer NOT at max → give heart
        if (killerHealth < MAX_HEALTH) {
            double newHealth = Math.min(killerHealth + HEART_VALUE, MAX_HEALTH);
            killer.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(newHealth);
            return;
        }

        // === Killer at max → give heart item ===
        ItemStack heart = createHeart();

        if (killer.getInventory().firstEmpty() != -1) {
            killer.getInventory().addItem(heart);
        } else {
            victim.getWorld().dropItemNaturally(victim.getLocation(), heart);
        }
    }

    // Create heart item
    private ItemStack createHeart() {
        ItemStack heart = new ItemStack(Material.NETHER_STAR, 1);
        ItemMeta meta = heart.getItemMeta();
        meta.setDisplayName("§4Heart");
        heart.setItemMeta(meta);
        return heart;
    }
}
