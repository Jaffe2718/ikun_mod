package me.jaffe2718.ikun_mod.client;

import me.jaffe2718.ikun_mod.client.render.entity.ChickenKunEntityRenderer;
import me.jaffe2718.ikun_mod.client.render.entity.XiaoHeiZiEntityRenderer;
import me.jaffe2718.ikun_mod.client.render.block.ChickenCoopBlockEntityRenderer;
import me.jaffe2718.ikun_mod.unit.BlockRegistry;
import me.jaffe2718.ikun_mod.unit.EntityRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;

public class IKunModClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(EntityRegistry.CHICKEN_KUN, ChickenKunEntityRenderer::new);
        EntityRendererRegistry.register(EntityRegistry.XIAO_HEI_ZI, XiaoHeiZiEntityRenderer::new);
        BlockEntityRendererFactories.register(BlockRegistry.CHICKEN_COOP_BLOCK_ENTITY, ChickenCoopBlockEntityRenderer::new);
    }
}
