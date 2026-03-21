package com.deejay.lifesteal.basic;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DeathBan implements Listener {

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {

        Player player = event.getEntity();

        double maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();

        // Check if player reached 0 hearts
        if (maxHealth <= 0) {

            String name = player.getName();

            // Broadcast red message
            Bukkit.broadcastMessage("§c" + name + " was banned");

            // Play Wither spawn sound to all online players
            Bukkit.getOnlinePlayers().forEach(p ->
                    p.playSound(p.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1f, 1f)
            );

            // Ban player
            Bukkit.getBanList(org.bukkit.BanList.Type.NAME)
                    .addBan(name, "§cYou were banned.", null, null);

            // Kick player
            player.kickPlayer("§cYou were banned.");
        }
    }
}
