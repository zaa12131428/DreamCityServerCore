package org.yukina.Listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.yukina.DreamCityServerCore;

public class QuitEventProcess implements Listener {

    @EventHandler
    public void onQuitEvent(PlayerQuitEvent event){
        DreamCityServerCore.dreamCityAPI.setLogin(event.getPlayer(),"退出");
        event.setQuitMessage("§8[§c-§8] §7"+event.getPlayer().getName());
    }
}
