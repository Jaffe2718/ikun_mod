package me.jaffe2718.ikun_mod.client.render.entity;

import me.jaffe2718.ikun_mod.IKunMod;
import me.jaffe2718.ikun_mod.entity.ChickenKunEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animation.Animation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.loading.math.MathParser;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.GeoRenderer;

@Environment(EnvType.CLIENT)
public class ChickenKunEntityRenderer extends GeoEntityRenderer<ChickenKunEntity> {

    public static final String HEAD_PITCH = "variable.head_pitch";

    public static final RawAnimation IDLE = RawAnimation.begin().then("animation.chicken_kun.idle", Animation.LoopType.DEFAULT);
    public static final RawAnimation WALK = RawAnimation.begin().then("animation.chicken_kun.walk", Animation.LoopType.DEFAULT);
    public static final RawAnimation RUN = RawAnimation.begin().then("animation.chicken_kun.run", Animation.LoopType.DEFAULT);
    public static final RawAnimation SUSHAN6 = RawAnimation.begin().then("animation.chicken_kun.sushan6", Animation.LoopType.DEFAULT);
    public static final RawAnimation KUNSHANKAO = RawAnimation.begin().then("animation.chicken_kun.kunshankao", Animation.LoopType.DEFAULT);
    public static final RawAnimation KUNJUMP = RawAnimation.begin().then("animation.chicken_kun.kunjump", Animation.LoopType.DEFAULT);
    public static final RawAnimation KUNYAO = RawAnimation.begin().then("animation.chicken_kun.kunyao", Animation.LoopType.DEFAULT);

    public ChickenKunEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new ChickenKunEntityModel());
    }

    static class ChickenKunEntityModel extends GeoModel<ChickenKunEntity> {
        private static final Identifier ANIMATION = IKunMod.id("animations/entity/chicken_kun.animation.json");
        private static final Identifier MODEL = IKunMod.id("geo/entity/chicken_kun.geo.json");
        private static final Identifier TEXTURE = IKunMod.id("textures/entity/chicken_kun.png");

        @Override
        public Identifier getModelResource(ChickenKunEntity chickenKunEntity, @Nullable GeoRenderer<ChickenKunEntity> geoRenderer) {
            return MODEL;
        }

        @Override
        public Identifier getTextureResource(ChickenKunEntity chickenKunEntity, @Nullable GeoRenderer<ChickenKunEntity> geoRenderer) {
            return TEXTURE;
        }

        @Override
        public Identifier getAnimationResource(ChickenKunEntity chickenKunEntity) {
            return ANIMATION;
        }

        @Override
        public void applyMolangQueries(AnimationState<ChickenKunEntity> animationState, double animTime) {
            super.applyMolangQueries(animationState, animTime);
            MathParser.setVariable(HEAD_PITCH, animationState.getAnimatable()::getPitch);

        }
    }
}
