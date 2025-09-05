package de.midevelopment.minecraft.bungeePlaytimeTracker.commands;

import de.midevelopment.minecraft.bungeePlaytimeTracker.SharePoint;
import de.midevelopment.minecraft.bungeePlaytimeTracker.database.PlaytimeHandler;
import de.midevelopment.minecraft.bungeePlaytimeTracker.utils.TimeConverter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import javax.xml.crypto.Data;

import java.time.Duration;

import static net.md_5.bungee.api.ChatColor.RED;


public class PlaytimeCommand extends Command {
    public PlaytimeCommand() {
        super("playtime");
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        if (!(commandSender instanceof ProxiedPlayer player)) {
            commandSender.sendMessage(
                    new TextComponent(
                            RED +  "You must be a player to use this command. The console has no playtime!"
                    )
            );
            return;
        }

        int playtime = SharePoint.getPlaytimeHandler().getPlayerCurrentPlaytime(player.getUniqueId());
        Duration duration = TimeConverter.convertSecondsToDuration(playtime);
        commandSender.sendMessage(
                new TextComponent(
                        ChatColor.GREEN + "Your current playtime is " + TimeConverter.getDetailedTimeString(duration)
                )
        );

    }
}
