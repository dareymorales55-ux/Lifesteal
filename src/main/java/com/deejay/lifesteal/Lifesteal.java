package com.deejay.lifesteal;

import com.deejay.lifesteal.core.PlayerListener;
import com.deejay.lifesteal.items.Heart;
import org.bukkit.plugin.java.JavaPlugin;

public final class Lifesteal extends JavaPlugin {

    @Override
    public void onEnable() {

        saveDefaultConfig();

        getServer().getPluginManager().registerEvents(
                new PlayerListener(this),
                this
        );

        Heart heart = new Heart(this);

        getServer().getPluginManager().registerEvents(
                heart,
                this
        );

        getCommand("withdraw").setExecutor(heart);

        getLogger().info("Lifesteal has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Lifesteal has been disabled!");
    }
}
