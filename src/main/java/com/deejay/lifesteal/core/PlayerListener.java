package com.deejay.lifesteal.core;

import com.deejay.lifesteal.Lifesteal;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListener implements Listener {

    private final Lifesteal plugin;

    public PlayerListener(Lifesteal plugin) {
        this.plugin = plugin;
    }

    // =========================
    // FIRST JOIN HEARTS
    // =========================
    @EventHandler
    public void onFirstJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (!player.hasPlayedBefore()) {

            int startingHearts =
                    plugin.getConfig().getInt("starting-hearts");

            double health = startingHearts * 2.0;

            player.getAttribute(Attribute.MAX_HEALTH)
                    .setBaseValue(health);

            player.setHealth(health);
        }
    }

    // =========================
    // PLAYER DEATH
    // =========================
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {

        Player victim = event.getEntity();

        double victimHealth =
                victim.getAttribute(Attribute.MAX_HEALTH)
                        .getBaseValue();

        // =========================
        // HEART LOSS
        // =========================
        String lossValue =
                plugin.getConfig()
                        .getString("hearts-lost-on-death");

        double heartsToRemove;

        if (lossValue.equalsIgnoreCase("ALL")) {

            heartsToRemove = victimHealth;

        } else {

            heartsToRemove =
                    Integer.parseInt(lossValue) * 2.0;
        }

        double newHealth =
                victimHealth - heartsToRemove;

        if (newHealth < 0) {
            newHealth = 0;
        }

        victim.getAttribute(Attribute.MAX_HEALTH)
                .setBaseValue(newHealth);

        // =========================
        // BAN CHECK
        // =========================
        int minHearts =
                plugin.getConfig().getInt("min-hearts");

        double minHealth = minHearts * 2.0;

        if (newHealth < minHealth) {

            String broadcast =
                    plugin.getConfig()
                            .getString("messages.player-ban-message")
                            .replace("%player%", victim.getName());

            Bukkit.broadcastMessage(broadcast);

            String reason =
                    plugin.getConfig()
                            .getString("messages.player-ban-reason");

            victim.ban(reason, null, null, true);

            String soundName =
                    plugin.getConfig()
                            .getString("player-ban-sound");

            try {

                Sound sound =
                        Sound.valueOf(soundName);

                for (Player online : Bukkit.getOnlinePlayers()) {

                    online.playSound(
                            online.getLocation(),
                            sound,
                            1.0f,
                            1.0f
                    );
                }

            } catch (IllegalArgumentException ignored) {
            }
        }

        // =========================
        // HEART GAIN ON KILL
        // =========================
        Player killer = victim.getKiller();

        if (killer == null) {
            return;
        }

        double killerHealth =
                killer.getAttribute(Attribute.MAX_HEALTH)
                        .getBaseValue();

        String gainValue =
                plugin.getConfig()
                        .getString("hearts-gain-on-kill");

        double heartsToAdd;

        if (gainValue.equalsIgnoreCase("ALL")) {

            heartsToAdd = victimHealth;

        } else {

            heartsToAdd =
                    Integer.parseInt(gainValue) * 2.0;
        }

        double maxHealth =
                plugin.getConfig()
                        .getInt("max-hearts") * 2.0;

        double finalHealth =
                killerHealth + heartsToAdd;

        double overflow = 0;

        if (finalHealth > maxHealth) {

            overflow = finalHealth - maxHealth;

            finalHealth = maxHealth;
        }

        killer.getAttribute(Attribute.MAX_HEALTH)
                .setBaseValue(finalHealth);

        String message =
                plugin.getConfig()
                        .getString("messages.heart-gained")
                        .replace("%amount%",
                                String.valueOf(heartsToAdd / 2));

        killer.sendMessage(message);

        // =========================
        // OVERFLOW HEART DROPS
        // =========================
        if (overflow > 0) {

            // Heart item drop logic later

        }
    }
}
