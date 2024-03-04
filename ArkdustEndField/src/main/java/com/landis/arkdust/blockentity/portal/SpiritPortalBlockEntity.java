package com.landis.arkdust.blockentity.portal;

import com.landis.arkdust.registry.BlockEntityRegistry;
import com.landis.arkdust.registry.render.RenderTypeRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Matrix4f;

public class SpiritPortalBlockEntity extends BlockEntity {
    public SpiritPortalBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(BlockEntityRegistry.SPIRIT_PORTAL.get(), pPos, pBlockState);
    }

    @Override
    public void onLoad() {
        super.onLoad();
    }

    public static class Renderer implements BlockEntityRenderer<SpiritPortalBlockEntity> {
        public Renderer(BlockEntityRendererProvider.Context pContext){}

        @Override
        public void render(SpiritPortalBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
            Matrix4f m4f = pPoseStack.last().pose();
            this.renderFace(pBlockEntity, m4f, pBuffer.getBuffer(getRenderType()), 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, Direction.SOUTH);
            this.renderFace(pBlockEntity, m4f, pBuffer.getBuffer(getRenderType()), 0.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, Direction.NORTH);
            this.renderFace(pBlockEntity, m4f, pBuffer.getBuffer(getRenderType()), 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.0F, Direction.EAST);
            this.renderFace(pBlockEntity, m4f, pBuffer.getBuffer(getRenderType()), 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 0.0F, Direction.WEST);
            this.renderFace(pBlockEntity, m4f, pBuffer.getBuffer(getRenderType()), 0.0F, 1.0F, 0.0F,0.0F, 0.0F, 0.0F, 1.0F, 1.0F, Direction.DOWN);
            this.renderFace(pBlockEntity, m4f, pBuffer.getBuffer(getRenderType()), 0.0F, 1.0F, 1.0F,1.0F, 1.0F, 1.0F, 0.0F, 0.0F, Direction.UP);
        }

        private static RenderType getRenderType(){
            return RenderTypeRegistry.SPIRIT_PORTAL;
        };

        private void renderFace(
                SpiritPortalBlockEntity blockEntity, Matrix4f pPose, VertexConsumer pConsumer,
                float pX0, float pX1, float pY0, float pY1, float pZ0,
                float pZ1, float pZ2, float pZ3, Direction pDirection) {
            if (!blockEntity.level.getBlockState(blockEntity.worldPosition.relative(pDirection)).is(blockEntity.getBlockState().getBlock())) {
                pConsumer.vertex(pPose, pX0, pY0, pZ0).endVertex();
                pConsumer.vertex(pPose, pX1, pY0, pZ1).endVertex();
                pConsumer.vertex(pPose, pX1, pY1, pZ2).endVertex();
                pConsumer.vertex(pPose, pX0, pY1, pZ3).endVertex();
            }
        }
    }
}
