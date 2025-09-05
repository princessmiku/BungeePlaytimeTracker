package de.midevelopment.minecraft.bungeePlaytimeTracker.listener;

import de.midevelopment.minecraft.bungeePlaytimeTracker.SharePoint;
import de.midevelopment.minecraft.bungeePlaytimeTracker.database.PlaytimeHandler;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

public class PlayerListener implements Listener {

    private final Plugin plugin;

    public PlayerListener(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void postLoginEvent(PostLoginEvent event) {
        ProxiedPlayer player = event.getPlayer();

        ProxyServer.getInstance().getScheduler().runAsync(plugin, () -> {
            SharePoint.getPlaytimeHandler().registerPlayer(player);
        });
    }

    @EventHandler
    public void playerDisconnectEvent(PlayerDisconnectEvent event) {
        ProxiedPlayer player = event.getPlayer();
        ProxyServer.getInstance().getScheduler().runAsync(plugin, () -> {
            SharePoint.getPlaytimeHandler().stopPlaytime(SharePoint.getPlayerSession(player.getUniqueId()));
            SharePoint.removePlayerSession(player.getUniqueId());
            SharePoint.getPlaytimeHandler().getPlayerCurrentPlaytime(event.getPlayer().getUniqueId());
        });
    }

    @EventHandler
    public void playerServerSwitchEvent(ServerConnectedEvent event) {
        ProxiedPlayer player = event.getPlayer();
        ProxyServer.getInstance().getScheduler().runAsync(plugin, () -> {
            if (SharePoint.hasPlayerSession(player.getUniqueId())) {
                SharePoint.getPlaytimeHandler().stopPlaytime(SharePoint.getPlayerSession(player.getUniqueId()));
            }
            int sessionId = SharePoint.getPlaytimeHandler().startPlaytime(player.getUniqueId(), event.getServer().getInfo().getName());
            SharePoint.setPlayerSession(player.getUniqueId(), sessionId);
        });
    }

}
