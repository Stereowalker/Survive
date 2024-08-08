package com.stereowalker.survive.world.item.component;

import java.util.UUID;
import java.util.function.UnaryOperator;

import com.stereowalker.survive.FoodUtils;
import com.stereowalker.survive.Survive;
import com.stereowalker.unionlib.core.registries.RegistryHolder;
import com.stereowalker.unionlib.core.registries.RegistryObject;

import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;

@RegistryHolder(namespace = Survive.MOD_ID, registry = DataComponentType.class)
public class SDataComponents {
	@RegistryObject("status_owner")
	public static final DataComponentType<UUID> STATUS_OWNER = register(
	        type -> type.persistent(UUIDUtil.CODEC).networkSynchronized(UUIDUtil.STREAM_CODEC).cacheEncoding()
	);
	@RegistryObject("drinks_left")
	public static final DataComponentType<Integer> DRINKS_LEFT = register(
			type -> type.persistent(ExtraCodecs.NON_NEGATIVE_INT).networkSynchronized(ByteBufCodecs.VAR_INT)
			);
	@RegistryObject("soap_left")
	public static final DataComponentType<Integer> SOAP_LEFT = register(
			type -> type.persistent(ExtraCodecs.NON_NEGATIVE_INT).networkSynchronized(ByteBufCodecs.VAR_INT)
			);
	@RegistryObject("expire_time")
	public static final DataComponentType<Long> EXPIRE_TIME = register(
			type -> type.persistent(Survive.NON_NEGATIVE_LONG).networkSynchronized(ByteBufCodecs.VAR_LONG)
			);
	@RegistryObject("food_status")
	public static final DataComponentType<FoodUtils.FoodStatus> FOOD_STATUS = register(
			type -> type.persistent(FoodUtils.FoodStatus.CODEC).networkSynchronized(FoodUtils.FoodStatus.STREAM_CODEC)
			);
	@RegistryObject("biome_source")
	public static final DataComponentType<ResourceLocation> BIOME_SOURCE = register(
			p_333150_ -> p_333150_.persistent(ResourceLocation.CODEC).networkSynchronized(ResourceLocation.STREAM_CODEC)
			);
    private static <T> DataComponentType<T> register(UnaryOperator<DataComponentType.Builder<T>> pBuilder) {
        return pBuilder.apply(DataComponentType.builder()).build();
    }
}
