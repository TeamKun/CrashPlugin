package net.teamfruit.crashplugin;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class CrashPlugin extends JavaPlugin {

    public static Logger logger;
    public static Plugin plugin;
    public static CrashLogic logic;

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        logger = getLogger();
        logic = new CrashLogic();

        getCommand("crash").setExecutor(new CrashCommandExecutor());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

}
