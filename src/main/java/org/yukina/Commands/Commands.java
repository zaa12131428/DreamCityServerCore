package org.yukina.Commands;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.yukina.DataBase.DataBase;
import org.yukina.DreamCityServerCore;
import org.yukina.Utils.CardType;
import org.yukina.Utils.Utils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.yukina.DreamCityServerCore.PREFIX;

public class Commands implements CommandExecutor {
    private Plugin plugin;

    public Commands(Plugin plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if ("DreamCity".equalsIgnoreCase(label)) {

            if("SetMapleCard".equalsIgnoreCase(args[0]) && sender.hasPermission("DreamCity.Admin")) {
                switch (args[1]) {
                    case "D":
                        DreamCityServerCore.dreamCityAPI.BuyCardDays(Integer.parseInt(args[2]), args[3], CardType.D);
                        sender.sendMessage(PREFIX + "§a成功给玩家 §b[§6" + args[3] + "§b] §a开通 " + CardType.D.getName() + " §e" + args[2] + " §a天!");
                        break;
                    case "M":
                        DreamCityServerCore.dreamCityAPI.BuyCardDays(Integer.parseInt(args[2]), args[3], CardType.M);
                        sender.sendMessage(PREFIX + "§a成功给玩家 §b[§6" + args[3] + "§b] §a开通 " + CardType.M.getName() + " §e" + args[2] + " §a天!");
                        break;
                    case "Q":
                        DreamCityServerCore.dreamCityAPI.BuyCardDays(Integer.parseInt(args[2]), args[3], CardType.Q);
                        sender.sendMessage(PREFIX + "§a成功给玩家 §b[§6" + args[3] + "§b] §a开通 " + CardType.Q.getName() + " §e" + args[2] + " §a天!");
                        break;
                    case "Y":
                        DreamCityServerCore.dreamCityAPI.BuyCardDays(Integer.parseInt(args[2]), args[3], CardType.Y);
                        sender.sendMessage(PREFIX + "§a成功给玩家 §b[§6" + args[3] + "§b] §a开通 " + CardType.Y.getName() + " §e" + args[2] + " §a天!");
                        break;
                    default:
                        sender.sendMessage(PREFIX + "§c输入类型错误！");
                }
                return true;
            }

            if (sender instanceof Player) {
                Player player = (Player) sender;
                YamlConfiguration settings = DreamCityServerCore.settings;
                if(args.length != 0){

                    if("MapleCardInfo".equalsIgnoreCase(args[0])){
                        DreamCityServerCore.dreamCityAPI.getMapleCardInfo(player);
                        return true;
                    }

                    if ("giveMeGUI".equalsIgnoreCase(args[0])) {
                        if (!player.getInventory().contains(Material.WATCH)) {
                            if(DreamCityServerCore.settings.getString("ServerGUIName").contains("空岛")) {
                                Bukkit.dispatchCommand(player, "sf guide");
                            }
                            if(settings.getString("ServerGUIName").contains("生存")){
                                player.getInventory().addItem(DreamCityServerCore.book);
                                Bukkit.dispatchCommand(player, "sf guide");
                            }
                            player.getInventory().addItem(DreamCityServerCore.serverMenu);
                            player.sendMessage(PREFIX + "§a系统向您发送了一个服务器导航菜单,请妥善保管~");
                            return true;
                        } else {
                            sender.sendMessage(PREFIX + "§c你已经有一个服务器向导书了!");
                        }
                        return true;
                    }

                    if ("BuyMapleCard".equalsIgnoreCase(args[0])) {
                        try {
                            Connection connection = DataBase.getConnection();
                            Statement statement = connection.createStatement();
                            statement.execute("use VIPBuy");

                            if("D".equalsIgnoreCase(args[1])){
                                DreamCityServerCore.dreamCityAPI.BuyMapleCard("D",player,Integer.parseInt(args[2]));
                                return true;
                            }

                            if ("M".equalsIgnoreCase(args[1])) {
                                DreamCityServerCore.dreamCityAPI.BuyMapleCard("M",player,31);
                                return true;
                            }

                            if ("Q".equalsIgnoreCase(args[1])) {
                                DreamCityServerCore.dreamCityAPI.BuyMapleCard("Q",player,92);
                                return true;
                            }

                            if ("Y".equalsIgnoreCase(args[1])) {
                                DreamCityServerCore.dreamCityAPI.BuyMapleCard("Y",player,365);
                                return true;
                            }
                        }catch (SQLException e){
                            e.printStackTrace();
                        }
                    }

                    if ("reload".equalsIgnoreCase(args[0])) {
                        if (player.isOp()) {
                            plugin.reloadConfig();
                            player.sendMessage(PREFIX + "§a插件 Config.yml 重载完毕!");
                            return true;
                        } else {
                            player.sendMessage(PREFIX + "§c你无法执行此命令,原因: §4权限不足");
                            return true;
                        }
                    }

                    if ("resetSQL".equalsIgnoreCase(args[0])) {
                        if (player.isOp()) {
                            Utils.resetTable();
                            player.sendMessage(PREFIX + "§a插件 数据库 重置完毕!");
                            return true;
                        } else {
                            player.sendMessage(PREFIX + "§c你无法执行此命令,原因: §4权限不足");
                            return true;
                        }
                    }

                    if (args[0].equalsIgnoreCase("GoTo")) {
                        DreamCityServerCore.dreamCityAPI.teleportServer(player, args[1]);
                        return true;
                    }

                    if ("OnLoginTpLoc".equalsIgnoreCase(args[0])) {
                        if (player.isOp()) {
                            String locSerialize = player.getLocation().serialize().toString().replaceAll("\n", "").replaceAll("\\{", "").replaceAll("\\}", "");
                            settings.set("OnLoginLoc", locSerialize);
                            try {
                                settings.save(DreamCityServerCore.config);
                            } catch (IOException e) {
                            }
                            player.sendMessage(PREFIX + "§a玩家登陆出生点设置成功！");
                            player.sendMessage(PREFIX + "§6X§f: §a" + player.getLocation().getX());
                            player.sendMessage(PREFIX + "§bY§f: §a" + player.getLocation().getY());
                            player.sendMessage(PREFIX + "§dZ§f: §a" + player.getLocation().getZ());
                            player.sendMessage(PREFIX + "§ePitch§f: §a" + player.getLocation().getPitch());
                            player.sendMessage(PREFIX + "§cYaw§f: §a" + player.getLocation().getYaw());
                            return true;
                        } else {
                            player.sendMessage(PREFIX + "§c你无法执行此命令,原因: §4权限不足");
                            return true;
                        }
                    }
                    if ("dailySign".equalsIgnoreCase(args[0])) {
                        DreamCityServerCore.dreamCityAPI.signPlayer(player);
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
