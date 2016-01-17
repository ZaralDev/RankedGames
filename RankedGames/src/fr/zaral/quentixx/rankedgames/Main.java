package fr.zaral.quentixx.rankedgames;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import fr.zaral.quentixx.rankedgames.kit.Kit;
import fr.zaral.quentixx.rankedgames.listeners.PlayerListener;
import fr.zaral.quentixx.rankedgames.listeners.SignListener;
import fr.zaral.quentixx.rankedgames.ranked.Ranked;
import fr.zaral.quentixx.rankedgames.ranked.RankedType;
import fr.zaral.quentixx.rankedgames.utils.ConfigAccessor;
import fr.zaral.quentixx.rankedgames.utils.MapUtils;


public class Main
  extends JavaPlugin
{
  public static Plugin plugin;
  public static ArrayList<Ranked> rankeds = new ArrayList();
  public static ArrayList<RankedType> types = new ArrayList();
  public static Set<String> mapList;
  public static Set<String> kitList;
  public static String[] map;
  public static HashMap<RankedType, Inventory> chooseInventory;
  public static Inventory kitsInventory;
  private static File dataFolder;
  public static File mapsDir;
  public static File backupDir;
  public static ItemStack itemStack;
  public static ConfigAccessor kitConfig;
  
  public void onEnable()
  {
    getLogger().info("Loading plugin...");
    plugin = this;
    mapList = new HashSet();
    kitList = new HashSet();
    
    dataFolder = getDataFolder();
    if (!dataFolder.exists()) {
      dataFolder.mkdir();
    }
    mapsDir = new File(dataFolder.getPath() + File.separator + "maps");
    if (!mapsDir.exists()) {
      mapsDir.mkdir();
    }
    backupDir = new File(dataFolder.getPath() + File.separator + "backup");
    if (!backupDir.exists()) {
      backupDir.mkdir();
    }
    chooseInventory = new HashMap();
    new SpamLevel();
    
    getLogger().info("Loading configuration file...");
    kitConfig = new ConfigAccessor(this, "kits.yml");
    kitConfig.saveDefaultConfig();
    
    saveDefaultConfig();
    readConfig();
    
    getLogger().info("Loading Listeners...");
    
    Bukkit.getServer().getPluginManager().registerEvents(new PlayerListener(), this);
    Bukkit.getServer().getPluginManager().registerEvents(new SignListener(), this);
    for (int i = 1; i <= 3; i++) {
      types.add(new RankedType(i));
    }
    for (RankedType type : types) {
      loadChooseInventory(type);
    }
    loadKitsInventory();
  }
  
  public static void readConfig()
  {
    plugin.getLogger().info("Initialize config...");
    mapList.addAll(plugin.getConfig().getConfigurationSection("Maps").getKeys(false));
    map = (String[])mapList.toArray(new String[mapList.size()]);
    for (int i = 0; i < map.length; i++)
    {
      List<String> list = plugin.getConfig().getStringList("Maps." + map[i] + ".typeMap");
      String[] stringList = new String[list.size()];
      stringList = (String[])list.toArray(stringList);
    }
    kitList.addAll(kitConfig.getConfig().getConfigurationSection("Kits").getKeys(false));
    String[] kit = (String[])kitList.toArray(new String[kitList.size()]);
    for (int i = 0; i < kit.length; i++)
    {
      Bukkit.broadcastMessage("Kit: " + i);
      new Kit(kit[i], kitConfig.getConfig().getString("Kits." + kit[i] + ".icon"));
    }
  }
  
  public void loadChooseInventory(RankedType type)
  {
    String[] mapList = MapUtils.getAllExistingMap();
    String[] validMap = new String[mapList.length];
    Inventory inventory = null;
    for (int i = 0; i < mapList.length; i++)
    {
      MapUtils.directoryName.put(MapUtils.getName(mapList[i]), mapList[i]);
      Bukkit.broadcastMessage(mapList[i] + " " + i);
      if (MapUtils.isValidMap(mapList[i], type)) {
        validMap[i] = mapList[i];
      }
    }
    String name = "Voter une map (" + type.getType() + "vs" + type.getType() + ")";
    inventory = getServer().createInventory(null, 9, name);
    if (mapList.length <= 18) {
      inventory.setMaxStackSize(18);
    } else if (mapList.length <= 27) {
      inventory.setMaxStackSize(27);
    } else if (mapList.length <= 36) {
      inventory.setMaxStackSize(36);
    } else if (mapList.length <= 45) {
      inventory.setMaxStackSize(45);
    } else {
      inventory.setMaxStackSize(54);
    }
    for (int i = 0; i < validMap.length; i++) {
      if (validMap[i] != null)
      {
        ItemStack item = new ItemStack(MapUtils.getInventoryItem(validMap[i]));
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(validMap[i]);
        ArrayList<String> lore = new ArrayList();
        lore.add("�EClique pour voter");
        meta.setLore(lore);
        item.setItemMeta(meta);
        inventory.addItem(new ItemStack[] { item });
        Bukkit.broadcastMessage("item: " + item.toString());
      }
    }
    chooseInventory.put(type, inventory);
  }
  
  public static Inventory getInventory(RankedType type)
  {
    if (chooseInventory.get(type) != null) {
      return (Inventory)chooseInventory.get(type);
    }
    return null;
  }
  
  public void loadKitsInventory()
  {
    kitsInventory = getServer().createInventory(null, 54, "Kits");
    for (Kit kit : Kit.getKits())
    {
      kitsInventory.addItem(new ItemStack[] { kit.getIcon() });
      Bukkit.broadcastMessage("�9kiticon" + kit.getIcon());
      Bukkit.broadcastMessage("�4kit" + kit.toString());
    }
  }
}
