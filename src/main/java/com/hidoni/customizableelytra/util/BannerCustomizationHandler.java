package com.hidoni.customizableelytra.util;

import com.hidoni.customizableelytra.config.Config;
import com.hidoni.customizableelytra.items.CustomizableElytraItem;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.model.AgeableModel;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.MissingTextureSprite;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.BannerPattern;
import net.minecraft.tileentity.BannerTileEntity;
import net.minecraft.util.ResourceLocation;

import java.util.List;

public class BannerCustomizationHandler extends CustomizationHandler
{
    private final List<Pair<BannerPattern, DyeColor>> patterns;

    public BannerCustomizationHandler(ItemStack itemIn)
    {
        this(itemIn.getOrCreateTag());
    }

    public BannerCustomizationHandler(CompoundNBT tagIn)
    {
        super(tagIn.getBoolean("HideCapePattern"));
        CompoundNBT blockEntityTag = tagIn.getCompound("BlockEntityTag");
        DyeColor baseColor = DyeColor.byId(blockEntityTag.getInt("Base"));
        ListNBT patternsList = blockEntityTag.getList("Patterns", 10).copy();
        this.patterns = BannerTileEntity.getPatternColorData(baseColor, patternsList);
    }

    @Override
    public int getColor(int index)
    {
        return patterns.get(0).getSecond().getColorValue();
    }

    @Override
    public <T extends LivingEntity, M extends AgeableModel<T>> void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, M renderModel, ResourceLocation textureLocation, boolean hasGlint)
    {
        renderModel.setRotationAngles(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        IVertexBuilder ivertexbuilder = ItemRenderer.getEntityGlintVertexBuilder(bufferIn, RenderType.getEntityNoOutline(textureLocation), false, hasGlint);
        renderModel.render(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        for (int i = 0; i < 17 && i < patterns.size(); ++i)
        {
            Pair<BannerPattern, DyeColor> pair = patterns.get(i);
            float[] afloat = pair.getSecond().getColorComponentValues();
            if (i == 0 && Config.bannerBasePatternUsesCapeTexture.get())
            {
                renderModel.render(matrixStackIn, ItemRenderer.getBuffer(bufferIn, RenderType.getEntityTranslucent(textureLocation), false, false), packedLightIn, OverlayTexture.NO_OVERLAY, afloat[0], afloat[1], afloat[2], 1.0F);
            }
            else
            {
                RenderMaterial rendermaterial = new RenderMaterial(AtlasTexture.LOCATION_BLOCKS_TEXTURE, CustomizableElytraItem.getTextureLocation(pair.getFirst()));
                if (rendermaterial.getSprite().getName() != MissingTextureSprite.getLocation()) // Don't render this banner pattern if it's missing, silently hide the pattern
                {
                    renderModel.render(matrixStackIn, rendermaterial.getBuffer(bufferIn, RenderType::getEntityTranslucent), packedLightIn, OverlayTexture.NO_OVERLAY, afloat[0], afloat[1], afloat[2], 1.0F);
                }
            }
        }
    }
}
