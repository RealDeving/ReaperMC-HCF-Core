package me.rainny.reaper.timer;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import com.doctordark.utils.Config;

import lombok.Data;
import lombok.Getter;
import me.rainny.reaper.HCF;
import me.rainny.reaper.eventutils.EventTimer;
import me.rainny.reaper.timer.type.ArcherTimer;
import me.rainny.reaper.timer.type.ClassLoad;
import me.rainny.reaper.timer.type.CombatTimer;
import me.rainny.reaper.timer.type.EnderPearlTimer;
import me.rainny.reaper.timer.type.FishermenTimer;
import me.rainny.reaper.timer.type.GappleTimer;
import me.rainny.reaper.timer.type.InvincibilityTimer;
import me.rainny.reaper.timer.type.LogoutTimer;
import me.rainny.reaper.timer.type.StuckTimer;
import me.rainny.reaper.timer.type.TeleportTimer;

@Data
public class TimerManager implements Listener {

	public final CombatTimer combatTimer;

	private final LogoutTimer logoutTimer;

	private final EnderPearlTimer enderPearlTimer;

	private final EventTimer eventTimer;

	public final GappleTimer gappleTimer;

	private final InvincibilityTimer invincibilityTimer;

	@Getter
	public final ArcherTimer archerTimer;

	@Getter
	private final ClassLoad pvpClassWarmupTimer;

	private final StuckTimer stuckTimer;

	private final FishermenTimer fishermenTimer;

	public final TeleportTimer teleportTimer;

	private final Set<Timer> timers = new LinkedHashSet<>();

	private final JavaPlugin plugin;
	private Config config;

	public TimerManager(HCF plugin) {
		(this.plugin = plugin).getServer().getPluginManager().registerEvents(this, plugin);
		this.registerTimer(this.enderPearlTimer = new EnderPearlTimer(plugin));
		this.registerTimer(this.logoutTimer = new LogoutTimer());
		this.registerTimer(this.gappleTimer = new GappleTimer(plugin));
		this.registerTimer(this.stuckTimer = new StuckTimer());
		this.registerTimer(this.invincibilityTimer = new InvincibilityTimer(plugin));
		this.registerTimer(this.combatTimer = new CombatTimer(plugin));
		this.registerTimer(this.teleportTimer = new TeleportTimer(plugin));
		this.registerTimer(this.eventTimer = new EventTimer(plugin));
		this.registerTimer(this.pvpClassWarmupTimer = new ClassLoad(plugin));
		this.registerTimer(this.archerTimer = new ArcherTimer(plugin));
		registerTimer(this.fishermenTimer = new FishermenTimer(plugin));
		this.reloadTimerData();
	}

	public void registerTimer(Timer timer) {
		this.timers.add(timer);
		if (timer instanceof Listener) {
			this.plugin.getServer().getPluginManager().registerEvents((Listener) timer, this.plugin);
		}
	}

	public void unregisterTimer(Timer timer) {
		this.timers.remove(timer);
	}

	/**
	 * Reloads the {@link Timer} data from storage.
	 */
	public void reloadTimerData() {
		this.config = new Config((HCF) plugin, "timers");
		for (Timer timer : this.timers) {
			timer.load(this.config);
		}
	}

	/**
	 * Saves the {@link Timer} data to storage.
	 */
	public void saveTimerData() {
		for (Timer timer : this.timers) {
			timer.onDisable(this.config);
		}

		this.config.save();
	}

	public CombatTimer getCombatTimer() {
		return this.combatTimer;
	}

	public GappleTimer getGappleTimer() {
		return this.gappleTimer;
	}

	public FishermenTimer getFishermenTimer() {
		return this.fishermenTimer;
	}

	public LogoutTimer getLogoutTimer() {
		return this.logoutTimer;
	}

	public EnderPearlTimer getEnderPearlTimer() {
		return this.enderPearlTimer;
	}

	public EventTimer getEventTimer() {
		return this.eventTimer;
	}

	public StuckTimer getStuckTimer() {
		return this.stuckTimer;
	}

	public InvincibilityTimer getInvincibilityTimer() {
		return this.invincibilityTimer;
	}

	public TeleportTimer getTeleportTimer() {
		return this.teleportTimer;
	}

	public Collection<Timer> getTimers() {
		return this.timers;
	}

}
