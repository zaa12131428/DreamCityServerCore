package org.yukina;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.yukina.DataBase.DataBase;
import org.yukina.Utils.CardType;
import org.yukina.Utils.Utils;
import us.myles.ViaVersion.api.Via;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.yukina.DreamCityServerCore.PREFIX;
import static org.yukina.DreamCityServerCore.settings;

public class DreamCityAPI {
    private final Plugin plugin;

    public DreamCityAPI(Plugin plugin){
        this.plugin = plugin;
    }

    public String protocolVersionFormat(int protocolVer) {
        if (protocolVer >= 328 && protocolVer <= 382) {
            return "1.12.x";
        }
        if (protocolVer >= 383 && protocolVer <= 471) {
            return "1.13.x";
        }
        if(protocolVer >= 472 && protocolVer <= 564){
            return "1.14.x";
        }
        if(protocolVer >= 565 && protocolVer <= 715){
            return "1.15.x";
        }
        return "UnknowMinecraftVersion";
    }

    //统计登陆
    public void setLogin(Player player,String action){
        try {
            String date = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss").format(new Date());
            String ver = protocolVersionFormat(Via.getAPI().getPlayerVersion(player.getUniqueId()));
            Connection connection = DataBase.getConnection();
            Statement statement = connection.createStatement();
            statement.execute("use PlayerStatistics");
            statement.execute("INSERT INTO PlayerStatisticsTable (playername,LoginDate,ServerName,IPAddress,Version,OPType) VALUES ('"+player.getName()+"','"+date+"','"+settings.getString("ServerGUIName")+"','"+player.getAddress().getHostString()+"','"+ver+"','"+action+"')");
            connection.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    //检测签到数据表是否存在给定玩家
    public boolean dataBaseHasPlayer(Player p){
        try {
            Connection connection = DataBase.getConnection();
            Statement statement = connection.createStatement();

            statement.execute("use Sign");
            ResultSet result = statement.executeQuery("SELECT * FROM SignTable WHERE playername = '"+p.getName()+"'");
            result.last();
            boolean a = result.getRow() != 0;

            connection.close();
            return a;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    //检查玩家今天是否签到
    public boolean checkPlayerSign(Player p) throws SQLException{
        Connection connection = DataBase.getConnection();
        Statement statement = connection.createStatement();
        int day = Integer.parseInt(Utils.getDayInMon());
        statement.execute("use Sign");
        ResultSet result = statement.executeQuery("SELECT * FROM SignTable WHERE playername = '"+p.getName()+"'");
        result.last();
        boolean a =  result.getInt("Day"+day) != 0;
        connection.close();
        return a;
    }

    //检查玩家连续签到天数
    public int getPlayerSignDay(Player player) {
        int t = 0;
        try {
            String playerName = player.getName();
            int day = Integer.parseInt(DreamCityServerCore.settings.getString("Date").substring(2, 4));

            Connection connection = DataBase.getConnection();
            Statement statement = connection.createStatement();

            statement.execute("use Sign");
            ResultSet result = statement.executeQuery("SELECT * FROM SignTable WHERE playername = '" + playerName + "'");
            result.last();

            for (; day > 0; day--) {
                if (result.getInt("Day" + day) == 1) {
                    t += 1;
                } else {
                    break;
                }
            }
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return t;
    }

    //按照天数购买枫叶卡
    public void BuyCardDays(int day, Player player, CardType type){
        new Thread(()->{
            try {
                YamlConfiguration settings = DreamCityServerCore.settings;
                String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

                Connection connection = DataBase.getConnection();
                Statement statement = connection.createStatement();
                statement.execute("use VIPBuy");

                ResultSet result = statement.executeQuery("SELECT * FROM VIPBuyTable WHERE PlayerName = '" + player.getName() + "'");
                result.last();
                if (result.getRow() != 0) {
                    if (result.getInt("Status") == 1) {
                        Date endTime = new SimpleDateFormat("yyyy-MM-dd").parse(result.getString("EndTime"), new ParsePosition(0));
                        if (result.getString("VIPType").equalsIgnoreCase("Y") || result.getString("VIPType").equalsIgnoreCase("Q")) {
                            statement.execute("UPDATE VIPBuyTable SET EndTime = '" + Utils.getAfterNDay(endTime, day) + "' WHERE PlayerName = '" + player.getName() + "'");
                        } else {
                            statement.execute("UPDATE VIPBuyTable SET EndTime = '" + Utils.getAfterNDay(endTime, day) + "' , VIPType = 'M'WHERE PlayerName = '" + player.getName() + "'");
                        }
                        player.sendMessage(PREFIX + " §a续费成功! §6商品: §b[§6枫叶通行证§e-"+type.getName()+"§b] §4x1");
                        player.sendMessage(PREFIX + " §6续费日期: §b[§6" + date + "§b] §c到期时间: §b[§4" + Utils.getAfterNDay(endTime, day) + "§b]");
                    }
                } else {
                    player.setOp(true);
                    player.performCommand(settings.getString("MapleCard-BuySuccessCommand").replace("%player%", player.getName()));
                    player.setOp(false);

                    statement.execute("INSERT INTO VIPBuyTable (PlayerName,Status,BuyTime,EndTime,VIPType) VALUES ('" + player.getName() + "',1,'" + date + "','" + Utils.getAfterNDay(new Date(), day) + "','"+ (type==null ? type.getValue() : "D") +"')");

                    player.sendMessage(PREFIX + " §a购买成功! §6商品: §b[§6枫叶通行证§e-"+type.getName()+"§b] §4x1");
                    player.sendMessage(PREFIX + " §6购买日期: §b[§6" + date + "§b] §c到期时间: §b[§4" + Utils.getAfterNDay(new Date(), day) + "§b]");
                }
                connection.close();
            }catch (SQLException e){
                player.sendMessage(PREFIX + " §4购买失败! §6商品: §b[§6枫叶通行证§e-§a月卡§b] §4x1 &4请联系服务器管理员解决！");
                e.printStackTrace();
            }
        }).start();
    }

    //按照天数和玩家名称购买枫叶卡
    public void BuyCardDays(int day, String name, CardType type){
        new Thread(()->{
            try {
                String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

                Connection connection = DataBase.getConnection();
                Statement statement = connection.createStatement();
                statement.execute("use VIPBuy");

                ResultSet result = statement.executeQuery("SELECT * FROM VIPBuyTable WHERE PlayerName = '" + name + "'");
                result.last();
                if (result.getRow() != 0) {
                    if (result.getInt("Status") == 1) {
                        Date endTime = new SimpleDateFormat("yyyy-MM-dd").parse(result.getString("EndTime"), new ParsePosition(0));
                        if (result.getString("VIPType").equalsIgnoreCase("Y") || result.getString("VIPType").equalsIgnoreCase("Q")) {
                            statement.execute("UPDATE VIPBuyTable SET EndTime = '" + Utils.getAfterNDay(endTime, day) + "' WHERE PlayerName = '" + name + "'");
                        } else {
                            statement.execute("UPDATE VIPBuyTable SET EndTime = '" + Utils.getAfterNDay(endTime, day) + "' , VIPType = 'M'WHERE PlayerName = '" + name + "'");
                        }
                    }
                } else {
                    statement.execute("INSERT INTO VIPBuyTable (PlayerName,Status,BuyTime,EndTime,VIPType) VALUES ('" + name + "',1,'" + date + "','" + Utils.getAfterNDay(new Date(), day) + "','"+ (type==null ? type.getValue() : "D") +"')");
                }
                connection.close();
            }catch (SQLException e){
                e.printStackTrace();
            }
        }).start();
    }

    //购买枫叶卡 年费月费季费体验卡
    public void BuyMapleCard(String cardType, Player player, int day) throws SQLException{
        YamlConfiguration settings = DreamCityServerCore.settings;
        int money = DreamCityServerCore.api.look(player.getUniqueId());

        Connection connection = DataBase.getConnection();
        Statement statement = connection.createStatement();
        statement.execute("use VIPBuy");

        if(cardType.equalsIgnoreCase("D")){
            CardType type = CardType.D;
            if (money >= settings.getInt("MapleCard-D") * settings.getInt("MapleCard-D-DisCount") * day) {
                DreamCityServerCore.api.take(player.getUniqueId(), settings.getInt("MapleCard-D") * settings.getInt("MapleCard-M-DisCount"));
                if(day != 0) {
                    BuyCardDays(day, player, type);
                }else {
                    player.sendMessage(PREFIX + " §c请输入正确的日期!");
                }
            } else{
                player.sendMessage(PREFIX + " §c您没有足够的点券来支付: §b[§6枫叶通行证§e-"+type.getName()+" §9"+day+"§6天§b]");
            }
        }

        if(cardType.equalsIgnoreCase("M")) {
            CardType type = CardType.M;
            if (money >= settings.getInt("MapleCard-M") * settings.getInt("MapleCard-M-DisCount")) {
                DreamCityServerCore.api.take(player.getUniqueId(), settings.getInt("MapleCard-M") * settings.getInt("MapleCard-M-DisCount"));
                BuyCardDays(31,player,type);
            } else {
                player.sendMessage(PREFIX + " §c您没有足够的点券来支付: §b[§6枫叶通行证§e-"+type.getName()+"§b]");
            }
        }else if(cardType.equalsIgnoreCase("Q")){
            CardType type = CardType.Q;
            if (money >= settings.getInt("MapleCard-Q") * settings.getInt("MapleCard-Q-DisCount")) {
                DreamCityServerCore.api.take(player.getUniqueId(), settings.getInt("MapleCard-Q") * settings.getInt("MapleCard-Q-DisCount"));
                BuyCardDays(92,player,type);
            } else {
                player.sendMessage(PREFIX + " §c您没有足够的点券来支付: §b[§6枫叶通行证§e-"+type.getName()+"§b]");
            }
        }else if(cardType.equalsIgnoreCase("Y")) {
            CardType type = CardType.Y;
            if (money >= settings.getInt("MapleCard-Y") * settings.getInt("MapleCard-Y-DisCount")) {
                DreamCityServerCore.api.take(player.getUniqueId(), settings.getInt("MapleCard-Y") * settings.getInt("MapleCard-Y-DisCount"));
                BuyCardDays(365,player,type);
            } else {
                player.sendMessage(PREFIX + " §c您没有足够的点券来支付: §b[§6枫叶通行证§e-"+type.getName()+"§b]");
            }
        }
    }

    //获取玩家的枫叶卡状态
    public void getMapleCardInfo(Player player){
        new Thread(()->{
            try{
                Connection connection = DataBase.getConnection();
                Statement statement = connection.createStatement();
                statement.execute("use VIPBuy");
                ResultSet rs = statement.executeQuery("SELECT * FROM VIPBuyTable WHERE PlayerName = '" + player.getName() + "'");
                rs.last();
                if(rs.getRow() != 0){
                    String Status = rs.getString("Status").equalsIgnoreCase("1")?"§a激活":"§c未激活";
                    String buyTime = rs.getString("BuyTime");
                    String endTime = rs.getString("EndTime");
                    String VIPType = rs.getString("VIPType");
                    CardType type;
                    switch (VIPType){
                        case "D":type = CardType.D;break;
                        case "M":type = CardType.M;break;
                        case "Q":type = CardType.Q;break;
                        case "Y":type = CardType.Y;break;
                        default:type = null;
                    }
                    player.sendMessage(PREFIX + "§a查询成功,玩家 §8> §b"+player.getName());
                    player.sendMessage(PREFIX + "§b[§6枫叶通行证§b] §a激活状态 §8> "+Status);
                    player.sendMessage(PREFIX + "§b[§6枫叶通行证§b] §a购买时间 §8> §a[§e"+buyTime+"§a]");
                    player.sendMessage(PREFIX + "§b[§6枫叶通行证§b] §a到期时间 §8> §a[§e"+endTime+"§a]");
                    player.sendMessage(PREFIX + "§b[§6枫叶通行证§b] §a类型 §8> "+type.getName());
                }else{
                    player.sendMessage(PREFIX + "§c您没有购买 §b[§6枫叶通行证§b]§c 无法查询！");
                }
                connection.close();
            }catch (SQLException e){
                e.printStackTrace();
            }
        }).start();
    }

    //玩家签到方法
    public void signPlayer(Player player){
        try {
            int day = Integer.parseInt(Utils.getDayInMon());
            Connection connection = DataBase.getConnection();
            Statement statement = connection.createStatement();
            if (!checkPlayerSign(player)) {
                statement.execute("use Sign");
                statement.executeUpdate("UPDATE SignTable SET Day" + day + " = 1 WHERE PlayerName = '" + player.getName() + "'");
                player.sendMessage(PREFIX + "§a签到成功,快进入子服务器领取每日奖励吧~");
                int i = getPlayerSignDay(player);
                player.sendMessage(PREFIX + "§a您已经连续 §6" + i + " §a天签到了,请保持！");
            } else {
                player.sendMessage(PREFIX + "§6您已经签到过了,不能重复签到~");
            }
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //BungeeCord服务器传送方法
    public void teleportServer(Player player,String serverName){
        new Thread(() -> {
            try {
                player.sendMessage(PREFIX + "§a通向 §6[§c" + serverName + "§6] 的传送门已经为你开启,请勿移动!");
                Thread.sleep(1000);
                player.sendMessage(PREFIX + "§4>>>>>>BOOM<<<<<<");

                ByteArrayOutputStream b = new ByteArrayOutputStream();
                DataOutputStream out = new DataOutputStream(b);
                try {
                    out.writeUTF("Connect");
                    out.writeUTF(serverName);
                } catch (IOException ex) {
                }
                player.sendPluginMessage(plugin, "BungeeCord", b.toByteArray());
            } catch (InterruptedException e) {
            }
        }).start();
    }

    //在数据库中创建玩家的行
    public void createPlayerInDB(Player player){
        try {
            Connection connection = DataBase.getConnection();
            Statement statement = connection.createStatement();
            statement.execute("use Sign");
            statement.execute("INSERT INTO SignTable (playername,Day1,Day2,Day3,Day4,Day5,Day6,Day7,Day8,Day9,Day10,Day11,Day12,Day13,Day14,Day15,Day16,Day17,Day18,Day19,Day20,Day21,Day22,Day23,Day24,Day25,Day26,Day27,Day28,Day29,Day30,Day31) VALUES ('" + player.getName() + "',0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0)");
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //获取玩家是否是枫叶卡用户
    public boolean getPlayerIsMaple(Player player){
        try{
            Connection connection = DataBase.getConnection();
            Statement statement = connection.createStatement();
            statement.execute("use VIPBuy");
            ResultSet rs = statement.executeQuery("SELECT * FROM VIPBuyTable WHERE PlayerName = '" + player.getName() + "'");
            rs.last();
            boolean a = (rs.getRow() != 0);
            connection.close();
            return a;
        }catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    //获取玩家的枫叶卡是否到期
    public boolean getPlayerMapleCardIsEnd(Player player){
        try {
            Connection connection = DataBase.getConnection();
            Statement statement = connection.createStatement();
            statement.execute("use VIPBuy");
            ResultSet rs = statement.executeQuery("SELECT * FROM VIPBuyTable WHERE PlayerName = '" + player.getName() + "'");
            rs.last();
            int days = Utils.differentDays(new Date(), new SimpleDateFormat("yyyy-MM-dd").parse(rs.getString("EndTime"), new ParsePosition(0)));
            return days <= 0;
        }catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    //在数据库中删除玩家的枫叶卡数据
    public void deletePlayerMapleCard(Player player){
        try {
            Connection connection = DataBase.getConnection();
            Statement statement = connection.createStatement();
            statement.execute("use VIPBuy");
            statement.execute("DELETE FROM VIPBuyTable WHERE PlayerName = '"+ player.getName() +"'");
            connection.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    //获取玩家的枫叶卡还剩多少天到期
    public int getPlayerMapleCardDay(Player player) {
        try {
            Connection connection = DataBase.getConnection();
            Statement statement = connection.createStatement();
            statement.execute("use VIPBuy");
            ResultSet rs = statement.executeQuery("SELECT * FROM VIPBuyTable WHERE PlayerName = '" + player.getName() + "'");
            rs.last();
            int day = Utils.differentDays(new Date(), new SimpleDateFormat("yyyy-MM-dd").parse(rs.getString("EndTime"), new ParsePosition(0)));
            connection.close();
            return day;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public void setEndMapleCard(Player player){
        try{
            Connection connection = DataBase.getConnection();
            Statement statement = connection.createStatement();
            statement.execute("use VIPBuy");
            statement.executeUpdate("UPDATE VIPBuyTable SET Status = 0 WHERE PlayerName = '" + player.getName() + "'");
            connection.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
}
