package me.tahacheji.mafana;

import me.tahacheji.mafana.data.MySQL;
import me.tahacheji.mafana.data.SQLGetter;
import org.bukkit.plugin.java.JavaPlugin;

public final class MafanaDatabaseAPI extends JavaPlugin {

    private MySQL mySQL;
    private static MafanaDatabaseAPI instance;
    private SQLGetter sqlGetter;

    @Override
    public void onEnable() {
        instance = this;
    }

    public SQLGetter getSqlGetter() {
        return sqlGetter;
    }

    public static MafanaDatabaseAPI getInstance() {
        return instance;
    }

    public MySQL getMySQL() {
        return mySQL;
    }

}
