package org.yukina.Listeners;

import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.plugin.Plugin;

import static org.yukina.DreamCityServerCore.PREFIX;

public class DropItemEventProcess implements Listener{
    private final Plugin plugin;

    public DropItemEventProcess(Plugin plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayDropItemEvent(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        Item item = event.getItemDrop();
        if (item.getItemStack().getItemMeta().getDisplayName() != null) {
            if (item.getItemStack().getItemMeta().getDisplayName().contains("服务器向导书")) {
                player.sendMessage(PREFIX + "§c服务器向导书禁止丢弃~");
                event.setCancelled(true);
            }
        }
    }
}
