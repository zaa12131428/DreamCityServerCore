package org.yukina.Listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.yukina.DreamCityServerCore;

public class BlockPlaceEventProcess implements Listener {

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event){
        if(event.getBlock().getType().toString().contains("DISPENSER")){
            Location block = event.getBlock().getLocation();
            Location under = new Location(block.getWorld(),block.getBlockX(),block.getBlockY()+1,block.getBlockZ());

            if(under.getBlock().getType().toString().contains("CHEST")){
                Block b = under.getBlock();
                if(b.getRelative(1,0,0).getType().toString().contains("WALL_SIGN")){
                    Sign s = (Sign) b.getRelative(1,0,0).getState();
                    if(s.getLine(3).contains("价格")){
                        event.setCancelled(true);
                        event.getPlayer().sendMessage(DreamCityServerCore.PREFIX+"§c禁止在商店下方放置远古基座");
                    }
                }
                if(b.getRelative(-1,0,0).getType().toString().contains("WALL_SIGN")){
                    Sign s = (Sign) b.getRelative(-1,0,0).getState();
                    if(s.getLine(3).contains("价格")){
                        event.setCancelled(true);
                        event.getPlayer().sendMessage(DreamCityServerCore.PREFIX+"§c禁止在商店下方放置远古基座");
                    }
                }
                if(b.getRelative(0,1,0).getType().toString().contains("WALL_SIGN")){
                    Sign s = (Sign) b.getRelative(0,1,0).getState();
                    if(s.getLine(3).contains("价格")){
                        event.setCancelled(true);
                        event.getPlayer().sendMessage(DreamCityServerCore.PREFIX+"§c禁止在商店下方放置远古基座");
                    }
                }
                if(b.getRelative(0,-1,0).getType().toString().contains("WALL_SIGN")){
                    Sign s = (Sign) b.getRelative(0,-1,0).getState();
                    if(s.getLine(3).contains("价格")){
                        event.setCancelled(true);
                        event.getPlayer().sendMessage(DreamCityServerCore.PREFIX+"§c禁止在商店下方放置远古基座");
                    }
                }
                if(b.getRelative(0,0,1).getType().toString().contains("WALL_SIGN")){
                    Sign s = (Sign) b.getRelative(0,0,1).getState();
                    if(s.getLine(3).contains("价格")){
                        event.setCancelled(true);
                        event.getPlayer().sendMessage(DreamCityServerCore.PREFIX+"§c禁止在商店下方放置远古基座");
                    }
                }
                if(b.getRelative(0,0,-1).getType().toString().contains("WALL_SIGN")){
                    Sign s = (Sign) b.getRelative(0,0,-1).getState();
                    if(s.getLine(3).contains("价格")){
                        event.setCancelled(true);
                        event.getPlayer().sendMessage(DreamCityServerCore.PREFIX+"§c禁止在商店下方放置远古基座");
                    }
                }
            }
        }
    }
}
