package com.stereowalker.survive.util;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

public class TemperatureModifier {
	private ResourceLocation id;
	private double mod;
	
	public TemperatureModifier() {
	}
	
	public TemperatureModifier(ResourceLocation id, double mod) {
		this.id = id;
		this.mod = mod;
	}
	
	public double getMod() {
		return mod;
	}
	
	public ResourceLocation getId() {
		return id;
	}
	
	public CompoundNBT write(CompoundNBT nbt) {
		nbt.putString("id", this.id.toString());
		nbt.putDouble("mod", this.mod);
		return nbt;
	}
	
	public void read(CompoundNBT nbt) {
		this.id = new ResourceLocation(nbt.getString("id"));
		this.mod = nbt.getDouble("mod");
	}
	
	/**
	 * It's more advisable to use the method from {@linkTemperatureStats}
	 * @param modifier
	 */
	protected void setMod(double mod) {
		this.mod = roundDecimal(3, mod);
	}

	public static double roundDecimal(int i, double value) {
		int modInt = (int) (value*(Math.pow(i, 10)));
		return modInt / (Math.pow(i, 10));
	}
	
	@Override
	public String toString() {
		return "{id: "+id+", modifier: "+mod+"}";
	}
}
