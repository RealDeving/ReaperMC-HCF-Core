package me.rainny.reaper.listener.combatloggers;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_7_R4.CraftServer;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_7_R4.event.CraftEventFactory;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.google.common.base.Function;

import net.minecraft.server.v1_7_R4.DamageSource;
import net.minecraft.server.v1_7_R4.Entity;
import net.minecraft.server.v1_7_R4.EntityAgeable;
import net.minecraft.server.v1_7_R4.EntityHuman;
import net.minecraft.server.v1_7_R4.EntityPlayer;
import net.minecraft.server.v1_7_R4.EntityVillager;
import net.minecraft.server.v1_7_R4.PlayerInteractManager;
import net.minecraft.server.v1_7_R4.WorldServer;
import net.minecraft.util.com.mojang.authlib.GameProfile;

public class LoggerEntity extends EntityVillager {
	private static final Function<Double, Double> DAMAGE_FUNC;
	private UUID uuid;

	static {
		DAMAGE_FUNC = (f1 -> 0.0);
	}

	private static PlayerNmsResult getResult(World world, UUID playerUUID) {
		OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerUUID); // Use bukkit to load this
		if (offlinePlayer.hasPlayedBefore()) {
			WorldServer worldServer = ((CraftWorld) world).getHandle();
			EntityPlayer entityPlayer = new EntityPlayer(((CraftServer) Bukkit.getServer()).getServer(), worldServer,
					new GameProfile(playerUUID, offlinePlayer.getName()), new PlayerInteractManager(worldServer));
			CraftPlayer player = entityPlayer.getBukkitEntity();
			if (player != null) {
				player.loadData();
				return new PlayerNmsResult(player, entityPlayer);
			}
		}
		return null;
	}

	public LoggerEntity(Player player, org.bukkit.World world) {
		this(player, ((CraftWorld) world).getHandle());
	}

	private LoggerEntity(Player player, WorldServer world) {
		super(world);
		this.lastDamager = ((CraftPlayer) player).getHandle().lastDamager;
		Location location = player.getLocation();
		final double x = location.getX();
		final double y = location.getY();
		final double z = location.getZ();
		this.uuid = player.getUniqueId();
		getBukkitEntity().setMaxHealth(((Damageable) player).getMaxHealth());
		getBukkitEntity().setHealth(((Damageable) player).getHealth());
		setInvisible(false);
		
		setCustomName(player.getName());
		setCustomNameVisible(true);
		setPositionRotation(x, y, z, location.getYaw(), location.getPitch());
		fallDistance = player.getFallDistance();
		world.addEntity(this, SpawnReason.CUSTOM);
		retrack();
	}

	public EntityAgeable createChild(EntityAgeable ea) {
		return null;
	}

	public UUID getUUID() {
		return this.uuid;
	}

	public void move(final double d0, final double d1, final double d2) {
		super.move(0, d1, 0);
	}

	public void b(final int i) {
	}

	public void dropDeathLoot(boolean flag, int i) {
	}

	public Entity findTarget() {
		return null;
	}

	public boolean damageEntity(final DamageSource damageSource, final float amount) {
		PlayerNmsResult nmsResult = getResult(this.world.getWorld(), this.uuid);
		if (nmsResult == null) {
			return true;
		}
		EntityPlayer entityPlayer = nmsResult.ep;
		if (entityPlayer != null) {
			entityPlayer.setPosition(this.locX, this.locY, this.locZ);
			EntityDamageEvent event = CraftEventFactory.handleLivingEntityDamageEvent(entityPlayer, damageSource,
					(double) amount, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, LoggerEntity.DAMAGE_FUNC, LoggerEntity.DAMAGE_FUNC,
					LoggerEntity.DAMAGE_FUNC, LoggerEntity.DAMAGE_FUNC, LoggerEntity.DAMAGE_FUNC,
					LoggerEntity.DAMAGE_FUNC);
			if (event.isCancelled()) {
				return false;
			}
		}
		return super.damageEntity(damageSource, amount);
	}

	public boolean a(EntityHuman entityHuman) {
		return false;
	}

	public void h() {
		super.h();
	}

	public void collide(Entity entity) {
	}

	public void die(DamageSource damageSource) {
		final PlayerNmsResult playerNmsResult = getResult((World) this.world.getWorld(), this.uuid);
		if (playerNmsResult != null) {
			Player player = playerNmsResult.pl;
			PlayerInventory inventory = player.getInventory();
			boolean keepInventory = this.world.getGameRules().getBoolean("keepInventory");
			List<ItemStack> drops = new ArrayList<>();
			if (!keepInventory) {
				for (ItemStack loggerDeathEvent : inventory.getContents()) {
					if (loggerDeathEvent != null && loggerDeathEvent.getType() != Material.AIR) {
						drops.add(loggerDeathEvent);
					}
				}
				for (ItemStack loggerDeathEvent : inventory.getArmorContents()) {
					if (loggerDeathEvent != null && loggerDeathEvent.getType() != Material.AIR) {
						drops.add(loggerDeathEvent);
					}
				}
			}
			String var13 = this.combatTracker.b().c();
			EntityPlayer var14 = playerNmsResult.ep;
			var14.combatTracker = this.combatTracker;
			if (Bukkit.getPlayer(var14.getName()) != null) {
				Bukkit.getPlayer(var14.getUniqueID()).kickPlayer(var13);
			}
			PlayerDeathEvent var15 = CraftEventFactory.callPlayerDeathEvent(var14, drops, var13, keepInventory);
			var13 = var15.getDeathMessage();
			if (var13 != null && !var13.isEmpty()) {
				Bukkit.broadcastMessage(var13);
			}
			super.die(damageSource);
			LoggerDeathEvent var16 = new LoggerDeathEvent(this, this.killer);
			Bukkit.getPluginManager().callEvent(var16);
			if (!var15.getKeepInventory()) {
				inventory.clear();
				inventory.setArmorContents(new ItemStack[inventory.getArmorContents().length]);
			}
			var14.setLocation(this.locX, this.locY, this.locZ, this.yaw, this.pitch);
			var14.setHealth(0.0f);
			player.saveData();
			((WorldServer) world).getTracker().untrackEntity(this);
			if (!playerNmsResult.pl.isOnline()) {
				((WorldServer) playerNmsResult.ep.world).getTracker().untrackEntity(playerNmsResult.ep);
			}
		}
	}

	public CraftLivingEntity getBukkitEntity() {
		return (CraftLivingEntity) super.getBukkitEntity();
	}

	private static class PlayerNmsResult {
		public final Player pl;
		public final EntityPlayer ep;

		public PlayerNmsResult(Player pl, EntityPlayer ep) {
			this.pl = pl;
			this.ep = ep;
		}

	}

}