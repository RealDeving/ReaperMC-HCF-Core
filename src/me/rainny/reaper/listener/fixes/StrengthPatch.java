package me.rainny.reaper.listener.fixes;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class StrengthPatch implements Listener {

	@EventHandler
	public void onAttack(EntityDamageByEntityEvent e) {
		if(!(e.getDamager() instanceof Player) || !(e.getEntity() instanceof Player)) {
			return;
		}
		Player attacker = (Player) e.getDamager();
		
		for(PotionEffect effect : attacker.getActivePotionEffects()) {
			if(effect.getType().equals(PotionEffectType.INCREASE_DAMAGE)) {
				e.setDamage(e.getDamage() - (3 * effect.getAmplifier()));
			}
		}
		
	}

}
