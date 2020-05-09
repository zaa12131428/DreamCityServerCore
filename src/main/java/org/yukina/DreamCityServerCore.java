package org.yukina;

import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.yukina.Commands.*;
import org.yukina.DataBase.DataBase;
import org.yukina.Listeners.*;
import org.yukina.Utils.Utils;
import java.io.File;
import java.util.ArrayList;
import java.util.List;


public final class DreamCityServerCore extends JavaPlugin implements Listener, PluginMessageListener {

    public static File config;

    public static PlayerPointsAPI api;

    public static DreamCityAPI dreamCityAPI;
    public static ItemStack serverMenu,book;
    public static YamlConfiguration settings;
    public static final String PREFIX = "§b[§6梦想之都核心§b] ";

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new JoinEventProcess(), this);
        getServer().getPluginManager().registerEvents(new DamageEventProcess(this), this);
        getServer().getPluginManager().registerEvents(new DropItemEventProcess(this), this);
        getServer().getPluginManager().registerEvents(new InteractEventProcess(this), this);
        getServer().getPluginManager().registerEvents(new BlockPlaceEventProcess(),this);
        getServer().getPluginManager().registerEvents(new QuitEventProcess(),this);

        getServer().getPluginCommand("DreamCity").setExecutor(new Commands(this));

        getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", this::onPluginMessageReceived);
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        Plugin playerPoints1 = getServer().getPluginManager().getPlugin("PlayerPoints");
        PlayerPoints playerPoints = PlayerPoints.class.cast(playerPoints1);
        api = playerPoints.getAPI();

        InitFile();
        InitItem();
        DataBase.InitDB();
        Utils.resetTable();

        dreamCityAPI = new DreamCityAPI(this);
        getLogger().info("§c梦想之都 >>> 枫叶通行证+梦想之都核心启动完成！");
    }

    public void InitFile() {
        config = new File(getDataFolder(), "config.yml");
        if (!config.exists()) {
            saveDefaultConfig();
        }
        settings = YamlConfiguration.loadConfiguration(config);
    }

    public void InitItem() {
        serverMenu = new ItemStack(Material.WATCH);
        ItemMeta serverMenuMeta = serverMenu.getItemMeta();
        List<String> serverMenuLores = new ArrayList<>();

        serverMenuMeta.setDisplayName("§6>>>>>> §f[§a" + settings.getString("ServerGUIName") + "§f] §c服务器向导书 §6<<<<<<");
        serverMenuLores.add("§6使用方法 §f: §d右键即可打开服务器菜单");
        serverMenuLores.add("§6是否丢弃 §f: §4禁止丢弃!");
        serverMenuMeta.setLore(serverMenuLores);
        serverMenu.setItemMeta(serverMenuMeta);

        if(settings.getString("ServerGUIName").contains("生存")){
            book = new ItemStack(Material.ENCHANTED_BOOK);
            ItemMeta bookMeta = book.getItemMeta();
            List<String> bookLores = new ArrayList<>();

            bookMeta.setDisplayName("§6>>>>>> §f[§a" + settings.getString("ServerGUIName") + "§f] §c服务器附魔书 §6<<<<<<");
            bookLores.add("§6使用方法 §f: §d右键即可打开服务器附魔菜单");
            bookLores.add("§6是否丢弃 §f: §4禁止丢弃!");
            bookMeta.setLore(bookLores);
            book.setItemMeta(bookMeta);
        }
    }

    @Override
    public synchronized void onPluginMessageReceived(String channel, Player player, byte[] message) {

    }
}
