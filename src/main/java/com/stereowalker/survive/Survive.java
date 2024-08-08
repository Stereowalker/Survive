package com.stereowalker.survive;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.stereowalker.survive.compat.OriginsCompat;
import com.stereowalker.survive.compat.SItemProperties;
import com.stereowalker.survive.config.Config;
import com.stereowalker.survive.config.FoodConfig;
import com.stereowalker.survive.config.HygieneConfig;
import com.stereowalker.survive.config.ServerConfig;
import com.stereowalker.survive.config.StaminaConfig;
import com.stereowalker.survive.config.TemperatureConfig;
import com.stereowalker.survive.config.ThirstConfig;
import com.stereowalker.survive.config.WellbeingConfig;
import com.stereowalker.survive.core.cauldron.SCauldronInteraction;
import com.stereowalker.survive.events.SleepEvents;
import com.stereowalker.survive.events.SurviveEvents;
import com.stereowalker.survive.events.ThirstEvents;
import com.stereowalker.survive.hooks.ColdStorage;
import com.stereowalker.survive.json.ArmorJsonHolder;
import com.stereowalker.survive.json.BiomeJsonHolder;
import com.stereowalker.survive.json.BlockTemperatureJsonHolder;
import com.stereowalker.survive.json.EntityTemperatureJsonHolder;
import com.stereowalker.survive.json.FoodJsonHolder;
import com.stereowalker.survive.json.PotionJsonHolder;
import com.stereowalker.survive.json.property.BlockPropertyHandlerImpl;
import com.stereowalker.survive.network.protocol.game.ClientboundDataTransferPacket;
import com.stereowalker.survive.network.protocol.game.ClientboundDrinkSoundPacket;
import com.stereowalker.survive.network.protocol.game.ClientboundSurvivalStatsPacket;
import com.stereowalker.survive.network.protocol.game.ServerboundArmorStaminaPacket;
import com.stereowalker.survive.network.protocol.game.ServerboundInteractWithWaterPacket;
import com.stereowalker.survive.network.protocol.game.ServerboundPlayerStatusBookPacket;
import com.stereowalker.survive.network.protocol.game.ServerboundRelaxPacket;
import com.stereowalker.survive.network.protocol.game.ServerboundStaminaExhaustionPacket;
import com.stereowalker.survive.network.protocol.game.ServerboundThirstMovementPacket;
import com.stereowalker.survive.resource.ArmorDataManager;
import com.stereowalker.survive.resource.BiomeDataManager;
import com.stereowalker.survive.resource.BlockDataManager;
import com.stereowalker.survive.resource.EntityTemperatureDataManager;
import com.stereowalker.survive.resource.FluidDataManager;
import com.stereowalker.survive.resource.ItemConsummableDataManager;
import com.stereowalker.survive.resource.PotionDrinkDataManager;
import com.stereowalker.survive.server.commands.NeedsCommand;
import com.stereowalker.survive.tags.FluidSTags;
import com.stereowalker.survive.tags.ItemSTags;
import com.stereowalker.survive.world.DataMaps;
import com.stereowalker.survive.world.effect.SMobEffects;
import com.stereowalker.survive.world.entity.ai.attributes.SAttributes;
import com.stereowalker.survive.world.item.CanteenItem;
import com.stereowalker.survive.world.item.HygieneItems;
import com.stereowalker.survive.world.item.SArmorMaterials;
import com.stereowalker.survive.world.item.SCreativeModeTab;
import com.stereowalker.survive.world.item.SItems;
import com.stereowalker.survive.world.item.alchemy.SPotions;
import com.stereowalker.survive.world.item.component.SDataComponents;
import com.stereowalker.survive.world.item.crafting.SRecipeSerializer;
import com.stereowalker.survive.world.item.enchantment.SEnchantmentEffectComponents;
import com.stereowalker.survive.world.level.CGameRules;
import com.stereowalker.survive.world.level.block.SBlocks;
import com.stereowalker.survive.world.level.material.PurifiedWaterFluid;
import com.stereowalker.survive.world.level.material.SFluids;
import com.stereowalker.survive.world.spellcraft.SSpells;
import com.stereowalker.unionlib.api.collectors.BrewingRecipeCollector;
import com.stereowalker.unionlib.api.collectors.CommandCollector;
import com.stereowalker.unionlib.api.collectors.ConfigCollector;
import com.stereowalker.unionlib.api.collectors.DefaultAttributeModifier;
import com.stereowalker.unionlib.api.collectors.InsertCollector;
import com.stereowalker.unionlib.api.collectors.PacketCollector;
import com.stereowalker.unionlib.api.collectors.ReloadListeners;
import com.stereowalker.unionlib.api.creativetabs.CreativeTabBuilder;
import com.stereowalker.unionlib.api.creativetabs.CreativeTabPopulator;
import com.stereowalker.unionlib.api.registries.RegistryCollector;
import com.stereowalker.unionlib.event.potionfluid.FluidToPotionEvent;
import com.stereowalker.unionlib.event.potionfluid.PotionToFluidEvent;
import com.stereowalker.unionlib.insert.Inserts;
import com.stereowalker.unionlib.mod.MinecraftMod;
import com.stereowalker.unionlib.mod.PacketHolder;
import com.stereowalker.unionlib.mod.ServerSegment;
import com.stereowalker.unionlib.util.VersionHelper;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.component.WrittenBookContent;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.entries.NestedLootTable;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod(value = "survive")
public class Survive extends MinecraftMod implements PacketHolder {

	public static final float DEFAULT_TEMP = 37.0F;
	public static final int PURIFIED_WATER_COLOR = 0xFF41d3f8;
	public static Map<Holder<Potion>,List<Fluid>> POTION_FLUID_MAP;
	public static final String MOD_ID = "survive";

	public static final Config CONFIG = new Config();
	public static final FoodConfig FOOD_CONFIG = new FoodConfig();
	public static final StaminaConfig STAMINA_CONFIG = new StaminaConfig();
	public static final HygieneConfig HYGIENE_CONFIG = new HygieneConfig();
	public static final TemperatureConfig TEMPERATURE_CONFIG = new TemperatureConfig();
	public static final ThirstConfig THIRST_CONFIG = new ThirstConfig();
	public static final WellbeingConfig WELLBEING_CONFIG = new WellbeingConfig();
	

	
	public static void sendPacket(WrittenBookContent tag) {
		new ServerboundPlayerStatusBookPacket(tag, !Survive.TEMPERATURE_CONFIG.displayTempInFahrenheit, 
				net.minecraft.client.resources.language.I18n.get("book.patient.sleep", "%1$s"),
				net.minecraft.client.resources.language.I18n.get("book.patient.temperature", "%1$s")).send();
	}
	
	public static final Codec<Long> NON_NEGATIVE_LONG = Codec.LONG
            .validate(
                    p_274889_ -> p_274889_.compareTo(0L) >= 0 && p_274889_.compareTo(Long.MAX_VALUE) <= 0
                            ? DataResult.success(p_274889_)
                            : DataResult.error(() -> "Value must be non-negative: " + p_274889_)
                );

	public static boolean isPrimalWinterLoaded;
	public static final ItemConsummableDataManager consummableReloader = new ItemConsummableDataManager();
	public static final PotionDrinkDataManager potionReloader = new PotionDrinkDataManager();
	public static final ArmorDataManager armorReloader = new ArmorDataManager();
	public static final BlockDataManager blockReloader = new BlockDataManager();
	public static final BiomeDataManager biomeReloader = new BiomeDataManager();
	public static final EntityTemperatureDataManager entityReloader = new EntityTemperatureDataManager();
	public static final FluidDataManager fluidReloader = new FluidDataManager();
	private static Survive instance;

	public static boolean isCombatLoaded() {
		return ModList.get().isLoaded("combat");
	}
	public static boolean isOriginsLoaded() {
		return ModList.get().isLoaded("origins");
	}

	public Survive() 
	{
		super("survive", () -> new SurviveClientSegment(), () -> new ServerSegment());
		instance = this;
		eventBus().addListener(this::clientRegistries);
		//		MinecraftForge.EVENT_BUS.register(this);
		MinecraftForge.EVENT_BUS.addListener((Consumer<PotionToFluidEvent>) event -> {
			if (event.getPotion() == SPotions.PURIFIED_WATER.holder()) {
				event.setFluid(SFluids.PURIFIED_WATER);
				event.setFlowingFluid(SFluids.FLOWING_PURIFIED_WATER);
			}
		});
		MinecraftForge.EVENT_BUS.addListener((Consumer<FluidToPotionEvent.FromStateEvent>) event -> {
			if (event.getFluid().getType() instanceof PurifiedWaterFluid) {
				event.setPotion(SPotions.PURIFIED_WATER.holder());
			}
		});
		isPrimalWinterLoaded = ModList.get().isLoaded("primalwinter");
	}
	
	@Override
	public void onModConstruct() {
		new FluidSTags();
		new ItemSTags();
		if (isCombatLoaded())SSpells.registerAll(eventBus());
		if (isOriginsLoaded())OriginsCompat.initOriginsPatcher();
		BlockPropertyHandlerImpl.init();
	}
	
	@Override
	public void setupBrewingRecipes(BrewingRecipeCollector collector) {
		collector.addMix(Potions.AWKWARD, SItems.ICE_CUBE, SPotions.COLD_RESISTANCE);
		collector.addMix(SPotions.COLD_RESISTANCE, Items.REDSTONE, SPotions.LONG_COLD_RESISTANCE);
		collector.addMix(SPotions.COLD_RESISTANCE, Items.GLOWSTONE_DUST, SPotions.STRONG_COLD_RESISTANCE);
		
		collector.addMix(Potions.AWKWARD, SItems.MAGMA_PASTE, SPotions.HEAT_RESISTANCE);
		collector.addMix(SPotions.HEAT_RESISTANCE, Items.REDSTONE, SPotions.LONG_HEAT_RESISTANCE);
		collector.addMix(SPotions.HEAT_RESISTANCE, Items.GLOWSTONE_DUST, SPotions.STRONG_HEAT_RESISTANCE);
		
		collector.builder().addContainer(SItems.FILLED_CANTEEN);
		collector.builder().addContainer(SItems.FILLED_NETHERITE_CANTEEN);
	}
	
	@Override
	public void onModStartup() {
		SCauldronInteraction.bootStrap();
		CGameRules.init();

//		for(Item item : ForgeRegistries.ITEMS) {
//			if (item.isEdible())
//				DataMaps.Server.defaultFood.put(ForgeRegistries.ITEMS.getKey(item), item.getFoodProperties());
//		}
	}
	
	@Override
	public void onModStartupInClient() {
		RenderType frendertype = RenderType.translucent();
		ItemBlockRenderTypes.setRenderLayer(SFluids.PURIFIED_WATER, frendertype);
		ItemBlockRenderTypes.setRenderLayer(SFluids.FLOWING_PURIFIED_WATER, frendertype);
	}
	
	@Override
	public void setupConfigs(ConfigCollector collector) {
		collector.registerConfig(ServerConfig.class);
		collector.registerConfig(FOOD_CONFIG);
		collector.registerConfig(CONFIG);
		collector.registerConfig(HYGIENE_CONFIG); 
		collector.registerConfig(TEMPERATURE_CONFIG);
		collector.registerConfig(THIRST_CONFIG);
		collector.registerConfig(WELLBEING_CONFIG);
		collector.registerConfig(STAMINA_CONFIG);
	}
	
	@Override
	public void setupCommands(CommandCollector collector) {
		NeedsCommand.register(collector.dispatcher());
	}

	@Override
	public void setupRegistries(RegistryCollector collector) {
		collector.addRegistryHolder(Registries.ATTRIBUTE, SAttributes.class);
		collector.addRegistryHolder(Registries.BLOCK, SBlocks.class);
		collector.addRegistryHolder(Registries.FLUID, SFluids.class);
		collector.addRegistryHolder(Registries.ITEM, SItems.class);
		collector.addRegistryHolder(Registries.ITEM, HygieneItems.class);
		collector.addRegistryHolder(Registries.MOB_EFFECT, SMobEffects.class);
		collector.addRegistryHolder(Registries.RECIPE_SERIALIZER, SRecipeSerializer.class);
		collector.addRegistryHolder(Registries.DATA_COMPONENT_TYPE, SDataComponents.class);
		collector.addRegistryHolder(Registries.ENCHANTMENT_EFFECT_COMPONENT_TYPE, SEnchantmentEffectComponents.class);
		collector.addRegistryHolder(Registries.ARMOR_MATERIAL, SArmorMaterials.class);
		collector.addRegistryHolder(Registries.POTION, SPotions.class);
	}
	
	@Override
	public void registerInserts(InsertCollector collector) {
		collector.addInsert(Inserts.LIVING_TICK, SurviveEvents::sendToClient);
		collector.addInsert(Inserts.LIVING_TICK, SurviveEvents::updateEnvTemperature);
		collector.addInsert(Inserts.PLAYER_RESTORE, SurviveEvents::restoreStats);
		collector.addInsert(Inserts.LOGGED_OUT, SurviveEvents::desyncClient);
		collector.addInsert(Inserts.LEVEL_LOAD, SurviveEvents::addReload);
		collector.addInsert(Inserts.LOOT_TABLE_LOAD, (id,lootTable,cancel)->{
			String ANIMAL_LOOT = "entities/animal_fat";
			List<Pair<ResourceLocation, List<String>>> LOOT_MODIFIERS = Lists.newArrayList(
					Pair.of(VersionHelper.toLoc("entities/sheep"), Lists.newArrayList(ANIMAL_LOOT)),
					Pair.of(VersionHelper.toLoc("entities/chicken"), Lists.newArrayList(ANIMAL_LOOT)),
					Pair.of(VersionHelper.toLoc("entities/cow"), Lists.newArrayList(ANIMAL_LOOT)),
					Pair.of(VersionHelper.toLoc("entities/pig"), Lists.newArrayList(ANIMAL_LOOT))
					);
			
			BiFunction<String, Integer, LootPoolEntryContainer.Builder<?>> getInjectEntry = (name, weight) -> {
				ResourceKey<LootTable> table = ResourceKey.create(Registries.LOOT_TABLE, Survive.getInstance().location("inject/" + name));
				return NestedLootTable.lootTableReference(table).setWeight(weight);
			};
			
			LOOT_MODIFIERS.forEach((pair) -> {
				if(id.equals(pair.getKey())) {
					pair.getValue().forEach((file) -> {
						Survive.getInstance().debug("Injecting \""+file+"\" in "+pair.getKey());
						lootTable.get().addPool(LootPool.lootPool()
								.add(getInjectEntry.apply(file, 1))
								.setBonusRolls(UniformGenerator.between(0.0F, 1.0F))
								.name("survive_inject").build());
					});
				}
			});
		});
		collector.addInsert(Inserts.ITEM_TOOLTIP, (stack, player, tip, flag)->{
			if (player != null)
				FoodUtils.applyFoodStatusToTooltip(player, stack, tip);
		});
		collector.addInsert(Inserts.MENU_OPEN, (player, menu)->{
			if (player != null)
				FoodUtils.giveLifespanToFood(menu.getItems(), player.level().getGameTime());
		});
		collector.addInsert(Inserts.PLAYER_CAN_SLEEP, SleepEvents::allowSleep);
		collector.addInsert(Inserts.PLAYER_CONTINUE_SLEEP, SleepEvents::allowSleep);
		collector.addInsert(Inserts.INTERACT_WITH_BLOCK, ThirstEvents::interactWithWaterSourceBlock);
		collector.addInsert(Inserts.MENU_OPEN, (player, menu) -> {
			if (menu instanceof ChestMenu chest && chest.getContainer() instanceof ChestBlockEntity block && player instanceof ServerPlayer pl) {
				ColdStorage cold = (ColdStorage)block;
			}
		});
		collector.addInsert(Inserts.LEVEL_WAKE_UP, (level, time)->{
			SleepEvents.replenishEnergy(level);
		});
	}
	
	@Override
	public void modifyDefaultEntityAttributes(DefaultAttributeModifier modifier) {
		super.modifyDefaultEntityAttributes(modifier);
		modifier.addToEntity(EntityType.PLAYER, SAttributes.COLD_RESISTANCE.holder(), SAttributes.HEAT_RESISTANCE.holder(), SAttributes.MAX_STAMINA.holder());
	}
	
	@Override
	public void registerPackets(PacketCollector collector) {
		collector.registerServerboundPacket(ServerboundArmorStaminaPacket.id, ServerboundArmorStaminaPacket.class, ServerboundArmorStaminaPacket::new);
		collector.registerServerboundPacket(ServerboundThirstMovementPacket.id, ServerboundThirstMovementPacket.class, ServerboundThirstMovementPacket::new);
		collector.registerServerboundPacket(ServerboundInteractWithWaterPacket.id, ServerboundInteractWithWaterPacket.class, ServerboundInteractWithWaterPacket::new);
		collector.registerServerboundPacket(ServerboundStaminaExhaustionPacket.id, ServerboundStaminaExhaustionPacket.class, ServerboundStaminaExhaustionPacket::new);
		collector.registerServerboundPacket(ServerboundRelaxPacket.id, ServerboundRelaxPacket.class, ServerboundRelaxPacket::new);
		collector.registerServerboundPacket(ServerboundPlayerStatusBookPacket.id, ServerboundPlayerStatusBookPacket.class, ServerboundPlayerStatusBookPacket::new);
		collector.registerClientboundPacket(ClientboundSurvivalStatsPacket.id, ClientboundSurvivalStatsPacket.class, ClientboundSurvivalStatsPacket::new);
		collector.registerClientboundPacket(ClientboundDrinkSoundPacket.id, ClientboundDrinkSoundPacket.class, ClientboundDrinkSoundPacket::new);
		collector.registerClientboundPacket(ClientboundDataTransferPacket.id, ClientboundDataTransferPacket.class, ClientboundDataTransferPacket::new);
	}

	//TODO: FInd Somewhere to put all these
	public static void registerDrinkDataForItem(ResourceLocation location, FoodJsonHolder drinkData) {
		DataMaps.Server.consummableItem.put(location, drinkData);
	}
	public static void registerDrinkDataForPotion(ResourceLocation location, PotionJsonHolder consummableData) {
		DataMaps.Server.potionDrink.put(location, consummableData);
	}
	public static void registerArmorTemperatures(ResourceLocation location, ArmorJsonHolder armorData) {
		DataMaps.Server.armor.put(location, armorData);
	}
	public static void registerBlockTemperatures(ResourceLocation location, BlockTemperatureJsonHolder drinkData) {
		DataMaps.Server.blockTemperature.put(location, drinkData);
	}
	public static void registerEntityTemperatures(ResourceLocation location, EntityTemperatureJsonHolder drinkData) {
		DataMaps.Server.entityTemperature.put(location, drinkData);
	}
	public static void registerBiomeTemperatures(ResourceLocation location, BiomeJsonHolder biomeData) {
		DataMaps.Server.biome.put(location, biomeData);
	}

	public void debug(Object message) {
		if (CONFIG.debugMode)getLogger().debug(message);
	}

	public void clientRegistries(final FMLClientSetupEvent event)
	{
		event.enqueueWork(()->{
			SItemProperties.registerAll();
		});
	}
	
	public static ItemStack convertToPlayerStatusBook(ItemStack stack) {
		ItemStack result = new ItemStack(Items.WRITTEN_BOOK);
		result.applyComponents(stack.getComponents());
		result.set(SDataComponents.STATUS_OWNER, UUID.fromString("00000000-0000-0000-0000-000000000000"));
		WrittenBookContent book = result.get(DataComponents.WRITTEN_BOOK_CONTENT);
		if (book != null) {
			WrittenBookContent b = new WrittenBookContent(book.title(), book.author(), 0, book.pages(), book.resolved());
			result.set(DataComponents.WRITTEN_BOOK_CONTENT, b);
		}
		return result;
	}
	
	@Override
	public void registerServerRelaodableResources(ReloadListeners reloadListener) {
		reloadListener.listenTo(consummableReloader);
		reloadListener.listenTo(potionReloader);
		reloadListener.listenTo(armorReloader);
		reloadListener.listenTo(blockReloader);
		reloadListener.listenTo(biomeReloader);
		reloadListener.listenTo(entityReloader);
		reloadListener.listenTo(fluidReloader);
	}
	
	@Override
	public void registerCreativeTabs(CreativeTabBuilder builder) {
		builder.addTab("main_tab", SCreativeModeTab.TAB_MAIN);
	}
	
	@Override
	public void populateCreativeTabs(CreativeTabPopulator populator) {
		//Hygiene related
		if (populator.getTab().getDisplayName().equals(SCreativeModeTab.TAB_MAIN.getDisplayName()) && Survive.HYGIENE_CONFIG.enabled) {
			populator.addItems(HygieneItems.BATH_SPONGE);
			populator.addItems(HygieneItems.WHITE_WASHCLOTH);
			populator.addItems(HygieneItems.ORANGE_WASHCLOTH);
			populator.addItems(HygieneItems.MAGENTA_WASHCLOTH);
			populator.addItems(HygieneItems.LIGHT_BLUE_WASHCLOTH);
			populator.addItems(HygieneItems.YELLOW_WASHCLOTH);
			populator.addItems(HygieneItems.LIME_WASHCLOTH);
			populator.addItems(HygieneItems.PINK_WASHCLOTH);
			populator.addItems(HygieneItems.GRAY_WASHCLOTH);
			populator.addItems(HygieneItems.LIGHT_GRAY_WASHCLOTH);
			populator.addItems(HygieneItems.CYAN_WASHCLOTH);
			populator.addItems(HygieneItems.PURPLE_WASHCLOTH);
			populator.addItems(HygieneItems.BLUE_WASHCLOTH);
			populator.addItems(HygieneItems.BROWN_WASHCLOTH);
			populator.addItems(HygieneItems.GREEN_WASHCLOTH);
			populator.addItems(HygieneItems.RED_WASHCLOTH);
			populator.addItems(HygieneItems.BLACK_WASHCLOTH);
			populator.addItems(HygieneItems.WOOD_ASH);
			populator.addItems(HygieneItems.POTASH_SOLUTION);
			populator.addItems(HygieneItems.POTASH);
			populator.addItems(HygieneItems.ANIMAL_FAT);
			populator.addItems(HygieneItems.SOAP_MIX);
			populator.addItems(HygieneItems.SOAP_BOTTLE);
		}
		if (populator.getTab().getDisplayName().equals(SCreativeModeTab.TAB_MAIN.getDisplayName())) {
			populator.addItems(SItems.WOOL_HAT);
			populator.addItems(SItems.WOOL_JACKET);
			populator.addItems(SItems.WOOL_PANTS);
			populator.addItems(SItems.WOOL_BOOTS);
			populator.addItems(SItems.STIFFENED_HONEY_HELMET);
			populator.addItems(SItems.STIFFENED_HONEY_CHESTPLATE);
			populator.addItems(SItems.STIFFENED_HONEY_LEGGINGS);
			populator.addItems(SItems.STIFFENED_HONEY_BOOTS);
			populator.addItems(SItems.WATER_BOWL);
			populator.addItems(SItems.PURIFIED_WATER_BOWL);
			populator.addItems(SItems.ICE_CUBE);
			populator.addItems(SItems.THERMOMETER);
			populator.addItems(SItems.TEMPERATURE_REGULATOR);
			populator.addItems(SItems.LARGE_HEATING_PLATE);
			populator.addItems(SItems.LARGE_COOLING_PLATE);
			populator.addItems(SItems.MEDIUM_HEATING_PLATE);
			populator.addItems(SItems.MEDIUM_COOLING_PLATE);
			populator.addItems(SItems.SMALL_HEATING_PLATE);
			populator.addItems(SItems.SMALL_COOLING_PLATE);
			populator.addItems(SItems.CHARCOAL_FILTER);
			populator.addItems(SItems.USED_CHARCOAL_FILTER);
			populator.addItems(SItems.PURIFIED_WATER_BUCKET);
			populator.addItems(SItems.MAGMA_PASTE);
			populator.addItems(SItems.CANTEEN);
			populator.getParams().holders().lookup(Registries.POTION).ifPresent(p_327138_ -> {
				generatePotionEffectTypes(populator.getOutput(), p_327138_, SItems.FILLED_CANTEEN, THIRST_CONFIG.canteen_fill_amount, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
			});
			populator.addItems(SItems.NETHERITE_CANTEEN);
			populator.getParams().holders().lookup(Registries.POTION).ifPresent(p_327138_ -> {
				generatePotionEffectTypes(populator.getOutput(), p_327138_, SItems.FILLED_NETHERITE_CANTEEN, THIRST_CONFIG.nether_canteen_fill_amount, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
			});
		}
		
	}

    private static void generatePotionEffectTypes(
        CreativeModeTab.Output pOutput, HolderLookup<Potion> pPotions, Item pItem, int max, CreativeModeTab.TabVisibility pTabVisibility
    ) {
        pPotions.listElements()
            .map(potion -> CanteenItem.addToCanteen(new ItemStack(pItem), max, potion))
            .forEach(p_270000_ -> pOutput.accept(p_270000_, pTabVisibility));
    }

	public static List<String> defaultDimensionMods() {
		List<String> dims = new ArrayList<String>();
		dims.add("minecraft:overworld,0.0");
		dims.add("minecraft:the_nether,0.0");
		dims.add("minecraft:the_end,0.0");
		return dims;
	}
	
	public static Survive getInstance() {
		return instance;
	}
}
