package com.stereowalker.survive.network.client;

import java.util.UUID;
import java.util.function.Supplier;

import com.stereowalker.survive.Survive;
import com.stereowalker.survive.config.Config;
import com.stereowalker.survive.entity.SurviveEntityStats;
import com.stereowalker.survive.item.CanteenItem;
import com.stereowalker.survive.item.SItems;
import com.stereowalker.survive.network.server.SDrinkSoundPacket;
import com.stereowalker.survive.util.WaterStats;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CauldronBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.WaterFluid;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

public class CInteractWithWaterPacket {
	private BlockPos pos;
	private boolean addThirst;
	private double waterAmount;
	private double hydrationAmount;
	private Hand hand;
	private UUID uuid;

	public CInteractWithWaterPacket(final BlockPos pos, final boolean addThirst, final double waterAmount, final double hydrationAmount, final Hand hand, final UUID uuid) {
		this.pos = pos;
		this.uuid = uuid;
		this.addThirst = addThirst;
		this.hand = hand;
		this.waterAmount = waterAmount;
		this.hydrationAmount = hydrationAmount;
	}

	public CInteractWithWaterPacket(final BlockPos pos, final boolean addThirst, final double waterAmount, final Hand hand, final UUID uuid) {
		this(pos, addThirst, waterAmount, 0, hand, uuid);
	}

	public static void encode(final CInteractWithWaterPacket msg, final PacketBuffer packetBuffer) {
		packetBuffer.writeBlockPos(msg.pos);
		packetBuffer.writeBoolean(msg.addThirst);
		packetBuffer.writeDouble(msg.waterAmount);
		packetBuffer.writeDouble(msg.hydrationAmount);
		packetBuffer.writeEnumValue(msg.hand);
		packetBuffer.writeLong(msg.uuid.getMostSignificantBits());
		packetBuffer.writeLong(msg.uuid.getLeastSignificantBits());
	}

	public static CInteractWithWaterPacket decode(final PacketBuffer packetBuffer) {
		return new CInteractWithWaterPacket(packetBuffer.readBlockPos(), packetBuffer.readBoolean(), packetBuffer.readDouble(), packetBuffer.readDouble(), packetBuffer.readEnumValue(Hand.class), new UUID(packetBuffer.readLong(), packetBuffer.readLong()));
	}
	
	public static boolean canDrinkThis(FluidState fluid, boolean canDrinkFlowingIfInfinite) {
		if (fluid.isSource())
			return true; /* FlowingFluid.canSourcesMultiply */
		else if (fluid.getFluid() instanceof WaterFluid && canDrinkFlowingIfInfinite) return true;
		return false;
	}
	
	public static boolean shouldRemoveSource(FluidState fluid) {
		if ((!(fluid.getFluid() instanceof WaterFluid) || Config.shouldRemoveSourceWaterBlock) && fluid.isSource()) return true;
		return false;
	}

	public static void handle(final CInteractWithWaterPacket msg, final Supplier<NetworkEvent.Context> contextSupplier) {
		final NetworkEvent.Context context = contextSupplier.get();
		context.enqueueWork(() -> {
			final ServerPlayerEntity sender = context.getSender();
			if (sender == null) {
				return;
			}
			final BlockPos pos = msg.pos;
			final boolean addThirst = msg.addThirst;
			final ItemStack heldItem = sender.getHeldItem(msg.hand);
			final double waterAmount = msg.waterAmount;
			final double hydrationAmount = msg.hydrationAmount;
			BlockState block = sender.world.getBlockState(pos);
			FluidState fluid = sender.world.getFluidState(pos);
			final UUID uuid = msg.uuid;
			if (uuid.equals(PlayerEntity.getUUID(sender.getGameProfile()))) {
				if (Config.enable_thirst) {
					if (heldItem.isEmpty()) {
						if (sender.isCrouching()) {
							WaterStats waterStats = SurviveEntityStats.getWaterStats(sender);
							if (waterStats.needWater()) {
								boolean flag = false;
								if (addThirst)WaterStats.applyThirst(sender, 0.5f/*TODO MAKE BIOMES HAVE DIFFERENT THIRST CHANCES*/);
								if (block.getBlock() == Blocks.CAULDRON) {
									CauldronBlock cauldron = (CauldronBlock) block.getBlock();
									int i = block.get(CauldronBlock.LEVEL);
									cauldron.setWaterLevel(sender.world, pos, block, i - 1);
									flag = true;
								} else if (canDrinkThis(fluid, Config.drinkFromFlowingWater)) {
									if (shouldRemoveSource(fluid)) {
										sender.world.setBlockState(pos, Blocks.AIR.getDefaultState());
									}
									flag = true;
								} else if (sender.world.isRainingAt(pos)) {
									flag = true;
								}
								if (flag) {
									waterStats.addStats((int) waterAmount, (float) hydrationAmount);
								}
								Survive.CHANNEL.sendTo(new SDrinkSoundPacket(pos, sender.getUniqueID()), sender.connection.getNetworkManager(), NetworkDirection.PLAY_TO_CLIENT);
							}
							SurviveEntityStats.setWaterStats(sender, waterStats);
						}
					} else if (heldItem.getItem() == SItems.CANTEEN) {
						if (isValidContainerSource(waterAmount)) {
							int i = 3;
							heldItem.shrink(1);
							if (block.getBlock() == Blocks.CAULDRON) {
								CauldronBlock cauldron = (CauldronBlock) block.getBlock();
								i = block.get(CauldronBlock.LEVEL);
								cauldron.setWaterLevel(sender.world, pos, block, 0);
							}
							if (addThirst) sender.addItemStackToInventory(CanteenItem.addPropertiesToCanteen(new ItemStack(SItems.WATER_CANTEEN), i));
							else sender.addItemStackToInventory(CanteenItem.addPropertiesToCanteen(new ItemStack(SItems.PURIFIED_WATER_CANTEEN), i));
						}
					} else if (heldItem.getItem() == Items.GLASS_BOTTLE) {
						if (isValidContainerSource(waterAmount)) {
							if (!addThirst) {
								int i = 3;
								heldItem.shrink(1);
								if (block.getBlock() == Blocks.CAULDRON) {
									CauldronBlock cauldron = (CauldronBlock) block.getBlock();
									i = block.get(CauldronBlock.LEVEL);
									cauldron.setWaterLevel(sender.world, pos, block, i - 1);
								}
								sender.addItemStackToInventory(new ItemStack(SItems.PURIFIED_WATER_BOTTLE));
							}
						}
					} else if (heldItem.getItem() == Items.BOWL) {
						if (isValidContainerSource(waterAmount)) {
							int i = 3;
							heldItem.shrink(1);
							if (block.getBlock() == Blocks.CAULDRON) {
								CauldronBlock cauldron = (CauldronBlock) block.getBlock();
								i = block.get(CauldronBlock.LEVEL);
								cauldron.setWaterLevel(sender.world, pos, block, i - 1);
							}
							if (addThirst) sender.addItemStackToInventory(new ItemStack(SItems.WATER_BOWL));
							else sender.addItemStackToInventory(new ItemStack(SItems.PURIFIED_WATER_BOWL));
						}
					}
				}
			}
		});
		context.setPacketHandled(true);
	}

	public static boolean isValidContainerSource(double waterAmount) {
		return waterAmount >= 3.0D;
	}
}
