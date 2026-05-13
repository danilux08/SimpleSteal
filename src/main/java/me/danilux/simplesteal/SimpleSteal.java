package me.danilux.simplesteal;

import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import me.danilux.simplesteal.commands.Command;
import me.danilux.simplesteal.commands.SSCommand;
import me.danilux.simplesteal.config.ConfigManager;
import me.danilux.simplesteal.database.DBManager;
import me.danilux.simplesteal.listeners.EntityListener;
import me.danilux.simplesteal.listeners.PlayerListener;
import me.danilux.simplesteal.utils.BanUtils;
import me.danilux.simplesteal.utils.FormatUtils;
import me.danilux.simplesteal.utils.lifesteal.LifeStealUtils;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class SimpleSteal extends JavaPlugin {

    private final ConfigManager configManager = new ConfigManager(this);
    private final DBManager dbManager = new DBManager(this);
    private final FormatUtils formatUtils = new FormatUtils(this);
    private final BanUtils banUtils = new BanUtils(this);
    private final LifeStealUtils lifeStealUtils = new LifeStealUtils(this);

    @Override
    public void onEnable() {
        this.getLogger().info("Registering events...");
        this.registerListeners(new EntityListener(this), new PlayerListener(this));
        this.getLogger().info("Registering commands...");
        this.registerCommands();
        this.getLogger().info("Loading configurations...");
        this.configManager.loadAll();
        this.getLogger().info("Connecting databases...");
        this.dbManager.connectAll();
        this.getLogger().info("Done!");
    }

    @Override
    public void onDisable() {
        this.getLogger().info("Disconnecting databases...");
        this.dbManager.disconnectAll();
        this.getLogger().info("Saving configurations...");
        this.configManager.saveAll();
        this.getLogger().info("Bye, bye!");
    }

    private void registerListeners(Listener... listeners) {
        for(Listener listener : listeners) this.getServer().getPluginManager().registerEvents(listener, this);
    }

    private void registerCommands() {
        this.getLifecycleManager().registerEventHandler(
                LifecycleEvents.COMMANDS,
                event -> {
                    this.registerCommand(new SSCommand(this), event.registrar());
                }
        );
    }

    private void registerCommand(Command command, Commands commands) {
        commands.register(command.build(), command.getDescription(), command.getAliases());
    }

    public ConfigManager getConfigManager() {
        return this.configManager;
    }

    public DBManager getDBManager() {
        return this.dbManager;
    }

    public FormatUtils getFormatUtils() {
        return this.formatUtils;
    }

    public BanUtils getBanUtils() {
        return this.banUtils;
    }

    public LifeStealUtils getLifeStealUtils() {
        return this.lifeStealUtils;
    }
}
