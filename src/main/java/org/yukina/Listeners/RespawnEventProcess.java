package org.yukina.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.Plugin;
import org.yukina.DreamCityServerCore;

public class RespawnEventProcess implements Listener {
    private final Plugin plugin;

    public RespawnEventProcess(Plugin plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerRespawnEvent(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        if (!player.getInventory().contains(Material.WATCH)) {
            player.getInventory().addItem(DreamCityServerCore.serverMenu);
            if(DreamCityServerCore.settings.getString("ServerGUIName").contains("空岛")) {
                Bukkit.dispatchCommand(player, "sf guide");
            }
            if(DreamCityServerCore.settings.getString("ServerGUIName").contains("生存")){
                player.getInventory().addItem(DreamCityServerCore.book);
                Bukkit.dispatchCommand(player, "sf guide");
            }
            player.sendMessage(DreamCityServerCore.PREFIX + "§a系统向您发送了一个服务器导航菜单,请妥善保管~");
        }else {
            player.sendMessage(DreamCityServerCore.PREFIX + "§c你已经有一个服务器向导书了!");
        }
    }
}
