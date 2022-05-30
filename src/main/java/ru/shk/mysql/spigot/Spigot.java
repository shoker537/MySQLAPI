package ru.shk.mysql.spigot;

import org.bukkit.plugin.java.JavaPlugin;
import ru.shk.mysql.database.MySQL;

import java.sql.SQLException;

public class Spigot extends JavaPlugin {

    @Override
    public void onLoad() {
        Config.loadConfig(getDataFolder());
    }

    @Override
    public void onEnable() {
        getServer().getScheduler().runTaskTimerAsynchronously(this, () -> MySQL.sqls.forEach((s, connection) -> {
            try {
                connection.createStatement().executeQuery("SELECT 'Hello world'").next();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }), 72000, 72000);
    }
}