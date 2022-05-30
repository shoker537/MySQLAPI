package ru.shk.mysql.bungee;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import ru.shk.mysql.database.MySQL;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;

public class Config {
    public static boolean ready = false;
    public static String host;
    public static String user;
    public static String pass;
    public static final HashMap<String, HashMap<String, String>> customDatabases = new HashMap<>();

    static void loadConfig(File parent) {
        MySQL.spigot = false;
        ProxyServer.getInstance().getLogger().info("Using Bungee configuration manager");
        File f = new File(parent + File.separator + "config.yml");
        if (!f.exists()) {
            try {
                f.getParentFile().mkdirs();
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Configuration c = null;
        try {
            c = ConfigurationProvider.getProvider(YamlConfiguration.class).load(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (c == null || !c.contains("mysql")) {
            ProxyServer.getInstance().getLogger().log(Level.SEVERE, "!!! Конфиг MySQL не настроен!");
            c.set("mysql.host", "/");
            c.set("mysql.user", "/");
            c.set("mysql.pass", "/");
            try {
                ConfigurationProvider.getProvider(YamlConfiguration.class).save(c, f);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        Configuration finalC = c;
        c.getKeys().forEach(s -> {
            if(!s.equals("mysql")){
                HashMap<String, String> data = new HashMap<>();
                data.put("host",  finalC.getString(s+".host"));
                data.put("user", finalC.getString(s+".user"));
                data.put("pass", finalC.getString(s+".pass"));
                customDatabases.put(s, data);
                ProxyServer.getInstance().getLogger().info("Loaded mysql data '"+s+"'");
            }
        });
        host = c.getString("mysql.host");
        user = c.getString("mysql.user");
        pass = c.getString("mysql.pass");
        if (host == null || user == null || pass == null || host.equals("/") || user.equals("/") || pass.equals("/")) {
            ProxyServer.getInstance().getLogger().log(Level.SEVERE, "!!! Конфиг MySQL не настроен!");
            return;
        }
        ready = true;
    }
}
