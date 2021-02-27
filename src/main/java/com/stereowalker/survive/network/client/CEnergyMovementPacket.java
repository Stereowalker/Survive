package com.stereowalker.survive.network.client;

import java.util.Random;

import com.stereowalker.survive.Survive;
import com.stereowalker.survive.config.Config;
import com.stereowalker.survive.entity.SurviveEntityStats;
import com.stereowalker.survive.events.SurviveEvents;
import com.stereowalker.survive.util.EnergyStats;
import com.stereowalker.unionlib.network.client.CUnionPacket;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;

public class CEnergyMovementPacket extends CUnionPacket {
	private float moveF;
	private float moveS;
	private boolean jump;

	public CEnergyMovementPacket(final float moveF, final float moveS, final boolean jump) {
		super(Survive.CHANNEL);
		this.moveF = moveF;
		this.moveS = moveS;
		this.jump = jump;
	}

	public CEnergyMovementPacket(PacketBuffer packetBuffer) {
		super(packetBuffer, Survive.CHANNEL);
		this.moveF = packetBuffer.readFloat();
		this.moveS = packetBuffer.readFloat();
		this.jump = packetBuffer.readBoolean();
	}

	@Override
	public void encode(final PacketBuffer packetBuffer) {
		packetBuffer.writeFloat(this.moveF);
		packetBuffer.writeFloat(this.moveS);
		packetBuffer.writeBoolean(this.jump);
	}

	@Override
	public boolean handleOnServer(ServerPlayerEntity sender) {
		Random rand = new Random();
		if (Config.enable_stamina) {
			EnergyStats stats = SurviveEntityStats.getEnergyStats(sender);
			int movM = (int) ((moveS+moveF)*10);
			float moveMul = 0;
			if (movM > 0 && sender.getRidingEntity() == null) {
				if (sender.isActualySwimming()) {
					moveMul += 0.05F;
				} else if (sender.isCrouching()) {
					moveMul += 0.1F;
				} else {
					moveMul += 0.2F;
				}
				if (sender.isSprinting()) {
					moveMul+=3.0F;
				}
			}
			if (jump) {
				moveMul+=1.0F;
			}

			int rgn = rand.nextInt(1);

			float maxWeight = 21.0F;

			if (SurviveEvents.getTotalArmorWeight(sender)/maxWeight > 1.0F) moveMul += SurviveEvents.getTotalArmorWeight(sender)/maxWeight - 1.0F;

			moveMul *= SurviveEvents.getTotalArmorWeight(sender)/maxWeight;

			if (moveMul > 0) stats.addExhaustion(sender, moveMul);
			else if (rgn == 0 && SurviveEvents.getTotalArmorWeight(sender)/maxWeight < 1.0F) stats.addStats(1);
			SurviveEntityStats.setEnergyStats(sender, stats);
		}
		return true;
	}
}
