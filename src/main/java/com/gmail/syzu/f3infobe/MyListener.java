package com.gmail.syzu.f3infobe;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.text.DecimalFormat;
import java.util.HashMap;

public class MyListener implements Listener
{
    public static void main(String[] args) {
        System.out.println("Hello, NotSyzu!");
    }

    private static DecimalFormat twoPlaces = new DecimalFormat("#.###");
    static HashMap<Player, BossBar> bossBarMap = new HashMap<>();
    static HashMap<Player, Boolean> showDebugScreenMap = new HashMap<>();

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        updateInfo(player, e.getTo());
    }

    public static void updateInfo(Player player, Location location) {
        // Return if no brand yet sent
        if (BrandPluginMessageListener.playerBrands.get(player) == null) return;
        // Return if not Geyser
        if (!BrandPluginMessageListener.playerBrands.get(player).equals("Geyser")) return;

        BossBar currentBossBar = bossBarMap.get(player);

        // If the player boss bar isn't null and should be removed
        Boolean currentOption = showDebugScreenMap.get(player);
        if (currentOption == null) currentOption = true;

        if (currentBossBar != null && !currentOption) {
            // Remove the boss bar
            currentBossBar.removePlayer(player);
            bossBarMap.remove(player);
        }

        if (!currentOption) return;

        Block targetBlock = player.getTargetBlockExact(6);

        if (currentBossBar == null) {
            currentBossBar = Bukkit.createBossBar(getBossBarTitle(player, location, targetBlock), BarColor.BLUE, BarStyle.SOLID);
            currentBossBar.addPlayer(player);
            bossBarMap.put(player, currentBossBar);
        } else {
            currentBossBar.setTitle(getBossBarTitle(player, location, targetBlock));
        }

        if (targetBlock == null || targetBlock.getLocation() == null) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("-"));
        } else {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(
                    targetBlock.getBlockData().getAsString()
                            .replaceFirst("^minecraft:", "") // Frees up space
                            .replaceAll(",", ",\n") // Split onto
                            .replaceAll("\\[", "\n[") // multiple lines
            ));
        }
    }

    private static String getBossBarTitle(Player player, Location location, Block targetBlock) {
        String debugString = "----- Debug Screen -----\n\n" +
                "Pos: " +  twoPlaces.format(location.getX()) + " " +  twoPlaces.format(location.getY()) +
                " " +  twoPlaces.format(location.getZ());

        if (targetBlock == null || targetBlock.getLocation() == null) {
            debugString += "\nLooking at: - - -";
        } else {
            debugString += "\nLooking at: " + (int) targetBlock.getLocation().getX() + " " +
                    (int) targetBlock.getLocation().getY() + " " +
                    (int) targetBlock.getLocation().getZ();
        }

        debugString += "\nFacing: ";

        // https://stackoverflow.com/questions/35831619/get-the-direction-a-player-is-looking - second answer
        float yaw = location.getYaw();
        if (yaw < 0) {
            yaw += 360;
        }
        if (yaw >= 315 || yaw < 45) {
            debugString += "south";
        } else if (yaw < 135) {
            debugString += "west";
        } else if (yaw < 225) {
            debugString += "north";
        } else if (yaw < 315) {
            debugString += "east";
        } else {
            debugString += "north";
        }

        return debugString;
    }
}
