package me.rainny.reaper.ymls;

import java.util.Set;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import com.doctordark.utils.BetterConfig;

import me.rainny.reaper.HCF;
import me.rainny.reaper.systems.signs.KitmapSignListener;
import me.rainny.reaper.systems.signs.KitmapSignListener.Kit;

public class KitsYML {

	public static FileConfiguration config;

	public static void init() {
		loadKits();
	}
	
	private static void loadKits() {
		BetterConfig betterconfig = new BetterConfig(HCF.getPlugin(), "kits.yml", null);
		betterconfig.saveDefaultConfig();
		config = betterconfig.getConfiguration();
		Set<String> keys = config.getKeys(false);

		for (String key : keys) {
			ConfigurationSection section = config.getConfigurationSection(key);
			Kit kit = new Kit();
			kit.setHelmet(loadItem(section.getConfigurationSection("helmet")));
			kit.setChest(loadItem(section.getConfigurationSection("chest")));
			kit.setLegs(loadItem(section.getConfigurationSection("legs")));
			kit.setBoots(loadItem(section.getConfigurationSection("boots")));
			ConfigurationSection inv = section.getConfigurationSection("inv");
			if (inv != null) {
				for (String index : inv.getKeys(false)) {
					int idx = 0;
					try {
						idx = Integer.parseInt(index);
					} catch (Exception e) {
						System.out.println("Invalid number " + index);
						continue;
					}
					ConfigurationSection item = inv.getConfigurationSection(index);
					kit.addItem(idx, loadItem(item));
				}
			}
			KitmapSignListener.kits.put(key, kit);
		}
	}

	private static ItemStack loadItem(ConfigurationSection section) {
		if (section == null) {
			return null;
		}
		String type = section.getString("type");
		Material mat = Material.getMaterial((String) type);
		if (mat == null) {
			System.out.println("Invalid material " + type);
			return null;
		}
		int amount = Math.max(1, section.getInt("amount", 1));
		short durability = (short) section.getInt("durability", 0);
		ItemStack result = new ItemStack(mat, amount, durability);
		ConfigurationSection enchantments = section.getConfigurationSection("enchantments");
		if (enchantments != null) {
			for (String ench : enchantments.getKeys(false)) {
				int level = enchantments.getInt(ench);
				Enchantment enchantment = Enchantment.getByName((String) ench);
				if (enchantment == null) {
					System.out.println("Invalid enchantment " + ench);
					continue;
				}
				result.addUnsafeEnchantment(enchantment, level);
			}
		}
		return result;
	}
}
