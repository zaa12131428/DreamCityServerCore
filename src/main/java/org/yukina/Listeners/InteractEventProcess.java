package org.yukina.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import static org.yukina.DreamCityServerCore.PREFIX;

public class InteractEventProcess implements Listener {
    private final Plugin plugin;

    public InteractEventProcess(Plugin plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if (event.getItem() != null && event.getItem().getType() != Material.AIR) {
                ItemStack item = event.getItem();
                if (item.getItemMeta().getDisplayName() != null && !item.getItemMeta().getDisplayName().equalsIgnoreCase("")) {
                    if (item.getItemMeta().getDisplayName().contains("服务器向导书")) {
                        Bukkit.dispatchCommand(player, "chestcommands open cd.yml");
                        player.sendMessage(PREFIX + "§a您打开了服务器指导书,根据提示进行传送吧~");
                    }
                    if(item.getItemMeta().getDisplayName().contains("服务器附魔书")){
                        Bukkit.dispatchCommand(player, "ce");
                        player.sendMessage(PREFIX + "§a您打开了服务器附魔书,根据提示进行附魔操作吧~");
                    }
                }
            }
        }
    }
}
