package me.rainny.reaper.factionutils.args;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.doctordark.utils.other.command.CommandArgument;

import me.rainny.reaper.HCF;
import me.rainny.reaper.factionutils.FactionMember;
import me.rainny.reaper.factionutils.struct.Role;
import me.rainny.reaper.factionutils.type.PlayerFaction;

public class FactionOpenArgument extends CommandArgument {

    private final HCF plugin;

    public FactionOpenArgument(HCF plugin) {
        super("open", "Opens the faction to the public.");
        this.plugin = plugin;
    }

    @Override
    public String getUsage(String label) {
        return '/' + label + ' ' + getName();
    }

    @SuppressWarnings("deprecation")
	@Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
            return true;
        }

        Player player = (Player) sender;
        PlayerFaction playerFaction = plugin.getFactionManager().getPlayerFaction(player);

        if (playerFaction == null) {
            sender.sendMessage(ChatColor.RED + "You are not in a faction.");
            return true;
        }

        FactionMember factionMember = playerFaction.getMember(player.getUniqueId());

        if (factionMember.getRole() != Role.LEADER) {
            sender.sendMessage(ChatColor.RED + "You must be a faction leader to do this.");
            return true;
        }

        boolean newOpen = !playerFaction.isOpen();
        playerFaction.setOpen(newOpen);
        playerFaction.broadcast(ChatColor.GRAY + sender.getName() + " has " + (newOpen ? ChatColor.GREEN + "opened" : ChatColor.RED + "closed") + ChatColor.GRAY + " the faction to public.");
        return true;
    }
}
