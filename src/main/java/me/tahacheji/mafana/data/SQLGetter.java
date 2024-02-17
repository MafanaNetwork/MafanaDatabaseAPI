package me.TahaCheji.mysqlData;
import me.TahaCheji.MainAPI;
import org.bukkit.Bukkit;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class SQLGetter {

    public MySQL mySQL;
    public String tableString;
    public List<MysqlValue> mysqlValues = new ArrayList<>();

    public SQLGetter(MySQL plugin) {
        this.mySQL = plugin;
    }

    public void createTable(String tableName, MysqlValue... mysqlValues) {
        tableString = tableName;
        PreparedStatement ps;
        try {
            String x3 = "CREATE TABLE IF NOT EXISTS " + tableName + " (id INT AUTO_INCREMENT PRIMARY KEY, ";
            for (MysqlValue mysqlValue : mysqlValues) {
                if (mysqlValues.length != 1) {
                    if (mysqlValue.getStringValue() != null) {
                        if(mysqlValue.getX() != null) {
                            x3 = x3 + mysqlValue.getName() + " " + mysqlValue.getX() + ",";
                        } else {
                            x3 = x3 + mysqlValue.getName() + " VARCHAR(100),";
                        }
                        continue;
                    }
                    if (mysqlValue.getIntValue() != null) {
                        if(mysqlValue.getX() != null) {
                            x3 = x3 + mysqlValue.getName() + " " + mysqlValue.getX() + ",";
                        } else {
                            x3 = x3 + mysqlValue.getName() + " INT(100),";
                        }
                        continue;
                    }
                    if (mysqlValue.getDoubleValue() != null) {
                        if(mysqlValue.getX() != null) {
                            x3 = x3 + mysqlValue.getName() + " " + mysqlValue.getX() + ",";
                        } else {
                            x3 = x3 + mysqlValue.getName() + " DOUBLE(5,0),";
                        }
                        continue;
                    }
                    if (mysqlValue.getUuidValue() != null) {
                        if(mysqlValue.getX() != null) {
                            x3 = x3 + mysqlValue.getName() + " " + mysqlValue.getX() + ",";
                        } else {
                            x3 = x3 + mysqlValue.getName() + " VARCHAR(100),";
                        }
                    }
                    getMysqlValues().add(mysqlValue);
                } else {
                    if (mysqlValue.getStringValue() != null) {
                        if(mysqlValue.getX() != null) {
                            x3 = x3 + mysqlValue.getName() + " " + mysqlValue.getX() + ",";
                        } else {
                            x3 = x3 + mysqlValue.getName() + " VARCHAR(100)";
                        }
                        continue;
                    }
                    if (mysqlValue.getIntValue() != null) {
                        if(mysqlValue.getX() != null) {
                            x3 = x3 + mysqlValue.getName() + " " + mysqlValue.getX() + ",";
                        } else {
                            x3 = x3 + mysqlValue.getName() + " INT(100)";
                        }
                        continue;
                    }
                    if (mysqlValue.getDoubleValue() != null) {
                        if(mysqlValue.getX() != null) {
                            x3 = x3 + mysqlValue.getName() + " " + mysqlValue.getX() + ",";
                        } else {
                            x3 = x3 + mysqlValue.getName() + " DOUBLE(5,0)";
                        }
                        continue;
                    }
                    if (mysqlValue.getUuidValue() != null) {
                        if(mysqlValue.getX() != null) {
                            x3 = x3 + mysqlValue.getName() + " " + mysqlValue.getX() + ",";
                        } else {
                            x3 = x3 + mysqlValue.getName() + " VARCHAR(100)";
                        }
                    }
                    getMysqlValues().add(mysqlValue);
                }
            }
            if (mysqlValues.length != 1) {
                x3 = x3 + "UUID VARCHAR(100)";
            }
            x3 = x3 + ")";
            ps = mySQL.getConnection().prepareStatement(x3);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean exists(UUID uuid) {
        try {
            PreparedStatement ps = getMySQL().getConnection().prepareStatement("SELECT * FROM " + tableString + " WHERE UUID=?");
            ps.setString(1, uuid.toString());
            ResultSet resultSet = ps.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public CompletableFuture<Boolean> existsAsync(UUID uuid) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();

        Bukkit.getScheduler().runTaskAsynchronously(MainAPI.getInstance(), () -> {
            try {
                PreparedStatement ps = getMySQL().getConnection().prepareStatement("SELECT * FROM " + tableString + " WHERE UUID=?");
                ps.setString(1, uuid.toString());
                ResultSet resultSet = ps.executeQuery();
                boolean exists = resultSet.next();
                future.complete(exists);
            } catch (SQLException e) {
                e.printStackTrace();
                future.completeExceptionally(e);
            }
        });

        return future;
    }

    public void setInt(MysqlValue mysqlValue) {
        try {
            if(!exists(mysqlValue.getMysqlUUID())) {
                PreparedStatement ps2 = getMySQL().getConnection().prepareStatement("INSERT IGNORE INTO " + tableString + " (" + mysqlValue.getName() + ",UUID) VALUES (?,?)");
                ps2.setInt(1, mysqlValue.getIntValue());
                ps2.setString(2, mysqlValue.getMysqlUUID().toString());
                ps2.executeUpdate();
            } else {
                PreparedStatement ps = getMySQL().getConnection().prepareStatement("UPDATE " + tableString + " SET " + mysqlValue.getName() + "=? " + "WHERE UUID=?");
                ps.setInt(1, mysqlValue.getIntValue());
                ps.setString(2, mysqlValue.getMysqlUUID().toString());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeInt(int i, MysqlValue mysqlValue) {
        try {
            PreparedStatement ps = getMySQL().getConnection().prepareStatement("DELETE FROM " + tableString  +" WHERE " + mysqlValue.getName() + " = ?");
            ps.setInt(1, i);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle the exception appropriately, such as logging an error or notifying the player about the failure.
        }
    }
    public int getInt(UUID uuid, MysqlValue mysqlValue) {
        try {
            PreparedStatement ps = getMySQL().getConnection().prepareStatement("SELECT " + mysqlValue.getName() + " FROM "  + tableString + " WHERE UUID=?");
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            int xp = 0;
            if(rs.next()) {
                xp = rs.getInt(mysqlValue.getName());
                return xp;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<Integer> getAllIntager(UUID uuid, MysqlValue mysqlValue) throws SQLException {
        List<Integer> x = new ArrayList<>();
        PreparedStatement ps = getMySQL().getConnection().prepareStatement("SELECT " + mysqlValue.getName() + " FROM " + tableString + " WHERE UUID=?");
        ps.setString(1, uuid.toString());
        ResultSet resultSet = ps.executeQuery();
        try {
            while (resultSet.next()) {
                Integer xs = resultSet.getInt(mysqlValue.getName());
                x.add(xs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return x;
    }

    public List<Integer> getAllIntager(MysqlValue mysqlValue) throws SQLException {
        List<Integer> x = new ArrayList();
        PreparedStatement ps = this.getMySQL().getConnection().prepareStatement("SELECT " + mysqlValue.getName() + " FROM " + this.tableString);
        ResultSet resultSet = ps.executeQuery();

        try {
            while(resultSet.next()) {
                Integer xs = resultSet.getInt(mysqlValue.getName());
                x.add(xs);
            }
        } catch (SQLException var6) {
            var6.printStackTrace();
        }

        return x;
    }

    public CompletableFuture<Void> setIntAsync(MysqlValue mysqlValue) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        Bukkit.getScheduler().runTaskAsynchronously(MainAPI.getInstance(), () -> {
            try {
                if (!exists(mysqlValue.getMysqlUUID())) {
                    PreparedStatement ps2 = getMySQL().getConnection().prepareStatement("INSERT IGNORE INTO " + tableString + " (" + mysqlValue.getName() + ",UUID) VALUES (?,?)");
                    ps2.setInt(1, mysqlValue.getIntValue());
                    ps2.setString(2, mysqlValue.getMysqlUUID().toString());
                    ps2.executeUpdate();
                } else {
                    PreparedStatement ps = getMySQL().getConnection().prepareStatement("UPDATE " + tableString + " SET " + mysqlValue.getName() + "=? " + "WHERE UUID=?");
                    ps.setInt(1, mysqlValue.getIntValue());
                    ps.setString(2, mysqlValue.getMysqlUUID().toString());
                    ps.executeUpdate();
                }
                future.complete(null);
            } catch (SQLException e) {
                e.printStackTrace();
                future.completeExceptionally(e);
            }
        });

        return future;
    }

    public CompletableFuture<Void> removeIntAsync(int i, MysqlValue mysqlValue) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        Bukkit.getScheduler().runTaskAsynchronously(MainAPI.getInstance(), () -> {
            try {
                PreparedStatement ps = getMySQL().getConnection().prepareStatement("DELETE FROM " + tableString + " WHERE " + mysqlValue.getName() + " = ?");
                ps.setInt(1, i);
                ps.executeUpdate();
                ps.close();
                future.complete(null);
            } catch (SQLException e) {
                e.printStackTrace();
                future.completeExceptionally(e);
            }
        });

        return future;
    }

    public CompletableFuture<Integer> getIntAsync(UUID uuid, MysqlValue mysqlValue) {
        CompletableFuture<Integer> future = new CompletableFuture<>();

        Bukkit.getScheduler().runTaskAsynchronously(MainAPI.getInstance(), () -> {
            try {
                PreparedStatement ps = getMySQL().getConnection().prepareStatement("SELECT " + mysqlValue.getName() + " FROM " + tableString + " WHERE UUID=?");
                ps.setString(1, uuid.toString());
                ResultSet rs = ps.executeQuery();
                int xp = 0;
                if (rs.next()) {
                    xp = rs.getInt(mysqlValue.getName());
                }
                future.complete(xp);
            } catch (SQLException e) {
                e.printStackTrace();
                future.completeExceptionally(e);
            }
        });

        return future;
    }

    public CompletableFuture<List<Integer>> getAllIntegerAsync(UUID uuid, MysqlValue mysqlValue) {
        CompletableFuture<List<Integer>> future = new CompletableFuture<>();

        Bukkit.getScheduler().runTaskAsynchronously(MainAPI.getInstance(), () -> {
            List<Integer> x = new ArrayList<>();
            try {
                PreparedStatement ps = getMySQL().getConnection().prepareStatement("SELECT " + mysqlValue.getName() + " FROM " + tableString + " WHERE UUID=?");
                ps.setString(1, uuid.toString());
                ResultSet resultSet = ps.executeQuery();
                while (resultSet.next()) {
                    Integer xs = resultSet.getInt(mysqlValue.getName());
                    x.add(xs);
                }
                future.complete(x);
            } catch (SQLException e) {
                e.printStackTrace();
                future.completeExceptionally(e);
            }
        });

        return future;
    }

    public CompletableFuture<List<Integer>> getAllIntegerAsync(MysqlValue mysqlValue) {
        CompletableFuture<List<Integer>> future = new CompletableFuture<>();

        Bukkit.getScheduler().runTaskAsynchronously(MainAPI.getInstance(), () -> {
            List<Integer> x = new ArrayList<>();
            try {
                PreparedStatement ps = this.getMySQL().getConnection().prepareStatement("SELECT " + mysqlValue.getName() + " FROM " + this.tableString);
                ResultSet resultSet = ps.executeQuery();
                while (resultSet.next()) {
                    Integer xs = resultSet.getInt(mysqlValue.getName());
                    x.add(xs);
                }
                future.complete(x);
            } catch (SQLException e) {
                e.printStackTrace();
                future.completeExceptionally(e);
            }
        });

        return future;
    }


    public void setString(MysqlValue mysqlValue) {
        try {
            if(!exists(mysqlValue.getMysqlUUID())) {
                PreparedStatement ps2 = getMySQL().getConnection().prepareStatement("INSERT IGNORE INTO " + tableString + " (" + mysqlValue.getName() + ",UUID) VALUES (?,?)");
                ps2.setString(1, mysqlValue.getStringValue());
                ps2.setString(2, mysqlValue.getMysqlUUID().toString());
                ps2.executeUpdate();
            } else {
                PreparedStatement ps = getMySQL().getConnection().prepareStatement("UPDATE " + tableString + " SET " + mysqlValue.getName() + "=? " + "WHERE UUID=?");
                ps.setString(1, mysqlValue.getStringValue());
                ps.setString(2, mysqlValue.getMysqlUUID().toString());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeString(String i, MysqlValue mysqlValue) {
        try {
            PreparedStatement ps = getMySQL().getConnection().prepareStatement("DELETE FROM " + tableString  +" WHERE " + mysqlValue.getName() + " = ?");
            ps.setString(1, i);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public String getString(UUID uuid, MysqlValue mysqlValue) {
        try {
            PreparedStatement ps = getMySQL().getConnection().prepareStatement("SELECT " + mysqlValue.getName() + " FROM "  + tableString + " WHERE UUID=?");
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            String xp = "";
            if(rs.next()) {
                xp = rs.getString(mysqlValue.getName());
                return xp;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }

    public List<String> getAllString(MysqlValue mysqlValue) throws SQLException {
        List<String> x = new ArrayList<>();
        PreparedStatement ps = getMySQL().getConnection().prepareStatement("SELECT " + mysqlValue.getName() + " FROM " + tableString);
        ResultSet resultSet = ps.executeQuery();
        try {
            while (resultSet.next()) {
                String xs = resultSet.getString(mysqlValue.getName());
                x.add(xs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return x;
    }

    public List<String> getAllString(UUID uuid, MysqlValue mysqlValue) {
        List<String> values = new ArrayList<>();
        try {
            PreparedStatement ps = this.getMySQL().getConnection().prepareStatement("SELECT " + mysqlValue.getName() + " FROM " + this.tableString + " WHERE UUID=?");
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String xp = rs.getString(mysqlValue.getName());
                values.add(xp);
            }
        } catch (SQLException var6) {
            var6.printStackTrace();
        }

        return values;
    }

    public CompletableFuture<Void> setStringAsync(MysqlValue mysqlValue) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        Bukkit.getScheduler().runTaskAsynchronously(MainAPI.getInstance(), () -> {
            try {
                if (!exists(mysqlValue.getMysqlUUID())) {
                    PreparedStatement ps2 = getMySQL().getConnection().prepareStatement("INSERT IGNORE INTO " + tableString + " (" + mysqlValue.getName() + ",UUID) VALUES (?,?)");
                    ps2.setString(1, mysqlValue.getStringValue());
                    ps2.setString(2, mysqlValue.getMysqlUUID().toString());
                    ps2.executeUpdate();
                } else {
                    PreparedStatement ps = getMySQL().getConnection().prepareStatement("UPDATE " + tableString + " SET " + mysqlValue.getName() + "=? " + "WHERE UUID=?");
                    ps.setString(1, mysqlValue.getStringValue());
                    ps.setString(2, mysqlValue.getMysqlUUID().toString());
                    ps.executeUpdate();
                }
                future.complete(null);
            } catch (SQLException e) {
                e.printStackTrace();
                future.completeExceptionally(e);
            }
        });

        return future;
    }

    public CompletableFuture<Void> removeStringAsync(String i, MysqlValue mysqlValue) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        Bukkit.getScheduler().runTaskAsynchronously(MainAPI.getInstance(), () -> {
            try {
                PreparedStatement ps = getMySQL().getConnection().prepareStatement("DELETE FROM " + tableString + " WHERE " + mysqlValue.getName() + " = ?");
                ps.setString(1, i);
                ps.executeUpdate();
                ps.close();
                future.complete(null);
            } catch (SQLException e) {
                e.printStackTrace();
                future.completeExceptionally(e);
            }
        });

        return future;
    }

    public CompletableFuture<String> getStringAsync(UUID uuid, MysqlValue mysqlValue) {
        CompletableFuture<String> future = new CompletableFuture<>();

        Bukkit.getScheduler().runTaskAsynchronously(MainAPI.getInstance(), () -> {
            try {
                PreparedStatement ps = getMySQL().getConnection().prepareStatement("SELECT " + mysqlValue.getName() + " FROM " + tableString + " WHERE UUID=?");
                ps.setString(1, uuid.toString());
                ResultSet rs = ps.executeQuery();
                String xp = "";
                if (rs.next()) {
                    xp = rs.getString(mysqlValue.getName());
                }
                future.complete(xp);
            } catch (SQLException e) {
                e.printStackTrace();
                future.completeExceptionally(e);
            }
        });

        return future;
    }

    public CompletableFuture<List<String>> getAllStringAsync(MysqlValue mysqlValue) {
        CompletableFuture<List<String>> future = new CompletableFuture<>();

        Bukkit.getScheduler().runTaskAsynchronously(MainAPI.getInstance(), () -> {
            List<String> values = new ArrayList<>();
            try {
                PreparedStatement ps = getMySQL().getConnection().prepareStatement("SELECT " + mysqlValue.getName() + " FROM " + tableString);
                ResultSet resultSet = ps.executeQuery();
                while (resultSet.next()) {
                    String xs = resultSet.getString(mysqlValue.getName());
                    values.add(xs);
                }
                future.complete(values);
            } catch (SQLException e) {
                e.printStackTrace();
                future.completeExceptionally(e);
            }
        });

        return future;
    }

    public CompletableFuture<List<String>> getAllStringAsync(UUID uuid, MysqlValue mysqlValue) {
        CompletableFuture<List<String>> future = new CompletableFuture<>();

        Bukkit.getScheduler().runTaskAsynchronously(MainAPI.getInstance(), () -> {
            List<String> values = new ArrayList<>();
            try {
                PreparedStatement ps = getMySQL().getConnection().prepareStatement("SELECT " + mysqlValue.getName() + " FROM " + tableString + " WHERE UUID=?");
                ps.setString(1, uuid.toString());
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    String xp = rs.getString(mysqlValue.getName());
                    values.add(xp);
                }
                future.complete(values);
            } catch (SQLException e) {
                e.printStackTrace();
                future.completeExceptionally(e);
            }
        });

        return future;
    }



    public void setDouble(MysqlValue mysqlValue) {
        try {
            if(!exists(mysqlValue.getMysqlUUID())) {
                PreparedStatement ps2 = getMySQL().getConnection().prepareStatement("INSERT IGNORE INTO " + tableString + " (" + mysqlValue.getName() + ",UUID) VALUES (?,?)");
                ps2.setDouble(1, mysqlValue.getDoubleValue());
                ps2.setString(2, mysqlValue.getMysqlUUID().toString());
                ps2.executeUpdate();
            } else {
                PreparedStatement ps = getMySQL().getConnection().prepareStatement("UPDATE " + tableString + " SET " + mysqlValue.getName() + "=? " + "WHERE UUID=?");
                ps.setDouble(1, mysqlValue.getDoubleValue());
                ps.setString(2, mysqlValue.getMysqlUUID().toString());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeDouble(Double i, MysqlValue mysqlValue) {
        try {
            PreparedStatement ps = getMySQL().getConnection().prepareStatement("DELETE FROM " + tableString  +" WHERE " + mysqlValue.getName() + " = ?");
            ps.setDouble(1, i);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public Double getDouble(UUID uuid, MysqlValue mysqlValue) {
        double xp = 0;
        try {
            PreparedStatement ps = getMySQL().getConnection().prepareStatement("SELECT " + mysqlValue.getName() + " FROM " + tableString + " WHERE UUID=?");
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            xp = 0.0;
            if (rs.next()) {
                xp = rs.getDouble(mysqlValue.getName());
                return xp;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return xp;
    }

    public List<Double> getAllDouble(MysqlValue mysqlValue) throws SQLException {
        List<Double> x = new ArrayList<>();
        PreparedStatement ps = getMySQL().getConnection().prepareStatement("SELECT " + mysqlValue.getName() + " FROM " + tableString);
        ResultSet resultSet = ps.executeQuery();
        try {
            while (resultSet.next()) {
                Double xs = resultSet.getDouble(mysqlValue.getName());
                x.add(xs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return x;
    }

    public List<Double> getAllDouble(UUID uuid, MysqlValue mysqlValue) {
        List<Double> values = new ArrayList<>();
        try {
            PreparedStatement ps = this.getMySQL().getConnection().prepareStatement("SELECT " + mysqlValue.getName() + " FROM " + this.tableString + " WHERE UUID=?");
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Double xp = rs.getDouble(mysqlValue.getName());
                values.add(xp);
            }
        } catch (SQLException var6) {
            var6.printStackTrace();
        }

        return values;
    }

    public CompletableFuture<Void> setDoubleAsync(MysqlValue mysqlValue) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        Bukkit.getScheduler().runTaskAsynchronously(MainAPI.getInstance(), () -> {
            try {
                if (!exists(mysqlValue.getMysqlUUID())) {
                    PreparedStatement ps2 = getMySQL().getConnection().prepareStatement("INSERT IGNORE INTO " + tableString + " (" + mysqlValue.getName() + ",UUID) VALUES (?,?)");
                    ps2.setDouble(1, mysqlValue.getDoubleValue());
                    ps2.setString(2, mysqlValue.getMysqlUUID().toString());
                    ps2.executeUpdate();
                } else {
                    PreparedStatement ps = getMySQL().getConnection().prepareStatement("UPDATE " + tableString + " SET " + mysqlValue.getName() + "=? " + "WHERE UUID=?");
                    ps.setDouble(1, mysqlValue.getDoubleValue());
                    ps.setString(2, mysqlValue.getMysqlUUID().toString());
                    ps.executeUpdate();
                }
                future.complete(null);
            } catch (SQLException e) {
                e.printStackTrace();
                future.completeExceptionally(e);
            }
        });

        return future;
    }

    public CompletableFuture<Void> removeDoubleAsync(Double i, MysqlValue mysqlValue) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        Bukkit.getScheduler().runTaskAsynchronously(MainAPI.getInstance(), () -> {
            try {
                PreparedStatement ps = getMySQL().getConnection().prepareStatement("DELETE FROM " + tableString + " WHERE " + mysqlValue.getName() + " = ?");
                ps.setDouble(1, i);
                ps.executeUpdate();
                ps.close();
                future.complete(null);
            } catch (SQLException e) {
                e.printStackTrace();
                future.completeExceptionally(e);
            }
        });

        return future;
    }

    public CompletableFuture<Double> getDoubleAsync(UUID uuid, MysqlValue mysqlValue) {
        CompletableFuture<Double> future = new CompletableFuture<>();

        Bukkit.getScheduler().runTaskAsynchronously(MainAPI.getInstance(), () -> {
            try {
                PreparedStatement ps = getMySQL().getConnection().prepareStatement("SELECT " + mysqlValue.getName() + " FROM " + tableString + " WHERE UUID=?");
                ps.setString(1, uuid.toString());
                ResultSet rs = ps.executeQuery();
                double xp = 0.0;
                if (rs.next()) {
                    xp = rs.getDouble(mysqlValue.getName());
                }
                future.complete(xp);
            } catch (SQLException e) {
                e.printStackTrace();
                future.completeExceptionally(e);
            }
        });

        return future;
    }

    public CompletableFuture<List<Double>> getAllDoubleAsync(MysqlValue mysqlValue) {
        CompletableFuture<List<Double>> future = new CompletableFuture<>();

        Bukkit.getScheduler().runTaskAsynchronously(MainAPI.getInstance(), () -> {
            List<Double> values = new ArrayList<>();
            try {
                PreparedStatement ps = getMySQL().getConnection().prepareStatement("SELECT " + mysqlValue.getName() + " FROM " + tableString);
                ResultSet resultSet = ps.executeQuery();
                while (resultSet.next()) {
                    Double xs = resultSet.getDouble(mysqlValue.getName());
                    values.add(xs);
                }
                future.complete(values);
            } catch (SQLException e) {
                e.printStackTrace();
                future.completeExceptionally(e);
            }
        });

        return future;
    }


    public void setUUID(MysqlValue mysqlValue) {
        try {
            if(!exists(mysqlValue.getMysqlUUID())) {
                PreparedStatement ps2 = getMySQL().getConnection().prepareStatement("INSERT IGNORE INTO " + tableString + " (" + mysqlValue.getName() + ",UUID) VALUES (?,?)");
                ps2.setString(1, mysqlValue.getUuidValue().toString());
                ps2.setString(2, mysqlValue.getMysqlUUID().toString());
                ps2.executeUpdate();
            } else {
                PreparedStatement ps = getMySQL().getConnection().prepareStatement("UPDATE " + tableString + " SET " + mysqlValue.getName() + "=? " + "WHERE UUID=?");
                ps.setString(1, mysqlValue.getUuidValue().toString());
                ps.setString(2, mysqlValue.getMysqlUUID().toString());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void removeUUID(UUID i, MysqlValue mysqlValue) {
        try {
            PreparedStatement ps = getMySQL().getConnection().prepareStatement("DELETE FROM " + tableString  +" WHERE " + mysqlValue.getName() + " = ?");
            ps.setString(1, i.toString());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public UUID getUUID(UUID uuid, MysqlValue mysqlValue) {
        UUID xp = null;
        try {
            PreparedStatement ps = getMySQL().getConnection().prepareStatement("SELECT " + mysqlValue.getName() + " FROM " + tableString + " WHERE UUID=?");
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                xp = UUID.fromString(rs.getString(mysqlValue.getName()));
                return xp;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return xp;
    }

    public List<UUID> getAllUUID(MysqlValue mysqlValue) throws SQLException {
        List<UUID> players = new ArrayList<>();
        PreparedStatement ps = getMySQL().getConnection().prepareStatement("SELECT " + mysqlValue.getName() + " FROM " + tableString);
        ResultSet resultSet = ps.executeQuery();
        try {
            while (resultSet.next()) {
                UUID playerUUID = UUID.fromString(resultSet.getString(mysqlValue.getName()));
                players.add(playerUUID);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return players;
    }

    public List<UUID> getAllUUID(UUID uuid, MysqlValue mysqlValue) {
        List<UUID> values = new ArrayList<>();
        try {
            PreparedStatement ps = this.getMySQL().getConnection().prepareStatement("SELECT " + mysqlValue.getName() + " FROM " + this.tableString + " WHERE UUID=?");
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                UUID xp = UUID.fromString(rs.getString(mysqlValue.getName()));
                values.add(xp);
            }
        } catch (SQLException var6) {
            var6.printStackTrace();
        }

        return values;
    }

    public CompletableFuture<Void> setUUIDAsync(MysqlValue mysqlValue) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        Bukkit.getScheduler().runTaskAsynchronously(MainAPI.getInstance(), () -> {
            try {
                if (!exists(mysqlValue.getMysqlUUID())) {
                    PreparedStatement ps2 = getMySQL().getConnection().prepareStatement("INSERT IGNORE INTO " + tableString + " (" + mysqlValue.getName() + ",UUID) VALUES (?,?)");
                    ps2.setString(1, mysqlValue.getUuidValue().toString());
                    ps2.setString(2, mysqlValue.getMysqlUUID().toString());
                    ps2.executeUpdate();
                } else {
                    PreparedStatement ps = getMySQL().getConnection().prepareStatement("UPDATE " + tableString + " SET " + mysqlValue.getName() + "=? WHERE UUID=?");
                    ps.setString(1, mysqlValue.getUuidValue().toString());
                    ps.setString(2, mysqlValue.getMysqlUUID().toString());
                    ps.executeUpdate();
                }
                future.complete(null);
            } catch (SQLException e) {
                e.printStackTrace();
                future.completeExceptionally(e);
            }
        });

        return future;
    }

    public CompletableFuture<Void> removeUUIDAsync(UUID i, MysqlValue mysqlValue) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        Bukkit.getScheduler().runTaskAsynchronously(MainAPI.getInstance(), () -> {
            try {
                PreparedStatement ps = getMySQL().getConnection().prepareStatement("DELETE FROM " + tableString + " WHERE " + mysqlValue.getName() + " = ?");
                ps.setString(1, i.toString());
                ps.executeUpdate();
                ps.close();
                future.complete(null);
            } catch (SQLException e) {
                e.printStackTrace();
                future.completeExceptionally(e);
            }
        });

        return future;
    }

    public CompletableFuture<UUID> getUUIDAsync(UUID uuid, MysqlValue mysqlValue) {
        CompletableFuture<UUID> future = new CompletableFuture<>();

        Bukkit.getScheduler().runTaskAsynchronously(MainAPI.getInstance(), () -> {
            UUID xp = null;
            try {
                PreparedStatement ps = getMySQL().getConnection().prepareStatement("SELECT " + mysqlValue.getName() + " FROM " + tableString + " WHERE UUID=?");
                ps.setString(1, uuid.toString());
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    xp = UUID.fromString(rs.getString(mysqlValue.getName()));
                }
                future.complete(xp);
            } catch (SQLException e) {
                e.printStackTrace();
                future.completeExceptionally(e);
            }
        });

        return future;
    }

    public CompletableFuture<List<UUID>> getAllUUIDAsync(MysqlValue mysqlValue) {
        CompletableFuture<List<UUID>> future = new CompletableFuture<>();

        Bukkit.getScheduler().runTaskAsynchronously(MainAPI.getInstance(), () -> {
            List<UUID> players = new ArrayList<>();
            try {
                PreparedStatement ps = getMySQL().getConnection().prepareStatement("SELECT " + mysqlValue.getName() + " FROM " + tableString);
                ResultSet resultSet = ps.executeQuery();
                while (resultSet.next()) {
                    UUID playerUUID = UUID.fromString(resultSet.getString(mysqlValue.getName()));
                    players.add(playerUUID);
                }
                future.complete(players);
            } catch (SQLException e) {
                e.printStackTrace();
                future.completeExceptionally(e);
            }
        });

        return future;
    }


    public void setObject(MysqlValue mysqlValue) {
        try {
            if(!exists(mysqlValue.getMysqlUUID())) {
                PreparedStatement ps2 = getMySQL().getConnection().prepareStatement("INSERT IGNORE INTO " + tableString + " (" + mysqlValue.getName() + ",UUID) VALUES (?,?)");
                ps2.setString(1, mysqlValue.getUuidValue().toString());
                ps2.setObject(2, mysqlValue.getObjectValue());
                ps2.executeUpdate();
            } else {
                PreparedStatement ps = getMySQL().getConnection().prepareStatement("UPDATE " + tableString + " SET " + mysqlValue.getName() + "=? " + "WHERE UUID=?");
                ps.setString(1, mysqlValue.getUuidValue().toString());
                ps.setObject(2, mysqlValue.getObjectValue());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void removeObject(Object i, MysqlValue mysqlValue) {
        try {
            PreparedStatement ps = getMySQL().getConnection().prepareStatement("DELETE FROM " + tableString  +" WHERE " + mysqlValue.getName() + " = ?");
            ps.setObject(1, i);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle the exception appropriately, such as logging an error or notifying the player about the failure.
        }
    }
    public Object getObject(UUID uuid, MysqlValue mysqlValue) {
        Object xp;
        try {
            PreparedStatement ps = getMySQL().getConnection().prepareStatement("SELECT " + mysqlValue.getName() + " FROM " + tableString + " WHERE UUID=?");
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                xp = rs.getObject(mysqlValue.getName());
                return xp;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }



    public List<Object> getAllObject(MysqlValue mysqlValue) throws SQLException {
        List<Object> x = new ArrayList<>();
        PreparedStatement ps = getMySQL().getConnection().prepareStatement("SELECT " + mysqlValue.getName() + " FROM " + tableString);
        ResultSet resultSet = ps.executeQuery();
        try {
            while (resultSet.next()) {
                Object playerUUID = resultSet.getObject(mysqlValue.getName());
                x.add(playerUUID);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return x;
    }

    public List<Object> getAllObject(UUID uuid, MysqlValue mysqlValue) {
        List<Object> values = new ArrayList<>();
        try {
            PreparedStatement ps = this.getMySQL().getConnection().prepareStatement("SELECT " + mysqlValue.getName() + " FROM " + this.tableString + " WHERE UUID=?");
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Object xp = rs.getObject(mysqlValue.getName());
                values.add(xp);
            }
        } catch (SQLException var6) {
            var6.printStackTrace();
        }

        return values;
    }

    public CompletableFuture<Object> getObjectAsync(UUID uuid, MysqlValue mysqlValue) {
        CompletableFuture<Object> future = new CompletableFuture<>();

        Bukkit.getScheduler().runTaskAsynchronously(MainAPI.getInstance(), () -> {
            try {
                PreparedStatement ps = getMySQL().getConnection().prepareStatement("SELECT " + mysqlValue.getName() + " FROM " + tableString + " WHERE UUID=?");
                ps.setString(1, uuid.toString());
                ResultSet rs = ps.executeQuery();
                Object result = null;
                if (rs.next()) {
                    result = rs.getObject(mysqlValue.getName());
                }
                future.complete(result);
            } catch (SQLException e) {
                e.printStackTrace();
                future.completeExceptionally(e);
            }
        });

        return future;
    }

    public CompletableFuture<List<Object>> getAllObjectAsync(UUID uuid, MysqlValue mysqlValue) {
        CompletableFuture<List<Object>> future = new CompletableFuture<>();

        Bukkit.getScheduler().runTaskAsynchronously(MainAPI.getInstance(), () -> {
            List<Object> values = new ArrayList<>();
            try {
                PreparedStatement ps = getMySQL().getConnection().prepareStatement("SELECT " + mysqlValue.getName() + " FROM " + tableString + " WHERE UUID=?");
                ps.setString(1, uuid.toString());
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    Object result = rs.getObject(mysqlValue.getName());
                    values.add(result);
                }
                future.complete(values);
            } catch (SQLException e) {
                e.printStackTrace();
                future.completeExceptionally(e);
            }
        });

        return future;
    }

    public CompletableFuture<Void> setObjectAsync(MysqlValue mysqlValue) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        Bukkit.getScheduler().runTaskAsynchronously(MainAPI.getInstance(), () -> {
            try {
                if (!exists(mysqlValue.getMysqlUUID())) {
                    PreparedStatement ps2 = getMySQL().getConnection().prepareStatement("INSERT IGNORE INTO " + tableString + " (" + mysqlValue.getName() + ",UUID) VALUES (?,?)");
                    ps2.setObject(1, mysqlValue.getObjectValue());
                    ps2.setString(2, mysqlValue.getMysqlUUID().toString());
                    ps2.executeUpdate();
                } else {
                    PreparedStatement ps = getMySQL().getConnection().prepareStatement("UPDATE " + tableString + " SET " + mysqlValue.getName() + "=? WHERE UUID=?");
                    ps.setObject(1, mysqlValue.getObjectValue());
                    ps.setString(2, mysqlValue.getMysqlUUID().toString());
                    ps.executeUpdate();
                }
                future.complete(null);
            } catch (SQLException e) {
                e.printStackTrace();
                future.completeExceptionally(e);
            }
        });

        return future;
    }

    public CompletableFuture<Void> removeObjectAsync(Object i, MysqlValue mysqlValue) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        Bukkit.getScheduler().runTaskAsynchronously(MainAPI.getInstance(), () -> {
            try {
                PreparedStatement ps = getMySQL().getConnection().prepareStatement("DELETE FROM " + tableString + " WHERE " + mysqlValue.getName() + " = ?");
                ps.setObject(1, i);
                ps.executeUpdate();
                ps.close();
                future.complete(null);
            } catch (SQLException e) {
                e.printStackTrace();
                future.completeExceptionally(e);
            }
        });

        return future;
    }

    public CompletableFuture<List<Object>> getAllObjectAsync(MysqlValue mysqlValue) {
        CompletableFuture<List<Object>> future = new CompletableFuture<>();

        Bukkit.getScheduler().runTaskAsynchronously(MainAPI.getInstance(), () -> {
            List<Object> values = new ArrayList<>();
            try {
                PreparedStatement ps = getMySQL().getConnection().prepareStatement("SELECT " + mysqlValue.getName() + " FROM " + tableString);
                ResultSet resultSet = ps.executeQuery();
                while (resultSet.next()) {
                    Object result = resultSet.getObject(mysqlValue.getName());
                    values.add(result);
                }
                future.complete(values);
            } catch (SQLException e) {
                e.printStackTrace();
                future.completeExceptionally(e);
            }
        });

        return future;
    }

    public MySQL getMySQL() {
        return mySQL;
    }

    public List<MysqlValue> getMysqlValues() {
        return mysqlValues;
    }
}
