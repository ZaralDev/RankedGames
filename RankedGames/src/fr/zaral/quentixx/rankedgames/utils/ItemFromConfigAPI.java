package fr.zaral.quentixx.rankedgames.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemFromConfigAPI {
	
	private List<String> stringList;
	private String itemToString;
	private String[] enchantString;
	private int amount;
	private short data;

	public ItemFromConfigAPI(String lineConfig) {
		stringList = splitToStringList(lineConfig);
		itemToString = ((String) stringList.get(0));
		amount = Integer.parseInt((String) stringList.get(1));
		data = ((short) getData());
		enchantString = checkEnchantment();
	}

	private List<String> splitToStringList(String line) {
		List<String> stringList = new ArrayList<>();
		stringList.addAll(Arrays.asList(line.split(" ")));
		return stringList;
	}

	private int getData() {
		int data = 0;
		if (stringList.size() <= 2)
			return data;
		try {
			data = Integer.parseInt((String) stringList.get(2));
		} catch (NumberFormatException ex) {}
		return data;
	}

	private String[] checkEnchantment() {
		int i = 0;
		boolean contains = false;
		String ex = null;
		String[] enchant = new String[stringList.size()];
		for (String list : stringList) {
			Enchantment[] arrayOfEnchantment;
			int j = (arrayOfEnchantment = Enchantment.values()).length;
			for (int o = 0; o < j; o++) {
				Enchantment enchantList = arrayOfEnchantment[o];
				if (list == null)
					break;
				if (contains) {
					if (ex.equalsIgnoreCase(list)) {
						contains = true;
						break;
					}
					enchant[i] = list;
					// Bukkit.broadcastMessage("§6POWER : " + list);
					i++;
					contains = false;
					break;
				} 
				if (list.toUpperCase().contains(enchantList.getName())) {
					// Bukkit.broadcastMessage("§cEnchant : " + enchantList.getName());
					ex = list;
					enchant[i] = list;
					i++;
					contains = true;
				}
			}
		}
		return enchant;
	}

	private void setEnchantments(String[] stringEnchant, ItemMeta itemMeta) {
		for (int i = 0; i < stringEnchant.length; i++) {
			if (i % 2 == 0) {
				/* Pair */
				int actual = i;
				int amount = 1;
				Enchantment enchant = Enchantment.getByName(stringEnchant[i]);
				if (stringEnchant[i++] != null) {
					try {
						amount = Integer.parseInt(stringEnchant[actual++]);
					} catch (NumberFormatException ex) {}
				} 
				if (enchant != null)
					itemMeta.addEnchant(enchant, amount, true);

			} else
				break;
		}
	}

	@SuppressWarnings("unused")
	public ItemStack build() {
		ItemStack item = new ItemStack(Material.getMaterial(itemToString), amount, data);
		ItemMeta itemMeta = item.getItemMeta();
		if (enchantString != null)
			setEnchantments(enchantString, itemMeta);
		item.setItemMeta(itemMeta);
		if (item == null)
			System.out.println("L'item " + itemToString + " est egal a nul");
		return item;
	}
}
