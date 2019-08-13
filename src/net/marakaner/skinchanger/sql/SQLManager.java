package net.marakaner.skinchanger.sql;

import net.marakaner.skinchanger.SkinChanger;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class SQLManager {

    private String host;
    private String port;
    private String database;
    private String username;
    private String password;

    private Connection connection;

    public SQLManager(String host, String port, String database, String username, String password) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
    }

    public void executeUpdate(String query) {
        new BukkitRunnable() {
            @Override
            public void run() {
                PreparedStatement preparedStatement = null;
                try {
                    preparedStatement = connection.prepareStatement(query);
                    preparedStatement.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        preparedStatement.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.runTaskAsynchronously(SkinChanger.getInstance());
    }

    public void executeQuery(String query, Consumer<Map<String, Object>> consumer, String... values) {
        new BukkitRunnable() {
            @Override
            public void run() {
                PreparedStatement preparedStatement = null;
                ResultSet resultSet = null;
                try {
                    preparedStatement = connection.prepareStatement(query);
                    resultSet = preparedStatement.executeQuery();

                    Map<String, Object> map = new HashMap<>();

                    if (resultSet.next()) {
                        for(String all : values) {
                            map.put(all, resultSet.getObject(all));
                        }
                    }

                    consumer.accept(map);
                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        preparedStatement.close();
                        resultSet.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.runTaskAsynchronously(SkinChanger.getInstance());
    }

    public void executeMultiUpdate(String... query) {
        new BukkitRunnable() {
            @Override
            public void run() {
                for(String all : query) {
                    PreparedStatement preparedStatement = null;
                    try {
                        preparedStatement = connection.prepareStatement(all);
                        preparedStatement.executeUpdate();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            preparedStatement.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }.runTaskAsynchronously(SkinChanger.getInstance());
    }

    public boolean isConnected() {
        return this.connection != null;
    }

    public void connect() {
        if(!isConnected()) {
            try {
                this.connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?autoReconnect=true", username, password);
                System.out.println("The plugin is connected with the sql");
            } catch (SQLException e) {
            }
        }
    }

    public void disconnect() {
        try {
            this.connection.close();
            System.out.println("The plugin is disconnected with the sql");
        } catch (SQLException e) {
        }
    }

    public String getDatabase() {
        return database;
    }

    public String getHost() {
        return host;
    }

    public String getPassword() {
        return password;
    }

    public String getPort() {
        return port;
    }

    public String getUsername() {
        return username;
    }
}
