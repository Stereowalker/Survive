package com.stereowalker.survive.world.item.component;

import java.awt.Component;
import java.util.UUID;
import java.util.function.UnaryOperator;

import com.stereowalker.survive.FoodUtils;
import com.stereowalker.survive.Survive;
import com.stereowalker.unionlib.core.registries.RegistryHolder;
import com.stereowalker.unionlib.core.registries.RegistryObject;
import com.stereowalker.unionlib.util.VersionHelper;

import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
//import net.minecraft.core.component.DataComponentType;
//import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;

@RegistryHolder(namespace = Survive.MOD_ID)
public class SDataComponents {
//	@RegistryObject("status_owner")
//	public static final DataComponentType<UUID> STATUS_OWNER = register(
//	        type -> type.persistent(UUIDUtil.CODEC).networkSynchronized(UUIDUtil.STREAM_CODEC).cacheEncoding()
//	);
	public static final VersionHelper.Data<UUID> STATUS_OWNER_D = new VersionHelper.Data<UUID>(
			(stack) -> stack.getTag() != null && stack.getTag().contains("status_owner"),
			(stack) -> stack.getTag().getUUID("status_owner"),
			(stack, dat) -> stack.getOrCreateTag().putUUID("status_owner", dat),
			(stack) -> stack.removeTagKey("status_owner"));
//	@RegistryObject("drinks_left")
//	public static final DataComponentType<Integer> DRINKS_LEFT = register(
//			type -> type.persistent(ExtraCodecs.NON_NEGATIVE_INT).networkSynchronized(ByteBufCodecs.VAR_INT)
//			);
	public static final VersionHelper.Data<Integer> DRINKS_LEFT_D = new VersionHelper.Data<Integer>(
			(stack) -> stack.getTag() != null && stack.getTag().contains("DrinksLeft"),
			(stack) -> stack.getTag().getInt("DrinksLeft"),
			(stack, dat) -> stack.getOrCreateTag().putInt("DrinksLeft", dat),
			(stack) -> stack.removeTagKey("DrinksLeft"));
//	@RegistryObject("soap_left")
//	public static final DataComponentType<Integer> SOAP_LEFT = register(
//			type -> type.persistent(ExtraCodecs.NON_NEGATIVE_INT).networkSynchronized(ByteBufCodecs.VAR_INT)
//			);
	public static final VersionHelper.Data<Integer> SOAP_LEFT_D = new VersionHelper.Data<Integer>(
			(stack) -> stack.getTag() != null && stack.getTag().contains("SoapLeft"),
			(stack) -> stack.getOrCreateTag().getInt("SoapLeft"),
			(stack, dat) -> stack.getOrCreateTag().putInt("SoapLeft", dat),
			(stack) -> stack.removeTagKey("SoapLeft"));
//	@RegistryObject("food_status")
//	public static final DataComponentType<FoodUtils.FoodStatus> FOOD_STATUS = register(
//			type -> type.persistent(FoodUtils.FoodStatus.CODEC).networkSynchronized(FoodUtils.FoodStatus.STREAM_CODEC)
//			);
	public static final VersionHelper.Data<FoodUtils.FoodStatus> FOOD_STATUS_D = new VersionHelper.Data<FoodUtils.FoodStatus>(
			(stack) -> stack.getTag() != null && stack.getTag().contains("food_status"),
			(stack) -> new FoodUtils.FoodStatus(stack.getTag().getCompound("food_status").getLong("creationTime"), stack.getTag().getCompound("food_status").getLong("lifespan"), stack.getTag().getCompound("food_status").getLong("saltDose")),
			(stack, dat) -> {
				CompoundTag tag = new CompoundTag();
				tag.putLong("creationTime", dat.creationTime());
				tag.putDouble("lifespan", dat.lifespan());
				tag.putDouble("lifespan", dat.lifespan());
				tag.putDouble("saltDose", dat.saltDose());
				stack.getOrCreateTag().put("food_status", tag);
			},
			(stack) -> stack.removeTagKey("food_status"));
//	@RegistryObject("biome_source")
//	public static final DataComponentType<ResourceLocation> BIOME_SOURCE = register(
//			p_333150_ -> p_333150_.persistent(ResourceLocation.CODEC).networkSynchronized(ResourceLocation.STREAM_CODEC)
//			);
	public static final VersionHelper.Data<ResourceLocation> BIOME_SOURCE_D = new VersionHelper.Data<ResourceLocation>(
			(stack) -> stack.getTag() != null && stack.getTag().contains("biome_source"),
			(stack) -> VersionHelper.toLoc(stack.getOrCreateTag().getString("biome_source")),
			(stack, dat) -> stack.getOrCreateTag().putString("biome_source", dat.toString()),
			(stack) -> stack.removeTagKey("biome_source"));
//    private static <T> DataComponentType<T> register(UnaryOperator<DataComponentType.Builder<T>> pBuilder) {
//        return pBuilder.apply(DataComponentType.builder()).build();
//    }
}
