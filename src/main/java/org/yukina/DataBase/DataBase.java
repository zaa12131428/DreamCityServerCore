package org.yukina.DataBase;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.bukkit.configuration.file.YamlConfiguration;
import org.yukina.DreamCityServerCore;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DataBase {
    private static ComboPooledDataSource source;

    public static void InitDB(){

        try {
            source = new ComboPooledDataSource();

            source.setDriverClass("com.mysql.cj.jdbc.Driver");
            source.setJdbcUrl("jdbc:mysql://"+ DreamCityServerCore.settings.getString("DBUrl")+"?useSSL=false&serverTimezone=GMT&autoReconnect=true&failOverReadOnly=false");
            source.setUser(DreamCityServerCore.settings.getString("DBUserName"));
            source.setPassword(DreamCityServerCore.settings.getString("DBPassWord"));
            source.setInitialPoolSize(DreamCityServerCore.settings.getInt("DBInitPoolSize"));
            source.setMaxPoolSize(DreamCityServerCore.settings.getInt("DBMaxPoolSize"));
            source.setMinPoolSize(DreamCityServerCore.settings.getInt("DBMinPoolSize"));
            source.setIdleConnectionTestPeriod(30);
            source.setAcquireIncrement(5);

            Connection connection = getConnection();
            Statement statement = connection.createStatement();

            statement.execute("CREATE DATABASE IF NOT EXISTS Sign");
            statement.execute("use Sign");
            statement.execute("CREATE TABLE IF NOT EXISTS SignTable(PlayerID INT(10) NOT NULL AUTO_INCREMENT PRIMARY KEY,PlayerName VARCHAR (200), Day1 INT(10), Day2 INT (10), Day3 INT (10), Day4 INT (10), Day5 INT (10), Day6 INT (10), Day7 INT (10), Day8 INT (10), Day9 INT (10), Day10 INT (10), Day11 INT (10), Day12 INT (10), Day13 INT (10), Day14 INT (10), Day15 INT (10), Day16 INT (10), Day17 INT (10), Day18 INT (10), Day19 INT (10), Day20 INT (10), Day21 INT (10), Day22 INT (10), Day23 INT (10), Day24 INT (10), Day25 INT (10), Day26 INT (10), Day27 INT (10), Day28 INT (10), Day29 INT (10), Day30 INT (10), Day31 INT (10))");

            statement.execute("CREATE DATABASE IF NOT EXISTS VIPBuy");
            statement.execute("use VIPBuy");
            statement.execute("CREATE TABLE IF NOT EXISTS VIPBuyTable(PlayerID INT(10) NOT NULL AUTO_INCREMENT PRIMARY KEY,PlayerName VARCHAR (200), Status INT(10), BuyTime VARCHAR (20), EndTime VARCHAR (20), VIPType VARCHAR (20))");

            statement.execute("CREATE DATABASE IF NOT EXISTS PlayerStatistics");
            statement.execute("use PlayerStatistics");
            statement.execute("CREATE TABLE IF NOT EXISTS PlayerStatisticsTable(PlayerID INT(10) NOT NULL AUTO_INCREMENT PRIMARY KEY,PlayerName VARCHAR (50), LoginDate VARCHAR (100), ServerName VARCHAR (20), IPAddress VARCHAR (20),Version VARCHAR (20),OPType VARCHAR (20))");

            connection.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static Connection getConnection(){
        try {
            return source.getConnection();
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }
}
