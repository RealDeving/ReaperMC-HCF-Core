package me.rainny.reaper.args;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.doctordark.utils.other.chat.ClickAction;
import com.doctordark.utils.other.chat.Text;

public class StaffInfoCommand implements CommandExecutor{

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        Player p = (Player) sender;

        if (cmd.getName().equalsIgnoreCase("staffinfo")) {
            if(p.hasPermission("hcf.command.staffinfo")) {
                if (args.length == 0) {
                    p.sendMessage("§c/staffinfo paper {Name}");
                    p.sendMessage("§7- §cCheating");
                    new Text("[Click to View Info]").setColor(ChatColor.RED).setClick(ClickAction.RUN_COMMAND, "/staffinfo page cheating").send(p);
                    p.sendMessage("§7- §cXray");
                    new Text("[Click to View Info]").setColor(ChatColor.AQUA).setClick(ClickAction.RUN_COMMAND, "/staffinfo page xray").send(p);
                    p.sendMessage("§7- §cMods");
                    new Text("[Click to View Info]").setColor(ChatColor.GRAY).setClick(ClickAction.RUN_COMMAND, "/staffinfo page mods").send(p);
                    p.sendMessage("§7- §cChat");
                    new Text("[Click to View Info]").setColor(ChatColor.GREEN).setClick(ClickAction.RUN_COMMAND, "/staffinfo page chat").send(p);
                    p.sendMessage("4. §dIllegal");
                    new Text("[Click to open Illegal]").setColor(ChatColor.GREEN).setClick(ClickAction.RUN_COMMAND, "/staffinfo page illegal").send(p);
                    return true;
                }
                if (args.length == 1) {
                    if (args[0].equalsIgnoreCase("paper")) {
                        p.sendMessage("§c/staffinfo paper {Name}");
                        p.sendMessage("1. §7§cHacking");
                        new Text("[Click to open Hacking]").setColor(ChatColor.GREEN).setClick(ClickAction.RUN_COMMAND, "/staffinfo page cheating").send(p);
                        p.sendMessage("2. §cXray");
                        new Text("[CFlick to open Xray]").setColor(ChatColor.GREEN).setClick(ClickAction.RUN_COMMAND, "/staffinfo page xray").send(p);
                        p.sendMessage("3. §cMods");
                        new Text("[Click to open Mods]").setColor(ChatColor.GREEN).setClick(ClickAction.RUN_COMMAND, "/staffinfo page mods").send(p);
                        p.sendMessage("4. §cChat");
                        new Text("[Click to open chat]").setColor(ChatColor.GREEN).setClick(ClickAction.RUN_COMMAND, "/staffinfo page chat").send(p);
                        p.sendMessage("4. §dIllegal");
                        new Text("[Click to open Illegal]").setColor(ChatColor.GREEN).setClick(ClickAction.RUN_COMMAND, "/staffinfo page illegal").send(p);
                        return true;
                    }
                }

                if (args[1].equalsIgnoreCase("cheating")) {
                    p.sendMessage("§8§m-----------------§8[§cHacking§8]§m------------------");
                    p.sendMessage("§cObvious Hacking - IP-Ban");
                    p.sendMessage("§8>§cHacking (Admitted) - Perm ban");
                    p.sendMessage("§8>§cGhost Client - Perm ban");
                    p.sendMessage("§8>§cGhost Client (Admitted) - Perm ban");
                    p.sendMessage("§8>§cBunnyHopping - IP-Ban");
                    p.sendMessage("§8>§cPhasing - IP-Ban");
                    p.sendMessage("§8>§cSpeed Hacks - IP-Ban");
                    p.sendMessage("§8>§cFly - IP-Ban");
                    p.sendMessage("§8>§cReach - Perm Ban");
                    p.sendMessage("§8>§cReach (Admitted) - 14d ban");
                    p.sendMessage("§8>§cAutoClicker - 30d ban");
                    p.sendMessage("§8>§cAutoClicker (Admitted) - 14d ban");
                    p.sendMessage("§8§m-----------------§8[§cHacking§8]§m------------------");

                    return true;
                }

                if (args[1].equalsIgnoreCase("xray")) {
                    p.sendMessage("§8§m------------------§8[§bXray§8]§m-------------------");
                    p.sendMessage("§8>§bXray Ores - 14d ban");
                    p.sendMessage("§8>§bXray Ores (Admitted) - 7d ban");
                    p.sendMessage("§8>§bXray Player - 21d ban");
                    p.sendMessage("§8>§bXray Player (Admitted) - 21d ban");
                    p.sendMessage("§8>§bXray Spawner - 15d ban");
                    p.sendMessage("§8>§bXray Spawner (Admitted) - 1w ban");
                    p.sendMessage("§8§m------------------§8[§bXray§8]§m-------------------");
                    return true;
                }

                if (args[1].equalsIgnoreCase("mods")) {
                    p.sendMessage("§8§m------------------§8[§7Mods§8]§m-------------------");
                    p.sendMessage("§8>§7Reis MiniMap (Radar In It) - 2w ban");
                    p.sendMessage("§8>§7Reis MiniMap (Radar In It - Admitted) - 1w ban");
                    p.sendMessage("§8>§7No Lite Loader - Perm ban");
                    p.sendMessage("§8>§7No Lite Loader (Admitted) - 1m ban");
                    p.sendMessage("§8§m------------------§8[§7Mods§8]§m-------------------");
                    return true;
                }

                if (args[1].equalsIgnoreCase("illegal")) {
                    p.sendMessage("§8§m------------------§8[§dIllegal§8]§m-------------------");
                    p.sendMessage("§8>§dInsiding: 2 week ban.");
                    p.sendMessage("§> §dKick & Kill: 2 week ban.");
                    p.sendMessage("§8§m------------------§8[§dIllegal§8]§m-------------------");
                    return true;
                }

                if (args[1].equalsIgnoreCase("chat")) {
                    p.sendMessage("§8§m------------------§8[§aChat§8]§m-------------------");
                    p.sendMessage("§8>§aSuicidal Encouragement - 1d mute");
                    p.sendMessage("§8>§aDdos / Dox - Perm ban");
                    p.sendMessage("§8>§aSpamming - 10m mute");
                    p.sendMessage("§8>§aFaction Spam - 20m mute");
                    p.sendMessage("§8>§aAdvertising IP - Perm ban (Give them about 5 sec before ban just incase it was an accident) ");
                    p.sendMessage("§8>§aTs Ip Post - ClearChat ASAP; 3h mute");
                    p.sendMessage("§8§m------------------§8[§aChat§8]§m-------------------");
                    return true;
                } else {
                    p.sendMessage("§dNo Permissions to view our staff script!");
                }
            }
        }

        return false;
    }
}
