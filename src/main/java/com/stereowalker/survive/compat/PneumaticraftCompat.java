package com.stereowalker.survive.compat;


import me.desht.pneumaticcraft.api.item.IItemRegistry;
import me.desht.pneumaticcraft.common.pneumatic_armor.CommonUpgradeHandlers;
import me.desht.pneumaticcraft.common.upgrades.ModUpgrades;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import me.desht.pneumaticcraft.common.pneumatic_armor.CommonArmorHandler;

public class PneumaticraftCompat {


	public static float getACMod(Player player, double temp, Level level, BlockPos pos, boolean applyTemp) {
        float mod = 0.0f;
		CommonArmorHandler handler = CommonArmorHandler.getHandlerForPlayer(player);

        if (!handler.isUpgradeEnabled(CommonUpgradeHandlers.airConHandler)
                || !handler.isArmorReady(EquipmentSlot.CHEST)
                || handler.getArmorPressure(EquipmentSlot.CHEST) < 0.1) {
            return 0.0f;
        }
        int upgrades = handler.getUpgradeCount(EquipmentSlot.CHEST, ModUpgrades.AIR_CONDITIONING.get(), 4);
        if (upgrades == 0) {
            return 0.0f;
        }

//        int targetTemp = initialTemperature.getRawValue();
//        int playerTemp = TemperatureHelper.getTemperatureData(player).getTemperature().getRawValue();
//        int deltaTemp = (TemperatureScale.getScaleMidpoint() - playerTemp);
//        if (Math.abs(deltaTemp) < 2)
//            deltaTemp = 0;
//        else if (Math.abs(deltaTemp) == 2)
//            deltaTemp /= 2;
//
//        deltaTemp *= upgrades;
//        targetTemp += deltaTemp;
//        if (deltaTemp != lastDelta.getOrDefault(player.getUniqueID(), 0)) {
//            NetworkHandler.sendToPlayer(new PacketPlayerTemperatureDelta(deltaTemp), (ServerPlayerEntity) player);
//            lastDelta.put(player.getUniqueID(), deltaTemp);
//        }
//
//        int airUsage = (int) (deltaTemp * ConfigHandler.integration.tanAirConAirUsageMultiplier);
        int airUsage = (int) (1.0 * upgrades);
        mod = -1.0f * upgrades;
        handler.addAir(EquipmentSlot.CHEST, -Math.abs(airUsage));



		return mod;
	}
}
