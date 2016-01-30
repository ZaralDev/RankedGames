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
		for (int i = 1; i <= 5; i++)
			if (needed <= 9*i)
				return 9*i;
		return 9;
	}
}
