package com.stereowalker.survive.network.protocol.game;

import java.util.function.Function;

import com.stereowalker.survive.Survive;
import com.stereowalker.survive.needs.IRealisticEntity;
import com.stereowalker.unionlib.network.protocol.game.ServerboundUnionPacket;
import com.stereowalker.unionlib.util.VersionHelper;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Items;

public class ServerboundPlayerStatusBookPacket extends ServerboundUnionPacket {
	private CompoundTag book;
	private boolean celcius;
	private String sleepPage;
	private String tempPage;

	public ServerboundPlayerStatusBookPacket(final CompoundTag book, final boolean celcius, final String sleepPage, final String tempPage) {
		super(null);
		this.book = book;
		this.celcius = celcius;
		this.sleepPage = sleepPage;
		this.tempPage = tempPage;
	}

	public ServerboundPlayerStatusBookPacket(FriendlyByteBuf byteBuf) {
		super(byteBuf);
		this.book = byteBuf.readAnySizeNbt();
		this.celcius = byteBuf.readBoolean();
		this.sleepPage = byteBuf.readUtf();
		this.tempPage = byteBuf.readUtf();
	}

	@Override
	public void encode(final FriendlyByteBuf byteBuf) {
		byteBuf.writeNbt(this.book);
		byteBuf.writeBoolean(this.celcius);
		byteBuf.writeUtf(this.sleepPage);
		byteBuf.writeUtf(this.tempPage);
	}

	@Override
	public boolean handleOnServer(ServerPlayer sender) {
		if (sender.getItemInHand(InteractionHand.MAIN_HAND).getItem().equals(Items.WRITTEN_BOOK) && sender instanceof IRealisticEntity real) {
			CompoundTag compoundtag = this.book;
			ListTag contents = compoundtag.getList("pages", 8);
			int pages = 6;
			if (contents.size() < pages) while (contents.size() < pages) contents.add(contents.size(), (Tag)StringTag.valueOf(""));
			String status0 = "§2§nGeneral:§r\n"+
					"Water Level = "+real.waterData().getWaterLevel()+"\n"+
					"Hydration = "+real.waterData().getHydrationLevel()+"\n"+
					"Food Level = "+sender.getFoodData().getFoodLevel()+"\n"+
					"Saturation Level = "+sender.getFoodData().getSaturationLevel()+"\n"+
					"Energy Level = "+real.staminaData().getLTS();

			String status1 = "§2§nWellness:§r\n";
			if (real.wellbeingData().isWell())
				status1+= "No ilnesses dectected";
			else
				status1+= "Intensity = "+(real.wellbeingData().getIntensity()+1)+"\n"+
						"Reason = "+real.wellbeingData().getReason()+"\n";

			String status3 = "§2§nHygiene:§r\n";
			status3+= "Cleanliness level = "+(100 - real.hygieneData().getUncleanLevel())+"";

			String status4 = "§2§nNutrition:§r\n";
			status4+= "Carbohydrates = "+real.nutritionData().carbs().level()+"\n"+
						"Proteins = "+real.nutritionData().protein().level()+"\n"+
						"Fats = "+real.nutritionData().fat().level()+"\n";

			Function<String, Tag> ft = (s) -> (Tag)StringTag.valueOf("{\"text\":\""+s.replaceAll("\n", "\\\\n")+"\"}");
			contents.set(0, ft.apply(status0));
			contents.set(1, ft.apply(status1));
			contents.set(2, ft.apply(String.format(this.sleepPage, real.sleepData().getDaysAwake())));
			contents.set(3, ft.apply(status3));
			contents.set(4, ft.apply(status4));
			contents.set(5, ft.apply(String.format(this.tempPage, (!celcius ? (real.temperatureData().getFahrenheit()+" °F") : (real.temperatureData().getCelcius()+" °C")))));
			compoundtag.put("pages", contents);
			sender.getItemInHand(InteractionHand.MAIN_HAND).setTag(compoundtag);
		}
		return true;
	}
	
	public static ResourceLocation id = VersionHelper.toLoc(Survive.MOD_ID, "serverbound_player_status_book");
	@Override
	public ResourceLocation id() {
		return id;
	}
}
