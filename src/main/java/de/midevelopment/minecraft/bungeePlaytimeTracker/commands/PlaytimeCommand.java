package de.midevelopment.minecraft.bungeePlaytimeTracker.commands;

import de.midevelopment.minecraft.bungeePlaytimeTracker.SharePoint;
import de.midevelopment.minecraft.bungeePlaytimeTracker.utils.LocaleHandler;
import de.midevelopment.minecraft.bungeePlaytimeTracker.utils.TimeConverter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;


public class PlaytimeCommand extends Command implements TabExecutor {
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
                            ChatColor.RED + LocaleHandler.get("player_only")
                    )
            );
            return;
        }

        // Subbefehl "leaderboard" verarbeiten
        if (strings.length > 0 && strings[0].equalsIgnoreCase("leaderboard")) {
            displayLeaderboard(player);
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

        TextComponent title = new TextComponent(" ⏱ " + LocaleHandler.get("playtime"));
        title.setColor(ChatColor.of("#55FFAA"));
        title.setBold(true);

        TextComponent sep = new TextComponent(" • ");
        sep.setColor(ChatColor.DARK_GRAY);

        TextComponent value = new TextComponent(prettyTime);
        value.setColor(ChatColor.of("#00D4FF"));
        value.setBold(true);

        player.sendMessage(line);
        player.sendMessage(new ComponentBuilder()
                .append(title)
                .append(sep)
                .append(value)
                .create());
        player.sendMessage(line);
    }

    private void displayLeaderboard(ProxiedPlayer player) {
        Map<String, Integer> topPlayers = SharePoint.getPlaytimeHandler().getTopPlayers(10);

        TextComponent header = new TextComponent("⏱ " + LocaleHandler.get("playtime_leaderboard"));
        header.setColor(ChatColor.of("#55FFAA"));
        header.setBold(true);

        TextComponent line = new TextComponent("——————————————————————");
        line.setColor(ChatColor.DARK_GRAY);
        line.setStrikethrough(true);

        player.sendMessage(line);
        player.sendMessage(header);
        player.sendMessage(line);

        int rank = 1;
        for (Map.Entry<String, Integer> entry : topPlayers.entrySet()) {
            String playerName = entry.getKey();

            Duration duration = TimeConverter.convertSecondsToDuration(entry.getValue());
            String prettyTime = TimeConverter.convertDurationToTimeString(duration);

            TextComponent rankComp = new TextComponent("#" + rank + " ");
            rankComp.setColor(ChatColor.GRAY);

            TextComponent nameComp = new TextComponent(playerName);
            nameComp.setColor(entry.getKey().equals(player.getUniqueId()) ?
                    ChatColor.of("#FFFF55") : ChatColor.WHITE);

            TextComponent timeComp = new TextComponent(" - " + prettyTime);
            timeComp.setColor(ChatColor.of("#00D4FF"));

            player.sendMessage(new ComponentBuilder()
                    .append(rankComp)
                    .append(nameComp)
                    .append(timeComp)
                    .create());

            rank++;
        }

        player.sendMessage(line);
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            String input = args[0].toLowerCase();
            List<String> completions = new ArrayList<>();

            if ("leaderboard".startsWith(input)) {
                completions.add("leaderboard");
            }

            return completions;
        }

        return Collections.emptyList();
    }
}