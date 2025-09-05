package de.midevelopment.minecraft.bungeePlaytimeTracker.commands;

import de.midevelopment.minecraft.bungeePlaytimeTracker.SharePoint;
import de.midevelopment.minecraft.bungeePlaytimeTracker.utils.TimeConverter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;


import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


public class PlaytimeCommand extends Command {
    private final Map<UUID, Long> lastUsage = new HashMap<>();
    private final Map<UUID, Integer> playtimeCache = new HashMap<>();
    private static final long COOLDOWN_TIME = TimeUnit.MINUTES.toMillis(1);

    public PlaytimeCommand() {
        super("playtime");
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        if (!(commandSender instanceof ProxiedPlayer player)) {
            commandSender.sendMessage(
                    new TextComponent(
                            ChatColor.RED + "You must be a player to use this command. The console has no playtime!"
                    )
            );
            return;
        }

        UUID playerUUID = player.getUniqueId();
        long currentTime = System.currentTimeMillis();
        int playtime;

        if (lastUsage.containsKey(playerUUID) &&
                currentTime - lastUsage.get(playerUUID) < COOLDOWN_TIME &&
                playtimeCache.containsKey(playerUUID)) {
            playtime = playtimeCache.get(playerUUID);
        } else {
            playtime = SharePoint.getPlaytimeHandler().getPlayerCurrentPlaytime(playerUUID);
            playtimeCache.put(playerUUID, playtime);
            lastUsage.put(playerUUID, currentTime);
        }

        Duration duration = TimeConverter.convertSecondsToDuration(playtime);
        String prettyTime = TimeConverter.getDetailedTimeString(duration);

        TextComponent line = new TextComponent("——————————————");
        line.setColor(ChatColor.DARK_GRAY);
        line.setStrikethrough(true);

        TextComponent title = new TextComponent(" ⏱ Playtime");
        title.setColor(ChatColor.of("#55FFAA"));
        title.setBold(true);

        TextComponent sep = new TextComponent(" • ");
        sep.setColor(ChatColor.DARK_GRAY);

        TextComponent value = new TextComponent(prettyTime);
        value.setColor(ChatColor.of("#00D4FF"));
        value.setBold(true);

        // Sende die formatierten Zeilen
        player.sendMessage(line);
        player.sendMessage(new ComponentBuilder()
                .append(title)
                .append(sep)
                .append(value)
                .create());
        player.sendMessage(line);

    }
}
