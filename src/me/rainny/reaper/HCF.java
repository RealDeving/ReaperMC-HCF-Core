package me.rainny.reaper;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.Rainnny.Reaper.StaffMode;
import com.doctordark.utils.internal.com.doctordark.base.BasePlugin;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;

import lombok.Getter;
import me.rainny.reaper.Tablist.Azazel;
import me.rainny.reaper.Tablist.tab.Provider.TabProvider;
import me.rainny.reaper.args.CobbleCommand;
import me.rainny.reaper.args.CoordsCommand;
import me.rainny.reaper.args.DevCommand;
import me.rainny.reaper.args.FreezeCommand;
import me.rainny.reaper.args.GiveCrowbarCommand;
import me.rainny.reaper.args.GlowstoneMountain;
import me.rainny.reaper.args.GoppleCommand;
import me.rainny.reaper.args.HCFHelpCommand;
import me.rainny.reaper.args.HubCommand;
import me.rainny.reaper.args.LocationCommand;
import me.rainny.reaper.args.LogoutCommand;
import me.rainny.reaper.args.LookingForFactionCommand;
import me.rainny.reaper.args.MapKitCommand;
import me.rainny.reaper.args.MedicCommand;
import me.rainny.reaper.args.OresCommand;
import me.rainny.reaper.args.PanicCommand;
import me.rainny.reaper.args.PingCommand;
import me.rainny.reaper.args.PlayTimeCommand;
import me.rainny.reaper.args.RawcastCommand;
import me.rainny.reaper.args.ReaperCommand;
import me.rainny.reaper.args.ReclaimCommand;
import me.rainny.reaper.args.RefundCommand;
import me.rainny.reaper.args.RegenCommand;
import me.rainny.reaper.args.RenameCommand;
import me.rainny.reaper.args.ResetStatsCommand;
import me.rainny.reaper.args.SaveData;
import me.rainny.reaper.args.ServerTimeCommand;
import me.rainny.reaper.args.SetCommand;
import me.rainny.reaper.args.SpawnCannonCommand;
import me.rainny.reaper.args.SpawnCommand;
import me.rainny.reaper.args.StaffInfoCommand;
import me.rainny.reaper.args.TellLocationCommand;
import me.rainny.reaper.args.ToggleBroadcastsCommand;
import me.rainny.reaper.args.ToggleCapzoneEntryCommand;
import me.rainny.reaper.args.ToggleLightningCommand;
import me.rainny.reaper.args.ToggleSidebarCommand;
import me.rainny.reaper.args.recordCommand;
import me.rainny.reaper.armors.PvpClassManager;
import me.rainny.reaper.armors.bard.EffectRestorer;
import me.rainny.reaper.bosses.commands.ReaperSpawn;
import me.rainny.reaper.bosses.listeners.BossDeathListener;
import me.rainny.reaper.conquestevent.ConquestExecutor;
import me.rainny.reaper.deathban.Deathban;
import me.rainny.reaper.deathban.DeathbanListener;
import me.rainny.reaper.deathban.DeathbanManager;
import me.rainny.reaper.deathban.FlatFileDeathbanManager;
import me.rainny.reaper.deathban.StaffReviveCommand;
import me.rainny.reaper.deathban.lives.LivesExecutor;
import me.rainny.reaper.deathban.lives.PvpTimerCommand;
import me.rainny.reaper.economy.EconomyCommand;
import me.rainny.reaper.economy.EconomyManager;
import me.rainny.reaper.economy.FlatFileEconomyManager;
import me.rainny.reaper.economy.PayCommand;
import me.rainny.reaper.economy.ShopSignListener;
import me.rainny.reaper.endoftheworld.EotwCommand;
import me.rainny.reaper.endoftheworld.EotwHandler;
import me.rainny.reaper.endoftheworld.EotwListener;
import me.rainny.reaper.eventutils.CaptureZone;
import me.rainny.reaper.eventutils.EventExecutor;
import me.rainny.reaper.eventutils.EventScheduler;
import me.rainny.reaper.factionutils.FactionExecutor;
import me.rainny.reaper.factionutils.FactionManager;
import me.rainny.reaper.factionutils.FactionMember;
import me.rainny.reaper.factionutils.FactionUser;
import me.rainny.reaper.factionutils.FlatFileFactionManager;
import me.rainny.reaper.factionutils.args.FactionClaimChunkArgument;
import me.rainny.reaper.factionutils.args.FactionManagerArgument;
import me.rainny.reaper.factionutils.claim.Claim;
import me.rainny.reaper.factionutils.claim.ClaimHandler;
import me.rainny.reaper.factionutils.claim.ClaimWandListener;
import me.rainny.reaper.factionutils.claim.Subclaim;
import me.rainny.reaper.factionutils.claim.SubclaimWandListener;
import me.rainny.reaper.factionutils.type.CapturableFaction;
import me.rainny.reaper.factionutils.type.ClaimableFaction;
import me.rainny.reaper.factionutils.type.ConquestFaction;
import me.rainny.reaper.factionutils.type.EndPortalFaction;
import me.rainny.reaper.factionutils.type.Faction;
import me.rainny.reaper.factionutils.type.GlowstoneMountainFaction;
import me.rainny.reaper.factionutils.type.KothFaction;
import me.rainny.reaper.factionutils.type.PlayerFaction;
import me.rainny.reaper.factionutils.type.RoadFaction;
import me.rainny.reaper.factionutils.type.SpawnFaction;
import me.rainny.reaper.kothevent.KothExecutor;
import me.rainny.reaper.listener.BookDeenchantListener;
import me.rainny.reaper.listener.BorderListener;
import me.rainny.reaper.listener.BottledExpListener;
import me.rainny.reaper.listener.ChatListener;
import me.rainny.reaper.listener.Cooldowns;
import me.rainny.reaper.listener.CoreListener;
import me.rainny.reaper.listener.CreativeClickListener;
import me.rainny.reaper.listener.CrowbarListener;
import me.rainny.reaper.listener.DeathListener;
import me.rainny.reaper.listener.DeathMessageListener;
import me.rainny.reaper.listener.ElevatorListener;
import me.rainny.reaper.listener.EndListener;
import me.rainny.reaper.listener.EntityLimitListener;
import me.rainny.reaper.listener.ExpMultiplierListener;
import me.rainny.reaper.listener.FactionListener;
import me.rainny.reaper.listener.FoundDiamondsListener;
import me.rainny.reaper.listener.GlowstoneListener;
import me.rainny.reaper.listener.HitDetectionListener;
import me.rainny.reaper.listener.ItemRemoverListener;
import me.rainny.reaper.listener.MineListener;
import me.rainny.reaper.listener.PlayTimeManager;
import me.rainny.reaper.listener.PortalListener;
import me.rainny.reaper.listener.PotListener;
import me.rainny.reaper.listener.ProtectionListener;
import me.rainny.reaper.listener.SignSubclaimListener;
import me.rainny.reaper.listener.SkullListener;
import me.rainny.reaper.listener.UnRepairableListener;
import me.rainny.reaper.listener.UserManager;
import me.rainny.reaper.listener.WorldListener;
import me.rainny.reaper.listener.combatloggers.CombatLogListener;
import me.rainny.reaper.listener.combatloggers.CustomEntityRegistration;
import me.rainny.reaper.listener.fixes.ArmorFixListener;
import me.rainny.reaper.listener.fixes.BeaconStrengthFixListener;
import me.rainny.reaper.listener.fixes.BlockHitFixListener;
import me.rainny.reaper.listener.fixes.BlockJumpGlitchFixListener;
import me.rainny.reaper.listener.fixes.BoatGlitchFixListener;
import me.rainny.reaper.listener.fixes.ColonCommandFixListener;
import me.rainny.reaper.listener.fixes.CreatureSpawn;
import me.rainny.reaper.listener.fixes.EnchantLimitListener;
import me.rainny.reaper.listener.fixes.EndFix;
import me.rainny.reaper.listener.fixes.EnderChestRemovalListener;
import me.rainny.reaper.listener.fixes.HungerFixListener;
import me.rainny.reaper.listener.fixes.InfinityArrowFixListener;
import me.rainny.reaper.listener.fixes.PearlGlitchListener;
import me.rainny.reaper.listener.fixes.PluginViewListener;
import me.rainny.reaper.listener.fixes.PortalTrapFixListener;
import me.rainny.reaper.listener.fixes.PotionLimitListener;
import me.rainny.reaper.listener.fixes.StrengthPatch;
import me.rainny.reaper.listener.fixes.VoidGlitchFixListener;
import me.rainny.reaper.listener.render.ProtocolLibHook;
import me.rainny.reaper.listener.render.VisualiseHandler;
import me.rainny.reaper.listener.render.WallBorderListener;
import me.rainny.reaper.pm.MsgCMD;
import me.rainny.reaper.pm.ReplyCMD;
import me.rainny.reaper.pm.SoundsCMD;
import me.rainny.reaper.pm.TogglePMCMD;
import me.rainny.reaper.scoreboard.NameManager;
import me.rainny.reaper.scoreboard.ScoreboardProvider;
import me.rainny.reaper.scoreboard.providers.ScoreboardManager;
import me.rainny.reaper.sql.SQLUtil;
import me.rainny.reaper.startoftheworld.SotwCommand;
import me.rainny.reaper.startoftheworld.SotwListener;
import me.rainny.reaper.startoftheworld.SotwTimer;
import me.rainny.reaper.systems.Tags.Tags;
import me.rainny.reaper.systems.crates.KeyListener;
import me.rainny.reaper.systems.crates.KeyManager;
import me.rainny.reaper.systems.signs.DeathSignListener;
import me.rainny.reaper.systems.signs.EventSignListener;
import me.rainny.reaper.systems.signs.KitmapSignListener;
import me.rainny.reaper.timer.TimerExecutor;
import me.rainny.reaper.timer.TimerManager;
import me.rainny.reaper.ymls.KitsYML;
import me.rainny.reaper.ymls.LangYML;
import me.rainny.reaper.ymls.ReclaimYML;
import me.rainny.reaper.ymls.SettingsYML;

public class HCF extends JavaPlugin implements Listener {

	public static HCF plugin;

	private PlayTimeManager playTimeManager;

	private Random random = new Random();

	private ClaimHandler claimHandler;

	private DeathbanManager deathbanManager;

	private EconomyManager economyManager;

	private EffectRestorer effectRestorer;

	private EotwHandler eotwHandler;

	public EventScheduler eventScheduler;

	private FactionManager factionManager;

	private FoundDiamondsListener foundDiamondsListener;

	private PvpClassManager pvpClassManager;

	private SotwTimer sotwTimer;

	private TimerManager timerManager;

	private KeyManager keyManager;

	private FactionUser factionUser;

	private UserManager userManager;

	private VisualiseHandler visualiseHandler;

	public HashMap<UUID, String> ranks;

	public SQLUtil sql;
	public static boolean sqlWorks;

	@Getter
	private NameManager nameManager;

	private WorldEditPlugin worldEdit;
	private StaffMode staff;

	public static String PREFIX;
	public static ChatColor M;
	public static ChatColor M2;
	public static ChatColor O;
	public static ChatColor E;
	public static ChatColor S;
	public Azazel azazel;

	private CombatLogListener combatLogListener;

	public CombatLogListener getCombatLogListener() {
		return this.combatLogListener;
	}

	@Override
	public void onEnable() {
		Bukkit.getServer().getPluginManager().registerEvents((Listener) this, (Plugin) this);
		Cooldowns.createCooldown("medic_cooldown");

		HCF.plugin = this;
		BasePlugin.getPlugin().init(this);
		this.ranks = new HashMap<UUID, String>();

		ProtocolLibHook.hook(this);

		Plugin wep = getServer().getPluginManager().getPlugin("WorldEdit");
		this.worldEdit = wep instanceof WorldEditPlugin && wep.isEnabled() ? (WorldEditPlugin) wep : null;
		CustomEntityRegistration.registerCustomEntities();

		SettingsYML.init(this);
		sqlWorks = false;
		(sql = new SQLUtil()).setup();
		ReclaimYML.init(this);
		LangYML.init(this);
		KitsYML.init();
		this.effectRestorer = new EffectRestorer(this);
		this.registerConfiguration();
		this.registerCommands();
		this.registerManagers();
		this.registerListeners();
		new ScoreboardManager(this, new ScoreboardProvider(this), SettingsYML.SCOREBOARD_TITLE);
		if (sqlWorks) {
			new Tags(this);
		}
		new BukkitRunnable() {
			@Override
			public void run() {
				getServer().broadcast(ChatColor.GREEN.toString() + ChatColor.BOLD + "Saving!" + "\n" + ChatColor.GREEN
						+ "Saved all faction and player data to the database." + "\n" + ChatColor.GRAY + "Current TPS: "
						+ ChatColor.GRAY, "hcf.seesaves");
				saveData();
			}
		}.runTaskTimerAsynchronously(plugin, TimeUnit.MINUTES.toMillis(5L), TimeUnit.MINUTES.toMillis(5L));
		new EndFix(this);
		azazel = new Azazel(this, new TabProvider());
		staff = StaffMode.instance;
	}

	public StaffMode getStaff() {
		return this.staff;
	}

	@SuppressWarnings("static-access")
	private void saveData() {
		this.combatLogListener.removeCombatLoggers();
		this.deathbanManager.saveDeathbanData();
		this.economyManager.saveEconomyData();
		this.factionManager.saveFactionData();
		this.keyManager.saveKeyData();
		this.timerManager.saveTimerData();
		this.userManager.saveUserData();
	}

	@Override
	public void onDisable() {
		this.pvpClassManager.onDisable();
		this.factionManager.saveFactionData();
		this.deathbanManager.saveDeathbanData();
		this.economyManager.saveEconomyData();
		this.factionManager.saveFactionData();
		this.keyManager.saveKeyData();
		this.timerManager.saveTimerData();
		this.userManager.saveUserData();
		this.playTimeManager.savePlaytimeData();
		this.saveData();

		HCF.plugin = null; // always initialise last
	}

	private void registerConfiguration() {
		ConfigurationSerialization.registerClass(CaptureZone.class);
		ConfigurationSerialization.registerClass(Deathban.class);
		ConfigurationSerialization.registerClass(Claim.class);
		ConfigurationSerialization.registerClass(Subclaim.class);
		ConfigurationSerialization.registerClass(Deathban.class);
		ConfigurationSerialization.registerClass(FactionUser.class);
		ConfigurationSerialization.registerClass(ClaimableFaction.class);
		ConfigurationSerialization.registerClass(ConquestFaction.class);
		ConfigurationSerialization.registerClass(CapturableFaction.class);
		ConfigurationSerialization.registerClass(KothFaction.class);
		ConfigurationSerialization.registerClass(EndPortalFaction.class);
		ConfigurationSerialization.registerClass(Faction.class);
		ConfigurationSerialization.registerClass(FactionMember.class);
		ConfigurationSerialization.registerClass(PlayerFaction.class);
		ConfigurationSerialization.registerClass(RoadFaction.class);
		ConfigurationSerialization.registerClass(SpawnFaction.class);
		ConfigurationSerialization.registerClass(GlowstoneMountainFaction.class);
		ConfigurationSerialization.registerClass(RoadFaction.NorthRoadFaction.class);
		ConfigurationSerialization.registerClass(RoadFaction.EastRoadFaction.class);
		ConfigurationSerialization.registerClass(RoadFaction.SouthRoadFaction.class);
		ConfigurationSerialization.registerClass(RoadFaction.WestRoadFaction.class);
	}

	private void registerListeners() {
		PluginManager manager = this.getServer().getPluginManager();
		this.playTimeManager = new PlayTimeManager(this);
		manager.registerEvents(this.playTimeManager, this);
		manager.registerEvents(this.combatLogListener = new CombatLogListener(this), this);
		manager.registerEvents(new HitDetectionListener(), this);
		manager.registerEvents(new FactionManagerArgument(this), this);
		manager.registerEvents(new BlockHitFixListener(), this);
		manager.registerEvents(new BlockJumpGlitchFixListener(), this);
		manager.registerEvents(new BoatGlitchFixListener(), this);
		manager.registerEvents(new BookDeenchantListener(), this);
		manager.registerEvents(new FactionClaimChunkArgument(this), this);
		manager.registerEvents(new BorderListener(), this);
		manager.registerEvents(new CreatureSpawn(), this);
		manager.registerEvents(new CobbleCommand(), this);
		manager.registerEvents(new BottledExpListener(), this);
		manager.registerEvents(new PortalTrapFixListener(this), this);
		manager.registerEvents(new ChatListener(this), this);
		manager.registerEvents(new ClaimWandListener(this), this);
		manager.registerEvents(new CoreListener(this), this);
		manager.registerEvents(new ElevatorListener(), this);
		manager.registerEvents(new CrowbarListener(this), this);
		manager.registerEvents(new DeathListener(this), this);
		manager.registerEvents(new DeathMessageListener(this), this);
		manager.registerEvents(new DeathSignListener(this), this);
		if (SettingsYML.KIT_MAP == false) {
			manager.registerEvents(new DeathbanListener(this), this);
		} else {
			manager.registerEvents(new KitmapSignListener(), this);
		}
		manager.registerEvents(new EnchantLimitListener(), this);
		manager.registerEvents(new EnderChestRemovalListener(), this);
		manager.registerEvents(new EntityLimitListener(), this);
		manager.registerEvents(new EndListener(), this);
		manager.registerEvents(new EotwListener(this), this);
		manager.registerEvents(new EventSignListener(), this);
		manager.registerEvents(new ExpMultiplierListener(), this);
		manager.registerEvents(new FactionListener(this), this);
		manager.registerEvents(this.foundDiamondsListener = new FoundDiamondsListener(this), this);
		manager.registerEvents(new InfinityArrowFixListener(), this);
		manager.registerEvents(new KeyListener(this), this);
		manager.registerEvents(new PearlGlitchListener(this), this);
		manager.registerEvents(new PortalListener(this), this);
		manager.registerEvents(new PotionLimitListener(), this);
		manager.registerEvents(new ProtectionListener(this), this);
		manager.registerEvents(new SubclaimWandListener(this), this);
		manager.registerEvents(new SignSubclaimListener(this), this);
		manager.registerEvents(new ShopSignListener(this), this);
		manager.registerEvents(new SkullListener(), this);
		manager.registerEvents(new SotwListener(this), this);
		manager.registerEvents(new BeaconStrengthFixListener(), this);
		manager.registerEvents(new VoidGlitchFixListener(), this);
		manager.registerEvents(new UnRepairableListener(), this);
		manager.registerEvents(new WallBorderListener(this), this);
		manager.registerEvents(new WorldListener(this), this);
		manager.registerEvents(new PluginViewListener(), this);
		manager.registerEvents(new MineListener(this), this);
		manager.registerEvents(new ItemRemoverListener(this), this);
		manager.registerEvents(new PotListener(), this);
		manager.registerEvents(new GlowstoneListener(this), this);
		manager.registerEvents(new StrengthPatch(), this);
		manager.registerEvents(new CreativeClickListener(), this);
		manager.registerEvents(new ArmorFixListener(), this);
		manager.registerEvents(new HungerFixListener(), this);
		manager.registerEvents(new ColonCommandFixListener(), this);
		manager.registerEvents(new BossDeathListener(), this);
	}

	private void registerCommands() {
		getCommand("spawn").setExecutor(new SpawnCommand(this));
		getCommand("cobble").setExecutor(new CobbleCommand());
		getCommand("hub").setExecutor(new HubCommand(this));
		getCommand("titanium").setExecutor(new MedicCommand(this));
		getCommand("crowgive").setExecutor(new GiveCrowbarCommand());
		getCommand("staffinfo").setExecutor(new StaffInfoCommand());
		getCommand("hcfhelp").setExecutor(new HCFHelpCommand());
		getCommand("coords").setExecutor(new CoordsCommand());
		getCommand("conquest").setExecutor(new ConquestExecutor(this));
		getCommand("economy").setExecutor(new EconomyCommand(this));
		getCommand("refund").setExecutor(new RefundCommand());
		getCommand("eotw").setExecutor(new EotwCommand(this));
		getCommand("event").setExecutor(new EventExecutor(this));
		getCommand("hcfhelp").setExecutor(new HCFHelpCommand());
		getCommand("faction").setExecutor(new FactionExecutor(this));
		getCommand("gopple").setExecutor(new GoppleCommand(this));
		getCommand("koth").setExecutor(new KothExecutor(this));
		getCommand("lives").setExecutor(new LivesExecutor(this));
		getCommand("location").setExecutor(new LocationCommand(this));
		getCommand("ping").setExecutor(new PingCommand());
		getCommand("rawcast").setExecutor(new RawcastCommand());
		getCommand("freeze").setExecutor(new FreezeCommand(this));
		getCommand("logout").setExecutor(new LogoutCommand(this));
		getCommand("mapkit").setExecutor(new MapKitCommand(this));
		getCommand("pay").setExecutor(new PayCommand(this));
		getCommand("pvptimer").setExecutor(new PvpTimerCommand(this));
		getCommand("regen").setExecutor(new RegenCommand(this));
		getCommand("set").setExecutor(new SetCommand());
		getCommand("servertime").setExecutor(new ServerTimeCommand());
		getCommand("sotw").setExecutor(new SotwCommand(this));
		getCommand("spawncannon").setExecutor(new SpawnCannonCommand(this));
		getCommand("staffrevive").setExecutor(new StaffReviveCommand(this));
		getCommand("timer").setExecutor(new TimerExecutor(this));
		getCommand("togglebroadcasts").setExecutor(new ToggleBroadcastsCommand());
		getCommand("togglecapzoneentry").setExecutor(new ToggleCapzoneEntryCommand(this));
		getCommand("togglelightning").setExecutor(new ToggleLightningCommand(this));
		getCommand("togglesidebar").setExecutor(new ToggleSidebarCommand(this));
		getCommand("rename").setExecutor(new RenameCommand(this));
		getCommand("ores").setExecutor(new OresCommand(this));
		getCommand("playtime").setExecutor(new PlayTimeCommand());
		getCommand("glowstonemountain").setExecutor(new GlowstoneMountain(this));
		getCommand("lff").setExecutor(new LookingForFactionCommand());
		getCommand("lookingforfaction").setExecutor(new LookingForFactionCommand());
		getCommand("tl").setExecutor(new TellLocationCommand());
		getCommand("tc").setExecutor(new TellLocationCommand());
		getCommand("telllocation").setExecutor(new TellLocationCommand());
		getCommand("teamcoordinates").setExecutor(new TellLocationCommand());
		getCommand("fc").setExecutor(new TellLocationCommand());
		getCommand("recording").setExecutor(new recordCommand());
		getCommand("rec").setExecutor(new recordCommand());// no clue
		getCommand("reclaim").setExecutor(new ReclaimCommand());
		getCommand("dev").setExecutor(new DevCommand());
		getCommand("savedata").setExecutor(new SaveData());
		getCommand("reaper").setExecutor(new ReaperCommand());
		getCommand("resetstats").setExecutor(new ResetStatsCommand());
		getCommand("msg").setExecutor(new MsgCMD());
		getCommand("reply").setExecutor(new ReplyCMD());
		getCommand("sounds").setExecutor(new SoundsCMD());
		getCommand("togglepm").setExecutor(new TogglePMCMD());
		getCommand("panic").setExecutor(new PanicCommand());
		getCommand("grimreaper").setExecutor(new ReaperSpawn());
		Map<String, Map<String, Object>> map = getDescription().getCommands();
		for (Map.Entry<String, Map<String, Object>> entry : map.entrySet()) {
			PluginCommand command = getCommand(entry.getKey());
			command.setPermission("command." + entry.getKey());
			command.setPermissionMessage(ChatColor.RED + "You do not have permissions to execute this command.");
		}
	}

	@SuppressWarnings("unused")
	private void registerCooldowns() {
		Cooldowns.createCooldown("medic_cooldown");
	}

	private void registerManagers() {
		this.claimHandler = new ClaimHandler(this);
		this.deathbanManager = new FlatFileDeathbanManager(this);
		this.economyManager = new FlatFileEconomyManager(this);
		this.eotwHandler = new EotwHandler(this);
		this.eventScheduler = new EventScheduler(this);
		this.factionManager = new FlatFileFactionManager(this);
		this.keyManager = new KeyManager(this);
		this.pvpClassManager = new PvpClassManager(this);
		this.sotwTimer = new SotwTimer();
		this.timerManager = new TimerManager(this); // needs to be registered before ScoreboardHandler
		this.userManager = new UserManager(this);
		this.visualiseHandler = new VisualiseHandler();
		this.nameManager = new NameManager();
	}
	
	/*
	this core was vary good to us thought the time but its time buy u can use it i would not even try to meme this
	its already a meme <3
	from UTF: "this core sucks peen"
	
	*/

	public PlayTimeManager getPlayTimeManager() {
		return this.playTimeManager;
	}

	public static HCF getPlugin() {
		return HCF.plugin;
	}

	public static HCF getInstance() {
		return HCF.plugin;
	}

	public Random getRandom() {
		return this.random;
	}

	public ClaimHandler getClaimHandler() {
		return this.claimHandler;
	}

	public DeathbanManager getDeathbanManager() {
		return this.deathbanManager;
	}

	public EconomyManager getEconomyManager() {
		return this.economyManager;
	}

	public EffectRestorer getEffectRestorer() {
		return this.effectRestorer;
	}

	public EotwHandler getEotwHandler() {
		return this.eotwHandler;
	}

	public static SotwTimer getSotwRunnable() {
		return HCF.getSotwRunnable();
	}

	public FactionUser getFactionUser() {
		return this.factionUser;
	}

	public EventScheduler getEventScheduler() {
		return this.eventScheduler;
	}

	public FactionManager getFactionManager() {
		return this.factionManager;
	}

	public FoundDiamondsListener getFoundDiamondsListener() {
		return this.foundDiamondsListener;
	}

	public KeyManager getKeyManager() {
		return this.keyManager;
	}

	public PvpClassManager getPvpClassManager() {
		return this.pvpClassManager;
	}

	public SotwTimer getSotwTimer() {
		return this.sotwTimer;
	}

	public TimerManager getTimerManager() {
		return this.timerManager;
	}

	public UserManager getUserManager() {
		return this.userManager;
	}

	public VisualiseHandler getVisualiseHandler() {
		return this.visualiseHandler;
	}

	public WorldEditPlugin getWorldEdit() {
		return this.worldEdit;
	}

	static {
		HCF.PREFIX = "§9Reaper §8» §7";
		HCF.M = ChatColor.DARK_RED;
		HCF.M2 = ChatColor.RED;
		HCF.O = ChatColor.GRAY;
		HCF.E = ChatColor.RED;
		HCF.S = ChatColor.GREEN;
	}

}
