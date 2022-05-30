package ru.shk.mysql.database;

import com.mysql.cj.jdbc.MysqlDataSource;
import ru.shk.mysql.spigot.Config;

import java.sql.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class MySQL {
    public static final HashMap<String, Connection> sqls = new HashMap<>();
    public static boolean spigot = true;
    private Connection con;
    private final ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(5);

    public MySQL(String database) {
        String host = spigot ? ru.shk.mysql.spigot.Config.host : ru.shk.mysql.bungee.Config.host;
        String user = spigot ? ru.shk.mysql.spigot.Config.user : ru.shk.mysql.bungee.Config.user;
        String pass = spigot ? ru.shk.mysql.spigot.Config.pass : ru.shk.mysql.bungee.Config.pass;
        String link = "jdbc:mysql://" + host + ":3306/" + database;
        for (String s : sqls.keySet()) {
            if (link.equals(s)) {
                con = sqls.get(s);
                try {
                    if (con == null || con.isClosed()) continue;
                } catch (SQLException throwables) {
                    getLogger().warning(throwables.getMessage());
                    for (StackTraceElement st : throwables.getStackTrace()) getLogger().warning(" > "+st.toString());
                }
                return;
            }
        }
        try {
            con = getMysqlConnection(host, user, pass, database);
            sqls.put(link, con);
        } catch (SQLException e) {
            getLogger().warning(e.getMessage());
            for (StackTraceElement st : e.getStackTrace()) getLogger().warning(" > "+st.toString());
        }
    }

    private Logger getLogger(){
        return spigot?org.bukkit.Bukkit.getLogger():net.md_5.bungee.api.ProxyServer.getInstance().getLogger();
    }

    public MySQL(String section, String db) {
        HashMap<String, String> data = spigot ? Config.customDatabases.get(section) : ru.shk.mysql.bungee.Config.customDatabases.get(section);
        if(data==null){
            throw new NullPointerException("Section '"+section+"' not found in MySQLAPI config!");
        }
        try {
            con = getMysqlConnection(data.get("host"), data.get("user"), data.get("pass"), db);
        } catch (SQLException e) {
            getLogger().warning(e.getMessage());
            for (StackTraceElement st : e.getStackTrace()) getLogger().warning(" > "+st.toString());
        }
    }

    public MySQL(String host, String user, String password, String db) {
        try {
            con = getMysqlConnection(host, user, password, db);
        } catch (SQLException e) {
            getLogger().warning(e.getMessage());
            for (StackTraceElement st : e.getStackTrace()) getLogger().warning(" > "+st.toString());
        }
    }

    private Connection getMysqlConnection(String host, String user, String password, String db) throws SQLException {
//        HikariConfig config = new HikariConfig();
//        config.setJdbcUrl("jdbc:mysql://"+host+":3306/"+db+"?autoReconnect=true&useSSL=false&serverTimeZone=UTC+3&characterEncoding=UTF-8");
//        config.setUsername(user);
//        config.setPassword(password);
//        config.setConnectionTimeout(43100000);
//        config.setKeepaliveTime(43100000);
//        config.setMaxLifetime(43200000);
//        config.addDataSourceProperty("autoReconnectForPools", "true");
//        //config.addDataSourceProperty("autoReconnect", "true");
//        //config.addDataSourceProperty("useSSL", "false");
//        //config.addDataSourceProperty("characterEncoding", "UTF-8");
//        //config.addDataSourceProperty("serverTimezone", "UTC+3");
//        config.addDataSourceProperty("allowPublicKeyRetrieval", "true");
//
//        HikariDataSource ds = new HikariDataSource(config);
//        ds.setKeepaliveTime(Long.MAX_VALUE);
//        return ds.getConnection();
        MysqlDataSource mysqlDS = new MysqlDataSource();
        try {
            mysqlDS.setUseSSL(false);
            mysqlDS.setCharacterEncoding("UTF-8");
            mysqlDS.setServerTimezone("UTC+3");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        mysqlDS.setURL("jdbc:mysql://" + host + ":3306/" + db + "?autoReconnect=true");
        try {
            mysqlDS.setAllowPublicKeyRetrieval(true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        mysqlDS.setUser(user);
        mysqlDS.setPassword(password);
        try {
            executor.setKeepAliveTime(20, TimeUnit.SECONDS);
            return mysqlDS.getConnection();
        } catch (SQLException e) {
            getLogger().warning(e.getMessage());
            for (StackTraceElement st : e.getStackTrace()) getLogger().warning(" > "+st.toString());
            return null;
        }
    }
  /*
  public MySQL(String host, String user, String password, String db){
      Properties properties = new Properties();
      properties.setProperty("user",user);
      properties.setProperty("password",password);
      properties.setProperty("useUnicode","true");
      properties.setProperty("useSSL","false");
      properties.setProperty("characterEncoding","utf-8");
      try {
          con = DriverManager.getConnection("jdbc:mysql://" + host + ":3306/" + db + "?autoReconnect=true", properties);
      } catch (SQLException throwables) {
          throwables.printStackTrace();
      }
  }*/

    private Properties createProperties() {
        Properties properties = new Properties();
        properties.setProperty("useUnicode", "true");
        properties.setProperty("useSSL", "false");
        properties.setProperty("characterEncoding", "utf-8");
        return properties;
    }

    public void Update(final String qry) {
        executor.submit(
                () -> {
                    try {
                        final Statement stmt = con.createStatement();
                        stmt.executeUpdate(qry);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });
    }

    public UpdateBuilder Insert(){
        return new UpdateBuilder(this, "INSERT INTO");
    }
    public UpdateBuilder Update(){
        return new UpdateBuilder(this, "UPDATE ");
    }
    public QueryBuilder Query(){
        return new QueryBuilder(this);
    }

    public ResultSet Query(final String qry) {
        ResultSet rs = null;
        try {
            final Statement stmt = con.createStatement();
            rs = stmt.executeQuery(qry);
        } catch (SQLException e) {
            getLogger().warning(e.getMessage());
            for (StackTraceElement st : e.getStackTrace()) getLogger().warning(" > "+st.toString());
        }
        return rs;
    }

    public int QueryInt(String qry, int fallback){
        ResultSet rs = Query(qry);
        try {
            if(rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            getLogger().warning(e.getMessage());
            for (StackTraceElement st : e.getStackTrace()) getLogger().warning(" > "+st.toString());
        }
        return fallback;
    }

    public PreparedStatement getStatement(String st) throws SQLException {
        return con.prepareStatement(st);
    }

    public PreparedStatement getStatementWithGeneratedKeys(String st) throws SQLException {
        return con.prepareStatement(st, Statement.RETURN_GENERATED_KEYS);
    }

    public void UpdateSync(String qry) {
        try {
            final Statement stmt = con.createStatement();
            stmt.executeUpdate(qry);
        } catch (SQLException e) {
            getLogger().warning(e.getMessage());
            for (StackTraceElement st : e.getStackTrace()) getLogger().warning(" > "+st.toString());
        }
    }

    public void UpdateExcepted(String qry) throws SQLException {
        final Statement stmt = con.createStatement();
        stmt.executeUpdate(qry);
    }

    public boolean isConnected(){
        try {
            return con!=null && !con.isClosed();
        } catch (SQLException throwables) {
            return false;
        }
    }

    public void disconnect() throws SQLException {
        con.close();
    }
}
