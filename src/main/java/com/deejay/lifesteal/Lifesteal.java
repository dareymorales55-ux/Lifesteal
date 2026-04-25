package com.deejay.lifesteal;

import com.deejay.lifesteal.core.PlayerDeathListener;
import com.deejay.lifesteal.core.PlayerKillListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class Lifesteal extends JavaPlugin {

    @Override
    public void onEnable() {
        // Create config.yml if it doesn't exist
        saveDefaultConfig();

        // Register core listeners
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerKillListener(this), this);

        getLogger().info("Lifesteal has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Lifesteal has been disabled!");
    }
}
