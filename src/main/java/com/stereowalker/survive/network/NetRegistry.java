package com.stereowalker.survive.network;

import com.stereowalker.survive.Survive;
import com.stereowalker.survive.network.client.CEnergyMovementPacket;
import com.stereowalker.survive.network.client.CEnergyTaxPacket;
import com.stereowalker.survive.network.client.CInteractWithWaterPacket;
import com.stereowalker.survive.network.client.CThirstMovementPacket;
import com.stereowalker.survive.network.server.SDrinkSoundPacket;
import com.stereowalker.survive.network.server.SSurvivalStatsPacket;
import com.stereowalker.unionlib.network.PacketRegistry;


public class NetRegistry {
	public static void registerMessages() {
		int netID = -1;
		PacketRegistry.registerMessage(Survive.getInstance().CHANNEL, netID++, CEnergyMovementPacket.class, (packetBuffer) -> {return new CEnergyMovementPacket(packetBuffer);});
		Survive.getInstance().CHANNEL.registerMessage(netID++, SSurvivalStatsPacket.class, SSurvivalStatsPacket::encode, SSurvivalStatsPacket::decode, SSurvivalStatsPacket::handle);
		Survive.getInstance().CHANNEL.registerMessage(netID++, CThirstMovementPacket.class, CThirstMovementPacket::encode, CThirstMovementPacket::decode, CThirstMovementPacket::handle);
		Survive.getInstance().CHANNEL.registerMessage(netID++, CInteractWithWaterPacket.class, CInteractWithWaterPacket::encode, CInteractWithWaterPacket::decode, CInteractWithWaterPacket::handle);
		Survive.getInstance().CHANNEL.registerMessage(netID++, SDrinkSoundPacket.class, SDrinkSoundPacket::encode, SDrinkSoundPacket::decode, SDrinkSoundPacket::handle);
		Survive.getInstance().CHANNEL.registerMessage(netID++, CEnergyTaxPacket.class, CEnergyTaxPacket::encode, CEnergyTaxPacket::decode, CEnergyTaxPacket::handle);
	}
}
