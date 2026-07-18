package com.stereowalker.survive.network.protocol.game;

import com.stereowalker.survive.Survive;
import com.stereowalker.unionlib.network.protocol.game.ClientboundUnionPacket;
import com.stereowalker.unionlib.util.VersionHelper;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;

public class ClientboundDrinkSoundPacket extends ClientboundUnionPacket {
	private BlockPos pos;

	public ClientboundDrinkSoundPacket(final BlockPos pos) {
		super(null);
		this.pos = pos;
	}

	public ClientboundDrinkSoundPacket(RegistryFriendlyByteBuf byteBuf) {
		super(byteBuf);
		this.pos = byteBuf.readBlockPos();
	}

	@Override
	public void encode(final FriendlyByteBuf byteBuf) {
		byteBuf.writeBlockPos(this.pos);
	}

	@Override
	public boolean runOnClient(Player sender) {
		sender.level().playLocalSound(this.pos.getX(), this.pos.getY(), this.pos.getZ(), SoundEvents.GENERIC_DRINK.value(), SoundSource.PLAYERS, 0.5F, sender.level().getRandom().nextFloat() * 0.1F + 0.9F, false);
		sender.swing(InteractionHand.MAIN_HAND);
		return true;
	}

	public static Identifier id = VersionHelper.toLoc(Survive.MOD_ID, "clientbound_drink_sound");
	@Override
	public Identifier id() {
		return id;
	}
}
