package com.deejay.lifesteal.items;

import com.deejay.lifesteal.basic.DeathBan;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ReviveBeacon implements Listener {

    private final JavaPlugin plugin;
    private final Map<UUID, String> revivedThisSession = new HashMap<>();

    public ReviveBeacon(JavaPlugin plugin) {
        this.plugin = plugin;
        registerRecipe();
    }

    // Create the Revive Beacon item
    public ItemStack createBeacon() {
        ItemStack beacon = new ItemStack(Material.BEACON, 1);
        ItemMeta meta = beacon.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + "Revive Beacon");
        beacon.setItemMeta(meta);
        return beacon;
    }

    // --- Crafting Recipe ---
    private void registerRecipe() {
        ItemStack beaconItem = createBeacon();
        NamespacedKey key = new NamespacedKey(plugin, "revive_beacon");

        ShapedRecipe recipe = new ShapedRecipe(key, beaconItem);

        recipe.shape(
                "HEEHE",
                "REHC ",
                "HEENHE"
        );

        // Ingredients mapping
        recipe.setIngredient('H', new RecipeChoice.ExactChoice(new ItemStack(Material.NETHER_STAR))); // Example Heavy Core
        recipe.setIngredient('E', Material.ELYTRA);
        recipe.setIngredient('C', Material.CONDUIT);
        recipe.setIngredient('RE', new RecipeChoice.ExactChoice(new ItemStack(Material.COMPASS))); // Recovery Compass
        recipe.setIngredient('EN', Material.ENCHANTED_GOLDEN_APPLE);
        recipe.setIngredient('HE', new RecipeChoice.ExactChoice(new Heart(plugin).createHeart(1))); // Heart Item

        Bukkit.addRecipe(recipe);
    }

    // Right-click to open GUI
    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR) return;
        if (event.getItem() == null) return;
        if (!event.getItem().hasItemMeta()) return;
        if (!ChatColor.GREEN + "Revive Beacon".equals(event.getItem().getItemMeta().getDisplayName())) return;

        Player player = event.getPlayer();
        Inventory gui = Bukkit.createInventory(null, 27, ChatColor.DARK_GREEN + "Revive Beacon");

        for (String name : DeathBan.getDeathBannedPlayers()) {
            ItemStack skull = DeathBan.getHeadForPlayer(name);
            ItemMeta meta = skull.getItemMeta();
            meta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + name + "\n" + ChatColor.WHITE + "Click to revive");
            skull.setItemMeta(meta);
            gui.addItem(skull);
        }

        player.openInventory(gui);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle() == null) return;
        if (!event.getView().getTitle().contains("Revive Beacon")) return;

        event.setCancelled(true);

        if (event.getCurrentItem() == null) return;

        String display = event.getCurrentItem().getItemMeta().getDisplayName();
        if (display == null) return;

        String name = display.split("\n")[0].replaceAll(ChatColor.RED + "" + ChatColor.BOLD.toString(), "");
        Player revived = Bukkit.getPlayerExact(name);

        if (revived != null) {
            revivedThisSession.put(revived.getUniqueId(), event.getWhoClicked().getName());

            DeathBan.removeBan(name);

            Player reviver = (Player) event.getWhoClicked();
            reviver.sendMessage(ChatColor.RED + "Successfully revived " + name);

            ItemStack hand = reviver.getInventory().getItemInMainHand();
            hand.setAmount(hand.getAmount() - 1);

            reviver.closeInventory();
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (revivedThisSession.containsKey(player.getUniqueId())) {
            String reviverName = revivedThisSession.get(player.getUniqueId());

            player.setHealth(Math.min(6.0, player.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH).getBaseValue()));
            player.playSound(player.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT, 1f, 1f);

            Bukkit.broadcastMessage(ChatColor.GREEN + player.getName() + " was revived");
            Bukkit.broadcastMessage(ChatColor.WHITE + player.getName() + " was revived by " + reviverName);

            revivedThisSession.remove(player.getUniqueId());
        }
    }
}
