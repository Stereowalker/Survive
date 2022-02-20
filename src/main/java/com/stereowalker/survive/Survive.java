package com.stereowalker.survive;

import java.util.ArrayList;
import java.util.List;

import com.stereowalker.survive.commands.SCommands;
import com.stereowalker.survive.compat.OriginsCompat;
import com.stereowalker.survive.config.Config;
import com.stereowalker.survive.config.ServerConfig;
import com.stereowalker.survive.events.SurviveEvents;
import com.stereowalker.survive.fluid.SFluids;
import com.stereowalker.survive.network.client.CArmorStaminaPacket;
import com.stereowalker.survive.network.client.CEnergyTaxPacket;
import com.stereowalker.survive.network.client.CInteractWithWaterPacket;
import com.stereowalker.survive.network.client.CThirstMovementPacket;
import com.stereowalker.survive.network.server.SArmorDataTransferPacket;
import com.stereowalker.survive.network.server.SDrinkSoundPacket;
import com.stereowalker.survive.network.server.SSurvivalStatsPacket;
import com.stereowalker.survive.potion.BrewingRecipes;
import com.stereowalker.survive.resource.ArmorDataManager;
import com.stereowalker.survive.resource.BiomeTemperatureDataManager;
import com.stereowalker.survive.resource.BlockTemperatureDataManager;
import com.stereowalker.survive.resource.EntityTemperatureDataManager;
import com.stereowalker.survive.resource.ItemConsummableDataManager;
import com.stereowalker.survive.resource.PotionDrinkDataManager;
import com.stereowalker.survive.spell.SSpells;
import com.stereowalker.survive.stat.SStats;
import com.stereowalker.survive.util.data.ArmorData;
import com.stereowalker.survive.util.data.BiomeTemperatureData;
import com.stereowalker.survive.util.data.BlockTemperatureData;
import com.stereowalker.survive.util.data.EntityTemperatureData;
import com.stereowalker.survive.util.data.FoodData;
import com.stereowalker.survive.util.data.PotionData;
import com.stereowalker.survive.world.CGameRules;
import com.stereowalker.unionlib.client.gui.screen.ConfigScreen;
import com.stereowalker.unionlib.config.ConfigBuilder;
import com.stereowalker.unionlib.mod.UnionMod;
import com.stereowalker.unionlib.network.PacketRegistry;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.minecraftforge.registries.ForgeRegistries;

@Mod(value = "survive")
public class Survive extends UnionMod {

	public static final float DEFAULT_TEMP = 37.0F;
	public static final String MOD_ID = "survive";
	public static boolean isPrimalWinterLoaded;
	public static final ItemConsummableDataManager consummableReloader = new ItemConsummableDataManager();
	public static final PotionDrinkDataManager potionReloader = new PotionDrinkDataManager();
	public static final ArmorDataManager armorReloader = new ArmorDataManager();
	public static final BlockTemperatureDataManager blockReloader = new BlockTemperatureDataManager();
	public static final BiomeTemperatureDataManager biomeReloader = new BiomeTemperatureDataManager();
	public static final EntityTemperatureDataManager entityReloader = new EntityTemperatureDataManager();
	private static Survive instance;
	
	public static boolean isCombatLoaded() {
		return ModList.get().isLoaded("combat");
	}
	public static boolean isOriginsLoaded() {
		return ModList.get().isLoaded("origins");
	}
	
	public Survive() 
	{
		super("survive", new ResourceLocation(MOD_ID, "textures/icon.png"), LoadType.BOTH);
		instance = this;
		ConfigBuilder.registerConfig(Config.class);
		ConfigBuilder.registerConfig(ServerConfig.class);
		final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		modEventBus.addListener(this::setup);
		modEventBus.addListener(this::clientRegistries);
//		MinecraftForge.EVENT_BUS.register(this);
		MinecraftForge.EVENT_BUS.addListener(this::registerCommands);
		isPrimalWinterLoaded = ModList.get().isLoaded("primalwinter");
		if (isCombatLoaded()) {
			SSpells.registerAll(modEventBus);
			SStats.registerAll(modEventBus);
		}
		if (isOriginsLoaded()) {
			OriginsCompat.initOriginsPatcher();
		}
	}
	
	@Override
	public void registerMessages(SimpleChannel channel) {
		int netID = -1;
		PacketRegistry.registerMessage(channel, netID++, CArmorStaminaPacket.class, (packetBuffer) -> {return new CArmorStaminaPacket(packetBuffer);});
		channel.registerMessage(netID++, SSurvivalStatsPacket.class, SSurvivalStatsPacket::encode, SSurvivalStatsPacket::decode, SSurvivalStatsPacket::handle);
		channel.registerMessage(netID++, CThirstMovementPacket.class, CThirstMovementPacket::encode, CThirstMovementPacket::decode, CThirstMovementPacket::handle);
		channel.registerMessage(netID++, CInteractWithWaterPacket.class, CInteractWithWaterPacket::encode, CInteractWithWaterPacket::decode, CInteractWithWaterPacket::handle);
		channel.registerMessage(netID++, SDrinkSoundPacket.class, SDrinkSoundPacket::encode, SDrinkSoundPacket::decode, SDrinkSoundPacket::handle);
		channel.registerMessage(netID++, CEnergyTaxPacket.class, CEnergyTaxPacket::encode, CEnergyTaxPacket::decode, CEnergyTaxPacket::handle);
		channel.registerMessage(netID++, SArmorDataTransferPacket.class, SArmorDataTransferPacket::encode, SArmorDataTransferPacket::decode, SArmorDataTransferPacket::handle);
	}
	
	//TODO: FInd Somewhere to put all these
	public static void registerDrinkDataForItem(ResourceLocation location, FoodData drinkData) {
		DataMaps.Server.consummableItem.put(location, drinkData);
	}
	public static void registerDrinkDataForPotion(ResourceLocation location, PotionData consummableData) {
		DataMaps.Server.potionDrink.put(location, consummableData);
	}
	public static void registerArmorTemperatures(ResourceLocation location, ArmorData armorData) {
		DataMaps.Server.armor.put(location, armorData);
	}
	public static void registerBlockTemperatures(ResourceLocation location, BlockTemperatureData drinkData) {
		DataMaps.Server.blockTemperature.put(location, drinkData);
	}
	public static void registerEntityTemperatures(ResourceLocation location, EntityTemperatureData drinkData) {
		DataMaps.Server.entityTemperature.put(location, drinkData);
	}
	public static void registerBiomeTemperatures(ResourceLocation location, BiomeTemperatureData biomeData) {
		DataMaps.Server.biomeTemperature.put(location, biomeData);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public Screen getConfigScreen(Minecraft mc, Screen previousScreen) {
		return new ConfigScreen(previousScreen, Config.class, new TranslationTextComponent("gui.survive.config.title"));
	}
	
	public void registerCommands(RegisterCommandsEvent event) {
		SCommands.registerCommands(event.getDispatcher());
	}

	public void debug(Object message) {
		if (Config.debugMode)getLogger().debug(message);
	}

	private void setup(final FMLCommonSetupEvent event)
	{
		BrewingRecipes.addBrewingRecipes();
		CGameRules.init();
		SurviveEvents.registerHeatMap();
		
		for(Item item : ForgeRegistries.ITEMS) {
			if (item.isFood())
				DataMaps.Server.defaultFood.put(item.getRegistryName(), item.getFood());
		}
	}

	public void clientRegistries(final FMLClientSetupEvent event)
	{
		RenderType frendertype = RenderType.getTranslucent();
		RenderTypeLookup.setRenderLayer(SFluids.PURIFIED_WATER, frendertype);
		RenderTypeLookup.setRenderLayer(SFluids.FLOWING_PURIFIED_WATER, frendertype);
	}

	public static List<String> defaultDimensionMods() {
		List<String> dims = new ArrayList<String>();
		dims.add("minecraft:overworld,0.0");
		dims.add("minecraft:the_nether,0.0");
		dims.add("minecraft:the_end,0.0");
		return dims;
	}
	
	public static List<String> defaultArmorMods(){
		List<String> armorTemps = new ArrayList<String>();
		//Ars Nouveau
		armorTemps.add("ars_nouveau:novice_robes,3.0");
		armorTemps.add("ars_nouveau:novice_leggings,3.0");
		armorTemps.add("ars_nouveau:novice_hood,3.0");
		armorTemps.add("ars_nouveau:novice_boots,3.0");
		armorTemps.add("ars_nouveau:apprentice_robes,3.0");
		armorTemps.add("ars_nouveau:apprentice_leggings,3.0");
		armorTemps.add("ars_nouveau:apprentice_hood,3.0");
		armorTemps.add("ars_nouveau:apprentice_boots,3.0");
		armorTemps.add("ars_nouveau:archmage_robes,3.0");
		armorTemps.add("ars_nouveau:archmage_leggings,3.0");
		armorTemps.add("ars_nouveau:archmage_hood,3.0");
		armorTemps.add("ars_nouveau:archmage_boots,3.0");
		return armorTemps;
	}
	
	public static List<String> defaultWaterContainers() {
		List<String> water = new ArrayList<String>();
//		water.add("minecraft:sweet_berries,-2");
		water.add("farmersdelight:milk_bottle,4");
//		water.add("minecraft:pumpkin_pie,-4");
		return water;
	}
	
	public static List<String> defaultThirstContainers() {
		List<String> thirst = new ArrayList<String>();
//		thirst.add("minecraft:honey_bottle,1");
//		thirst.add("minecraft:pufferfish,-8");
//		thirst.add("minecraft:rotten_flesh,-4");
//		thirst.add("minecraft:poisonous_potato,-8");
		thirst.add("minecraft:spider_eye,-8");
		thirst.add("minecraft:bread,-6");
		thirst.add("minecraft:cookie,-1");
		thirst.add("farmersdelight:raw_pasta,-4");
		thirst.add("farmersdelight:pie_crust,-2");
		thirst.add("farmersdelight:slice_of_cake,-2");
		thirst.add("farmersdelight:slice_of_apple_pie,-2");
		thirst.add("farmersdelight:slice_of_sweet_berry_cheesecake,-2");
		thirst.add("farmersdelight:slice_of_chocolate_pie,-2");
		thirst.add("farmersdelight:sweet_berry_cookie,-1");
		thirst.add("farmersdelight:honey_cookie,-1");
		thirst.add("farmersdelight:cooked_rice,-4");
		return thirst;
	}
	
	public static List<String> defaultChilledContainers() {
		List<String> chilled = new ArrayList<String>();
//		chilled.add("minecraft:beetroot_soup,3");
		chilled.add("minecraft:potato,0");
		chilled.add("minecraft:carrot,0");
		chilled.add("create:builders_tea,8");
		chilled.add("farmersdelight:tomato_sauce,1");
		chilled.add("farmersdelight:cabbage,0");
		chilled.add("farmersdelight:cabbage_leaf,0");
		chilled.add("farmersdelight:tomato,0");
		chilled.add("farmersdelight:onion,0");
		chilled.add("farmersdelight:pumpkin_slice,0");
		chilled.add("farmersdelight:minced_beef,0");
		chilled.add("farmersdelight:mixed_salad,0");
		return chilled;
	}
	
	public static List<String> defaultHeatedContainers(){
		List<String> heated = new ArrayList<String>(); 
		heated.add("minecraft:rabbit_stew,0");
		heated.add("farmersdelight:hot_cocoa,4");
		heated.add("farmersdelight:beef_stew,1");
		heated.add("farmersdelight:chicken_soup,5");
		heated.add("farmersdelight:vegetable_soup,5");
		heated.add("farmersdelight:pumpkin_soup,5");
		heated.add("farmersdelight:nether_salad,0");
		heated.add("farmersdelight:dumplings,0");
		heated.add("farmersdelight:stuffed_pumpkin,0");
		heated.add("farmersdelight:fish_stew,1");
		heated.add("farmersdelight:baked_cod_stew,1");
		heated.add("farmersdelight:honey_glazed_ham,-2");
		heated.add("farmersdelight:pasta_with_meatballs,0");
		heated.add("farmersdelight:pasta_with_mutton_chop,0");
		heated.add("farmersdelight:vegetable_noodles,0");
		heated.add("farmersdelight:steak_and_potatoes,0");
		heated.add("farmersdelight:shepherds_pie,0");
		heated.add("farmersdelight:ratatouille,0");
		heated.add("farmersdelight:squid_ink_pasta,0");
		heated.add("farmersdelight:grilled_salmon,0");
		return heated;
	}
	public static Survive getInstance() {
		return instance;
	}
}
