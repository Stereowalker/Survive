package com.stereowalker.survive.network.protocol.game;

import java.util.List;
import java.util.function.Function;

import com.google.common.collect.Lists;
import com.stereowalker.survive.Survive;
import com.stereowalker.survive.needs.IRealisticEntity;
import com.stereowalker.unionlib.network.protocol.game.ServerboundUnionPacket;
import com.stereowalker.unionlib.util.VersionHelper;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.Filterable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.WrittenBookContent;

public class ServerboundPlayerStatusBookPacket extends ServerboundUnionPacket {
	private WrittenBookContent book;
	private boolean celcius;
	private String sleepPage;
	private String tempPage;

	public ServerboundPlayerStatusBookPacket(final WrittenBookContent book, final boolean celcius, final String sleepPage, final String tempPage) {
		super(Survive.getInstance().channel);
		this.book = book;
		this.celcius = celcius;
		this.sleepPage = sleepPage;
		this.tempPage = tempPage;
	}

	public ServerboundPlayerStatusBookPacket(RegistryFriendlyByteBuf byteBuf) {
		super(byteBuf, Survive.getInstance().channel);
		this.book = WrittenBookContent.STREAM_CODEC.decode(byteBuf);
		this.celcius = byteBuf.readBoolean();
		this.sleepPage = byteBuf.readUtf();
		this.tempPage = byteBuf.readUtf();
	}

	@Override
	public void encode(final FriendlyByteBuf byteBuf) {
		WrittenBookContent.STREAM_CODEC.encode((RegistryFriendlyByteBuf)byteBuf, this.book);
		byteBuf.writeBoolean(this.celcius);
		byteBuf.writeUtf(this.sleepPage);
		byteBuf.writeUtf(this.tempPage);
	}

	@Override
	public boolean handleOnServer(ServerPlayer sender) {
		if (sender.getItemInHand(InteractionHand.MAIN_HAND).getItem().equals(Items.WRITTEN_BOOK) && sender instanceof IRealisticEntity real) {
			List<Filterable<Component>> contents = Lists.newArrayList();
			int pages = 6;
			if (contents.size() < pages) while (contents.size() < pages) contents.add(contents.size(), Filterable.passThrough(Component.literal("")));
			String status0 = "§2§nGeneral:§r\n"+
					"Water Level = "+real.getWaterData().getWaterLevel()+"\n"+
					"Hydration = "+real.getWaterData().getHydrationLevel()+"\n"+
					"Food Level = "+sender.getFoodData().getFoodLevel()+"\n"+
					"Saturation Level = "+sender.getFoodData().getSaturationLevel()+"\n"+
					"Energy Level = "+real.staminaData().getEnergyLevel();

			String status1 = "§2§nWellness:§r\n";
			if (real.wellbeingData().isWell())
				status1+= "No ilnesses dectected";
			else
				status1+= "Intensity = "+(real.wellbeingData().getIntensity()+1)+"\n"+
						"Reason = "+real.wellbeingData().getReason()+"\n";

			String status3 = "§2§nHygiene:§r\n";
			status3+= "Cleanliness level = "+(100 - real.hygieneData().getUncleanLevel())+"";

			String status4 = "§2§nNutrition:§r\n";
			status4+= "Carbohydrates = "+real.nutritionData().getCarbLevel()+"\n"+
					"Proteins = "+real.nutritionData().getProteinLevel()+"\n";

			Function<String, Filterable<Component>> ft = (s) -> Filterable.passThrough(Component.literal(s));
			contents.set(0, ft.apply(status0));
			contents.set(1, ft.apply(status1));
			contents.set(2, ft.apply(String.format(this.sleepPage, real.sleepData().getDaysAwake())));
			contents.set(3, ft.apply(status3));
			contents.set(4, ft.apply(status4));
			contents.set(5, ft.apply(String.format(this.tempPage, (!celcius ? (real.getTemperatureData().getFahrenheit()+" °F") : (real.getTemperatureData().getCelcius()+" °C")))));
			
			sender.getItemInHand(InteractionHand.MAIN_HAND).set(DataComponents.WRITTEN_BOOK_CONTENT, book.withReplacedPages(contents));
		}
		return true;
	}
	
	public static ResourceLocation id = VersionHelper.toLoc(Survive.MOD_ID, "serverbound_player_status_book");
	@Override
	public ResourceLocation id() {
		return id;
	}
}
