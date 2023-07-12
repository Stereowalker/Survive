package com.stereowalker.survive.world.temperature.conditions;

import com.google.gson.JsonObject;
import com.stereowalker.unionlib.util.RegistryHelper;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BiomeCondition extends TemperatureChangeCondition<BiomeCondition.Instance>{

	public BiomeCondition() {
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
	public Instance createInstance(CompoundTag nbt) {
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
		public boolean shouldChangeTemperature(Player player) {
			if (player.level().getBiome(player.blockPosition()).unwrapKey().isPresent())
				return RegistryHelper.matchesRegistryKey(this.biome, player.level().getBiome(player.blockPosition()).unwrapKey().get());
			else return false;
		}

		@Override
		public CompoundTag serialize() {
			CompoundTag nbt = new CompoundTag();
			nbt.putFloat("temperature", this.getTemperature());
			nbt.putString("biome", this.biome.toString());
			return nbt;
		}

		@Override
		@OnlyIn(Dist.CLIENT)
		public Component getAdditionalContext() {
			return Component.translatable("temperature_context.biome", this.biome);
		}
	}
}
