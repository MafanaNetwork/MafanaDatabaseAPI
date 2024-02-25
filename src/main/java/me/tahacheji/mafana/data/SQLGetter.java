package me.tahacheji.mafana.data;

import me.tahacheji.mafana.MafanaDatabaseAPI;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

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
            StringBuilder x3 = new StringBuilder("CREATE TABLE IF NOT EXISTS " + tableName + " (id INT AUTO_INCREMENT PRIMARY KEY, ");
            for (DatabaseValue databaseValue : databaseValues) {
                if (databaseValues.length != 1) {
                    if (databaseValue.getStringValue() != null) {
                        if (databaseValue.getX() != null) {
                            x3.append(databaseValue.getName()).append(" ").append(databaseValue.getX()).append(",");
                        } else {
                            x3.append(databaseValue.getName()).append(" VARCHAR(100),");
                        }
                        continue;
                    }
                    if (databaseValue.getIntValue() != null) {
                        if (databaseValue.getX() != null) {
                            x3.append(databaseValue.getName()).append(" ").append(databaseValue.getX()).append(",");
                        } else {
                            x3.append(databaseValue.getName()).append(" INT(100),");
                        }
                        continue;
                    }
                    if (databaseValue.getDoubleValue() != null) {
                        if (databaseValue.getX() != null) {
                            x3.append(databaseValue.getName()).append(" ").append(databaseValue.getX()).append(",");
                        } else {
                            x3.append(databaseValue.getName()).append(" DOUBLE(5,0),");
                        }
                        continue;
                    }
                    if (databaseValue.getUuidValue() != null) {
                        if (databaseValue.getX() != null) {
                            x3.append(databaseValue.getName()).append(" ").append(databaseValue.getX()).append(",");
                        } else {
                            x3.append(databaseValue.getName()).append(" VARCHAR(100),");
                        }
                    }
                    getMysqlValues().add(databaseValue);
                } else {
                    if (databaseValue.getStringValue() != null) {
                        if (databaseValue.getX() != null) {
                            x3.append(databaseValue.getName()).append(" ").append(databaseValue.getX()).append(",");
                        } else {
                            x3.append(databaseValue.getName()).append(" VARCHAR(100)");
                        }
                        continue;
                    }
                    if (databaseValue.getIntValue() != null) {
                        if (databaseValue.getX() != null) {
                            x3.append(databaseValue.getName()).append(" ").append(databaseValue.getX()).append(",");
                        } else {
                            x3.append(databaseValue.getName()).append(" INT(100)");
                        }
                        continue;
                    }
                    if (databaseValue.getDoubleValue() != null) {
                        if (databaseValue.getX() != null) {
                            x3.append(databaseValue.getName()).append(" ").append(databaseValue.getX()).append(",");
                        } else {
                            x3.append(databaseValue.getName()).append(" DOUBLE(5,0)");
                        }
                        continue;
                    }
                    if (databaseValue.getUuidValue() != null) {
                        if (databaseValue.getX() != null) {
                            x3.append(databaseValue.getName()).append(" ").append(databaseValue.getX()).append(",");
                        } else {
                            x3.append(databaseValue.getName()).append(" VARCHAR(100)");
                        }
                    }
                    getMysqlValues().add(databaseValue);
                }
            }
            if (databaseValues.length != 1) {
                x3.append("UUID VARCHAR(100)");
            }
            x3.append(")");
            ps = mySQL.getConnection().prepareStatement(x3.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean existsSync(UUID uuid) {
        try (Connection connection = getMySQL().getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT 1 FROM " + tableString + " WHERE UUID=?")) {
            ps.setString(1, uuid.toString());
            try (ResultSet resultSet = ps.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    public CompletableFuture<Boolean> existsAsync(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = getMySQL().getConnection();
                 PreparedStatement ps = connection.prepareStatement("SELECT COUNT(*) FROM " + tableString + " WHERE UUID=?")) {
                ps.setString(1, uuid.toString());
                try (ResultSet resultSet = ps.executeQuery()) {
                    if (resultSet.next()) {
                        int count = resultSet.getInt(1);
                        return count > 0;
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        });
    }


    public List<Integer> getAllIntSync(UUID uuid, DatabaseValue databaseValue) throws SQLException {
        List<Integer> x = new ArrayList<>();
        try (Connection connection = getMySQL().getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT " + databaseValue.getName() + " FROM " + tableString + " WHERE UUID=?")) {
            ps.setString(1, uuid.toString());
            try (ResultSet resultSet = ps.executeQuery()) {
                while (resultSet.next()) {
                    Integer xs = resultSet.getInt(databaseValue.getName());
                    x.add(xs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e; // Re-throw the exception to be handled by the caller
        }
        return x;
    }


    public int getIntSync(UUID uuid, DatabaseValue databaseValue) {
        try (Connection connection = getMySQL().getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT " + databaseValue.getName() + " FROM " + tableString + " WHERE UUID=?")) {
            ps.setString(1, uuid.toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(databaseValue.getName());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }


    public List<Integer> getAllIntSync(DatabaseValue databaseValue) {
        List<Integer> x = new ArrayList<>();
        try (Connection connection = this.getMySQL().getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT " + databaseValue.getName() + " FROM " + this.tableString);
             ResultSet resultSet = ps.executeQuery()) {
            while (resultSet.next()) {
                Integer xs = resultSet.getInt(databaseValue.getName());
                x.add(xs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return x;
    }


    public CompletableFuture<Void> setIntAsync(DatabaseValue databaseValue) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        Logger logger = LoggerFactory.getLogger(getClass());

        CompletableFuture<Boolean> existsFuture = existsAsync(databaseValue.getMysqlUUID());

        existsFuture.thenComposeAsync(exists -> {
            try (Connection connection = getMySQL().getConnection()) {
                if (!exists) {
                    try (PreparedStatement ps2 = connection.prepareStatement("INSERT IGNORE INTO " + tableString + " (" + databaseValue.getName() + ",UUID) VALUES (?,?)")) {
                        ps2.setInt(1, databaseValue.getIntValue());
                        ps2.setString(2, databaseValue.getMysqlUUID().toString());
                        ps2.executeUpdate();
                    }
                } else {
                    try (PreparedStatement ps = connection.prepareStatement("UPDATE " + tableString + " SET " + databaseValue.getName() + "=? WHERE UUID=?")) {
                        ps.setInt(1, databaseValue.getIntValue());
                        ps.setString(2, databaseValue.getMysqlUUID().toString());
                        ps.executeUpdate();
                    }
                }
                return CompletableFuture.completedFuture(null);
            } catch (SQLException e) {
                logger.error("Error occurred while executing SQL query for setIntAsync", e);
                return CompletableFuture.failedFuture(e);
            }
        }).exceptionally(ex -> {
            logger.error("Error occurred during setIntAsync operation", ex);
            future.completeExceptionally(ex);
            return null;
        }).thenAccept(result -> future.complete(null));

        return future;
    }


    public CompletableFuture<Void> removeIntAsync(int i, DatabaseValue databaseValue) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = getMySQL().getConnection();
                 PreparedStatement ps = connection.prepareStatement("DELETE FROM " + tableString + " WHERE " + databaseValue.getName() + " = ?")) {
                ps.setInt(1, i);
                ps.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public CompletableFuture<Integer> getIntAsync(UUID uuid, DatabaseValue databaseValue) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = getMySQL().getConnection();
                 PreparedStatement ps = connection.prepareStatement("SELECT " + databaseValue.getName() + " FROM " + tableString + " WHERE UUID=?")) {
                ps.setString(1, uuid.toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt(databaseValue.getName());
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return 0;
        });
    }

    public CompletableFuture<List<Integer>> getAllIntegerAsync(UUID uuid, DatabaseValue databaseValue) {
        return CompletableFuture.supplyAsync(() -> {
            List<Integer> x = new ArrayList<>();
            try (Connection connection = getMySQL().getConnection();
                 PreparedStatement ps = connection.prepareStatement("SELECT " + databaseValue.getName() + " FROM " + tableString + " WHERE UUID=?")) {
                ps.setString(1, uuid.toString());
                try (ResultSet resultSet = ps.executeQuery()) {
                    while (resultSet.next()) {
                        Integer xs = resultSet.getInt(databaseValue.getName());
                        x.add(xs);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return x;
        });
    }


    public CompletableFuture<List<Integer>> getAllIntegerAsync(DatabaseValue databaseValue) {
        return CompletableFuture.supplyAsync(() -> {
            List<Integer> x = new ArrayList<>();
            try (Connection connection = getMySQL().getConnection();
                 PreparedStatement ps = connection.prepareStatement("SELECT " + databaseValue.getName() + " FROM " + this.tableString)) {
                try (ResultSet resultSet = ps.executeQuery()) {
                    while (resultSet.next()) {
                        Integer xs = resultSet.getInt(databaseValue.getName());
                        x.add(xs);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return x;
        });
    }


    public void setStringSync(DatabaseValue databaseValue) {
        try {
            if (!existsSync(databaseValue.getMysqlUUID())) {
                try (Connection connection = getMySQL().getConnection();
                     PreparedStatement ps2 = connection.prepareStatement("INSERT IGNORE INTO " + tableString + " (" + databaseValue.getName() + ", UUID) VALUES (?, ?)")) {
                    ps2.setString(1, databaseValue.getStringValue());
                    ps2.setString(2, databaseValue.getMysqlUUID().toString());
                    ps2.executeUpdate();
                }
            } else {
                try (Connection connection = getMySQL().getConnection();
                     PreparedStatement ps = connection.prepareStatement("UPDATE " + tableString + " SET " + databaseValue.getName() + " = ? WHERE UUID = ?")) {
                    ps.setString(1, databaseValue.getStringValue());
                    ps.setString(2, databaseValue.getMysqlUUID().toString());
                    ps.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getStringSync(UUID uuid, DatabaseValue databaseValue) {
        try (Connection connection = getMySQL().getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT " + databaseValue.getName() + " FROM " + tableString + " WHERE UUID = ?")) {
            ps.setString(1, uuid.toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString(databaseValue.getName());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }


    public List<String> getAllStringSync(DatabaseValue databaseValue) {
        List<String> x = new ArrayList<>();
        try (Connection connection = getMySQL().getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT " + databaseValue.getName() + " FROM " + tableString)) {
            try (ResultSet resultSet = ps.executeQuery()) {
                while (resultSet.next()) {
                    String xs = resultSet.getString(databaseValue.getName());
                    x.add(xs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return x;
    }


    public List<String> getAllStringSync(UUID uuid, DatabaseValue databaseValue) {
        List<String> values = new ArrayList<>();
        try (Connection connection = getMySQL().getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT " + databaseValue.getName() + " FROM " + this.tableString + " WHERE UUID=?")) {
            ps.setString(1, uuid.toString());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String xp = rs.getString(databaseValue.getName());
                    values.add(xp);
                }
            }
        } catch (SQLException var6) {
            var6.printStackTrace();
        }

        return values;
    }


    public CompletableFuture<Void> setStringAsync(DatabaseValue databaseValue) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        Logger logger = LoggerFactory.getLogger(getClass());

        CompletableFuture<Boolean> existsFuture = existsAsync(databaseValue.getMysqlUUID());

        existsFuture.thenComposeAsync(exists -> {
            try (Connection connection = getMySQL().getConnection()) {
                if (!exists) {
                    try (PreparedStatement ps2 = connection.prepareStatement("INSERT IGNORE INTO " + tableString + " (" + databaseValue.getName() + ",UUID) VALUES (?,?)")) {
                        ps2.setString(1, databaseValue.getStringValue());
                        ps2.setString(2, databaseValue.getMysqlUUID().toString());
                        ps2.executeUpdate();
                    }
                } else {
                    try (PreparedStatement ps = connection.prepareStatement("UPDATE " + tableString + " SET " + databaseValue.getName() + "=? WHERE UUID=?")) {
                        ps.setString(1, databaseValue.getStringValue());
                        ps.setString(2, databaseValue.getMysqlUUID().toString());
                        ps.executeUpdate();
                    }
                }
                return CompletableFuture.completedFuture(null);
            } catch (SQLException e) {
                logger.error("Error occurred while executing SQL query for setStringAsync", e);
                return CompletableFuture.failedFuture(e);
            }
        }).exceptionally(ex -> {
            logger.error("Error occurred during setStringAsync operation", ex);
            future.completeExceptionally(ex);
            return null;
        }).thenAccept(result -> future.complete(null));

        return future;
    }

    public CompletableFuture<Void> removeStringAsync(String i, DatabaseValue databaseValue) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = getMySQL().getConnection();
                 PreparedStatement ps = connection.prepareStatement("DELETE FROM " + tableString + " WHERE " + databaseValue.getName() + " = ?")) {
                ps.setString(1, i);
                ps.executeUpdate();
                return null;
            } catch (SQLException e) {
                throw new CompletionException(e);
            }
        });
    }


    public CompletableFuture<String> getStringAsync(UUID uuid, DatabaseValue databaseValue) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = getMySQL().getConnection();
                 PreparedStatement ps = connection.prepareStatement("SELECT " + databaseValue.getName() + " FROM " + tableString + " WHERE UUID=?")) {
                ps.setString(1, uuid.toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getString(databaseValue.getName());
                    } else {
                        return "";
                    }
                }
            } catch (SQLException e) {
                throw new CompletionException(e);
            }
        });
    }


    public CompletableFuture<List<String>> getAllStringAsync(DatabaseValue databaseValue) {
        return CompletableFuture.supplyAsync(() -> {
            List<String> values = new ArrayList<>();
            try (Connection connection = getMySQL().getConnection();
                 PreparedStatement ps = connection.prepareStatement("SELECT " + databaseValue.getName() + " FROM " + tableString)) {
                try (ResultSet resultSet = ps.executeQuery()) {
                    while (resultSet.next()) {
                        String xs = resultSet.getString(databaseValue.getName());
                        values.add(xs);
                    }
                    return values;
                }
            } catch (SQLException e) {
                throw new CompletionException(e);
            }
        });
    }

    public CompletableFuture<List<String>> getAllStringAsync(UUID uuid, DatabaseValue databaseValue) {
        return CompletableFuture.supplyAsync(() -> {
            List<String> values = new ArrayList<>();
            try (Connection connection = getMySQL().getConnection();
                 PreparedStatement ps = connection.prepareStatement("SELECT " + databaseValue.getName() + " FROM " + tableString + " WHERE UUID=?")) {
                ps.setString(1, uuid.toString());
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String xp = rs.getString(databaseValue.getName());
                        values.add(xp);
                    }
                    return values;
                }
            } catch (SQLException e) {
                throw new CompletionException(e);
            }
        });
    }

    public CompletableFuture<Void> setDoubleAsync(DatabaseValue databaseValue) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        Logger logger = LoggerFactory.getLogger(getClass());

        CompletableFuture<Boolean> existsFuture = existsAsync(databaseValue.getMysqlUUID());

        existsFuture.thenAcceptAsync(exists -> {
            try (Connection connection = getMySQL().getConnection()) {
                if (!exists) {
                    try (PreparedStatement ps2 = connection.prepareStatement("INSERT IGNORE INTO " + tableString + " (" + databaseValue.getName() + ",UUID) VALUES (?,?)")) {
                        ps2.setDouble(1, databaseValue.getDoubleValue());
                        ps2.setString(2, databaseValue.getMysqlUUID().toString());
                        ps2.executeUpdate();
                    }
                } else {
                    try (PreparedStatement ps = connection.prepareStatement("UPDATE " + tableString + " SET " + databaseValue.getName() + "=? WHERE UUID=?")) {
                        ps.setDouble(1, databaseValue.getDoubleValue());
                        ps.setString(2, databaseValue.getMysqlUUID().toString());
                        ps.executeUpdate();
                    }
                }
                future.complete(null);
            } catch (SQLException e) {
                logger.error("Error occurred while executing SQL query for setDoubleAsync", e);
                future.completeExceptionally(e);
            }
        }).exceptionally(ex -> {
            logger.error("Error occurred during setDoubleAsync operation", ex);
            future.completeExceptionally(ex);
            return null;
        });

        return future;
    }


    public CompletableFuture<Void> removeDoubleAsync(Double i, DatabaseValue databaseValue) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = getMySQL().getConnection();
                 PreparedStatement ps = connection.prepareStatement("DELETE FROM " + tableString + " WHERE " + databaseValue.getName() + " = ?")) {
                ps.setDouble(1, i);
                ps.executeUpdate();
                ps.close();
                return null;
            } catch (SQLException e) {
                throw new CompletionException(e);
            }
        });
    }


    public CompletableFuture<Double> getDoubleAsync(UUID uuid, DatabaseValue databaseValue) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = getMySQL().getConnection();
                 PreparedStatement ps = connection.prepareStatement("SELECT " + databaseValue.getName() + " FROM " + tableString + " WHERE UUID=?")) {
                ps.setString(1, uuid.toString());
                try (ResultSet rs = ps.executeQuery()) {
                    double xp = 0.0;
                    if (rs.next()) {
                        xp = rs.getDouble(databaseValue.getName());
                    }
                    return xp;
                } catch (SQLException e) {
                    throw new CompletionException(e);
                }
            } catch (SQLException e) {
                throw new CompletionException(e);
            }
        });
    }

    public CompletableFuture<List<Double>> getAllDoubleAsync(DatabaseValue databaseValue) {
        return CompletableFuture.supplyAsync(() -> {
            List<Double> values = new ArrayList<>();
            try (Connection connection = getMySQL().getConnection();
                 PreparedStatement ps = connection.prepareStatement("SELECT " + databaseValue.getName() + " FROM " + tableString)) {
                try (ResultSet resultSet = ps.executeQuery()) {
                    while (resultSet.next()) {
                        Double xs = resultSet.getDouble(databaseValue.getName());
                        values.add(xs);
                    }
                    return values;
                } catch (SQLException e) {
                    throw new CompletionException(e);
                }
            } catch (SQLException e) {
                throw new CompletionException(e);
            }
        });
    }

    public CompletableFuture<Void> setUUIDAsync(DatabaseValue databaseValue) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        Logger logger = LoggerFactory.getLogger(getClass());

        CompletableFuture<Boolean> existsFuture = existsAsync(databaseValue.getMysqlUUID());

        existsFuture.thenComposeAsync(exists -> {
            try (Connection connection = getMySQL().getConnection()) {
                if (!exists) {
                    try (PreparedStatement ps2 = connection.prepareStatement("INSERT IGNORE INTO " + tableString + " (" + databaseValue.getName() + ",UUID) VALUES (?,?)")) {
                        ps2.setString(1, databaseValue.getUuidValue().toString());
                        ps2.setString(2, databaseValue.getMysqlUUID().toString());
                        ps2.executeUpdate();
                    }
                } else {
                    try (PreparedStatement ps = connection.prepareStatement("UPDATE " + tableString + " SET " + databaseValue.getName() + "=? WHERE UUID=?")) {
                        ps.setString(1, databaseValue.getUuidValue().toString());
                        ps.setString(2, databaseValue.getMysqlUUID().toString());
                        ps.executeUpdate();
                    }
                }
                return CompletableFuture.completedFuture(null);
            } catch (SQLException e) {
                logger.error("Error occurred while executing SQL query for setUUIDAsync", e);
                return CompletableFuture.failedFuture(e);
            }
        }).exceptionally(ex -> {
            logger.error("Error occurred during setUUIDAsync operation", ex);
            future.completeExceptionally(ex);
            return null;
        }).thenAccept(result -> future.complete(null));

        return future;
    }

    public CompletableFuture<Void> removeUUIDAsync(UUID i, DatabaseValue databaseValue) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = getMySQL().getConnection();
                 PreparedStatement ps = connection.prepareStatement("DELETE FROM " + tableString + " WHERE " + databaseValue.getName() + " = ?")) {
                ps.setString(1, i.toString());
                ps.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        });
    }

    public CompletableFuture<UUID> getUUIDAsync(UUID uuid, DatabaseValue databaseValue) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = getMySQL().getConnection();
                 PreparedStatement ps = connection.prepareStatement("SELECT " + databaseValue.getName() + " FROM " + tableString + " WHERE UUID=?")) {
                ps.setString(1, uuid.toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return UUID.fromString(rs.getString(databaseValue.getName()));
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                throw new CompletionException(e);
            }
            return null;
        });
    }

    public CompletableFuture<List<UUID>> getAllUUIDAsync(DatabaseValue databaseValue) {
        return CompletableFuture.supplyAsync(() -> {
            List<UUID> players = new ArrayList<>();
            try (Connection connection = getMySQL().getConnection();
                 PreparedStatement ps = connection.prepareStatement("SELECT " + databaseValue.getName() + " FROM " + tableString)) {
                try (ResultSet resultSet = ps.executeQuery()) {
                    while (resultSet.next()) {
                        UUID playerUUID = UUID.fromString(resultSet.getString(databaseValue.getName()));
                        players.add(playerUUID);
                    }
                    return players;
                }
            } catch (SQLException e) {
                e.printStackTrace();
                throw new CompletionException(e);
            }
        });
    }

    public List<UUID> getAllUUIDSync(DatabaseValue databaseValue) throws SQLException {
        List<UUID> players = new ArrayList<>();
        try (PreparedStatement ps = getMySQL().getConnection().prepareStatement("SELECT " + databaseValue.getName() + " FROM " + tableString)) {
            try (ResultSet resultSet = ps.executeQuery()) {
                while (resultSet.next()) {
                    UUID playerUUID = UUID.fromString(resultSet.getString(databaseValue.getName()));
                    players.add(playerUUID);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return players;
    }


    public List<UUID> getAllUUIDSync(UUID uuid, DatabaseValue databaseValue) {
        List<UUID> values = new ArrayList<>();
        try {
            try (PreparedStatement ps = this.getMySQL().getConnection().prepareStatement("SELECT " + databaseValue.getName() + " FROM " + this.tableString + " WHERE UUID=?")) {
                ps.setString(1, uuid.toString());
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        UUID xp = UUID.fromString(rs.getString(databaseValue.getName()));
                        values.add(xp);
                    }
                }
            }
        } catch (SQLException var6) {
            var6.printStackTrace();
        }
        return values;
    }


    public MySQL getMySQL() {
        return mySQL;
    }

    public List<DatabaseValue> getMysqlValues() {
        return databaseValues;
    }
}
