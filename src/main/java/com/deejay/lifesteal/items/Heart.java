package com.deejay.lifesteal.items;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class Heart implements Listener, CommandExecutor {

    private static final double HEART_VALUE = 2.0;  // 1 heart = 2 health
    private static final double MIN_HEALTH = 2.0;   // 1 heart
    private static final double MAX_HEALTH = 40.0;  // 20 hearts
    private final JavaPlugin plugin;

    public Heart(JavaPlugin plugin) {
        this.plugin = plugin;
        registerRecipes();
    }

    // --- Create Heart Item ---
    private ItemStack createHeart(int amount) {
        ItemStack heart = new ItemStack(Material.NETHER_STAR, amount);
        ItemMeta meta = heart.getItemMeta();
        meta.setDisplayName("§4Heart");
        heart.setItemMeta(meta);
        return heart;
    }

    // --- Right-click Heart item to use ---
    @EventHandler
    public void onUse(PlayerInteractEvent event) {
        if (event.getItem() == null) return;

        ItemStack item = event.getItem();

        if (item.getType() != Material.NETHER_STAR) return;
        if (!item.hasItemMeta()) return;
        if (!item.getItemMeta().hasDisplayName()) return;
        if (!item.getItemMeta().getDisplayName().equals("§4Heart")) return;

        Player player = event.getPlayer();
        double maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();

        if (maxHealth >= MAX_HEALTH) {
            player.sendMessage(ChatColor.RED + "You are already at max hearts!");
            return;
        }

        // Add 1 heart
        double newHealth = Math.min(maxHealth + HEART_VALUE, MAX_HEALTH);
        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(newHealth);

        // Play sound
        player.playSound(player.getLocation(), Sound.BLOCK_RESPAWN_ANCHOR_CHARGE, 1f, 1f);

        // Consume item
        item.setAmount(item.getAmount() - 1);
    }

    // --- /withdraw command ---
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) return true;

        Player player = (Player) sender;

        if (args.length != 1) {
            player.sendMessage(ChatColor.RED + "Usage: /withdraw <amount>");
            return true;
        }

        int amount;

        try {
            amount = Integer.parseInt(args[0]);
        } catch (Exception e) {
            player.sendMessage(ChatColor.RED + "Invalid number.");
            return true;
        }

        if (amount <= 0) {
            player.sendMessage(ChatColor.RED + "Amount must be positive.");
            return true;
        }

        double maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
        double remove = amount * HEART_VALUE;

        // --- SAFEGUARD: cannot go below 1 heart ---
        if (maxHealth - remove < MIN_HEALTH) {
            player.sendMessage(ChatColor.RED + "You must keep at least 1 heart.");
            return true;
        }

        // Remove hearts
        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(maxHealth - remove);

        // Give Heart items
        player.getInventory().addItem(createHeart(amount));

        player.sendMessage(ChatColor.GREEN + "Withdrew " + amount + " heart(s).");

        return true;
    }

    // --- Register 3 Crafting Recipes ---
    private void registerRecipes() {

        ItemStack heartItem = createHeart(1);

        Material[] centers = new Material[] {
                Material.WITHER_SKELETON_SKULL,
                Material.DRAGON_HEAD,
                Material.NETHER_STAR // You can replace this with your custom "Ominous Trial Key" if you have a material or custom item
        };

        for (Material center : centers) {
            NamespacedKey key = new NamespacedKey(plugin, "heart_" + center.toString().toLowerCase());
            ShapedRecipe recipe = new ShapedRecipe(key, heartItem);

            recipe.shape(
                    "NEN",
                    "ECE",
                    "NEN"
            );

            recipe.setIngredient('N', Material.NAUTILUS_SHELL);
            recipe.setIngredient('E', Material.NETHERITE_INGOT);
            recipe.setIngredient('C', new RecipeChoice.ExactChoice(new ItemStack(center)));

            Bukkit.addRecipe(recipe);
        }
    }
}
