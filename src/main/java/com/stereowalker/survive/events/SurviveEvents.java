package com.stereowalker.survive.events;

import java.util.List;

import com.stereowalker.survive.Survive;
import com.stereowalker.survive.compat.SereneSeasonsCompat;
import com.stereowalker.survive.config.Config;
import com.stereowalker.survive.config.ServerConfig;
import com.stereowalker.survive.enchantment.SEnchantmentHelper;
import com.stereowalker.survive.entity.SurviveEntityStats;
import com.stereowalker.survive.fluid.SFluids;
import com.stereowalker.survive.item.SItems;
import com.stereowalker.survive.network.client.CInteractWithWaterPacket;
import com.stereowalker.survive.network.server.SSurvivalStatsPacket;
import com.stereowalker.survive.potion.SEffects;
import com.stereowalker.survive.temperature.TemperatureChangeInstance;
import com.stereowalker.survive.util.TemperatureStats;
import com.stereowalker.survive.util.TemperatureUtil;
import com.stereowalker.survive.util.data.BlockTemperatureData;
import com.stereowalker.unionlib.state.properties.UBlockStateProperties;
import com.stereowalker.unionlib.util.ModHelper;
import com.stereowalker.unionlib.util.RegistryHelper;

import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CauldronBlock;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.EquipmentSlotType.Group;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.EffectInstance;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.SleepingTimeCheckEvent;
import net.minecraftforge.event.world.SleepFinishedTimeEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.network.NetworkDirection;

@EventBusSubscriber
public class SurviveEvents {
	public static final Object2FloatMap<Fluid> FLUID_THIRST_MAP = new Object2FloatOpenHashMap<Fluid>();
	public static final Object2FloatMap<Fluid> FLUID_HYDRATION_MAP = new Object2FloatOpenHashMap<Fluid>();
	public static final Object2BooleanMap<Fluid> FLUID_THIRSTY_MAP = new Object2BooleanOpenHashMap<Fluid>();


	public static void registerHeatMap() {
		registerFluidDrinkStats(Fluids.WATER, 4, 1.0F, true);
		registerFluidDrinkStats(SFluids.PURIFIED_WATER, 6, 3.0F, true);
		registerFluidDrinkStats(Fluids.FLOWING_WATER, 4, 1.0F, false);
		registerFluidDrinkStats(SFluids.FLOWING_PURIFIED_WATER, 6, 3.0F, false);
	}

	public static float getRegisteredThirst(Fluid fluid) {
		return FLUID_THIRST_MAP.getOrDefault(fluid, 0);
	}

	public static float getRegisteredHydration(Fluid fluid) {
		return FLUID_HYDRATION_MAP.getOrDefault(fluid, 0);
	}

	public static boolean getRegisteredThirstEffect(Fluid fluid) {
		return FLUID_THIRSTY_MAP.getOrDefault(fluid, false);
	}

	public static void registerFluidDrinkStats(Fluid fluid, int thirst, float hydration, boolean shouldGiveThirst) {
		FLUID_THIRST_MAP.put(fluid, thirst);
		FLUID_HYDRATION_MAP.put(fluid, hydration);
		FLUID_THIRSTY_MAP.put(fluid, shouldGiveThirst);
	}

	public static float getTotalArmorWeight(LivingEntity player) {
		float totalWeight = 0.124F;
		for (EquipmentSlotType slot : EquipmentSlotType.values()) {
			if (slot.getSlotType() == Group.ARMOR) {
				ItemStack stack = player.getItemStackFromSlot(slot);
				if (!stack.isEmpty() && Config.enable_weights) {
					if (Survive.armorModifierMap.containsKey(stack.getItem().getRegistryName()) && !SEnchantmentHelper.hasWeightless(stack)) {
						int i = SEnchantmentHelper.getFeatherweightModifier(stack);
						//Reduces the total weight of that armor piece by 18% for each level
						totalWeight += Survive.armorModifierMap.get(stack.getItem().getRegistryName()).getWeightModifier() * (1 - i*0.18);
					}
				}
			}
		}
		return totalWeight;
	}

	@SubscribeEvent
	public static void registerStats(LivingUpdateEvent event) {
		if(event.getEntityLiving() instanceof PlayerEntity) {
			PlayerEntity player = (PlayerEntity)event.getEntityLiving();
			SurviveEntityStats.addStatsOnSpawn(player);
		}
	}

	@SubscribeEvent
	public static void manageSleep(LivingUpdateEvent event) {
		if (Config.enable_sleep) {
			if (!event.getEntityLiving().world.isRemote && event.getEntityLiving() instanceof ServerPlayerEntity) {
				ServerPlayerEntity player = (ServerPlayerEntity)event.getEntityLiving();
				if (player.isSleeping())
					SurviveEntityStats.addAwakeTime(player, -player.getSleepTimer());
				else if (player.getServerWorld().getDimensionType().doesBedWork())
					SurviveEntityStats.addAwakeTime(player, 1);
				if (player.ticksExisted % 20 == 0) {
					addTiredEffect(player);
				}
			}
		}
	}

	@SubscribeEvent
	public static void allowSleep(SleepingTimeCheckEvent event) {
		if (Config.enable_sleep) {
			if (!event.getEntityLiving().world.isRemote && event.getEntityLiving() instanceof ServerPlayerEntity) {
				ServerPlayerEntity player = (ServerPlayerEntity)event.getEntityLiving();
				if (SurviveEntityStats.getAwakeTime(player) > time(0) - 5000 && (Config.canSleepDuringDay || !player.world.isDaytime())) {
					event.setResult(Result.ALLOW);
				}
			}
		}
	}

	public static void addTiredEffect(ServerPlayerEntity player) {
		if (timeAmp(player) >= 0 && !player.isPotionActive(SEffects.ENERGIZED)) {
			player.addPotionEffect(new EffectInstance(SEffects.TIREDNESS, 200, Math.min(timeAmp(player), Config.tiredTimeStacks), false, false, true));
		}
	}

	public static int time(int i) {
		return Config.initialTiredTime+(Config.tiredTimeStep*i);
	}

	public static int timeAmp(PlayerEntity player) {
		int extraTime = SurviveEntityStats.getAwakeTime(player) - Config.initialTiredTime;
		return MathHelper.floor(extraTime/Config.tiredTimeStep);
	}

	@SubscribeEvent
	public static void manageSleep(SleepFinishedTimeEvent event) {
		for (PlayerEntity player : event.getWorld().getPlayers()) {
			SurviveEntityStats.setAwakeTime(player, 0);
		}
	}

	@SubscribeEvent
	public static void sendToClient(LivingUpdateEvent event) {
		if (event.getEntityLiving() != null && !event.getEntityLiving().world.isRemote && event.getEntityLiving() instanceof ServerPlayerEntity) {
			ServerPlayerEntity player = (ServerPlayerEntity)event.getEntityLiving();
			Survive.getInstance().channel.sendTo(new SSurvivalStatsPacket(player), player.connection.getNetworkManager(), NetworkDirection.PLAY_TO_CLIENT);
		}
	}

	@SubscribeEvent
	public static void regulateHunger(LivingUpdateEvent event) {
		if (event.getEntityLiving() != null && !event.getEntityLiving().world.isRemote && event.getEntityLiving() instanceof ServerPlayerEntity) {
			ServerPlayerEntity player = (ServerPlayerEntity)event.getEntityLiving();
			if (Config.idle_hunger_tick_rate > -1) {
				if (player.ticksExisted%Config.idle_hunger_tick_rate == Config.idle_hunger_tick_rate-1) {
					player.getFoodStats().addExhaustion(Config.idle_hunger_exhaustion);
				}
			}
		}
	}

	@SubscribeEvent
	public static void regulateTemperature(LivingUpdateEvent event) {
		if (event.getEntityLiving() != null && !event.getEntityLiving().world.isRemote && event.getEntityLiving() instanceof ServerPlayerEntity) {
			ServerPlayerEntity player = (ServerPlayerEntity)event.getEntityLiving();


			if(Config.enable_temperature) {
				double temperature = SurviveEntityStats.getTemperatureStats(player).getTemperatureLevel();
				if(!(player.isCreative() || player.isSpectator())) {
					double maxHeat1 = TemperatureUtil.firstHeat(player);
					double maxHeat2 = TemperatureUtil.secondHeat(player);
					double maxHeat3 = TemperatureUtil.maxHeat(player);
					double maxCold1 = TemperatureUtil.firstCold(player);
					double maxCold2 = TemperatureUtil.secondCold(player);
					double maxCold3 = TemperatureUtil.maxCold(player);

					if (!player.isPotionActive(SEffects.HYPERTHERMIA)) {
						if (temperature > maxHeat1 && temperature <= maxHeat2) {
							player.addPotionEffect(new EffectInstance(SEffects.HYPERTHERMIA, 100, 0));
						}
						else if (temperature > maxHeat2 && temperature <= maxHeat3) {
							player.addPotionEffect(new EffectInstance(SEffects.HYPERTHERMIA, 100, 1));
						}
						else if (temperature > maxHeat3) {
							player.addPotionEffect(new EffectInstance(SEffects.HYPERTHERMIA, 100, 2));
						}
					}
					if (!player.isPotionActive(SEffects.HYPOTHERMIA)) {
						if (temperature < maxCold1 && temperature >= maxCold2) {
							player.addPotionEffect(new EffectInstance(SEffects.HYPOTHERMIA, 100, 0));
						}
						else if (temperature < maxCold2 && temperature >= maxCold3) {
							player.addPotionEffect(new EffectInstance(SEffects.HYPOTHERMIA, 100, 1));
						}
						else if (temperature < maxCold3) {
							player.addPotionEffect(new EffectInstance(SEffects.HYPOTHERMIA, 100, 2));
						}
					}
				}
			}
		}
	}

	@SubscribeEvent
	public static void regulateWetness(LivingUpdateEvent event) {
		if (event.getEntityLiving() != null && !event.getEntityLiving().world.isRemote && event.getEntityLiving() instanceof ServerPlayerEntity) {
			ServerPlayerEntity player = (ServerPlayerEntity)event.getEntityLiving();
			SurviveEntityStats.addWetTime(player, player.isWet() ? 1 : -2);
		}
	}

	/**
	 * Check if precipitation is currently happening at a position
	 */
	public static boolean isSnowingAt(World world, BlockPos position) {
		if (!world.isRaining()) {
			return false;
		} else if (!world.canSeeSky(position)) {
			return false;
		} else if (world.getHeight(Heightmap.Type.MOTION_BLOCKING, position).getY() > position.getY()) {
			return false;
		} else {
			Biome biome = world.getBiome(position);
			return biome.getPrecipitation() == Biome.RainType.SNOW || 
					biome.getTemperature(position) <= 0.15F || 
					ModHelper.isPrimalWinterLoaded() || 
					(ModHelper.isSereneSeasonsLoaded() && SereneSeasonsCompat.snowsHere(world, position));
		}
	}

	@SubscribeEvent
	public static void updateEnvTemperature(LivingUpdateEvent event) {
		if (event.getEntityLiving() != null && !event.getEntityLiving().world.isRemote && event.getEntityLiving() instanceof ServerPlayerEntity) {
			ServerPlayerEntity player = (ServerPlayerEntity)event.getEntityLiving();
			TemperatureStats stats = SurviveEntityStats.getTemperatureStats(player);
			double wetness = (double)(SurviveEntityStats.getWetTime(player)) / -1800.0D;
			TemperatureStats.setTemperatureModifier(player, "survive:wetness", wetness);

			double coolingMod = 0.0D;
			coolingMod -= 0.05D * (float)SEnchantmentHelper.getCoolingModifier(player.getItemStackFromSlot(EquipmentSlotType.HEAD));
			coolingMod -= 0.16D * (float)SEnchantmentHelper.getCoolingModifier(player.getItemStackFromSlot(EquipmentSlotType.CHEST));
			coolingMod -= 0.13D * (float)SEnchantmentHelper.getCoolingModifier(player.getItemStackFromSlot(EquipmentSlotType.LEGS));
			coolingMod -= 0.06D * (float)SEnchantmentHelper.getCoolingModifier(player.getItemStackFromSlot(EquipmentSlotType.FEET));
			TemperatureStats.setTemperatureModifier(player, "survive:cooling_enchantment", coolingMod);

			double warmingMod = 0.0D;
			warmingMod += 0.05D * (float)SEnchantmentHelper.getWarmingModifier(player.getItemStackFromSlot(EquipmentSlotType.HEAD));
			warmingMod += 0.16D * (float)SEnchantmentHelper.getWarmingModifier(player.getItemStackFromSlot(EquipmentSlotType.CHEST));
			warmingMod += 0.13D * (float)SEnchantmentHelper.getWarmingModifier(player.getItemStackFromSlot(EquipmentSlotType.LEGS));
			warmingMod += 0.06D * (float)SEnchantmentHelper.getWarmingModifier(player.getItemStackFromSlot(EquipmentSlotType.FEET));
			TemperatureStats.setTemperatureModifier(player, "survive:warming_enchantment", warmingMod);

			boolean shouldCool = false;
			if (stats.getTemperatureLevel() > Survive.DEFAULT_TEMP) {
				for (EquipmentSlotType types : EquipmentSlotType.values()) {
					if (SEnchantmentHelper.hasAdjustedCooling(player.getItemStackFromSlot(types))) {
						shouldCool = true;
					}
				}
			}
			TemperatureStats.setTemperatureModifier(player, "survive:adjusted_cooling_enchantment", shouldCool?-2.0D:0.0D);

			boolean shouldWarm = false;
			if (stats.getTemperatureLevel() < Survive.DEFAULT_TEMP) {
				for (EquipmentSlotType types : EquipmentSlotType.values()) {
					if (SEnchantmentHelper.hasAdjustedWarming(player.getItemStackFromSlot(types))) {
						shouldWarm = true;
					}
				}
			}
			TemperatureStats.setTemperatureModifier(player, "survive:adjusted_warming_enchantment", shouldWarm?2.0D:0.0D);


			double armorMod = 0.0D;
			for (EquipmentSlotType slot : EquipmentSlotType.values()) {
				if (slot.getSlotType() == Group.ARMOR) {
					if (!player.getItemStackFromSlot(slot).isEmpty()) {
						Item armor = player.getItemStackFromSlot(slot).getItem();
						float modifier = 1.0F;
						if (Survive.armorModifierMap.containsKey(armor.getRegistryName())) {
							List<TemperatureChangeInstance> instances = Survive.armorModifierMap.get(armor.getRegistryName()).getTemperatureModifier();
							for (TemperatureChangeInstance instance : instances) {
								if (instance.shouldChangeTemperature(player)) {
									modifier = instance.getTemperature();
									break;
								}
							}
						}
						armorMod += getModifierFromSlot(slot) * modifier;
					}
				}
			}
			TemperatureStats.setTemperatureModifier(player, "survive:armor", armorMod);

			double snow = 0.0D;
			if (isSnowingAt(player.getServerWorld(), player.getPosition())) {
				snow = -2.0D;
			}
			TemperatureStats.setTemperatureModifier(player, "survive:snow", snow);

			if (ModHelper.isSereneSeasonsLoaded()) {
				float seasonMod = SereneSeasonsCompat.modifyTemperatureBySeason(player.getEntityWorld(), player.getPosition());
				if (ModHelper.isPrimalWinterLoaded()) {
					seasonMod = -1.0F;
				}
				TemperatureStats.setTemperatureModifier(player, "survive:season", seasonMod);
			}
		}
	}

	public static float getModifierFromSlot(EquipmentSlotType slot) {
		switch (slot) {
		case HEAD:return 0.05F;
		case CHEST:return 0.16F;
		case LEGS:return 0.13F;
		case FEET:return 0.06F;
		default:return 0F;
		}
	}

	@SubscribeEvent
	public static void updateTemperature(LivingUpdateEvent event) {
		if (event.getEntityLiving() != null && !event.getEntityLiving().world.isRemote && event.getEntityLiving() instanceof ServerPlayerEntity) {
			ServerPlayerEntity player = (ServerPlayerEntity)event.getEntityLiving();
			for (TempType type : TempType.values()) {
				double temperature;
				if (type.isUsingExact()) {
					temperature = getExactTemperature(player.world, player.getPosition(), type);
				} else {
					temperature = getAverageTemperature(player.world, player.getPosition(), type, 5, Config.tempMode);
				}
				//				System.out.println(type+" "+temperature);
				double modifier = (temperature)/type.getReductionAmount();
				//				System.out.println(type.getReductionAmount());
				int modInt = (int) (modifier*1000);
				modifier = modInt / 1000.0D;
				if (player.ticksExisted%type.getTickInterval() == type.getTickInterval()-1) {
					TemperatureStats.setTemperatureModifier(player, "survive:"+type.getName(), modifier);
				}
			}
		}
	}


	public static double getExactTemperature(World world, BlockPos pos, TempType type) {
		float biomeTemp = (TemperatureUtil.getTemperature(world.getBiome(pos), pos)*2)-2;
		float skyLight = world.getChunkProvider().getLightManager().getLightEngine(LightType.SKY).getLightFor(pos);
		float gameTime = world.getDayTime() % 24000L;
		gameTime = gameTime/(200/3);
		gameTime = (float) Math.sin(Math.toRadians(gameTime));

		switch (type) {
		case SUN:
			if (skyLight > 5.0F) return gameTime*5.0F;
			else return -1.0F * 5.0F;

		case BIOME:
			if (ModHelper.isPrimalWinterLoaded()) {
				biomeTemp = -0.7F;
			}
			float dimensionMod = 1.0F;
			for (String dimensionList : ServerConfig.dimensionModifiers) {
				String[] dimension = dimensionList.split(",");
				ResourceLocation loc = new ResourceLocation(dimension[0]);
				if (RegistryHelper.matchesRegistryKey(loc, world.getDimensionKey())) {
					dimensionMod = Float.parseFloat(dimension[1]);
				}
			}
			return biomeTemp*dimensionMod;

		case BLOCK:
			float blockTemp = 0;
			int rangeInBlocks = 5;
			for (int x = -rangeInBlocks; x <= rangeInBlocks; x++) {
				for (int y = -rangeInBlocks; y <= rangeInBlocks; y++) {
					for (int z = -rangeInBlocks; z <= rangeInBlocks; z++) {

						BlockPos heatSource = new BlockPos(pos.getX()+x, pos.getY()+y, pos.getZ()+z);
						float blockLight = world.getChunkProvider().getLightManager().getLightEngine(LightType.BLOCK).getLightFor(heatSource);
						BlockState heatState = world.getBlockState(heatSource);
						int sourceRange = Survive.blockTemperatureMap.containsKey(heatState.getBlock().getRegistryName()) ? Survive.blockTemperatureMap.get(heatState.getBlock().getRegistryName()).getRange() : 5;

						if (pos.withinDistance(heatSource, sourceRange)) {
							blockTemp += blockLight/500.0F;
							if (Survive.blockTemperatureMap.containsKey(heatState.getBlock().getRegistryName())) {
								BlockTemperatureData blockTemperatureData = Survive.blockTemperatureMap.get(heatState.getBlock().getRegistryName());
								if (blockTemperatureData.usesLitOrActiveProperty()) {
									boolean litOrActive = false;
									if (heatState.hasProperty(BlockStateProperties.LIT) && heatState.get(BlockStateProperties.LIT)) litOrActive = true;
									if (heatState.hasProperty(UBlockStateProperties.ACTIVE) && heatState.get(UBlockStateProperties.ACTIVE)) litOrActive = true;
									if (litOrActive) blockTemp += blockTemperatureData.getTemperatureModifier();
								}
								else
									blockTemp += blockTemperatureData.getTemperatureModifier();

								if (blockTemperatureData.usesLevelProperty()) {
									if (heatState.hasProperty(BlockStateProperties.LEVEL_0_15)) {
										blockTemp*=(heatState.get(BlockStateProperties.LEVEL_0_15)+1)/16;
									}
									else if (heatState.hasProperty(BlockStateProperties.LEVEL_0_8)) {
										blockTemp*=(heatState.get(BlockStateProperties.LEVEL_0_8)+1)/9;
									}
									else if (heatState.hasProperty(BlockStateProperties.LEVEL_1_8)) {
										blockTemp*=(heatState.get(BlockStateProperties.LEVEL_1_8))/8;
									}
									else if (heatState.hasProperty(BlockStateProperties.LEVEL_0_3)) {
										blockTemp*=(heatState.get(BlockStateProperties.LEVEL_0_3)+1)/4;
									}
								}
							}
						}
					}
				}
			}
			return blockTemp;

		case SHADE:
			return ((skyLight / 7.5F) - 1);

		default:
			return Survive.DEFAULT_TEMP;
		}
	}

	private enum TempType {
		BIOME("biome", 10, 7, false), BLOCK("block", 10, 9, true), SHADE("shade", 10, 200, true), SUN("sun", 10, 200, true);

		String name;
		int tickInterval;
		double reductionAmount;
		boolean usingExact;
		private TempType(String name, int tickIntervalIn, double reductionAmountIn, boolean usingExactIn) {
			this.tickInterval = tickIntervalIn;
			this.reductionAmount = reductionAmountIn;
			this.usingExact = usingExactIn;
			this.name = name;
		}
		public String getName() {
			return name;
		}
		public int getTickInterval() {
			return tickInterval;
		}
		public double getReductionAmount() {
			return reductionAmount;
		}

		public boolean isUsingExact() {
			return usingExact;
		}
	}

	public static double getBlendedTemperature(World world, BlockPos mainPos, BlockPos blendPos, TempType type) {
		float distance = (float) Math.sqrt(mainPos.distanceSq(blendPos));// 2 - 10 - 0
		if (distance <= 5.0D) {
			float blendRatio0 = distance / 5.0F;   // 0.2 - 1.0 - 0.0
			float blendRatio1 = 1.0F - blendRatio0; // 0.8 - 0.0 - 1.0
			double temp0 = getExactTemperature(world, blendPos, type);
			double temp1 = getExactTemperature(world, mainPos, type);
			return ((temp0*blendRatio0)+(temp1*blendRatio1));
		} else {
			return getExactTemperature(world, mainPos, type);
		}
	}

	public static float getAverageTemperature(World world, BlockPos pos, TempType type, int rangeInBlocks, TempMode mode) {
		float temp = 0;
		int tempAmount = 0;
		for (int x = -rangeInBlocks; x <= rangeInBlocks; x++) {
			for (int y = -rangeInBlocks; y <= rangeInBlocks; y++) {
				for (int z = -rangeInBlocks; z <= rangeInBlocks; z++) {
					if (mode == TempMode.BLEND)temp+=getBlendedTemperature(world, new BlockPos(pos.getX()+x, pos.getY()+y, pos.getZ()+z), pos, type);
					else if (mode == TempMode.NORMAL)temp+=getExactTemperature(world, new BlockPos(pos.getX()+x, pos.getY()+y, pos.getZ()+z), type);
					tempAmount++;
				}
			}
		}
		return temp/((float)tempAmount);
	}

	public enum TempMode {
		BLEND, NORMAL;
	}

	@SubscribeEvent
	public static void interactWithWaterSourceBlock(PlayerInteractEvent.RightClickEmpty event) {
		RayTraceResult raytraceresult = rayTrace(event.getWorld(), event.getEntityLiving(), RayTraceContext.FluidMode.SOURCE_ONLY);
		BlockPos blockpos = ((BlockRayTraceResult)raytraceresult).getPos();
		if (event.getWorld().isRemote) {
			//Source Block Of Water
			Fluid fluid = event.getWorld().getFluidState(blockpos).getFluid();
			if (FLUID_THIRST_MAP.containsKey(fluid) && FLUID_THIRSTY_MAP.containsKey(fluid) && FLUID_HYDRATION_MAP.containsKey(fluid)) {
				Survive.getInstance().channel.sendTo(new CInteractWithWaterPacket(blockpos, getRegisteredThirstEffect(fluid), getRegisteredThirst(fluid), getRegisteredHydration(fluid), event.getHand(), event.getPlayer().getUniqueID()), ((ClientPlayerEntity)event.getPlayer()).connection.getNetworkManager(), NetworkDirection.PLAY_TO_SERVER);
			}
			//Air Block
			if (event.getWorld().isRainingAt(blockpos)) {
				Survive.getInstance().channel.sendTo(new CInteractWithWaterPacket(event.getPos(), false, 1.0D, 0.5D, event.getHand(), event.getPlayer().getUniqueID()), ((ClientPlayerEntity)event.getPlayer()).connection.getNetworkManager(), NetworkDirection.PLAY_TO_SERVER);
			}
		}
	}

	@SubscribeEvent
	public static void interactWithWaterSourceBlock(PlayerInteractEvent.RightClickBlock event) {
		RayTraceResult raytraceresult = rayTrace(event.getWorld(), event.getEntityLiving(), RayTraceContext.FluidMode.SOURCE_ONLY);
		BlockPos blockpos = ((BlockRayTraceResult)raytraceresult).getPos();
		BlockState state = event.getWorld().getBlockState(event.getPos());
		Fluid fluid = event.getWorld().getFluidState(blockpos).getFluid();
		BlockState stateUnder = event.getWorld().getBlockState(event.getPos().down());
		Item item = event.getItemStack().getItem();
		if (event.getWorld().isRemote && (event.getItemStack().isEmpty() || item == SItems.CANTEEN || item == Items.GLASS_BOTTLE || item == Items.BOWL)) {
			//Source Block Of Water
			if (FLUID_THIRST_MAP.containsKey(fluid) && FLUID_THIRSTY_MAP.containsKey(fluid) && FLUID_HYDRATION_MAP.containsKey(fluid)) {
				event.setCanceled(true);
				event.setCancellationResult(ActionResultType.SUCCESS);
				Survive.getInstance().channel.sendTo(new CInteractWithWaterPacket(blockpos, getRegisteredThirstEffect(fluid), getRegisteredThirst(fluid), getRegisteredHydration(fluid), event.getHand(), event.getPlayer().getUniqueID()), ((ClientPlayerEntity)event.getPlayer()).connection.getNetworkManager(), NetworkDirection.PLAY_TO_SERVER);
			}
			//Cauldron
			if (state.getBlock() == Blocks.CAULDRON) {
				int i = state.get(CauldronBlock.LEVEL);
				if (i > 0) {
					event.setCanceled(true);
					event.setCancellationResult(ActionResultType.SUCCESS);
					if (stateUnder.getBlock() == Blocks.CAMPFIRE && stateUnder.get(BlockStateProperties.LIT)) {
						Survive.getInstance().channel.sendTo(new CInteractWithWaterPacket(event.getPos(), false, 4.0D, event.getHand(), event.getPlayer().getUniqueID()), ((ClientPlayerEntity)event.getPlayer()).connection.getNetworkManager(), NetworkDirection.PLAY_TO_SERVER);
					}
					else {
						Survive.getInstance().channel.sendTo(new CInteractWithWaterPacket(event.getPos(), true, 4.0D, event.getHand(), event.getPlayer().getUniqueID()), ((ClientPlayerEntity)event.getPlayer()).connection.getNetworkManager(), NetworkDirection.PLAY_TO_SERVER);
					}
				}
			}
		}
	}

	protected static RayTraceResult rayTrace(World worldIn, LivingEntity player, RayTraceContext.FluidMode fluidMode) {
		float f = player.rotationPitch;
		float f1 = player.rotationYaw;
		Vector3d vec3d = player.getEyePosition(1.0F);
		float f2 = MathHelper.cos(-f1 * ((float)Math.PI / 180F) - (float)Math.PI);
		float f3 = MathHelper.sin(-f1 * ((float)Math.PI / 180F) - (float)Math.PI);
		float f4 = -MathHelper.cos(-f * ((float)Math.PI / 180F));
		float f5 = MathHelper.sin(-f * ((float)Math.PI / 180F));
		float f6 = f3 * f4;
		float f7 = f2 * f4;
		double d0 = player.getAttribute(ForgeMod.REACH_DISTANCE.get()).getValue();
		Vector3d vec3d1 = vec3d.add((double)f6 * d0, (double)f5 * d0, (double)f7 * d0);
		return worldIn.rayTraceBlocks(new RayTraceContext(vec3d, vec3d1, RayTraceContext.BlockMode.OUTLINE, fluidMode, player));
	}

	@SubscribeEvent
	public static void restoreStats(PlayerEvent.Clone event) {
		SurviveEntityStats.getOrCreateModNBT(event.getPlayer());
		if (!event.isWasDeath()) {
			SurviveEntityStats.setHygieneStats(event.getPlayer(), SurviveEntityStats.getHygieneStats(event.getOriginal()));
			SurviveEntityStats.setWaterStats(event.getPlayer(), SurviveEntityStats.getWaterStats(event.getOriginal()));
			SurviveEntityStats.setEnergyStats(event.getPlayer(), SurviveEntityStats.getEnergyStats(event.getOriginal()));
			SurviveEntityStats.setTemperatureStats(event.getPlayer(), SurviveEntityStats.getTemperatureStats(event.getOriginal()));
			SurviveEntityStats.setAwakeTime(event.getPlayer(), SurviveEntityStats.getAwakeTime(event.getOriginal()));
			SurviveEntityStats.setWetTime(event.getPlayer(), SurviveEntityStats.getWetTime(event.getOriginal()));
		}
	}

	@SubscribeEvent
	public static void addReload(AddReloadListenerEvent event) {
		event.addListener(Survive.thirstReloader);
		event.addListener(Survive.potionReloader);
		event.addListener(Survive.armorReloader);
		event.addListener(Survive.blockReloader);
	}
}
