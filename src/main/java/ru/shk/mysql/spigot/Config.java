package ru.shk.mysql.spigot;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class Config {
    public static boolean ready = false;
    public static String host;
    public static String user;
    public static String pass;

    public static final HashMap<String, HashMap<String, String>> customDatabases = new HashMap<>();

    static void loadConfig(File parent) {
        Bukkit.getLogger().info("Using Spigot configuration manager");
        File f = new File(parent + File.separator + "config.yml");
        if (!f.exists()) {
            try {
                f.getParentFile().mkdirs();
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        YamlConfiguration c = null;
        try {
            c = new YamlConfiguration();
            c.load(f);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
        if (!c.contains("mysql")) {
            System.out.println("!!! Конфиг MySQL не настроен!");
            c.set("mysql.host", "/");
            c.set("mysql.user", "/");
            c.set("mysql.pass", "/");
            try {
                c.save(f);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        YamlConfiguration finalC = c;
        c.getKeys(false).forEach(s -> {
            if(!s.equals("mysql")){
                HashMap<String, String> data = new HashMap<>();
                data.put("host", finalC.getString(s+".host"));
                data.put("user", finalC.getString(s+".user"));
                data.put("pass", finalC.getString(s+".pass"));
                customDatabases.put(s, data);
                Bukkit.getLogger().info("Loaded mysql data '"+s+"'");
            }
        });
        host = c.getString("mysql.host");
        user = c.getString("mysql.user");
        pass = c.getString("mysql.pass");
        if (host == null
                || user == null
                || pass == null
                || host.equals("/")
                || user.equals("/")
                || pass.equals("/")) {
            System.out.println("!!! Конфиг MySQL не настроен!");
            return;
        }
        ready = true;
    }
}
