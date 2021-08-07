package com.stereowalker.survive.temperature;

import com.google.gson.JsonObject;
import com.stereowalker.unionlib.util.RegistryHelper;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
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
			return RegistryHelper.matchesRegistryKey(this.biome, player.world.func_242406_i(player.getPosition()).get());
		}

		@Override
		public CompoundNBT serialize() {
			CompoundNBT nbt = new CompoundNBT();
			nbt.putFloat("temperature", this.getTemperature());
			nbt.putString("biome", this.biome.toString());
			return nbt;
		}
		
		@Override
		@OnlyIn(Dist.CLIENT)
		public ITextComponent getAdditionalContext() {
			return new TranslationTextComponent("temperature_context.biome", this.biome);
		}
	}
}
