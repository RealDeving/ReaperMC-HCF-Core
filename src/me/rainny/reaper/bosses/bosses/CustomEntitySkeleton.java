package me.rainny.reaper.bosses.bosses;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftLivingEntity;
import org.bukkit.entity.Skeleton;
import org.bukkit.event.entity.CreatureSpawnEvent;

import net.minecraft.server.v1_7_R4.EntitySkeleton;
import net.minecraft.server.v1_7_R4.GenericAttributes;
import net.minecraft.server.v1_7_R4.Item;
import net.minecraft.server.v1_7_R4.Items;
import net.minecraft.server.v1_7_R4.World;


public class CustomEntitySkeleton extends EntitySkeleton {

    public CustomEntitySkeleton(World world) {
        super(world);
    }

    protected void initAttributes() {
        this.getAttributeInstance(GenericAttributes.e).setValue(10.0D);
        this.getAttributeInstance(GenericAttributes.maxHealth).setValue(150.0D);
        this.setCustomName(ChatColor.RED  + ChatColor.BOLD.toString() + "Grim Reaper");
        this.setCustomNameVisible(true);

        //If you want it to be fast like the flash are something idk
      //  this.getAttributeInstance(GenericAttributes.d).setValue(2.0D);
    }

    protected Item getLoot() {
        return Items.APPLE;
    }

    public static Skeleton spawn(Location location) {
        World world = (World) ((CraftWorld) location.getWorld()).getHandle();
        final CustomEntitySkeleton customEntity = new CustomEntitySkeleton(world);

        customEntity.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        ((CraftLivingEntity) customEntity.getBukkitEntity()).setRemoveWhenFarAway(false);
        world.addEntity(customEntity, CreatureSpawnEvent.SpawnReason.CUSTOM);
        return (Skeleton) customEntity.getBukkitEntity();
    }
}

