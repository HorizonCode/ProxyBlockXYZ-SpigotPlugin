package de.timeox2k.spigot.listener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;

public class ConnectionListener implements Listener {

	private final ArrayList<String> addressCache = new ArrayList<>();
	private final ArrayList<String> blockedCache = new ArrayList<>();

	@EventHandler
	public void on(AsyncPlayerPreLoginEvent event) {
		String address = event.getAddress().getHostName().replace("\\", "");

		
		if(address.equalsIgnoreCase("127.0.0.1"))
			return;
		
		if (blockedCache.contains(address)) {
			event.disallow(Result.KICK_OTHER,
					"§cProxyBlock.XYZ\n\n§4We found your IP in our Database.\n\n§cPlease disable your VPN.\n\n§aMore Information: https://proxyblock.xyz");
		}

		if (!addressCache.contains(address) && !blockedCache.contains(address)) {
			try {
				URL url = new URL("https://proxyblock.xyz/api/check.php?ip=" + address);
				URLConnection urlConnection = url.openConnection();
				urlConnection.setRequestProperty("User-Agent",
						"Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");

				BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
				String line;
				while ((line = in.readLine()) != null) {
					if (line.contains("1")) {
						event.disallow(Result.KICK_OTHER,
								"§cProxyBlock.XYZ\n\n§4We found your IP in our Database.\n\n§cPlease disable your VPN.\n\n§aMore Information: https://proxyblock.xyz");
						blockedCache.add(address);
					}
				}

				addressCache.add(address);

			} catch (IOException e) {
				e.printStackTrace();
				event.disallow(Result.KICK_OTHER,
						"§cProxyBlock.XYZ\n\n§4We were unable to check your IP.\n\n§cPlease try again later.\n\n§aMore Information: https://proxyblock.xyz");
			}
		}

	}
	
	@EventHandler
	public void on(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		String address = player.getAddress().getHostName().replace("\\", "");

		
		if(address.equalsIgnoreCase("127.0.0.1")) {
			player.sendMessage("§7[§aProxyBlockXYZ§7] §cWe are unable to Check your IP because it is an Localhost Server or IP-Forwarding on the Server is not enabled!");
		}else if(!blockedCache.contains(address)) {
			player.sendMessage("§7[§aProxyBlockXYZ§7] §aWe have checked your IP and you are not using a VPN! Thank you!");
		}
	}

}
