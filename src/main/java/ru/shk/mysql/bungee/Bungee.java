package ru.shk.mysql.bungee;

import net.md_5.bungee.api.plugin.Plugin;
import ru.shk.mysql.database.MySQL;

import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

public class Bungee extends Plugin {

    @Override
    public void onLoad() {
        Config.loadConfig(getDataFolder());
    }

    @Override
    public void onEnable() {
        getProxy().getScheduler().schedule(this, () -> {
            new Thread(() -> {
                MySQL.sqls.forEach((s, connection) -> {
                    try {
                        connection.createStatement().executeQuery("SELECT 'Hello world'").next();
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                });
            }).start();
        }, 1, 1, TimeUnit.HOURS);
    }
}
