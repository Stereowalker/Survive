//package com.stereowalker.survive.config;
//
//import java.io.File;
//
//import com.electronwill.nightconfig.core.file.CommentedFileConfig;
//import com.electronwill.nightconfig.core.io.WritingMode;
//import com.stereowalker.survive.Survive;
//
//import net.minecraftforge.common.ForgeConfigSpec;
//import net.minecraftforge.fml.ModLoadingContext;
//import net.minecraftforge.fml.common.Mod;
//import net.minecraftforge.fml.config.ModConfig;
//import net.minecraftforge.fml.loading.FMLPaths;
//
//@Mod.EventBusSubscriber
//public class OldConfigBuilder {
//	private static final ForgeConfigSpec.Builder client_builder = new ForgeConfigSpec.Builder();
//	public static final ForgeConfigSpec client_config;
//	
//	private static final ForgeConfigSpec.Builder common_builder = new ForgeConfigSpec.Builder();
//	public static final ForgeConfigSpec common_config;
//	
//	private static final ForgeConfigSpec.Builder server_builder = new ForgeConfigSpec.Builder();
//	public static final ForgeConfigSpec server_config;
//	
//	static {
//		OldConfig.init(server_builder, common_builder, client_builder);
//		server_config = server_builder.build();
//		client_config = client_builder.build();
//		common_config = common_builder.build();
//	}
//	
//	public static void loadConfig(ForgeConfigSpec config, String path) {
//		Survive.LOGGER.info("Loading config " + path);
//		final CommentedFileConfig file = CommentedFileConfig.builder(new File(path)).sync().autosave().writingMode(WritingMode.REPLACE).build();
//		Survive.LOGGER.info("Built config " + path);
//		file.load();
//		Survive.LOGGER.info("Loaded config " + path);
//		config.setConfig(file);
//	}
//	
//	public static void registerConfigs() {
//		ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, server_config, "old-survive.server.toml");
//		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, client_config, "old-survive.client.toml");
//		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, common_config, "old-survive.common.toml");
//	}
//	
//	public static void loadConfigs() {
//		loadConfig(client_config, FMLPaths.CONFIGDIR.get().resolve("old-survive.client.toml").toString());
//		loadConfig(server_config, FMLPaths.CONFIGDIR.get().resolve("old-survive.server.toml").toString());
//		loadConfig(common_config, FMLPaths.CONFIGDIR.get().resolve("old-survive.common.toml").toString());
//	}
//}
