package de.midevelopment.minecraft.bungeePlaytimeTracker.spigot;

import de.midevelopment.minecraft.bungeePlaytimeTracker.SharePoint;
import de.midevelopment.minecraft.bungeePlaytimeTracker.utils.TimeConverter;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

public class PlaceholderAPIWrapper extends PlaceholderExpansion {

    @Override
    public String getIdentifier() {
        return "playtimetracker";
    }


    @Override
    public String getAuthor() {
        return "Miku";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        if (player == null) return "";
        switch (identifier) {
            case "short":
                return String.valueOf(TimeConverter.convertDurationToTimeStringShort(
                        TimeConverter.convertSecondsToDuration(SharePoint.getPlaytimeHandler().getPlayerPlaytime(player.getUniqueId()))
                ));
            case "normal":
                return String.valueOf(TimeConverter.convertDurationToTimeString(
                        TimeConverter.convertSecondsToDuration(SharePoint.getPlaytimeHandler().getPlayerPlaytime(player.getUniqueId()))
                ));
            case "long":
                return String.valueOf(TimeConverter.getDetailedTimeString(
                        TimeConverter.convertSecondsToDuration(SharePoint.getPlaytimeHandler().getPlayerPlaytime(player.getUniqueId()))
                ));
            default:
                return String.valueOf(SharePoint.getPlaytimeHandler().getPlayerPlaytime(player.getUniqueId()));
        }
    }
}
