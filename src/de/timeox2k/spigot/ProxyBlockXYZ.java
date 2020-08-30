package de.timeox2k.spigot;

import org.bukkit.plugin.java.JavaPlugin;

import de.timeox2k.spigot.listener.ConnectionListener;

public class ProxyBlockXYZ extends JavaPlugin {
	
	private static ProxyBlockXYZ instance;
	
	@Override
	public void onEnable() {
		instance = this;
		
		getServer().getPluginManager().registerEvents(new ConnectionListener(), this);
	}
	
	public static ProxyBlockXYZ getInstance() {
		return instance;
	}

}
