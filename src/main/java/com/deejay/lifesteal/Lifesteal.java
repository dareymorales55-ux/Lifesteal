package com.deejay.lifesteal;

import org.bukkit.plugin.java.JavaPlugin;

public final class Lifesteal extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("Lifesteal has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Lifesteal has been disabled!");
    }
}
