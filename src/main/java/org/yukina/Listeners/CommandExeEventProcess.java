package org.yukina.Listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.Plugin;
import org.yukina.DreamCityServerCore;

public class CommandExeEventProcess {
    private final Plugin plugin;

    public CommandExeEventProcess(Plugin plugin){
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerCommandExecuteEvent(PlayerCommandPreprocessEvent event) {
        Player p = event.getPlayer();

        if (DreamCityServerCore.settings.getString("ServerGUIName").contains("创造")) {
            String command = event.getMessage().replace("/", "");
            if (!p.isOp()) {
                if (command.equalsIgnoreCase("plot auto") || command.equalsIgnoreCase("plot a")) {
                    if (p.getLocation().getWorld().getName().equalsIgnoreCase("BuildWorld")) {
                        event.setMessage("NO-GETPLOT");
                        event.setCancelled(true);
                        p.sendMessage(DreamCityServerCore.PREFIX + "§c作品展示区,禁止领取地皮!");
                    }
                }
            }
        }

        if(event.getMessage().equalsIgnoreCase("/?")){
            if(!p.isOp()) {
                p.sendMessage(DreamCityServerCore.PREFIX + "§c未知指令!");
                event.setMessage("NO-PERMISSION");
                event.setCancelled(true);
            }
        }
    }
}
