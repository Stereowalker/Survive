package com.stereowalker.survive.network.protocol.game;

import com.stereowalker.survive.Survive;
import com.stereowalker.unionlib.network.protocol.game.ClientboundUnionPacket;
import com.stereowalker.unionlib.util.VersionHelper;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

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
	public boolean runOnClient(Player sender) {
		sender.level().playLocalSound(this.pos.getX(), this.pos.getY(), this.pos.getZ(), new ItemStack(Items.POTION).getDrinkingSound(), SoundSource.PLAYERS, 0.5F, sender.level().random.nextFloat() * 0.1F + 0.9F, false);
		sender.swing(InteractionHand.MAIN_HAND);
		return true;
	}

	public static ResourceLocation id = VersionHelper.toLoc(Survive.MOD_ID, "clientbound_drink_sound");
	@Override
	public ResourceLocation id() {
		return id;
	}
}
