package me.jaffe2718.ikun_mod.mixin;

import me.jaffe2718.ikun_mod.IKunMod;
import me.jaffe2718.ikun_mod.unit.EffectRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {

    private static final Identifier CRUSH_TEXTURE = IKunMod.id("textures/misc/crush.png");

    @Shadow @Final private MinecraftClient client;

    @Inject(at = @At("TAIL"), method = "renderMiscOverlays")
    private void renderMiscOverlays(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        if (this.client.player instanceof ClientPlayerEntity clientPlayerEntity
                && clientPlayerEntity.hasStatusEffect(EffectRegistry.CRUSH)) {
            int tab = (context.getScaledWindowWidth() - context.getScaledWindowHeight()) / 2;
            int size = Math.min(context.getScaledWindowWidth(), context.getScaledWindowHeight());
            context.drawTexture(
                    RenderLayer::getGuiTextured,
                    CRUSH_TEXTURE,
                    Math.max(tab, 0), -Math.min(tab, 0), 0, 0,
                    size, size, size, size
            );
        }
    }
}
