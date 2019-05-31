package me.rainny.reaper.listener;

import com.google.common.base.Preconditions;

import me.rainny.reaper.HCF;
import net.minecraft.server.v1_7_R4.EntityLiving;

import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DeathMessageListener implements Listener {
    private final HCF plugin;

    public DeathMessageListener(final HCF plugin) {
        this.plugin = plugin;
    }

    public static String replaceLast(final String text, final String regex, final String replacement) {
        return text.replaceFirst("(?s)" + regex + "(?!.*?" + regex + ')', replacement);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(final PlayerDeathEvent event) {
        final String message = event.getDeathMessage();
        if(message == null || message.isEmpty()) {
            return;
        }
        event.setDeathMessage(getDeathMessage(message,  event.getEntity(),  this.getKiller(event)));
    }

    private CraftEntity getKiller(final PlayerDeathEvent event) {
        final EntityLiving lastAttacker = ((CraftPlayer) event.getEntity()).getHandle().aX();
        return (lastAttacker == null) ? null : lastAttacker.getBukkitEntity();
    }

    private String getDeathMessage(String input, final Entity entity, final Entity killer) {
        input = input.replaceFirst("\\[", "");
        input = replaceLast(input, "]", "");
        if(entity != null) {
            input = input.replaceFirst("(?i)" + this.getEntityName(entity), ChatColor.RED + this.getDisplayName(entity) + ChatColor.RED);
        }
        if(killer != null && (entity == null || !killer.equals(entity))) {
            input = input.replaceFirst("(?i)" + this.getEntityName(killer), ChatColor.RED + this.getDisplayName(killer) + ChatColor.RED);
        }
        return input;
    }

    private String getEntityName(final Entity entity) {
        Preconditions.checkNotNull((Object) entity,  "Entity cannot be null");
        return (entity instanceof Player) ? ((Player) entity).getName() : ((CraftEntity) entity).getHandle().getName();
    }

    private String getDisplayName(final Entity entity) {
        Preconditions.checkNotNull((Object) entity,  "Entity cannot be null");
        if(entity instanceof Player) {
            final Player player = (Player) entity;
            return player.getName() + ChatColor.DARK_RED + '[' + ChatColor.DARK_RED + this.plugin.getUserManager().getUser(player.getUniqueId()).getKills() + ChatColor.DARK_RED + ']';
        }
        return WordUtils.capitalizeFully(entity.getType().name().replace('_', ' '));
    }
}
