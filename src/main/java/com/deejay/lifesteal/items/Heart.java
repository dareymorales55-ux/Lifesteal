package com.deejay.lifesteal.items;

import com.deejay.lifesteal.Lifesteal;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class Heart implements Listener, CommandExecutor {

    private final Lifesteal plugin;

    public Heart(Lifesteal plugin) {
        this.plugin = plugin;

        createRecipe();
    }

    // =========================================================
    // CREATE HEART ITEM
    // =========================================================
    public ItemStack createHeart() {

        String materialName =
                plugin.getConfig()
                        .getString("items.heart.base-item");

        Material material =
                Material.valueOf(materialName);

        ItemStack item = new ItemStack(material);

        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(
                plugin.getConfig()
                        .getString("items.heart.item-name")
        );

        boolean glint =
                plugin.getConfig()
                        .getBoolean("items.heart.enchant-glint");

        if (glint) {

            meta.addEnchant(
                    Enchantment.UNBREAKING,
                    1,
                    true
            );

            meta.addItemFlags(
                    ItemFlag.HIDE_ENCHANTS
            );
        }

        item.setItemMeta(meta);

        return item;
    }

    // =========================================================
    // CREATE RECIPE
    // =========================================================
    public void createRecipe() {

        ItemStack heart = createHeart();

        NamespacedKey key =
                new NamespacedKey(plugin, "heart");

        ShapedRecipe recipe =
                new ShapedRecipe(key, heart);

        List<String> shape =
                plugin.getConfig()
                        .getStringList("items.heart.recipe.shape");

        recipe.shape(
                shape.get(0),
                shape.get(1),
                shape.get(2)
        );

        for (String entry :
                plugin.getConfig()
                        .getConfigurationSection(
                                "items.heart.recipe.ingredients"
                        ).getKeys(false)) {

            String materialName =
                    plugin.getConfig()
                            .getString(
                                    "items.heart.recipe.ingredients." + entry
                            );

            recipe.setIngredient(
                    entry.charAt(0),
                    Material.valueOf(materialName)
            );
        }

        plugin.getServer().addRecipe(recipe);
    }

    // =========================================================
    // USE HEART
    // =========================================================
    @EventHandler
    public void onUse(PlayerInteractEvent event) {

        Player player = event.getPlayer();

        ItemStack item = player.getInventory().getItemInMainHand();

        if (!item.hasItemMeta()) return;

        if (!item.getItemMeta().hasDisplayName()) return;

        String itemName =
                plugin.getConfig()
                        .getString("items.heart.item-name");

        if (!item.getItemMeta().getDisplayName().equals(itemName)) {
            return;
        }

        double maxHealth =
                player.getAttribute(Attribute.GENERIC_MAX_HEALTH)
                        .getBaseValue();

        double configMax =
                plugin.getConfig()
                        .getInt("max-hearts") * 2.0;

        if (maxHealth >= configMax) {
            return;
        }

        player.getAttribute(Attribute.GENERIC_MAX_HEALTH)
                .setBaseValue(maxHealth + 2.0);

        item.setAmount(item.getAmount() - 1);

        String soundName =
                plugin.getConfig()
                        .getString("items.heart.sound-played-on-use");

        try {

            Sound sound =
                    Sound.valueOf(soundName);

            player.playSound(
                    player.getLocation(),
                    sound,
                    1.0f,
                    1.0f
            );

        } catch (IllegalArgumentException ignored) {
        }
    }

    // =========================================================
    // /WITHDRAW
    // =========================================================
    @Override
    public boolean onCommand(
            CommandSender sender,
            Command command,
            String label,
            String[] args
    ) {

        if (!(sender instanceof Player player)) {
            return true;
        }

        if (args.length != 1) {
            return true;
        }

        int amount;

        try {

            amount = Integer.parseInt(args[0]);

        } catch (NumberFormatException e) {

            return true;
        }

        double currentHealth =
                player.getAttribute(Attribute.GENERIC_MAX_HEALTH)
                        .getBaseValue();

        double healthToRemove =
                amount * 2.0;

        int minHearts =
                plugin.getConfig()
                        .getInt("min-hearts");

        double minHealth =
                minHearts * 2.0;

        if ((currentHealth - healthToRemove) < minHealth) {

            player.sendMessage(
                    plugin.getConfig()
                            .getString(
                                    "messages.withdraw-too-much-message"
                            )
            );

            return true;
        }

        player.getAttribute(Attribute.GENERIC_MAX_HEALTH)
                .setBaseValue(currentHealth - healthToRemove);

        player.getInventory().addItem(
                createHeart(amount)
        );

        String message =
                plugin.getConfig()
                        .getString("messages.heart-withdrew")
                        .replace(
                                "%amount%",
                                String.valueOf(amount)
                        );

        player.sendMessage(message);

        return true;
    }

    // =========================================================
    // CREATE MULTIPLE HEARTS
    // =========================================================
    public ItemStack createHeart(int amount) {

        ItemStack item = createHeart();

        item.setAmount(amount);

        return item;
    }
}
