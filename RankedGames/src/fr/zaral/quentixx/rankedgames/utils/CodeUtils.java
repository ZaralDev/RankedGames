package fr.zaral.quentixx.rankedgames.utils;

import java.util.Random;

import org.bukkit.Bukkit;

public class CodeUtils {
	
	public static int randomInt(int min, int max) {
		Random rand = new Random();
		int randomNum = rand.nextInt(max - min + 1) + min;
		return randomNum;
	}
  
	public static void runConsoleCommand(String command) {
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
	}
	
	public static int getIdealInvSize(int needed) {
		int i = 9;
		int count = 1;
		while (i != 54) {
			if (needed <= i)
				return i;
			count++;
			i *= count;
		}
		return 9;
	}
}
