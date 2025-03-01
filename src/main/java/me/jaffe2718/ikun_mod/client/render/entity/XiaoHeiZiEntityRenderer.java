package me.jaffe2718.ikun_mod.client.render.entity;

import me.jaffe2718.ikun_mod.IKunMod;
import me.jaffe2718.ikun_mod.entity.XiaoHeiZiEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class XiaoHeiZiEntityRenderer extends EntityRenderer<XiaoHeiZiEntity, EntityRenderState> {

    private static final Identifier TEXTURE = IKunMod.id("textures/entity/xiao_hei_zi.png");
    private static final RenderLayer LAYER = RenderLayer.getEntityCutoutNoCull(TEXTURE);


    public XiaoHeiZiEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public void render(EntityRenderState state, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        matrices.push();
        matrices.scale(2.0F, 2.0F, 2.0F);
        matrices.multiply(this.dispatcher.getRotation());
        MatrixStack.Entry entry = matrices.peek();
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(LAYER);
        produceVertex(vertexConsumer, entry, light, 0.3F, 0.3F, 0, 1);
        produceVertex(vertexConsumer, entry, light, 0.7F, 0.3F, 1, 1);
        produceVertex(vertexConsumer, entry, light, 0.7F, 0.7F, 1, 0);
        produceVertex(vertexConsumer, entry, light, 0.3F, 0.7F, 0, 0);
        matrices.pop();
        super.render(state, matrices, vertexConsumers, light);
    }

    private static void produceVertex(VertexConsumer vertexConsumer, MatrixStack.Entry matrix, int light, float x, float z, int textureU, int textureV) {
        vertexConsumer.vertex(matrix, x - 0.5F, z - 0.25F, 0.0F).color(-1).texture((float)textureU, (float)textureV).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(matrix, 0.0F, 1.0F, 0.0F);
    }


    @Override
    public EntityRenderState createRenderState() {
        return new EntityRenderState();
    }
}
