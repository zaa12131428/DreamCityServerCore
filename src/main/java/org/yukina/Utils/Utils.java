package org.yukina.Utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.yukina.DataBase.DataBase;
import org.yukina.DreamCityServerCore;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Utils {

    public static Location getFormatLoc() {
        YamlConfiguration settings = YamlConfiguration.loadConfiguration(DreamCityServerCore.config);
        if (settings.contains("OnLoginLoc")) {
            String loc[] = settings.getString("OnLoginLoc").split(",");

            World w = Bukkit.getWorld(loc[0].split("=")[1]);
            double x = Double.parseDouble(loc[1].split("=")[1]);
            double y = Double.parseDouble(loc[2].split("=")[1]);
            double z = Double.parseDouble(loc[3].split("=")[1]);
            float pitch = Float.parseFloat(loc[4].split("=")[1]);
            float yaw = Float.parseFloat(loc[5].split("=")[1]);
            return new Location(w, x, y, z, yaw, pitch);
        }
        return null;
    }

    public static int differentDays(Date date1, Date date2) {
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(date1);
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(date2);

        int day1 = calendar1.get(Calendar.DAY_OF_YEAR);
        int day2 = calendar2.get(Calendar.DAY_OF_YEAR);
        int year1 = calendar1.get(Calendar.YEAR);
        int year2 = calendar2.get(Calendar.YEAR);

        if (year1 != year2)  //不同年
        {
            int timeDistance = 0;
            for (int i = year1; i < year2; i++) { //闰年
                if (i % 4 == 0 && i % 100 != 0 || i % 400 == 0) {
                    timeDistance += 366;
                } else { // 不是闰年
                    timeDistance += 365;
                }
            }
            return timeDistance + (day2 - day1);
        } else {// 同年
            return day2 - day1;
        }
    }


    public static String getAfterNDay(Date date, int n) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, n);
        String a = new SimpleDateFormat("yyyy-MM-dd").format(c.getTime());
        return a;
    }

    public static void resetTable() {
        new Thread(() -> {
            YamlConfiguration settings = DreamCityServerCore.settings;
            while (true) {
                try {
                    String date = new SimpleDateFormat("MMdd").format(new Date());
                    if (settings.getString("Date") == null) {
                        settings.set("Date", date);
                        settings.save(DreamCityServerCore.config);
                    } else if (!settings.getString("Date").substring(0, 2).equalsIgnoreCase(date.substring(0, 2))) {
                        settings.set("Date", date);
                        settings.save(DreamCityServerCore.config);
                        try {
                            Connection connection = DataBase.getConnection();
                            Statement statement = connection.createStatement();
                            statement.execute("use Sign");
                            ResultSet rs = statement.executeQuery("SELECT * FROM SignTable");
                            List<String> players = new ArrayList<>();
                            while (rs.next()) {
                                String t = rs.getString("PlayerName");
                                players.add(t);
                            }

                            for (String s : players) {
                                for (int i = 1; i <= 31; i++) {
                                    statement.execute("UPDATE SignTable SET Day" + i + " = 0 WHERE PlayerName = '" + s + "'");
                                }
                            }
                            connection.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (!date.substring(2, 4).equalsIgnoreCase(settings.getString("Date").substring(2, 4))) {
                        settings.set("Date", date);
                        settings.save(DreamCityServerCore.config);
                    } else {
                        Thread.sleep(1000);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static String getDayInMon(){
        return new SimpleDateFormat("dd").format(new java.util.Date());
    }
}
