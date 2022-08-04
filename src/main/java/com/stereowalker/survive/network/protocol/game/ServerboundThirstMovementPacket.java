package com.stereowalker.survive.network.protocol.game;

import com.stereowalker.survive.Survive;
import com.stereowalker.survive.needs.IRealisticEntity;
import com.stereowalker.unionlib.network.protocol.game.ServerboundUnionPacket;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

public class ServerboundThirstMovementPacket extends ServerboundUnionPacket {
	private float moveF;
	private float moveS;
	private boolean jump;

	public ServerboundThirstMovementPacket(final float moveF, final float moveS, final boolean jump) {
		super(Survive.getInstance().channel);
		this.moveF = moveF;
		this.moveS = moveS;
		this.jump = jump;
	}

	public ServerboundThirstMovementPacket(FriendlyByteBuf byteBuf) {
		super(byteBuf, Survive.getInstance().channel);
		this.moveF = byteBuf.readFloat();
		this.moveS = byteBuf.readFloat();
		this.jump = byteBuf.readBoolean();
	}

	@Override
	public void encode(final FriendlyByteBuf byteBuf) {
		byteBuf.writeFloat(this.moveF);
		byteBuf.writeFloat(this.moveS);
		byteBuf.writeBoolean(this.jump);
	}

	@Override
	public boolean handleOnServer(ServerPlayer sender) {
		if (Survive.THIRST_CONFIG.enabled) {
			int movM = (int) ((moveS+moveF)*10);
			float moveMul;
			if (movM > 0)
				moveMul = 1.0F;
			else
				moveMul = 0.5F;

			if (sender.isSprinting())
				moveMul+=2.0F;
			if (sender.isCrouching())
				moveMul+=0.5F;
			if (jump)
				moveMul+=1.5F;
			((IRealisticEntity)sender).getWaterData().addExhaustion(sender, 0.1F*moveMul);
		}
		return true;
	}
}
