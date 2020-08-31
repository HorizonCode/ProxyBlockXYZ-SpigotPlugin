package xyz.proxyblock.spigot.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerJoinEvent;
import xyz.proxyblock.spigot.ProxyBlockXYZ;
import xyz.proxyblock.utils.WebUtils;

import java.util.LinkedList;
import java.util.concurrent.ExecutionException;

public class ConnectionListener implements Listener {

    public static final LinkedList<String> addressCache = new LinkedList<>();
    private static final LinkedList<String> blockedCache = new LinkedList<>();
    public static LinkedList<String> whitelist = new LinkedList<>();
    private WebUtils webUtils = new WebUtils(ProxyBlockXYZ.getInstance().getConfig().getInt("ProxyBlockXYZ.maxCheckThreads"));
    public static boolean tempToggle = false;

    @EventHandler
    public void onLogin(AsyncPlayerPreLoginEvent event) {
        String address = event.getAddress().getHostName().replace("\\", "");

        if (address.equalsIgnoreCase("127.0.0.1") || tempToggle)
            return;

        if (whitelist.contains(address) || whitelist.contains(event.getName()) || whitelist.contains(event.getUniqueId().toString()) || whitelist.contains(event.getUniqueId().toString().replace("-", "")))
            return;

        if (blockedCache.contains(address)) {
            event.disallow(Result.KICK_OTHER,
                    "§cProxyBlock.XYZ\n\n§4We found your IP in our Database.\n\n§cPlease disable your VPN.\n\n§aMore Information: https://proxyblock.xyz");
        }

        if (!addressCache.contains(address) && !blockedCache.contains(address)) {
            try {
                String response = webUtils.getResponseFromURLAsync("https://proxyblock.xyz/api/check.php?ip=" + address);
                if (response.contains("1")) {
                    event.disallow(Result.KICK_OTHER,
                            "§cProxyBlock.XYZ\n\n§4We found your IP in our Database.\n\n§cPlease disable your VPN.\n\n§aMore Information: https://proxyblock.xyz");
                    blockedCache.add(address);
                }

                addressCache.add(address);

            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
                event.disallow(Result.KICK_OTHER,
                        "§cProxyBlock.XYZ\n\n§4We were unable to check your IP.\n\n§cPlease try again later.\n\n§aMore Information: https://proxyblock.xyz");
            }
        }

        if (ProxyBlockXYZ.getInstance().getConfig().getBoolean("ProxyBlockXYZ.clearcache")) {
            if (addressCache.size() >= ProxyBlockXYZ.getInstance().getConfig().getInt("ProxyBlockXYZ.clearCacheAfterSize")) {
                addressCache.clear();
                ProxyBlockXYZ.getProxyLogger().info("Cleared the Address-Cache after reaching limit.");
            } else if (blockedCache.size() >= ProxyBlockXYZ.getInstance().getConfig().getInt("ProxyBlockXYZ.clearCacheAfterSize")) {
                blockedCache.clear();
                ProxyBlockXYZ.getProxyLogger().info("Cleared the Blocked-Cache after reaching limit.");
            }
        }

    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String address = player.getAddress().getHostName().replace("\\", "");
        if(tempToggle){
            player.sendMessage("§7[§aProxyBlockXYZ§7] §cWe are currently unable to check IP's because it's disabled, enable it with /pb enable !");
            return;
        }
        if (address.equalsIgnoreCase("127.0.0.1") && player.hasPermission("proxyblock.manage")) {
            player.sendMessage("§7[§aProxyBlockXYZ§7] §cWe are currently unable to check IP's because it is a localhost Server or IP-Forwarding is not enabled!");
        }
    }

}
