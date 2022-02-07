package com.stereowalker.survive.network.protocol.game;

import java.util.UUID;
import java.util.function.Supplier;

import com.stereowalker.survive.Survive;
import com.stereowalker.survive.core.SurviveEntityStats;
import com.stereowalker.survive.needs.WaterData;
import com.stereowalker.survive.world.item.CanteenItem;
import com.stereowalker.survive.world.item.SItems;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.WaterFluid;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

public class ServerboundInteractWithWaterPacket {
	private BlockPos pos;
	private boolean addThirst;
	private double waterAmount;
	private double hydrationAmount;
	private InteractionHand hand;
	private UUID uuid;

	public ServerboundInteractWithWaterPacket(final BlockPos pos, final boolean addThirst, final double waterAmount, final double hydrationAmount, final InteractionHand hand, final UUID uuid) {
		this.pos = pos;
		this.uuid = uuid;
		this.addThirst = addThirst;
		this.hand = hand;
		this.waterAmount = waterAmount;
		this.hydrationAmount = hydrationAmount;
	}

	public ServerboundInteractWithWaterPacket(final BlockPos pos, final boolean addThirst, final double waterAmount, final InteractionHand hand, final UUID uuid) {
		this(pos, addThirst, waterAmount, 0, hand, uuid);
	}

	public static void encode(final ServerboundInteractWithWaterPacket msg, final FriendlyByteBuf packetBuffer) {
		packetBuffer.writeBlockPos(msg.pos);
		packetBuffer.writeBoolean(msg.addThirst);
		packetBuffer.writeDouble(msg.waterAmount);
		packetBuffer.writeDouble(msg.hydrationAmount);
		packetBuffer.writeEnum(msg.hand);
		packetBuffer.writeLong(msg.uuid.getMostSignificantBits());
		packetBuffer.writeLong(msg.uuid.getLeastSignificantBits());
	}

	public static ServerboundInteractWithWaterPacket decode(final FriendlyByteBuf packetBuffer) {
		return new ServerboundInteractWithWaterPacket(packetBuffer.readBlockPos(), packetBuffer.readBoolean(), packetBuffer.readDouble(), packetBuffer.readDouble(), packetBuffer.readEnum(InteractionHand.class), new UUID(packetBuffer.readLong(), packetBuffer.readLong()));
	}
	
	public static boolean canDrinkThis(FluidState fluid, boolean canDrinkFlowingIfInfinite) {
		if (fluid.isSource())
			return true; /* FlowingFluid.canSourcesMultiply */
		else if (fluid.getType() instanceof WaterFluid && canDrinkFlowingIfInfinite) return true;
		return false;
	}
	
	public static boolean shouldRemoveSource(FluidState fluid) {
		if ((!(fluid.getType() instanceof WaterFluid) || Survive.THIRST_CONFIG.shouldRemoveSourceWaterBlock) && fluid.isSource()) return true;
		return false;
	}

	public static void handle(final ServerboundInteractWithWaterPacket msg, final Supplier<NetworkEvent.Context> contextSupplier) {
		final NetworkEvent.Context context = contextSupplier.get();
		context.enqueueWork(() -> {
			final ServerPlayer sender = context.getSender();
			if (sender == null) {
				return;
			}
			final BlockPos pos = msg.pos;
			final boolean addThirst = msg.addThirst;
			final ItemStack heldItem = sender.getItemInHand(msg.hand);
			final double waterAmount = msg.waterAmount;
			final double hydrationAmount = msg.hydrationAmount;
			BlockState block = sender.level.getBlockState(pos);
			FluidState fluid = sender.level.getFluidState(pos);
			final UUID uuid = msg.uuid;
			if (uuid.equals(Player.createPlayerUUID(sender.getGameProfile()))) {
				if (Survive.THIRST_CONFIG.enabled) {
					if (heldItem.isEmpty()) {
						if (sender.isCrouching()) {
							WaterData waterStats = SurviveEntityStats.getWaterStats(sender);
							if (waterStats.needWater()) {
								boolean flag = false;
								if (block.getBlock() == Blocks.WATER_CAULDRON) {
									LayeredCauldronBlock.lowerFillLevel(block, sender.level, pos);
									flag = true;
								} else if (canDrinkThis(fluid, Survive.THIRST_CONFIG.drinkFromFlowingWater)) {
									if (shouldRemoveSource(fluid)) {
										sender.level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
									}
									flag = true;
								} else if (sender.level.isRainingAt(pos)) {
									flag = true;
								}
								if (flag) {
									waterStats.addStats((int) waterAmount, (float) hydrationAmount, addThirst ? WaterData.applyThirst(sender, 0.5f/*TODO MAKE BIOMES HAVE DIFFERENT THIRST CHANCES*/) : false);
								}
								Survive.getInstance().channel.sendTo(new ClientboundDrinkSoundPacket(pos, sender.getUUID()), sender.connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
							}
							waterStats.save(sender);
						}
					} else if (heldItem.getItem() == SItems.CANTEEN) {
						if (isValidContainerSource(waterAmount)) {
							int i = Survive.THIRST_CONFIG.canteen_fill_amount;
							heldItem.shrink(1);
							if (addThirst) sender.addItem(CanteenItem.addPropertiesToCanteen(new ItemStack(SItems.WATER_CANTEEN), i));
							else sender.addItem(CanteenItem.addPropertiesToCanteen(new ItemStack(SItems.PURIFIED_WATER_CANTEEN), i));
						}
					} else if (heldItem.getItem() == Items.GLASS_BOTTLE) {
						if (isValidContainerSource(waterAmount)) {
							if (!addThirst) {
								heldItem.shrink(1);
								sender.addItem(new ItemStack(SItems.PURIFIED_WATER_BOTTLE));
							}
						}
					} else if (heldItem.getItem() == Items.BOWL) {
						if (isValidContainerSource(waterAmount)) {
							heldItem.shrink(1);
							if (addThirst) sender.addItem(new ItemStack(SItems.WATER_BOWL));
							else sender.addItem(new ItemStack(SItems.PURIFIED_WATER_BOWL));
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
