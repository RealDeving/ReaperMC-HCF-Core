package me.rainny.reaper.factionutils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.doctordark.utils.other.command.ArgumentExecutor;
import com.doctordark.utils.other.command.CommandArgument;

import me.rainny.reaper.HCF;
import me.rainny.reaper.factionutils.args.*;
import me.rainny.reaper.factionutils.args.staff.*;

/**
 * Class to handle the command and tab completion for the faction command.
 */
public class FactionExecutor extends ArgumentExecutor {

	private final CommandArgument helpArgument;

	public FactionExecutor(HCF plugin) {
		super("faction");

		addArgument(new FactionManagerArgument(plugin));
		addArgument(new FactionAcceptArgument(plugin));
		addArgument(new FactionAllyArgument(plugin));
		addArgument(new FactionAnnouncementArgument(plugin));
		addArgument(new FactionChatArgument(plugin));
		addArgument(new FactionChatSpyArgument(plugin));
		addArgument(new FactionClaimArgument(plugin));
		addArgument(new FactionClaimChunkArgument(plugin));
		addArgument(new FactionClaimForArgument(plugin));
		addArgument(new FactionClaimsArgument(plugin));
		addArgument(new FactionClearClaimsArgument(plugin));
		addArgument(new FactionCreateArgument(plugin));
		addArgument(new FactionDemoteArgument(plugin));
		addArgument(new FactionDepositArgument(plugin));
		addArgument(new FactionDisbandArgument(plugin));
		addArgument(new FactionSetDtrRegenArgument(plugin));
		addArgument(new FactionForceDemoteArgument(plugin));
		addArgument(new FactionForceJoinArgument(plugin));
		addArgument(new FactionForceKickArgument(plugin));
		addArgument(new FactionForceLeaderArgument(plugin));
		addArgument(new FactionForcePromoteArgument(plugin));
		addArgument(helpArgument = new FactionHelpArgument(this));
		addArgument(new FactionHomeArgument(this, plugin));
		addArgument(new FactionInviteArgument(plugin));
		addArgument(new FactionInvitesArgument(plugin));
		addArgument(new FactionKickArgument(plugin));
		addArgument(new FactionLeaderArgument(plugin));
		addArgument(new FactionLeaveArgument(plugin));
		addArgument(new FactionListArgument(plugin));
		addArgument(new FactionMapArgument(plugin));
		addArgument(new FactionMessageArgument(plugin));
		addArgument(new FactionOpenArgument(plugin));
		addArgument(new FactionRemoveArgument(plugin));
		addArgument(new FactionRenameArgument(plugin));
		addArgument(new FactionPromoteArgument(plugin));
		addArgument(new FactionSetDtrArgument(plugin));
		addArgument(new FactionSetDeathbanMultiplierArgument(plugin));
		addArgument(new FactionSetHomeArgument(plugin));
		addArgument(new FactionShowArgument(plugin));
		addArgument(new FactionStuckArgument(plugin));
		addArgument(new FactionSubclaimArgumentExecutor(plugin));
		addArgument(new FactionTopArgument(plugin));
		addArgument(new FactionUnclaimArgument(plugin));
		addArgument(new FactionUnallyArgument(plugin));
		addArgument(new FactionUninviteArgument(plugin));
		addArgument(new FactionUnsubclaimArgument(plugin));
		addArgument(new FactionWithdrawArgument(plugin));
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length < 1) {
			helpArgument.onCommand(sender, command, label, args);
			return true;
		}

		CommandArgument argument = getArgument(args[0]);
		if (argument != null) {
			String permission = argument.getPermission();
			if (permission == null || sender.hasPermission(permission)) {
				argument.onCommand(sender, command, label, args);
				return true;
			}
		}

		helpArgument.onCommand(sender, command, label, args);
		return true;
	}
}
