package com.stereowalker.survive.network.protocol.game;

import com.stereowalker.survive.Survive;
import com.stereowalker.unionlib.network.protocol.game.ClientboundUnionPacket;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ClientboundDrinkSoundPacket extends ClientboundUnionPacket {
	private BlockPos pos;

	public ClientboundDrinkSoundPacket(final BlockPos pos) {
		super(Survive.getInstance().channel);
		this.pos = pos;
	}

	public ClientboundDrinkSoundPacket(FriendlyByteBuf byteBuf) {
		super(byteBuf, Survive.getInstance().channel);
		this.pos = byteBuf.readBlockPos();
	}

	@Override
	public void encode(final FriendlyByteBuf byteBuf) {
		byteBuf.writeBlockPos(this.pos);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean handleOnClient(LocalPlayer sender) {
		Minecraft.getInstance().player.level.playLocalSound(this.pos.getX(), this.pos.getY(), this.pos.getZ(), new ItemStack(Items.POTION).getDrinkingSound(), SoundSource.PLAYERS, 0.5F, Minecraft.getInstance().player.level.random.nextFloat() * 0.1F + 0.9F, false);
		Minecraft.getInstance().player.swing(InteractionHand.MAIN_HAND);
		return true;
	}
}
