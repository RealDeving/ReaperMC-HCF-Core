package me.rainny.reaper.listener;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import com.google.common.base.Optional;

import me.rainny.reaper.HCF;
import me.rainny.reaper.factionutils.FactionUser;
import me.rainny.reaper.factionutils.event.CaptureZoneEnterEvent;
import me.rainny.reaper.factionutils.event.CaptureZoneLeaveEvent;
import me.rainny.reaper.factionutils.event.FactionCreateEvent;
import me.rainny.reaper.factionutils.event.FactionDtrChangeEvent;
import me.rainny.reaper.factionutils.event.FactionRemoveEvent;
import me.rainny.reaper.factionutils.event.FactionRenameEvent;
import me.rainny.reaper.factionutils.event.PlayerClaimEnterEvent;
import me.rainny.reaper.factionutils.event.PlayerJoinFactionEvent;
import me.rainny.reaper.factionutils.event.PlayerLeaveFactionEvent;
import me.rainny.reaper.factionutils.event.PlayerLeftFactionEvent;
import me.rainny.reaper.factionutils.struct.RegenStatus;
import me.rainny.reaper.factionutils.struct.Relation;
import me.rainny.reaper.factionutils.type.Faction;
import me.rainny.reaper.factionutils.type.KothFaction;
import me.rainny.reaper.factionutils.type.PlayerFaction;
import me.rainny.reaper.ymls.SettingsYML;

public class FactionListener implements Listener {

	private static final long FACTION_JOIN_WAIT_MILLIS = TimeUnit.SECONDS.toMillis(30L);
	private static final String FACTION_JOIN_WAIT_WORDS = DurationFormatUtils
			.formatDurationWords(FACTION_JOIN_WAIT_MILLIS, true, true);

	private static final String LAND_CHANGED_META_KEY = "landChangedMessage";
	private static final long LAND_CHANGE_MSG_THRESHOLD = 225L;

	private final HCF plugin;

	public FactionListener(HCF plugin) {
		this.plugin = plugin;
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onFactionRenameMonitor(FactionRenameEvent event) {
		Faction faction = event.getFaction();
		if (faction instanceof KothFaction) {
			((KothFaction) faction).getCaptureZone().setName(event.getNewName());
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onFactionCreate(FactionCreateEvent event) {
		Faction faction = event.getFaction();
		if (faction instanceof PlayerFaction) {
			CommandSender sender = event.getSender();
			for (Player player : Bukkit.getOnlinePlayers()) {
				Relation relation = faction.getRelation(player);
				String msg = ChatColor.GRAY + "The faction " + relation.toChatColour()
						+ (player == null ? faction.getName() : faction.getName()) + ChatColor.GRAY + " has been "
						+ ChatColor.GREEN + "created" + ChatColor.GRAY + " by " + ChatColor.AQUA
						+ (sender instanceof Player ? ((Player) sender).getName() : sender.getName()) + ChatColor.GRAY
						+ '.';
				player.sendMessage(msg);
			}
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onFactionRemove(FactionRemoveEvent event) {
		Faction faction = event.getFaction();
		if (faction instanceof PlayerFaction) {
			CommandSender sender = event.getSender();
			for (Player player : Bukkit.getOnlinePlayers()) {
				Relation relation = faction.getRelation(player);
				String msg = ChatColor.GRAY + "The faction " + relation.toChatColour()
						+ (player == null ? faction.getName() : faction.getName()) + ChatColor.GRAY + " has been "
						+ ChatColor.RED + "disbanded" + ChatColor.GRAY + " by " + ChatColor.AQUA
						+ (sender instanceof Player ? ((Player) sender).getName() : sender.getName()) + ChatColor.GRAY
						+ '.';
				player.sendMessage(msg);
			}
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onFactionRename(FactionRenameEvent event) {
		Faction faction = event.getFaction();
		if (faction instanceof PlayerFaction) {
			for (Player player : Bukkit.getOnlinePlayers()) {
				Relation relation = faction.getRelation(player);
				String msg = ChatColor.GRAY + "The faction " + relation.toChatColour() + event.getOriginalName()
						+ ChatColor.GRAY + " has been " + ChatColor.AQUA + "renamed" + ChatColor.GRAY + " to "
						+ relation.toChatColour() + event.getNewName() + ChatColor.GRAY + '.';
				player.sendMessage(msg);
			}
		}
	}

	private long getLastLandChangedMeta(Player player) {
		List<MetadataValue> value = player.getMetadata(LAND_CHANGED_META_KEY);
		long millis = System.currentTimeMillis();
		long remaining = value == null || value.isEmpty() ? 0L : value.get(0).asLong() - millis;
		if (remaining <= 0L) { // update the metadata.
			player.setMetadata(LAND_CHANGED_META_KEY,
					new FixedMetadataValue(plugin, millis + LAND_CHANGE_MSG_THRESHOLD));
		}
		return remaining;
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onCaptureZoneEnter(CaptureZoneEnterEvent event) {
		Player player = event.getPlayer();
		if (getLastLandChangedMeta(player) > 0L)
			return; // delay before re-messaging.
		if (plugin.getUserManager().getUser(player.getUniqueId()).isCapzoneEntryAlerts()) {
			player.sendMessage(ChatColor.GRAY + "Now entering cap zone: " + event.getCaptureZone().getDisplayName()
					+ ChatColor.GRAY + '(' + event.getFaction().getName() + ChatColor.GRAY + ')');
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onCaptureZoneLeave(CaptureZoneLeaveEvent event) {
		Player player = event.getPlayer();
		if (getLastLandChangedMeta(player) > 0L)
			return; // delay before re-messaging.
		if (plugin.getUserManager().getUser(player.getUniqueId()).isCapzoneEntryAlerts()) {
			player.sendMessage(ChatColor.GRAY + "Now leaving cap zone: " + event.getCaptureZone().getDisplayName()
					+ ChatColor.GRAY + '(' + event.getFaction().getName() + ChatColor.GRAY + ')');
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	private void onPlayerClaimEnter(PlayerClaimEnterEvent event) {
		Faction toFaction = event.getToFaction();
		if (toFaction.isSafezone()) {
			Player player = event.getPlayer();
			player.setFoodLevel(20);
			player.setFireTicks(0);
			player.setSaturation(4.0F);
		}
		Player player = event.getPlayer();
		if (getLastLandChangedMeta(player) > 0L)
			return; // delay before re-messaging.
		Faction fromFaction = event.getFromFaction();
		player.sendMessage(ChatColor.GRAY + "Leaving " + fromFaction.getDisplayName(player) + ChatColor.GRAY
				+ ", Entering " + toFaction.getDisplayName(player));
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerLeftFaction(PlayerLeftFactionEvent event) {
		Optional<Player> optionalPlayer = event.getPlayer();
		if (optionalPlayer.isPresent()) {
			plugin.getUserManager().getUser(optionalPlayer.get().getUniqueId())
					.setLastFactionLeaveMillis(System.currentTimeMillis());
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onPlayerPreFactionJoin(PlayerJoinFactionEvent event) {
		Faction faction = event.getFaction();
		Optional<Player> optionalPlayer = event.getPlayer();
		if (faction instanceof PlayerFaction && optionalPlayer.isPresent()) {
			Player player = optionalPlayer.get();
			PlayerFaction playerFaction = (PlayerFaction) faction;
			if (!plugin.getEotwHandler().isEndOfTheWorld() && playerFaction.getRegenStatus() == RegenStatus.PAUSED) {
				event.setCancelled(true);
				player.sendMessage(ChatColor.RED + "You cannot join factions that are not regenerating DTR.");
				return;
			}
			long difference = (plugin.getUserManager().getUser(player.getUniqueId()).getLastFactionLeaveMillis()
					- System.currentTimeMillis()) + FACTION_JOIN_WAIT_MILLIS;
			if (difference > 0L && !player.hasPermission("hcf.faction.argument.staff.forcejoin")) {
				event.setCancelled(true);
				player.sendMessage(ChatColor.RED + "You cannot join factions after just leaving within "
						+ FACTION_JOIN_WAIT_WORDS + ". " + "You gotta wait another "
						+ DurationFormatUtils.formatDurationWords(difference, true, true) + '.');
			}
			UUID uuid = player.getUniqueId();
			HCF.getInstance().getUserManager().users.putIfAbsent(uuid, new FactionUser(uuid));
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onFactionLeave(PlayerLeaveFactionEvent event) {
		if (event.isForce() || event.isKick()) {
			return;
		}
		Faction faction = event.getFaction();
		if (faction instanceof PlayerFaction) {
			Optional<Player> optional = event.getPlayer();
			if (optional.isPresent()) {
				Player player = optional.get();
				if (plugin.getFactionManager().getFactionAt(player.getLocation()) == faction) {
					event.setCancelled(true);
					player.sendMessage(ChatColor.RED + "You may not leave a faction whilist in its territory!");
				}
			}
		}
	}

	private String prefix = ChatColor.DARK_GRAY + "(" + ChatColor.RED + "!" + ChatColor.DARK_GRAY + ") ";

	@SuppressWarnings("deprecation")
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		PlayerFaction playerFaction = plugin.getFactionManager().getPlayerFaction(player);
		if (playerFaction != null) {
			playerFaction.printDetails(player);
			playerFaction.broadcast(prefix + ChatColor.GRAY + "Your faction member " + ChatColor.GOLD
					+ playerFaction.getMember(player).getRole().getAstrix() + player.getName() + ChatColor.GRAY
					+ " has " + ChatColor.GREEN + "connected.");
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		PlayerFaction playerFaction = plugin.getFactionManager().getPlayerFaction(player);
		if (playerFaction != null) {
			playerFaction.broadcast(prefix + ChatColor.GRAY + "Your faction member " + ChatColor.GOLD
					+ playerFaction.getMember(player).getRole().getAstrix() + player.getName() + ChatColor.GRAY
					+ " has " + ChatColor.RED + "disconnected.");
		}
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onFactionDtrChangeEvent(FactionDtrChangeEvent event) {
		if (SettingsYML.KIT_MAP) {
			event.setCancelled(true);
		}
	}
}
