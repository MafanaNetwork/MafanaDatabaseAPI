package me.tahacheji.mafana.data;
import me.tahacheji.mafana.MafanaDatabaseAPI;
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
    public List<DatabaseValue> databaseValues = new ArrayList<>();

    public SQLGetter(MySQL plugin) {
        this.mySQL = plugin;
    }

    public void createTable(String tableName, DatabaseValue... databaseValues) {
        tableString = tableName;
        PreparedStatement ps;
        try {
            String x3 = "CREATE TABLE IF NOT EXISTS " + tableName + " (id INT AUTO_INCREMENT PRIMARY KEY, ";
            for (DatabaseValue databaseValue : databaseValues) {
                if (databaseValues.length != 1) {
                    if (databaseValue.getStringValue() != null) {
                        if(databaseValue.getX() != null) {
                            x3 = x3 + databaseValue.getName() + " " + databaseValue.getX() + ",";
                        } else {
                            x3 = x3 + databaseValue.getName() + " VARCHAR(100),";
                        }
                        continue;
                    }
                    if (databaseValue.getIntValue() != null) {
                        if(databaseValue.getX() != null) {
                            x3 = x3 + databaseValue.getName() + " " + databaseValue.getX() + ",";
                        } else {
                            x3 = x3 + databaseValue.getName() + " INT(100),";
                        }
                        continue;
                    }
                    if (databaseValue.getDoubleValue() != null) {
                        if(databaseValue.getX() != null) {
                            x3 = x3 + databaseValue.getName() + " " + databaseValue.getX() + ",";
                        } else {
                            x3 = x3 + databaseValue.getName() + " DOUBLE(5,0),";
                        }
                        continue;
                    }
                    if (databaseValue.getUuidValue() != null) {
                        if(databaseValue.getX() != null) {
                            x3 = x3 + databaseValue.getName() + " " + databaseValue.getX() + ",";
                        } else {
                            x3 = x3 + databaseValue.getName() + " VARCHAR(100),";
                        }
                    }
                    getMysqlValues().add(databaseValue);
                } else {
                    if (databaseValue.getStringValue() != null) {
                        if(databaseValue.getX() != null) {
                            x3 = x3 + databaseValue.getName() + " " + databaseValue.getX() + ",";
                        } else {
                            x3 = x3 + databaseValue.getName() + " VARCHAR(100)";
                        }
                        continue;
                    }
                    if (databaseValue.getIntValue() != null) {
                        if(databaseValue.getX() != null) {
                            x3 = x3 + databaseValue.getName() + " " + databaseValue.getX() + ",";
                        } else {
                            x3 = x3 + databaseValue.getName() + " INT(100)";
                        }
                        continue;
                    }
                    if (databaseValue.getDoubleValue() != null) {
                        if(databaseValue.getX() != null) {
                            x3 = x3 + databaseValue.getName() + " " + databaseValue.getX() + ",";
                        } else {
                            x3 = x3 + databaseValue.getName() + " DOUBLE(5,0)";
                        }
                        continue;
                    }
                    if (databaseValue.getUuidValue() != null) {
                        if(databaseValue.getX() != null) {
                            x3 = x3 + databaseValue.getName() + " " + databaseValue.getX() + ",";
                        } else {
                            x3 = x3 + databaseValue.getName() + " VARCHAR(100)";
                        }
                    }
                    getMysqlValues().add(databaseValue);
                }
            }
            if (databaseValues.length != 1) {
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

        Bukkit.getScheduler().runTaskAsynchronously(MafanaDatabaseAPI.getInstance(), () -> {
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

    public void setInt(DatabaseValue databaseValue) {
        try {
            if(!exists(databaseValue.getMysqlUUID())) {
                PreparedStatement ps2 = getMySQL().getConnection().prepareStatement("INSERT IGNORE INTO " + tableString + " (" + databaseValue.getName() + ",UUID) VALUES (?,?)");
                ps2.setInt(1, databaseValue.getIntValue());
                ps2.setString(2, databaseValue.getMysqlUUID().toString());
                ps2.executeUpdate();
            } else {
                PreparedStatement ps = getMySQL().getConnection().prepareStatement("UPDATE " + tableString + " SET " + databaseValue.getName() + "=? " + "WHERE UUID=?");
                ps.setInt(1, databaseValue.getIntValue());
                ps.setString(2, databaseValue.getMysqlUUID().toString());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeInt(int i, DatabaseValue databaseValue) {
        try {
            PreparedStatement ps = getMySQL().getConnection().prepareStatement("DELETE FROM " + tableString  +" WHERE " + databaseValue.getName() + " = ?");
            ps.setInt(1, i);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle the exception appropriately, such as logging an error or notifying the player about the failure.
        }
    }
    public int getInt(UUID uuid, DatabaseValue databaseValue) {
        try {
            PreparedStatement ps = getMySQL().getConnection().prepareStatement("SELECT " + databaseValue.getName() + " FROM "  + tableString + " WHERE UUID=?");
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            int xp = 0;
            if(rs.next()) {
                xp = rs.getInt(databaseValue.getName());
                return xp;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<Integer> getAllIntager(UUID uuid, DatabaseValue databaseValue) throws SQLException {
        List<Integer> x = new ArrayList<>();
        PreparedStatement ps = getMySQL().getConnection().prepareStatement("SELECT " + databaseValue.getName() + " FROM " + tableString + " WHERE UUID=?");
        ps.setString(1, uuid.toString());
        ResultSet resultSet = ps.executeQuery();
        try {
            while (resultSet.next()) {
                Integer xs = resultSet.getInt(databaseValue.getName());
                x.add(xs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return x;
    }

    public List<Integer> getAllIntager(DatabaseValue databaseValue) throws SQLException {
        List<Integer> x = new ArrayList();
        PreparedStatement ps = this.getMySQL().getConnection().prepareStatement("SELECT " + databaseValue.getName() + " FROM " + this.tableString);
        ResultSet resultSet = ps.executeQuery();

        try {
            while(resultSet.next()) {
                Integer xs = resultSet.getInt(databaseValue.getName());
                x.add(xs);
            }
        } catch (SQLException var6) {
            var6.printStackTrace();
        }

        return x;
    }

    public CompletableFuture<Void> setIntAsync(DatabaseValue databaseValue) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        Bukkit.getScheduler().runTaskAsynchronously(MafanaDatabaseAPI.getInstance(), () -> {
            try {
                if (!exists(databaseValue.getMysqlUUID())) {
                    PreparedStatement ps2 = getMySQL().getConnection().prepareStatement("INSERT IGNORE INTO " + tableString + " (" + databaseValue.getName() + ",UUID) VALUES (?,?)");
                    ps2.setInt(1, databaseValue.getIntValue());
                    ps2.setString(2, databaseValue.getMysqlUUID().toString());
                    ps2.executeUpdate();
                } else {
                    PreparedStatement ps = getMySQL().getConnection().prepareStatement("UPDATE " + tableString + " SET " + databaseValue.getName() + "=? " + "WHERE UUID=?");
                    ps.setInt(1, databaseValue.getIntValue());
                    ps.setString(2, databaseValue.getMysqlUUID().toString());
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

    public CompletableFuture<Void> removeIntAsync(int i, DatabaseValue databaseValue) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        Bukkit.getScheduler().runTaskAsynchronously(MafanaDatabaseAPI.getInstance(), () -> {
            try {
                PreparedStatement ps = getMySQL().getConnection().prepareStatement("DELETE FROM " + tableString + " WHERE " + databaseValue.getName() + " = ?");
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

    public CompletableFuture<Integer> getIntAsync(UUID uuid, DatabaseValue databaseValue) {
        CompletableFuture<Integer> future = new CompletableFuture<>();

        Bukkit.getScheduler().runTaskAsynchronously(MafanaDatabaseAPI.getInstance(), () -> {
            try {
                PreparedStatement ps = getMySQL().getConnection().prepareStatement("SELECT " + databaseValue.getName() + " FROM " + tableString + " WHERE UUID=?");
                ps.setString(1, uuid.toString());
                ResultSet rs = ps.executeQuery();
                int xp = 0;
                if (rs.next()) {
                    xp = rs.getInt(databaseValue.getName());
                }
                future.complete(xp);
            } catch (SQLException e) {
                e.printStackTrace();
                future.completeExceptionally(e);
            }
        });

        return future;
    }

    public CompletableFuture<List<Integer>> getAllIntegerAsync(UUID uuid, DatabaseValue databaseValue) {
        CompletableFuture<List<Integer>> future = new CompletableFuture<>();

        Bukkit.getScheduler().runTaskAsynchronously(MafanaDatabaseAPI.getInstance(), () -> {
            List<Integer> x = new ArrayList<>();
            try {
                PreparedStatement ps = getMySQL().getConnection().prepareStatement("SELECT " + databaseValue.getName() + " FROM " + tableString + " WHERE UUID=?");
                ps.setString(1, uuid.toString());
                ResultSet resultSet = ps.executeQuery();
                while (resultSet.next()) {
                    Integer xs = resultSet.getInt(databaseValue.getName());
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

    public CompletableFuture<List<Integer>> getAllIntegerAsync(DatabaseValue databaseValue) {
        CompletableFuture<List<Integer>> future = new CompletableFuture<>();

        Bukkit.getScheduler().runTaskAsynchronously(MafanaDatabaseAPI.getInstance(), () -> {
            List<Integer> x = new ArrayList<>();
            try {
                PreparedStatement ps = this.getMySQL().getConnection().prepareStatement("SELECT " + databaseValue.getName() + " FROM " + this.tableString);
                ResultSet resultSet = ps.executeQuery();
                while (resultSet.next()) {
                    Integer xs = resultSet.getInt(databaseValue.getName());
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


    public void setString(DatabaseValue databaseValue) {
        try {
            if(!exists(databaseValue.getMysqlUUID())) {
                PreparedStatement ps2 = getMySQL().getConnection().prepareStatement("INSERT IGNORE INTO " + tableString + " (" + databaseValue.getName() + ",UUID) VALUES (?,?)");
                ps2.setString(1, databaseValue.getStringValue());
                ps2.setString(2, databaseValue.getMysqlUUID().toString());
                ps2.executeUpdate();
            } else {
                PreparedStatement ps = getMySQL().getConnection().prepareStatement("UPDATE " + tableString + " SET " + databaseValue.getName() + "=? " + "WHERE UUID=?");
                ps.setString(1, databaseValue.getStringValue());
                ps.setString(2, databaseValue.getMysqlUUID().toString());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeString(String i, DatabaseValue databaseValue) {
        try {
            PreparedStatement ps = getMySQL().getConnection().prepareStatement("DELETE FROM " + tableString  +" WHERE " + databaseValue.getName() + " = ?");
            ps.setString(1, i);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public String getString(UUID uuid, DatabaseValue databaseValue) {
        try {
            PreparedStatement ps = getMySQL().getConnection().prepareStatement("SELECT " + databaseValue.getName() + " FROM "  + tableString + " WHERE UUID=?");
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            String xp = "";
            if(rs.next()) {
                xp = rs.getString(databaseValue.getName());
                return xp;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }

    public List<String> getAllString(DatabaseValue databaseValue) throws SQLException {
        List<String> x = new ArrayList<>();
        PreparedStatement ps = getMySQL().getConnection().prepareStatement("SELECT " + databaseValue.getName() + " FROM " + tableString);
        ResultSet resultSet = ps.executeQuery();
        try {
            while (resultSet.next()) {
                String xs = resultSet.getString(databaseValue.getName());
                x.add(xs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return x;
    }

    public List<String> getAllString(UUID uuid, DatabaseValue databaseValue) {
        List<String> values = new ArrayList<>();
        try {
            PreparedStatement ps = this.getMySQL().getConnection().prepareStatement("SELECT " + databaseValue.getName() + " FROM " + this.tableString + " WHERE UUID=?");
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String xp = rs.getString(databaseValue.getName());
                values.add(xp);
            }
        } catch (SQLException var6) {
            var6.printStackTrace();
        }

        return values;
    }

    public CompletableFuture<Void> setStringAsync(DatabaseValue databaseValue) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        Bukkit.getScheduler().runTaskAsynchronously(MafanaDatabaseAPI.getInstance(), () -> {
            try {
                if (!exists(databaseValue.getMysqlUUID())) {
                    PreparedStatement ps2 = getMySQL().getConnection().prepareStatement("INSERT IGNORE INTO " + tableString + " (" + databaseValue.getName() + ",UUID) VALUES (?,?)");
                    ps2.setString(1, databaseValue.getStringValue());
                    ps2.setString(2, databaseValue.getMysqlUUID().toString());
                    ps2.executeUpdate();
                } else {
                    PreparedStatement ps = getMySQL().getConnection().prepareStatement("UPDATE " + tableString + " SET " + databaseValue.getName() + "=? " + "WHERE UUID=?");
                    ps.setString(1, databaseValue.getStringValue());
                    ps.setString(2, databaseValue.getMysqlUUID().toString());
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

    public CompletableFuture<Void> removeStringAsync(String i, DatabaseValue databaseValue) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        Bukkit.getScheduler().runTaskAsynchronously(MafanaDatabaseAPI.getInstance(), () -> {
            try {
                PreparedStatement ps = getMySQL().getConnection().prepareStatement("DELETE FROM " + tableString + " WHERE " + databaseValue.getName() + " = ?");
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

    public CompletableFuture<String> getStringAsync(UUID uuid, DatabaseValue databaseValue) {
        CompletableFuture<String> future = new CompletableFuture<>();

        Bukkit.getScheduler().runTaskAsynchronously(MafanaDatabaseAPI.getInstance(), () -> {
            try {
                PreparedStatement ps = getMySQL().getConnection().prepareStatement("SELECT " + databaseValue.getName() + " FROM " + tableString + " WHERE UUID=?");
                ps.setString(1, uuid.toString());
                ResultSet rs = ps.executeQuery();
                String xp = "";
                if (rs.next()) {
                    xp = rs.getString(databaseValue.getName());
                }
                future.complete(xp);
            } catch (SQLException e) {
                e.printStackTrace();
                future.completeExceptionally(e);
            }
        });

        return future;
    }

    public CompletableFuture<List<String>> getAllStringAsync(DatabaseValue databaseValue) {
        CompletableFuture<List<String>> future = new CompletableFuture<>();

        Bukkit.getScheduler().runTaskAsynchronously(MafanaDatabaseAPI.getInstance(), () -> {
            List<String> values = new ArrayList<>();
            try {
                PreparedStatement ps = getMySQL().getConnection().prepareStatement("SELECT " + databaseValue.getName() + " FROM " + tableString);
                ResultSet resultSet = ps.executeQuery();
                while (resultSet.next()) {
                    String xs = resultSet.getString(databaseValue.getName());
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

    public CompletableFuture<List<String>> getAllStringAsync(UUID uuid, DatabaseValue databaseValue) {
        CompletableFuture<List<String>> future = new CompletableFuture<>();

        Bukkit.getScheduler().runTaskAsynchronously(MafanaDatabaseAPI.getInstance(), () -> {
            List<String> values = new ArrayList<>();
            try {
                PreparedStatement ps = getMySQL().getConnection().prepareStatement("SELECT " + databaseValue.getName() + " FROM " + tableString + " WHERE UUID=?");
                ps.setString(1, uuid.toString());
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    String xp = rs.getString(databaseValue.getName());
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



    public void setDouble(DatabaseValue databaseValue) {
        try {
            if(!exists(databaseValue.getMysqlUUID())) {
                PreparedStatement ps2 = getMySQL().getConnection().prepareStatement("INSERT IGNORE INTO " + tableString + " (" + databaseValue.getName() + ",UUID) VALUES (?,?)");
                ps2.setDouble(1, databaseValue.getDoubleValue());
                ps2.setString(2, databaseValue.getMysqlUUID().toString());
                ps2.executeUpdate();
            } else {
                PreparedStatement ps = getMySQL().getConnection().prepareStatement("UPDATE " + tableString + " SET " + databaseValue.getName() + "=? " + "WHERE UUID=?");
                ps.setDouble(1, databaseValue.getDoubleValue());
                ps.setString(2, databaseValue.getMysqlUUID().toString());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeDouble(Double i, DatabaseValue databaseValue) {
        try {
            PreparedStatement ps = getMySQL().getConnection().prepareStatement("DELETE FROM " + tableString  +" WHERE " + databaseValue.getName() + " = ?");
            ps.setDouble(1, i);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public Double getDouble(UUID uuid, DatabaseValue databaseValue) {
        double xp = 0;
        try {
            PreparedStatement ps = getMySQL().getConnection().prepareStatement("SELECT " + databaseValue.getName() + " FROM " + tableString + " WHERE UUID=?");
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            xp = 0.0;
            if (rs.next()) {
                xp = rs.getDouble(databaseValue.getName());
                return xp;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return xp;
    }

    public List<Double> getAllDouble(DatabaseValue databaseValue) throws SQLException {
        List<Double> x = new ArrayList<>();
        PreparedStatement ps = getMySQL().getConnection().prepareStatement("SELECT " + databaseValue.getName() + " FROM " + tableString);
        ResultSet resultSet = ps.executeQuery();
        try {
            while (resultSet.next()) {
                Double xs = resultSet.getDouble(databaseValue.getName());
                x.add(xs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return x;
    }

    public List<Double> getAllDouble(UUID uuid, DatabaseValue databaseValue) {
        List<Double> values = new ArrayList<>();
        try {
            PreparedStatement ps = this.getMySQL().getConnection().prepareStatement("SELECT " + databaseValue.getName() + " FROM " + this.tableString + " WHERE UUID=?");
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Double xp = rs.getDouble(databaseValue.getName());
                values.add(xp);
            }
        } catch (SQLException var6) {
            var6.printStackTrace();
        }

        return values;
    }

    public CompletableFuture<Void> setDoubleAsync(DatabaseValue databaseValue) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        Bukkit.getScheduler().runTaskAsynchronously(MafanaDatabaseAPI.getInstance(), () -> {
            try {
                if (!exists(databaseValue.getMysqlUUID())) {
                    PreparedStatement ps2 = getMySQL().getConnection().prepareStatement("INSERT IGNORE INTO " + tableString + " (" + databaseValue.getName() + ",UUID) VALUES (?,?)");
                    ps2.setDouble(1, databaseValue.getDoubleValue());
                    ps2.setString(2, databaseValue.getMysqlUUID().toString());
                    ps2.executeUpdate();
                } else {
                    PreparedStatement ps = getMySQL().getConnection().prepareStatement("UPDATE " + tableString + " SET " + databaseValue.getName() + "=? " + "WHERE UUID=?");
                    ps.setDouble(1, databaseValue.getDoubleValue());
                    ps.setString(2, databaseValue.getMysqlUUID().toString());
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

    public CompletableFuture<Void> removeDoubleAsync(Double i, DatabaseValue databaseValue) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        Bukkit.getScheduler().runTaskAsynchronously(MafanaDatabaseAPI.getInstance(), () -> {
            try {
                PreparedStatement ps = getMySQL().getConnection().prepareStatement("DELETE FROM " + tableString + " WHERE " + databaseValue.getName() + " = ?");
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

    public CompletableFuture<Double> getDoubleAsync(UUID uuid, DatabaseValue databaseValue) {
        CompletableFuture<Double> future = new CompletableFuture<>();

        Bukkit.getScheduler().runTaskAsynchronously(MafanaDatabaseAPI.getInstance(), () -> {
            try {
                PreparedStatement ps = getMySQL().getConnection().prepareStatement("SELECT " + databaseValue.getName() + " FROM " + tableString + " WHERE UUID=?");
                ps.setString(1, uuid.toString());
                ResultSet rs = ps.executeQuery();
                double xp = 0.0;
                if (rs.next()) {
                    xp = rs.getDouble(databaseValue.getName());
                }
                future.complete(xp);
            } catch (SQLException e) {
                e.printStackTrace();
                future.completeExceptionally(e);
            }
        });

        return future;
    }

    public CompletableFuture<List<Double>> getAllDoubleAsync(DatabaseValue databaseValue) {
        CompletableFuture<List<Double>> future = new CompletableFuture<>();

        Bukkit.getScheduler().runTaskAsynchronously(MafanaDatabaseAPI.getInstance(), () -> {
            List<Double> values = new ArrayList<>();
            try {
                PreparedStatement ps = getMySQL().getConnection().prepareStatement("SELECT " + databaseValue.getName() + " FROM " + tableString);
                ResultSet resultSet = ps.executeQuery();
                while (resultSet.next()) {
                    Double xs = resultSet.getDouble(databaseValue.getName());
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


    public void setUUID(DatabaseValue databaseValue) {
        try {
            if(!exists(databaseValue.getMysqlUUID())) {
                PreparedStatement ps2 = getMySQL().getConnection().prepareStatement("INSERT IGNORE INTO " + tableString + " (" + databaseValue.getName() + ",UUID) VALUES (?,?)");
                ps2.setString(1, databaseValue.getUuidValue().toString());
                ps2.setString(2, databaseValue.getMysqlUUID().toString());
                ps2.executeUpdate();
            } else {
                PreparedStatement ps = getMySQL().getConnection().prepareStatement("UPDATE " + tableString + " SET " + databaseValue.getName() + "=? " + "WHERE UUID=?");
                ps.setString(1, databaseValue.getUuidValue().toString());
                ps.setString(2, databaseValue.getMysqlUUID().toString());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void removeUUID(UUID i, DatabaseValue databaseValue) {
        try {
            PreparedStatement ps = getMySQL().getConnection().prepareStatement("DELETE FROM " + tableString  +" WHERE " + databaseValue.getName() + " = ?");
            ps.setString(1, i.toString());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public UUID getUUID(UUID uuid, DatabaseValue databaseValue) {
        UUID xp = null;
        try {
            PreparedStatement ps = getMySQL().getConnection().prepareStatement("SELECT " + databaseValue.getName() + " FROM " + tableString + " WHERE UUID=?");
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                xp = UUID.fromString(rs.getString(databaseValue.getName()));
                return xp;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return xp;
    }

    public List<UUID> getAllUUID(DatabaseValue databaseValue) throws SQLException {
        List<UUID> players = new ArrayList<>();
        PreparedStatement ps = getMySQL().getConnection().prepareStatement("SELECT " + databaseValue.getName() + " FROM " + tableString);
        ResultSet resultSet = ps.executeQuery();
        try {
            while (resultSet.next()) {
                UUID playerUUID = UUID.fromString(resultSet.getString(databaseValue.getName()));
                players.add(playerUUID);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return players;
    }

    public List<UUID> getAllUUID(UUID uuid, DatabaseValue databaseValue) {
        List<UUID> values = new ArrayList<>();
        try {
            PreparedStatement ps = this.getMySQL().getConnection().prepareStatement("SELECT " + databaseValue.getName() + " FROM " + this.tableString + " WHERE UUID=?");
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                UUID xp = UUID.fromString(rs.getString(databaseValue.getName()));
                values.add(xp);
            }
        } catch (SQLException var6) {
            var6.printStackTrace();
        }

        return values;
    }

    public CompletableFuture<Void> setUUIDAsync(DatabaseValue databaseValue) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        Bukkit.getScheduler().runTaskAsynchronously(MafanaDatabaseAPI.getInstance(), () -> {
            try {
                if (!exists(databaseValue.getMysqlUUID())) {
                    PreparedStatement ps2 = getMySQL().getConnection().prepareStatement("INSERT IGNORE INTO " + tableString + " (" + databaseValue.getName() + ",UUID) VALUES (?,?)");
                    ps2.setString(1, databaseValue.getUuidValue().toString());
                    ps2.setString(2, databaseValue.getMysqlUUID().toString());
                    ps2.executeUpdate();
                } else {
                    PreparedStatement ps = getMySQL().getConnection().prepareStatement("UPDATE " + tableString + " SET " + databaseValue.getName() + "=? WHERE UUID=?");
                    ps.setString(1, databaseValue.getUuidValue().toString());
                    ps.setString(2, databaseValue.getMysqlUUID().toString());
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

    public CompletableFuture<Void> removeUUIDAsync(UUID i, DatabaseValue databaseValue) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        Bukkit.getScheduler().runTaskAsynchronously(MafanaDatabaseAPI.getInstance(), () -> {
            try {
                PreparedStatement ps = getMySQL().getConnection().prepareStatement("DELETE FROM " + tableString + " WHERE " + databaseValue.getName() + " = ?");
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

    public CompletableFuture<UUID> getUUIDAsync(UUID uuid, DatabaseValue databaseValue) {
        CompletableFuture<UUID> future = new CompletableFuture<>();

        Bukkit.getScheduler().runTaskAsynchronously(MafanaDatabaseAPI.getInstance(), () -> {
            UUID xp = null;
            try {
                PreparedStatement ps = getMySQL().getConnection().prepareStatement("SELECT " + databaseValue.getName() + " FROM " + tableString + " WHERE UUID=?");
                ps.setString(1, uuid.toString());
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    xp = UUID.fromString(rs.getString(databaseValue.getName()));
                }
                future.complete(xp);
            } catch (SQLException e) {
                e.printStackTrace();
                future.completeExceptionally(e);
            }
        });

        return future;
    }

    public CompletableFuture<List<UUID>> getAllUUIDAsync(DatabaseValue databaseValue) {
        CompletableFuture<List<UUID>> future = new CompletableFuture<>();

        Bukkit.getScheduler().runTaskAsynchronously(MafanaDatabaseAPI.getInstance(), () -> {
            List<UUID> players = new ArrayList<>();
            try {
                PreparedStatement ps = getMySQL().getConnection().prepareStatement("SELECT " + databaseValue.getName() + " FROM " + tableString);
                ResultSet resultSet = ps.executeQuery();
                while (resultSet.next()) {
                    UUID playerUUID = UUID.fromString(resultSet.getString(databaseValue.getName()));
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


    public void setObject(DatabaseValue databaseValue) {
        try {
            if(!exists(databaseValue.getMysqlUUID())) {
                PreparedStatement ps2 = getMySQL().getConnection().prepareStatement("INSERT IGNORE INTO " + tableString + " (" + databaseValue.getName() + ",UUID) VALUES (?,?)");
                ps2.setString(1, databaseValue.getUuidValue().toString());
                ps2.setObject(2, databaseValue.getObjectValue());
                ps2.executeUpdate();
            } else {
                PreparedStatement ps = getMySQL().getConnection().prepareStatement("UPDATE " + tableString + " SET " + databaseValue.getName() + "=? " + "WHERE UUID=?");
                ps.setString(1, databaseValue.getUuidValue().toString());
                ps.setObject(2, databaseValue.getObjectValue());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void removeObject(Object i, DatabaseValue databaseValue) {
        try {
            PreparedStatement ps = getMySQL().getConnection().prepareStatement("DELETE FROM " + tableString  +" WHERE " + databaseValue.getName() + " = ?");
            ps.setObject(1, i);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle the exception appropriately, such as logging an error or notifying the player about the failure.
        }
    }
    public Object getObject(UUID uuid, DatabaseValue databaseValue) {
        Object xp;
        try {
            PreparedStatement ps = getMySQL().getConnection().prepareStatement("SELECT " + databaseValue.getName() + " FROM " + tableString + " WHERE UUID=?");
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                xp = rs.getObject(databaseValue.getName());
                return xp;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }



    public List<Object> getAllObject(DatabaseValue databaseValue) throws SQLException {
        List<Object> x = new ArrayList<>();
        PreparedStatement ps = getMySQL().getConnection().prepareStatement("SELECT " + databaseValue.getName() + " FROM " + tableString);
        ResultSet resultSet = ps.executeQuery();
        try {
            while (resultSet.next()) {
                Object playerUUID = resultSet.getObject(databaseValue.getName());
                x.add(playerUUID);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return x;
    }

    public List<Object> getAllObject(UUID uuid, DatabaseValue databaseValue) {
        List<Object> values = new ArrayList<>();
        try {
            PreparedStatement ps = this.getMySQL().getConnection().prepareStatement("SELECT " + databaseValue.getName() + " FROM " + this.tableString + " WHERE UUID=?");
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Object xp = rs.getObject(databaseValue.getName());
                values.add(xp);
            }
        } catch (SQLException var6) {
            var6.printStackTrace();
        }

        return values;
    }

    public CompletableFuture<Object> getObjectAsync(UUID uuid, DatabaseValue databaseValue) {
        CompletableFuture<Object> future = new CompletableFuture<>();

        Bukkit.getScheduler().runTaskAsynchronously(MafanaDatabaseAPI.getInstance(), () -> {
            try {
                PreparedStatement ps = getMySQL().getConnection().prepareStatement("SELECT " + databaseValue.getName() + " FROM " + tableString + " WHERE UUID=?");
                ps.setString(1, uuid.toString());
                ResultSet rs = ps.executeQuery();
                Object result = null;
                if (rs.next()) {
                    result = rs.getObject(databaseValue.getName());
                }
                future.complete(result);
            } catch (SQLException e) {
                e.printStackTrace();
                future.completeExceptionally(e);
            }
        });

        return future;
    }

    public CompletableFuture<List<Object>> getAllObjectAsync(UUID uuid, DatabaseValue databaseValue) {
        CompletableFuture<List<Object>> future = new CompletableFuture<>();

        Bukkit.getScheduler().runTaskAsynchronously(MafanaDatabaseAPI.getInstance(), () -> {
            List<Object> values = new ArrayList<>();
            try {
                PreparedStatement ps = getMySQL().getConnection().prepareStatement("SELECT " + databaseValue.getName() + " FROM " + tableString + " WHERE UUID=?");
                ps.setString(1, uuid.toString());
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    Object result = rs.getObject(databaseValue.getName());
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

    public CompletableFuture<Void> setObjectAsync(DatabaseValue databaseValue) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        Bukkit.getScheduler().runTaskAsynchronously(MafanaDatabaseAPI.getInstance(), () -> {
            try {
                if (!exists(databaseValue.getMysqlUUID())) {
                    PreparedStatement ps2 = getMySQL().getConnection().prepareStatement("INSERT IGNORE INTO " + tableString + " (" + databaseValue.getName() + ",UUID) VALUES (?,?)");
                    ps2.setObject(1, databaseValue.getObjectValue());
                    ps2.setString(2, databaseValue.getMysqlUUID().toString());
                    ps2.executeUpdate();
                } else {
                    PreparedStatement ps = getMySQL().getConnection().prepareStatement("UPDATE " + tableString + " SET " + databaseValue.getName() + "=? WHERE UUID=?");
                    ps.setObject(1, databaseValue.getObjectValue());
                    ps.setString(2, databaseValue.getMysqlUUID().toString());
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

    public CompletableFuture<Void> removeObjectAsync(Object i, DatabaseValue databaseValue) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        Bukkit.getScheduler().runTaskAsynchronously(MafanaDatabaseAPI.getInstance(), () -> {
            try {
                PreparedStatement ps = getMySQL().getConnection().prepareStatement("DELETE FROM " + tableString + " WHERE " + databaseValue.getName() + " = ?");
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

    public CompletableFuture<List<Object>> getAllObjectAsync(DatabaseValue databaseValue) {
        CompletableFuture<List<Object>> future = new CompletableFuture<>();

        Bukkit.getScheduler().runTaskAsynchronously(MafanaDatabaseAPI.getInstance(), () -> {
            List<Object> values = new ArrayList<>();
            try {
                PreparedStatement ps = getMySQL().getConnection().prepareStatement("SELECT " + databaseValue.getName() + " FROM " + tableString);
                ResultSet resultSet = ps.executeQuery();
                while (resultSet.next()) {
                    Object result = resultSet.getObject(databaseValue.getName());
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

    public List<DatabaseValue> getMysqlValues() {
        return databaseValues;
    }
}
