package com.deejay.lifesteal;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class Hardcore implements CommandExecutor {

    private final JavaPlugin plugin;
    private final File file;
    private final FileConfiguration config;

    public Hardcore(JavaPlugin plugin) {
        this.plugin = plugin;

        // Create Hardcore.yml in plugin folder
        file = new File(plugin.getDataFolder(), "Hardcore.yml");
        if (!file.exists()) {
            try {
                plugin.getDataFolder().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        config = YamlConfiguration.loadConfiguration(file);

        // Initialize if not set
        if (!config.contains("Hardcore")) {
            config.set("Hardcore", false);
            saveConfig();
        }
    }

    private void saveConfig() {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setHardcore(boolean enabled) {
        config.set("Hardcore", enabled);
        saveConfig();

        for (World world : Bukkit.getWorlds()) {
            world.setHardcore(enabled);
        }

        String status = enabled ? "enabled" : "disabled";
        Bukkit.broadcastMessage("§cHardcore mode has been " + status + "!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length != 1) {
            sender.sendMessage("Usage: /hardcore <true|false>");
            return true;
        }

        boolean enable;
        if (args[0].equalsIgnoreCase("true")) {
            enable = true;
        } else if (args[0].equalsIgnoreCase("false")) {
            enable = false;
        } else {
            sender.sendMessage("Invalid argument. Use true or false.");
            return true;
        }

        setHardcore(enable);
        return true;
    }

    // Optional getter
    public boolean isHardcoreEnabled() {
        return config.getBoolean("Hardcore", false);
    }
}
