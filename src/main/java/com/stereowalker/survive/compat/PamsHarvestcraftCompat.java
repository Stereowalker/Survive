package com.stereowalker.survive.compat;

import java.util.List;

import com.google.common.collect.Lists;

public class PamsHarvestcraftCompat {
	public static List<String> normalPamHCDrinks(){
		return Lists.newArrayList(
				reg(foodCore("NoodleSoup"), 3, 2.0F),
				reg(foodCore("Yogurt"), 3, 2.0F),
				reg(foodCore("ChocolateYogurt"), 3, 2.0F),
				reg(foodCore("AppleYogurt"), 3, 2.0F),
				reg(foodCore("SweetberryYogurt"), 3, 2.0F),
				reg(foodCore("PumpkinYogurt"), 3, 2.0F),
				reg(foodCore("CaramelYogurt"), 3, 2.0F),
				reg(foodCore("IceCream"), 3, 2.0F),
				reg(foodCore("ChocolateIceCream"), 3, 2.0F),
				reg(foodCore("CaramelIceCream"), 3, 2.0F),
				reg(foodCore("Hotchocolate"), 3, 2.0F),
				reg(foodCore("ChocolatePudding"), 3, 2.0F),
				reg(foodCore("AppleJuice"), 3, 2.0F),
				reg(foodCore("AppleSmoothie"), 3, 2.0F),
				reg(foodCore("AppleJelly"), 3, 2.0F),
				reg(foodCore("Applesauce"), 3, 2.0F),
				reg(foodCore("MelonJuice"), 3, 2.0F),
				reg(foodCore("MelonSmoothie"), 3, 2.0F),
				reg(foodCore("MelonJelly"), 3, 2.0F),
				reg(foodCore("SweetberryJuice"), 3, 2.0F),
				reg(foodCore("SweetberrySmoothie"), 3, 2.0F),
				reg(foodCore("SweetberryJelly"), 3, 2.0F),
				reg(foodCore("PotatoSoup"), 3, 2.0F),
				reg(foodCore("CarrotSoup"), 3, 2.0F),
				reg(foodCore("PumpkinSoup"), 3, 2.0F),
				reg(foodCore("Stock"), 3, 2.0F),
				reg(foodCore("Fruitpunch"), 3, 2.0F),
				reg(foodCore("Stew"), 3, 2.0F),
				reg(foodCore("P8juice"), 3, 2.0F)
				);
	}
	
	public static List<String> uncleanPamHCDrinks(){
		return Lists.newArrayList("");
	}
	
	public static List<String> heatedPamHCDrinks(){
		return Lists.newArrayList(
				reg(crops("HotTea"), 3, 2.0F),
				reg(crops("HotCoffee"), 3, 2.0F)
				);
	}
	
	public static List<String> stimulatingPamHCDrinks(){
		return Lists.newArrayList(
				reg(crops("HotCoffee"), 3, 2.0F)
				);
	}
	
	public static List<String> chilledPamHCDrinks(){
		return Lists.newArrayList("");
	}
	
	public static String reg(String id, int thirst, float hydration) {
		return id+","+thirst+","+hydration;
	}
	
	public static String foodCore(String string) {
		return "pamhc2foodcore:"+string.toLowerCase()+"item";
	}
	
	public static String crops(String string) {
		return "pamhc2crops:"+string.toLowerCase()+"item";
	}
}
