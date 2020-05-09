package org.yukina.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.yukina.DreamCityServerCore;
import org.yukina.Utils.Utils;
import us.myles.ViaVersion.api.Via;

import static org.yukina.DreamCityServerCore.PREFIX;

public class JoinEventProcess implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        DreamCityServerCore.dreamCityAPI.setLogin(player,"登陆");

        if (!DreamCityServerCore.dreamCityAPI.dataBaseHasPlayer(player)) {
            DreamCityServerCore.dreamCityAPI.createPlayerInDB(player);
        }

        player.teleport(Utils.getFormatLoc());
        YamlConfiguration settings = DreamCityServerCore.settings;

        try {
            if (DreamCityServerCore.dreamCityAPI.getPlayerIsMaple(player)) {
                if (DreamCityServerCore.dreamCityAPI.getPlayerMapleCardIsEnd(player)) {
                    event.setJoinMessage(PREFIX + "§a玩家§b > §f[§6" + player.getName() + "§f] §b< §a来到服务器~");
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(),settings.getString("MapleCard-EndCommand").replace("%player%", player.getName()));
                    player.sendTitle("§b[§6枫叶通行证§b]", "§c您的枫叶通行证已经到期了!", 20, 100, 20);
                    DreamCityServerCore.dreamCityAPI.setEndMapleCard(player);
                } else {
                    event.setJoinMessage(PREFIX + "§c欢迎尊贵的 §6枫叶通行证玩家 §e > §6[§b" + player.getName() + "§6] §b < §c来到服务器~");
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(),settings.getString("MapleCard-BuySuccessCommand").replace("%player%", player.getName()));
                    player.sendTitle("§b[§6枫叶通行证§b]", "§a您的枫叶通行证还有 §e" + DreamCityServerCore.dreamCityAPI.getPlayerMapleCardDay(player) + " 天§a到期!", 20, 100, 20);
                }
            } else {
                event.setJoinMessage(PREFIX + "§a玩家§b > §f[§6" + player.getName() + "§f] §b< §a来到服务器~");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!player.getInventory().contains(Material.WATCH)) {
            player.getInventory().addItem(DreamCityServerCore.serverMenu);
            if (settings.getString("ServerGUIName").contains("空岛")) {
                Bukkit.dispatchCommand(player, "sf guide");
            }
            if (settings.getString("ServerGUIName").contains("生存")) {
                player.getInventory().addItem(DreamCityServerCore.book);
                Bukkit.dispatchCommand(player, "sf guide");
            }
            player.sendMessage(PREFIX + "§a系统向您发送了一个服务器导航菜单,请妥善保管~");
        } else {
            player.sendMessage(PREFIX + "§c你已经有一个服务器向导书了!");
        }
    }
}
