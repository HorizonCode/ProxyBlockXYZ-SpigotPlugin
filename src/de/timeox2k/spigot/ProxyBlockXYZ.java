package de.timeox2k.spigot;

import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import de.timeox2k.spigot.listener.ConnectionListener;

public class ProxyBlockXYZ extends JavaPlugin {
	
	private static ProxyBlockXYZ instance;
	private static Logger proxyLogger;
	@Override
	public void onEnable() {
		instance = this;
		proxyLogger = getLogger();
		getServer().getPluginManager().registerEvents(new ConnectionListener(), this);
		
		getConfig().addDefault("ProxyBlockXYZ.clearCache", true);
		getConfig().addDefault("ProxyBlockXYZ.clearCacheAfterSize", 100000);
		getConfig().addDefault("ProxyBlockXYZ.clearCacheAfterMinutes", 60);

		getConfig().options().copyDefaults(true);
		saveConfig();

		if(getConfig().getBoolean("ProxyBlockXYZ.clearcache")) {
			new BukkitRunnable() {
				
				@Override
				public void run() {
					ConnectionListener.addressCache.clear();
					ProxyBlockXYZ.getProxyLogger().info("Cleared the Address-Cache after reaching Timelimit.");
				}
			}.runTaskTimer(getInstance(), 20 * 60 * getConfig().getInt("ProxyBlockXYZ.clearCacheAfterMinutes"), 20 * 60 * getConfig().getInt("ProxyBlockXYZ.clearCacheAfterMinutes"));
			
		}
	}
	
	public static Logger getProxyLogger() {
		return proxyLogger;
	}
	
	public static ProxyBlockXYZ getInstance() {
		return instance;
	}

}
