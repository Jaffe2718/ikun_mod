package me.jaffe2718.ikun_mod.client.render.block;

import me.jaffe2718.ikun_mod.IKunMod;
import me.jaffe2718.ikun_mod.block.entity.ChickenCoopBlockEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animation.Animation;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class ChickenCoopBlockEntityRenderer extends GeoBlockRenderer<ChickenCoopBlockEntity> {

    public static final RawAnimation ACTIVE = RawAnimation.begin().then("animation.chicken_coop.active", Animation.LoopType.DEFAULT);
    public static final RawAnimation STATIC = RawAnimation.begin().then("animation.chicken_coop.static", Animation.LoopType.DEFAULT);

    public ChickenCoopBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        super(new DefaultedBlockGeoModel<>(IKunMod.id("chicken_coop")));
    }

    static class ChickenCoopBlockModel extends DefaultedBlockGeoModel<ChickenCoopBlockEntity> {

        public ChickenCoopBlockModel() {
            super(IKunMod.id("chicken_coop"));
        }

        @Override
        public @Nullable RenderLayer getRenderType(ChickenCoopBlockEntity animatable, Identifier texture) {
            return RenderLayer.getEntityTranslucent(texture);
        }
    }
}
