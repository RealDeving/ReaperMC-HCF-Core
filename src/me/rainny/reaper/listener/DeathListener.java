package me.rainny.reaper.listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.Statistic;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import com.doctordark.utils.JavaUtils;

import me.rainny.reaper.HCF;
import me.rainny.reaper.economy.EconomyManager;
import me.rainny.reaper.factionutils.FactionUser;
import me.rainny.reaper.factionutils.struct.Role;
import me.rainny.reaper.factionutils.type.Faction;
import me.rainny.reaper.factionutils.type.PlayerFaction;
import me.rainny.reaper.listener.combatloggers.LoggerDeathEvent;
import me.rainny.reaper.ymls.SettingsYML;
import net.minecraft.server.v1_7_R4.EntityHuman;
import net.minecraft.server.v1_7_R4.EntityLightning;
import net.minecraft.server.v1_7_R4.PacketPlayOutSpawnEntityWeather;
import net.minecraft.server.v1_7_R4.WorldServer;

public class DeathListener implements Listener {

	private final HCF plugin;
	private final ArrayList<UUID> loggersDead;

	public DeathListener(HCF plugin) {
		this.plugin = plugin;
		this.loggersDead = new ArrayList<UUID>();
	}

	public static HashMap<UUID, ItemStack[]> PlayerInventoryContents;
	public static HashMap<UUID, ItemStack[]> PlayerArmorContents;
	private String prefix = ChatColor.DARK_GRAY + "(" + ChatColor.RED + "!" + ChatColor.DARK_GRAY + ") ";

	static {
		PlayerInventoryContents = new HashMap<>();
		PlayerArmorContents = new HashMap<>();
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void onPlayerDeathKillIncrement(PlayerDeathEvent event) {
		Player killer = event.getEntity().getKiller();
		if (killer != null) {
			FactionUser user = plugin.getUserManager().getUser(killer.getUniqueId());
			user.setKills(killer.getStatistic(Statistic.PLAYER_KILLS));
		}
	}

	private static final long BASE_REGEN_DELAY = TimeUnit.MINUTES.toMillis(40L);
	// *Kitmap* private static final long BASE_REGEN_DELAY =
	// TimeUnit.MINUTES.toMillis(0L);

	@SuppressWarnings("deprecation")
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();
		PlayerFaction playerFaction = plugin.getFactionManager().getPlayerFaction(player);

		if (playerFaction != null) {
			Faction factionAt = plugin.getFactionManager().getFactionAt(player.getLocation());
			double dtrLoss = (factionAt.getDtrLossMultiplier());
			playerFaction.setDeathsUntilRaidable(playerFaction.getDeathsUntilRaidable() - dtrLoss);

			Role role = playerFaction.getMember(player.getUniqueId()).getRole();
			if (!SettingsYML.KIT_MAP) {
				playerFaction.setRemainingRegenerationTime(
						BASE_REGEN_DELAY + (playerFaction.getOnlinePlayers().size() * TimeUnit.MINUTES.toMillis(2L)));
			}
			playerFaction.broadcast(prefix + ChatColor.GRAY + "Member Death: " + SettingsYML.TEAMMATE_COLOUR
					+ role.getAstrix() + player.getName() + ChatColor.GRAY + ". " + "DTR: (" + ChatColor.GOLD
					+ JavaUtils.format(playerFaction.getDeathsUntilRaidable(), 2) + '/'
					+ JavaUtils.format(playerFaction.getMaximumDeathsUntilRaidable(), 2) + ChatColor.GRAY + ").");
		}
		Integer balance = 0;
		if (plugin.getEconomyManager().getBalance(player.getUniqueId()) > 0) {
			balance = plugin.getEconomyManager().getBalance(player.getUniqueId()) % 10;
			if (player.getKiller() instanceof Player) {
				plugin.getEconomyManager().subtractBalance(player.getUniqueId(), balance);
				if (balance != 0) {
					plugin.getEconomyManager().addBalance(player.getKiller().getUniqueId(), balance);
					player.getKiller()
							.sendMessage(ChatColor.GRAY + "You earned " + ChatColor.GREEN + ChatColor.BOLD
									+ EconomyManager.ECONOMY_SYMBOL + balance + ChatColor.GRAY + " for killing "
									+ ChatColor.WHITE + player.getName() + "");
				}
				return;

			}
		}

		if (Bukkit.spigot().getTPS()[0] > 15) { // Prevent unnecessary lag during prime times.
			Location location = player.getLocation();
			WorldServer worldServer = ((CraftWorld) location.getWorld()).getHandle();

			EntityLightning entityLightning = new EntityLightning(worldServer, location.getX(), location.getY(),
					location.getZ(), false);
			PacketPlayOutSpawnEntityWeather packet = new PacketPlayOutSpawnEntityWeather(entityLightning);
			for (Player target : Bukkit.getOnlinePlayers()) {
				if (plugin.getUserManager().getUser(target.getUniqueId()).isShowLightning()) {
					((CraftPlayer) target).getHandle().playerConnection.sendPacket(packet);
					target.playSound(target.getLocation(), Sound.AMBIENCE_THUNDER, 1.0F, 1.0F);
				}
			}
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerLoggerDeath(LoggerDeathEvent event) {
		EntityHuman humanKiller = event.getKiller();
		if (humanKiller == null) {
			return;
		}
		Player killer = Bukkit.getPlayer(humanKiller.getProfile().getId());
		if (killer == null) {
			return;
		}
		FactionUser user = plugin.getUserManager().getUser(killer.getUniqueId());
		user.setKills(user.getKills() + 1);
		killer.incrementStatistic(Statistic.PLAYER_KILLS);
		this.loggersDead.add(event.getLoggerEntity().getUUID());
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerJoinAfterLoggerKilled(PlayerJoinEvent e) {
		if (this.loggersDead.contains(e.getPlayer().getUniqueId())) {
			this.loggersDead.remove(e.getPlayer().getUniqueId());
			e.getPlayer().incrementStatistic(Statistic.DEATHS);
		}
	}
}
