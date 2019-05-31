package me.rainny.reaper.factionutils.args;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

import com.doctordark.utils.other.command.CommandArgument;

import me.rainny.reaper.HCF;
import me.rainny.reaper.factionutils.claim.ClaimHandler;
import me.rainny.reaper.factionutils.type.PlayerFaction;

import java.util.UUID;

public class FactionClaimArgument extends CommandArgument {

    private final HCF plugin;

    public FactionClaimArgument(HCF plugin) {
        super("claim", "Claim land in the Wilderness.", new String[] { "claimland" });
        this.plugin = plugin;
    }

    @Override
    public String getUsage(String label) {
        return '/' + label + ' ' + getName();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
            return true;
        }

        Player player = (Player) sender;
        UUID uuid = player.getUniqueId();

        PlayerFaction playerFaction = plugin.getFactionManager().getPlayerFaction(uuid);

        if (playerFaction == null) {
            sender.sendMessage(ChatColor.RED + "You are not in a faction.");
            return true;
        }

        if (playerFaction.isRaidable()) {
            sender.sendMessage(ChatColor.RED + "You cannot claim land for your faction while raidable.");
            return true;
        }

        PlayerInventory inventory = player.getInventory();

        if (inventory.contains(ClaimHandler.CLAIM_WAND)) {
            sender.sendMessage(ChatColor.RED + "You already have a claiming wand in your inventory.");
            return true;
        }

        if (inventory.contains(ClaimHandler.SUBCLAIM_WAND)) {
            sender.sendMessage(ChatColor.RED + "You cannot have a claiming wand whilst you have a subclaim wand in your inventory.");
            return true;
        }

        if (!inventory.addItem(ClaimHandler.CLAIM_WAND).isEmpty()) {
            sender.sendMessage(ChatColor.RED + "Your inventory is full.");
            return true;
        }

        sender.sendMessage(ChatColor.GRAY + "Claiming wand added to inventory, read the item to understand how to claim. You can also" + 
        ChatColor.GRAY + " use " + ChatColor.AQUA + '/' + label + " claimchunk" + ChatColor.GRAY + '.');

        return true;
    }
}
