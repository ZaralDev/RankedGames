package fr.zaral.quentixx.rankedgames.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import fr.zaral.quentixx.rankedgames.RankedGames;
import fr.zaral.quentixx.rankedgames.ranked.RankedType;

public class MapUtils {
	
	public static HashMap<String, String> directoryName = new HashMap<>();
  
	public static String[] getAllExistingMap() {
		return RankedGames.map;
	}
  
	public static String getName(String mapName) {
		return RankedGames.plugin.getConfig().getString("Maps." + mapName + ".name");
	}
  
	public static String getDirectoryParent(String name) {
		// Bukkit.broadcastMessage("�9 " + name);
		// Bukkit.broadcastMessage("�9" + Main.plugin.getConfig().getString(new StringBuilder("Maps.").append(name).append(".directoryName").toString()));
		return (String)directoryName.get(name);
	}
  
	public static String getDirectory(String mapName) {
		// Bukkit.broadcastMessage("�9 " + mapName);
		// Bukkit.broadcastMessage("�6" + Main.plugin.getConfig().getString(new StringBuilder("Maps.").append(mapName).append(".directoryName").toString()));
		return RankedGames.plugin.getConfig().getString("Maps." + mapName + ".directoryName");
	}
  
	public static Material getInventoryItem(String mapName) {
		String itemStackToString = RankedGames.plugin.getConfig().getString("Maps." + mapName + ".inventoryItem");
		// Bukkit.broadcastMessage(itemStackToString + " 2 " + mapName);
		Material material = Material.AIR;
		Material[] arrayOfMaterial;
		int j = (arrayOfMaterial = Material.values()).length;
		for (int i = 0; i < j; i++) {
			Material m = arrayOfMaterial[i];
			if (m.toString().equalsIgnoreCase(itemStackToString))
				material = m;
		}
		return material;
	}
  
	public static boolean isValidMap(String mapName, RankedType type) {
		@SuppressWarnings("unchecked")
		ArrayList<String> list = (ArrayList<String>) RankedGames.plugin.getConfig().getList("Maps." + mapName + ".typeMap");
		String[] stringList = new String[list.size()];
		stringList = (String[])list.toArray(stringList);
		for (int i = 0; i < stringList.length; i++) {
			int convert = Integer.parseInt(stringList[i]);
			RankedType rankedType = RankedType.get(convert);
			if (rankedType.getType() == type.getType())
				return true;
		}
		return false;
	}
  
	public static File getBackup() {
		return new File(RankedGames.plugin.getConfig().getString("backup"));
	}
  
	public static File copyFile(String srcDir, String backupDir) {
		File src = new File(srcDir);
		File backup = new File(backupDir);
		if (!src.exists()) {
			System.out.println("§CLa source: " + src.getPath() + " n'existe pas.");
			return null;
		}
		try {
			FileUtils.copyDirectory(src, backup);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return backup;
	}
  
	public static void saveDataTo(FileConfiguration data, File file) {
		try {
			data.save(file);
		} catch (IOException localIOException) {}
	}
}
