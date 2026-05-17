package com.deejay.lifesteal;

import com.deejay.lifesteal.core.PlayerListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class Lifesteal extends JavaPlugin {

    @Override
    public void onEnable() {
        // Create config.yml if it doesn't exist
        saveDefaultConfig();

        // Register listener
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);

        getLogger().info("Lifesteal has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Lifesteal has been disabled!");
    }
}
