package com.stereowalker.survive.world.item.component;

import java.util.UUID;
import java.util.function.UnaryOperator;

import com.stereowalker.survive.FoodUtils;
import com.stereowalker.survive.Survive;
import com.stereowalker.unionlib.core.registries.RegistryHolder;
import com.stereowalker.unionlib.core.registries.RegistryObject;
import com.stereowalker.unionlib.util.VersionHelper;

import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;

@RegistryHolder(namespace = Survive.MOD_ID)
public class SDataComponents {
	@RegistryObject("status_owner")
	public static final DataComponentType<UUID> STATUS_OWNER = register(
	        type -> type.persistent(UUIDUtil.CODEC).networkSynchronized(UUIDUtil.STREAM_CODEC).cacheEncoding()
	);
	public static final VersionHelper.Data<UUID> STATUS_OWNER_D = new VersionHelper.Data<UUID>(
			(stack) -> stack.has(STATUS_OWNER),
			(stack) -> stack.get(STATUS_OWNER),
			(stack, dat) -> stack.set(STATUS_OWNER, dat),
			(stack) -> stack.remove(STATUS_OWNER));
	@RegistryObject("drinks_left")
	public static final DataComponentType<Integer> DRINKS_LEFT = register(
			type -> type.persistent(ExtraCodecs.NON_NEGATIVE_INT).networkSynchronized(ByteBufCodecs.VAR_INT)
			);
	public static final VersionHelper.Data<Integer> DRINKS_LEFT_D = new VersionHelper.Data<Integer>(
			(stack) -> stack.has(DRINKS_LEFT),
			(stack) -> stack.get(DRINKS_LEFT),
			(stack, dat) -> stack.set(DRINKS_LEFT, dat),
			(stack) -> stack.remove(DRINKS_LEFT));
	@RegistryObject("soap_left")
	public static final DataComponentType<Integer> SOAP_LEFT = register(
			type -> type.persistent(ExtraCodecs.NON_NEGATIVE_INT).networkSynchronized(ByteBufCodecs.VAR_INT)
			);
	public static final VersionHelper.Data<Integer> SOAP_LEFT_D = new VersionHelper.Data<Integer>(
			(stack) -> stack.has(SOAP_LEFT),
			(stack) -> stack.get(SOAP_LEFT),
			(stack, dat) -> stack.set(SOAP_LEFT, dat),
			(stack) -> stack.remove(SOAP_LEFT));
	@RegistryObject("food_status")
	public static final DataComponentType<FoodUtils.FoodStatus> FOOD_STATUS = register(
			type -> type.persistent(FoodUtils.FoodStatus.CODEC).networkSynchronized(FoodUtils.FoodStatus.STREAM_CODEC)
			);
	public static final VersionHelper.Data<FoodUtils.FoodStatus> FOOD_STATUS_D = new VersionHelper.Data<FoodUtils.FoodStatus>(
			(stack) -> stack.has(FOOD_STATUS),
			(stack) -> stack.get(FOOD_STATUS),
			(stack, dat) -> stack.set(FOOD_STATUS, dat),
			(stack) -> stack.remove(FOOD_STATUS));
	@RegistryObject("biome_source")
	public static final DataComponentType<ResourceLocation> BIOME_SOURCE = register(
			p_333150_ -> p_333150_.persistent(ResourceLocation.CODEC).networkSynchronized(ResourceLocation.STREAM_CODEC)
			);
	public static final VersionHelper.Data<ResourceLocation> BIOME_SOURCE_D = new VersionHelper.Data<ResourceLocation>(
			(stack) -> stack.has(BIOME_SOURCE),
			(stack) -> stack.get(BIOME_SOURCE),
			(stack, dat) -> stack.set(BIOME_SOURCE, dat),
			(stack) -> stack.remove(BIOME_SOURCE));
    private static <T> DataComponentType<T> register(UnaryOperator<DataComponentType.Builder<T>> pBuilder) {
        return pBuilder.apply(DataComponentType.builder()).build();
    }
}
