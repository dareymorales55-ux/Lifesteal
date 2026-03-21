package com.deejay.lifesteal.basic;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SkullType;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

public class DeathBan implements Listener {

    // List of deathbanned player names
    private static final List<String> deathBannedPlayers = new ArrayList<>();

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        double maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();

        // Ban player if health reaches 0
        if (maxHealth <= 0) {
            String name = player.getName();

            // Add to deathbanned list
            if (!deathBannedPlayers.contains(name)) {
                deathBannedPlayers.add(name);
            }

            // Broadcast red message
            Bukkit.broadcastMessage("§c" + name + " was banned");

            // Play Wither spawn sound
            Bukkit.getOnlinePlayers().forEach(p ->
                    p.playSound(p.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1f, 1f)
            );

            // Ban player in server
            Bukkit.getBanList(org.bukkit.BanList.Type.NAME)
                    .addBan(name, "§cYou were banned.", null, null);

            // Kick player
            player.kickPlayer("§cYou were banned.");
        }
    }

    // Get all deathbanned players
    public static List<String> getDeathBannedPlayers() {
        return new ArrayList<>(deathBannedPlayers);
    }

    // Remove player from deathbanned list (called by ReviveBeacon)
    public static void removeBan(String name) {
        deathBannedPlayers.remove(name);

        // Also unban in server if exists
        Bukkit.getBanList(org.bukkit.BanList.Type.NAME).pardon(name);
    }

    // Get player head item
    public static ItemStack getHeadForPlayer(String name) {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        meta.setOwner(name);
        meta.setDisplayName("§c§l" + name + "\n§fClick to revive");
        skull.setItemMeta(meta);
        return skull;
    }
}
