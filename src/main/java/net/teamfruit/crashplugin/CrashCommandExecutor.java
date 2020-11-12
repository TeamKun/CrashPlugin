package net.teamfruit.crashplugin;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CrashCommandExecutor implements CommandExecutor, TabCompleter {

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("crash")) {
            sender.sendMessage(new ComponentBuilder()
                    .append("[★] ").color(ChatColor.DARK_GREEN)
                    .append("権限がありません。").color(ChatColor.RED)
                    .create()
            );
            return true;
        }

        if (args.length == 0)
            return false;

        String targetsSelector = String.join(" ", args);

        List<Player> targets;
        try {
            targets = Bukkit.selectEntities(sender, targetsSelector).stream()
                    .filter(Player.class::isInstance)
                    .map(Player.class::cast)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            sender.sendMessage(new ComponentBuilder()
                    .append("[★] ").color(ChatColor.DARK_GREEN)
                    .append("セレクターがミスっています").color(ChatColor.RED)
                    .create()
            );
            return true;
        }

        if (targets.isEmpty()) {
            sender.sendMessage(new ComponentBuilder()
                    .append("[★] ").color(ChatColor.DARK_GREEN)
                    .append(targetsSelector).color(ChatColor.RED)
                    .append("はオフラインです").color(ChatColor.RED)
                    .create()
            );
            return true;
        }

        String targetNames = targets.stream().map(Player::getName).collect(Collectors.joining(", "));

        targets.forEach(CrashPlugin.logic::crash);
        sender.sendMessage(new ComponentBuilder()
                .append("[★] ").color(ChatColor.DARK_GREEN)
                .append(targetNames).color(ChatColor.GREEN)
                .append(" をクラッシュさせました").color(ChatColor.GREEN)
                .create()
        );
        Bukkit.broadcast(new ComponentBuilder()
                .append("[★] ").color(ChatColor.DARK_PURPLE)
                .append(sender.getName()).color(ChatColor.LIGHT_PURPLE)
                .append(" が ").color(ChatColor.LIGHT_PURPLE)
                .append(targetNames).color(ChatColor.RED)
                .append(" をクラッシュさせました").color(ChatColor.RED)
                .create()
        );

        return true;
    }

    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        String targetsSelector = String.join(" ", args);

        return Stream.concat(
                Stream.of("@a", "@p", "@r"),
                Bukkit.getOnlinePlayers().stream()
                        .map(Player::getName)
                        .filter(e -> e.startsWith(targetsSelector))
        ).collect(Collectors.toList());
    }

}
