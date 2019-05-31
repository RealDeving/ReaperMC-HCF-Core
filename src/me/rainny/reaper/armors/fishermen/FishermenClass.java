package me.rainny.reaper.armors.fishermen;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.doctordark.utils.other.Cooldown;

import me.rainny.reaper.HCF;
import me.rainny.reaper.armors.PvpClass;
import me.rainny.reaper.eventutils.CaptureZone;

public class FishermenClass extends PvpClass implements Listener {

	public HCF plugin;
	public FishermenClass instance;
	
	public static ArrayList<Player> drawing = new ArrayList<>();
	
	public FishermenClass(HCF plugin) {
		super("Fishermen", TimeUnit.SECONDS.toMillis(5));

		this.plugin = plugin;
        this.passiveEffects.add(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 2));
        passiveEffects.add(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 1));
        this.passiveEffects.add(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2));
		
	}
	
	
	@EventHandler
    public void onEntityRod(final PlayerFishEvent e) {
        final Player p = e.getPlayer();
        if (this.plugin.getPvpClassManager().getEquippedClass(p) == this && e.getState() == PlayerFishEvent.State.CAUGHT_ENTITY && e.getCaught() instanceof Player) {
            if(CaptureZone.getCappingPlayer() != p) {
            	if(!Cooldown.isInCooldown(p.getUniqueId(), "fisherman")) {
                	final Player caught = (Player)e.getCaught();
                    caught.setVelocity(new Vector(0.0, 1.25, 0.0));
                    Cooldown c = new Cooldown(p.getUniqueId(), "fisherman", 30);
                    c.start();
                } else {
                	p.sendMessage("§cYou cannot use your fishermen rod yet.");
                }
            } else {
            	p.sendMessage("§cYou cannot use your fishermen rod on a capping player.");
            }
        	
        }
    }
	

	@Override
	public boolean isApplicableFor(Player player) {
		PlayerInventory playerInventory = player.getInventory();

        ItemStack helmet = playerInventory.getHelmet();
        if (helmet == null || helmet.getType() != Material.GOLD_HELMET) {
            return false;
        }

        ItemStack chestplate = playerInventory.getChestplate();
        if (chestplate == null || chestplate.getType() != Material.LEATHER_CHESTPLATE) {
            return false;
        }

        ItemStack leggings = playerInventory.getLeggings();
        if (leggings == null || leggings.getType() != Material.GOLD_LEGGINGS) {
            return false;
        }

        ItemStack boots = playerInventory.getBoots();
        return !(boots == null || boots.getType() != Material.LEATHER_BOOTS);
	}
	

}
