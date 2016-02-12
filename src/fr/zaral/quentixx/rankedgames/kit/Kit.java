package fr.zaral.quentixx.rankedgames.kit;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import fr.zaral.quentixx.rankedgames.RankedGames;
import fr.zaral.quentixx.rankedgames.utils.ItemFromConfigAPI;
import fr.zaral.quentixx.rankedgames.utils.PlayerUtils;

public class Kit {
	
	private static ArrayList<Kit> kits = new ArrayList<>();
	
	public static Kit get(String name) {
		for (Kit kit : kits)
			if (kit.getName().equalsIgnoreCase(name))
				return kit;
		return null;
	}
  
	public static ArrayList<Kit> getKits() {
		return kits;
	}
  
	public static Kit get(ItemStack icon) {
		for (Kit kit : kits)
			if (kit.getIcon().equals(icon))
				return kit;
		return null;
	}
  
	private String name;
	private String iconString;
	private ItemStack icon;
	private String path;
	public ItemStack helmet;
	public ItemStack chestplate;
	public ItemStack leggings;
	public ItemStack boots;
	private FileConfiguration config = RankedGames.kitConfig.getConfig();
	public ItemStack[] items;
  
	public Kit(String name, String icon) {
		this.name = name;
		this.iconString = icon;
		this.path = ("Kits." + name);
		this.icon = convertToItemStack(this.iconString);
		this.helmet = getHelmet();
		this.chestplate = getChesplate();
		this.leggings = getLeggings();
		this.boots = getBoots();
		this.items = getItems();
		kits.add(this);
	}
  
	private ItemStack convertToItemStack(String string) {
		ItemStack item = null;
		try {
			item = new ItemStack(Material.getMaterial(string));
			ItemMeta itemMeta = item.getItemMeta();
			itemMeta.setDisplayName(this.name);
			item.setItemMeta(itemMeta);
		} catch (Exception ex) {
			Bukkit.broadcastMessage("�4Erreur lors de la creation de l'item.");
		}
		return item;
	}
  
	private ItemStack getHelmet() {
		String helmetString = this.config.getString(this.path + ".helmet");
		ItemFromConfigAPI item = new ItemFromConfigAPI(helmetString);
		ItemStack itemStack = item.build();
		Bukkit.broadcastMessage("§bHEMET: " + itemStack.toString());
		return itemStack;
	}
  
	private ItemStack getChesplate() {
		String helmetString = this.config.getString(this.path + ".chestplate");
		ItemFromConfigAPI item = new ItemFromConfigAPI(helmetString);
		ItemStack itemStack = item.build();
		return itemStack;
	}
  
	private ItemStack getLeggings() {
		String helmetString = this.config.getString(this.path + ".leggings");
		ItemFromConfigAPI item = new ItemFromConfigAPI(helmetString);
		ItemStack itemStack = item.build();
		return itemStack;
	}
  
	private ItemStack getBoots() {
		String helmetString = this.config.getString(this.path + ".boots");
		ItemFromConfigAPI item = new ItemFromConfigAPI(helmetString);
		ItemStack itemStack = item.build();
		return itemStack;
	}
  
	private ItemStack[] getItems() {
		List<String> list = this.config.getStringList(this.path + ".items");
		String[] stringList = new String[list.size()];
		stringList = (String[])list.toArray(stringList);
		ItemStack[] itemStacks = new ItemStack[stringList.length];
		for (int i = 0; i < stringList.length; i++) {
			String itemString = stringList[i];
			ItemFromConfigAPI item = new ItemFromConfigAPI(itemString);
			itemStacks[i] = item.build();
		}
		return itemStacks;
	}
  
	public String getName() {
		return this.name;
	}
  
	public ItemStack getIcon() {
		return this.icon;
	}
  
	public void setStuff(Player player) {
		PlayerUtils.goNaked(player);
		PlayerInventory inventory = player.getInventory();
		inventory.setHelmet(this.helmet);
		inventory.setChestplate(this.chestplate);
		inventory.setLeggings(this.leggings);
		inventory.setBoots(this.boots);
		ItemStack[] arrayOfItemStack;
		int j = (arrayOfItemStack = this.items).length;
		for (int i = 0; i < j; i++) {
			ItemStack item = arrayOfItemStack[i];
			inventory.addItem(new ItemStack[]{item});
		}
		player.updateInventory();
	}
}
