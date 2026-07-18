package com.stereowalker.survive.compat;


import com.stereowalker.survive.Survive;
//import me.desht.pneumaticcraft.api.item.IItemRegistry;
//import me.desht.pneumaticcraft.common.pneumatic_armor.CommonUpgradeHandlers;
//import me.desht.pneumaticcraft.common.upgrades.ModUpgrades;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
//import me.desht.pneumaticcraft.common.pneumatic_armor.CommonArmorHandler;

import static java.lang.Math.abs;

public class PneumaticraftCompat {


	public static float getACMod(Player player, double temp, Level level, BlockPos pos, boolean applyTemp) {
        float mod = 0.0f;
//		CommonArmorHandler handler = CommonArmorHandler.getHandlerForPlayer(player);
//
//		if (CommonUpgradeHandlers.airConHandler == null) return 0;
//        if (!handler.isUpgradeEnabled(CommonUpgradeHandlers.airConHandler)
//                || !handler.isArmorReady(EquipmentSlot.CHEST)
//                || handler.getArmorPressure(EquipmentSlot.CHEST) < 0.1) {
//            return 0.0f;
//        }
//        int upgrades = handler.getUpgradeCount(EquipmentSlot.CHEST, ModUpgrades.AIR_CONDITIONING.get(), 4);
//        if (upgrades == 0) {
//            return 0.0f;
//        }
//
//        float targetTemp = 36.5f;
//        double deltaTemp = (targetTemp - temp);
//        if (abs(deltaTemp) < 0.1)
//            deltaTemp = 0;
//
//        deltaTemp *= (upgrades);
//      //  Survive.getInstance().getLogger().warn("The temp is {}", temp);
//
//        //int airUsage = (int) (deltaTemp * ConfigHandler.integration.tanAirConAirUsageMultiplier);
//        int airUsage = (int) (-1 * abs(deltaTemp * Survive.TEMPERATURE_CONFIG.airConAirUsageModifier));
//        mod = (float) (deltaTemp * Survive.TEMPERATURE_CONFIG.airConTempModifier);
//        handler.addAir(EquipmentSlot.CHEST, -abs(airUsage));
//        Survive.getInstance().debug(String.format("The temp is %.2f and the modifier is %.2f and the air usage is %d", temp, mod, airUsage));



		return mod;
	}
}
