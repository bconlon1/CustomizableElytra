package com.hidoni.customizableelytra.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import com.hidoni.customizableelytra.CustomizableElytra;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.File;

@Mod.EventBusSubscriber
public class Config
{
    private static final ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec config;

    public static ForgeConfigSpec.BooleanValue useLowQualityElytraBanners;
    public static ForgeConfigSpec.BooleanValue bannerBasePatternUsesCapeTexture;

    static
    {
        builder.comment("Customizable Elytra Common Config File");

        useLowQualityElytraBanners = builder
                .comment("If this is set to true, elytras will use lower quality textures for the banners (requires reload)")
                .define("items.elytras_use_low_quality_banners", false);

        bannerBasePatternUsesCapeTexture = builder
                .comment("If this is set to true, customizing an elytra with a banner will use your cape texture (if present) as the base pattern, instead of a plain base")
                .define("items.elytras_base_pattern_is_cape_texture", true);

        config = builder.build();
    }

    public static void loadConfig(ForgeConfigSpec config, String path)
    {
        CustomizableElytra.LOGGER.debug("Beginning config loading!");
        final CommentedFileConfig file = CommentedFileConfig.builder(new File(path)).sync().autosave().writingMode(WritingMode.REPLACE).preserveInsertionOrder().build();
        file.load();
        config.setConfig(file);
        CustomizableElytra.LOGGER.debug("Finished config loading!");
    }

    public static void init()
    {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.config);
        loadConfig(config, FMLPaths.CONFIGDIR.get().resolve(CustomizableElytra.MOD_ID + "-common.toml").toString());

    }
}
