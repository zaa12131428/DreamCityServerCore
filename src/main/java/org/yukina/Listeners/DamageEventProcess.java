package org.yukina.Listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.Plugin;

import java.text.DecimalFormat;

public class DamageEventProcess implements Listener {
    private final Plugin plugin;

    public DamageEventProcess(Plugin plugin){
        this.plugin = plugin;
    }


    @EventHandler
    public void onPlayaerByEntity(EntityDamageByEntityEvent event){
        Entity entity = event.getDamager();
        String damage = new DecimalFormat("0.0").format(event.getDamage());
        if(entity instanceof Player){
            Player p = (Player)entity;
            p.sendTitle("","§c§l造成 §b"+damage+" §c§l伤害",0,20,0);
        }
    }
}
