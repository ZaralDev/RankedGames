package fr.zaral.quentixx.rankedgames.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemFromConfigAPI
{
	private List<String> stringList;
	private String itemToString;
	private String[] enchantString;
	private int amount;
	private short data;

	public ItemFromConfigAPI(String lineConfig)
	{
		this.stringList = splitToStringList(lineConfig);
		this.itemToString = ((String)this.stringList.get(0));
		this.amount = Integer.parseInt((String)this.stringList.get(1));
		this.data = ((short)getData());
		this.enchantString = checkEnchantment();
	}

	private List<String> splitToStringList(String line)
	{
		List<String> stringList = new ArrayList();
		stringList.addAll(Arrays.asList(line.split(" ")));
		return stringList;
	}

	private int getData()
	{
		int data = 0;
		if (this.stringList.size() <= 2) {
			return data;
		}
		try
		{
			data = Integer.parseInt((String)this.stringList.get(2));
		}
		catch (NumberFormatException localNumberFormatException) {}
		return data;
	}

	private String[] checkEnchantment()
	{
		int i = 0;
		boolean contains = false;
		String ex = null;
		String[] enchant = new String[this.stringList.size()];
		for (String list : this.stringList)
		{
			Enchantment[] arrayOfEnchantment;
			int j = (arrayOfEnchantment = Enchantment.values()).length;
			for (int o = 0; o < j; o++)
			{
				Enchantment enchantList = arrayOfEnchantment[o];
				if (list == null) {
					break;
				}
				if (contains)
				{
					if (ex.equalsIgnoreCase(list))
					{
						contains = true;
						break;
					}
					enchant[i] = list;
					Bukkit.broadcastMessage("§6POWER : " + list);
					i++;
					contains = false;
					break;
				} 
				if (list.toUpperCase().contains(enchantList.getName()))
				{
					Bukkit.broadcastMessage("§cEnchant : " + enchantList.getName());
					ex = list;
					enchant[i] = list;
					i++;
					contains = true;
				}
			}
		}
		return enchant;
	}

	private void setEnchantments(String[] stringEnchant, ItemMeta itemMeta)
	{
		for (int i = 0; i < stringEnchant.length; i++) {
			if (i % 2 == 0) {
				/* Pair */
				int actual = i;
				int amount = 1;
				Enchantment enchant = Enchantment.getByName(stringEnchant[i]);
				if (stringEnchant[i++] != null) {
					try {
						amount = Integer.parseInt(stringEnchant[actual++]);
					} catch (NumberFormatException e) { }
				} 
				if (enchant != null)
					itemMeta.addEnchant(enchant, amount, true);

			} else {
				break;
			}
		}
	}

	@SuppressWarnings("unused")
	public ItemStack build()
	{
		ItemStack item = new ItemStack(Material.getMaterial(this.itemToString), this.amount, this.data);
		ItemMeta itemMeta = item.getItemMeta();
		if (this.enchantString != null) {
			setEnchantments(this.enchantString, itemMeta);
		}
		item.setItemMeta(itemMeta);
		if (item == null) {
			System.out.println("�cL'item '" + this.itemToString + "' est null");
		}
		return item;
	}
}
