package xyz.proxyblock.spigot;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.proxyblock.spigot.listener.ConnectionListener;

import java.util.Arrays;
import java.util.logging.Logger;

public class ProxyBlockXYZ extends JavaPlugin implements CommandExecutor {

    private static ProxyBlockXYZ instance;
    private static Logger proxyLogger;

    public static Logger getProxyLogger() {
        return proxyLogger;
    }

    public static ProxyBlockXYZ getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        proxyLogger = getLogger();

        getConfig().addDefault("ProxyBlockXYZ.clearCache", true);
        getConfig().addDefault("ProxyBlockXYZ.clearCacheAfterSize", 100000);
        getConfig().addDefault("ProxyBlockXYZ.clearCacheAfterMinutes", 60);
        getConfig().addDefault("ProxyBlockXYZ.maxCheckThreads", 10);
        getConfig().addDefault("ProxyBlockXYZ.whitelist", Arrays.asList(new String[]{"a06e0573ccdb4b8da6bb68bff581862d", "a06e0573-ccdb-4b8d-a6bb-68bff581862d", "Timeox2k", "123.456.789.101"}));

        getConfig().options().copyDefaults(true);
        saveConfig();

        getServer().getPluginManager().registerEvents(new ConnectionListener(), this);

        ConnectionListener.whitelist.addAll(getConfig().getStringList("ProxyBlockXYZ.whitelist"));

        if (getConfig().getBoolean("ProxyBlockXYZ.clearcache")) {
            new BukkitRunnable() {

                @Override
                public void run() {
                    ConnectionListener.addressCache.clear();
                    ProxyBlockXYZ.getProxyLogger().info("Cleared the Address-Cache after reaching Timelimit.");
                }
            }.runTaskTimerAsynchronously(getInstance(), 20 * 60 * getConfig().getInt("ProxyBlockXYZ.clearCacheAfterMinutes"), 20 * 60 * getConfig().getInt("ProxyBlockXYZ.clearCacheAfterMinutes"));
        }
    }

    @Override
    public boolean onCommand(CommandSender from, Command command, String label, String[] args) {
        if ((label.equalsIgnoreCase("pb") || label.equalsIgnoreCase("proxyblock"))) {
            if (args.length > 0 && from.hasPermission("proxyblock.manage")) {
                String action = args[0];
                switch (action.toLowerCase()) {
                    case "whitelist":
                        if (args.length > 2) {
                            String subAction = args[1];
                            String object = args[2];
                            switch (subAction.toLowerCase()) {
                                case "add":
                                    if (!ConnectionListener.whitelist.contains(object)) {
                                        from.sendMessage("§7[§aProxyBlockXYZ§7] §aadded " + object + " to the whitelist!");
                                        ConnectionListener.whitelist.add(object);
                                        getConfig().set("ProxyBlockXYZ.whitelist", ConnectionListener.whitelist);
                                        saveConfig();
                                        break;
                                    }
                                    from.sendMessage("§7[§aProxyBlockXYZ§7] §c" + object + " is already whitelisted.");
                                    break;
                                case "remove":
                                    if (ConnectionListener.whitelist.contains(object)) {
                                        from.sendMessage("§7[§aProxyBlockXYZ§7] §aremoved " + object + " from the whitelist!");
                                        ConnectionListener.whitelist.remove(object);
                                        getConfig().set("ProxyBlockXYZ.whitelist", ConnectionListener.whitelist);
                                        saveConfig();
                                        break;
                                    }
                                    from.sendMessage("§7[§aProxyBlockXYZ§7] §c" + object + " is not whitelisted.");

                                    break;
                                default:
                                    printHelpWhitelist(from, label);
                                    break;
                            }
                            break;
                        }
                        printHelpWhitelist(from, label);
                        break;
                    case "enable":
                        ConnectionListener.tempToggle = false;
                        from.sendMessage("§7[§aProxyBlockXYZ§7] §aProxyBlock is now enabled!");
                        break;
                    case "disable":
                        ConnectionListener.tempToggle = true;
                        from.sendMessage("§7[§aProxyBlockXYZ§7] §cProxyBlock is now disabled!");
                        break;
                    case "reload":
                        reloadConfig();
                        from.sendMessage("§7[§aProxyBlockXYZ§7] §aconfig reloaded.");
                        ConnectionListener.whitelist.clear();
                        ConnectionListener.whitelist.addAll(getConfig().getStringList("ProxyBlockXYZ.whitelist"));
                        break;
                    default:
                        printHelp(from, label);
                        break;
                }
            } else {
                printHelp(from, label);
            }
        }
        return false;
    }

    private void printHelp(CommandSender from, String label) {
        from.sendMessage("§7---------§7[§2ProxyBlockXYZ§7]---------");
        from.sendMessage("§7/§2" + label + " §lwhitelist §a-> §7shows the whitelist usage");
        from.sendMessage("§7/§2" + label + " §lenable §a-> §7enables the proxyblock service");
        from.sendMessage("§7/§2" + label + " §ldisable §a-> §7disables the proxyblock service");
        from.sendMessage("§7/§2" + label + " §lreload §a-> §7reloads the config");
        from.sendMessage("§7---------------------------------");
    }

    private void printHelpWhitelist(CommandSender from, String label) {
        from.sendMessage("§7---------§7[§2ProxyBlockXYZ - Whitelist§7]---------");
        from.sendMessage("§7/§2" + label + " §lwhitelist add <Player/IP/UUID> §a-> §7adds a Player/IP/UUID");
        from.sendMessage("§7/§2" + label + " §lwhitelist remove <Player/IP/UUID> §a-> §7removes a Player/IP/UUID");
        from.sendMessage("§7---------------------------------------------");
    }

}
