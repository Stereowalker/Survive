package com.stereowalker.survive.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.stereowalker.survive.world.level.block.entity.RealisticCampfireBlockEntity;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.CampfireBlock;

public class RealisticCampfireRenderer implements BlockEntityRenderer<RealisticCampfireBlockEntity> {
	private static final float SIZE = 0.375F;
    private final ItemRenderer itemRenderer;

    public RealisticCampfireRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = context.getItemRenderer();
    }

    public void render(RealisticCampfireBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        Direction direction = blockEntity.getBlockState().getValue(CampfireBlock.FACING);
        NonNullList<ItemStack> nonnulllist = blockEntity.getItems();
        int i = (int)blockEntity.getBlockPos().asLong();

        for (int j = 0; j < nonnulllist.size(); j++) {
            ItemStack itemstack = nonnulllist.get(j);
            if (itemstack != ItemStack.EMPTY) {
                poseStack.pushPose();
                poseStack.translate(0.5F, 0.44921875F, 0.5F);
                Direction direction1 = Direction.from2DDataValue((j + direction.get2DDataValue()) % 4);
                float f = -direction1.toYRot();
                poseStack.mulPose(Axis.YP.rotationDegrees(f));
                poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
                poseStack.translate(-0.3125F, -0.3125F, 0.0F);
                poseStack.scale(0.375F, 0.375F, 0.375F);
                this.itemRenderer.renderStatic(itemstack, ItemDisplayContext.FIXED, packedLight, packedOverlay, poseStack, bufferSource, blockEntity.getLevel(), i + j);
                poseStack.popPose();
            }
        }
    }
}
