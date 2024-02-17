package me.TahaCheji.mysqlData;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQL {

    public String host;
    public String port;
    public String database;
    public String username;
    public String password;
    public Connection con;
    public SQLGetter sqlGetter;

    public MySQL(String host, String port, String database, String username, String password) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
    }

    static ConsoleCommandSender console = Bukkit.getConsoleSender();

    public void connect() {
        if (!isConnected()) {
            try {
                con = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
                console.sendMessage("\247c[\2476Minepedia-System\247c] \247bMySQL-Verbindung wurde aufgebaut!");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void disconnect() {
        if (isConnected()) {
            try {
                con.close();
                console.sendMessage("\247c[\2476Minepedia-System\247c]\247bMySQL-Verbindung wurde geschlossen!");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isConnected() {
        return (con == null ? false : true);
    }

    public Connection getConnection() {
        return con;
    }

    public String getHost() {
        return host;
    }

    public String getPort() {
        return port;
    }

    public String getDatabase() {
        return database;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Connection getCon() {
        return con;
    }

    public SQLGetter getSqlGetter() {
        return sqlGetter;
    }

    public void setSqlGetter(SQLGetter sqlGetter) {
        this.sqlGetter = sqlGetter;
    }

    public static ConsoleCommandSender getConsole() {
        return console;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setCon(Connection con) {
        this.con = con;
    }

    public static void setConsole(ConsoleCommandSender console) {
        MySQL.console = console;
    }
}
