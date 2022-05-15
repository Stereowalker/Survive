package com.stereowalker.survive.network.protocol.game;

import com.stereowalker.survive.Survive;
import com.stereowalker.survive.core.SurviveEntityStats;
import com.stereowalker.survive.needs.WaterData;
import com.stereowalker.survive.world.item.SItems;
import com.stereowalker.unionlib.network.protocol.game.ServerboundUnionPacket;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.WaterFluid;
import net.minecraftforge.network.NetworkDirection;

public class ServerboundInteractWithWaterPacket extends ServerboundUnionPacket {
	private BlockPos pos;
	private boolean addThirst;
	private double waterAmount;
	private double hydrationAmount;
	private InteractionHand hand;

	public ServerboundInteractWithWaterPacket(final BlockPos pos, final boolean addThirst, final double waterAmount, final double hydrationAmount, final InteractionHand hand) {
		super(Survive.getInstance().channel);
		this.pos = pos;
		this.addThirst = addThirst;
		this.waterAmount = waterAmount;
		this.hydrationAmount = hydrationAmount;
		this.hand = hand;
	}
	
	public ServerboundInteractWithWaterPacket(final BlockPos pos, final boolean addThirst, final double waterAmount, final InteractionHand hand) {
		this(pos, addThirst, waterAmount, 0, hand);
	}

	public ServerboundInteractWithWaterPacket(FriendlyByteBuf packetBuffer) {
		super(packetBuffer, Survive.getInstance().channel);
		this.pos = packetBuffer.readBlockPos();
		this.addThirst = packetBuffer.readBoolean();
		this.hand = packetBuffer.readEnum(InteractionHand.class);
		this.waterAmount = packetBuffer.readDouble();
		this.hydrationAmount = packetBuffer.readDouble();
	}

	@Override
	public void encode(final FriendlyByteBuf byteBuf) {
		byteBuf.writeBlockPos(pos);
		byteBuf.writeBoolean(addThirst);
		byteBuf.writeEnum(hand);
		byteBuf.writeDouble(waterAmount);
		byteBuf.writeDouble(hydrationAmount);
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

	@Override
	public boolean handleOnServer(ServerPlayer sender) {
		if (Survive.THIRST_CONFIG.enabled) {
			final ItemStack heldItem = sender.getItemInHand(hand);
			BlockState block = sender.level.getBlockState(pos);
			FluidState fluid = sender.level.getFluidState(pos);
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
							waterStats.drink((int) waterAmount, (float) hydrationAmount, addThirst ? WaterData.applyThirst(sender, 0.5f/*TODO MAKE BIOMES HAVE DIFFERENT THIRST CHANCES*/) : false);
						}
						Survive.getInstance().channel.sendTo(new ClientboundDrinkSoundPacket(pos, sender.getUUID()), sender.connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
					}
					waterStats.save(sender);
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
		return true;
	}

	public static boolean isValidContainerSource(double waterAmount) {
		return waterAmount >= 3.0D;
	}
}
