package com.deejay.lifesteal;

import com.deejay.lifesteal.basic.KillDeath;
import com.deejay.lifesteal.basic.DeathBan;
import com.deejay.lifesteal.items.Heart;
import org.bukkit.plugin.java.JavaPlugin;

public final class Lifesteal extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("Lifesteal has been enabled!");

        // --- Register KillDeath listener ---
        getServer().getPluginManager().registerEvents(new KillDeath(), this);

        // --- Register DeathBan listener ---
        getServer().getPluginManager().registerEvents(new DeathBan(), this);

        // --- Register Heart listener & command (with recipes) ---
        Heart heart = new Heart(this); // pass JavaPlugin instance for recipes
        getServer().getPluginManager().registerEvents(heart, this);
        getCommand("withdraw").setExecutor(heart);
    }

    @Override
    public void onDisable() {
        getLogger().info("Lifesteal has been disabled!");
    }
}
