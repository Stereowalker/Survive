package com.stereowalker.survive.world.item.enchantment;

import java.util.List;
import java.util.function.UnaryOperator;

import com.stereowalker.survive.Survive;
import com.stereowalker.unionlib.core.registries.RegistryHolder;
import com.stereowalker.unionlib.core.registries.RegistryObject;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.util.Unit;
import net.minecraft.world.item.enchantment.ConditionalEffect;
import net.minecraft.world.item.enchantment.effects.EnchantmentValueEffect;
import net.minecraft.world.level.storage.loot.Validatable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

@RegistryHolder(namespace = Survive.MOD_ID)
public class SEnchantmentEffectComponents {

	@RegistryObject("cooling")
    public static final DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>> COOLING = register(
	        type -> type.persistent(ConditionalEffect.codec(EnchantmentValueEffect.CODEC).listOf().validate(Validatable.listValidatorForContext(LootContextParamSets.ENCHANTED_DAMAGE)))
	);
	@RegistryObject("warming")
	public static final DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>> WARMING = register(
			type -> type.persistent(ConditionalEffect.codec(EnchantmentValueEffect.CODEC).listOf().validate(Validatable.listValidatorForContext(LootContextParamSets.ENCHANTED_DAMAGE)))
	);
	@RegistryObject("feathers")
	public static final DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>> FEATHERS = register(
			type -> type.persistent(ConditionalEffect.codec(EnchantmentValueEffect.CODEC).listOf().validate(Validatable.listValidatorForContext(LootContextParamSets.ENCHANTED_DAMAGE)))
	);
	@RegistryObject("auto_cooling")
	public static final DataComponentType<Unit> AUTO_COOLING = register(
	        type -> type.persistent(Unit.CODEC)
	);
	@RegistryObject("auto_warming")
	public static final DataComponentType<Unit> AUTO_WARMING = register(
	        type -> type.persistent(Unit.CODEC)
	);
	@RegistryObject("weightless")
	public static final DataComponentType<Unit> WEIGHTLESS = register(
	        type -> type.persistent(Unit.CODEC)
	);
	
    private static <T> DataComponentType<T> register(UnaryOperator<DataComponentType.Builder<T>> pBuilder) {
        return pBuilder.apply(DataComponentType.builder()).build();
    }
}
