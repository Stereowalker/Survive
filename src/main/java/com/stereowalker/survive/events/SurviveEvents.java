package com.stereowalker.survive.events;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.commons.lang3.tuple.Triple;

import com.mojang.datafixers.util.Pair;
import com.stereowalker.survive.Survive;
import com.stereowalker.survive.api.IBlockPropertyHandler;
import com.stereowalker.survive.api.IBlockPropertyHandler.PropertyPair;
import com.stereowalker.survive.api.world.level.block.TemperatureEmitter;
import com.stereowalker.survive.compat.SereneSeasonsCompat;
import com.stereowalker.survive.config.ServerConfig;
import com.stereowalker.survive.core.SurviveEntityStats;
import com.stereowalker.survive.core.TempMode;
import com.stereowalker.survive.json.BlockTemperatureJsonHolder;
import com.stereowalker.survive.json.EntityTemperatureJsonHolder;
import com.stereowalker.survive.json.FluidJsonHolder;
import com.stereowalker.survive.needs.IRealisticEntity;
import com.stereowalker.survive.needs.SleepData;
import com.stereowalker.survive.needs.TemperatureData;
import com.stereowalker.survive.needs.TemperatureUtil;
import com.stereowalker.survive.network.protocol.game.ClientboundDataTransferPacket;
import com.stereowalker.survive.network.protocol.game.ClientboundSurvivalStatsPacket;
import com.stereowalker.survive.network.protocol.game.ServerboundInteractWithWaterPacket;
import com.stereowalker.survive.world.DataMaps;
import com.stereowalker.survive.world.effect.SMobEffects;
import com.stereowalker.survive.world.entity.ai.attributes.SAttributes;
import com.stereowalker.survive.world.item.enchantment.SEnchantmentHelper;
import com.stereowalker.survive.world.seasons.Season;
import com.stereowalker.survive.world.temperature.TemperatureModifier.ContributingFactor;
import com.stereowalker.survive.world.temperature.TemperatureQuery;
import com.stereowalker.survive.world.temperature.conditions.TemperatureChangeInstance;
import com.stereowalker.unionlib.util.ModHelper;
import com.stereowalker.unionlib.util.RegistryHelper;
import com.stereowalker.unionlib.util.math.UnionMathHelper;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlot.Type;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.SleepingTimeCheckEvent;
import net.minecraftforge.event.world.SleepFinishedTimeEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.network.NetworkDirection;

@EventBusSubscriber
public class SurviveEvents {
	@SubscribeEvent
	public static void allowSleep(SleepingTimeCheckEvent event) {
		if (Survive.CONFIG.enable_sleep) {
			if (event.getEntityLiving() instanceof ServerPlayer) {
				ServerPlayer player = (ServerPlayer)event.getEntityLiving();
				if (SurviveEntityStats.getSleepStats(player).getAwakeTimer() > time(0) - 5000 && Survive.CONFIG.canSleepDuringDay) {
					event.setResult(Result.ALLOW);
				}
				else if (SurviveEntityStats.getEnergyStats(player).getEnergyLevel() < player.getAttributeValue(SAttributes.MAX_STAMINA)/2) {
					event.setResult(Result.ALLOW);
				}
			}
		}
	}

	public static int time(int i) {
		return Survive.CONFIG.initialTiredTime+(Survive.CONFIG.tiredTimeStep*i);
	}

	@SubscribeEvent
	public static void manageSleep(SleepFinishedTimeEvent event) {
		for (Player player : event.getWorld().players()) {
			SleepData stats = SurviveEntityStats.getSleepStats(player);
			stats.setAwakeTimer(0);
			stats.save(player);
		}
	}

	@SubscribeEvent
	public static void sendToClient(LivingUpdateEvent event) {
		if (event.getEntityLiving() != null && !event.getEntityLiving().level.isClientSide && event.getEntityLiving() instanceof ServerPlayer) {
			ServerPlayer player = (ServerPlayer)event.getEntityLiving();
			Survive.getInstance().channel.sendTo(new ClientboundSurvivalStatsPacket(player), player.connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
			if (!DataMaps.Server.syncedToClient) {
				Survive.getInstance().getLogger().info("Syncing All Data To Client ("+player.getDisplayName().getString()+")");
				Survive.getInstance().getLogger().info("Syncing Armor Data");
				MutableInt a = new MutableInt(0);
				DataMaps.Server.armor.forEach((key, value) -> {
					Survive.getInstance().channel.sendTo(new ClientboundDataTransferPacket(key, value, a.getValue() == 0), player.connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
					a.increment();;
				});
				Survive.getInstance().getLogger().info("Done with Armors");
				Survive.getInstance().getLogger().info("Syncing Fluid Data");
				MutableInt i = new MutableInt(0);
				DataMaps.Server.fluid.forEach((key, value) -> {
					Survive.getInstance().channel.sendTo(new ClientboundDataTransferPacket(key, value, i.getValue() == 0), player.connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
					i.increment();;
				});
				Survive.getInstance().getLogger().info("Done with Fluids");
				DataMaps.Server.syncedToClient = true;
			}
		}
	}

	@SubscribeEvent
	public static void regulateWetness(LivingUpdateEvent event) {
		if (event.getEntityLiving() != null && !event.getEntityLiving().level.isClientSide && event.getEntityLiving() instanceof ServerPlayer) {
			ServerPlayer player = (ServerPlayer)event.getEntityLiving();
			SurviveEntityStats.addWetTime(player, player.isUnderWater() ? 2 : player.isInWaterOrRain() ? 1 : -2);
		}
	}

	/**
	 * Check if precipitation is currently happening at a position
	 */
	@SuppressWarnings("deprecation")
	public static boolean isSnowingAt(Level world, BlockPos position) {
		if (!world.isRaining()) {
			return false;
		} else if (!world.canSeeSky(position)) {
			return false;
		} else if (world.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, position).getY() > position.getY()) {
			return false;
		} else {
			Biome biome = world.getBiome(position).value();
			return biome.getPrecipitation() == Biome.Precipitation.SNOW || 
					biome.getTemperature(position) <= 0.15F || 
					ModHelper.isPrimalWinterLoaded() || 
					(ModHelper.isSereneSeasonsLoaded() && SereneSeasonsCompat.snowsHere(world, position));
		}
	}

	@SubscribeEvent
	public static void updateEnvTemperature(LivingUpdateEvent event) {
		if (event.getEntityLiving() != null && event.getEntityLiving() instanceof ServerPlayer) {
			ServerPlayer player = (ServerPlayer)event.getEntityLiving();
			if (player.isAlive()) {
				for (ResourceLocation queryId : TemperatureQuery.queries.keySet()) {
					double queryValue = TemperatureQuery.queries.get(queryId).getA().run(player, SurviveEntityStats.getTemperatureStats(player).getTemperatureLevel(), player.level, player.blockPosition());
					TemperatureData.setTemperatureModifier(player, queryId, queryValue, TemperatureQuery.queries.get(queryId).getB());
				}
			}
		}
	}

	public static float getModifierFromSlot(EquipmentSlot slot) {
		switch (slot) {
		case HEAD:return 0.05F;
		case CHEST:return 0.16F;
		case LEGS:return 0.13F;
		case FEET:return 0.06F;
		default:return 0F;
		}
	}

	public static double getExactTemperature(Level world, BlockPos pos, TempType type) {
		float skyLight = world.getChunkSource().getLightEngine().getLayerListener(LightLayer.SKY).getLightValue(pos);
		float gameTime = world.getDayTime() % 24000L;
		gameTime = gameTime/(200/3);
		gameTime = (float) Math.sin(Math.toRadians(gameTime));

		switch (type) {
		case SUN:
			float sunIntensity = 5.0f;
			if (DataMaps.Server.biomeTemperature.containsKey(world.getBiome(pos).value().getRegistryName())) {
				sunIntensity = DataMaps.Server.biomeTemperature.get(world.getBiome(pos).value().getRegistryName()).getSunIntensity();
			}
			if (skyLight > 5.0F) return gameTime*sunIntensity;
			else return -1.0F * sunIntensity;

		case BIOME:
			float biomeTemp = (TemperatureUtil.getTemperature(world.getBiome(pos).value(), pos)*2)-2;
			if (ModHelper.isPrimalWinterLoaded()) {
				biomeTemp = -0.7F;
			}
			return biomeTemp;

		case BLOCK:
			float totalBlockTemp = 0;
			int rangeInBlocks = 5;
			for (int x = -rangeInBlocks; x <= rangeInBlocks; x++) {
				for (int y = -rangeInBlocks; y <= rangeInBlocks; y++) {
					for (int z = -rangeInBlocks; z <= rangeInBlocks; z++) {

						float blockTemp = 0;
						BlockPos heatSource = new BlockPos(pos.getX()+x, pos.getY()+y, pos.getZ()+z);
						float blockLight = world.getChunkSource().getLightEngine().getLayerListener(LightLayer.BLOCK).getLightValue(heatSource);
						BlockState heatState = world.getBlockState(heatSource);
						float sourceRange;
						if (heatState.getBlock() instanceof TemperatureEmitter) {
							sourceRange = ((TemperatureEmitter)heatState.getBlock()).getModificationRange(heatState);
						} else {
							sourceRange = DataMaps.Server.blockTemperature.containsKey(heatState.getBlock().getRegistryName()) ? DataMaps.Server.blockTemperature.get(heatState.getBlock().getRegistryName()).getRange() : 5;
						}

						if (pos.closerThan(heatSource, sourceRange)) {
							blockTemp += blockLight/500.0F;
							//Radiator Override
							if (heatState.getBlock() instanceof TemperatureEmitter) {
								blockTemp = ((TemperatureEmitter)heatState.getBlock()).getTemperatureModification(heatState);
							}
							else if (DataMaps.Server.blockTemperature.containsKey(heatState.getBlock().getRegistryName())) {
								BlockTemperatureJsonHolder blockTemperatureData = DataMaps.Server.blockTemperature.get(heatState.getBlock().getRegistryName());
								if (blockTemperatureData.getStateChangeProperty() != null) {
									boolean setTemp = false;
									List<Triple<IBlockPropertyHandler<?>,List<PropertyPair<?>>,Map<String,Float>>> changeProperty = blockTemperatureData.getStateChangeProperty();
									first:
										for (Triple<IBlockPropertyHandler<?>, List<PropertyPair<?>>, Map<String, Float>> handler : changeProperty) {
											boolean meets = true;
											for (PropertyPair<?> requirements : handler.getMiddle()) {
												if (!heatState.getValue(requirements.getFirst()).equals(requirements.getSecond())) {
													meets = false;
													break;
												}
											}
											if (meets)
												for (String prop2 : handler.getRight().keySet())
													if (heatState.getValue(handler.getLeft().derivedProperty()).equals(handler.getLeft().getValue(prop2))) {
														blockTemp += handler.getRight().get(prop2);
														setTemp = true;
														break first;
													}
										}
									if (!setTemp) blockTemp += blockTemperatureData.getTemperatureModifier();
								}
								else {
									blockTemp += blockTemperatureData.getTemperatureModifier();

									if (blockTemperatureData.usesLevelProperty()) {
										if (heatState.hasProperty(BlockStateProperties.LEVEL)) {
											blockTemp*=(heatState.getValue(BlockStateProperties.LEVEL)+1)/16;
										}
										else if (heatState.hasProperty(BlockStateProperties.LEVEL_COMPOSTER)) {
											blockTemp*=(heatState.getValue(BlockStateProperties.LEVEL_COMPOSTER)+1)/9;
										}
										else if (heatState.hasProperty(BlockStateProperties.LEVEL_FLOWING)) {
											blockTemp*=(heatState.getValue(BlockStateProperties.LEVEL_FLOWING))/8;
										}
										else if (heatState.hasProperty(BlockStateProperties.LEVEL_CAULDRON)) {
											blockTemp*=(heatState.getValue(BlockStateProperties.LEVEL_CAULDRON)+1)/4;
										}
									}
								}
							}
							totalBlockTemp+=blockTemp;
						}
					}
				}
			}
			return totalBlockTemp;

		case SHADE:
			return ((skyLight / 7.5F) - 1);

		case ENTITY:
			float totalEntityTemp = 0;
			rangeInBlocks = 5;
			for (Entity entity : world.getEntitiesOfClass(Entity.class, new AABB(pos.offset(rangeInBlocks, rangeInBlocks, rangeInBlocks), pos.offset(-rangeInBlocks, -rangeInBlocks, -rangeInBlocks)))) {
				float sourceRange = DataMaps.Server.entityTemperature.containsKey(entity.getType().getRegistryName()) ? DataMaps.Server.entityTemperature.get(entity.getType().getRegistryName()).getRange() : 5;

				if (pos.closerThan(entity.blockPosition(), sourceRange)) {
					if (DataMaps.Server.entityTemperature.containsKey(entity.getType().getRegistryName())) {
						EntityTemperatureJsonHolder entityTemperatureData = DataMaps.Server.entityTemperature.get(entity.getType().getRegistryName());
						totalEntityTemp+=entityTemperatureData.getTemperatureModifier();
					}
				}
			}
			return totalEntityTemp;

		default:
			return Survive.DEFAULT_TEMP;
		}
	}

	private enum TempType {
		BIOME("biome", 7, false), BLOCK("block", 9, true), ENTITY("entity", 9, true), SHADE("shade", 200, true), SUN("sun", 200, true);

		String name;
		double reductionAmount;
		boolean usingExact;
		private TempType(String name, double reductionAmountIn, boolean usingExactIn) {
			this.reductionAmount = reductionAmountIn;
			this.usingExact = usingExactIn;
			this.name = name;
		}
		public String getName() {
			return name;
		}
		public double getReductionAmount() {
			return reductionAmount;
		}

		public boolean isUsingExact() {
			return usingExact;
		}
	}

	public static double getBlendedTemperature(Level world, BlockPos mainPos, BlockPos blendPos, TempType type) {
		float distance = (float) Math.sqrt(mainPos.distSqr(blendPos));// 2 - 10 - 0
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

	public static float getAverageTemperature(Level world, BlockPos pos, TempType type, int rangeInBlocks, TempMode mode) {
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

	@SuppressWarnings("resource")
	@SubscribeEvent
	public static void interactWithWaterSourceBlock(PlayerInteractEvent.RightClickEmpty event) {
		HitResult raytraceresult = rayTrace(event.getWorld(), event.getEntityLiving(), ClipContext.Fluid.SOURCE_ONLY);
		BlockPos blockpos = ((BlockHitResult)raytraceresult).getBlockPos();
		if (event.getWorld().isClientSide && event.getItemStack().isEmpty() && event.getHand() == InteractionHand.MAIN_HAND) {
			//Source Block Of Water
			Fluid fluid = event.getWorld().getFluidState(blockpos).getType();
			if (DataMaps.Client.fluid.containsKey(fluid.getRegistryName())) {
				FluidJsonHolder fluidHolder = DataMaps.Client.fluid.get(fluid.getRegistryName());
				new ServerboundInteractWithWaterPacket(blockpos, fluidHolder.getThirstChance(), fluidHolder.getThirstAmount(), fluidHolder.getHydrationAmount(), event.getHand()).send();
			}
			//Air Block
			if (event.getWorld().isRainingAt(blockpos)) {
				new ServerboundInteractWithWaterPacket(event.getPos(), 0.0f, 1.0D, 0.5D, event.getHand()).send();
			}
		}
	}

	@SuppressWarnings("resource")
	@SubscribeEvent
	public static void interactWithWaterSourceBlock(PlayerInteractEvent.RightClickBlock event) {
		HitResult raytraceresult = rayTrace(event.getWorld(), event.getEntityLiving(), ClipContext.Fluid.SOURCE_ONLY);
		BlockPos blockpos = ((BlockHitResult)raytraceresult).getBlockPos();
		BlockState state = event.getWorld().getBlockState(event.getPos());
		Fluid fluid = event.getWorld().getFluidState(blockpos).getType();
		BlockState stateUnder = event.getWorld().getBlockState(event.getPos().below());
		if (event.getWorld().isClientSide && event.getItemStack().isEmpty()) {
			//Source Block Of Water
			if (DataMaps.Client.fluid.containsKey(fluid.getRegistryName())) {
				FluidJsonHolder fluidHolder = DataMaps.Client.fluid.get(fluid.getRegistryName());
				event.setCanceled(true);
				event.setCancellationResult(InteractionResult.SUCCESS);
				new ServerboundInteractWithWaterPacket(blockpos, fluidHolder.getThirstChance(), fluidHolder.getThirstAmount(), fluidHolder.getHydrationAmount(), event.getHand()).send();
			}
			//Cauldron
			if (state.getBlock() == Blocks.WATER_CAULDRON) {
				int i = state.getValue(LayeredCauldronBlock.LEVEL);
				if (i > 0) {
					event.setCanceled(true);
					event.setCancellationResult(InteractionResult.SUCCESS);
					if (stateUnder.getBlock() == Blocks.CAMPFIRE && stateUnder.getValue(BlockStateProperties.LIT)) {
						new ServerboundInteractWithWaterPacket(event.getPos(), 0.0f, 4.0D, event.getHand()).send();
					}
					else {
						new ServerboundInteractWithWaterPacket(event.getPos(), 0.5f, 4.0D, event.getHand()).send();
					}
				}
			}
		}
	}

	protected static HitResult rayTrace(Level worldIn, LivingEntity player, ClipContext.Fluid fluidMode) {
		float f = player.getXRot();
		float f1 = player.getYRot();
		Vec3 vec3d = player.getEyePosition(1.0F);
		float f2 = Mth.cos(-f1 * ((float)Math.PI / 180F) - (float)Math.PI);
		float f3 = Mth.sin(-f1 * ((float)Math.PI / 180F) - (float)Math.PI);
		float f4 = -Mth.cos(-f * ((float)Math.PI / 180F));
		float f5 = Mth.sin(-f * ((float)Math.PI / 180F));
		float f6 = f3 * f4;
		float f7 = f2 * f4;
		double d0 = player.getAttribute(ForgeMod.REACH_DISTANCE.get()).getValue();
		Vec3 vec3d1 = vec3d.add((double)f6 * d0, (double)f5 * d0, (double)f7 * d0);
		return worldIn.clip(new ClipContext(vec3d, vec3d1, ClipContext.Block.OUTLINE, fluidMode, player));
	}

	@SubscribeEvent
	public static void restoreStats(PlayerEvent.Clone event) {
		SurviveEntityStats.getOrCreateModNBT(event.getPlayer());
		if (!event.isWasDeath()) {
			IRealisticEntity original = ((IRealisticEntity)event.getOriginal());
			SurviveEntityStats.setNutritionStats(event.getPlayer(), SurviveEntityStats.getNutritionStats(event.getOriginal()));
			SurviveEntityStats.setWellbeingStats(event.getPlayer(), SurviveEntityStats.getWellbeingStats(event.getOriginal()));
			SurviveEntityStats.setHygieneStats(event.getPlayer(), SurviveEntityStats.getHygieneStats(event.getOriginal()));
			SurviveEntityStats.setWaterStats(event.getPlayer(), original.getWaterData());
			SurviveEntityStats.setStaminaStats(event.getPlayer(), SurviveEntityStats.getEnergyStats(event.getOriginal()));
			SurviveEntityStats.setTemperatureStats(event.getPlayer(), SurviveEntityStats.getTemperatureStats(event.getOriginal()));
			SurviveEntityStats.setSleepStats(event.getPlayer(), SurviveEntityStats.getSleepStats(event.getOriginal()));
			SurviveEntityStats.setWetTime(event.getPlayer(), SurviveEntityStats.getWetTime(event.getOriginal()));
		}
	}

	@SubscribeEvent
	public static void addReload(WorldEvent.Load event) {
		System.out.println("Resistering Temperature Queries");
		//Environment
		for (TempType type : TempType.values()) {
			TemperatureQuery.registerQuery("survive:"+type.getName(), ContributingFactor.ENVIRONMENTAL, (player, temp, level, pos)-> {
				double temperature;
				if (type.isUsingExact()) {
					temperature = getExactTemperature(level, pos, type);
				} else {
					temperature = getAverageTemperature(level, pos, type, 5, Survive.TEMPERATURE_CONFIG.tempMode);
				}
				return UnionMathHelper.roundDecimal(3, (temperature)/type.getReductionAmount());
			});
		}
		TemperatureQuery.registerQuery("survive:snow", ContributingFactor.ENVIRONMENTAL, (player, temp, level, pos)-> {
			double snow = 0.0D;
			if (isSnowingAt(level, pos)) {
				snow = -2.0D;
			}
			return snow;
		});
		TemperatureQuery.registerQuery("survive:season", ContributingFactor.ENVIRONMENTAL, (player, temp, level, pos)-> {
			float seasonMod = 0;
			if (ModHelper.isSereneSeasonsLoaded()) {
				Season season = SereneSeasonsCompat.modifyTemperatureBySeason(level, pos);
				if (DataMaps.Server.biomeTemperature.containsKey(level.getBiome(pos).value().getRegistryName())) {
					seasonMod = DataMaps.Server.biomeTemperature.get(level.getBiome(pos).value().getRegistryName()).getSeasonModifiers().get(season);
				} else {
					seasonMod = season.getModifier();
				}
				if (ModHelper.isPrimalWinterLoaded()) {
					seasonMod = -1.0F;
				}
			}
			return seasonMod;
		});
		TemperatureQuery.registerQuery("survive:dimension", ContributingFactor.ENVIRONMENTAL, (player, temp, level, pos)->{
			for (String dimensionList : ServerConfig.dimensionModifiers) {
				String[] dimension = dimensionList.split(",");
				ResourceLocation loc = new ResourceLocation(dimension[0]);
				if (RegistryHelper.matchesRegistryKey(loc, level.dimension())) {
					return Float.parseFloat(dimension[1]);
				}
			}
			return 0;
		});
		//Internal
		TemperatureQuery.registerQuery("survive:wetness", ContributingFactor.INTERNAL, (player, temp, level, pos)->{
			if (DataMaps.Server.biomeTemperature.containsKey(level.getBiome(pos).value().getRegistryName())) {
				float f = DataMaps.Server.biomeTemperature.get(level.getBiome(pos).value().getRegistryName()).getWetnessModifier();
				return ((double)(SurviveEntityStats.getWetTime(player)) / -1800.0D) * f;
			} else {
				return (double)(SurviveEntityStats.getWetTime(player)) / -1800.0D;
			}
		});
		TemperatureQuery.registerQuery("survive:cooling_enchantment", ContributingFactor.INTERNAL, (player, temp, level, pos)->{
			double coolingMod = 0.0D;
			coolingMod -= 0.05D * (float)SEnchantmentHelper.getCoolingModifier(player.getItemBySlot(EquipmentSlot.HEAD));
			coolingMod -= 0.16D * (float)SEnchantmentHelper.getCoolingModifier(player.getItemBySlot(EquipmentSlot.CHEST));
			coolingMod -= 0.13D * (float)SEnchantmentHelper.getCoolingModifier(player.getItemBySlot(EquipmentSlot.LEGS));
			coolingMod -= 0.06D * (float)SEnchantmentHelper.getCoolingModifier(player.getItemBySlot(EquipmentSlot.FEET));
			return coolingMod;
		});
		TemperatureQuery.registerQuery("survive:warming_enchantment", ContributingFactor.INTERNAL, (player, temp, level, pos)->{
			double warmingMod = 0.0D;
			warmingMod += 0.05D * (float)SEnchantmentHelper.getWarmingModifier(player.getItemBySlot(EquipmentSlot.HEAD));
			warmingMod += 0.16D * (float)SEnchantmentHelper.getWarmingModifier(player.getItemBySlot(EquipmentSlot.CHEST));
			warmingMod += 0.13D * (float)SEnchantmentHelper.getWarmingModifier(player.getItemBySlot(EquipmentSlot.LEGS));
			warmingMod += 0.06D * (float)SEnchantmentHelper.getWarmingModifier(player.getItemBySlot(EquipmentSlot.FEET));
			return warmingMod;
		});
		TemperatureQuery.registerQuery("survive:adjusted_cooling_enchantment", ContributingFactor.INTERNAL, (player, temp, level, pos)->{
			boolean shouldCool = false;
			if (temp > Survive.DEFAULT_TEMP) {
				for (EquipmentSlot types : EquipmentSlot.values()) {
					if (SEnchantmentHelper.hasAdjustedCooling(player.getItemBySlot(types))) {
						shouldCool = true;
					}
				}
			}
			return shouldCool?-2.0D:0.0D;
		});
		TemperatureQuery.registerQuery("survive:adjusted_warming_enchantment", ContributingFactor.INTERNAL, (player, temp, level, pos)->{
			boolean shouldWarm = false;
			if (temp < Survive.DEFAULT_TEMP) {
				for (EquipmentSlot types : EquipmentSlot.values()) {
					if (SEnchantmentHelper.hasAdjustedWarming(player.getItemBySlot(types))) {
						shouldWarm = true;
					}
				}
			}
			return shouldWarm?2.0D:0.0D;
		});
		TemperatureQuery.registerQuery("survive:armor", ContributingFactor.INTERNAL, (player, temp, level, pos)->{
			double armorMod = 0.0D;
			for (EquipmentSlot slot : EquipmentSlot.values()) {
				if (slot.getType() == Type.ARMOR) {
					if (!player.getItemBySlot(slot).isEmpty()) {
						Item armor = player.getItemBySlot(slot).getItem();
						float modifier = 1.0F;
						if (DataMaps.Server.armor.containsKey(armor.getRegistryName())) {
							for (Pair<String,TemperatureChangeInstance> instance : DataMaps.Server.armor.get(armor.getRegistryName()).getTemperatureModifier()) {
								if (instance.getSecond().shouldChangeTemperature(player)) {
									modifier = instance.getSecond().getTemperature();
									break;
								}
							}
						}
						armorMod += getModifierFromSlot(slot) * modifier;
					}
				}
			}
			return armorMod;
		});
		TemperatureQuery.registerQuery("survive:chilled_effect", ContributingFactor.INTERNAL, (player, temp, level, pos)->{
			if (player.hasEffect(SMobEffects.CHILLED))
				return -(0.05F * (float)(player.getEffect(SMobEffects.CHILLED).getAmplifier() + 1));
			else
				return 0;
		});
		TemperatureQuery.registerQuery("survive:heated_effect", ContributingFactor.INTERNAL, (player, temp, level, pos)->{
			if (player.hasEffect(SMobEffects.HEATED))
				return +(0.05F * (float)(player.getEffect(SMobEffects.HEATED).getAmplifier() + 1));
			else
				return 0;
		});
	}
}
