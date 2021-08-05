package com.stereowalker.survive.temperature;

import com.google.gson.JsonObject;
import com.stereowalker.unionlib.util.RegistryHelper;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

public class BiomeNotCondition extends TemperatureChangeCondition<BiomeNotCondition.Instance>{

	public BiomeNotCondition() {
		super();
	}

	@Override
	public Instance createInstance(JsonObject object) {
		float temperatureIn = 0;
		String biomeIn = "";
		if(object.has("temperature") && object.get("temperature").isJsonPrimitive()) {
			temperatureIn = object.get("temperature").getAsFloat();
		}
		if(object.has("biome") && object.get("biome").isJsonPrimitive()) {
			biomeIn = object.get("biome").getAsString();
		}
		return new Instance(temperatureIn, new ResourceLocation(biomeIn));
	}
	
	@Override
	public Instance createInstance(CompoundNBT nbt) {
		float temperatureIn = nbt.getFloat("temperature");
		String biomeIn = nbt.getString("biome");
		return new Instance(temperatureIn, new ResourceLocation(biomeIn));
	}

	static class Instance extends TemperatureChangeInstance {
		private ResourceLocation biome;

		public Instance(float temperatureIn, ResourceLocation biomeIn) {
			super(temperatureIn);
			this.biome = biomeIn;
		}

		@Override
		public boolean shouldChangeTemperature(PlayerEntity player) {
			return !RegistryHelper.matchesRegistryKey(this.biome, player.world.func_242406_i(player.getPosition()).get());
		}
	}
}
